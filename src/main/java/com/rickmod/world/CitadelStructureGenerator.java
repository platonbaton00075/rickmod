package com.rickmod.world;

import com.rickmod.entity.GromflomiteSoldierEntity;
import com.rickmod.entity.MortyEntity;
import com.rickmod.entity.RickEntity;
import com.rickmod.registry.ModBlocks;
import com.rickmod.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CitadelStructureGenerator {
    public static void generateGrandCitadel(ServerLevel level, BlockPos center) {
        BlockState steel = ModBlocks.CITADEL_STEEL_BLOCK.get().defaultBlockState();
        BlockState crystal = ModBlocks.PORTAL_CRYSTAL_BLOCK.get().defaultBlockState();
        BlockState glass = Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();
        BlockState cyanGlass = Blocks.CYAN_STAINED_GLASS.defaultBlockState();
        BlockState seaLantern = Blocks.SEA_LANTERN.defaultBlockState();
        BlockState quartz = Blocks.QUARTZ_BLOCK.defaultBlockState();
        BlockState dark = Blocks.POLISHED_BLACKSTONE.defaultBlockState();
        BlockState megaPlanks = ModBlocks.MEGA_PLANKS.get().defaultBlockState();
        BlockState megaLog = ModBlocks.MEGA_LOG.get().defaultBlockState();
        BlockState megaLeaves = ModBlocks.MEGA_LEAVES.get().defaultBlockState();

        // 1. Central Plaza (radius 26) + Deep Space Conical Hull (down to -28)
        int mainR = 26;
        for (int x = -mainR; x <= mainR; x++) {
            for (int z = -mainR; z <= mainR; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist <= mainR) {
                    // Main floor with intricate geometric inlays
                    BlockState floor = steel;
                    if (dist < 4.5) floor = crystal;
                    else if (dist < 6.0 || (Math.abs(dist - 15.0) < 0.8)) floor = seaLantern;
                    else if ((Math.abs(x) <= 1 || Math.abs(z) <= 1) && dist < 24) floor = quartz;
                    else if ((x + z) % 4 == 0) floor = dark;
                    level.setBlock(center.offset(x, -1, z), floor, 3);

                    // Clear air above
                    for (int y = 0; y <= 52; y++) {
                        level.setBlock(center.offset(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                    }

                    // Conical underside hull
                    int depth = (int) ((mainR - dist) * 1.15);
                    for (int y = 2; y <= depth; y++) {
                        BlockState hull = (y % 5 == 0 && dist > (mainR - y - 2)) ? crystal : (y % 3 == 0 ? dark : steel);
                        level.setBlock(center.offset(x, -y, z), hull, 3);
                    }
                }
            }
        }

        // Hanging plasma spire at bottom of Citadel
        for (int y = 28; y <= 38; y++) {
            level.setBlock(center.offset(0, -y, 0), y % 2 == 0 ? crystal : seaLantern, 3);
        }

        // 2. Central Council Tower (radius 8, height 48) + 3 Orbital Energy Rings
        int towerR = 8;
        int towerH = 48;
        for (int y = 0; y <= towerH; y++) {
            int curR = y > 38 ? Math.max(2, towerR - (y - 38)) : towerR;
            for (int x = -curR; x <= curR; x++) {
                for (int z = -curR; z <= curR; z++) {
                    double d = Math.sqrt(x * x + z * z);
                    if (d <= curR && d >= curR - 1.3) {
                        boolean isDoor = (y <= 3 && (Math.abs(x) <= 1 || Math.abs(z) <= 1));
                        if (!isDoor) {
                            BlockState wall = (y % 6 == 0) ? steel : ((x + z) % 2 == 0 ? glass : cyanGlass);
                            level.setBlock(center.offset(x, y, z), wall, 3);
                        }
                    }
                    // Observation decks every 12 blocks
                    if (y > 0 && y % 12 == 0 && d < curR - 1.3) {
                        level.setBlock(center.offset(x, y, z), d < 2.5 ? crystal : quartz, 3);
                    }
                }
            }
            // Central vertical portal beam
            level.setBlock(center.offset(0, y, 0), crystal, 3);
        }

        // 3 Floating Orbital Rings around the Tower
        int[] ringHeights = {16, 28, 40};
        int[] ringRadii = {14, 12, 10};
        for (int idx = 0; idx < 3; idx++) {
            int ry = ringHeights[idx];
            int rr = ringRadii[idx];
            for (int x = -rr - 1; x <= rr + 1; x++) {
                for (int z = -rr - 1; z <= rr + 1; z++) {
                    double d = Math.sqrt(x * x + z * z);
                    if (Math.abs(d - rr) < 0.9) {
                        level.setBlock(center.offset(x, ry, z), (x + z) % 2 == 0 ? crystal : seaLantern, 3);
                    }
                }
            }
        }

        // 3. 5 Radial Bridges + Outer Ring + 5 Specialized Sector Domes
        int outerDist = 48;
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(i * 72.0);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            // Radial bridge
            for (int r = mainR - 1; r <= outerDist; r++) {
                int bx = (int) Math.round(r * cos);
                int bz = (int) Math.round(r * sin);
                for (int w = -2; w <= 2; w++) {
                    int px = (int) Math.round(bx - w * sin);
                    int pz = (int) Math.round(bz + w * cos);
                    BlockState bState = (Math.abs(w) == 2) ? dark : (r % 4 == 0 && w == 0 ? seaLantern : steel);
                    level.setBlock(center.offset(px, -1, pz), bState, 3);
                    if (Math.abs(w) == 2) {
                        level.setBlock(center.offset(px, 0, pz), r % 5 == 0 ? crystal : glass, 3);
                    }
                }
            }

            // Sector Dome at the end of each radial arm
            int sx = (int) Math.round(outerDist * cos);
            int sz = (int) Math.round(outerDist * sin);
            BlockPos sectorCenter = center.offset(sx, 0, sz);
            int domeR = 10;
            for (int x = -domeR; x <= domeR; x++) {
                for (int z = -domeR; z <= domeR; z++) {
                    double d2 = Math.sqrt(x * x + z * z);
                    if (d2 <= domeR) {
                        level.setBlock(sectorCenter.offset(x, -1, z), d2 < 3 ? crystal : (i == 0 ? megaPlanks : steel), 3);
                        level.setBlock(sectorCenter.offset(x, -2, z), dark, 3);
                        for (int y = 0; y <= domeR; y++) {
                            double d3 = Math.sqrt(x * x + y * y + z * z);
                            if (d3 <= domeR && d3 >= domeR - 1.2 && y > 2) {
                                level.setBlock(sectorCenter.offset(x, y, z), y == domeR ? seaLantern : glass, 3);
                            }
                        }
                    }
                }
            }

            // Sector Interior Details
            if (i == 0) {
                // Botanical Dome with Alien Mega-Tree
                for (int ty = 0; ty < 6; ty++) level.setBlock(sectorCenter.offset(0, ty, 0), megaLog, 3);
                for (int lx = -3; lx <= 3; lx++) {
                    for (int lz = -3; lz <= 3; lz++) {
                        for (int ly = 4; ly <= 7; ly++) {
                            if (Math.abs(lx) + Math.abs(lz) + Math.abs(ly - 5) <= 4) {
                                level.setBlock(sectorCenter.offset(lx, ly, lz), megaLeaves, 3);
                            }
                        }
                    }
                }
            } else if (i == 1 || i == 2) {
                // Portal Reactor Core
                for (int ry = 0; ry <= 6; ry++) {
                    level.setBlock(sectorCenter.offset(0, ry, 0), crystal, 3);
                }
            }

            // Spawn Ricks and Mortys in each sector
            RickEntity rick = ModEntities.RICK_CITIZEN.get().create(level);
            if (rick != null) {
                rick.moveTo(sectorCenter.getX() + 2.5, sectorCenter.getY(), sectorCenter.getZ() + 2.5, 0, 0);
                level.addFreshEntity(rick);
            }
            MortyEntity morty = ModEntities.MORTY_CITIZEN.get().create(level);
            if (morty != null) {
                morty.moveTo(sectorCenter.getX() - 2.5, sectorCenter.getY(), sectorCenter.getZ() - 2.5, 0, 0);
                level.addFreshEntity(morty);
            }
        }

        // 4. Outer Orbital Ring connecting all 5 Sectors
        for (int deg = 0; deg < 360; deg++) {
            double rad = Math.toRadians(deg);
            for (int r = outerDist - 2; r <= outerDist + 2; r++) {
                int rx = (int) Math.round(r * Math.cos(rad));
                int rz = (int) Math.round(r * Math.sin(rad));
                BlockState ringBlock = (r == outerDist - 2 || r == outerDist + 2) ? dark : (deg % 15 == 0 ? crystal : steel);
                level.setBlock(center.offset(rx, -1, rz), ringBlock, 3);
            }
        }

        // 5. Loot Chests & Federation Attack Squad on Main Plaza
        BlockPos chestPos = center.offset(4, 0, 4);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(chestPos);
        if (be instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(com.rickmod.registry.ModItems.PORTAL_FLUID_FLASK.get(), 6));
            chest.setItem(1, new ItemStack(com.rickmod.registry.ModItems.PORTAL_CORE.get(), 2));
            chest.setItem(2, new ItemStack(Items.DIAMOND, 12));
            chest.setItem(3, new ItemStack(Items.EMERALD, 24));
        }

        for (int s = 0; s < 3; s++) {
            GromflomiteSoldierEntity soldier = ModEntities.GROMFLOMITE_SOLDIER.get().create(level);
            if (soldier != null) {
                soldier.moveTo(center.getX() - 12 + s * 3, center.getY(), center.getZ() + 12, 0, 0);
                level.addFreshEntity(soldier);
            }
        }
    }
}
