package com.virusX.passwordBro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseUser;

import es.dmoral.toasty.Toasty;

public class SetPasswordActivity extends AppCompatActivity {

    private EditText edt1, edt2;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        setTitle("Change Password");

        Intent receivedData = getIntent();
        username = receivedData.getStringExtra("username");


        edt1 = findViewById(R.id.setNewEdt);
        edt2 = findViewById(R.id.setNewEdt2);
    }

    public void changeBtnTapped(View button) {
        String password = edt1.getText().toString().trim();
        String confirmPassword = edt2.getText().toString().trim();

        if(password.equals("") || confirmPassword.equals("")) {
            Toasty.error(this, "Please enter new Password",
                    Toasty.LENGTH_SHORT, true).show();
        }

        if (password.equals(confirmPassword)) {
            ParseUser parseUser;
        }
    }
}
