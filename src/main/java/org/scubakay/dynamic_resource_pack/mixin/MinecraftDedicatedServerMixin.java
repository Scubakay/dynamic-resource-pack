package org.scubakay.dynamic_resource_pack.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.scubakay.dynamic_resource_pack.util.ConfigFileHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@SuppressWarnings("UnusedMixin")
@Mixin(MinecraftDedicatedServer.class)
public class MinecraftDedicatedServerMixin {
    @Inject(method = "getResourcePackProperties", at = @At("HEAD"), cancellable = true)
    public void dynamicResourcePack$getResourcePackProperties(CallbackInfoReturnable<Optional<MinecraftServer.ServerResourcePackProperties>> cir) {
        MinecraftServer.ServerResourcePackProperties properties = ConfigFileHandler.getResourcePackProperties(null);
        cir.setReturnValue(Optional.ofNullable(properties));
        cir.cancel();
    }
}
