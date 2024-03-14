package dev.potato.xpworldborder.models;

import dev.potato.xpworldborder.utilities.enumerations.QuadrantType;
import org.bukkit.Location;

public record Quadrant(QuadrantType quadrantType, Location referenceLocation, Location center) {
    public static Quadrant getQuadrant(Location referenceLocation, Location center) {
        Quadrant quadrant;
        double referenceX = referenceLocation.getX();
        double referenceZ = referenceLocation.getZ();
        double centerX = center.getX();
        double centerZ = center.getZ();

        if (referenceX >= centerX && referenceZ >= centerZ) {
            quadrant = new Quadrant(QuadrantType.POS_POS, referenceLocation, center);
        } else if (referenceX > centerX && referenceZ < centerZ) {
            quadrant = new Quadrant(QuadrantType.POS_NEG, referenceLocation, center);
        } else if (referenceX < centerX && referenceZ < centerZ) {
            quadrant = new Quadrant(QuadrantType.NEG_NEG, referenceLocation, center);
        } else if (referenceX < centerX && referenceZ > centerZ) {
            quadrant = new Quadrant(QuadrantType.NEG_POS, referenceLocation, center);
        } else {
            quadrant = new Quadrant(QuadrantType.CENTER, referenceLocation, center);
        }

        return quadrant;
    }

    public double getDistanceX() {
        return Math.abs(referenceLocation.getX() - center.getX());
    }

    public double getDistanceZ() {
        return Math.abs(referenceLocation.getZ() - center.getZ());
    }

    public double getDistanceY() {
        return Math.abs(referenceLocation.getY() - center.getY());
    }

    public double getDiagonalDistance() {
        return getDistanceX() + getDistanceZ();
    }
}