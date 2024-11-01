package com.rune580.sharedprospecting.networking;

import net.minecraft.entity.player.EntityPlayerMP;

import org.jetbrains.annotations.NotNull;

import com.rune580.sharedprospecting.database.SPTeamData;

import serverutils.lib.io.DataIn;
import serverutils.lib.io.DataOut;
import serverutils.lib.net.MessageToServer;
import serverutils.lib.net.NetworkWrapper;

public class MessageRequestUpdate extends MessageToServer {

    private long revision;

    public MessageRequestUpdate() {}

    public MessageRequestUpdate(long revision) {
        this.revision = revision;
    }

    @Override
    public NetworkWrapper getWrapper() {
        return SPNetwork.NET;
    }

    @Override
    public void writeData(DataOut data) {
        data.writeLong(revision);
    }

    @Override
    public void readData(DataIn data) {
        revision = data.readLong();
    }

    @Override
    public void onMessage(@NotNull EntityPlayerMP player) {
        SPTeamData data = SPTeamData.get(player);
        if (data == null) return;

        data.sendUpdate(player, revision);
    }
}
