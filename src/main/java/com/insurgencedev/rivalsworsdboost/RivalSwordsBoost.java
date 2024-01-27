package com.insurgencedev.rivalsworsdboost;

import com.insurgencedev.rivalsworsdboost.listeners.RivalSwordsEventListener;
import org.insurgencedev.insurgencesets.api.addon.ISetsAddon;
import org.insurgencedev.insurgencesets.api.addon.InsurgenceSetsAddon;
import org.insurgencedev.insurgencesets.libs.fo.Common;

@ISetsAddon(name = "RivalSwordsBoost", version = "1.0.0", author = "Insurgence Dev Team", description = "RivalMobSwords Support")
public class RivalSwordsBoost extends InsurgenceSetsAddon {

    @Override
    public void onAddonReloadablesStart() {
        if (Common.doesPluginExist("RivalMobSwords")) {
            registerEvent(new RivalSwordsEventListener());
        }
    }
}
