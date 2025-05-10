package com.rune580.sharedprospecting.mixins.late.visualprospecting;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rune580.sharedprospecting.networking.MessageSendDepleted;
import com.rune580.sharedprospecting.util.RevisionUtil;
import com.sinthoras.visualprospecting.database.OreVeinPosition;

import it.unimi.dsi.fastutil.longs.Long2BooleanMaps;

@Mixin(value = OreVeinPosition.class, remap = false)
public class MixinOreVeinPosition {

    @Shadow
    private boolean depleted;

    @Shadow
    @Final
    public int chunkX;

    @Shadow
    @Final
    public int chunkZ;

    @Inject(method = "toggleDepleted", at = @At("TAIL"))
    private void sharedprospecting$shareDepletion(CallbackInfo ci) {
        new MessageSendDepleted(Long2BooleanMaps.singleton(RevisionUtil.getOreVeinKey(chunkX, chunkZ), depleted))
            .sendToServer();
    }
}
