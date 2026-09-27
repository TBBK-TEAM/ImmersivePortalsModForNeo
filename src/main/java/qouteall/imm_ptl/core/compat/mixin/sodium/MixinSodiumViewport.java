package qouteall.imm_ptl.core.compat.mixin.sodium;

import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.caffeinemc.mods.sodium.client.render.viewport.frustum.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qouteall.imm_ptl.core.compat.sodium_compatibility.SodiumInterface;

@Mixin(value = Viewport.class, remap = false)
public class MixinSodiumViewport {
    /**
     * Since Sodium 0.8, {@link Viewport#isBoxVisible(int, int, int)} uses
     * {@link Frustum#testSection(float, float, float)} instead of
     * {@link Frustum#testAab(float, float, float, float, float, float)}.
     * The passed coordinates are the chunk section's camera-relative origin,
     * so the section's bounding box (which is what ImmPtl's frustum culler expects)
     * is reconstructed around it with {@link Viewport#CHUNK_SECTION_RADIUS}.
     */
    @Redirect(
        method = "isBoxVisible",
        at = @At(
            value = "INVOKE",
            target = "Lnet/caffeinemc/mods/sodium/client/render/viewport/frustum/Frustum;testSection(FFF)Z"
        )
    )
    private boolean redirectTestSection(
        Frustum instance,
        float originX, float originY, float originZ
    ) {
        boolean inFrustum = instance.testSection(
            originX, originY, originZ
        );
        
        if (inFrustum) {
            if (SodiumInterface.frustumCuller != null) {
                float radius = Viewport.CHUNK_SECTION_RADIUS;
                boolean canDetermineInvisible =
                    SodiumInterface.frustumCuller.canDetermineInvisibleWithCameraCoord(
                        originX - radius, originY - radius, originZ - radius,
                        originX + radius, originY + radius, originZ + radius
                    );
                return !canDetermineInvisible;
            }
        }
        
        return inFrustum;
    }
}
