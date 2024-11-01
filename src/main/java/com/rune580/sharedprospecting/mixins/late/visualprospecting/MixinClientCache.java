package com.rune580.sharedprospecting.mixins.late.visualprospecting;

import org.spongepowered.asm.mixin.Mixin;

import com.rune580.sharedprospecting.database.ClientRevision;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.WorldCache;

@Mixin(value = ClientCache.class, remap = false)
public abstract class MixinClientCache extends WorldCache {

    @Override
    public boolean loadVeinCache(String worldId) {
        boolean loaded = ((WorldCacheAccessor) this).getIsLoaded();
        boolean result = super.loadVeinCache(worldId);
        if (loaded) return result;

        ClientRevision.onClientCacheLoad();
        return result;
    }
}
