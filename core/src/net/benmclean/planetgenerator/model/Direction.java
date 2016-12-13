package net.benmclean.planetgenerator.model;

public enum Direction {
    NONE(0, 0), NORTH(0, 1), SOUTH(0, -1), WEST(-1, 0), EAST(1, 0),
    NORTHEAST(1, 1), SOUTHEAST(1, -1), SOUTHWEST(-1, -1), NORTHWEST(-1, 1);

    private final int dx, dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() { return dx; }
    public int dy() { return dy; }

    public static Direction getRandomDirection(){
        return Direction.values()[(int)(Math.random() * (Direction.values().length - 1))];
    }

    public Direction opposite() { return opposite(this); }
    public static Direction opposite(Direction value) {
        switch(value) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST: return WEST;
            case WEST: return EAST;
            case NORTHEAST: return SOUTHWEST;
            case SOUTHEAST: return NORTHWEST;
            case SOUTHWEST: return NORTHEAST;
            case NORTHWEST: return SOUTHEAST;
            default: return NONE;
        }
    }

    public String toString() { return toString(this); }
    public static String toString(Direction value) {
        switch(value) {
            case NORTH: return "N";
            case SOUTH: return "S";
            case EAST: return "E";
            case WEST: return "W";
            case NORTHEAST: return "NE";
            case SOUTHEAST: return "SE";
            case SOUTHWEST: return "SW";
            case NORTHWEST: return "NW";
            default: return null;
        }
    }
}
