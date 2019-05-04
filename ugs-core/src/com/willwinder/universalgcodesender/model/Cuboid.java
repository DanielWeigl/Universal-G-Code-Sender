package com.willwinder.universalgcodesender.model;

/**
 * A wrapper class for a couboid defined by two diagonal corners
 * E.g. used to define the maximum working space
 */
public class Cuboid {
    Position minPos;
    Position maxPos;

    public Cuboid() {
        minPos = null;
        maxPos = null;
    }

    public Cuboid(Position minPos, Position maxPos) {
        this.minPos = minPos;
        this.maxPos = maxPos;
    }

    public void extendBy(Position position){
        if (minPos == null) {
            minPos=new Position(position);
        }
        if (maxPos == null) {
            maxPos=new Position(position);
        }
        if(position.x < minPos.x) {
            minPos.x = position.x;
        }
        if(position.x > maxPos.x) {
            maxPos.x = position.x;
        }
        if(position.y < minPos.y) {
            minPos.y = position.y;
        }
        if(position.y > maxPos.y) {
            maxPos.y = position.y;
        }
        if(position.z < minPos.z) {
            minPos.z = position.z;
        }
        if(position.z > maxPos.z) {
            maxPos.z = position.z;
        }
    }

    public Position getMinPos() {
        return minPos;
    }

    public Position getMaxPos() {
        return maxPos;
    }

}
