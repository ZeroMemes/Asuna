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

package com.sasha.asuna.mod.gui.clickgui.elements;

public interface IAsunaGuiElement {

    /**
     * Draws the element
     *
     * @param x The cursor's X position
     * @param y The cursor's Y position
     */
    void drawElement(int x, int y);

    boolean onMouseEngage(int x, int y, int b);

    boolean onMouseRelease(int x, int y, int b);

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    int getHeight();

}