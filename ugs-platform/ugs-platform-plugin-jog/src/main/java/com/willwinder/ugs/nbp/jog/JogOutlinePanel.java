/*
    Copyright 2018 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.ugs.nbp.jog;

import com.willwinder.ugs.nbm.visualizer.CoordinatesSubMenu;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.Position;
import com.willwinder.universalgcodesender.model.UnitUtils;
import com.willwinder.universalgcodesender.services.JogService;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * A panel for jogging around the workarea
 *
 * @author dweigl
 */
public class JogOutlinePanel extends JPanel {
    private final JogService jogService;
    private final BackendAPI backend;
    private javax.swing.JButton jogOutlineButton;
    private javax.swing.JLabel workAreaLabel;
    private javax.swing.JLabel bottomLeftCoordLabel;
    private javax.swing.JLabel topRightCoordLabel;
    private Position btmLeft;
    private Position topRight;
    private UnitUtils.Units units;

    public JogOutlinePanel(JogService jogService, BackendAPI backend) {
        this.jogService = jogService;
        this.backend = backend;
        createComponents();
    }

    public void updateBounds(Position btmLeft, Position topRight) {
        this.btmLeft = btmLeft;
        this.topRight = topRight;

        updateLabels();
    }

    private void updateLabels() {
        bottomLeftCoordLabel.setText(fmtCornerPos(btmLeft));
        topRightCoordLabel.setText(fmtCornerPos(topRight));
        double width = topRight.getX() - btmLeft.getX();
        double height = topRight.getY() - btmLeft.getY();
        workAreaLabel.setText("Area: " + String.format("%.2f x %.2f [%s]", width, height, units.abbreviation));

        bottomLeftCoordLabel.setComponentPopupMenu(new CoordinatesSubMenu(backend, btmLeft).getPopupMenu());
        topRightCoordLabel.setComponentPopupMenu(new CoordinatesSubMenu(backend, topRight).getPopupMenu());

    }

    private String fmtCornerPos(Position pos) {
        return String.format("X%.3f Y%.3f", pos.getX(), pos.getY());
    }

    public void setUnit(UnitUtils.Units units) {
        this.units = units;
    }


    private static boolean isDarkLaF() {
        return UIManager.getBoolean("nb.dark.theme");
    }

    private void createComponents() {
        jogOutlineButton = new javax.swing.JButton();
        workAreaLabel = new javax.swing.JLabel();
        bottomLeftCoordLabel = new javax.swing.JLabel();
        topRightCoordLabel = new javax.swing.JLabel();


        workAreaLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        workAreaLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jogOutlineButton, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                                        .addComponent(topRightCoordLabel)
                                        .addComponent(bottomLeftCoordLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(workAreaLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(247, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(topRightCoordLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(workAreaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bottomLeftCoordLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jogOutlineButton)
                                .addContainerGap(151, Short.MAX_VALUE))
        );

        jogOutlineButton.setAction(new jogOutlineAction());

        // text
        jogOutlineButton.setText(Localization.getString("platform.plugin.jog.outline.jogWorkOutlineButton"));
        workAreaLabel.setText("");

    }


    private class jogOutlineAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            jogService.jogOutline(btmLeft, topRight);
        }
    }
}
