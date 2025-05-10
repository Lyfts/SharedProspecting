package com.rune580.sharedprospecting.networking;

import com.rune580.sharedprospecting.database.ClientRevision;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import serverutils.lib.io.DataIn;
import serverutils.lib.io.DataOut;
import serverutils.lib.net.MessageToClient;
import serverutils.lib.net.NetworkWrapper;

public class MessageUpdateSingleDepleted extends MessageToClient {

    private int dimension;
    private long depletedVein;
    private boolean isDepleted;

    public MessageUpdateSingleDepleted() {}

    public MessageUpdateSingleDepleted(int dimension, long depletedVein, boolean isDepleted) {
        this.dimension = dimension;
        this.depletedVein = depletedVein;
        this.isDepleted = isDepleted;
    }

    @Override
    public NetworkWrapper getWrapper() {
        return SPNetwork.NET;
    }

    public void writeData(DataOut data) {
        data.writeVarInt(dimension);
        data.writeVarLong(depletedVein);
        data.writeBoolean(isDepleted);
    }

    @Override
    public void readData(DataIn data) {
        dimension = data.readVarInt();
        depletedVein = data.readVarLong();
        isDepleted = data.readBoolean();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onMessage() {
        ClientRevision.setDepleted(dimension, depletedVein, isDepleted);
    }
}
