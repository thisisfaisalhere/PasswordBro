package com.virusX.passwordBro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

class DataBackupHelper {
    private Context context;
    private static final String TAG = "passwordBro";
    @SuppressLint("SdCardPath")
    private static final String dirPath = "/data/data/" + MainActivity.PACKAGE_NAME + "/backup";


    DataBackupHelper(Context context) {
        this.context = context;
    }

    boolean createBackup() {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        File exportDir = new File(dirPath);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, "db.csv");
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
            Cursor data = databaseHelper.getData();
            ArrayList<String> dataList = new ArrayList<>();
            try {
                while(data.moveToNext()) {
                    dataList.add(data.getString(1));
                    dataList.add(data.getString(2));
                    dataList.add(data.getString(3));
                    csvWriter.writeNext(dataList);
                    dataList.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            csvWriter.flush();
            csvWriter.close();
            data.close();
            Log.d(TAG, "createBackup: done");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    void backupData() {
        String fileContent = getFileContent();
        byte[] data = fileContent.getBytes();
        String fileName = "backup-" + Calendar.getInstance().getTime() + ".csv";
        ParseFile file = new ParseFile(fileName, data);
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put("backupData", file);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toasty.success(context, "Backup Successful",
                            Toasty.LENGTH_SHORT, true).show();
                    Log.d(TAG, "done: backup successful");
                } else {
                    Toasty.error(context, "An Error occurred",
                            Toasty.LENGTH_LONG, true).show();
                }
            }
        });
    }

    private String getFileContent() {
        String lineStr;
        StringBuilder fileContent = new StringBuilder();
        String file = dirPath + "/db.csv";

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((lineStr = bufferedReader.readLine()) != null) {
                fileContent.append(lineStr).append("\n");
            }
            bufferedReader.close();
            Log.d(TAG, "getFileContent: " + "Successfully got the content of '" + file + "'");
        } catch(FileNotFoundException ex) {
            Log.d(TAG, "getFileContent: " + "Unable to open a file '" + file + "'");
        } catch(IOException ex) {
            Log.d(TAG, "getFileContent: " + "Error reading a file '" + file + "'");
        }
        return fileContent.toString();
    }

    void retrieveData() {

    }

    void deleteBackup() {
    }

    void deleteAccount() {
    }
}
