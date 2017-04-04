package com.softberry.fendoff;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

import ar.com.daidalos.afiledialog.FileChooserDialog;

/**
 * Created by umarovr on 1/21/15.
 */
public class VaultActivity extends MyActionBarActivity {
    public static ArrayList list;
    private ListView listView;
    private String vaultPass;
    private File vault;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);


        listView = (ListView) findViewById(R.id.vaultList);
        Category[] values = new Category[]{
                new Category("General"),
                new Category("Emails"),
                new Category("Contacts"),
                new Category("Credit Cards"),
        };
        if (list == null) {
            list = new ArrayList<Category>(Arrays.asList(values));
        }
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    EntriesFragment esf = new EntriesFragment();
                    EntriesFragment.category = ((Category) list.get(position));
                    showFragment(esf);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        });
        vaultPass = getIntent().getStringExtra("pass");
        vault = new File(getIntent().getStringExtra("vault"));
        vaultPass = getIntent().getStringExtra("pass");

        getSupportActionBar().setTitle("Categories");
    }


    public void createCategory(View view) {
        EditText et = (EditText) findViewById(R.id.new_category_name);
        Category cat = new Category(et.getText().toString());
        list.add(cat);
        pop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vault, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("FendOff is an application to encrypt your files using an original encryption (VSEM 1.0). Developed by Victor Solovyev: victorsolov@gmail.com");
            dlgAlert.setTitle("FendOff");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dlgAlert.create().show();
            return true;
        } else if (id == R.id.action_add) {
            if (findViewById(R.id.list_entries) != null) {
                if (EntriesFragment.category != null) {
                    NewEntryFragment.fields = EntriesFragment.category.fields;
                }
                showFragment(new NewEntryFragment());
            } else {
                showFragment(new NewCategoryFragment());
            }

        } else if (id == R.id.action_change_password) {
            showFragment(new NewPassFragment());
        } else if (id == R.id.action_save_exit) {
            finish();
        } else if (id == R.id.action_delete_category) {
            if (EntriesFragment.category != null) {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.action_delete_category + "?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (list.contains(EntriesFragment.category)) {
                                    pop();
                                    list.remove(EntriesFragment.category);
                                    EntriesFragment.category = null;
                                    adapter.notifyDataSetChanged();
                                }
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            } else {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Choose category first!");
                dlgAlert.setTitle("FendOff");
                dlgAlert.setCancelable(true);
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dlgAlert.create().show();
            }
        } else if (id == R.id.action_export) {
            FileChooserDialog dialog = new FileChooserDialog(this);
            dialog.setCanCreateFiles(true);
            dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
                public void onFileSelected(Dialog source, File file) {
                    source.dismiss();
                }

                public void onFileSelected(Dialog source, File folder, String name) {
                    source.dismiss();
                    try {
                        File output = new File(folder, name);
                        String p = output.toString();
                        if (!p.endsWith(".txt")) {
                            p = p + ".txt";
                        }
                        FileOutputStream stream = new FileOutputStream(new File(p));
                        try {
                            stream.write(vaultToString().getBytes());
                        } finally {
                            stream.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            dialog.show();
        } else if (id == R.id.action_import) {
            FileChooserDialog dialog = new FileChooserDialog(this);
            dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
                public void onFileSelected(Dialog source, File file) {
                    source.dismiss();
                    Pattern p = Pattern.compile("\\s{1}.*");
                    Pattern p2 = Pattern.compile("\\s{2}.*");
                    Pattern p4 = Pattern.compile("\\s{4}.*");
                    Pattern p8 = Pattern.compile("\\s{8}.*");
                    try {
                        Scanner scanner = new Scanner(file);
                        scanner.useDelimiter(Pattern.compile("[\\r\\n]+"));
                        ArrayList<Category> newList = new ArrayList<Category>();
                        while (scanner.hasNextLine()) {
                            String lineC = scanner.nextLine();
                            Category c = new Category(lineC);
                            while (scanner.hasNextLine() && scanner.hasNext(p2)) {
                                String lineE = scanner.nextLine();
                                Entry e = new Entry(lineE.trim());
                                while (scanner.hasNextLine() && scanner.hasNext(p4)) {
                                    String lineN = scanner.nextLine().trim();
                                    String lineF = scanner.nextLine().trim();
                                    while (scanner.hasNextLine() && scanner.hasNext(p8)) {
                                        lineF += "\n" + scanner.nextLine().trim();
                                    }
                                    Field f = new Field(lineN, lineF);
                                    e.fields.add(f);
                                }
                                c.entries.add(e);
                            }
                            newList.add(c);
                        }
                        list.clear();
                        for (Category c : newList) {
                            list.add(c);
                        }
                        if (EntriesFragment.category != null) {
                            EntriesFragment.category = null;
                            pop();
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                public void onFileSelected(Dialog source, File folder, String name) {
                    source.dismiss();

                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private String vaultToString() {
        String v = "";
        for (int i = 0; i < list.size(); i++) {
            Category c = (Category) list.get(i);
            v += c.name + "\n";
            for (int j = 0; j < c.entries.size(); j++) {
                Entry e = c.entries.get(j);
                v += "  " + e.name + "\n";
                for (int k = 0; k < e.fields.size(); k++) {
                    v += "    " + e.fields.get(k).fieldName + "\n";
                    v += "        " + e.fields.get(k).fieldContent + "\n";
                }
            }
        }
        return v;
    }

    public void changePassword(View view) {
        EditText et1 = (EditText) findViewById(R.id.old_password);
        if (et1.getText().toString().equals(vaultPass)) {
            EditText et2 = (EditText) findViewById(R.id.new_password);
            String newPass = et2.getText().toString();
            if (newPass.length() > 0) {
                vaultPass = newPass;
                pop();
            } else {
                Toast.makeText(getApplicationContext(), "New password cannot be empty",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Input correct current password",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void saveList() {
        Utils.saveEncryptedFile(list, vault, vaultPass, null);
    }

    public void createEntry(View view) {
        try {
            TextView tv1 = (TextView) findViewById(R.id.new_entry_name);
            String newName = tv1.getText().toString();
            Entry entry = new Entry(newName);
            LinearLayout ll = (LinearLayout) findViewById(R.id.layout_new_entry);
            for (int i = 2; i < ll.getChildCount(); i += 2) {
                View v = ll.getChildAt(i);
                if (v instanceof RelativeLayout) {
                    break;
                }
                String fName, fContent;
                TextView tv = (TextView) v;
                fName = tv.getText().toString();
                EditText et = (EditText) ll.getChildAt(i + 1);
                fContent = et.getText().toString();
                Field field = new Field(fName, fContent);
                entry.fields.add(field);
            }
            if (EntriesFragment.category.entries.size() == 0) {
                EntriesFragment.category.fields = entry.fields;
            }
            EntriesFragment.category.entries.add(entry);
            EntriesFragment.adapter.notifyDataSetChanged();
            pop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addField(String name) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.layout_new_entry);
        int index = ll.getChildCount() - 1;
        TextView tv = new TextView(this);
        tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        tv.setTextColor(Color.GRAY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 0, 0, 0);
        tv.setLayoutParams(params);
        tv.setText(name);
        ll.addView(tv, index);
        EditText et = new EditText(this);
        ll.addView(et, index + 1);
    }

    public void addField(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New field");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addField(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }

    public void deleteEntry(View view) {
        TextView tv = (TextView) findViewById(R.id.entry_name);
        int position = (int) tv.getTag();
        EntriesFragment.category.entries.remove(position);
        EntriesFragment.adapter.notifyDataSetChanged();
        pop();
    }

    public void saveEntry(View view) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.layout_entry);
        for (int i = 2; i < ll.getChildCount(); i += 2) {
            View v = ll.getChildAt(i);
            if (!(v instanceof EditText)) {
                break;
            }
            EditText et = (EditText) v;
            EntryFragment.entry.fields.get(i / 2 - 1).fieldContent = et.getText().toString();
        }
        pop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveList();
    }

    public void showFragment(Fragment f) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, f);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void pop() {
        getSupportFragmentManager().popBackStack();
    }

    private void openEntry(EntryFragment ef) {
        showFragment(ef);
    }

    public static class EntryFragment extends Fragment {
        public static Entry entry;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_entry, container, false);
            try {
                int pos = getArguments().getInt("position");
                TextView tv1 = (TextView) rootView.findViewById(R.id.entry_name);
                tv1.setTag(pos);
                tv1.setText(entry.name);
                int index = 1;
                LinearLayout ll = ((LinearLayout) tv1.getParent());
                for (Field f : entry.fields) {
                    TextView tv = new TextView(getActivity());
                    tv.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    tv.setTextColor(Color.GRAY);
                    params.setMargins(10, 0, 0, 10);
                    tv.setLayoutParams(params);
                    tv.setText(f.fieldName);
                    ll.addView(tv, index);
                    EditText et = new EditText(getActivity());
                    et.setText(f.fieldContent);
                    et.setInputType(InputType.TYPE_CLASS_TEXT | f.inputType);
                    ll.addView(et, index + 1);
                    index += 2;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return rootView;
        }
    }

    public static class NewEntryFragment extends Fragment {
        public static ArrayList<Field> fields;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_entry, container, false);
            if (fields != null) {
                LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.layout_new_entry);
                int index = 2;
                for (Field f : fields) {
                    TextView tv = new TextView(getActivity());
                    tv.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
                    tv.setTextColor(Color.GRAY);
                    tv.setText(f.fieldName);
                    ll.addView(tv, index);
                    EditText et = new EditText(getActivity());
                    et.setInputType(InputType.TYPE_CLASS_TEXT | f.inputType);
                    //et.setText(f.fieldContent);
                    ll.addView(et, index + 1);
                    index += 2;
                }
            }
            return rootView;
        }
    }

    public static class NewCategoryFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_category, container, false);
            return rootView;
        }


    }

    public static class NewPassFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pass3, container, false);
            return rootView;
        }


    }

    public static class EntriesFragment extends Fragment {
        public static Category category;
        public static ListView listView;
        public static ArrayAdapter<String> adapter;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_entries, container, false);
            listView = (ListView) rootView.findViewById(R.id.list_entries);

            adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, (ArrayList) category.entries);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Entry itemValue = (Entry) listView.getItemAtPosition(position);
                    Bundle bundle = new Bundle();
                    EntryFragment.entry = itemValue;
                    bundle.putInt("position", position);
                    EntryFragment ef = new EntryFragment();
                    ef.setArguments(bundle);
                    ((VaultActivity) getActivity()).openEntry(ef);
                }

            });
            ((VaultActivity) getActivity()).getSupportActionBar().setTitle(category.name);
            return rootView;
        }


    }
}

class Entry implements Serializable {
    public String name;
    public ArrayList<Field> fields;

    public Entry(String name) {
        this.name = name;
        fields = new ArrayList<>();
    }


    public String toString() {
        return name;
    }

}

class Field implements Serializable {
    public String fieldName;
    public String fieldContent;
    public int inputType;

    public Field(String name, String content) {
        fieldName = name;
        fieldContent = content;
    }

    public Field(String name, int type) {
        fieldName = name;
        inputType = type;
    }
}

class Category implements Serializable {
    public ArrayList<Entry> entries;
    public String name;
    public ArrayList<Field> fields;

    public Category(String name) {
        this.name = name;
        entries = new ArrayList<>();
    }

    public String toString() {
        return name;
    }
}
