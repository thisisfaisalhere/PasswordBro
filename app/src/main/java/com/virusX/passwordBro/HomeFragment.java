package com.virusX.passwordBro;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.Objects;
import es.dmoral.toasty.Toasty;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class HomeFragment extends Fragment
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private Intent intent;
    private ArrayList<String> nameList, passwordList, usernameList;
    private ArrayList<Integer> IDList;
    private ArrayAdapter arrayAdapter;
    private ListView listView;
    private FloatingActionMenu fab;
    private FloatingActionButton fabGenerate,fabAdd;
    private View backgroundDimmer;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Objects.requireNonNull(getActivity()).setTitle("Home");

        listView = view.findViewById(R.id.listView);
        fab = view.findViewById(R.id.fab);
        fabGenerate = view.findViewById(R.id.fabGenerate);
        fabAdd = view.findViewById(R.id.fabAdd);
        backgroundDimmer = view.findViewById(R.id.background_dimmer);
        TextView homeSubtitle = view.findViewById(R.id.homeSubtitle);

        IDList = new ArrayList<>();
        nameList = new ArrayList<>();
        passwordList = new ArrayList<>();
        usernameList = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, nameList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(HomeFragment.this);
        listView.setOnItemLongClickListener(HomeFragment.this);

        addDataToList();

        if(nameList.size() == 0) {
            homeSubtitle.setText(R.string.home_subtitle_2);
        } else {
            homeSubtitle.setText(R.string.home_subtitle);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getContext(), GenerateActivity.class);
                startActivity(intent);
                fab.toggle(true);
            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getContext(), EditActivity.class);
                startActivity(intent);
                fab.toggle(true);
            }
        });
        fab.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if(opened) {
                    backgroundDimmer.setVisibility(View.VISIBLE);
                    backgroundDimmer.animate().alpha(1).setDuration(300);
                } else {
                    backgroundDimmer.animate().alpha(0).setDuration(300);
                    backgroundDimmer.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAccount();
        listView.setAdapter(arrayAdapter);
        nameList.clear();
        passwordList.clear();
        IDList.clear();
        usernameList.clear();
        addDataToList();
    }

    private void checkAccount() {
        final SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
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

    private void addDataToList() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        Cursor data = databaseHelper.getData();
        try {
            while (data.moveToNext()) {
                IDList.add(data.getInt(0));
                nameList.add(data.getString(1));
                passwordList.add(data.getString(2));
                usernameList.add(data.getString(3));
            }
            arrayAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), EditActivity.class);
        intent.putExtra("name", nameList.get(position));
        intent.putExtra("password", passwordList.get(position));
        intent.putExtra("username", usernameList.get(position));
        intent.putExtra("position", IDList.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ClipboardManager clipboard = (ClipboardManager)
                Objects.requireNonNull(getContext()).getSystemService(Context.CLIPBOARD_SERVICE);

        String pass = passwordList.get(position);

        ClipData clip = ClipData.newPlainText("Copied Password", pass);
        try {
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            Toasty.info(getContext(), "Password Copied to Clipboard",
                    Toasty.LENGTH_SHORT, true).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
