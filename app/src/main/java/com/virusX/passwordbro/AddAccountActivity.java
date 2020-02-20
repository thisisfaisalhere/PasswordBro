package com.virusX.passwordbro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import es.dmoral.toasty.Toasty;

public class AddAccountActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private EditText usernameEdt,passwordEdt;
    private CheckBox checkBox;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        if(ParseUser.getCurrentUser() != null) {
            transitionToNextActivity();
        }

        usernameEdt = findViewById(R.id.usernameEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        checkBox = findViewById(R.id.checkBox);
        button = findViewById(R.id.button);

        passwordEdt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClickButton(button);
                }
                return false;
            }
        });

    }

    public void onClickButton(View button) {
        String username = usernameEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();
        boolean checked = checkBox.isChecked();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Account...");

        if(username.equals("") || password.equals("")) {
            Toasty.error(this, "username/password cannot be empty",
                    Toasty.LENGTH_SHORT ,true).show();
        } else {
            if(checked) {
                try {
                    ParseUser parseUser = new ParseUser();
                    parseUser.setUsername(username);
                    parseUser.setPassword(password);
                    progressDialog.show();
                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toasty.success(AddAccountActivity.this, "Account Added Successfully",
                                        Toasty.LENGTH_SHORT, true).show();
                                transitionToNextActivity();
                            } else {
                                Toasty.error(AddAccountActivity.this, e.getMessage() + "",
                                        Toasty.LENGTH_SHORT, true).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    progressDialog.show();
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(user != null && e == null) {
                                Toasty.success(AddAccountActivity.this, "Account Added Successfully",
                                        Toasty.LENGTH_SHORT, true).show();
                                transitionToNextActivity();
                            } else {
                                Toasty.error(AddAccountActivity.this, e.getMessage() + "",
                                        Toasty.LENGTH_SHORT, true).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void rootLayoutTapped(View view) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void transitionToNextActivity() {
        Intent intent = new Intent(this, AccountDetailsActivity.class);
        startActivity(intent);
        finish();
    }
}
