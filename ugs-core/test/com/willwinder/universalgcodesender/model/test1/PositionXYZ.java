package com.willwinder.universalgcodesender.model.test1;

import com.google.common.collect.ImmutableMap;
import com.willwinder.universalgcodesender.model.Axis;
import com.willwinder.universalgcodesender.model.UnitUtils;

import java.util.Map;

public class PositionXYZ extends PositionXY implements AxisPosition {
    double z;



    public PositionXYZ(double x, double y, double z, UnitUtils.Units units) {
        super(x, y, units);
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    @Override
    public Map<Axis, Double> getAll() {
        return ImmutableMap.of(
                Axis.X, getX(),
                Axis.Y, getY(),
                Axis.Z, getZ()
        );
    }

    @Override
    public boolean has(Axis axis) {
        switch (axis) {
            case X:
            case Y:
            case Z:
                return true;
            default:
                return false;
        }
    }

    @Override
    public double get(Axis axis) {
        switch (axis) {
            case X:
                return getX();
            case Y:
                return getY();
            case Z:
                return getZ();
            default:
                throw new IllegalArgumentException("Axis " + axis + " not set in " + this);
        }
    }
}
