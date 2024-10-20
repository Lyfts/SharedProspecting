package com.rune580.sharedprospecting.networking;

import net.minecraft.entity.player.EntityPlayerMP;

import org.jetbrains.annotations.NotNull;

import com.rune580.sharedprospecting.SharedProspectingMod;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import serverutils.lib.data.ForgeTeam;

public class SPNetwork {

    private static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE
        .newSimpleChannel(SharedProspectingMod.MOD_ID);
    private static int channelId = 0;

    public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
        Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        networkWrapper.registerMessage(messageHandler, requestMessageType, channelId++, side);
    }

    public static void sendToPlayer(IMessage message, EntityPlayerMP player) {
        networkWrapper.sendTo(message, player);
    }

    public static void sendToServer(IMessage message) {
        networkWrapper.sendToServer(message);
    }

    public static void sendToTeamMembers(IMessage message, @NotNull ForgeTeam team) {
        for (EntityPlayerMP member : team.getOnlineMembers()) {
            sendToPlayer(message, member);
        }
    }
}
