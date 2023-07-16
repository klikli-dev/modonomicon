package com.klikli_dev.modonomicon.config;

import com.klikli_dev.modonomicon.platform.services.ClientConfigHelper;

public class ForgeClientConfigHelper implements ClientConfigHelper {
    @Override
    public boolean enableSmoothZoom() {
        return ClientConfig.get().qolCategory.enableSmoothZoom.get();
    }

    @Override
    public boolean storeLastOpenPageWhenClosingEntry() {
        return ClientConfig.get().qolCategory.storeLastOpenPageWhenClosingEntry.get();
    }
}
