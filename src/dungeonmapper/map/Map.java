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

import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Map
{
    public static final int floorTypes = 10;
    public static final int normalWallTypes = 6;
    public static final int flipWallTypes = 4;
    public static final int glyphTypes = 21;

    private int width;
    private int height;
    private int floors;

    private int cursorX;
    private int cursorY;
    private int cursorZ;

    private Tile[][][] tiles;

    private boolean writingFloor = false;
    private boolean writingTopWall = false;
    private boolean writingBottomWall = false;
    private boolean writingLeftWall = false;
    private boolean writingRightWall = false;
    private boolean writingGlyph = false;

    private int activeFloor = 1;
    private int activeWall = 1;
    private int activeGlyph = 1;
    private String name = "Untitled";

    public Map(int width, int height, int floors)
    {
        this.width = width;
        this.height = height;
        this.floors = floors;

        cursorX = width/2;
        cursorY = height/2;
        cursorZ = 0;

        tiles = new Tile[width][height][floors];

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                for (int k = 0; k < floors; k++)
                    tiles[i][j][k] = new Tile();
    }

    public Tile getTile(int x, int y, int z)
    {
        return tiles[x][y][z];
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getFloors()
    {
        return floors;
    }

    public int getCursorX()
    {
        return cursorX;
    }

    public int getCursorY()
    {
        return cursorY;
    }

    public int getCursorZ()
    {
        return cursorZ;
    }

    public String getName()
    {
        return name;
    }

    private void checkWrite()
    {
        if (writingFloor)
            tiles[cursorX][cursorY][cursorZ].setFloor(getActiveFloor());

        // Left and top use the flipped wall where applicable
        if (writingTopWall && cursorY > 0)
        {
            if (getActiveWall() >= 20)
                tiles[cursorX][cursorY-1][cursorZ].setHorizWall(getActiveWall() + 20);
            else
                tiles[cursorX][cursorY-1][cursorZ].setHorizWall(getActiveWall());
        }
        if (writingLeftWall && cursorX > 0)
        {
            if (getActiveWall() >= 20)
                tiles[cursorX-1][cursorY][cursorZ].setVertWall(getActiveWall() + 20);
            else
                tiles[cursorX-1][cursorY][cursorZ].setVertWall(getActiveWall());
        }


        if (writingBottomWall)
            tiles[cursorX][cursorY][cursorZ].setHorizWall(getActiveWall());
        if (writingRightWall)
            tiles[cursorX][cursorY][cursorZ].setVertWall(getActiveWall());
        if (writingGlyph)
            tiles[cursorX][cursorY][cursorZ].setGlyph(getActiveGlyph());
    }

    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            // Cursor Movement:
            case KeyEvent.VK_RIGHT:
                if (cursorX < (width - 1))
                    cursorX++;
                break;
            case KeyEvent.VK_LEFT:
                if (cursorX > 0)
                    cursorX--;
                break;
            case KeyEvent.VK_UP:
                if (cursorY > 0)
                    cursorY--;
                break;
            case KeyEvent.VK_DOWN:
                if (cursorY < (height - 1))
                    cursorY++;
                break;
            case KeyEvent.VK_PAGE_UP:
                if (cursorZ > 0)
                    cursorZ--;
                break;
            case KeyEvent.VK_PAGE_DOWN:
                if (cursorZ < (floors - 1))
                    cursorZ++;
                break;

            // Writing Events
            case KeyEvent.VK_SPACE:
                writingFloor = true;
                break;
            case KeyEvent.VK_W:
                writingTopWall = true;
                break;
            case KeyEvent.VK_S:
                writingBottomWall = true;
                break;
            case KeyEvent.VK_A:
                writingLeftWall = true;
                break;
            case KeyEvent.VK_D:
                writingRightWall = true;
                break;
            case KeyEvent.VK_X:
                writingGlyph = true;
                break;

            // Cursor changes:
            case KeyEvent.VK_E:
                activeWall++;
                // Normal to flippable wall jump
                if (activeWall >= normalWallTypes && activeWall < 20)
                    activeWall = 20;

                // Wrap-around to the start
                if (activeWall >= 20 + flipWallTypes)
                    activeWall = 0;
                break;
            case KeyEvent.VK_Q:
                activeWall--;

                // Wrap to end
                if (activeWall < 0)
                    activeWall = 20 + flipWallTypes - 1;

                // Jump from flippable to normal walls
                if (activeWall < 20 && activeWall > normalWallTypes)
                    activeWall = normalWallTypes - 1;
                break;
            case KeyEvent.VK_Z:
                activeGlyph--;
                if (activeGlyph < 0)
                    activeGlyph = glyphTypes - 1;
                break;
            case KeyEvent.VK_C:
                activeGlyph = (activeGlyph + 1) % glyphTypes;
                break;
            case KeyEvent.VK_F:
                activeFloor = (activeFloor + 1) % floorTypes;
                break;
            case KeyEvent.VK_V:
                activeFloor--;
                if (activeFloor < 0)
                    activeFloor = floorTypes - 1;
                break;

        }

        checkWrite();
    }

    public void keyReleased(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            // Disable Writing Events:
            case KeyEvent.VK_SPACE:
                writingFloor = false;
                break;
            case KeyEvent.VK_W:
                writingTopWall = false;
                break;
            case KeyEvent.VK_S:
                writingBottomWall = false;
                break;
            case KeyEvent.VK_A:
                writingLeftWall = false;
                break;
            case KeyEvent.VK_D:
                writingRightWall = false;
                break;
            case KeyEvent.VK_X:
                writingGlyph = false;
                break;
        }
    }

    public int getActiveFloor() {
        return activeFloor;
    }

    public int getActiveWall() {
        return activeWall;
    }

    public int getActiveGlyph() {
        return activeGlyph;
    }

    /**
     * Simply removes the extension from the specified filename
     * @param filename filename to parse
     * @return filename without extension
     */
    private static String baseName(String filename)
    {
        int dotpos = filename.lastIndexOf(".");
        if (dotpos < 0)
            return filename;
        else
            return filename.substring(0, dotpos);
    }

    public void saveMap(File savefile) throws IOException
    {
        BufferedWriter mapWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(savefile))));

        mapWriter.write(String.format("%d %d %d", width, height, floors));
        mapWriter.newLine();

        Tile temp = null;

        for (int k = 0; k < floors; k++)
        {
            for (int j = 0; j < height; j++)
            {
                for (int i = 0; i< width; i++)
                {
                    temp = tiles[i][j][k];
                    mapWriter.write(String.format("%d %d %d %d ",
                            temp.getFloor(),
                            temp.getHorizWall(),
                            temp.getVertWall(),
                            temp.getGlyph()));
                }
                mapWriter.newLine();
            }
            mapWriter.newLine();
        }

        mapWriter.close();

        name = baseName(savefile.getName());
    }


    public static Map loadMap(File loadfile) throws FileNotFoundException, IOException
    {
        Scanner sc = new Scanner(new GZIPInputStream(new FileInputStream(loadfile)));

        int width, height, floors;

        width = sc.nextInt();
        height = sc.nextInt();
        floors = sc.nextInt();
        sc.nextLine();

        Map loadingMap = new Map(width, height, floors);

        Tile temp;
        for (int k = 0; k < floors; k++)
        {
            for (int j = 0; j < height; j++)
            {
                for (int i = 0; i< width; i++)
                {
                    temp = loadingMap.tiles[i][j][k];

                    temp.setFloor(sc.nextInt());
                    temp.setHorizWall(sc.nextInt());
                    temp.setVertWall(sc.nextInt());
                    temp.setGlyph(sc.nextInt());
                }
                sc.nextLine();
            }
            sc.nextLine();
        }

        sc.close();

        loadingMap.name = baseName(loadfile.getName());

        return loadingMap;
    }
}
