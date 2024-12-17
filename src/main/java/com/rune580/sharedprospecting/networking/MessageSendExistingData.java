package com.rune580.sharedprospecting.networking;

import net.minecraft.entity.player.EntityPlayerMP;

import org.jetbrains.annotations.NotNull;

import com.rune580.sharedprospecting.database.SPTeamData;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import serverutils.lib.io.DataIn;
import serverutils.lib.io.DataOut;
import serverutils.lib.net.MessageToServer;
import serverutils.lib.net.NetworkWrapper;

public class MessageSendExistingData extends MessageToServer {

    private int dimension;
    private LongList oreVeins;
    private LongList undergroundFluids;

    public MessageSendExistingData() {}

    public MessageSendExistingData(int dim, LongList ores, LongList fluids) {
        this.dimension = dim;
        this.oreVeins = ores;
        this.undergroundFluids = fluids;
    }

    @Override
    public NetworkWrapper getWrapper() {
        return SPNetwork.NET;
    }

    @Override
    public void writeData(DataOut data) {
        data.writeVarInt(dimension);
        data.writeVarInt(oreVeins.size());
        oreVeins.forEach(data::writeVarLong);
        data.writeVarInt(undergroundFluids.size());
        undergroundFluids.forEach(data::writeVarLong);
    }

    @Override
    public void readData(DataIn data) {
        dimension = data.readVarInt();
        int size = data.readVarInt();
        oreVeins = new LongArrayList();
        for (int i = 0; i < size; i++) {
            oreVeins.add(data.readVarLong());
        }

        size = data.readVarInt();
        undergroundFluids = new LongArrayList();
        for (int i = 0; i < size; i++) {
            undergroundFluids.add(data.readVarLong());
        }
    }

    @Override
    public void onMessage(@NotNull EntityPlayerMP player) {
        SPTeamData data = SPTeamData.get(player);
        if (data == null) return;

        data.addOreVeins(dimension, oreVeins);
        data.addUndergroundFluids(dimension, undergroundFluids);
    }
}
