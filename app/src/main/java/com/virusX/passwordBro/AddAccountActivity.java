package com.virusX.passwordBro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class AddAccountActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private TextView forgotPasswordTxt;
    private EditText usernameEdt,passwordEdt, confirmPasswordEdt, emailEdt;
    private CheckBox checkBox;
    private boolean checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        if(ParseUser.getCurrentUser() != null) {
            transitionToNextActivity();
        }

        usernameEdt = findViewById(R.id.usernameEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        confirmPasswordEdt = findViewById(R.id.confirmPasswordEdt);
        checkBox = findViewById(R.id.checkBox);
        forgotPasswordTxt = findViewById(R.id.forgotPasswordTxt);
        emailEdt = findViewById(R.id.emailEdt);

        forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAccountActivity.this, GetUsernameActivity.class);
                startActivity(intent);
            }
        });
    }

    public void checked(View view) {
        checked = checkBox.isChecked();
        if(checked) {
            emailEdt.setVisibility(View.VISIBLE);
            confirmPasswordEdt.setVisibility(View.VISIBLE);
            forgotPasswordTxt.setVisibility(View.GONE);
            confirmPasswordEdt.setOnKeyListener(new View.OnKeyListener() {
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
        } else {
            confirmPasswordEdt.setVisibility(View.GONE);
            emailEdt.setVisibility(View.GONE);
            forgotPasswordTxt.setVisibility(View.VISIBLE);
            passwordEdt.setOnKeyListener(new View.OnKeyListener() {
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
    }

    public void onClickButton(View button) {
        String username = usernameEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();
        String mail = emailEdt.getText().toString();
        String confirmPassword = confirmPasswordEdt.getText().toString().trim();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Account...");

        if(username.equals("") || password.equals("")) {
            Toasty.error(this, "username/password cannot be empty",
                    Toasty.LENGTH_SHORT ,true).show();
        } else {
            if(checked) {
                if(confirmPassword.equals("")) {
                    Toasty.error(this, "confirm password cannot be empty",
                            Toasty.LENGTH_SHORT ,true).show();
                } else {
                    if(!confirmPassword.equals(password)) {
                        Toasty.error(this, "Password and confirm password did not matched",
                                Toasty.LENGTH_SHORT ,true).show();
                    } else if(!mail.contains("@")) {
                        Toasty.error(this, "Invalid email",
                                Toasty.LENGTH_SHORT ,true).show();
                    } else {
                        try {
                            ParseUser parseUser = new ParseUser();
                            parseUser.setEmail(mail);
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
                    }
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
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus())
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
