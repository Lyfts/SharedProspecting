package com.rune580.sharedprospecting.database;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;

import serverutils.lib.data.ForgeTeam;

public class TeamsCache {

    public static final TeamsCache instance = new TeamsCache();

    private final Map<String, TeamCache> teamCacheMap = new HashMap<>();

    public TeamCache get(ForgeTeam team) {
        final String uuid = team.getUIDCode();
        return teamCacheMap.computeIfAbsent(uuid, (_key) -> new TeamCache(team));
    }

    public @Nullable TeamCache getByPlayer(EntityPlayerMP player) {
        return teamCacheMap.values()
            .stream()
            .filter(
                value -> value.getTeam()
                    .getOnlineMembers()
                    .stream()
                    .anyMatch(
                        teamPlayer -> teamPlayer.getPersistentID()
                            .equals(player.getPersistentID())))
            .findFirst()
            .orElse(null);
    }

    public void save() {
        for (TeamCache teamCache : teamCacheMap.values()) {
            teamCache.saveVeinCache();
        }
    }

    public void reset() {
        for (TeamCache teamCache : teamCacheMap.values()) {
            teamCache.reset();
        }

        teamCacheMap.clear();
    }

    public void remove(String uuid, String worldId) {
        TeamCache teamCache = teamCacheMap.get(uuid);
        if (teamCache == null) return;
        teamCache.delete(worldId);

        teamCacheMap.remove(uuid);
    }

    public void remove(ForgeTeam team, String worldId) {
        remove(team.getUIDCode(), worldId);
    }
}
