package com.virusX.passwordbro;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import es.dmoral.toasty.Toasty;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent receivedIntentData = getIntent();
        String receivedName = receivedIntentData.getStringExtra("name");
        String receivedPass = receivedIntentData.getStringExtra("password");
        final int position = receivedIntentData.getIntExtra("position", -1);

        setTitle(receivedName + "\'s details");

        final EditText editTextName = findViewById(R.id.editTextName);
        final EditText editTextPass = findViewById(R.id.editTextPass);
        ImageButton buttonCopy = findViewById(R.id.buttonCopy);
        final Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        final Button buttonDelete = findViewById(R.id.buttonDelete);

        final DatabaseHelper databaseHelper = new DatabaseHelper(this);
        final Handler handler = new Handler();
        final ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);

        editTextName.setText(receivedName);
        editTextPass.setText(receivedPass);

        final Intent intent = new Intent(EditActivity.this, MainActivity.class);

        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPass.getText().toString();
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Password", password);
                try {
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
                progressDialog.setMessage("Saving...");
                progressDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String name = editTextName.getText().toString();
                        String pass = editTextPass.getText().toString();
                        databaseHelper.updateDate(position, name, pass);
                        Toasty.success(EditActivity.this,
                                "Updated successfully", Toasty.LENGTH_SHORT, true).show();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }, 1000);

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Deleting...");
                progressDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        databaseHelper.deleteData(position);
                        Toasty.success(EditActivity.this,
                                "Deleted successfully", Toasty.LENGTH_SHORT, true).show();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                    }
                }, 1000);

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
