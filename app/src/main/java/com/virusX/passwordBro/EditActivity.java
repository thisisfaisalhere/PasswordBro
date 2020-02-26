package com.virusX.passwordBro;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
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
        position = receivedIntentData.getIntExtra("position", -1);

        setTitle(receivedName + "\'s details");

        editTextName = findViewById(R.id.editTextName);
        editTextPass = findViewById(R.id.editTextPass);
        editTextUser = findViewById(R.id.editTextUsername);
        ImageButton buttonCopy = findViewById(R.id.buttonCopy);
        ImageButton buttonShow = findViewById(R.id.buttonShow);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        Button buttonDelete = findViewById(R.id.buttonDelete);
        progressBar = findViewById(R.id.editPgBar);

        databaseHelper = new DatabaseHelper(this);
        handler = new Handler();

        editTextName.setText(receivedName);
        editTextPass.setText(receivedPass);
        editTextUser.setText(receivedUsername);

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
                progressBar.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String name = editTextName.getText().toString();
                        String pass = editTextPass.getText().toString();
                        String user = editTextUser.getText().toString();
                        databaseHelper.updateDate(position, name, pass, user);
                        Toasty.success(EditActivity.this,
                                "Updated successfully", Toasty.LENGTH_SHORT, true).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);

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
