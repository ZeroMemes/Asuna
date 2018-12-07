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


package com.sasha.asuna.mod;

import com.sasha.asuna.mod.gui.clickgui.elements.AsunaGuiWindow;
import com.sasha.asuna.mod.misc.PlayerIdentity;
import com.sasha.asuna.mod.misc.YMLParser;
import com.sasha.asuna.mod.waypoint.Waypoint;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.sasha.asuna.mod.AsunaMod.logMsg;
import static com.sasha.asuna.mod.AsunaMod.logWarn;


public class AsunaDataManager {
    private final Lock threadLock = new ReentrantLock();
    private final Lock waypointLock = new ReentrantLock();
    private final Lock identityLock = new ReentrantLock();

    public LinkedHashMap<String, PlayerIdentity> identityCacheMap = new LinkedHashMap<>();

    public synchronized void savePlayerIdentity(PlayerIdentity id, boolean delete) throws IOException {
        logMsg(true, "Saving identity " + id.getStringUuid() + "...");
        identityLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File dir = new File("playeridentitycache");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File f = new File("playeridentitycache/" + id.getStringUuid() + ".mcid");
            if (f.exists() || delete) {
                f.delete();
                if (delete) return;
            }
            FileOutputStream fstream = new FileOutputStream(f);
            ObjectOutputStream stream = new ObjectOutputStream(fstream);
            stream.writeObject(id);
            stream.close();
            fstream.close();
        } finally {
            identityLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public PlayerIdentity getPlayerIdentity(String UUID) {
        if (identityCacheMap.containsKey(UUID)) {
            return identityCacheMap.get(UUID);
        }
        return new PlayerIdentity(UUID);
    }

    public synchronized void saveWaypoint(Waypoint waypoint, boolean delete) throws IOException {
        logMsg(true, "Saving waypoint " + waypoint.getName() + "...");
        waypointLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File dir = new File("waypoints");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File f = new File("waypoints/" + waypoint.getName() + ".wypt");
            if (f.exists() || delete) {
                f.delete();
                if (delete) return;
            }
            FileOutputStream fstream = new FileOutputStream(f);
            ObjectOutputStream stream = new ObjectOutputStream(fstream);
            stream.writeObject(waypoint);
            stream.close();
            fstream.close();
        } finally {
            waypointLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void loadPlayerIdentities() throws IOException {
        logMsg(true, "Loading id's...");
        identityLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File("playeridentitycache");
            if (!f.exists()) {
                logMsg(true, "No id's to load, skipping.");
                return; // nothing to load :p
            }
            if (!f.isDirectory()) {
                f.delete();
                return; // nothing to load :p
            }
            List<File> files = Arrays.asList(f.listFiles());
            files.stream().filter(file -> file.getName().endsWith(".mcid")).forEach(wyptFile -> {
                try {
                    FileInputStream inputStream = new FileInputStream(wyptFile);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    Object wayptObj = objectInputStream.readObject();
                    if (wayptObj instanceof PlayerIdentity) {
                        identityCacheMap.put(((PlayerIdentity) wayptObj).getStringUuid(), (PlayerIdentity) wayptObj);
                        objectInputStream.close();
                        inputStream.close();
                        return;
                    }
                    objectInputStream.close();
                    inputStream.close();
                    logWarn(true, wyptFile.getName() + " was not a id, skipping.");
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace(); //dont rly care
                    return;
                }
            });
        } finally {
            identityLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized ArrayList<Waypoint> loadWaypoints() throws IOException {
        logMsg(true, "Loading waypoints...");
        waypointLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File("waypoints");
            if (!f.exists()) {
                logMsg(true, "No waypoints to load, skipping.");
                return new ArrayList<>(); // nothing to load :p
            }
            if (!f.isDirectory()) {
                f.delete();
                return new ArrayList<>(); // nothing to load :p
            }
            ArrayList<Waypoint> theWaypoints = new ArrayList<>();
            List<File> files = Arrays.asList(f.listFiles());
            files.stream().filter(file -> file.getName().endsWith(".wypt")).forEach(wyptFile -> {
                try {
                    FileInputStream inputStream = new FileInputStream(wyptFile);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    Object wayptObj = objectInputStream.readObject();
                    if (wayptObj instanceof Waypoint) {
                        theWaypoints.add((Waypoint) wayptObj);
                        objectInputStream.close();
                        inputStream.close();
                        return;
                    }
                    objectInputStream.close();
                    inputStream.close();
                    logWarn(true, wyptFile.getName() + " was not a waypoint, skipping.");
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace(); //dont rly care
                    return;
                }
            });
            return theWaypoints;
        } finally {
            waypointLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void saveGuiElementPos(AsunaGuiWindow element) throws IOException {
        logMsg(true, "Saving \"" + element.getTitle() + "\"'s current GUI position...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File("AsunaLiteGui.yml");
            if (!f.exists()) {
                AsunaMod.logErr(true, "Data file doesn't exist smh");
                f.createNewFile();
            }
            YMLParser parser = new YMLParser(f);
            parser.set("Asuna.newclickgui." + element.getTitle().toLowerCase() + ".x", element.getX());
            parser.set("Asuna.newclickgui." + element.getTitle().toLowerCase() + ".y", element.getY());
            parser.save();
        } finally {
            threadLock.unlock();
        }
    }

    public synchronized int[] loadGuiElementPos(String elementTitle) throws IOException {
        logMsg(true, "Loading \"" + elementTitle + "\"'s saved GUI position...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File("AsunaLiteGui.yml");
            if (!f.exists()) {
                AsunaMod.logErr(true, "Data file doesn't exist smh");
                f.createNewFile();
                return new int[]{100, 100};
            }
            YMLParser parser = new YMLParser(f);
            int[] coords = new int[2];
            coords[0] = parser.getInt("Asuna.newclickgui." + elementTitle.toLowerCase() + ".x"
                    , 100);
            coords[1] = parser.getInt("Asuna.newclickgui." + elementTitle.toLowerCase() + ".y"
                    , 100);
            return coords;
        } finally {
            threadLock.unlock();
        }
    }
}
