package com.virusX.passwordBro;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class EditActivity extends AppCompatActivity {

    private EditText editTextName, editTextPass, editTextUser;
    private DatabaseHelper databaseHelper;
    private Handler handler;
    private ProgressBar progressBar;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent receivedIntentData = getIntent();
        String receivedName = receivedIntentData.getStringExtra("name");
        String receivedPass = receivedIntentData.getStringExtra("password");
        String receivedUsername = receivedIntentData.getStringExtra("username");
        boolean fromMain = receivedIntentData.getBooleanExtra("fromMain", false);
        position = receivedIntentData.getIntExtra("position", -1);

        if(fromMain) {
            setTitle("Add record");
        }else {
            setTitle(receivedName + "\'s details");
        }

        editTextName = findViewById(R.id.editTextName);
        editTextPass = findViewById(R.id.editTextPass);
        editTextUser = findViewById(R.id.editTextUsername);
        ImageButton buttonCopy = findViewById(R.id.buttonCopy);
        ImageButton buttonShow = findViewById(R.id.buttonShow);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        Button buttonDelete = findViewById(R.id.buttonDelete);
        progressBar = findViewById(R.id.editPgBar);

        if(fromMain) {
            buttonDelete.setVisibility(View.GONE);
        }

        databaseHelper = new DatabaseHelper(this);
        handler = new Handler();

        editTextName.setText(receivedName);
        editTextPass.setText(receivedPass);
        editTextUser.setText(receivedUsername);
        final boolean[] check = {true};

        editTextPass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
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

        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD
                        | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        });

        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPass.getText().toString();
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Password", password);
                try {
                    assert clipboard != null;
                    clipboard.setPrimaryClip(clip);
                    Toasty.info(EditActivity.this, "Password Copied to Clipboard",
                            Toasty.LENGTH_SHORT, true).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editTextName.getText().toString();
                final String pass = editTextPass.getText().toString();
                final String user = editTextUser.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                if(name.equals("") || pass.equals("") || user.equals("")) {
                    Toasty.error(EditActivity.this, "Field is empty",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(position == -1 && check[0]) {
                                databaseHelper.addData(name, pass, user);
                                check[0] = false;
                            } else if (position != -1) {
                                databaseHelper.updateDate(position, name, pass, user);
                            }
                            Toasty.success(EditActivity.this,
                                    "Saved successfully", Toasty.LENGTH_SHORT, true).show();
                        }
                    }, 1000);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        databaseHelper.deleteData(position);
                        Toasty.success(EditActivity.this,
                                "Deleted successfully", Toasty.LENGTH_SHORT, true).show();
                    }
                }, 1000);
                progressBar.setVisibility(View.GONE);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void editLayoutTapped(View view) {
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
