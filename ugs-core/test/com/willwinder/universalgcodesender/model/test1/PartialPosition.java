package com.willwinder.universalgcodesender.model.test1;


import com.google.common.collect.ImmutableMap;
import com.willwinder.universalgcodesender.model.Axis;
import com.willwinder.universalgcodesender.model.UnitUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a maybe partial coordinate (ie. only certain axis-values are known, eg. for moves where only some axis
 * are changed)
 */
public class PartialPosition implements AxisPosition {
    private final UnitUtils.Units unit;
    private final ImmutableMap<Axis, Double> axisPositions;

    private PartialPosition(Map<Axis, Double> axisPositions, UnitUtils.Units unit) {
        this.axisPositions = ImmutableMap.copyOf(axisPositions);  // maybe not needed, if we can make everything immutable
        this.unit = unit;
    }

    //    public PartialPosition(Double x, Double y) {
//        this.x = x;
//        this.y = y;
//        this.z = null;
//        this.units = UnitUtils.Units.UNKNOWN;
//    }
//
    public PositionXYZ from(double x, double y, double z, UnitUtils.Units units) {
        return new PositionXYZ(x, y, z, units);
    }

    public PositionXY from(double x, double y, UnitUtils.Units units) {
        return new PositionXY(x, y, units);
    }
//
//    public PartialPosition(Double x, Double y, Double z) {
//        this.x = x;
//        this.y = y;
//        this.z = z;
//        this.units = UnitUtils.Units.UNKNOWN;
//    }
//
//    public PartialPosition(Double x, Double y, Double z, UnitUtils.Units units) {
//        this.x = x;
//        this.y = y;
//        this.z = z;
//        this.units = units;
//    }


    // a shortcut to builder (needed, because of final coords)
    public static PartialPosition from(Axis axis, Double value) {
        return new Builder().setValue(axis, value).build();
    }

    public static PartialPosition from(AxisPosition position) {
        return new PartialPosition(position.getAll(), position.getUnit());
    }

    public static PositionXY fromXY(PositionXY position) {
        return new PositionXY(position.getX(), position.getY(), position.getUnit());
    }


    public boolean hasX() {
        return getAll().containsKey(Axis.X);
    }

    public boolean hasY() {
        return getAll().containsKey(Axis.X);
    }

    public boolean hasZ() {
        return getAll().containsKey(Axis.X);
    }

    @Override
    public boolean has(Axis axis) {
        return getAll().containsKey(axis);
    }

    @Override
    public double get(Axis axis) {
        //...            throw new IllegalArgumentException("Tried to get ...-axis which is not set");
        return getAll().get(axis);
    }

    public Double getX() {
        if (!hasX()) {
            throw new IllegalArgumentException("Tried to get x-axis which is not set");
        }
        return get(Axis.X);
    }

    public Double getY() {
        if (!hasY()) {
            throw new IllegalArgumentException("Tried to get y-axis which is not set");
        }
        return get(Axis.Y);
    }

    public Double getZ() {
        if (!hasZ()) {
            throw new IllegalArgumentException("Tried to get z-axis which is not set");
        }
        return get(Axis.Z);
    }

    //....


    public UnitUtils.Units getUnit() {
        return unit;
    }

    public Map<Axis, Double> getAll() {
        return axisPositions;
    }

    public double getAxis(Axis axis) {
        return axisPositions.get(axis);
    }

    public PartialPosition getPositionIn(UnitUtils.Units units) {
        double scale = UnitUtils.scaleUnits(this.unit, units);
        Builder builder = new Builder();
        for (Map.Entry<Axis, Double> axis : getAll().entrySet()) {
            builder.setValue(axis.getKey(), axis.getValue() * scale);
        }
        builder.setUnit(units);
        return builder.build();
    }


    public static final class Builder {
        private HashMap<Axis, Double> axisPosition = new HashMap<>(3);
        private UnitUtils.Units unit = UnitUtils.Units.UNKNOWN;

        public PartialPosition build() {
            return new PartialPosition(axisPosition, unit);
        }

        public Builder setValue(Axis axis, double value) {
            this.axisPosition.put(axis, value);
            return this;
        }

        public Builder setX(double x) {
            this.setValue(Axis.X, x);
            return this;
        }

        public Builder setY(double y) {
            this.setValue(Axis.Y, y);
            return this;
        }

        public Builder setZ(double z) {
            this.setValue(Axis.Z, z);
            return this;
        }

        public Builder copy(AxisPosition position) {
            this.axisPosition.putAll(position.getAll());
            this.unit = position.getUnit();
            return this;
        }

        public Builder setUnit(UnitUtils.Units unit) {
            this.unit = unit;
            return this;
        }


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartialPosition)) return false;
        PartialPosition that = (PartialPosition) o;
        return Objects.equals(getAll(), that.getAll()) &&
                Objects.equals(getUnit(), that.getUnit());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getAll(), getUnit());
    }

    @Override
    public String toString() {
        return "PartialPosition{" + getFormattedGCode() + " [" + getUnit() + "]}";
    }
}
