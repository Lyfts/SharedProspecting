package com.rune580.sharedprospecting.networking;

import com.rune580.sharedprospecting.SharedProspectingMod;

import serverutils.lib.net.NetworkWrapper;

public class SPNetwork {

    static final NetworkWrapper NET = NetworkWrapper.newWrapper(SharedProspectingMod.MOD_ID);

    public static void init() {
        NET.register(new MessageUpdateRevision());
        NET.register(new MessageSendExistingData());
        NET.register(new MessageRequestUpdate());
        NET.register(new MessageSendDepleted());
        NET.register(new MessageUpdateDepletions());
        NET.register(new MessageUpdateSingleDepleted());
    }
}
