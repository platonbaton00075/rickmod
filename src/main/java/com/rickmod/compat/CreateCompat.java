package com.rickmod.compat;

import com.rickmod.RickMod;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CreateCompat {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, RickMod.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, RickMod.MOD_ID);
    public static final DeferredRegister<Block> FLUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RickMod.MOD_ID);
    public static final DeferredRegister<Item> FLUID_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RickMod.MOD_ID);

    public static final RegistryObject<FluidType> PORTAL_FLUID_TYPE = FLUID_TYPES.register("liquid_portal_fluid",
            () -> new FluidType(FluidType.Properties.create().lightLevel(12).density(1200).viscosity(1500)));

    public static final RegistryObject<FlowingFluid> PORTAL_FLUID_SOURCE = FLUIDS.register("liquid_portal_fluid",
            () -> new ForgeFlowingFluid.Source(CreateCompat.FLUID_PROPERTIES));

    public static final RegistryObject<FlowingFluid> PORTAL_FLUID_FLOWING = FLUIDS.register("flowing_liquid_portal_fluid",
            () -> new ForgeFlowingFluid.Flowing(CreateCompat.FLUID_PROPERTIES));

    public static final RegistryObject<LiquidBlock> PORTAL_FLUID_BLOCK = FLUID_BLOCKS.register("liquid_portal_fluid_block",
            () -> new LiquidBlock(PORTAL_FLUID_SOURCE, BlockBehaviour.Properties.copy(Blocks.WATER).lightLevel(s -> 12)));

    public static final RegistryObject<Item> PORTAL_FLUID_BUCKET = FLUID_ITEMS.register("liquid_portal_fluid_bucket",
            () -> new BucketItem(PORTAL_FLUID_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final ForgeFlowingFluid.Properties FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            PORTAL_FLUID_TYPE, PORTAL_FLUID_SOURCE, PORTAL_FLUID_FLOWING)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(PORTAL_FLUID_BLOCK)
            .bucket(PORTAL_FLUID_BUCKET);

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        FLUID_BLOCKS.register(bus);
        FLUID_ITEMS.register(bus);
    }
}
