package com.virusX.passwordBro;

import android.content.Context;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import es.dmoral.toasty.Toasty;

class DataBackupHelper {

    private ArrayList<String> nameList, passwordList, usernameList;
    private Context context;
    private final String COL1 = "services";
    private final String COL2 = "key";
    private final String COL3 = "backupUsername";
    private ParseUser parseUser = ParseUser.getCurrentUser();

    DataBackupHelper(ArrayList<String> nameList, ArrayList<String> passwordList,
                     ArrayList<String> usernameList, Context context) {
        this.nameList = nameList;
        this.passwordList = passwordList;
        this.usernameList = usernameList;
        this.context = context;
    }

    DataBackupHelper(Context context) {
        this.context = context;
    }

    void backupData(boolean showMsg) {
        parseUser.put(COL1, nameList);
        parseUser.put(COL2, passwordList);
        parseUser.put(COL3, usernameList);

        if(showMsg) {
            parseUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toasty.success(context, "Backup Successful",
                                Toasty.LENGTH_SHORT, true).show();
                    } else {
                        Toasty.error(context, e.getMessage() + "",
                                Toasty.LENGTH_SHORT, true).show();
                    }
                }
            });
        } else {
            parseUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toasty.error(context, e.getMessage() + "",
                                Toasty.LENGTH_SHORT, true).show();
                    }
                }
            });
        }
    }

    void retrieveData() {
        nameList = new ArrayList<>();
        passwordList = new ArrayList<>();
        usernameList = new ArrayList<>();
        if (parseUser.getList(COL1) != null) {
            try {
                nameList.addAll(Objects.requireNonNull(parseUser.<String>getList(COL1)));
                passwordList.addAll(Objects.requireNonNull(parseUser.<String>getList(COL2)));
                usernameList.addAll(Objects.requireNonNull(parseUser.<String>getList(COL3)));
                if (nameList.size() == 0) {
                    Toasty.info(context, "No Backup Found",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    nameList.size();
                    if (passwordList.size() > 0) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                        boolean b = databaseHelper.matchDataSet(nameList, passwordList, usernameList);
                        if (b) {
                            Toasty.success(context, "Data Restored Successfully",
                                    Toasty.LENGTH_SHORT, true).show();
                        } else {
                            Toasty.info(context, "Nothing to restore",
                                    Toasty.LENGTH_SHORT, true).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toasty.info(context, "No Backup Found",
                    Toasty.LENGTH_SHORT, true).show();
        }
    }

    void deleteBackup() {
        ArrayList<String> emptyList = new ArrayList<>();
        parseUser.put(COL1, emptyList);
        parseUser.put(COL2, emptyList);
        parseUser.put(COL3, emptyList);

        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toasty.info(context, "Backup Deleted",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(context, e.getMessage() + "",
                            Toasty.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    void deleteAccount() {
        parseUser.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toasty.info(context, "Account Deleted",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(context, e.getMessage() + "",
                            Toasty.LENGTH_SHORT, true).show();
                }
            }
        });
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) throws NullPointerException {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
