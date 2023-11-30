package com.rune580.sharedprospecting.networking;

import com.rune580.sharedprospecting.worker.ClientToServerSync;
import com.rune580.sharedprospecting.worker.ServerToSingleClientSync;
import com.rune580.sharedprospecting.worker.TickWorker;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SyncMsg implements IMessage {
    private boolean fullSync;
    private boolean startSync;

    public SyncMsg() {
        fullSync = false;
        startSync = false;
    }

    public void setFullSync(boolean fullSync) {
        this.fullSync = fullSync;
    }

    public void setStartSync(boolean startSync) {
        this.startSync = startSync;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.fullSync = buf.readBoolean();
        this.startSync = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(fullSync);
        buf.writeBoolean(startSync);
    }


    public static class ServerHandler implements IMessageHandler<SyncMsg, IMessage> {
        @Override
        public IMessage onMessage(SyncMsg message, MessageContext ctx) {
            if (!message.fullSync)
                return null;

            ServerToSingleClientSync work = new ServerToSingleClientSync(ctx.getServerHandler().playerEntity);
            TickWorker.instance.queueWork(work);

            return null;
        }
    }

    public static class ClientHandler implements IMessageHandler<SyncMsg, IMessage> {
        @Override
        public IMessage onMessage(SyncMsg message, MessageContext ctx) {
            if (!message.startSync)
                return null;

            ClientToServerSync work = new ClientToServerSync();
            TickWorker.instance.queueWork(work);

            return null;
        }
    }
}
