package com.rickmod.world;

import com.rickmod.entity.MortyEntity;
import com.rickmod.entity.RickEntity;
import com.rickmod.registry.ModBlocks;
import com.rickmod.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rickmod")
public class CitadelStructureBuilder {
    public static final ResourceKey<Level> CITADEL_DIM = ResourceKey.create(
            Registries.DIMENSION, new ResourceLocation("rickmod", "citadel_of_ricks"));

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getTo().equals(CITADEL_DIM) && event.getEntity().level() instanceof ServerLevel serverLevel) {
            CitadelSavedData data = CitadelSavedData.get(serverLevel);
            if (!data.isBuilt()) {
                BlockPos center = new BlockPos(0, 100, 0);
                buildCanonicalCitadel(serverLevel, center);
                data.setBuilt(true);
            }
            event.getEntity().teleportTo(0.5, 102.0, 0.5);
        }
    }

    public static void buildCanonicalCitadel(ServerLevel level, BlockPos c) {
        BlockState steel = ModBlocks.CITADEL_STEEL_BLOCK.get().defaultBlockState();
        BlockState crystal = ModBlocks.PORTAL_CRYSTAL_BLOCK.get().defaultBlockState();
        BlockState megaLog = ModBlocks.MEGA_LOG.get().defaultBlockState();
        BlockState megaPlanks = ModBlocks.MEGA_PLANKS.get().defaultBlockState();
        BlockState megaLeaves = ModBlocks.MEGA_LEAVES.get().defaultBlockState();
        BlockState darkSteel = Blocks.DEEPSLATE_TILES.defaultBlockState();
        BlockState glass = Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();
        BlockState tintedGlass = Blocks.TINTED_GLASS.defaultBlockState();
        BlockState seaLantern = Blocks.SEA_LANTERN.defaultBlockState();
        BlockState beacon = Blocks.BEACON.defaultBlockState();
        BlockState ironBars = Blocks.IRON_BARS.defaultBlockState();

        // 1. Main Central Disc Platform (Radius 22) + Hull Underside Cone
        for (int dx = -22; dx <= 22; dx++) {
            for (int dz = -22; dz <= 22; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= 22.0) {
                    level.setBlock(c.offset(dx, 0, dz), steel, 3);
                    level.setBlock(c.offset(dx, -1, dz), darkSteel, 3);
                    // Underside tapering hull
                    int depth = (int) ((22.0 - dist) * 0.65);
                    for (int dy = 2; dy <= depth; dy++) {
                        level.setBlock(c.offset(dx, -dy, dz), (dy % 4 == 0) ? crystal : darkSteel, 3);
                    }
                    // Glowing concentric rings on main plaza
                    if (Math.abs(dist - 8.0) < 0.7 || Math.abs(dist - 16.0) < 0.7 || Math.abs(dist - 21.2) < 0.8) {
                        level.setBlock(c.offset(dx, 0, dz), crystal, 3);
                    }
                }
            }
        }

        // 2. Central Council Tower (Height 42 blocks) with Tapered Rings & Glass Spire
        for (int y = 1; y <= 42; y++) {
            int r = y < 12 ? 6 : (y < 28 ? 4 : (y < 38 ? 3 : 1));
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    double d = Math.sqrt(dx * dx + dz * dz);
                    if (d <= r + 0.2) {
                        if (d >= r - 0.9) {
                            boolean isWindow = (y % 4 == 1 || y % 4 == 2) && y < 38;
                            level.setBlock(c.offset(dx, y, dz), isWindow ? glass : steel, 3);
                        } else if (y % 7 == 0) {
                            level.setBlock(c.offset(dx, y, dz), darkSteel, 3);
                        } else {
                            level.setBlock(c.offset(dx, y, dz), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
            // Horizontal structural rings around the central spire
            if (y == 12 || y == 24 || y == 36) {
                int ringR = r + 3;
                for (int dx = -ringR; dx <= ringR; dx++) {
                    for (int dz = -ringR; dz <= ringR; dz++) {
                        double d = Math.sqrt(dx * dx + dz * dz);
                        if (d >= r && d <= ringR) {
                            level.setBlock(c.offset(dx, y, dz), Math.abs(d - ringR) < 0.8 ? crystal : darkSteel, 3);
                        }
                    }
                }
            }
        }
        // Top Space Beacon Antenna
        level.setBlock(c.offset(0, 43, 0), crystal, 3);
        level.setBlock(c.offset(0, 44, 0), beacon, 3);

        // 3. Five Radial Spokes (Canonical 5-armed Citadel layout) leading to Satellite Domes
        double[] angles = {
            0.0,
            Math.toRadians(72),
            Math.toRadians(144),
            Math.toRadians(216),
            Math.toRadians(288)
        };

        for (int i = 0; i < angles.length; i++) {
            double ang = angles[i];
            double cos = Math.cos(ang);
            double sin = Math.sin(ang);

            // Bridge from radius 21 to 46
            for (int step = 21; step <= 46; step++) {
                int bx = (int) Math.round(cos * step);
                int bz = (int) Math.round(sin * step);
                for (int w = -2; w <= 2; w++) {
                    int px = bx + (int) Math.round(-sin * w);
                    int pz = bz + (int) Math.round(cos * w);
                    BlockState floor = (w == 0) ? crystal : steel;
                    level.setBlock(c.offset(px, 0, pz), floor, 3);
                    level.setBlock(c.offset(px, -1, pz), darkSteel, 3);
                    if (Math.abs(w) == 2) {
                        level.setBlock(c.offset(px, 1, pz), (step % 5 == 0) ? seaLantern : ironBars, 3);
                    }
                }
            }

            // Satellite Platform at distance 54 (Radius 9)
            int sx = (int) Math.round(cos * 54);
            int sz = (int) Math.round(sin * 54);
            BlockPos satCenter = c.offset(sx, 0, sz);

            for (int dx = -9; dx <= 9; dx++) {
                for (int dz = -9; dz <= 9; dz++) {
                    double d = Math.sqrt(dx * dx + dz * dz);
                    if (d <= 9.0) {
                        level.setBlock(satCenter.offset(dx, 0, dz), Math.abs(d - 8.5) < 0.7 ? crystal : steel, 3);
                        level.setBlock(satCenter.offset(dx, -1, dz), darkSteel, 3);
                        if (d <= 7.0) {
                            level.setBlock(satCenter.offset(dx, -2, dz), darkSteel, 3);
                        }
                    }
                }
            }

            // Glass Biodome over each satellite module (Radius 8)
            for (int dx = -8; dx <= 8; dx++) {
                for (int dy = 1; dy <= 8; dy++) {
                    for (int dz = -8; dz <= 8; dz++) {
                        double d3 = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (d3 >= 7.2 && d3 <= 8.1) {
                            // Leave doorway facing the bridge
                            boolean isDoor = dy <= 3 && Math.abs(dx + cos * 8) < 2.5 && Math.abs(dz + sin * 8) < 2.5;
                            if (!isDoor) {
                                level.setBlock(satCenter.offset(dx, dy, dz), (dy == 1) ? darkSteel : glass, 3);
                            }
                        }
                    }
                }
            }

            // Module Specialization:
            if (i == 0) {
                // Biodome #1: Mega-Tree Botanical Sector
                for (int ty = 1; ty <= 5; ty++) {
                    level.setBlock(satCenter.offset(0, ty, 0), megaLog, 3);
                }
                for (int lx = -2; lx <= 2; lx++) {
                    for (int lz = -2; lz <= 2; lz++) {
                        for (int ly = 4; ly <= 6; ly++) {
                            if (Math.abs(lx) + Math.abs(lz) + Math.abs(ly - 5) <= 3) {
                                level.setBlock(satCenter.offset(lx, ly, lz), megaLeaves, 3);
                            }
                        }
                    }
                }
            } else if (i == 1 || i == 3) {
                // Sectors #2 & #4: Portal Reactor Core
                level.setBlock(satCenter.offset(0, 1, 0), darkSteel, 3);
                level.setBlock(satCenter.offset(0, 2, 0), crystal, 3);
                level.setBlock(satCenter.offset(0, 3, 0), crystal, 3);
                level.setBlock(satCenter.offset(0, 4, 0), seaLantern, 3);
            } else {
                // Sectors #3 & #5: Council Residential / Admin Quarters
                level.setBlock(satCenter.offset(0, 1, 0), megaPlanks, 3);
                level.setBlock(satCenter.offset(2, 1, 0), Blocks.CRAFTING_TABLE.defaultBlockState(), 3);
                level.setBlock(satCenter.offset(-2, 1, 0), Blocks.ENDER_CHEST.defaultBlockState(), 3);
            }

            // Spawn Ricks and Mortys in each sector
            spawnCitizenPair(level, satCenter.offset(1, 1, 1));
        }

        // Outer Orbital Ring connecting the 5 arms at radius 36
        for (int deg = 0; deg < 360; deg += 2) {
            double rad = Math.toRadians(deg);
            int rx = (int) Math.round(Math.cos(rad) * 36);
            int rz = (int) Math.round(Math.sin(rad) * 36);
            for (int ox = -1; ox <= 1; ox++) {
                for (int oz = -1; oz <= 1; oz++) {
                    level.setBlock(c.offset(rx + ox, 0, rz + oz), (ox == 0 && oz == 0) ? crystal : steel, 3);
                    level.setBlock(c.offset(rx + ox, -1, rz + oz), darkSteel, 3);
                }
            }
        }

        // Spawn Ricks and Mortys on Central Plaza
        spawnCitizenPair(level, c.offset(9, 1, 0));
        spawnCitizenPair(level, c.offset(-9, 1, 0));
        spawnCitizenPair(level, c.offset(0, 1, 9));
        spawnCitizenPair(level, c.offset(0, 1, -9));
    }

    private static void spawnCitizenPair(ServerLevel level, BlockPos pos) {
        RickEntity rick = ModEntities.RICK_CITIZEN.get().create(level);
        if (rick != null) {
            rick.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            level.addFreshEntity(rick);
        }
        MortyEntity morty = ModEntities.MORTY_CITIZEN.get().create(level);
        if (morty != null) {
            morty.moveTo(pos.getX() + 1.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            level.addFreshEntity(morty);
        }
    }

    public static class CitadelSavedData extends SavedData {
        private boolean built = false;

        public boolean isBuilt() { return built; }
        public void setBuilt(boolean built) { this.built = built; setDirty(); }

        public static CitadelSavedData load(CompoundTag tag) {
            CitadelSavedData data = new CitadelSavedData();
            data.built = tag.getBoolean("citadel_built_v2");
            return data;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putBoolean("citadel_built_v2", built);
            return tag;
        }

        public static CitadelSavedData get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(CitadelSavedData::load, CitadelSavedData::new, "rickmod_citadel_data_v2");
        }
    }
}
