package com.rune580.sharedprospecting.networking;

import com.rune580.sharedprospecting.database.ClientRevision;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import serverutils.lib.io.DataIn;
import serverutils.lib.io.DataOut;
import serverutils.lib.net.MessageToClient;
import serverutils.lib.net.NetworkWrapper;

public class MessageUpdateDepletions extends MessageToClient {

    private LongList depletedVeins;
    private int dimension;

    public MessageUpdateDepletions() {}

    public MessageUpdateDepletions(int dimension, LongList depletedVeins) {
        this.dimension = dimension;
        this.depletedVeins = depletedVeins;
    }

    @Override
    public NetworkWrapper getWrapper() {
        return SPNetwork.NET;
    }

    public void writeData(DataOut data) {
        data.writeVarInt(dimension);
        data.writeVarInt(depletedVeins.size());
        for (long key : depletedVeins) {
            data.writeVarLong(key);
        }
    }

    @Override
    public void readData(DataIn data) {
        dimension = data.readVarInt();
        int size = data.readVarInt();
        depletedVeins = new LongArrayList(size);
        for (int i = 0; i < size; i++) {
            depletedVeins.add(data.readVarLong());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onMessage() {
        ClientRevision.updateDepletions(dimension, depletedVeins);
    }
}
