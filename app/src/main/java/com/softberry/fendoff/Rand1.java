package com.softberry.fendoff;

public class Rand1 {
    private long seed;

    public Rand1() {
        long s = System.nanoTime(); // See note
        setSeed(s != 0 ? s : 1);
    }

    public Rand1(long seed) {
        setSeed(seed);
    }

    public long rand() {
        seed ^= (seed << 13);
        seed ^= (seed >>> 7);
        seed ^= (seed << 17);
        return seed;
    }

    public long randNonNegative() {
        return rand() >>> 1;
    }

    public long rand(int bits) {
        return rand() >>> (64 - bits);
    }

    public long rand(long min, long max) {
        if (min > max) { // See other note
            long m = max;
            max = min;
            min = m;
        }
        if (min == max) {
            return min;
        }
        return ((rand() >>> 1) % (max + 1 - min)) + min;
    }

    public float randf(float min, float max, int dev) {
        if (min == max) {
            return min;
        }
        return ((float) rand((long) (min * dev), (long) (max * dev)) / dev); // Probably should be long and not int.
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        seed = seed + 0xCAFEBCDE;
        if (seed == 0) seed = 0xCADEBCDE;
        //    throw new IllegalArgumentException("Seed cannot be zero.");
        this.seed = seed;
    }
}