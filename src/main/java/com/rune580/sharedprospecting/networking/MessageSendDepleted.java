package com.rune580.sharedprospecting.networking;

import net.minecraft.entity.player.EntityPlayerMP;

import org.jetbrains.annotations.NotNull;

import com.rune580.sharedprospecting.database.SPTeamData;

import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import serverutils.lib.io.DataIn;
import serverutils.lib.io.DataOut;
import serverutils.lib.net.MessageToServer;
import serverutils.lib.net.NetworkWrapper;

public class MessageSendDepleted extends MessageToServer {

    private Long2BooleanMap depletedVeins;
    private int dimension;

    public MessageSendDepleted() {}

    public MessageSendDepleted(Long2BooleanMap depletedVeins) {
        this(Integer.MIN_VALUE, depletedVeins);
    }

    public MessageSendDepleted(int dimension, Long2BooleanMap depletedVeins) {
        this.dimension = dimension;
        this.depletedVeins = depletedVeins;
    }

    @Override
    public NetworkWrapper getWrapper() {
        return SPNetwork.NET;
    }

    @Override
    public void writeData(DataOut data) {
        data.writeVarInt(dimension);
        data.writeVarInt(depletedVeins.size());
        for (Long2BooleanMap.Entry entry : depletedVeins.long2BooleanEntrySet()) {
            data.writeVarLong(entry.getLongKey());
            data.writeBoolean(entry.getBooleanValue());
        }
    }

    @Override
    public void readData(DataIn data) {
        dimension = data.readVarInt();
        int size = data.readVarInt();
        depletedVeins = new Long2BooleanOpenHashMap(size);
        for (int i = 0; i < size; i++) {
            long key = data.readVarLong();
            boolean value = data.readBoolean();
            depletedVeins.put(key, value);
        }
    }

    @Override
    public void onMessage(@NotNull EntityPlayerMP player) {
        SPTeamData data = SPTeamData.get(player);
        if (data == null) return;
        data.updateDepletedVeins(dimension == Integer.MIN_VALUE ? player.dimension : dimension, depletedVeins);
    }
}
