package com.willwinder.ugs.nbp.jog;

import javax.swing.*;
import java.awt.*;

public class JogJoystickDisplay extends JLabel {

    int x; int y;


    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int widthHalf = this.getWidth() / 2;
        int heightHalf = this.getHeight() / 2;
        g.drawOval(x + widthHalf,y+heightHalf, 10, 10);
    }


}
