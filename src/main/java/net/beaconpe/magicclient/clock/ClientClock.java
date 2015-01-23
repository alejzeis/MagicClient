package net.beaconpe.magicclient.clock;

import net.beaconpe.magicclient.MCPEClient;

import java.util.ArrayList;

public class ClientClock extends Thread{
    private MCPEClient client;
    private boolean running;

    private long currentTick = -1;
    private long lastTick = -1;
    private int tps = 20;

    private ArrayList<Task> tasks = new ArrayList<Task>();

    public ClientClock(MCPEClient client){
        this.client = client;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    public void run(){
        setName("MagicClient-Clock");
        client.getLogger().info("ClientClock started.");
        while(running){
            currentTick++;
            lastTick = currentTick - 1;
            int sleepTime = 1000 / tps;
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (tasks){
                for(Task task: tasks){
                    if((currentTick - task.getLastTickRan()) == task.getDelay()){
                        task.run();
                        //System.out.println("Running on: "+currentTick);
                        task.setLastTickRan(currentTick);
                    }
                }
            }
        }
    }

    /**
     * Sets this clock's TPS (Ticks Per Second). DO NOT SET BELOW 15!
     */
    public void setTPS(int tps){
        if(tps < 20 && tps > 15) {
            this.tps = tps;
        } else {
            throw new IllegalArgumentException("Invalid TPS.");
        }
    }

    /**
     * Registers a task.
     * @param task The task.
     */
    public void registerTask(Task task){
        synchronized (tasks) {
            tasks.add(task);
        }
    }

    public long getCurrentTick(){
        return currentTick;
    }

}
