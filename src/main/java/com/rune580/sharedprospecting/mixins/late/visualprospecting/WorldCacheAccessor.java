package com.rune580.sharedprospecting.mixins.late.visualprospecting;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.sinthoras.visualprospecting.database.DimensionCache;
import com.sinthoras.visualprospecting.database.WorldCache;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

@Mixin(value = WorldCache.class, remap = false)
public interface WorldCacheAccessor {

    @Accessor
    Int2ObjectMap<DimensionCache> getDimensions();

    @Accessor
    boolean getIsLoaded();

    @Invoker
    File callGetStorageDirectory();
}
