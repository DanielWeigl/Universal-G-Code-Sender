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

}
