package de.twenty11.unitprofile.callback;

public class ProfilerCallback {
    
    static int indention = 0;
    
    static int depthTrigger = 0;

    public static void before (String objectName, String methodName, int depth, long timestamp) {
        if (depth > depthTrigger) {
            System.out.println("---"+ objectName + "#" + methodName + " ("+depth+") " + timestamp);
            return;
        }
        System.out.println(indentBy(indention) + objectName + "#" + methodName + " ("+depth+") " + timestamp);
        indention++;
        depthTrigger++;
    }
    
    public static void after (String objectName, String methodName, int depth, long timestamp) {
        if (indention > 0) {
            indention--;
        }
        if (depth < depthTrigger && depthTrigger > 0) {
            depthTrigger--;
        }
        System.out.println(indentBy(indention) + objectName + "#" + methodName + " ("+depth+") " + timestamp);
    }
    
    private static String indentBy(int indention) {
        StringBuilder sb = new StringBuilder(String.valueOf(indention));
        for (int i = 0; i < 2 * indention; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
