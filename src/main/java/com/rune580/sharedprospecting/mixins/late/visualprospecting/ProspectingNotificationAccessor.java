package com.rune580.sharedprospecting.mixins.late.visualprospecting;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.network.ProspectingNotification;

@Mixin(value = ProspectingNotification.class, remap = false)
public interface ProspectingNotificationAccessor {

    @Accessor
    List<OreVeinPosition> getOreVeins();
}
