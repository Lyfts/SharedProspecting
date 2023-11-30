package com.rune580.sharedprospecting.networking;

import net.minecraft.entity.player.EntityPlayerMP;

import com.rune580.sharedprospecting.SharedProspectingMod;
import com.rune580.sharedprospecting.database.TeamCache;
import com.rune580.sharedprospecting.database.TeamsCache;
import com.sinthoras.visualprospecting.database.ClientCache;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ProspectionSyncMsg extends ProspectionSyncMsgBase {

    public ProspectionSyncMsg() {
        super(0);
    }

    public static class ServerHandler implements IMessageHandler<ProspectionSyncMsg, IMessage> {

        @Override
        public IMessage onMessage(ProspectionSyncMsg message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            TeamCache teamCache = TeamsCache.instance.getByPlayer(player);
            if (teamCache == null) {
                SharedProspectingMod.LOG.warn("Server received sync message for a player without a team cache!");
                return null;
            }

            teamCache.addOreVeins(message.oreVeins);
            teamCache.addUndergroundFluids(message.undergroundFluids);

            return null;
        }
    }

    public static class ClientHandler implements IMessageHandler<ProspectionSyncMsg, IMessage> {

        @Override
        public IMessage onMessage(ProspectionSyncMsg message, MessageContext ctx) {
            ClientCache.instance.putOreVeins(message.oreVeins);
            ClientCache.instance.putUndergroundFluids(message.undergroundFluids);

            return null;
        }
    }
}
