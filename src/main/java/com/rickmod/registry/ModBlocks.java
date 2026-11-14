package com.rickmod.registry;

import com.rickmod.RickMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RickMod.MOD_ID);

    public static final RegistryObject<Block> MEGA_LOG = registerBlock("mega_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).mapColor(MapColor.COLOR_CYAN).strength(2.5f)));

    public static final RegistryObject<Block> MEGA_PLANKS = registerBlock("mega_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0f, 3.0f)));

    public static final RegistryObject<Block> MEGA_STAIRS = registerBlock("mega_stairs",
            () -> new StairBlock(() -> MEGA_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));

    public static final RegistryObject<Block> MEGA_SLAB = registerBlock("mega_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));

    public static final RegistryObject<Block> MEGA_FENCE = registerBlock("mega_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)));

    public static final RegistryObject<Block> MEGA_FENCE_GATE = registerBlock("mega_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE), WoodType.OAK));

    public static final RegistryObject<Block> MEGA_LEAVES = registerBlock("mega_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).noOcclusion()));

    public static final RegistryObject<Block> CITADEL_STEEL_BLOCK = registerBlock("citadel_steel_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0f, 12.0f).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> PORTAL_CRYSTAL_BLOCK = registerBlock("portal_crystal_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.EMERALD).strength(3.0f, 6.0f).sound(SoundType.AMETHYST).lightLevel(s -> 14)));

    public static final RegistryObject<Block> BLENDER_GRATE_BLOCK = registerBlock("blender_grate_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0f, 10.0f).sound(SoundType.METAL)) {
                @Override
                public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
                    if (!level.isClientSide && entity instanceof LivingEntity) {
                        entity.hurt(level.damageSources().cactus(), 4.0f);
                    }
                    super.stepOn(level, pos, state, entity);
                }
            });

    public static final RegistryObject<Block> CRONENBERG_SOIL = registerBlock("cronenberg_soil",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT).mapColor(MapColor.COLOR_GREEN).sound(SoundType.SLIME_BLOCK)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
