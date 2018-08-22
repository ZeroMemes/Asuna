package com.sasha.adorufu.command.commands;

import com.sasha.adorufu.AdorufuMod;
import com.sasha.adorufu.module.ModuleManager;
import com.sasha.simplecmdsys.SimpleCommand;

import java.util.concurrent.atomic.AtomicBoolean;
import com.sasha.simplecmdsys.SimpleCommandInfo;
/**
 * Created by Sasha on 08/08/2018 at 9:26 PM
 **/
@SimpleCommandInfo(description = "Toggle a named module", syntax = {"<module>"})
public class ToggleCommand extends SimpleCommand {
    public ToggleCommand() {
        super("toggle");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null){
            AdorufuMod.logErr(false, "Arguments required! \"-toggle <module>\"");
            return;
        }
        AtomicBoolean found = new AtomicBoolean(false);
        ModuleManager.moduleRegistry.forEach(mod -> {
            if (mod.getModuleName().equalsIgnoreCase(this.getArguments()[0])){
                mod.toggle();
                AdorufuMod.logMsg(false, "Toggled " + mod.getModuleName());
                found.set(true);
                return;
            }
        });
        if (!found.get()) {
            AdorufuMod.logErr(false, "Couldn't find the specified module.");
        }
    }
}
