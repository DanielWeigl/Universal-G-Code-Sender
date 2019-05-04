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

import com.willwinder.ugs.nbm.visualizer.renderables.GcodeModel;
import com.willwinder.ugs.nbm.visualizer.shared.GcodeRenderer;
import com.willwinder.ugs.nbm.visualizer.shared.Renderable;
import com.willwinder.ugs.nbp.jog.actions.UseSeparateStepSizeAction;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
import com.willwinder.universalgcodesender.listeners.ControllerListener;
import com.willwinder.universalgcodesender.listeners.ControllerStatus;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.Alarm;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.Position;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.services.JogService;
import com.willwinder.universalgcodesender.types.GcodeCommand;
import com.willwinder.universalgcodesender.utils.SwingHelpers;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * The jog control panel in NetBeans
 *
 * @author Joacim Breiler
 */
@TopComponent.Description(
        preferredID = "JogOutlineTopComponent"
)
@TopComponent.Registration(
        mode = "middle_left",
        openAtStartup = true)
@ActionID(
        category = JogOutlineTopComponent.CATEGORY,
        id = JogOutlineTopComponent.ACTION_ID)
@ActionReference(
        path = JogOutlineTopComponent.WINDOW_PATH)
@TopComponent.OpenActionRegistration(
        displayName = "Jog Outline",
        preferredID = "JogOutlineTopComponent"
)
public final class JogOutlineTopComponent extends TopComponent implements UGSEventListener, ControllerListener {

    public static final String WINDOW_PATH = LocalizingService.MENU_WINDOW_PLUGIN;
    public static final String CATEGORY = LocalizingService.CATEGORY_WINDOW;
    public static final String ACTION_ID = "com.willwinder.ugs.nbp.jog.JogOutlineTopComponent";



    private final BackendAPI backend;
    private final JogOutlinePanel outlinePanel;
    private final JogService jogService;
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> continuousJogSchedule;

    public JogOutlineTopComponent() {
        backend = CentralLookup.getDefault().lookup(BackendAPI.class);
        jogService = CentralLookup.getDefault().lookup(JogService.class);
        UseSeparateStepSizeAction action = Lookup.getDefault().lookup(UseSeparateStepSizeAction.class);

        outlinePanel = new JogOutlinePanel(new JogService(backend), backend);
        outlinePanel.setEnabled(jogService.canJog());
        outlinePanel.setUnit(jogService.getUnits());

        backend.addUGSEventListener(this);
        backend.addControllerListener(this);

        setLayout(new BorderLayout());
        setName(LocalizingService.JogOutlineTitle);
        setToolTipText(LocalizingService.JogOutlineTooltip);

        setPreferredSize(new Dimension(250, 270));

        add(outlinePanel, BorderLayout.CENTER);

        if (action != null) {
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add(action);
            SwingHelpers.traverse(this, (comp) -> comp.setComponentPopupMenu(popupMenu));
        }
    }

    @Override
    protected void componentShowing() {
        super.componentShowing();
        recalcOutline();
    }

    @Override
    protected void componentClosed() {
        super.componentClosed();
        backend.removeUGSEventListener(this);
        backend.removeControllerListener(this);
    }

    @Override
    public void UGSEvent(UGSEvent event) {
        boolean canJog = jogService.canJog();
        if (canJog != outlinePanel.isEnabled()) {
            outlinePanel.setEnabled(canJog);
        }

        if (event.isSettingChangeEvent()) {
            outlinePanel.setUnit(backend.getSettings().getPreferredUnits());
        }

        if (event.isFileChangeEvent()){
            recalcOutline();
        }
    }

    private void recalcOutline() {
        GcodeModel gcodeModel=null;
        GcodeRenderer gcodeRenderer = Lookup.getDefault().lookup(GcodeRenderer.class);
        for (Renderable renderable : gcodeRenderer.getRenderables()) {
            if (renderable.getClass() == GcodeModel.class) {
                gcodeModel = (GcodeModel) renderable;
            }
        }
        if (gcodeModel != null && gcodeModel.getWorkingCuboid() != null) {
            outlinePanel.setEnabled(true);

            Position btmLeft =  gcodeModel.getWorkingCuboid().getMinPos(); //new Position(gcodeModel.getMin().x, gcodeModel.getMin().y, gcodeModel.getMin().z, UnitUtils.Units.MM);
            Position topRight =  gcodeModel.getWorkingCuboid().getMaxPos(); //new Position(gcodeModel.getMin().x, gcodeModel.getMin().y, gcodeModel.getMin().z, UnitUtils.Units.MM);
            //Position topRight = new Position(gcodeModel.getMax().x, gcodeModel.getMax().y, gcodeModel.getMax().z, UnitUtils.Units.MM);
            outlinePanel.updateBounds(btmLeft,topRight);
        } else {
            outlinePanel.setEnabled(false);
        }
    }

    @Override
    public void controlStateChange(UGSEvent.ControlState state) {
    }

    @Override
    public void fileStreamComplete(String filename, boolean success) {

    }

    @Override
    public void receivedAlarm(Alarm alarm) {

    }

    @Override
    public void commandSkipped(GcodeCommand command) {

    }

    @Override
    public void commandSent(GcodeCommand command) {

    }

    @Override
    public void commandComplete(GcodeCommand command) {
        // If there is a command with an error, assume we are jogging and cancel any event
        if (command.isError() && continuousJogSchedule != null) {
            continuousJogSchedule.cancel(true);
            jogService.cancelJog();
        }
    }

    @Override
    public void commandComment(String comment) {

    }

    @Override
    public void probeCoordinates(Position p) {

    }

    @Override
    public void statusStringListener(ControllerStatus status) {

    }

}
