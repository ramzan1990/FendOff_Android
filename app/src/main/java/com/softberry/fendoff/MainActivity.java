package com.softberry.fendoff;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import ar.com.daidalos.afiledialog.FileChooserDialog;


public class MainActivity extends MyActionBarActivity {

    private static File selectedFile;
    private static int mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent myIntent = new Intent(MainActivity.this, AboutActivity.class);
            MainActivity.this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openVault(View view) {
        File vault = getVaultFile();
        if (vault.exists()) {
            showFragment(new VaultPass2Fragment());
        } else {
            showFragment(new VaultPass1Fragment());
        }
    }

    public void resetVault(View view) {
        TextView tv1 = (TextView) findViewById(R.id.reset_password);
        final String rp = tv1.getText().toString().trim();
        if (rp.length() < 4) {
            Toast.makeText(getApplicationContext(), "Password should be at least 4 characters long!",
                    Toast.LENGTH_SHORT).show();
        } else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            createVault(rp);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                    dialog.dismiss();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure? This cannot be undone.").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    public void createVault(String p) {
        File vault = getVaultFile();
        if (vault.exists()) {
            vault.delete();
        }
        getSupportFragmentManager().popBackStack();
        Intent myIntent = new Intent(MainActivity.this, VaultActivity.class);
        myIntent.putExtra("pass", p);
        myIntent.putExtra("vault", vault.toString());
        MainActivity.this.startActivity(myIntent);
    }

    public void createVault(View view) {
        try {
            TextView tv = (TextView) findViewById(R.id.newVaultPass);
            String vaultPass = tv.getText().toString().trim();
            if (vaultPass.length() < 4) {
                Toast.makeText(getApplicationContext(), "Password should be at least 4 characters long!",
                        Toast.LENGTH_SHORT).show();
            } else {
                createVault(vaultPass);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private File getVaultFile() {
        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (Exception e) {
        }
        File vault = new File(s + "/" + "vault");
        return vault;
    }

    public void enterVault(View view) {
        TextView tv = (TextView) findViewById(R.id.vault_password);
        String vaultPass = tv.getText().toString();
        File vault = getVaultFile();
        ArrayList<Category> list = Utils.getVault(vault, vaultPass);
        if (list != null) {
            getSupportFragmentManager().popBackStack();
            Intent myIntent = new Intent(MainActivity.this, VaultActivity.class);
            myIntent.putExtra("pass", vaultPass);
            myIntent.putExtra("vault", vault.toString());
            VaultActivity.list = list;
            MainActivity.this.startActivity(myIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Wrong Password!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public String getPass() {
        TextView tv = (TextView) findViewById(R.id.pass_field);
        TextView tv2 = (TextView) findViewById(R.id.pass_field2);
        String pass = tv.getText().toString();
        String pass2 = tv2.getText().toString();
        pass = pass.trim();
        pass2 = pass2.trim();
        if(mode==1){
            pass2 = pass;
        }
        if (!pass.equals(pass2)) {
            Toast.makeText(getApplicationContext(), "Passwords don't match!",
                    Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 4) {
            Toast.makeText(getApplicationContext(), "Password should be at least 4 characters long!",
                    Toast.LENGTH_SHORT).show();
        } else {
            return pass;
        }
        return null;
    }

    public void saveSamePlace(View view) {
        String sFile = selectedFile.toString();
        String pass = getPass();
        if (pass != null) {
            if (mode == 0) {
                File output = new File(sFile + ".ff");
                Utils.saveEncryptedFile(selectedFile, output, pass, getApplicationContext());
            } else {
                String out = sFile;
                if (out.endsWith(".ff")) {
                    out = out.substring(0, out.length() - 3);
                }
                File output = new File(out);
                Utils.saveDecryptedFile(selectedFile, output, pass, getApplicationContext());
            }
        }
    }

    public void saveAs(View view) {
        final String pass = getPass();
        if (pass != null) {
            FileChooserDialog dialog = new FileChooserDialog(this);
            dialog.setCanCreateFiles(true);
            dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
                public void onFileSelected(Dialog source, File file) {
                    source.dismiss();
                }

                public void onFileSelected(Dialog source, File folder, String name) {
                    source.dismiss();
                    try {
                        File output = new File(folder, name+ ".ff");
                        if (!output.exists()) {
                            output.createNewFile();
                        }
                        if (mode == 0) {
                            Utils.saveEncryptedFile(selectedFile, output, pass, getApplicationContext());
                        } else {
                            Utils.saveDecryptedFile(selectedFile, output, pass, getApplicationContext());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            dialog.show();
        }
    }


    public void openFile(View view) {
        final Button b = (Button) view;
        FileChooserDialog dialog = new FileChooserDialog(this);
        dialog.show();
        dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
            public void onFileSelected(Dialog source, File file) {
                selectedFile = file;
                //TextView selectedFileTextView = (TextView) findViewById(R.id.file_name);
                //selectedFileTextView.setText(file.getName());

                b.setText(file.getName());
                LinearLayout ll = (LinearLayout) findViewById(R.id.saveLL);
                ll.setVisibility(View.VISIBLE);
                if(mode==1){
                    TextView tv1 =(TextView) findViewById(R.id.pass2);
                    EditText et1= (EditText) findViewById(R.id.pass_field2);
                    tv1.setVisibility(View.GONE);
                    et1.setVisibility(View.GONE);
                }else{
                    TextView tv1 =(TextView) findViewById(R.id.pass2);
                    EditText et1= (EditText) findViewById(R.id.pass_field2);
                    tv1.setVisibility(View.VISIBLE);
                    et1.setVisibility(View.VISIBLE);
                }
                source.dismiss();
            }

            public void onFileSelected(Dialog source, File folder, String name) {
                source.dismiss();
            }
        });
    }


    public void encryptFile(View view) {
        showFragment(new SaveFragment());
        mode = 0;
        getSupportActionBar().setTitle("Encrypt File");
    }

    public void decryptFile(View view) {
        showFragment(new SaveFragment());
        mode = 1;
        getSupportActionBar().setTitle("Decrypt File");
    }

    public void showFragment(Fragment f) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, f);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static class PlaceholderFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ((MyActionBarActivity) getActivity()).getSupportActionBar().setTitle("");
            return rootView;
        }
    }

    public static class SaveFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_save, container, false);
            return rootView;
        }
    }

    public static class VaultPass1Fragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pass1, container, false);
            return rootView;
        }
    }

    public static class VaultPass2Fragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pass2, container, false);
            return rootView;
        }
    }

}
