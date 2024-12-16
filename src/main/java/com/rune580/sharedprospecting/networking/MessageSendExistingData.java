package com.rune580.sharedprospecting.networking;

import net.minecraft.entity.player.EntityPlayerMP;

import org.jetbrains.annotations.NotNull;

import com.rune580.sharedprospecting.database.SPTeamData;
import com.rune580.sharedprospecting.util.RevisionUtil;
import com.sinthoras.visualprospecting.database.DimensionCache;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import serverutils.lib.io.DataIn;
import serverutils.lib.io.DataOut;
import serverutils.lib.net.MessageToServer;
import serverutils.lib.net.NetworkWrapper;

public class MessageSendExistingData extends MessageToServer {

    private int dimension;
    private final LongList oreVeins = new LongArrayList();
    private final LongList undergroundFluids = new LongArrayList();

    private static final DataOut.Serializer<Long> LONG_SERIALIZER = DataOut::writeLong;
    private static final DataIn.Deserializer<Long> LONG_DESERIALIZER = DataIn::readLong;

    public MessageSendExistingData() {}

    public MessageSendExistingData(DimensionCache cache) {
        this.dimension = cache.dimensionId;
        cache.getAllOreVeins()
            .forEach(vein -> oreVeins.add(RevisionUtil.getOreVeinKey(vein.chunkX, vein.chunkZ)));
        cache.getAllUndergroundFluids()
            .forEach(fluid -> undergroundFluids.add(RevisionUtil.getUndergroundFluidKey(fluid.chunkX, fluid.chunkZ)));
    }

    @Override
    public NetworkWrapper getWrapper() {
        return SPNetwork.NET;
    }

    @Override
    public void writeData(DataOut data) {
        data.writeInt(dimension);
        data.writeCollection(oreVeins, LONG_SERIALIZER);
        data.writeCollection(undergroundFluids, LONG_SERIALIZER);
    }

    @Override
    public void readData(DataIn data) {
        dimension = data.readInt();
        oreVeins.addAll(data.readCollection(LONG_DESERIALIZER));
        undergroundFluids.addAll(data.readCollection(LONG_DESERIALIZER));
    }

    @Override
    public void onMessage(@NotNull EntityPlayerMP player) {
        SPTeamData data = SPTeamData.get(player);
        if (data == null) return;

        data.addOreVeins(dimension, oreVeins);
        data.addUndergroundFluids(dimension, undergroundFluids);
    }
}
