package com.klikli_dev.modonomicon.platform;

import com.klikli_dev.modonomicon.Modonomicon;
import com.klikli_dev.modonomicon.platform.services.ClientConfigHelper;
import com.klikli_dev.modonomicon.platform.services.GuiHelper;
import com.klikli_dev.modonomicon.platform.services.NetworkHelper;
import com.klikli_dev.modonomicon.platform.services.PlatformHelper;

import java.util.ServiceLoader;

// Service loaders are a built-in Java feature that allow us to locate implementations of an interface that vary from one
// environment to another. In the context of MultiLoader we use this feature to access a mock API in the common code that
// is swapped out for the platform specific implementation at runtime.
public class Services {

    public static final PlatformHelper PLATFORM = load(PlatformHelper.class);
    public static final NetworkHelper NETWORK = load(NetworkHelper.class);
    public static final ClientConfigHelper CLIENT_CONFIG = load(ClientConfigHelper.class);
    public static final GuiHelper GUI = load(GuiHelper.class);

    // This code is used to load a service for the current environment. Your implementation of the service must be defined
    // manually by including a text file in META-INF/services named with the fully qualified class name of the service.
    // Inside the file you should write the fully qualified class name of the implementation to load for the platform. For
    // example our file on Forge points to ForgePlatformHelper while Fabric points to FabricPlatformHelper.
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Modonomicon.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}