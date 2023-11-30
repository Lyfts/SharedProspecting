package com.rune580.sharedprospecting.networking;

import com.rune580.sharedprospecting.Tokens;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

public class SPNetwork {
    private static SimpleNetworkWrapper networkWrapper;
    private static int channelId;

    public static void Init() {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Tokens.MODID);
        channelId = 0;
    }

    public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        networkWrapper.registerMessage(messageHandler, requestMessageType, channelId++, side);
    }

    public static void sendToPlayer(IMessage message, EntityPlayerMP player) {
        networkWrapper.sendTo(message, player);
    }

    public static void sendToServer(IMessage message) {
        networkWrapper.sendToServer(message);
    }
}