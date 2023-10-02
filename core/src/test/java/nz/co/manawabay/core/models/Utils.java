package nz.co.manawabay.core.models;

import com.adobe.cq.wcm.core.components.internal.DataLayerConfig;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.mockito.Mockito;

import static org.mockito.Mockito.lenient;
public class Utils {
    /**
     * Sets the data layer context aware configuration of the AEM test context to enable the data layer.
     *
     * @param context The AEM test context
     * @param enabled {@code true} to enable the data layer, {@code false} to disable it
     */
    public static void enableDataLayer(AemContext context, boolean enabled) {
        configureDataLayer(context, enabled, false);
    }

    private static void configureDataLayer(AemContext context, boolean enabled, boolean skip) {
        ConfigurationBuilder builder = Mockito.mock(ConfigurationBuilder.class);
        DataLayerConfig dataLayerConfig = Mockito.mock(DataLayerConfig.class);
        lenient().when(dataLayerConfig.enabled()).thenReturn(enabled);
        lenient().when(dataLayerConfig.skipClientlibInclude()).thenReturn(skip);
        lenient().when(builder.as(DataLayerConfig.class)).thenReturn(dataLayerConfig);
        context.registerAdapter(Resource.class, ConfigurationBuilder.class, builder);
    }
}
