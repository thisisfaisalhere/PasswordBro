package com.virusX.passwordBro;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.parse.ParseUser;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class AccountDetailsActivity extends AppCompatActivity {

    private DataBackupHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_details);

        setTitle("Account Details");

        Button restoreBackup = findViewById(R.id.restoreBackupBtn);
        Button deleteBackup = findViewById(R.id.deleteBackupBtn);
        Button fillQA = findViewById(R.id.fill_q_btn);
        Button delAccount = findViewById(R.id.delAccountBtn);
        TextView ac_details = findViewById(R.id.ac_details_txt);
        Button logoutBtn = findViewById(R.id.logoutBtn);
        Button backupBtn = findViewById(R.id.backupBtn);

        helper = new DataBackupHelper(this);

        String text = "Nickname: " + ParseUser.getCurrentUser().getUsername();

        ac_details.setText(text);

        restoreBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.retrieveData();
            }
        });

        deleteBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PrettyDialog prettyDialog = new PrettyDialog(AccountDetailsActivity.this);
                prettyDialog.setIcon(R.drawable.ic_error)
                        .setTitle("Alert")
                        .setMessage(getString(R.string.delete))
                        .addButton("Delete Backup",
                                R.color.pdlg_color_white,
                                R.color.pdlg_color_red,
                                new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        helper.deleteBackup();
                                        prettyDialog.dismiss();
                                    }
                                })
                        .addButton("Cancel",
                                R.color.pdlg_color_white,
                                R.color.pdlg_color_green,
                                new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        prettyDialog.dismiss();
                                    }
                                }).show();
            }
        });

        backupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = new DatabaseHelper(AccountDetailsActivity.this);
                Cursor data = databaseHelper.getData();
                ArrayList<String> nameList = new ArrayList<>();
                ArrayList<String> keyList = new ArrayList<>();
                try {
                    while(data.moveToNext()) {
                        nameList.add(data.getString(1));
                        keyList.add(data.getString(2));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    DataBackupHelper dataBackupHelper =
                            new DataBackupHelper(nameList, keyList, AccountDetailsActivity.this);
                    dataBackupHelper.backupData();
                }
            }
        });

        fillQA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountDetailsActivity.this, QnActivity.class);
                intent.putExtra("fromAddAcActivity", false);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Toasty.info(AccountDetailsActivity.this, "User Logged out",
                        Toasty.LENGTH_SHORT, true).show();
                finish();
            }
        });

        delAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PrettyDialog prettyDialog = new PrettyDialog(AccountDetailsActivity.this);
                prettyDialog.setIcon(R.drawable.ic_error)
                        .setTitle("Alert")
                        .setMessage(getString(R.string.delete_2))
                        .addButton("Delete Account",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                helper.deleteAccount();
                                finish();
                                prettyDialog.dismiss();
                            }
                        })
                        .addButton("Cancel",
                                R.color.pdlg_color_white,
                                R.color.pdlg_color_green,
                                new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        prettyDialog.dismiss();
                                    }
                                }).show();


            }
        });
    }
}
