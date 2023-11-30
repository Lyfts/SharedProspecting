package com.rune580.sharedprospecting.mixins.visualprospecting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.sinthoras.visualprospecting.database.WorldCache;

@Mixin(WorldCache.class)
public interface WorldCacheAccessor {

    @Accessor(remap = false)
    boolean getIsLoaded();
}
