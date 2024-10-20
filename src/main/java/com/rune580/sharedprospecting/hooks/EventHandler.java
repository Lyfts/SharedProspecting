package com.rune580.sharedprospecting.hooks;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent;

import com.rune580.sharedprospecting.SharedProspectingMod;
import com.rune580.sharedprospecting.database.TeamCache;
import com.rune580.sharedprospecting.database.TeamsCache;
import com.rune580.sharedprospecting.networking.SPNetwork;
import com.rune580.sharedprospecting.networking.SyncMsg;
import com.rune580.sharedprospecting.worker.TickWorker;
import com.sinthoras.visualprospecting.database.WorldIdHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import serverutils.events.team.ForgeTeamCreatedEvent;
import serverutils.events.team.ForgeTeamDeletedEvent;
import serverutils.events.team.ForgeTeamLoadedEvent;
import serverutils.events.team.ForgeTeamPlayerJoinedEvent;
import serverutils.lib.data.ForgeTeam;

public class EventHandler {

    @SubscribeEvent
    public void onTeamCreated(ForgeTeamCreatedEvent event) {
        ForgeTeam team = event.getTeam();
        SharedProspectingMod.LOG.info("Team {} created", team.getUIDCode());

        TeamsCache.instance.get(team)
            .loadVeinCache(WorldIdHandler.getWorldId());
    }

    @SubscribeEvent
    public void onTeamLoaded(ForgeTeamLoadedEvent event) {
        ForgeTeam team = event.getTeam();
        SharedProspectingMod.LOG.info("Team {} loaded", team.getUIDCode());

        TeamsCache.instance.get(team)
            .loadVeinCache(WorldIdHandler.getWorldId());
    }

    @SubscribeEvent
    public void onTeamDeleted(ForgeTeamDeletedEvent event) {
        ForgeTeam team = event.getTeam();
        SharedProspectingMod.LOG.info("Team {} deleted, removing team cache", team.getUIDCode());

        TeamsCache.instance.remove(team, WorldIdHandler.getWorldId());
    }

    @SubscribeEvent
    public void onTeamPlayerJoined(ForgeTeamPlayerJoinedEvent event) {
        EntityPlayerMP player = event.getPlayer()
            .getPlayer();
        ForgeTeam team = event.getTeam();
        SharedProspectingMod.LOG
            .info("Player {} joined Team {}, syncing data", player.getDisplayName(), team.getUIDCode());

        SyncMsg packet = new SyncMsg();
        packet.setStartSync(true);
        SPNetwork.sendToPlayer(packet, player);
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        TeamsCache.instance.save();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) return;

        TeamCache teamCache = TeamsCache.instance.getByPlayer(player);
        if (teamCache == null) return;

        SyncMsg packet = new SyncMsg();
        packet.setStartSync(true);
        SPNetwork.sendToPlayer(packet, player);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        TickWorker.instance.onTick();
        SharedProspectingMod.proxy.batchWorker.onTick();
    }
}
