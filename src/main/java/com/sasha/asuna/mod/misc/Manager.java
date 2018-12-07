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

package com.sasha.asuna.mod.misc;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.sasha.asuna.mod.AsunaMod;
import com.sasha.asuna.mod.command.commands.PathCommand;
import com.sasha.asuna.mod.feature.IAsunaFeature;
import com.sasha.asuna.mod.feature.IAsunaRenderableFeature;
import com.sasha.asuna.mod.feature.IAsunaTickableFeature;
import com.sasha.asuna.mod.feature.IAsunaTogglableFeature;
import com.sasha.asuna.mod.feature.annotation.FeatureInfo;
import com.sasha.asuna.mod.gui.hud.RenderableObject;
import com.sasha.eventsys.SimpleListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Sasha at 9:09 AM on 9/17/2018
 */
public class Manager {

    public static ImmutableSet<ClassPath.ClassInfo> findClasses(String pkg) throws IOException {
        return ClassPath.from(Manager.class.getClassLoader()).getTopLevelClasses(pkg);
    }

    public static class Feature implements SimpleListener {

        public static List<IAsunaFeature> featureRegistry = new ArrayList<>();

        public static void registerFeature(IAsunaFeature feature) {
            featureRegistry.add(feature);
            boolean event = false;
            for (Class<?> anInterface : feature.getClass().getInterfaces()) {
                if (anInterface == SimpleListener.class) {
                    event = true;
                    break;
                }
            }
            if (event) AsunaMod.EVENT_MANAGER.registerListener((SimpleListener) feature);
            feature.onLoad();
        }

        public static Iterator<IAsunaTogglableFeature> getTogglableFeatures() {
            return featureRegistry
                    .stream()
                    .filter(IAsunaTogglableFeature.class::isInstance)
                    .map(IAsunaTogglableFeature.class::cast)
                    .iterator();
        }

        /**
         * Tick all Tickable features.
         * If a feature is togglable, it will only tick if it's enabled.
         */
        public static void tickFeatures() {
            long l = System.nanoTime();
            PathCommand.tick();
            featureRegistry
                    .stream()
                    .filter(IAsunaTickableFeature.class::isInstance)
                    .map(IAsunaTickableFeature.class::cast)
                    .forEach(feature -> {
                        if (feature instanceof IAsunaTogglableFeature) {
                            if (((IAsunaTogglableFeature) feature).isEnabled()) {
                                feature.onTick();
                            }
                            return;
                        }
                        feature.onTick();
                    });
            AsunaMod.PERFORMANCE_ANAL.recordNewNormalTime(System.nanoTime() - l);
        }

        public static void renderFeatures() {
            long l = System.nanoTime();
            featureRegistry
                    .stream()
                    .filter(IAsunaRenderableFeature.class::isInstance)
                    .map(IAsunaRenderableFeature.class::cast)
                    .forEach(feature -> {
                        if (feature instanceof IAsunaTogglableFeature) {
                            if (((IAsunaTogglableFeature) feature).isEnabled()) {
                                feature.onRender();
                            }
                            return;
                        }
                        feature.onRender();
                    });
            AsunaMod.PERFORMANCE_ANAL.recordNewRenderTime(System.nanoTime() - l);
        }

        public static String getFeatureInfo(Class<? extends IAsunaFeature> featureClass) {
            if (featureClass.getAnnotation(FeatureInfo.class) == null) {
                return "No information provided for this feature!";
            }
            FeatureInfo info = featureClass.getAnnotation(FeatureInfo.class);
            return info.description();
        }

        public static <T extends IAsunaFeature> T findFeature(Class<T> featureClass) {
            // noinspection unchecked
            return (T) featureRegistry.stream()
                    .filter(f -> f.getClass().equals(featureClass))
                    .findFirst().orElse(null);
        }

        public static <T extends IAsunaFeature> boolean isFeatureEnabled(Class<T> featureClass) {
            IAsunaFeature feature = findFeature(featureClass);
            return feature instanceof IAsunaTogglableFeature && ((IAsunaTogglableFeature) feature).isEnabled();
        }
    }

    public static class Renderable implements SimpleListener {

        public static List<RenderableObject> renderableRegistry = new ArrayList<>();

        public static void register(RenderableObject robj) {
            renderableRegistry.add(robj);
            Data.registerSettingObject(robj);
        }
    }

    public static class Data {

        public static List<Object> settingRegistry = new ArrayList<>();

        public static void registerSettingObject(Object object) {
            settingRegistry.add(object);
        }

        public static void recoverSettings() {
            settingRegistry.forEach(e -> AsunaMod.SETTING_HANDLER.read(e));
        }

        public static void saveCurrentSettings() {
            settingRegistry.forEach(e -> AsunaMod.SETTING_HANDLER.save(e));
        }

    }
}

