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

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filter for dungeon files
 * @author  zerker
 */
public class DungeonFileFilter extends FileFilter
{

    public static String getExtension(File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1)
            ext = s.substring(i+1).toLowerCase();

        return ext;
    }


    @Override
    public boolean accept(File f)
    {
        if (f.isDirectory())
            return true;
        else
        {
            String extension = getExtension(f);
            return (extension != null && extension.equals("dungeon"));
        }
    }

    @Override
    public String getDescription()
    {
        return "Dungeon file (.dungeon)";
    }


}
