package com.virusX.passwordBro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import es.dmoral.toasty.Toasty;

public class GetUsernameActivity extends AppCompatActivity {

    private EditText usernameEdt, emailEdt;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_username);

        setTitle("Forgot Password");

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
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                                .getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
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
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("backupAns");
            parseQuery.whereEqualTo("username", username);
            progressBar.setVisibility(View.VISIBLE);
            parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(object != null & e ==  null) {
                        Intent intent = new Intent(GetUsernameActivity.this, QnActivity.class);
                        intent.putExtra("fromAddAcActivity", true );
                        intent.putExtra("username", username);
                        intent.putExtra("email", mail);
                        startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toasty.error(GetUsernameActivity.this, "Cannot find Backup Answers for " + username,
                                Toasty.LENGTH_SHORT, true).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public void getLayoutTapped(View view) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
