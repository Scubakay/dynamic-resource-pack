package org.scubakay.dynamic_resource_pack.mixin;

import net.minecraft.server.network.SendResourcePackTask;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.scubakay.dynamic_resource_pack.util.ConfigFileHandler;

@Mixin(SendResourcePackTask.class)
public class SendResourcePackTaskMixin {
    @Mutable
    @Final
    @Shadow
    private MinecraftServer.ServerResourcePackProperties packProperties;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void dynamicResourcePack$overridePackProperties(MinecraftServer.ServerResourcePackProperties packProperties, CallbackInfo ci) {
        this.packProperties = ConfigFileHandler.getResourcePackProperties(null);
    }
}
