package com.rune580.sharedprospecting.database;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.rune580.sharedprospecting.mixins.late.visualprospecting.WorldCacheAccessor;
import com.rune580.sharedprospecting.networking.MessageRequestUpdate;
import com.rune580.sharedprospecting.networking.MessageSendExistingData;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.DimensionCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.WorldCache;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLongPair;
import serverutils.lib.util.NBTUtils;

@EventBusSubscriber(side = Side.CLIENT)
public class ClientRevision {

    private static final Map<Integer, DimensionCache> clientCacheDims;
    private static final Int2LongMap dimRevision = new Int2LongOpenHashMap();
    private static File revisionFile;
    private static String teamId = "";
    private static ObjectLongPair<String> pendingRevision;

    static {
        try {
            // noinspection unchecked
            clientCacheDims = (Map<Integer, DimensionCache>) ReflectionHelper.findField(WorldCache.class, "dimensions")
                .get(ClientCache.instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get client dimensions field", e);
        }
    }

    private static void loadRevisionData(File dir) {
        if (!dimRevision.isEmpty()) {
            reset();
        }

        NBTTagCompound compound = NBTUtils.readNBT(revisionFile = new File(dir, "revision.dat"));
        if (compound == null) return;
        teamId = compound.getString("teamId");

        NBTTagCompound revisions = compound.getCompoundTag("revisions");
        for (String key : revisions.func_150296_c()) {
            dimRevision.put(Integer.parseInt(key), revisions.getLong(key));
        }
    }

    private static void saveRevisionData() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setString("teamId", teamId);
        NBTTagCompound revisions = new NBTTagCompound();
        for (int dim : dimRevision.keySet()) {
            revisions.setLong(Integer.toString(dim), dimRevision.get(dim));
        }
        compound.setTag("revisions", revisions);
        NBTUtils.writeNBT(revisionFile, compound);
    }

    public static void onClientCacheLoad(File dir) {
        loadRevisionData(dir);
        if (pendingRevision != null) {
            int dim = Minecraft.getMinecraft().thePlayer.dimension;
            updateRevision(pendingRevision.left(), dim, pendingRevision.rightLong());
            pendingRevision = null;
        }
    }

    public static void updateRevision(@NotNull String team, int dim, long revision) {
        WorldCacheAccessor cache = (WorldCacheAccessor) ClientCache.instance;
        if (!cache.getIsLoaded()) {
            pendingRevision = ObjectLongPair.of(team, revision);
            return;
        }

        if (!teamId.equals(team)) {
            teamId = team;
            dimRevision.clear();
        }

        long oldRevision = dimRevision.put(dim, revision);
        if (oldRevision == 0) {
            DimensionCache dimension = clientCacheDims.get(dim);
            if (dimension != null) {
                Collection<OreVeinPosition> veins = dimension.getAllOreVeins();
                Collection<UndergroundFluidPosition> fluids = dimension.getAllUndergroundFluids();
                if (!veins.isEmpty() || !fluids.isEmpty()) {
                    new MessageSendExistingData(dimension).sendToServer();
                }
            }
        }

        new MessageRequestUpdate(oldRevision).sendToServer();
    }

    public static void reset() {
        saveRevisionData();
        dimRevision.clear();
    }

    @SubscribeEvent
    public static void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        reset();
    }
}
