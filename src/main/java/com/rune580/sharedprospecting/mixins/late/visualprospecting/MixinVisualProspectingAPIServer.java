package com.rune580.sharedprospecting.mixins.late.visualprospecting;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rune580.sharedprospecting.database.SPTeamData;
import com.sinthoras.visualprospecting.VisualProspecting_API;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;

@Mixin(value = VisualProspecting_API.LogicalServer.class, remap = false)
public abstract class MixinVisualProspectingAPIServer {

    @Inject(method = "sendProspectionResultsToClient", at = @At("HEAD"))
    private static void sharedprospecting$addOresToTeam(EntityPlayerMP player, List<OreVeinPosition> oreVeins,
        List<UndergroundFluidPosition> undergroundFluids, CallbackInfo ci) {
        SPTeamData data = SPTeamData.get(player);
        if (data == null) return;

        data.addOreVeins(oreVeins);
        data.addUndergroundFluids(undergroundFluids);
    }
}
