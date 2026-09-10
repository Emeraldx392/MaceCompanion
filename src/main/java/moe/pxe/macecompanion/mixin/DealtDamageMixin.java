package moe.pxe.macecompanion.mixin;

import moe.pxe.macecompanion.enums.AchievementLostTriggers;
import moe.pxe.macecompanion.stateManagers.AchievementManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class DealtDamageMixin {

    @Inject(method = "handleDamageEvent", at = @At("HEAD"))
    private void onEntityDamagedPacket(ClientboundDamageEventPacket packet, CallbackInfo ci) {
        ClientPacketListener listener = (ClientPacketListener) (Object) this;
        if (listener.getLevel() != null) {
            Entity damagedEntity = listener.getLevel().getEntity(packet.entityId());
            if (damagedEntity != null && damagedEntity == AchievementManager.INSTANCE.getLastTarget()) {
                AchievementManager.INSTANCE.addAchievementLostTrigger(AchievementLostTriggers.DEAL_DAMAGE);
                AchievementManager.INSTANCE.setLastTarget(null);
            }
        }
    }
}
