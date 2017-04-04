package com.softberry.fendoff;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class Vsem1 {
    int stot;

    byte[] evsem_x(byte[] bm, long seed) {
        int len = bm.length;
        byte rb;
        Rand1 rnd = new Rand1();
        rnd.setSeed(seed);

        for (int i = 0; i < len; i++) {
            rb = (byte) rnd.rand(-128, 127);
            bm[i] = (byte) (bm[i] ^ rb);
        }
        return bm;
    }

    byte[] dvsem_x(byte[] bm, long seed) {

        int len = bm.length;
        byte rb;
        Rand1 rnd = new Rand1();
        rnd.setSeed(seed);
        for (int i = 0; i < len; i++) {
            rb = (byte) rnd.rand(-128, 127);
            bm[i] = (byte) (bm[i] ^ rb);
        }
        return bm;
    }

    byte[] evsem_t(byte[] bm, long seed) {
        int len = bm.length;
        int ip;
        byte bc = 0;
        Rand2 rnd = new Rand2();
        rnd.setSeed(seed);
        boolean[] bb = new boolean[len];
        for (int i = 0; i < len; i++)
            bb[i] = true;
        for (int i = 0; i < len - 1; i++) {
            if (bb[i]) {
                ip = rnd.rand(i + 1, len - 1); // System.out.println(i+"ip="+ip+" "+bb[ip]+" "+bm[i]+" "+bm[ip]+" "+len);
                if (bb[ip]) {
                    bc = bm[ip];
                    bm[ip] = bm[i];
                    bm[i] = bc;
                    bb[ip] = false;
                } else if ((ip + 1) < len) {
                    ip++;
                    while (!bb[ip]) {
                        ip++;
                        if (ip >= len)
                            break;
                    }

                    if (ip < len) {
                        bc = bm[ip];
                        bm[ip] = bm[i];
                        bm[i] = bc;
                        bb[ip] = false;
                    }
                } else {
                    ip--;
                    while (!bb[ip] && (ip > i)) {
                        if (ip >= len)
                            break;
                        ip--;
                    }
                    if (ip <= i)
                        break;
                }
            }
        }

        return bm;
    }

    byte[] dvsem_t(byte[] bm, long seed) {

        int len = bm.length;
        int ip;
        byte bc = 0;
        Rand2 rnd = new Rand2();
        rnd.setSeed(seed);
        boolean[] bb = new boolean[len];
        for (int i = 0; i < len; i++)
            bb[i] = true;
        for (int i = 0; i < len - 1; i++) {
            if (bb[i]) {
                ip = rnd.rand(i + 1, len - 1); // System.out.println("ip="+ip+" "+bb[ip]+" "+bm[i]+" "+bm[ip]+" "+len);
                if (bb[ip]) {
                    bc = bm[ip];
                    bm[ip] = bm[i];
                    bm[i] = bc;
                    bb[ip] = false;
                } else if ((ip + 1) < len) {
                    ip++;
                    while (!bb[ip]) {
                        ip++;
                        if (ip >= len)
                            break;
                    }

                    if (ip < len) {
                        bc = bm[ip];
                        bm[ip] = bm[i];
                        bm[i] = bc;
                        bb[ip] = false;
                    }
                } else {
                    ip--;
                    while (!bb[ip] && (ip > i)) {
                        if (ip >= len)
                            break;
                        ip--;
                    }
                    if (ip <= i)
                        break;
                }
            }
        }
        return bm;
    }

    byte[] evsem_s(byte[] bm, long seed) {
        int len = bm.length;
        byte rb, tb;
        Rand1 rnd = new Rand1();
        rnd.setSeed(seed);
        for (int i = 0; i < len; i++) {

            byte jj = (byte) rnd.rand(0, 255);
            int j = (int) rnd.rand(0, 7);
            rb = bm[i];
            tb = (byte) ((((0xFF & rb) >>> j) | (rb << (8 - j))));
            bm[i] = (byte) (tb ^ jj);
        }
        return bm;
    }

    byte[] dvsem_s(byte[] bm, long seed) {
        int len = bm.length;
        byte rb, tb;
        Rand1 rnd = new Rand1();
        rnd.setSeed(seed);
        for (int i = 0; i < len; i++) {
            byte jj = (byte) rnd.rand(0, 255);
            int j = (int) rnd.rand(0, 7);
            rb = (byte) (bm[i] ^ jj);
            bm[i] = (byte) ((0xFF & rb) >>> (8 - j) | (rb << j));
        }
        return bm;
    }

    byte[] evsem_ct(byte[] bm, long seed) {
        int len = bm.length;
        byte rb;
        byte bms[] = new byte[len];
        Rand1 rnd = new Rand1();
        rnd.setSeed(seed);
        int jl = (int) rnd.rand(0, len - 1);
        for (int i = 0; i < len; i++) {
            if (i + jl < len)
                bms[i + jl] = bm[i];
            else
                bms[i + jl - len] = bm[i];
        }
        for (int i = 0; i < len; i++) {
            byte j = (byte) rnd.rand(-128, 127);
            rb = (byte) bms[i];
            bm[i] = (byte) (rb ^ j);
        }

        return bm;
    }

    byte[] dvsem_ct(byte[] bm, long seed) {
        int len = bm.length;
        byte rb;
        byte bms[] = new byte[len];
        Rand1 rnd = new Rand1();
        rnd.setSeed(seed);
        int jl = (int) rnd.rand(0, len - 1);

        for (int i = 0; i < len; i++) {
            byte j = (byte) rnd.rand(-128, 127);
            rb = (byte) bm[i];
            bms[i] = (byte) (rb ^ j);
        } // bms[i]=bm[i];
        for (int i = 0; i < len; i++) {
            if (i - jl >= 0)
                bm[i - jl] = bms[i];
            else
                bm[len - jl + i] = bms[i];
        }

        return bm;
    }


    protected long[] pastosd(int num, String pas) {
        int i;
        int len = pas.length();
        int numb = num;
        if (len > 16) numb = 2 * num;
        if (len > 32) {
            numb = 3 * num;
        }
        long[] sab = new long[numb];
        stot = 0;
        String ppas = null;
        long[] sa = new long[num];
        int lc, lp;
        if (len != 0) {
            if (len <= 16) ppas = pas;
            else ppas = pas.substring(0, 16); //System.out.println("ppas="+ppas);
            sa = pass16(num, ppas);
            lc = num;
            lp = ppas.length();
            if (lp < num) lc = lp;
            for (i = 0; i < lc; i++) {
                sab[stot] = sa[i];
                stot++;
            }

            if (len > 16) {
                if (len <= 32) ppas = pas.substring(16);
                else ppas = pas.substring(16, 32); //System.out.println("ppas="+ppas);
                sa = pass16(num, ppas);
                lc = num;
                lp = ppas.length();
                if (lp < num) lc = lp;
                for (i = 0; i < lc; i++) {
                    sab[stot] = sa[i];
                    stot++;
                }
            }

            if (len > 32) {
                if (len <= 48) ppas = pas.substring(32); //System.out.println("ppas="+ppas);
                sa = pass16(num, ppas);
                lc = num;
                lp = ppas.length();
                if (lp < num) lc = lp;
                for (i = 0; i < lc; i++) {
                    sab[stot] = sa[i];
                    stot++;
                }
            }
        }
        if (stot == 0) {
            for (i = 0; i < num; i++) {
                sab[stot] = 3000;
                stot++;
            }
        }
        return sab;
    }


    protected static long[] pass16(int num, String pas) {
        long[] sa = new long[num];
        int i;
        int len = pas.length();
        int lsl = len % num;
        int ls = (len / num);
        if (len < num) {
            ls = len;
            lsl = 0;
        }
        int lso = ls + lsl;  // Check yniversalbnostb lso  mojet ono bitb> 8
        String[] spa = new String[num];
        int numc = num - 1;
        if (lsl == 0) numc = num;
        //System.out.println(pas);  System.out.println("==numc="+numc);  System.out.println(ls);  System.out.println(lsl);
        String pad = "";
        for (i = 0; i < numc; i++) {
            pad = "";
            if (ls == 1 || ls == 5) pad = "%#$";
            else if (ls == 2 || ls == 6) pad = "&^";
            else if (ls == 3 || ls == 7) pad = "$";
            spa[i] = pad + pas.substring(i * ls, (i + 1) * ls); //System.out.println("spa="+spa[i]);
            byte[] bs = spa[i].getBytes();   //System.out.println("bs="+bs);
            ByteBuffer bb = ByteBuffer.wrap(bs);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            if (ls <= 4) sa[i] = bb.getInt();
            else sa[i] = bb.getLong();
            if (numc < num) sa[num - 1] = sa[num - 2];
        }
        if (lsl > 0 && len > num) {
            pad = "";
            if (lso == 1 || lso == 5) pad = "123";
            else if (lso == 2 || lso == 6) pad = "45";
            else if (lso == 3 || lso == 7) pad = "6";
            spa[num - 1] = pad + pas.substring(numc * ls); //System.out.println("spao="+spa[num-1]);
            byte[] bs = spa[num - 1].getBytes();   //System.out.println("bs="+bs);
            //LongBuffer bb =ByteBuffer.wrap(bs).asLongBuffer();
            ByteBuffer bb = ByteBuffer.wrap(bs);
            bb.order(ByteOrder.BIG_ENDIAN);
            if (ls <= 4) sa[num - 1] = bb.getInt();
            else sa[num - 1] = bb.getLong();
        }
        return sa;
    }
}

