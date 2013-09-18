package de.twenty11.unitprofile.domain;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Describing object of an instrumented method.
 *
 */
public class Instrumentation implements Comparable<Instrumentation>{

    private static final Logger logger = LoggerFactory.getLogger(Instrumentation.class);
    
    private String thread;
    private String object;
    private String method;

    private List<Invocation> invocations = new ArrayList<Invocation>();

    public Instrumentation(String object, String method) {
        this.thread = Thread.currentThread().getName();
        this.object = object;
        this.method = method;
        //this.instrumented = 1;
        logger.debug("added " + thread + ", " + object + ", " + method);
    }
    
    public void addInvocation(Invocation invocation) {
        this.invocations.add(invocation);
        
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(thread).append(", ").append(object).append(", ").append(method);
        return sb.toString();
    }
    
    @Override
    public int compareTo(Instrumentation o) {
        return this.toString().compareTo(o.toString());
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
