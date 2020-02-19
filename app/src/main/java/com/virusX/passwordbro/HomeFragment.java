package com.virusX.passwordbro;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ArrayList<String> nameList, password;
    private ArrayList<Integer> IDList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView passwordList = view.findViewById(R.id.passwordList);

        nameList = new ArrayList<>();
        IDList = new ArrayList<>();
        password = new ArrayList<>();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_list_item_1, nameList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.WHITE);

                // Generate ListView Item using TextView
                return view;
            }
        };

        passwordList.setOnItemClickListener(HomeFragment.this);
        passwordList.setOnItemLongClickListener(HomeFragment.this);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        final Cursor data = databaseHelper.getData();

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
        return view;
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