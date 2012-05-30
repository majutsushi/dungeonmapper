/*
 * Copyright 2010 Ryan Armstrong
 *
 * This file is part of Dungeon Mapper
 *
 * Dungeon Mapper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Mapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dungeon Mapper; If not, see <http://www.gnu.org/licenses/>.
 */
package dungeonmapper.map;

public class Tile
{
    private int floor;
    private int vertWall;
    private int horizWall;
    private int glyph;

    public Tile()
    {
        floor = 0;
        vertWall = 0;
        horizWall = 0;
        glyph = 0;
    }


    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getVertWall() {
        return vertWall;
    }

    public void setVertWall(int vertWall) {
        this.vertWall = vertWall;
    }

    public int getHorizWall() {
        return horizWall;
    }

    public void setHorizWall(int horizWall) {
        this.horizWall = horizWall;
    }

    public int getGlyph() {
        return glyph;
    }

    public void setGlyph(int glyph) {
        this.glyph = glyph;
    }

}
