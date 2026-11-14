package com.rickmod.block;

import com.rickmod.blockentity.PhoenixPodBlockEntity;
import com.rickmod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PhoenixPodBlock extends BaseEntityBlock {
    public enum PodHalf implements StringRepresentable {
        LOWER("lower"),
        UPPER("upper");

        private final String name;
        PodHalf(String name) { this.name = name; }
        @Override public String getSerializedName() { return this.name; }
    }

    public static final EnumProperty<PodHalf> HALF = EnumProperty.create("half", PodHalf.class);
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public PhoenixPodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, PodHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(HALF, PodHalf.LOWER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, PodHalf.UPPER), 3);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        PodHalf half = state.getValue(HALF);
        if (dir == Direction.UP && half == PodHalf.LOWER && !neighborState.is(this)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (dir == Direction.DOWN && half == PodHalf.UPPER && !neighborState.is(this)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockPos basePos = state.getValue(HALF) == PodHalf.UPPER ? pos.below() : pos;
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (level.getBlockEntity(basePos) instanceof PhoenixPodBlockEntity podBE) {
                podBE.bindPlayer(serverPlayer);
            }
            serverPlayer.getPersistentData().putInt("rickmod_phoenix_x", basePos.getX());
            serverPlayer.getPersistentData().putInt("rickmod_phoenix_y", basePos.getY());
            serverPlayer.getPersistentData().putInt("rickmod_phoenix_z", basePos.getZ());
            serverPlayer.getPersistentData().putString("rickmod_phoenix_dim", level.dimension().location().toString());
            serverPlayer.getPersistentData().putBoolean("rickmod_phoenix_active", true);

            level.playSound(null, basePos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.35F);
            serverPlayer.sendSystemMessage(Component.literal("§b[Проект Феникс] §aДНК отсканирована! Резервный клон выращен в капсуле (" + basePos.getX() + ", " + basePos.getY() + ", " + basePos.getZ() + ")."));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(HALF) == PodHalf.LOWER && random.nextInt(2) == 0) {
            double x = pos.getX() + 0.3D + random.nextDouble() * 0.4D;
            double y = pos.getY() + 0.25D + random.nextDouble() * 1.4D;
            double z = pos.getZ() + 0.3D + random.nextDouble() * 0.4D;
            level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, x, y, z, 0.0D, 0.05D, 0.0D);
            if (random.nextInt(3) == 0) {
                level.addParticle(ParticleTypes.GLOW, x, y, z, 0.0D, 0.02D, 0.0D);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == PodHalf.LOWER ? new PhoenixPodBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return state.getValue(HALF) == PodHalf.LOWER
                ? createTickerHelper(type, ModBlockEntities.PHOENIX_POD.get(), PhoenixPodBlockEntity::tick)
                : null;
    }
}
