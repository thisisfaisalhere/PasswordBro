package com.virusX.passwordbro;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

class DataBackupHelper {

    private ArrayList<String> nameList, passwordList;
    private Context context;
    private final String COL1 = "services";
    private final String COL2 = "key";

    DataBackupHelper(ArrayList<String> nameList, ArrayList<String> passwordList, Context context) {
        this.nameList = nameList;
        this.passwordList = passwordList;
        this.context = context;
    }

    DataBackupHelper(Context context) {
        this.context = context;
    }

    void backupData() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put(COL1, nameList);
        parseUser.put(COL2, passwordList);

        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toasty.success(context, "Backup Successful",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(context, e.getMessage() + "",
                            Toasty.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    void retrieveData() {
        nameList = new ArrayList<>();
        passwordList = new ArrayList<>();
        ParseUser parseUser = ParseUser.getCurrentUser();
        if(parseUser.getList(COL1) != null) {
            try {
                nameList.addAll(parseUser.<String>getList(COL1));
                passwordList.addAll(parseUser.<String>getList(COL2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(nameList.size() > 0 && passwordList.size() > 0) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            databaseHelper.matchDataSet(nameList, passwordList);
        }
    }
}
