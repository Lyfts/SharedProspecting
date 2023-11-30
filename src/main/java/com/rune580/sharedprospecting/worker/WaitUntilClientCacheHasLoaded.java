package com.rune580.sharedprospecting.worker;

import com.rune580.sharedprospecting.mixinaccess.visualprospecting.IClientCacheMixin;
import com.sinthoras.visualprospecting.database.ClientCache;

public class WaitUntilClientCacheHasLoaded implements IWork {

    private final IClientCacheMixin cache;

    public WaitUntilClientCacheHasLoaded() {
        cache = (IClientCacheMixin) ClientCache.instance;
    }

    @Override
    public boolean run() {
        boolean isLoaded = cache.sharedProspecting$getIsLoaded();

        if (isLoaded) {
            ClientToServerFullSync work = new ClientToServerFullSync();
            TickWorker.instance.queueWork(work);
        }

        return isLoaded;
    }
}
