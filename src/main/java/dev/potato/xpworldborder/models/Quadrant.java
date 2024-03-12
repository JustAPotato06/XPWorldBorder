package dev.potato.xpworldborder.models;

import dev.potato.xpworldborder.utilities.enumerations.QuadrantType;
import org.bukkit.Location;

public class Quadrant {
    private int quadrantNumber;
    private QuadrantType quadrantType;
    private boolean isXPositive;
    private boolean isZPositive;
    private Location initialLocation;
    private Location center;

    public Quadrant(int quadrantNumber, QuadrantType quadrantType, boolean isXPositive, boolean isZPositive, Location initialLocation, Location center) {
        this.quadrantNumber = quadrantNumber;
        this.quadrantType = quadrantType;
        this.isXPositive = isXPositive;
        this.isZPositive = isZPositive;
        this.initialLocation = initialLocation;
        this.center = center;
    }

    public Quadrant() {
    }

    public int getQuadrantNumber() {
        return quadrantNumber;
    }

    public void setQuadrantNumber(int quadrantNumber) {
        this.quadrantNumber = quadrantNumber;
    }

    public QuadrantType getQuadrantType() {
        return quadrantType;
    }

    public void setQuadrantType(QuadrantType quadrantType) {
        this.quadrantType = quadrantType;
    }

    public boolean isXPositive() {
        return isXPositive;
    }

    public void setXPositive(boolean XPositive) {
        isXPositive = XPositive;
    }

    public boolean isZPositive() {
        return isZPositive;
    }

    public void setZPositive(boolean ZPositive) {
        isZPositive = ZPositive;
    }

    public Location getInitialLocation() {
        return initialLocation;
    }

    public void setInitialLocation(Location initialLocation) {
        this.initialLocation = initialLocation;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public static Quadrant getQuadrant(Location location, Location center) {
        Quadrant quadrant;
        if (location.getX() > center.getX() && location.getZ() > center.getZ()) {
            quadrant = new Quadrant(1, QuadrantType.POS_POS, true, true, location, center);
        } else if (location.getX() > center.getX() && location.getZ() < center.getZ()) {
            quadrant = new Quadrant(4, QuadrantType.POS_NEG, true, false, location, center);
        } else if (location.getX() < center.getX() && location.getZ() < center.getZ()) {
            quadrant = new Quadrant(3, QuadrantType.NEG_NEG, false, false, location, center);
        } else if (location.getX() < center.getX() && location.getZ() > center.getZ()) {
            quadrant = new Quadrant(2, QuadrantType.NEG_POS, false, true, location, center);
        } else {
            quadrant = new Quadrant(0, QuadrantType.CENTER, false, false, location, center);
        }
        return quadrant;
    }

    public static Location getHighestPoint(Location location) {
        return location.toHighestLocation().add(0, 2, 0);
    }

    public double getDistanceX() {
        return Math.abs(initialLocation.getX() - center.getX());
    }

    public double getDistanceZ() {
        return Math.abs(initialLocation.getZ() - center.getZ());
    }

    public double getDistanceY() {
        return Math.abs(initialLocation.getY() - center.getY());
    }

    public double getDiagonalDistance() {
        return getDistanceX() + getDistanceZ();
    }
}