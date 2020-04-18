package com.virusX.passwordBro;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class GetUsernameActivity extends AppCompatActivity {

    private EditText usernameEdt, emailEdt;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_username);

        setTitle("Forgot Password");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        usernameEdt = findViewById(R.id.forgotUsername);
        emailEdt = findViewById(R.id.forgotEmailEdt);
        progressBar = findViewById(R.id.pBar);

        usernameEdt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    try {
                        InputMethodManager inputMethodManager =
                                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        assert inputMethodManager != null;
                        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus())
                                .getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getBtnTapped(View view) {
        final String username = usernameEdt.getText().toString().trim();
        final String mail = emailEdt.getText().toString();
        if(username.equals("") || mail.equals("")) {
            Toasty.error(GetUsernameActivity.this, "Enter Nickname",
                    Toasty.LENGTH_SHORT, true).show();
        } else if (!mail.contains("@")) {
            Toasty.error(GetUsernameActivity.this, "Invalid Email",
                    Toasty.LENGTH_SHORT, true).show();
        } else {
            ParseUser.requestPasswordResetInBackground(mail, new RequestPasswordResetCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        Toasty.success(GetUsernameActivity.this, "Reset Password Email Sent Successfully",
                                Toasty.LENGTH_SHORT, true).show();
                        finish();
                    } else {
                        Toasty.error(GetUsernameActivity.this, e.getMessage() + "",
                                Toasty.LENGTH_SHORT, true).show();
                    }
                }
            });
        }
    }

    public void getLayoutTapped(View view) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus())
                    .getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
