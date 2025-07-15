package com.junbeom.utils;

public class Add {
    /**
     * return the sum of two integers
     * @param a first integer
     * @param b second integer
     * @return the sum of a and b
     */
    public static int add(int a, int b) {
        return a + b;
    }

    /**
     * return the sum of two doubles
     * @param a first double
     * @param b second double
     * @return the sum of a and b
     */
    public static double add(double a, double b) {
        return a + b;
    }

    /**
     * return the sum of multiple integers
     * @param numbers vararg of integers to be summed
     * @return the sum of the integers
     */
    public static int addAll(int... numbers) {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }
}
