package com.rickmod.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GlbModelLoader {
    public static class MeshPart {
        public final float[] vertices;
        public final float[] normals;
        public final int[] indices;
        public final float r, g, b, a;

        public MeshPart(float[] vertices, float[] normals, int[] indices, float r, float g, float b, float a) {
            this.vertices = vertices;
            this.normals = normals;
            this.indices = indices;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }

    public static class LoadedModel {
        public final List<MeshPart> parts = new ArrayList<>();
        public float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        public float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

        public void computeBounds() {
            for (MeshPart part : parts) {
                for (int i = 0; i < part.vertices.length; i += 3) {
                    float x = part.vertices[i];
                    float y = part.vertices[i + 1];
                    float z = part.vertices[i + 2];
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (z < minZ) minZ = z;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                    if (z > maxZ) maxZ = z;
                }
            }
            if (minX > maxX) {
                minX = minY = minZ = -0.5f;
                maxX = maxY = maxZ = 0.5f;
            }
        }

        public void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
            PoseStack.Pose pose = poseStack.last();
            Matrix4f poseMat = pose.pose();
            Matrix3f normMat = pose.normal();

            for (MeshPart part : parts) {
                for (int idx : part.indices) {
                    int vOff = idx * 3;
                    if (vOff + 2 >= part.vertices.length) continue;
                    float vx = part.vertices[vOff];
                    float vy = part.vertices[vOff + 1];
                    float vz = part.vertices[vOff + 2];

                    float nx = 0, ny = 1, nz = 0;
                    if (part.normals != null && vOff + 2 < part.normals.length) {
                        nx = part.normals[vOff];
                        ny = part.normals[vOff + 1];
                        nz = part.normals[vOff + 2];
                    }

                    consumer.vertex(poseMat, vx, vy, vz)
                            .color(part.r, part.g, part.b, part.a)
                            .uv(0.5f, 0.5f)
                            .overlayCoords(packedOverlay)
                            .uv2(packedLight)
                            .normal(normMat, nx, ny, nz)
                            .endVertex();
                }
            }
        }
    }

    private static final Map<ResourceLocation, LoadedModel> CACHE = new HashMap<>();

    public static LoadedModel getOrLoad(ResourceLocation location, float defaultR, float defaultG, float defaultB) {
        return CACHE.computeIfAbsent(location, loc -> loadInternal(loc, defaultR, defaultG, defaultB));
    }

    private static LoadedModel loadInternal(ResourceLocation location, float defR, float defG, float defB) {
        LoadedModel model = new LoadedModel();
        try {
            Optional<Resource> resOpt = Minecraft.getInstance().getResourceManager().getResource(location);
            if (resOpt.isEmpty()) {
                return buildFallbackGun(defR, defG, defB);
            }
            byte[] data;
            try (InputStream is = resOpt.get().open()) {
                data = is.readAllBytes();
            }
            if (data.length < 20) return buildFallbackGun(defR, defG, defB);

            ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            int magic = buf.getInt();
            if (magic != 0x46546C67) return buildFallbackGun(defR, defG, defB);
            buf.getInt(); // version
            buf.getInt(); // length

            String jsonStr = null;
            byte[] binChunk = null;

            while (buf.remaining() >= 8) {
                int chunkLen = buf.getInt();
                int chunkType = buf.getInt();
                if (chunkLen < 0 || chunkLen > buf.remaining()) break;
                byte[] chunkBytes = new byte[chunkLen];
                buf.get(chunkBytes);
                if (chunkType == 0x4E4F534A) {
                    jsonStr = new String(chunkBytes, StandardCharsets.UTF_8);
                } else if (chunkType == 0x004E4942) {
                    binChunk = chunkBytes;
                }
            }

            if (jsonStr == null || binChunk == null) return buildFallbackGun(defR, defG, defB);

            JsonObject root = JsonParser.parseString(jsonStr).getAsJsonObject();
            JsonArray accessors = root.getAsJsonArray("accessors");
            JsonArray bufferViews = root.getAsJsonArray("bufferViews");
            JsonArray meshes = root.getAsJsonArray("meshes");
            JsonArray materials = root.has("materials") ? root.getAsJsonArray("materials") : null;
            JsonArray nodes = root.has("nodes") ? root.getAsJsonArray("nodes") : null;
            JsonArray scenes = root.has("scenes") ? root.getAsJsonArray("scenes") : null;

            ByteBuffer binBuf = ByteBuffer.wrap(binChunk).order(ByteOrder.LITTLE_ENDIAN);

            if (nodes != null && scenes != null && scenes.size() > 0) {
                JsonObject scene0 = scenes.get(0).getAsJsonObject();
                JsonArray rootNodes = scene0.getAsJsonArray("nodes");
                for (JsonElement nElem : rootNodes) {
                    traverseNode(nElem.getAsInt(), new Matrix4f(), nodes, meshes, materials, accessors, bufferViews, binBuf, model, defR, defG, defB);
                }
            } else if (meshes != null) {
                for (int i = 0; i < meshes.size(); i++) {
                    extractMesh(i, new Matrix4f(), meshes, materials, accessors, bufferViews, binBuf, model, defR, defG, defB);
                }
            }

            if (model.parts.isEmpty()) {
                return buildFallbackGun(defR, defG, defB);
            }
            model.computeBounds();
            return model;
        } catch (Exception e) {
            return buildFallbackGun(defR, defG, defB);
        }
    }

    private static void traverseNode(int nodeIdx, Matrix4f parentMat, JsonArray nodes, JsonArray meshes, JsonArray materials,
                                     JsonArray accessors, JsonArray bufferViews, ByteBuffer binBuf, LoadedModel model,
                                     float defR, float defG, float defB) {
        if (nodeIdx < 0 || nodeIdx >= nodes.size()) return;
        JsonObject node = nodes.get(nodeIdx).getAsJsonObject();
        Matrix4f local = new Matrix4f();

        if (node.has("matrix")) {
            JsonArray m = node.getAsJsonArray("matrix");
            float[] arr = new float[16];
            for (int i = 0; i < 16; i++) arr[i] = m.get(i).getAsFloat();
            local.set(arr);
        } else {
            if (node.has("translation")) {
                JsonArray t = node.getAsJsonArray("translation");
                local.translate(t.get(0).getAsFloat(), t.get(1).getAsFloat(), t.get(2).getAsFloat());
            }
            if (node.has("rotation")) {
                JsonArray r = node.getAsJsonArray("rotation");
                local.rotate(new org.joml.Quaternionf(r.get(0).getAsFloat(), r.get(1).getAsFloat(), r.get(2).getAsFloat(), r.get(3).getAsFloat()));
            }
            if (node.has("scale")) {
                JsonArray s = node.getAsJsonArray("scale");
                local.scale(s.get(0).getAsFloat(), s.get(1).getAsFloat(), s.get(2).getAsFloat());
            }
        }

        Matrix4f world = new Matrix4f(parentMat).mul(local);

        if (node.has("mesh")) {
            extractMesh(node.get("mesh").getAsInt(), world, meshes, materials, accessors, bufferViews, binBuf, model, defR, defG, defB);
        }
        if (node.has("children")) {
            for (JsonElement child : node.getAsJsonArray("children")) {
                traverseNode(child.getAsInt(), world, nodes, meshes, materials, accessors, bufferViews, binBuf, model, defR, defG, defB);
            }
        }
    }

    private static void extractMesh(int meshIdx, Matrix4f transform, JsonArray meshes, JsonArray materials,
                                    JsonArray accessors, JsonArray bufferViews, ByteBuffer binBuf, LoadedModel model,
                                    float defR, float defG, float defB) {
        if (meshIdx < 0 || meshIdx >= meshes.size()) return;
        JsonObject mesh = meshes.get(meshIdx).getAsJsonObject();
        JsonArray primitives = mesh.getAsJsonArray("primitives");
        Matrix3f normTransform = new Matrix3f(transform).invert().transpose();

        for (JsonElement primElem : primitives) {
            JsonObject prim = primElem.getAsJsonObject();
            JsonObject attrs = prim.getAsJsonObject("attributes");
            if (!attrs.has("POSITION") || !prim.has("indices")) continue;

            int posAccIdx = attrs.get("POSITION").getAsInt();
            int normAccIdx = attrs.has("NORMAL") ? attrs.get("NORMAL").getAsInt() : -1;
            int idxAccIdx = prim.get("indices").getAsInt();

            float[] rawPos = readFloatArray(posAccIdx, 3, accessors, bufferViews, binBuf);
            float[] rawNorm = normAccIdx >= 0 ? readFloatArray(normAccIdx, 3, accessors, bufferViews, binBuf) : null;
            int[] indices = readIntArray(idxAccIdx, accessors, bufferViews, binBuf);

            for (int i = 0; i < rawPos.length; i += 3) {
                Vector4f v = new Vector4f(rawPos[i], rawPos[i + 1], rawPos[i + 2], 1.0f).mul(transform);
                rawPos[i] = v.x;
                rawPos[i + 1] = v.y;
                rawPos[i + 2] = v.z;

                if (rawNorm != null && i + 2 < rawNorm.length) {
                    Vector3f n = new Vector3f(rawNorm[i], rawNorm[i + 1], rawNorm[i + 2]).mul(normTransform).normalize();
                    rawNorm[i] = n.x;
                    rawNorm[i + 1] = n.y;
                    rawNorm[i + 2] = n.z;
                }
            }

            float r = defR, g = defG, b = defB, a = 1.0f;
            if (prim.has("material") && materials != null) {
                int matIdx = prim.get("material").getAsInt();
                if (matIdx >= 0 && matIdx < materials.size()) {
                    JsonObject mat = materials.get(matIdx).getAsJsonObject();
                    if (mat.has("pbrMetallicRoughness")) {
                        JsonObject pbr = mat.getAsJsonObject("pbrMetallicRoughness");
                        if (pbr.has("baseColorFactor")) {
                            JsonArray cf = pbr.getAsJsonArray("baseColorFactor");
                            r = cf.get(0).getAsFloat();
                            g = cf.get(1).getAsFloat();
                            b = cf.get(2).getAsFloat();
                            a = cf.get(3).getAsFloat();
                        }
                    }
                }
            }

            model.parts.add(new MeshPart(rawPos, rawNorm, indices, r, g, b, a));
        }
    }

    private static float[] readFloatArray(int accIdx, int numComps, JsonArray accessors, JsonArray bufferViews, ByteBuffer binBuf) {
        JsonObject acc = accessors.get(accIdx).getAsJsonObject();
        int bvIdx = acc.get("bufferView").getAsInt();
        int accOffset = acc.has("byteOffset") ? acc.get("byteOffset").getAsInt() : 0;
        int count = acc.get("count").getAsInt();

        JsonObject bv = bufferViews.get(bvIdx).getAsJsonObject();
        int bvOffset = bv.has("byteOffset") ? bv.get("byteOffset").getAsInt() : 0;
        int stride = bv.has("byteStride") ? bv.get("byteStride").getAsInt() : (numComps * 4);

        float[] out = new float[count * numComps];
        for (int i = 0; i < count; i++) {
            int base = bvOffset + accOffset + i * stride;
            for (int c = 0; c < numComps; c++) {
                out[i * numComps + c] = binBuf.getFloat(base + c * 4);
            }
        }
        return out;
    }

    private static int[] readIntArray(int accIdx, JsonArray accessors, JsonArray bufferViews, ByteBuffer binBuf) {
        JsonObject acc = accessors.get(accIdx).getAsJsonObject();
        int bvIdx = acc.get("bufferView").getAsInt();
        int accOffset = acc.has("byteOffset") ? acc.get("byteOffset").getAsInt() : 0;
        int count = acc.get("count").getAsInt();
        int compType = acc.get("componentType").getAsInt();

        JsonObject bv = bufferViews.get(bvIdx).getAsJsonObject();
        int bvOffset = bv.has("byteOffset") ? bv.get("byteOffset").getAsInt() : 0;

        int[] out = new int[count];
        for (int i = 0; i < count; i++) {
            if (compType == 5125) {
                out[i] = binBuf.getInt(bvOffset + accOffset + i * 4);
            } else if (compType == 5123) {
                out[i] = Short.toUnsignedInt(binBuf.getShort(bvOffset + accOffset + i * 2));
            } else {
                out[i] = Byte.toUnsignedInt(binBuf.get(bvOffset + accOffset + i));
            }
        }
        return out;
    }

    private static LoadedModel buildFallbackGun(float r, float g, float b) {
        LoadedModel fallback = new LoadedModel();
        float[] v = new float[]{
                -0.1f, -0.1f, -0.25f,  0.1f, -0.1f, -0.25f,  0.1f, 0.1f, -0.25f, -0.1f, 0.1f, -0.25f,
                -0.1f, -0.1f,  0.25f,  0.1f, -0.1f,  0.25f,  0.1f, 0.1f,  0.25f, -0.1f, 0.1f,  0.25f
        };
        int[] idx = new int[]{
                0, 1, 2, 2, 3, 0, 4, 6, 5, 6, 4, 7,
                0, 4, 5, 5, 1, 0, 3, 2, 6, 6, 7, 3,
                0, 3, 7, 7, 4, 0, 1, 5, 6, 6, 2, 1
        };
        fallback.parts.add(new MeshPart(v, null, idx, r, g, b, 1.0f));
        fallback.computeBounds();
        return fallback;
    }
}
