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

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class DungeonMapperApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new DungeonMapperView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of DungeonMapperApp
     */
    public static DungeonMapperApp getApplication() {
        return Application.getInstance(DungeonMapperApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(DungeonMapperApp.class, args);
    }
}
