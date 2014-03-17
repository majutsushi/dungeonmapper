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
package dungeonmapper;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import dungeonmapper.map.Map;
import dungeonmapper.map.Tile;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * View of a specified dungeon map
 * @author  zerker
 */
public class MapView extends javax.swing.JPanel
{

    // Icons for the map
    private final BufferedImage[] floors = new BufferedImage[Map.floorTypes];
    private final BufferedImage[] walls = new BufferedImage[60];
    private final BufferedImage[] horizwalls = new BufferedImage[60];
    private final BufferedImage[] glyphs = new BufferedImage[Map.glyphTypes];
    private BufferedImage cursor;
    private BufferedImage note;

    private int scale = 16;
    private Map map;

    // Current scrolling position on the map
    private int scrollX = -1;
    private int scrollY = -1;
    private boolean fullPaint = true;

    /** Creates new form MapView */
    public MapView(Map map)
    {
        this.map = map;
        load_and_scale_images();

        addKeyListener(new TAdapter());
        setFocusable(true);

        /*Dimension MapSize = new Dimension(map.getWidth() * scale,
                (map.getHeight() + 4) * scale);*/
    }

    private class TAdapter extends KeyAdapter
    {
        @Override
        public void keyReleased(KeyEvent e) {
            map.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            map.keyPressed(e);
            switch (e.getKeyCode())
            {
                // Floor changes. Not handled here, but tracked for re-painting:
                case KeyEvent.VK_PAGE_UP:
                case KeyEvent.VK_PAGE_DOWN:
                    break;
                // Plus (or equals without shift) will zoom in
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_EQUALS:
                case KeyEvent.VK_ADD:
                    if (scale < 64)
                    {
                        scale = scale * 2;
                        load_and_scale_images();
                    }
                    break;

                // Minus will zoom out
                case KeyEvent.VK_MINUS:
                case KeyEvent.VK_SUBTRACT:
                    if (scale > 8)
                    {
                        scale = scale / 2;
                        load_and_scale_images();
                    }
                    break;

                // All other keys do not have global drawing effects
                default:
                    fullPaint = false;
            }

            repaint();
        }
    }

    public Map getMap()
    {
        return map;
    }

    public void setMap(Map newMap)
    {
        map = newMap;
        load_and_scale_images();
        repaint();
    }



    private BufferedImage load_and_scale_image(Image original, int degrees)
    {
        // Assume square images
        int origsize = original.getWidth(null);
        float multiple = (float)scale/origsize;

        BufferedImage Working = new BufferedImage(origsize, origsize,
                BufferedImage.TYPE_4BYTE_ABGR);
        BufferedImage Output = new BufferedImage(scale, scale, BufferedImage.TYPE_4BYTE_ABGR);

        // Copy the original into the working:
        Graphics2D tempGraphics = Working.createGraphics();
        tempGraphics.drawImage(original, 0, 0, null);
        tempGraphics.dispose();

        // Do the transformation(s)
        AffineTransform transform = new AffineTransform();
        if (degrees != 0)
            transform.rotate(Math.toRadians(degrees), scale / 2, scale /2);
        if (origsize != scale)
            transform.scale(multiple, multiple);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);

