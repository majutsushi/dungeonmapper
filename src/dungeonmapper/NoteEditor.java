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

import org.jdesktop.application.Action;

/**
 *
 * @author  zerker
 */
public class NoteEditor extends javax.swing.JDialog
{
    boolean accepted = false;

    /** Creates new form NewMapSizes */
    public NoteEditor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getRootPane().setDefaultButton(okButton);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(dungeonmapper.DungeonMapperApp.class).getContext().getResourceMap(NoteEditor.class);
        setTitle(resourceMap.getString("title")); // NOI18N
        setModal(true);
        setName("newMap"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(dungeonmapper.DungeonMapperApp.class).getContext().getActionMap(NoteEditor.class, this);
        cancelButton.setAction(actionMap.get("pushedCancel")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        getContentPane().add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 100, 30));

        okButton.setAction(actionMap.get("pushedOK")); // NOI18N
        okButton.setName("okButton"); // NOI18N
        getContentPane().add(okButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 90, 30));

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 540, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void pushedOK()
    {
        accepted = true;
        this.setVisible(false);
    }

    @Action
    public void pushedCancel()
    {
        this.setVisible(false);
    }

    public boolean getAccepted()
    {
        return accepted;
    }
    
    public String getNote() {
        return jTextField1.getText();
    }
    
    public void setNote(String note) {
        jTextField1.setText(note);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

}
