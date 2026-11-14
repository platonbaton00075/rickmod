package com.rickmod.init;

import com.rickmod.RickMod;
import com.rickmod.block.BlenderGrateBlock;
import com.rickmod.block.PhoenixPodBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RickMod.MOD_ID);

    public static final RegistryObject<Block> MEGA_LOG = BLOCKS.register("mega_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.5F).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> MEGA_PLANKS = BLOCKS.register("mega_planks",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F, 3.0F).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> MEGA_STAIRS = BLOCKS.register("mega_stairs",
            () -> new StairBlock(() -> MEGA_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(MEGA_PLANKS.get())));

    public static final RegistryObject<Block> MEGA_SLAB = BLOCKS.register("mega_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(MEGA_PLANKS.get())));

    public static final RegistryObject<Block> MEGA_FENCE = BLOCKS.register("mega_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(MEGA_PLANKS.get())));

    public static final RegistryObject<Block> MEGA_FENCE_GATE = BLOCKS.register("mega_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(MEGA_PLANKS.get()), WoodType.OAK));

    public static final RegistryObject<Block> MEGA_LEAVES = BLOCKS.register("mega_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()));

    public static final RegistryObject<Block> CITADEL_STEEL_BLOCK = BLOCKS.register("citadel_steel_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 12.0F).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    public static final RegistryObject<Block> PORTAL_CRYSTAL_BLOCK = BLOCKS.register("portal_crystal_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.EMERALD).strength(3.0F, 8.0F).lightLevel(s -> 14).noOcclusion().sound(SoundType.AMETHYST)));

    public static final RegistryObject<Block> BLENDER_GRATE_BLOCK = BLOCKS.register("blender_grate_block",
            () -> new BlenderGrateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(4.0F, 10.0F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> CRONENBERG_SOIL = BLOCKS.register("cronenberg_soil",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.8F).sound(SoundType.SLIME_BLOCK)));

    public static final RegistryObject<Block> PHOENIX_POD = BLOCKS.register("phoenix_pod",
            () -> new PhoenixPodBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(4.0F, 12.0F).lightLevel(s -> 11).noOcclusion().sound(SoundType.GLASS)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
