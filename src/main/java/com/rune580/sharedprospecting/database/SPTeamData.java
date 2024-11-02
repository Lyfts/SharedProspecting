package com.rune580.sharedprospecting.database;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.rune580.sharedprospecting.SharedProspectingMod;
import com.rune580.sharedprospecting.mixins.late.visualprospecting.WorldCacheAccessor;
import com.rune580.sharedprospecting.networking.MessageUpdateRevision;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.DimensionCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.WorldCache;
import com.sinthoras.visualprospecting.database.WorldIdHandler;
import com.sinthoras.visualprospecting.network.ProspectingNotification;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import serverutils.events.team.ForgeTeamDataEvent;
import serverutils.events.team.ForgeTeamDeletedEvent;
import serverutils.events.team.ForgeTeamLoadedEvent;
import serverutils.events.team.ForgeTeamPlayerJoinedEvent;
import serverutils.events.team.ForgeTeamSavedEvent;
import serverutils.lib.data.ForgeTeam;
import serverutils.lib.data.TeamData;
import serverutils.lib.data.TeamType;
import serverutils.lib.data.Universe;
import serverutils.lib.util.FileUtils;
import serverutils.lib.util.NBTUtils;

@EventBusSubscriber
public class SPTeamData extends TeamData {

    private final Int2ObjectMap<OrderedDimCache> dimensions = new Int2ObjectOpenHashMap<>();

    public SPTeamData(ForgeTeam team) {
        super(team);
    }

    public static SPTeamData get(ForgeTeam team) {
        return team.getData()
            .get(SharedProspectingMod.MOD_ID);
    }

    public static @Nullable SPTeamData get(EntityPlayerMP player) {
        ForgeTeam team = Universe.get()
            .getPlayer(player).team;
        if (team.type.equals(TeamType.NONE)) return null;
        return get(team);
    }

    public void load(NBTTagCompound nbt) {
        importOldData();
        if (nbt == null) return;
        NBTTagCompound dimTag = nbt.getCompoundTag("dimensions");
        for (String key : dimTag.func_150296_c()) {
            int dimId = Integer.parseInt(key);
            dimensions.computeIfAbsent(dimId, OrderedDimCache::new)
                .load(dimTag.getCompoundTag(key));
        }
    }

