package com.rune580.sharedprospecting.mixins.late.visualprospecting;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.rune580.sharedprospecting.database.ClientRevision;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.network.WorldIdNotification;

@Mixin(value = WorldIdNotification.Handler.class, remap = false)
public abstract class MixinWorldIdNotification {

    @WrapOperation(
        method = "onMessage(Lcom/sinthoras/visualprospecting/network/WorldIdNotification;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;",
        at = @At(
            value = "INVOKE",
            target = "Lcom/sinthoras/visualprospecting/database/ClientCache;loadVeinCache(Ljava/lang/String;)Z"))
    private boolean sharedprospecting$loadClientRevision(ClientCache instance, String worldId,
        Operation<Boolean> original) {
        boolean result = original.call(instance, worldId);
        ClientRevision.onClientCacheLoad(new File(((WorldCacheAccessor) instance).callGetStorageDirectory(), worldId));
        return result;
    }
}
