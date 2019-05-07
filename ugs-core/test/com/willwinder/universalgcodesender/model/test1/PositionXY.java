package com.willwinder.universalgcodesender.model.test1;

import com.google.common.collect.ImmutableMap;
import com.willwinder.universalgcodesender.model.Axis;
import com.willwinder.universalgcodesender.model.UnitUtils;

import java.util.Map;

public class PositionXY implements AxisPosition {

    double x;
    double y;
    private final UnitUtils.Units unit;

    public PositionXY(double x, double y, UnitUtils.Units unit) {
        this.x = x;
        this.y = y;
        this.unit = unit;
    }

    public static PositionXY from(PositionXY other) {
        return new PositionXY(other.getX(), other.getY(), other.getUnit());
    }

    public static PositionXY from(PartialPosition other) {
        if (!other.has(Axis.X) || !other.has(Axis.Y)) {
            throw new IllegalArgumentException("...");
        }
        return new PositionXY(other.getX(), other.getY(), other.getUnit());
    }

    public double getX() {
        return x;
    }


    public double getY() {
        return y;
    }


    @Override
    public boolean has(Axis axis) {
        switch (axis) {
            case X:
            case Y:
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
            default:
                throw new IllegalArgumentException("Axis " + axis + " not set in " + this);
        }
    }

    @Override
    public Map<Axis, Double> getAll() {
        return ImmutableMap.of(Axis.X, getX(), Axis.Y, getY());
    }

    @Override
    public UnitUtils.Units getUnit() {
        return unit;
    }
}
