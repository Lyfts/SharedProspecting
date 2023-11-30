package com.rune580.sharedprospecting.hooks;

import com.rune580.sharedprospecting.networking.SPNetwork;
import com.rune580.sharedprospecting.networking.SyncMsg;
import com.rune580.sharedprospecting.worker.TickWorker;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.entity.player.EntityPlayerMP;

public class HooksFML {
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP player))
            return;

        SyncMsg packet = new SyncMsg();
        packet.setStartSync(true);
        SPNetwork.sendToPlayer(packet, player);
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {

    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        TickWorker.instance.onTick();
    }
}
