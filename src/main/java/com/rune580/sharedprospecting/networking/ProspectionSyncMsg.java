package com.rune580.sharedprospecting.networking;

import com.rune580.sharedprospecting.Config;
import com.rune580.sharedprospecting.SharedProspectingMod;
import com.rune580.sharedprospecting.database.TeamCache;
import com.rune580.sharedprospecting.database.TeamsCache;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.List;

public class ProspectionSyncMsg implements IMessage {
    private final List<OreVeinPosition> oreVeins = new ArrayList<>();
    private final List<UndergroundFluidPosition> undergroundFluids = new ArrayList<>();
    private int byteSize;

    public ProspectionSyncMsg() {
        byteSize = Integer.BYTES * 2;
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

    public int addUndergroundFluids(List<UndergroundFluidPosition> undergroundFluids) {
        final int fluidCapacity = getRemainingUndergroundFluidCapacity();
        final int consumed = Math.min(undergroundFluids.size(), fluidCapacity);

        this.undergroundFluids.addAll(undergroundFluids.subList(0, consumed));
        byteSize += consumed * UndergroundFluidPosition.BYTES;

        return consumed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final int oreVeinCount = buf.readInt();
        for (int i = 0; i < oreVeinCount; i++) {
            final int dimId = buf.readInt();
            final int chunkX = buf.readInt();
            final int chunkZ = buf.readInt();
            final boolean isDepleted = buf.readBoolean();
            final String oreVeinName = ByteBufUtils.readUTF8String(buf);

            oreVeins.add(new OreVeinPosition(dimId, chunkX, chunkZ, VeinTypeCaching.getVeinType(oreVeinName), isDepleted));
        }

        final int fluidCount = buf.readInt();
        for (int i = 0; i < fluidCount; i++) {
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

            undergroundFluids.add(new UndergroundFluidPosition(dimId, chunkX, chunkZ, fluid, chunks));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(oreVeins.size());
        for (OreVeinPosition oreVein : oreVeins) {
            buf.writeInt(oreVein.dimensionId);
            buf.writeInt(oreVein.chunkX);
            buf.writeInt(oreVein.chunkZ);
            buf.writeBoolean(oreVein.isDepleted());
            ByteBufUtils.writeUTF8String(buf, oreVein.veinType.name);
        }

        buf.writeInt(undergroundFluids.size());
        for (UndergroundFluidPosition undergroundFluid : undergroundFluids) {
            buf.writeInt(undergroundFluid.dimensionId);
            buf.writeInt(undergroundFluid.chunkX);
            buf.writeInt(undergroundFluid.chunkZ);
            buf.writeInt(undergroundFluid.fluid.getID());
            for (int x = 0; x < VP.undergroundFluidSizeChunkX; x++) {
                for (int z = 0; z < VP.undergroundFluidSizeChunkZ; z++) {
                    buf.writeInt(undergroundFluid.chunks[x][z]);
                }
            }
        }
    }

    public int getByteSize() {
        return this.byteSize;
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

    public static class ServerHandler implements IMessageHandler<ProspectionSyncMsg, IMessage> {
        @Override
        public IMessage onMessage(ProspectionSyncMsg message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            TeamCache teamCache = TeamsCache.instance.getByPlayer(player);
            if (teamCache == null) {
                SharedProspectingMod.LOG.warn("Server received sync message for a player without a team cache!");
                return null;
            }

            teamCache.addOreVeins(message.oreVeins);
            teamCache.addUndergroundFluids(message.undergroundFluids);

            return null;
        }
    }

    public static class ClientHandler implements IMessageHandler<ProspectionSyncMsg, IMessage> {
        @Override
        public IMessage onMessage(ProspectionSyncMsg message, MessageContext ctx) {
            ClientCache.instance.putOreVeins(message.oreVeins);
            ClientCache.instance.putUndergroundFluids(message.undergroundFluids);

            return null;
        }
    }
}
