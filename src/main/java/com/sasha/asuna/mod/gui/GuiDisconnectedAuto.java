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

package com.sasha.asuna.mod.gui;

import com.sasha.asuna.mod.AsunaMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.IOException;

public class GuiDisconnectedAuto extends GuiDisconnected {

    private long milliseconds;
    private ServerData serverData = null;

    private Thread reconnectFuture;

    public GuiDisconnectedAuto(GuiScreen screen, String reasonLocalizationKey, ITextComponent chatComp, long ms) {
        super(screen, reasonLocalizationKey, chatComp);
        this.milliseconds = ms;
    }

    public GuiDisconnectedAuto(GuiScreen screen, String reasonLocalizationKey, ITextComponent chatComp, long ms, ServerData serverData) {
        super(screen, reasonLocalizationKey, chatComp);
        this.milliseconds = ms;
        this.serverData = serverData;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        super.initGui();
        GuiButton guiButton = new GuiButton(999,
                this.width / 2 - 100,
                Math.min(this.height / 2 + this.textHeight / 2 + this.fontRenderer.FONT_HEIGHT,
                        this.height - 30) + 50, "Reconnect");
        if (serverData == null) {
            guiButton.enabled = false;
        }
        this.buttonList.add(guiButton);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 999) {
            if (serverData == null) {
                return;
            }
            net.minecraftforge.fml.client.FMLClientHandler.instance().connectToServer(this.parentScreen, serverData);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        milliseconds -= 30;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(AsunaMod.minecraft.fontRenderer, (float) milliseconds / 1000 + "s", this.width / 2,
                Math.min(this.height / 2 + this.textHeight / 2 + this.fontRenderer.FONT_HEIGHT,
                        this.height - 30) + 70, 0xffffff);
        if (milliseconds <= 0L && serverData != null) FMLClientHandler.instance().connectToServer(this.parentScreen, serverData);
    }
}
