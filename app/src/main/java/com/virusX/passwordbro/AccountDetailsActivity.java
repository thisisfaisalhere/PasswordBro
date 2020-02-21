package com.virusX.passwordbro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

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
                helper.deleteBackup();
            }
        });

        delAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.deleteAccount();
                finish();
            }
        });

        fillQA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountDetailsActivity.this, QnActivity.class);
                startActivity(intent);
            }
        });


    }
}
