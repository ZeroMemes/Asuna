package com.sasha.adorufu.module.modules;

import com.sasha.adorufu.AdorufuMod;
import com.sasha.adorufu.module.AdorufuCategory;
import com.sasha.adorufu.module.AdorufuModule;
import com.sasha.adorufu.module.ModuleInfo;
import com.sasha.adorufu.waypoint.Waypoint;
import com.sasha.adorufu.waypoint.WaypointManager;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

@ModuleInfo(description = "Display tracers to enabled waypoints.")
public class ModuleWaypoints extends AdorufuModule {
    public ModuleWaypoints() {
        super("Waypoints", AdorufuCategory.RENDER, false);
    }

    public static int i = 0;

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        i = 0;
    }

    @Override
    public void onTick() {

    }
    @Override
    public void onRender() {
        if (this.isEnabled()) {
            i = 0;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glLineWidth(1);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            GL11.glPushMatrix();
            GL11.glTranslated(-AdorufuMod.minecraft.getRenderManager().renderPosX, -AdorufuMod.minecraft.getRenderManager().renderPosY, -AdorufuMod.minecraft.getRenderManager().renderPosZ);

            // set start position
            Vec3d start = getClientLookVec().addVector(0, AdorufuMod.minecraft.player.getEyeHeight(), 0).addVector(AdorufuMod.minecraft.getRenderManager().renderPosX, AdorufuMod.minecraft.getRenderManager().renderPosY, AdorufuMod.minecraft.getRenderManager().renderPosZ);

            GL11.glBegin(GL11.GL_LINES);
            for (Waypoint waypoint : AdorufuMod.WAYPOINT_MANAGER.getWaypoints()) {
                if (!waypoint.isDoesRender()) {
                    continue;
                }
                i++;
                Vec3d end = (new Vec3d(waypoint.getCoords()[0], waypoint.getCoords()[1], waypoint.getCoords()[2]).scale(1));

                // set color
                GL11.glColor3f(0.0f, 1.5f, 1.5f);

                // draw line
                GL11.glVertex3d(start.x, start.y, start.z);
                GL11.glVertex3d(end.x, end.y, end.z);
            }
            GL11.glEnd();

            GL11.glPopMatrix();

            // GL resets
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }
    }
    private static Vec3d getClientLookVec() {
        double f = Math.cos(-AdorufuMod.minecraft.player.rotationYaw * 0.017453292F - (float)Math.PI);
        double f1 = Math.sin(-AdorufuMod.minecraft.player.rotationYaw * 0.017453292F - (float)Math.PI);
        double f2 = -Math.cos(-AdorufuMod.minecraft.player.rotationPitch * 0.017453292F);
        double f3 = Math.sin(-AdorufuMod.minecraft.player.rotationPitch * 0.017453292F);
        return new Vec3d(f1 * f2, f3, f * f2);
    }
}
