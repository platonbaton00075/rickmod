package com.rickmod.block;

import com.rickmod.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PhoenixPodBlockEntity extends BlockEntity {
    private UUID ownerUuid;
    private String ownerName = "";

    public PhoenixPodBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PHOENIX_POD_BE.get(), pos, state);
    }

    public void setOwner(UUID uuid, String name) {
        this.ownerUuid = uuid;
        this.ownerName = name;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Nullable
    public UUID getOwnerUuid() { return ownerUuid; }
    public String getOwnerName() { return ownerName; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (ownerUuid != null) tag.putUUID("OwnerUUID", ownerUuid);
        tag.putString("OwnerName", ownerName);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("OwnerUUID")) ownerUuid = tag.getUUID("OwnerUUID");
        ownerName = tag.getString("OwnerName");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