    public NBTTagCompound save() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound dimTag = new NBTTagCompound();
        for (Int2ObjectMap.Entry<OrderedDimCache> cache : dimensions.int2ObjectEntrySet()) {
            dimTag.setTag(
                Integer.toString(cache.getIntKey()),
                cache.getValue()
                    .save());
        }
        nbt.setTag("dimensions", dimTag);
        return nbt;
    }

    @Override
    public String getId() {
        return SharedProspectingMod.MOD_ID;
    }

    public Int2ObjectMap<OrderedDimCache> getDimensions() {
        return dimensions;
    }

    public void sendUpdate(EntityPlayerMP player, long playerRevision) {
        OrderedDimCache cache = dimensions.computeIfAbsent(player.dimension, OrderedDimCache::new);
        long revision = cache.getRevision();
        if (revision == playerRevision) return;

        List<OreVeinPosition> oreVeins = cache.getOresForRevision(playerRevision);
        List<UndergroundFluidPosition> fluids = cache.getFluidsForRevision(playerRevision);
        VP.network.sendTo(new ProspectingNotification(oreVeins, fluids), player);
    }

    public long getDimRevision(int dim) {
        OrderedDimCache cache = dimensions.computeIfAbsent(dim, OrderedDimCache::new);
        return cache.getRevision();
    }

    public void updateMemberRevision(EntityPlayerMP player) {
        int dim = player.dimension;
        new MessageUpdateRevision(team.getUIDCode(), dim, getDimRevision(dim)).sendTo(player);
    }

    public void addOreVeins(Collection<OreVeinPosition> oreVeins) {
        IntSet modifiedDims = new IntArraySet();
        for (OreVeinPosition oreVein : oreVeins) {
            if (putOreVein(oreVein)) {
                modifiedDims.add(oreVein.dimensionId);
            }
        }

        updateMembers(modifiedDims);
    }

    public void addUndergroundFluids(Collection<UndergroundFluidPosition> undergroundFluids) {
        IntSet modifiedDims = new IntArraySet();
        for (UndergroundFluidPosition fluidPosition : undergroundFluids) {
            if (putUndergroundFluids(fluidPosition)) {
                modifiedDims.add(fluidPosition.dimensionId);
            }
        }

        updateMembers(modifiedDims);
    }

    public boolean putOreVein(OreVeinPosition vein) {
        OrderedDimCache cache = dimensions.computeIfAbsent(vein.dimensionId, OrderedDimCache::new);
        return cache.putOreVein(vein);
    }

    public boolean putUndergroundFluids(UndergroundFluidPosition fluid) {
        OrderedDimCache cache = dimensions.computeIfAbsent(fluid.dimensionId, OrderedDimCache::new);
        return cache.putUndergroundFluid(fluid);
    }

    public void updateMembers(IntSet modifiedDims) {
        if (modifiedDims.isEmpty()) return;

        team.markDirty();
        for (EntityPlayerMP player : team.getOnlineMembers()) {
            int dim = player.dimension;
            if (modifiedDims.contains(dim)) {
                updateMemberRevision(player);
            }
        }
    }

    public void importOldData() {
        File oldFile = new File(SharedProspectingMod.MOD_ID + "/teams/", team.getUIDCode());
        if (!oldFile.exists()) return;
        WorldCache tempCache = new WorldCache() {

            @Override
            protected File getStorageDirectory() {
                return oldFile;
            }
        };

        WorldCacheAccessor tempCacheAccessor = (WorldCacheAccessor) tempCache;
        if (!tempCacheAccessor.callGetStorageDirectory()
            .exists()) return;

        tempCache.loadVeinCache(WorldIdHandler.getWorldId());

        Map<Integer, DimensionCache> dimensionCacheMap;
        try {
            // this is only done once when migrating the data, no need to cache the field
            // noinspection unchecked
            dimensionCacheMap = (Map<Integer, DimensionCache>) ReflectionHelper
                .findField(WorldCache.class, "dimensions")
                .get(tempCache);
        } catch (IllegalAccessException e) {
            SharedProspectingMod.LOG.error("Failed to get dimension cache map", e);
            return;
        }

        for (DimensionCache oldCache : dimensionCacheMap.values()) {
            addOreVeins(oldCache.getAllOreVeins());
            addUndergroundFluids(oldCache.getAllUndergroundFluids());
        }

        FileUtils.delete(oldFile);
        team.markDirty();
    }

    public static void updatePlayer(EntityPlayerMP player) {
        SPTeamData data = SPTeamData.get(player);
        if (data == null) return;
        data.updateMemberRevision(player);
    }

    @SubscribeEvent
    public static void onTeamLoaded(ForgeTeamLoadedEvent event) {
        ForgeTeam team = event.getTeam();
        SharedProspectingMod.LOG.info("Team {} loaded", team.getUIDCode());

        NBTTagCompound nbt = NBTUtils.readNBT(team.getDataFile(SharedProspectingMod.MOD_ID));
        SPTeamData.get(team)
            .load(nbt);
    }

    @SubscribeEvent
    public static void onTeamPlayerJoined(ForgeTeamPlayerJoinedEvent event) {
        EntityPlayerMP player = event.getPlayer()
            .getPlayer();
        SPTeamData.get(event.getTeam())
            .updateMemberRevision(player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP playerMP) || event.player.worldObj.isRemote) return;
        updatePlayer(playerMP);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.player instanceof EntityPlayerMP playerMP) || event.player.worldObj.isRemote) return;
        updatePlayer(playerMP);
    }

    @SubscribeEvent
    public static void registerTeamData(ForgeTeamDataEvent event) {
        event.register(new SPTeamData(event.getTeam()));
    }

    @SubscribeEvent
    public static void onTeamSaved(ForgeTeamSavedEvent event) {
        ForgeTeam team = event.getTeam();
        NBTUtils.writeNBTSafe(
            team.getDataFile(SharedProspectingMod.MOD_ID),
            SPTeamData.get(team)
                .save());
    }

    @SubscribeEvent
    public static void onTeamDeleted(ForgeTeamDeletedEvent event) {
        FileUtils.delete(
            event.getTeam()
                .getDataFile(SharedProspectingMod.MOD_ID));
    }
}
