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
import com.sasha.asuna.mod.events.client.ClientPacketRecieveEvent;
import com.sasha.asuna.mod.events.client.ClientPacketSendEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * Created by Sasha on 08/08/2018 at 8:08 PM
 **/
@Mixin(value = NetworkManager.class, priority = 999)
public abstract class MixinNetworkManager {


    @Shadow public INetHandler packetListener;

    @Inject(method = "dispatchPacket", at = @At("HEAD"), cancellable = true)
    public void dispatchPacket(final Packet<?> inPacket, @Nullable final GenericFutureListener<? extends Future<? super Void>>[] futureListeners, CallbackInfo info) {
        ClientPacketSendEvent event = new ClientPacketSendEvent(inPacket);
        AsunaMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }
/*
    @Redirect(
            method = "sendPacket(Lnet/minecraft/network/Packet;)V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/network/NetworkManager.dispatchPacket(Lnet/minecraft/network/Packet;[Lio/netty/util/concurrent/GenericFutureListener;)V"
            )
    )
    private void onDispatchPacket(NetworkManager networkManager, Packet<?> packet, GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
        ClientPacketSendEvent event = new ClientPacketSendEvent(packet);
        AsunaMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            return;
        }
        this.dispatchPacket(event.getSendPacket(), futureListeners);
    }
*/
    @Inject(
            method = "channelRead0",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet<?> packet, CallbackInfo info) {
        ClientPacketRecieveEvent event = new ClientPacketRecieveEvent(packet);
        AsunaMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }
}
