package com.willwinder.ugs.nbp.jog;


import javax.swing.*;
import javax.vecmath.Vector2d;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

// emulate a joystick-like behaviour for the mouse cursor
public class MousePositionFilter implements ActionListener {
    private static final Logger logger = Logger.getLogger(MousePositionFilter.class.getName());


    private double maxLen;
    private Vector2d current;
    private ActionListener actionListener;

    private final Timer timer;


    public MousePositionFilter(ActionListener onPositionChange) {
        current = new Vector2d(0, 0);
        actionListener = onPositionChange;
        timer = new Timer(100, this);
    }

    public void setMaxLen(int maxX, int maxY) {
        maxLen = Math.min(maxX, maxY) * 0.8; // a 20% hold-off
    }


    public void addMotion(int x, int y) {
        Vector2d d = new Vector2d(x, y);

        if (d.length() == 0) {
            return;  // no change
        }
        double weight;
        if (current.length() <= .001) {
            //timer.stop();
            weight = 0.2;
        } else {
            timer.start();
            weight = Math.exp((current.length() / maxLen) * 0.1) * 0.05;
            logger.info(String.format("MF: %f - %d,%d -> %s", weight, x, y, current.toString()));

        }

        d.scale(weight);
        current.add(d);

        actionListener.actionPerformed(null);

        // hardlimit
        if (current.length() > maxLen) {
            current.scale(maxLen / current.length());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // decay
        double decay = Math.max(1, ((maxLen - current.length()) / maxLen));
        current.scale(1 - decay * 0.1);
        logger.info(String.format("MFD: %s -> %s", decay, current));
        actionListener.actionPerformed(null);
        if (current.length() <= 0.02) {
            timer.stop();
            current.set(0, 0);
            logger.info("MF0");
        }
    }


    public int getPosX() {
        return (int) current.getX();
    }

    public int getPosY() {
        return (int) current.getY();
    }

    public void stop() {
        timer.stop();
        current.set(0, 0);
        actionListener.actionPerformed(null);
    }

    public void getMotionVector() {
//        logger.info(String.format("Motion: %f.3, %f.3, %f.3", dx, dy, dz));
    }

}