        op.filter(Working, Output);
        return Output;
    }

    private void load_and_scale_images()
    {
        // Get our resource map
        org.jdesktop.application.ResourceMap resourceMap =
                org.jdesktop.application.Application.getInstance(dungeonmapper.DungeonMapperApp.class).
                    getContext().getResourceMap(MapView.class);

        // Set up the initial "blank" images:
        floors[0] = new BufferedImage(scale, scale, BufferedImage.TYPE_4BYTE_ABGR);
        walls[0] = new BufferedImage(scale, scale, BufferedImage.TYPE_4BYTE_ABGR);

        // Load the floors:
        for (int i = 0; i< floors.length; i++)
            floors[i] = load_and_scale_image(resourceMap.getImageIcon("Mapping.floor[" + i + "]").getImage(), 0);

        // Load Normal Walls
        for (int i = 0; i< Map.normalWallTypes; i++)
        {
            walls[i] = load_and_scale_image(resourceMap.getImageIcon("Mapping.wall[" + i + "]").getImage(), 0);
            horizwalls[i] = load_and_scale_image(resourceMap.getImageIcon("Mapping.wall[" + i + "]").getImage(), 90);
        }

        // Load flippable Walls:
        for (int i = 20; i< 20 + Map.flipWallTypes; i++)
        {
            walls[i] = load_and_scale_image(resourceMap.getImageIcon("Mapping.wall[" + i + "]").getImage(), 0);
            horizwalls[i] = load_and_scale_image(resourceMap.getImageIcon("Mapping.wall[" + i + "]").getImage(), 90);
            walls[i + 20] = load_and_scale_image(resourceMap.getImageIcon("Mapping.wall[" + i + "]").getImage(), 180);
            horizwalls[i + 20] = load_and_scale_image(resourceMap.getImageIcon("Mapping.wall[" + i + "]").getImage(), 270);
        }

        // Load Glyphs
        for (int i = 0; i< glyphs.length; i++)
            glyphs[i] = load_and_scale_image(resourceMap.getImageIcon("Mapping.glyph[" + i + "]").getImage(), 0);


        cursor = load_and_scale_image(resourceMap.getImageIcon("Mapping.cursor").getImage(), 0);
        note = load_and_scale_image(resourceMap.getImageIcon("Mapping.note").getImage(), 0);
    }

    private void drawStatus(Graphics2D g2d, int viewWidth)
    {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, viewWidth, 5 * scale);

        int row = 0;
        int i = 0;

        // Info:
        g2d.setColor(Color.BLACK);

        Font statusFont = new Font("Dialog", Font.PLAIN, scale * 3 / 4);
        g2d.setFont(statusFont);
        g2d.drawString(String.format("%s : Floor %d / %d [X: %d / %d, Y: %d / %d]",
                map.getName(), map.getCursorZ() + 1, map.getFloors(),
                map.getCursorX() + 1, map.getWidth(),
                map.getCursorY() + 1, map.getHeight()),
                0, scale * 3 / 4);

        row++;

        // Floors:
        for (i = 0; i< Map.floorTypes; i++)
        {
            g2d.drawImage(floors[i], i * scale, row * scale, null);
            if (map.getActiveFloor() == i)
                g2d.drawImage(cursor, i * scale, row * scale, null);
        }
        row++;

        // Walls:
        int j = 0;
        i = 0;
        while (i < 20 + Map.flipWallTypes)
        {
            g2d.drawImage(walls[i], j * scale, row * scale, null);
            if (map.getActiveWall() == i)
                g2d.drawImage(cursor, j * scale, row * scale, null);

            i++;
            j++;

            // Jump to flippable walls after normal walls
            if (i >= Map.normalWallTypes && i<20)
                i = 20;
        }
        row++;

        // Glyphs:
        for (i = 0; i< Map.glyphTypes; i++)
        {
            g2d.drawImage(glyphs[i], i * scale, row * scale, null);
            if (map.getActiveGlyph() == i)
                g2d.drawImage(cursor, i * scale, row * scale, null);
        }
        row++;
        Tile current = map.getTile(map.getCursorX(), map.getCursorY(), map.getCursorZ());
        g2d.drawString(String.format("Note : " + current.getNote()), 0, (5 * scale) - 2);
    }

    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        Tile current;
        int floor = map.getCursorZ();

        Rectangle viewArea = this.getBounds();

        // Calculate the viewable area (height is offset for status box)
        int viewWidth = (int)viewArea.getWidth();
        int viewHeight = (int)viewArea.getHeight();

        int drawWidth = (viewWidth / scale);
        int drawHeight = (viewHeight / scale) - 5;
        if (drawWidth > map.getWidth())
            drawWidth = map.getWidth();
        if (drawHeight > map.getHeight())
            drawHeight = map.getHeight();

        // Determine our scrolling position
        int newScrollX = scrollX;
        int newScrollY = scrollY;

        // Recenter when cursor is completely off-screen or we don't know
        if (newScrollX < 0 || newScrollY < 0 ||
            map.getCursorX() < newScrollX ||
            map.getCursorY() < newScrollY ||
            map.getCursorX() >= newScrollX + drawWidth ||
            map.getCursorY() >= newScrollY + drawHeight)
        {
            newScrollX = map.getCursorX() - (drawWidth / 2);
            newScrollY = map.getCursorY() - (drawHeight /2);
        }


        // Move the view window when the cursor is within three blocks
        if (map.getCursorX() < (newScrollX + 3))
            newScrollX = map.getCursorX() - 3;
        if (map.getCursorY() < (newScrollY + 3))
            newScrollY = map.getCursorY() - 3;
        if (map.getCursorX() >= newScrollX + drawWidth - 3)
            newScrollX = map.getCursorX() + 3 - drawWidth;
        if (map.getCursorY() >= newScrollY + drawHeight - 3)
            newScrollY = map.getCursorY() + 3 - drawHeight;

        // Limit the view by edges:
        newScrollX = Math.min(newScrollX, map.getWidth() - drawWidth);
        newScrollY = Math.min(newScrollY, map.getHeight() - drawHeight);
        newScrollX = Math.max(newScrollX, 0);
        newScrollY = Math.max(newScrollY, 0);

        // Define the outer draw edges:
        int leftX, rightX, topY, bottomY;

        //Full re-draw when we scroll:
        if (newScrollX != scrollX || newScrollY != scrollY)
            fullPaint = true;

        // Store persistant info:
        scrollX = newScrollX;
        scrollY = newScrollY;

        //Choose our drawing boundary based on the fullPaint flag:
        if (fullPaint)
        {
            leftX = scrollX;
            rightX = scrollX + drawWidth;
            topY = scrollY;
            bottomY = scrollY + drawHeight;
        }
        else
        {
            leftX = map.getCursorX() - 1;
            rightX = map.getCursorX() + 2;
            topY = map.getCursorY() - 1;
            bottomY = map.getCursorY() + 2;
        }

        // Bounds check on the outer draw edges:
        leftX = Math.max(leftX, scrollX);
        rightX = Math.min(rightX, map.getWidth());
        topY = Math.max(topY, scrollY);
        bottomY = Math.min(bottomY, map.getHeight());

        // Also need to offset wall drawing by one:
        int wallX = Math.max(leftX - 1, scrollX);
        int wallY = Math.max(topY - 1, scrollY);

        // Draw the Status Box:
        drawStatus(g2d, viewWidth);

        // If doing a full paint, clear outside the viewable area:
        if (fullPaint)
        {
            g2d.setColor(Color.LIGHT_GRAY);
            int drawWidthPixels = drawWidth * scale;
            int drawHeightPixels = drawHeight * scale;

            if (drawWidthPixels < viewWidth)
                g2d.fillRect(drawWidthPixels, 0, viewWidth - drawWidthPixels, viewHeight);
            if (drawHeightPixels < viewHeight)
                g2d.fillRect(0, drawHeightPixels, viewWidth, viewHeight - drawHeightPixels);
        }


        // Draw the floor & glyphs
        for (int i=leftX; i<rightX; i++)
            for (int j=topY; j<bottomY; j++)
            {
                current = map.getTile(i, j, floor);
                g2d.drawImage(floors[current.getFloor()],
                        (i-scrollX) * scale, (j-scrollY+5) * scale, null);
                g2d.drawImage(glyphs[current.getGlyph()],
                        (i-scrollX) * scale, (j-scrollY+5) * scale, null);
            }

        // Then draw the walls overtop:
        for (int i=leftX; i<rightX; i++)
            for (int j=wallY; j<bottomY; j++)
            {
                current = map.getTile(i, j, floor);
                g2d.drawImage(horizwalls[current.getHorizWall()],
                        (i-scrollX) * scale, ((j-scrollY+5) * scale) + scale/2, null);
                if (! current.getNote().replace("\n", "").replace("\r", "").trim().equals("")) {
                    g2d.drawImage(note, (i-scrollX) * scale, (j-scrollY+5) * scale, null);
                }

            }

        for (int i=wallX; i<rightX; i++)
            for (int j=topY; j<bottomY; j++)
            {
                current = map.getTile(i, j, floor);
                g2d.drawImage(walls[current.getVertWall()],
                        ((i-scrollX) * scale) + scale/2, (j-scrollY+5) * scale, null);
            }

        // Finally draw the cursor:
        g2d.drawImage(cursor, (map.getCursorX() - scrollX) * scale,
                (map.getCursorY() - scrollY + 5) * scale, null);
        
        

        // Default to "true" for next time:
        fullPaint = true;
        Toolkit.getDefaultToolkit().sync();
    }

}
