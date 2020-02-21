package com.virusX.passwordbro;

import android.content.Context;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

class DataBackupHelper {

    private ArrayList<String> nameList, passwordList;
    private Context context;
    private final String COL1 = "services";
    private final String COL2 = "key";
    private ParseUser parseUser = ParseUser.getCurrentUser();

    DataBackupHelper(ArrayList<String> nameList, ArrayList<String> passwordList, Context context) {
        this.nameList = nameList;
        this.passwordList = passwordList;
        this.context = context;
    }

    DataBackupHelper(Context context) {
        this.context = context;
    }

    void backupData() {
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

    void deleteBackup() {
        //working
    }

    void deleteAccount() {
        ParseUser.getCurrentUser().logOut();
        parseUser.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toasty.info(context, "Account Deleted.",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(context, e.getMessage() + "",
                            Toasty.LENGTH_SHORT, true).show();
                }
            }
        });
    }
}
