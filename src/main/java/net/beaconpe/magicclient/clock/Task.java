package net.beaconpe.magicclient.clock;

/**
 * Represents a task that can be ran from the ClientClock.
 */
public interface Task extends Runnable{
    /**
     * Get the delay (in ticks) that this task runs between.
     * @return The delay.
     */
    int getDelay();

    /**
     * Set the delay (in ticks).
     * @param ticks The Amount of ticks this task should wait in between runs.
     */
    void setDelay(int ticks);

    /**
     * If this task repeats.
     * @return If the task repeats.
     */
    boolean isRepeating();

    long getLastTickRan();

    void setLastTickRan(long lastTickRan);

}
