package com.rune580.sharedprospecting.mixins.late.visualprospecting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.rune580.sharedprospecting.database.SPTeamData;
import com.sinthoras.visualprospecting.network.ProspectingRequest;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

@Mixin(value = ProspectingRequest.Handler.class, remap = false)
public abstract class MixinProspectingRequest {

    @ModifyReturnValue(
        method = "onMessage(Lcom/sinthoras/visualprospecting/network/ProspectingRequest;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;",
        at = @At("RETURN"))
    public IMessage sharedprospecting$captureOrePosition(IMessage original,
        @Local(argsOnly = true) MessageContext ctx) {
        if (original == null) return null;

        SPTeamData data = SPTeamData.get(ctx.getServerHandler().playerEntity);
        if (data != null) {
            ProspectingNotificationAccessor message = (ProspectingNotificationAccessor) original;
            data.addOreVeins(message.getOreVeins());
        }
        return original;
    }
}
