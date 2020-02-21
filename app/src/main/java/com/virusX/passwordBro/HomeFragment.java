package com.virusX.passwordBro;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.parse.ParseUser;
import java.util.ArrayList;
import es.dmoral.toasty.Toasty;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ArrayList<String> nameList, password;
    private ArrayList<Integer> IDList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView passwordList;
    private DatabaseHelper databaseHelper;
    private Cursor data;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        passwordList = view.findViewById(R.id.passwordList);
        TextView homeSubtitle = view.findViewById(R.id.homeSubtitle);

        nameList = new ArrayList<>();
        IDList = new ArrayList<>();
        password = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_list_item_1, nameList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.WHITE);
                return view;
            }
        };

        passwordList.setOnItemClickListener(HomeFragment.this);
        passwordList.setOnItemLongClickListener(HomeFragment.this);

        addDataToList();

        if(nameList.size() == 0) {
            homeSubtitle.setText(R.string.home_subtitle_2);
        } else {
            homeSubtitle.setText(R.string.home_subtitle);
        }

        if(ParseUser.getCurrentUser() != null) {
            DataBackupHelper helper = new DataBackupHelper(nameList, password, getContext());
            helper.backupData();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAccount();
        passwordList.setAdapter(null);
        nameList.clear();
        password.clear();
        IDList.clear();
        addDataToList();
    }

    private void addDataToList() {
        databaseHelper = new DatabaseHelper(getContext());
        data = databaseHelper.getData();
        try {
            while (data.moveToNext()) {
                IDList.add(data.getInt(0));
                nameList.add(data.getString(1));
                password.add(data.getString(2));
            }
            passwordList.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAccount() {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean backupPref = pref.getBoolean("backup", false);
        ParseUser user = ParseUser.getCurrentUser();
        if(backupPref && user == null) {
            final PrettyDialog prettyDialog = new PrettyDialog(getContext());
            prettyDialog.setTitle("Alert!!")
                    .setIcon(R.drawable.ic_error)
                    .setMessage("Cannot backup. No user Account Found..")
                    .addButton("Add Account",
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_green,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    Intent intent = new Intent(getContext(), AddAccountActivity.class);
                                    startActivity(intent);
                                    prettyDialog.dismiss();
                                }
                            })
                    .addButton("Disable Backup",
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_red,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putBoolean("backup", false);
                                    editor.apply();
                                    prettyDialog.dismiss();
                                }
                            }).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), EditActivity.class);
        intent.putExtra("name", nameList.get(position));
        intent.putExtra("password", password.get(position));
        intent.putExtra("position", IDList.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        ClipboardManager clipboard = (ClipboardManager)
                getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        String pass = password.get(position);

        ClipData clip = ClipData.newPlainText("Copied Password", pass);
        try {
            clipboard.setPrimaryClip(clip);
            Toasty.info(getContext(), "Password Copied to Clipboard",
                    Toasty.LENGTH_SHORT, true).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}