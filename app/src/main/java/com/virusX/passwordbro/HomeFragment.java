package com.virusX.passwordbro;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ArrayList<String> nameList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView passwordList = view.findViewById(R.id.passwordList);

        nameList = new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, nameList);

        passwordList.setOnItemClickListener(HomeFragment.this);
        passwordList.setOnItemLongClickListener(HomeFragment.this);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        final Cursor data = databaseHelper.getData();

        try {
            while (data.moveToNext()) {
                nameList.add(data.getString(1));
            }
            passwordList.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ClipboardManager clipboard = (ClipboardManager)
                getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        ArrayList<String> passwordList = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        Cursor data = databaseHelper.getData();

        try {
            while (data.moveToNext()) {
                passwordList.add(data.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String pass = passwordList.get(position);

        ClipData clip = ClipData.newPlainText("Copied Password", pass);
        try {
            clipboard.setPrimaryClip(clip);
            Toasty.info(getContext(), "Password Copied to Clipboard",
                    Toasty.LENGTH_SHORT, true).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {



        return false;
    }
}