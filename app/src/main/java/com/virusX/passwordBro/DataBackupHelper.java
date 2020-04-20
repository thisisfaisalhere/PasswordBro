package com.virusX.passwordBro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

class DataBackupHelper {
    private Context context;
    private static final String TAG = "passwordBro";
    @SuppressLint("SdCardPath")
    private static final String dirPath = "/data/data/" + MainActivity.PACKAGE_NAME + "/backup";
    private String backupFileName, retrieveFileName = "retrievedFile.csv";
    private static final String pathSeparator = "/";
    private ParseUser parseUser;
    private ProgressBar progressBar;
    private TextView textView;

    @SuppressLint("SimpleDateFormat")
    DataBackupHelper(Context context) {
        this.context = context;
        Date calendar = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String date = dateFormat.format(calendar);
        backupFileName = "backup_" + date +".csv";
        parseUser = ParseUser.getCurrentUser();
        textView = ((Activity)context).findViewById(R.id.acFrgmntTxt);
        progressBar = ((Activity)context).findViewById(R.id.acFrgmntPBar);
    }

    boolean createBackup() {
        progressBar.setVisibility(View.VISIBLE);
        textView.setText(R.string.creating_backup);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        File exportDir = new File(dirPath);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, backupFileName);
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
            textView.setText(R.string.backup_created);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    void backupData() {
        textView.setText(R.string.sending_data);
        String fileContent = getFileContent(dirPath + pathSeparator + backupFileName);
        byte[] data = fileContent.getBytes();
        ParseFile file = new ParseFile(backupFileName, data);
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
                progressBar.setVisibility(View.GONE);
                textView.setText("");
            }
        });

    }

    private String getFileContent(String fileLocation) {
        String lineStr;
        StringBuilder fileContent = new StringBuilder();

        try {
            FileReader fileReader = new FileReader(fileLocation);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((lineStr = bufferedReader.readLine()) != null) {
                fileContent.append(lineStr).append("\n");
            }
            bufferedReader.close();
            Log.d(TAG, "getFileContent: " + "Successfully got the content of '" + fileLocation + "'");
        } catch(FileNotFoundException ex) {
            Log.d(TAG, "getFileContent: " + "Unable to open a file '" + fileLocation + "'");
        } catch(IOException ex) {
            Log.d(TAG, "getFileContent: " + "Error reading a file '" + fileLocation + "'");
        }
        return fileContent.toString();
    }

    void retrieveData() {
        progressBar.setVisibility(View.VISIBLE);
        textView.setText(R.string.getting_data);
        ParseFile parseFile = parseUser.getParseFile("backupData");
        assert parseFile != null;
        parseFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if(e == null) {
                    File expoDir = new File(dirPath);
                    if(!expoDir.exists()) expoDir.mkdir();
                    File retrievedFile = new File(expoDir, retrieveFileName);
                    OutputStream outputStream;
                    try {
                        outputStream = new FileOutputStream(retrievedFile);
                        outputStream.write(data);
                        outputStream.close();
                        Log.d(TAG, "done: writing to file complete");
                        textView.setText(R.string.get_data_complete);
                        matchDataSet();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "done: " + e.getMessage());
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void matchDataSet() {
        Log.d(TAG, "matchDataSet: matching init");
        textView.setText(R.string.checking_data);
        String fileContent = getFileContent(dirPath + pathSeparator + retrieveFileName);
        ArrayList<Character> singleData = new ArrayList<>();
        ArrayList<String> dataList = new ArrayList<>();
        for(int i = 0; i < fileContent.length(); i++) {
            char ch = fileContent.charAt(i);
            if(ch != ',' && ch != '\n') {
                singleData.add(ch);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for(Character c: singleData) stringBuilder.append(c);
                dataList.add(stringBuilder.toString());
                singleData.clear();
            }
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        if(helper.matchDataSet(dataList)) {
            Toasty.success(context, "Retrieving Data Successfully Completed",
                    Toasty.LENGTH_SHORT, true).show();
        } else {
            Toasty.info(context, "Nothing to retrieve",
                    Toasty.LENGTH_SHORT, true).show();
        }
        textView.setText("");
    }

    void deleteBackup() {
        parseUser.remove("backupData");
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toasty.info(context, "Successfully Deleted Backup Data",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(context, "An error occurred",
                            Toasty.LENGTH_SHORT, true).show();
                    Log.d(TAG, "done: " + e.getMessage());
                }
            }
        });
    }

    void deleteAccount() {
        parseUser.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseUser.getCurrentUser();
                    ParseUser.logOut();
                    Toasty.info(context, "Account Deleted.",
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
            int i = 0;
            assert children != null;
            while (i < children.length) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
                i++;
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
