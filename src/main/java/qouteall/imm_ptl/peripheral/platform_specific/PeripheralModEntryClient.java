package qouteall.imm_ptl.peripheral.platform_specific;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import qouteall.imm_ptl.peripheral.CommandStickItem;
import qouteall.imm_ptl.peripheral.PeripheralModMain;

public class PeripheralModEntryClient {
    public static void registerBlockRenderLayers() {
//        BlockRenderLayerMap.INSTANCE.putBlock(
//            PeripheralModMain.portalHelperBlock,
//            RenderType.cutout()
//        );
    }

    public void onInitializeClient(IEventBus modEventBus) {
        PeripheralModEntryClient.registerBlockRenderLayers();

        PeripheralModMain.initClient();

        // Must not happen in the mod constructor: CommandStickItem.instance is only assigned when
        // the item RegisterEvent fires, which is after all mod constructors have run. Registering
        // the item property against a null item silently produces no property at all, and every
        // command stick variant then falls back to the default model.
        modEventBus.addListener(FMLClientSetupEvent.class, event -> CommandStickItem.initClient());
    }
}
