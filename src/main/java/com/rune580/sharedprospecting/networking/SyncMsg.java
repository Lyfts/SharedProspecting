package com.rune580.sharedprospecting.networking;

import com.rune580.sharedprospecting.worker.ServerToSingleClientSync;
import com.rune580.sharedprospecting.worker.TickWorker;
import com.rune580.sharedprospecting.worker.WaitUntilClientCacheHasLoaded;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SyncMsg implements IMessage {

    private boolean fullFinishedSync;
    private boolean startSync;

    public SyncMsg() {
        fullFinishedSync = false;
        startSync = false;
    }

    public void setFullFinishedSync(boolean fullSync) {
        this.fullFinishedSync = fullSync;
    }

    public void setStartSync(boolean startSync) {
        this.startSync = startSync;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.fullFinishedSync = buf.readBoolean();
        this.startSync = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(fullFinishedSync);
        buf.writeBoolean(startSync);
    }

    public static class ServerHandler implements IMessageHandler<SyncMsg, IMessage> {

        @Override
        public IMessage onMessage(SyncMsg message, MessageContext ctx) {
            if (!message.fullFinishedSync) return null;

            ServerToSingleClientSync work = new ServerToSingleClientSync(ctx.getServerHandler().playerEntity);
            TickWorker.instance.queueWork(work);

            return null;
        }
    }

    public static class ClientHandler implements IMessageHandler<SyncMsg, IMessage> {

        @Override
        public IMessage onMessage(SyncMsg message, MessageContext ctx) {
            if (!message.startSync) return null;

            WaitUntilClientCacheHasLoaded work = new WaitUntilClientCacheHasLoaded();
            TickWorker.instance.queueWork(work);

            return null;
        }
    }
}
