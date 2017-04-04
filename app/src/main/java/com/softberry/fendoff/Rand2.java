package com.softberry.fendoff;

public class Rand2 {
    private long seed = 0xCAFEBCDE;

    public long rand() {
        seed ^= (seed << 21);
        seed ^= (seed >>> 35);
        seed ^= (seed << 4);
        return seed;
    }

    public int rand(int min, int max) {
        if (min > max) {
            int m = max;
            max = min;
            min = m;
        }
        if (min == max) {
            return min;
        }
        int out = (int) rand() % max;
        out = (out < 0) ? -out : out;
        out = (out % (max + 1 - min)) + min;
        return out;
    }

    public void setSeed(long seed) {
        seed = seed + 0xCAFDBCDE;
        if (seed == 0) seed = 0xCADDBCDE;
        this.seed = seed;
    }

}
