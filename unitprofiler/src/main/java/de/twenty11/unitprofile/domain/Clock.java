package de.twenty11.unitprofile.domain;

/**
 * clock implementation to keep track of durations
 *
 */
public class Clock {
    
    private long start;
    private Long end = null;

    /**
     * creates and starts a clock.
     */
    public Clock() {
        this.start = System.currentTimeMillis();
    }
    
    /**
     * stops the clock 
     */
    public void stop() {
        this.end = System.currentTimeMillis();
    }

    public long getStart() {
        return start;
    }

    /**
     * @return current time if not stopped yet; otherwise time when stop method was called.
     */
    public long getEnd() {
        if (end == null) {
            return System.currentTimeMillis();
        }
        return end;
    }
    
    /**
     * @return has the clock been stopped
     */
    public boolean isStopped() {
        return end != null;
    }

    /**
     * @return time elapsed since starting (when not stopped yet); otherwise total elapsed time between creation and call of stop method.
     */
    public long getElapsed() {
        return getEnd() - getStart();
    }
    
    @Override
    public String toString() {
        long endOrNow = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder("Start: ").append(start);
        if (!isStopped()) {
            return sb.append(", Now: ").append(endOrNow).toString();
        }
        sb.append(", End: ").append(endOrNow).append(": ");
        sb.append(endOrNow - start);
        return sb.toString();
    }
}
