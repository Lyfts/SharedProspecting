package com.rune580.sharedprospecting.networking;

import net.minecraftforge.fluids.FluidRegistry;

import com.rune580.sharedprospecting.SharedProspectingMod;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;

import serverutils.lib.io.DataIn;
import serverutils.lib.io.DataOut;
import serverutils.lib.net.NetworkWrapper;

public class SPNetwork {

    static final NetworkWrapper NET = NetworkWrapper.newWrapper(SharedProspectingMod.MOD_ID);

    static final DataOut.Serializer<OreVeinPosition> ORE_SERIALIZER = SPNetwork::writeOreVein;
    static final DataOut.Serializer<UndergroundFluidPosition> FLUID_SERIALIZER = SPNetwork::writeUndergroundFluid;
    static final DataIn.Deserializer<OreVeinPosition> ORE_DESERIALIZER = SPNetwork::readOreVein;
    static final DataIn.Deserializer<UndergroundFluidPosition> FLUID_DESERIALIZER = SPNetwork::readUndergroundFluid;

    public static void init() {
        NET.register(new MessageUpdateRevision());
        NET.register(new MessageSendExistingData());
        NET.register(new MessageRequestUpdate());
    }

    private static void writeOreVein(DataOut data, OreVeinPosition oreVeinPosition) {
        data.writeInt(oreVeinPosition.dimensionId);
        data.writeInt(oreVeinPosition.chunkX);
        data.writeInt(oreVeinPosition.chunkZ);
        data.writeBoolean(oreVeinPosition.isDepleted());
        data.writeString(oreVeinPosition.veinType.name);
    }

    private static void writeUndergroundFluid(DataOut data, UndergroundFluidPosition undergroundFluidPosition) {
        data.writeInt(undergroundFluidPosition.dimensionId);
        data.writeInt(undergroundFluidPosition.chunkX);
        data.writeInt(undergroundFluidPosition.chunkZ);
        data.writeInt(undergroundFluidPosition.fluid.getID());
        for (int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
            for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                data.writeInt(undergroundFluidPosition.chunks[offsetChunkX][offsetChunkZ]);
            }
        }
    }

    private static OreVeinPosition readOreVein(DataIn data) {
        int dimId = data.readInt();
        int chunkX = data.readInt();
        int chunkZ = data.readInt();
        boolean isDepleted = data.readBoolean();
        String veinName = data.readString();

        return new OreVeinPosition(dimId, chunkX, chunkZ, VeinTypeCaching.getVeinType(veinName), isDepleted);
    }

    private static UndergroundFluidPosition readUndergroundFluid(DataIn data) {
        int dimId = data.readInt();
        int chunkX = data.readInt();
        int chunkZ = data.readInt();
        int fluidId = data.readInt();
        int[][] chunks = new int[VP.undergroundFluidSizeChunkX][VP.undergroundFluidSizeChunkZ];
        for (int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
            for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                chunks[offsetChunkX][offsetChunkZ] = data.readInt();
            }
        }

        return new UndergroundFluidPosition(dimId, chunkX, chunkZ, FluidRegistry.getFluid(fluidId), chunks);
    }
}
