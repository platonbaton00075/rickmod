package com.rickmod.block;

import com.rickmod.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class PhoenixPodBlockEntity extends BlockEntity {
    private UUID boundPlayerUuid;
    private String boundPlayerName = "";

    public PhoenixPodBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PHOENIX_POD.get(), pos, state);
    }

    public void bindPlayer(ServerPlayer player) {
        this.boundPlayerUuid = player.getUUID();
        this.boundPlayerName = player.getGameProfile().getName();
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setBoundPlayer(Player player) {
        this.boundPlayerUuid = player.getUUID();
        this.boundPlayerName = player.getGameProfile().getName();
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Nullable
    public UUID getBoundPlayerUuid() {
        return boundPlayerUuid;
    }

    public String getBoundPlayerName() {
        return boundPlayerName;
    }

    public boolean hasBoundPlayer() {
        return boundPlayerUuid != null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (boundPlayerUuid != null) {
            tag.putUUID("BoundPlayer", boundPlayerUuid);
        }
        tag.putString("BoundPlayerName", boundPlayerName);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("BoundPlayer")) {
            boundPlayerUuid = tag.getUUID("BoundPlayer");
        }
        boundPlayerName = tag.getString("BoundPlayerName");
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
