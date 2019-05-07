package com.willwinder.universalgcodesender.model.test1;

import com.willwinder.universalgcodesender.model.Axis;
import com.willwinder.universalgcodesender.model.UnitUtils;
import org.junit.Test;

public class PartialPositionTest1 {

    @Test
    public void test() {
        PositionXY pos2 = new PositionXY(1, 2, UnitUtils.Units.MM);
        System.out.println(formatGCode(pos2));

        PositionXY pos3 = new PositionXYZ(1, 2, 4, UnitUtils.Units.MM);
        System.out.println(formatGCode(pos3));
        System.out.println(formatGCode(PositionXY.from(pos3)));

        PartialPosition pos5 = new PartialPosition.Builder()
                .setX(5)
                .setY(6)
                .setZ(7)
                .setValue(Axis.A, 8)
                .setValue(Axis.C, 9)
                .setUnit(UnitUtils.Units.MM)
                .build();

        System.out.println(formatGCode(pos5));
        System.out.println(formatGCode(PositionXY.from(pos5)));
        System.out.println(formatGCode(PositionXYZ.from(pos5)));

    }

    public String formatGCode(AxisPosition pos) {
        return "G0" + pos.getFormattedGCode();
    }
}