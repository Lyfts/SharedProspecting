package com.rune580.sharedprospecting.hooks;

import com.rune580.sharedprospecting.SharedProspectingMod;
import com.rune580.sharedprospecting.database.TeamsCache;
import com.sinthoras.visualprospecting.database.WorldIdHandler;
import com.sinthoras.visualprospecting.hooks.ProspectingNotificationEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;
import serverutils.events.team.ForgeTeamCreatedEvent;
import serverutils.events.team.ForgeTeamDeletedEvent;
import serverutils.events.team.ForgeTeamLoadedEvent;
import serverutils.events.team.ForgeTeamPlayerJoinedEvent;
import serverutils.lib.data.ForgeTeam;

public class HooksEventBus {
    @SubscribeEvent
    public void onProspectingNotificationEvent(ProspectingNotificationEvent.OreVein event) {
        SharedProspectingMod.LOG.warn("Did this even work?, {}", event);
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
        ForgeTeam team = event.getTeam();
        SharedProspectingMod.LOG.info("PLAYER JOINEDMA");


    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        TeamsCache.instance.save();
    }
}
