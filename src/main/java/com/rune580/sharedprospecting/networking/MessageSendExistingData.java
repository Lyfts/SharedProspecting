package com.rune580.sharedprospecting.networking;

import static com.rune580.sharedprospecting.networking.SPNetwork.FLUID_DESERIALIZER;
import static com.rune580.sharedprospecting.networking.SPNetwork.FLUID_SERIALIZER;
import static com.rune580.sharedprospecting.networking.SPNetwork.ORE_DESERIALIZER;
import static com.rune580.sharedprospecting.networking.SPNetwork.ORE_SERIALIZER;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayerMP;

import org.jetbrains.annotations.NotNull;

import com.rune580.sharedprospecting.database.SPTeamData;
import com.sinthoras.visualprospecting.database.DimensionCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;

import serverutils.lib.io.DataIn;
import serverutils.lib.io.DataOut;
import serverutils.lib.net.MessageToServer;
import serverutils.lib.net.NetworkWrapper;

public class MessageSendExistingData extends MessageToServer {

    private Collection<OreVeinPosition> oreVeins;
    private Collection<UndergroundFluidPosition> undergroundFluids;

    public MessageSendExistingData() {}

    public MessageSendExistingData(DimensionCache cache) {
        this.oreVeins = cache.getAllOreVeins();
        this.undergroundFluids = cache.getAllUndergroundFluids();
    }

    @Override
    public NetworkWrapper getWrapper() {
        return SPNetwork.NET;
    }

    @Override
    public void writeData(DataOut data) {
        data.writeCollection(oreVeins, ORE_SERIALIZER);
        data.writeCollection(undergroundFluids, FLUID_SERIALIZER);
    }

    @Override
    public void readData(DataIn data) {
        oreVeins = data.readCollection(ORE_DESERIALIZER);
        undergroundFluids = data.readCollection(FLUID_DESERIALIZER);
    }

    @Override
    public void onMessage(@NotNull EntityPlayerMP player) {
        SPTeamData data = SPTeamData.get(player);
        if (data == null) return;

        data.addOreVeins(oreVeins);
        data.addUndergroundFluids(undergroundFluids);
    }
}
