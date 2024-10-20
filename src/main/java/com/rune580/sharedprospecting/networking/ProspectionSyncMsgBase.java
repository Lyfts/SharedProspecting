package com.rune580.sharedprospecting.networking;

import java.util.ArrayList;
import java.util.List;

import com.rune580.sharedprospecting.Config;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.utils.VPByteBufUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public abstract class ProspectionSyncMsgBase implements IMessage {

    protected final List<OreVeinPosition> oreVeins = new ArrayList<>();
    protected final List<UndergroundFluidPosition> undergroundFluids = new ArrayList<>();
    private int byteSize;

    protected ProspectionSyncMsgBase(int additionalByteSize) {
        byteSize = (Integer.BYTES * 2) + additionalByteSize;
    }

    /**
     * @return number of items added, may be less than size of list given.
     */
    public int addOreVeins(List<OreVeinPosition> oreVeins) {
        final int oreCapacity = getRemainingOreVeinCapacity();
        final int consumed = Math.min(oreVeins.size(), oreCapacity);

        this.oreVeins.addAll(oreVeins.subList(0, consumed));
        byteSize += consumed * OreVeinPosition.getMaxBytes();

        return consumed;
    }

    /**
     * @return number of items added, may be less than size of list given.
     */
    public int addUndergroundFluids(List<UndergroundFluidPosition> undergroundFluids) {
        final int fluidCapacity = getRemainingUndergroundFluidCapacity();
        final int consumed = Math.min(undergroundFluids.size(), fluidCapacity);

        this.undergroundFluids.addAll(undergroundFluids.subList(0, consumed));
        byteSize += consumed * UndergroundFluidPosition.BYTES;

        return consumed;
    }

    public int getRemainingByteCapacity() {
        return Config.maxSyncPacketSizeInBytes - this.byteSize;
    }

    public int getRemainingOreVeinCapacity() {
        return getRemainingByteCapacity() / OreVeinPosition.getMaxBytes();
    }

    public int getRemainingUndergroundFluidCapacity() {
        return getRemainingByteCapacity() / UndergroundFluidPosition.BYTES;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        oreVeins.addAll(VPByteBufUtils.ReadOreVeinPositions(buf));
        undergroundFluids.addAll(VPByteBufUtils.ReadUndergroundFluidPositions(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        VPByteBufUtils.WriteOreVeinPositions(buf, oreVeins);
        VPByteBufUtils.WriteUndergroundFluidPositions(buf, undergroundFluids);
    }
}
