package com.softberry.fendoff;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by umarovr on 1/20/15.
 */
public class Utils {

    public static void saveEncryptedFile(File selectedFile, File output, String input, Context context) {
        try {
            String passw = input;
            FileOutputStream oS = null;
            String allt = "";
            String outs;
            byte[] bouts;
            byte[] boutss;
            String sFile = selectedFile.toString();
            int dsize = readpfbs(sFile);
            byte[] allf = readpfb(dsize, sFile);
            output.createNewFile();
            oS = new FileOutputStream(output);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            outs = String.format("FendoffP " + dateFormat.format(date) + "\n");
            allt = allt.concat(outs);
            allt = allt.concat(passw);
            allt = allt.concat("\n");
            int lensh = allt.length();
            lensh = lensh + 3;
            outs = "" + lensh + "\n";
            allt = allt.concat(outs);
            boutss = allt.getBytes();
            ByteArrayOutputStream outst = new ByteArrayOutputStream();
            outst.write(boutss);
            outst.write(allf);
            bouts = outst.toByteArray();
            Vsem1 em = new Vsem1();
            long[] sa = em.pastosd(2, passw);
            int stot = em.stot;
            bouts = em.evsem_x(bouts, sa[0]);
            bouts = em.evsem_t(bouts, sa[1]);
            long s3 = 999;
            if (stot > 2) s3 = sa[2];
            bouts = em.evsem_s(bouts, s3);
            s3 = -999;
            if (stot > 3) s3 = sa[3];
            bouts = em.evsem_ct(bouts, s3);
            oS.write(bouts);
            oS.close();
            if(context!=null) {
                Toast.makeText(context, "File Saved",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void saveDecryptedFile(File selectedFile, File output, String input, Context context) {
        String sFile = selectedFile.toString();
        int fl = sFile.length();
        String passw = input;
        int dsize = readpfbs(sFile);
        byte[] allf = readpfb(dsize, sFile);
        Vsem1 em = new Vsem1();
        long[] sa = em.pastosd(2, passw);
        int stot = em.stot;
        long s3 = -999;
        if (stot > 3) s3 = sa[3];
        allf = em.dvsem_ct(allf, s3);
        s3 = 999;
        if (stot > 2) s3 = sa[2];
        allf = em.dvsem_s(allf, s3);
        allf = em.dvsem_t(allf, sa[1]);
        allf = em.dvsem_x(allf, sa[0]);
        String buf = new String(allf);
        Scanner scanner = new Scanner(buf);
        String line = " ";
        if (scanner.hasNextLine()) {
            line = scanner.nextLine();
            passw = scanner.nextLine();
            line = scanner.nextLine();
        }
        if (Utils.isPasswordCorrect(input, passw)) {
            int shl = Integer.valueOf(line);
            byte[] df = new byte[allf.length - shl];
            System.arraycopy(allf, shl, df, 0, allf.length - shl);
            try {
                FileOutputStream oS = new FileOutputStream(output);
                oS.write(df);
                oS.close();
                Toast.makeText(context, "File Saved",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFilePathFromContentUri(Uri selectedUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedUri, filePathColumn, null, null, null);
        if (cursor == null) return "";
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public static boolean isPasswordCorrect(String input, String line) {
        boolean isCorrect = true;
        if (input.length() != line.length()) {
            isCorrect = false;
        } else {
            isCorrect = input.equals(line);
        }
        line = "";
        return isCorrect;
    }

    public static int readpfbs(String fileName) {
        int dsize = 0;
        try {
            byte[] buffer = new byte[100];

            FileInputStream inputStream =
                    new FileInputStream(fileName);

            dsize = 0;
            int nRead = 0;
            while ((nRead = inputStream.read(buffer)) != -1) {
                dsize += nRead;
            }
            inputStream.close();
        } catch (Exception ex) {
        }
        return dsize;
    }


    public static byte[] readpfb(int dsize, String fileName) {
        int total;
        byte[] allf = new byte[dsize];
        try {
            byte[] buffer = new byte[100];
            FileInputStream inputStream =
                    new FileInputStream(fileName);
            total = 0;
            int nRead = 0;
            while ((nRead = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < nRead; i++) allf[i + total] = buffer[i];
                total += nRead;
            }
            inputStream.close();
            dsize = total;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return allf;
    }

    public static void saveEncryptedFile(List list, File vault, String vaultPass, Context applicationContext) {
        try {
            FileOutputStream fos = new FileOutputStream(vault);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
            fos.close();
            saveEncryptedFile(vault, vault, vaultPass, applicationContext);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static ArrayList getVault(File vault, String vaultPass){
        ArrayList<Category> list = null;
        try {
        String sFile = vault.toString();
        int fl = sFile.length();
        String passw = vaultPass;
        int dsize = readpfbs(sFile);
        byte[] allf = readpfb(dsize, sFile);
        Vsem1 em = new Vsem1();
        long[] sa = em.pastosd(2, passw);
        int stot = em.stot;
        long s3 = -999;
        if (stot > 3) s3 = sa[3];
        allf = em.dvsem_ct(allf, s3);
        s3 = 999;
        if (stot > 2) s3 = sa[2];
        allf = em.dvsem_s(allf, s3);
        allf = em.dvsem_t(allf, sa[1]);
        allf = em.dvsem_x(allf, sa[0]);
        String buf = new String(allf);
        Scanner scanner = new Scanner(buf);
        String line = " ";
        if (scanner.hasNextLine()) {
            line = scanner.nextLine();
            passw = scanner.nextLine();
            line = scanner.nextLine();
        }
        if (Utils.isPasswordCorrect(vaultPass, passw)) {
            int shl = Integer.valueOf(line);
            byte[] df = new byte[allf.length - shl];
            System.arraycopy(allf, shl, df, 0, allf.length - shl);

                ByteArrayInputStream bis = new ByteArrayInputStream(df);
                ObjectInputStream ois = new ObjectInputStream(bis);
                list = (ArrayList<Category>) ois.readObject();
                ois.close();
                bis.close();
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
