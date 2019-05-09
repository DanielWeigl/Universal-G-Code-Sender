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

import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.services.JogService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;

/**
 * A panel for jogging around the workarea
 *
 * @author dweigl
 */
public class MouseJogPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener {
    private static final Logger logger = Logger.getLogger(MouseJogPanel.class.getName());

    private final JogService jogService;
    private final BackendAPI backend;
    private JogJoystickDisplay captureMouse;
    private boolean ignoreRobotMove = false;
    private JogMotionFilter motionFilter = null;
    private MousePositionFilter mousePositionFilter;

    public MouseJogPanel(JogService jogService, BackendAPI backend) {
        this.jogService = jogService;
        this.backend = backend;
        createComponents();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("E:" + e.toString());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        logger.info("MD:" + e.toString());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (e.isControlDown()) {
            int widthHalf = captureMouse.getWidth() / 2;
            int heightHalf = captureMouse.getHeight() / 2;

            // logger.info("CMM:" + e.toString());
            if (!ignoreRobotMove) {

                mousePositionFilter.addMotion(e.getX() - widthHalf, e.getY() - heightHalf);

                //motionFilter.addMotion(e.getX(), e.getY() - heightHalf, 0);

                // fixate mouse in the center of the label
                try {
                    Robot robot = new Robot();
                    ignoreRobotMove = true;
                    robot.mouseMove(captureMouse.getLocationOnScreen().x + widthHalf, captureMouse.getLocationOnScreen().y + heightHalf);
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            } else {
                ignoreRobotMove = false;
            }
        } else {
            if (motionFilter != null) {
                motionFilter.stop();
                motionFilter = null;
            }
            mousePositionFilter.stop();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (motionFilter != null) {
            motionFilter.stop();
            motionFilter = null;
        }
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);
        logger.info(String.format("KE %s", e));
    }

    private static boolean isDarkLaF() {
        return UIManager.getBoolean("nb.dark.theme");
    }

    private void createComponents() {
        captureMouse = new JogJoystickDisplay();


        captureMouse.setHorizontalAlignment(SwingConstants.CENTER);
        captureMouse.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));


        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(captureMouse, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(247, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(captureMouse, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(151, Short.MAX_VALUE))
        );

        captureMouse.registerKeyboardAction(this, KeyStroke.getKeyStroke("CTRL"), 1);

        // text
        captureMouse.setText("Press CTRL + mouse to jog");

        captureMouse.addMouseMotionListener(this);
        captureMouse.addMouseListener(this);


        this.registerKeyboardAction(e -> {
            logger.info("KSR: " + e.toString());
            if (motionFilter != null) {
                motionFilter.stop();
                motionFilter = null;
            }
            mousePositionFilter.stop();

        }, KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0, true), WHEN_IN_FOCUSED_WINDOW);

        mousePositionFilter = new MousePositionFilter(e -> captureMouse.setXY(mousePositionFilter.getPosX(), mousePositionFilter.getPosY()));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                mousePositionFilter.setMaxLen(captureMouse.getWidth() / 2, captureMouse.getHeight() / 2);
            }
        });
    }



}
