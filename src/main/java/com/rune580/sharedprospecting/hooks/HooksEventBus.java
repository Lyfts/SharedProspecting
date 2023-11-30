package com.rune580.sharedprospecting.hooks;

import com.rune580.sharedprospecting.SharedProspectingMod;
import com.rune580.sharedprospecting.database.TeamsCache;
import com.rune580.sharedprospecting.networking.SPNetwork;
import com.rune580.sharedprospecting.networking.SyncMsg;
import com.rune580.sharedprospecting.worker.batch.ClientSyncBatchWork;
import com.sinthoras.visualprospecting.database.WorldIdHandler;
import com.sinthoras.visualprospecting.hooks.ProspectingNotificationEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent;
import serverutils.events.team.ForgeTeamCreatedEvent;
import serverutils.events.team.ForgeTeamDeletedEvent;
import serverutils.events.team.ForgeTeamLoadedEvent;
import serverutils.events.team.ForgeTeamPlayerJoinedEvent;
import serverutils.lib.data.ForgeTeam;

public class HooksEventBus {
    @SubscribeEvent
    public void onProspectingOreNotificationEvent(ProspectingNotificationEvent.OreVein event) {
        ClientSyncBatchWork.instance.addOreVein(event.getPosition());
    }

    @SubscribeEvent
    public void OnProspectingFluidNotificationEvent(ProspectingNotificationEvent.UndergroundFluid event) {
        ClientSyncBatchWork.instance.addUndergroundFluid(event.getPosition());
    }

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
        EntityPlayerMP player =  event.getPlayer().getPlayer();
        ForgeTeam team = event.getTeam();
        SharedProspectingMod.LOG.info("Player {} joined Team {}, syncing data", player.getDisplayName(), team.getUIDCode());

        SyncMsg packet = new SyncMsg();
        packet.setStartSync(true);
        SPNetwork.sendToPlayer(packet, player);
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        TeamsCache.instance.save();
    }
}
