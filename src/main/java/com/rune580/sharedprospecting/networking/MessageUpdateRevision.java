package com.rune580.sharedprospecting.networking;

import com.rune580.sharedprospecting.database.ClientRevision;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import serverutils.lib.io.DataIn;
import serverutils.lib.io.DataOut;
import serverutils.lib.net.MessageToClient;
import serverutils.lib.net.NetworkWrapper;

public class MessageUpdateRevision extends MessageToClient {

    private String teamId;
    private long revision;
    private int dim;

    public MessageUpdateRevision() {}

    public MessageUpdateRevision(String teamId, int dim, long revision) {
        this.teamId = teamId;
        this.dim = dim;
        this.revision = revision;
    }

    @Override
    public void readData(DataIn data) {
        teamId = data.readString();
        dim = data.readInt();
        revision = data.readLong();
    }

    @Override
    public void writeData(DataOut data) {
        data.writeString(teamId);
        data.writeInt(dim);
        data.writeLong(revision);
    }

    @Override
    public NetworkWrapper getWrapper() {
        return SPNetwork.NET;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onMessage() {
        ClientRevision.updateRevision(teamId, dim, revision);
    }
}
