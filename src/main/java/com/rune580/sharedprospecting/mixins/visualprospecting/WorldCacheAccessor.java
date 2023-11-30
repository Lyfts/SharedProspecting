package com.rune580.sharedprospecting.mixins.visualprospecting;

import com.sinthoras.visualprospecting.database.WorldCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldCache.class)
public interface WorldCacheAccessor {

    @Accessor(remap = false)
    boolean getIsLoaded();
}
