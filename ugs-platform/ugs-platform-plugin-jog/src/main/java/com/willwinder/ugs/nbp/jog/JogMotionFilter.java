package com.willwinder.ugs.nbp.jog;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class JogMotionFilter implements ActionListener {
    private static final Logger logger = Logger.getLogger(JogMotionFilter.class.getName());

    double dx=0;
    double dy=0;
    double dz=0;
    private final Timer timer;

    public JogMotionFilter() {
        timer = new Timer(100, this);
        timer.start();
    }

    public void addMotion(double dx, double dy, double dz){
        this.dx+=dx;
        this.dy+=dy;
        this.dz+=dz;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        dx=dx*0.9;
        dy=dy*0.9;
        dy=dy*0.9;
        getMotionVector();
    }

    public void getMotionVector(){
        logger.info(String.format("Motion: %f.3, %f.3, %f.3", dx, dy, dz));
    }

    public void stop() {
       timer.stop();

    }
}
