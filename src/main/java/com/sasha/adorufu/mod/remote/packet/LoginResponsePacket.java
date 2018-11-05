/*
 * Copyright (c) Sasha Stevens 2018.
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

package com.sasha.adorufu.mod.remote.packet;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.gui.remotedatafilegui.GuiCloudLogin;
import com.sasha.adorufu.mod.remote.PacketProcessor;

import java.util.ArrayList;

/**
 * Created by Sasha at 11:41 AM on 8/25/2018
 */
public class LoginResponsePacket extends Packet.Incoming {

    private boolean loginSuccessful;
    private String adorufuSessionId; // this is NOT your mojang session id, this is generated by the Adorufu response server and is as a verification token.
    private String response;

    public LoginResponsePacket(PacketProcessor processor) {
        super(processor, -1);
    }

    @Override
    public void processIncomingPacket() {
        GuiCloudLogin.message = this.response;
        if (loginSuccessful) {
            AdorufuMod.REMOTE_DATA_MANAGER.loggedIn = true;
            AdorufuMod.REMOTE_DATA_MANAGER.adorufuSessionId = this.adorufuSessionId;
            //AdorufuMod.minecraft.displayGuiScreen(new GuiCloudControl(new GuiMainMenu()));
        } else {
            AdorufuMod.logErr(true, "Data Server returned the \"failed login\" statuscode!");
        }
    }

    public String getResponse() {
        return response;
    }

    public String getAdorufuSessionId() {
        return adorufuSessionId;
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    @Override
    public void setDataVars(ArrayList<String> pckData) {
        response = pckData.get(0);
        loginSuccessful = Boolean.parseBoolean(pckData.get(1));
        if (loginSuccessful) {
            adorufuSessionId = pckData.get(2);
        }
    }
}
