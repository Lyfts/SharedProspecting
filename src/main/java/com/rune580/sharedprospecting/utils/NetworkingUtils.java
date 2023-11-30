package com.rune580.sharedprospecting.utils;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.List;

public final class NetworkingUtils {

    public static void WriteOreVeinPosition(ByteBuf buf, OreVeinPosition oreVeinPosition) {
        buf.writeInt(oreVeinPosition.dimensionId);
        buf.writeInt(oreVeinPosition.chunkX);
        buf.writeInt(oreVeinPosition.chunkZ);
        buf.writeBoolean(oreVeinPosition.isDepleted());
        ByteBufUtils.writeUTF8String(buf, oreVeinPosition.veinType.name);
    }

    public static OreVeinPosition ReadOreVeinPosition(ByteBuf buf) {
        final int dimId = buf.readInt();
        final int chunkX = buf.readInt();
        final int chunkZ = buf.readInt();
        final boolean isDepleted = buf.readBoolean();
        final String oreVeinName = ByteBufUtils.readUTF8String(buf);

        return new OreVeinPosition(dimId, chunkX, chunkZ, VeinTypeCaching.getVeinType(oreVeinName), isDepleted);
    }

    public static void WriteUndergroundFluidPosition(ByteBuf buf, UndergroundFluidPosition undergroundFluidPosition) {
        buf.writeInt(undergroundFluidPosition.dimensionId);
        buf.writeInt(undergroundFluidPosition.chunkX);
        buf.writeInt(undergroundFluidPosition.chunkZ);
        buf.writeInt(undergroundFluidPosition.fluid.getID());
        for (int x = 0; x < VP.undergroundFluidSizeChunkX; x++) {
            for (int z = 0; z < VP.undergroundFluidSizeChunkZ; z++) {
                buf.writeInt(undergroundFluidPosition.chunks[x][z]);
            }
        }
    }

    public static UndergroundFluidPosition ReadUndergroundFluidPosition(ByteBuf buf) {
        final int dimId = buf.readInt();
        final int chunkX = buf.readInt();
        final int chunkZ = buf.readInt();
        final Fluid fluid = FluidRegistry.getFluid(buf.readInt());
        final int[][] chunks = new int[VP.undergroundFluidSizeChunkX][VP.undergroundFluidSizeChunkZ];
        for (int x = 0; x < VP.undergroundFluidSizeChunkX; x++) {
            for (int z = 0; z < VP.undergroundFluidSizeChunkZ; z++) {
                chunks[x][z] = buf.readInt();
            }
        }

        return new UndergroundFluidPosition(dimId, chunkX, chunkZ, fluid, chunks);
    }

    public static void WriteOreVeinPositions(ByteBuf buf, List<OreVeinPosition> oreVeinPositions) {
        buf.writeInt(oreVeinPositions.size());
        for (OreVeinPosition oreVeinPosition : oreVeinPositions) {
            WriteOreVeinPosition(buf, oreVeinPosition);
        }
    }

    public static List<OreVeinPosition> ReadOreVeinPositions(ByteBuf buf) {
        List<OreVeinPosition> oreVeinPositions = new ArrayList<>();

        final int oreVeinCount = buf.readInt();
        for (int i = 0; i < oreVeinCount; i++) {
            oreVeinPositions.add(ReadOreVeinPosition(buf));
        }

        return oreVeinPositions;
    }

    public static void WriteUndergroundFluidPositions(ByteBuf buf, List<UndergroundFluidPosition> undergroundFluidPositions) {
        buf.writeInt(undergroundFluidPositions.size());
        for (UndergroundFluidPosition undergroundFluidPosition : undergroundFluidPositions) {
            WriteUndergroundFluidPosition(buf, undergroundFluidPosition);
        }
    }

    public static List<UndergroundFluidPosition> ReadUndergroundFluidPositions(ByteBuf buf) {
        List<UndergroundFluidPosition> undergroundFluidPositions = new ArrayList<>();

        final int undergroundFluidCount = buf.readInt();
        for (int i = 0; i < undergroundFluidCount; i++) {
            undergroundFluidPositions.add(ReadUndergroundFluidPosition(buf));
        }

        return undergroundFluidPositions;
    }
}
