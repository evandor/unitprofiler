package de.twenty11.unitprofile.domain;

public class Transformation {

    private String className;
    private TransformationResult transformationResult;
    private int origSize;
    private int newLength;

    public Transformation(String className, int origSize) {
        this.className = className;
        this.origSize = origSize;
        this.transformationResult = TransformationResult.UNTOUCHED;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
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
        Transformation other = (Transformation) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(className);
        sb.append(" [").append(transformationResult).append("]");
        sb.append(" (").append(origSize).append(" ");
        if (TransformationResult.TRANSFORMED.equals(transformationResult)) {
            sb.append("-> ").append(newLength);
        }
        sb.append(" bytes)");
        return sb.toString();
    }

    public void update(int newLength) {
        this.newLength = newLength;
        if (newLength != origSize) {
            this.transformationResult = TransformationResult.TRANSFORMED;
        }
    }

}
