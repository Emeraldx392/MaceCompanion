package moe.pxe.macecompanion.mixin;

import moe.pxe.macecompanion.stateManagers.SoundManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundMixin {

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void onPlaySound(SoundInstance instance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {

        String soundId = instance.getIdentifier().toString();

        if (SoundManager.INSTANCE.getSoundsToCancel().containsKey(soundId)) {
            if(!SoundManager.INSTANCE.getSoundsToCancel().get(soundId)) SoundManager.INSTANCE.getSoundsToCancel().remove(soundId);
            cir.cancel();
        }
    }
}
