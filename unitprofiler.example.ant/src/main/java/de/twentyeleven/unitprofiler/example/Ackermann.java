package de.twentyeleven.unitprofiler.example;

public class Ackermann {

    public static long calc(long m, long n) {
        if (m == 0)
            return n + 1;
        if (n == 0)
            return calc(m - 1, 1);
        return calc(m - 1, calc(m, n - 1));
    }
}
