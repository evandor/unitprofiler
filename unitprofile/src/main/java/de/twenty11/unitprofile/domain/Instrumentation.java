package de.twenty11.unitprofile.domain;


public class Instrumentation {

    private String thread;
    private String object;
    private String method;

    public Instrumentation(String object, String method) {
        this.thread = Thread.currentThread().getName();
        this.object = object;
        this.method = method;
        //System.out.println("added " + thread + ", " + object + ", " + method);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(thread).append(", ").append(object).append(", ").append(method);
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + ((thread == null) ? 0 : thread.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Instrumentation other = (Instrumentation) obj;
        if (method == null) {
            if (other.method != null)
                return false;
        } else if (!method.equals(other.method))
            return false;
        if (object == null) {
            if (other.object != null)
                return false;
        } else if (!object.equals(other.object))
            return false;
        if (thread == null) {
            if (other.thread != null)
                return false;
        } else if (!thread.equals(other.thread))
            return false;
        return true;
    }
    
    
}
