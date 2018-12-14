/*
 * Copyright (c) Sasha Stevens (2017 - 2018)
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.sasha.asuna.mod.mixin.client;

import com.sasha.asuna.mod.AsunaMod;
import com.sasha.asuna.mod.events.client.ClientEntityCollideEvent;
import com.sasha.asuna.mod.events.client.ClientPushOutOfBlocksEvent;
import com.sasha.asuna.mod.events.playerclient.PlayerKnockbackEvent;
import com.sasha.asuna.mod.feature.impl.FreecamFeature;
import com.sasha.asuna.mod.misc.Manager;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = 999)
public abstract class MixinEntity {

    @Shadow
    public int entityId;

    @Shadow
    public World world;

    @Shadow
    public abstract String getName();

    /*@Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo info) {
        EntityMoveEvent event = new EntityMoveEvent((Entity) (Object) this, this.world, this.entityId, type, x, y, z);
        AsunaMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) info.cancel();
        x = event.getX();
        y = event.getY();
        z = event.getZ();
    }*/

    @Inject(method = "isInsideOfMaterial", at = @At("HEAD"), cancellable = true)
    public void isInsideOfMaterial(Material materialIn, CallbackInfoReturnable<Boolean> info) {
        if (Manager.Feature.isFeatureEnabled(FreecamFeature.class)) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    public void applyEntityCollision(Entity entityIn, CallbackInfo info) {
        ClientEntityCollideEvent event = new ClientEntityCollideEvent(entityIn);
        AsunaMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    protected void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        ClientPushOutOfBlocksEvent event = new ClientPushOutOfBlocksEvent(x, y, z);
        AsunaMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(
            method = "setVelocity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preSetVelocity(double x, double y, double z, CallbackInfo ci) {
        if ((Entity) (Object) this instanceof EntityPlayerSP) {
            PlayerKnockbackEvent event = new PlayerKnockbackEvent(x, y, z);
            AsunaMod.EVENT_MANAGER.invokeEvent(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

}
