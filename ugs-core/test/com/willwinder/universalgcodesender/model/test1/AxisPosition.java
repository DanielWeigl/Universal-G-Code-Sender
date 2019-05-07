package com.willwinder.universalgcodesender.model.test1;

import com.willwinder.universalgcodesender.Utils;
import com.willwinder.universalgcodesender.model.Axis;
import com.willwinder.universalgcodesender.model.UnitUtils;

import java.text.NumberFormat;
import java.util.Map;

public interface AxisPosition {
    boolean has(Axis axis);

    double get(Axis axis);


    Map<Axis, Double> getAll();

    UnitUtils.Units getUnit();

    // or move this into a more generic "Formatter" class to decouple it a bit more
    default String getFormattedGCode() {
        return getFormattedGCode(Utils.formatter);

    }

    default String getFormattedGCode(NumberFormat formatter) {
        StringBuilder sb = new StringBuilder();
        getAll().forEach((axis, value) -> sb.append(axis).append(formatter.format(value)));
        return sb.toString();
    }

    default PositionXYZ getXYZ() {
        if (!this.has(Axis.X) || !this.has(Axis.Y) || !this.has(Axis.Z)) {
            throw new IllegalArgumentException("...");
        }
        return new PositionXYZ(this.get(Axis.X), this.get(Axis.Y), this.get(Axis.Z), this.getUnit());
    }

    default PositionXY getXY() {
        if (!this.has(Axis.X) || !this.has(Axis.Y)) {
            throw new IllegalArgumentException("...");
        }
        return new PositionXY(this.get(Axis.X), this.get(Axis.Y), this.getUnit());
    }


}
