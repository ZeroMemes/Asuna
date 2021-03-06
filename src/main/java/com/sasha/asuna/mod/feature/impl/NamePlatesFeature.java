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

import com.sasha.asuna.mod.feature.AbstractAsunaTogglableFeature;
import com.sasha.asuna.mod.feature.AsunaCategory;
import com.sasha.asuna.mod.feature.annotation.FeatureInfo;
import net.minecraft.entity.EntityLivingBase;

import static com.sasha.asuna.mod.misc.AsunaMath.dround;

@FeatureInfo(description = "Display an enchanced nametag above a player's head")
public class NamePlatesFeature extends AbstractAsunaTogglableFeature {
    public NamePlatesFeature() {
        super("NamePlates", AsunaCategory.RENDER);
    }

    public static String formatHealthTag(EntityLivingBase e) {
        float health = e.getHealth();
        float gapple = e.getAbsorptionAmount();
        float combined = health + gapple;
        if (combined > 20.0) {
            return "\247" + "b" + (dround(combined / 2, 3)) + " " + "\247" + "c<3";
        } else if (combined > 15.0) {
            return "\247" + "a" + (dround(combined / 2, 3)) + " " + "\247" + "c<3";
        } else if (combined > 10.0) {
            return "\247" + "e" + (dround(combined  / 2, 3)) + " " + "\247" + "c<3";
        } else if (combined > 5.0) {
            return "\247" + "c" + (dround(combined / 2, 3)) + " " + "\247" + "c<3";
        } else if (combined > 0.0) {
            return "\247" + "4" + (dround(combined / 2, 3)) + " " + "\247" + "c</3";
        } else {
            return "\247" + "4</3";
        }
    }
}
