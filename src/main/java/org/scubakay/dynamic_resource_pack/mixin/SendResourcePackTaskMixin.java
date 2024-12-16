package org.scubakay.dynamic_resource_pack.mixin;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.server.network.SendResourcePackTask;
import org.scubakay.dynamic_resource_pack.config.ServerProperties;
import org.scubakay.dynamic_resource_pack.util.ConfigFileHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(SendResourcePackTask.class)
public class SendResourcePackTaskMixin {
    @Inject(at = @At("HEAD"), method = "sendPacket", cancellable = true)
    public void injectPacketSend(Consumer<Packet<?>> sender, CallbackInfo ci) {
        ServerProperties config = ConfigFileHandler.getInstance().config;
        sender.accept(
                new ResourcePackSendS2CPacket(
                        config.id.get(),
                        config.url.get(),
                        config.hash.get(),
                        config.required.get(),
                        config.getPrompt(ConfigFileHandler.getInstance().getServer().getRegistryManager())
                )
        );
        ci.cancel();
    }
}
