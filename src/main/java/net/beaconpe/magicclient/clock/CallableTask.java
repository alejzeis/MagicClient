package net.beaconpe.magicclient.clock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Represents a task that can be called (method).
 * Contains sections from BlockServerProject(http://blockserver.org).
 */
public class CallableTask implements Runnable, Task{
    private Object object;
    private Method method;

    private boolean repeating;
    private int delay;
    protected long lastTickRan;

    private CallableTask(Object object, String method) throws NoSuchMethodException {
        this.object = object;
        this.method = object.getClass().getMethod(method);
        for(Class<?> exType: this.method.getExceptionTypes()){
            try{
                exType.asSubclass(RuntimeException.class);
            }
            catch(ClassCastException e){
                try{
                    exType.asSubclass(Error.class);
                }
                catch(ClassCastException e2){
                    throw new IllegalArgumentException("The method throws unsupported exceptions");
                }
            }
        }
    }

    /**
     * Registers this task to repeat forever.
     * @param clock The ClientClock to register the task to.
     * @param object The Object this Callable Method resides in.
     * @param method The Name of the method.
     * @return A CallableTask object.
     * @throws NoSuchMethodException If the method does not exist.
     */
    public static CallableTask registerRepeating(ClientClock clock, Object object, String method, int delay) throws NoSuchMethodException {
        CallableTask task = new CallableTask(object, method);
        task.repeating = true;
        clock.registerTask(task);
        task.lastTickRan = (int) clock.getCurrentTick();
        task.delay = delay;
        return task;
    }

    /**
     * Register this task to the ClientClock specified.
     * @param clock The ClientClock to register to.
     * @param object The Object this Callable Method resides in.
     * @param method The name of the method/
     * @return A Callable task object.
     * @throws NoSuchMethodException If the method does not exist.
     */
    public static CallableTask registerTask(ClientClock clock, Object object, String method, int delay) throws NoSuchMethodException {
        CallableTask task = new CallableTask(object, method);
        task.repeating = false;
        task.lastTickRan = (int) clock.getCurrentTick();
        task.delay = delay;
        clock.registerTask(task);
        return task;
    }

    public void run(){
        try {
            method.invoke(object);
        } catch(IllegalAccessException | InvocationTargetException e){
                if(e instanceof InvocationTargetException){
                    Throwable t = e.getCause();
                    if(t instanceof Error){
                        throw (Error) t;
                    }
                    if(t instanceof RuntimeException){
                        throw (RuntimeException) t;
                    }
                }
                e.printStackTrace();
        }
    }

    public boolean isRepeating(){
        return repeating;
    }

    public int getDelay(){
        return delay;
    }

    public long getLastTickRan(){
        return lastTickRan;
    }

    public void setLastTickRan(long lastTickRan){
        this.lastTickRan = lastTickRan;
    }

    public void setDelay(int delay){
        this.delay = delay;
    }
}
