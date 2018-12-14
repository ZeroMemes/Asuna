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

package com.sasha.asuna.mod.feature.impl;

import com.sasha.asuna.mod.AsunaMod;
import com.sasha.asuna.mod.events.client.ClientPacketSendEvent;
import com.sasha.asuna.mod.events.client.ClientPushOutOfBlocksEvent;
import com.sasha.asuna.mod.events.client.EntityMoveEvent;
import com.sasha.asuna.mod.feature.AbstractAsunaTogglableFeature;
import com.sasha.asuna.mod.feature.AsunaCategory;
import com.sasha.asuna.mod.feature.IAsunaTickableFeature;
import com.sasha.asuna.mod.feature.annotation.FeatureInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.world.GameType;

import static com.sasha.asuna.mod.AsunaMod.minecraft;

/**
 * Created by Sasha on 12/08/2018 at 9:12 AM
 **/
@FeatureInfo(description = "Client-sided spectator mode.")
public class FreecamFeature extends AbstractAsunaTogglableFeature implements SimpleListener, IAsunaTickableFeature {

    public static double oldX, oldY, oldZ, oldYaw, oldPitch;
    public static GameType oldGameType;

    public FreecamFeature() {
        super("Freecam", AsunaCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        oldX = AsunaMod.minecraft.player.posX;
        oldY = AsunaMod.minecraft.player.posY;
        oldZ = AsunaMod.minecraft.player.posZ;
        oldYaw = AsunaMod.minecraft.player.rotationYaw;
        oldPitch = AsunaMod.minecraft.player.rotationPitch;
        oldGameType = AsunaMod.minecraft.playerController.currentGameType;
        AsunaMod.minecraft.playerController.setGameType(GameType.SPECTATOR);
        AsunaMod.minecraft.player.setGameType(GameType.SPECTATOR);
        AsunaMod.minecraft.player.noClip = true;
    }

    @Override
    public void onDisable() {
        AsunaMod.minecraft.player.motionX = 0.0;
        AsunaMod.minecraft.player.motionY = 0.0;
        AsunaMod.minecraft.player.motionZ = 0.0;
        AsunaMod.minecraft.player.setLocationAndAngles(oldX, oldY, oldZ, (float) oldYaw, (float) oldPitch);
        AsunaMod.minecraft.playerController.setGameType(oldGameType);
        AsunaMod.minecraft.player.setGameType(oldGameType);
        AsunaMod.minecraft.player.noClip = false;
    }

    @Override
    public void onTick() {
        if (minecraft.world == null) this.toggleState();
        AsunaMod.minecraft.player.noClip = true;
    }

    @SimpleEventHandler
    public void onMove(EntityMoveEvent e) {
        if (this.isEnabled()) {
            e.getEntity().setEntityBoundingBox(e.getEntity().getEntityBoundingBox()
                    .offset(e.getX(), e.getY(), e.getZ()));
            e.getEntity().resetPositionToBB();
        }
    }
    @SimpleEventHandler
    public void onPacketTx(ClientPacketSendEvent e) {
        if (this.isEnabled()) {
            if (minecraft.world == null) this.toggleState();
            if (e.getSendPacket() instanceof CPacketPlayer || e.getSendPacket() instanceof CPacketInput) {
                e.setCancelled(true);
            }
        }
    }

    @SimpleEventHandler
    public void onPushoutofblocks(ClientPushOutOfBlocksEvent e) {
        if (minecraft.world == null && this.isEnabled()) this.toggleState();
        if (this.isEnabled()) e.setCancelled(true);
    }
}
