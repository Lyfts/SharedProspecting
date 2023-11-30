package com.rune580.sharedprospecting.mixins.visualprospecting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.rune580.sharedprospecting.mixinaccess.visualprospecting.IClientCacheMixin;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.WorldCache;

@Mixin(value = ClientCache.class, remap = false)
public abstract class ClientCacheMixin extends WorldCache implements IClientCacheMixin {

    @Unique
    @Override
    public boolean sharedProspecting$getIsLoaded() {
        return ((WorldCacheAccessor) this).getIsLoaded();
    }
}
