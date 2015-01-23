package net.beaconpe.magicclient.world;

/**
 * Represents a Position in the world.
 */
public class Location {
    protected double x;
    protected double y;
    protected double z;

    /**
     * Create a new Position based on coordinates.
     * @param x The X position.
     * @param y The Y position.
     * @param z The Z position.
     */
    public Location(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Get the X coordinate.
     * @return The X coordinate.
     */
    public double getX(){
        return x;
    }
    /**
     * Get the Y coordinate.
     * @return The Y coordinate.
     */
    public double getY(){
        return y;
    }
    /**
     * Get the Z coordinate.
     * @return The Z coordinate.
     */
    public double getZ(){
        return z;
    }

    /**
     * Set the X coordinate.
     * @param x The X coordinate.
     */
    public void setX(double x){
        this.x = x;
    }
    /**
     * Set the Y coordinate.
     * @param y The X coordinate.
     */
    public void setY(double y){
        this.y = y;
    }
    /**
     * Set the Z coordinate.
     * @param z The X coordinate.
     */
    public void setZ(double z){
        this.z = z;
    }
}
