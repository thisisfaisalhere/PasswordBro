package com.virusX.passwordBro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import es.dmoral.toasty.Toasty;

public class GenerateActivity extends AppCompatActivity {

    private TextView generatedPass;
    private int strength;
    private boolean checked = false;
    private String password = "";
    private DatabaseHelper databaseHelper;
    private EditText serviceNameEdt;
    private static final String med = "med_length";
    private static final String easy = "easy_length";
    private static final String hard = "hard_length";
    private int easyLen;
    private int medLen;
    private int hardLen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        try {
//            getSupportActionBar().hide();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        setContentView(R.layout.activity_generate);

        RadioGroup strengthRadioGroup = findViewById(R.id.strengthRadioGroup);
        final Button generateBtn = findViewById(R.id.generateButton);
        generatedPass = findViewById(R.id.generatedPass);
        final ImageButton copyBtn = findViewById(R.id.copyBtn);
        serviceNameEdt = findViewById(R.id.serviceNameEdt);
        final Button saveBtn = findViewById(R.id.saveBtn);

        final ProgressDialog progressDialog = new ProgressDialog(GenerateActivity.this);
        final Handler handler = new Handler();

        length();

        strengthRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.easyRadioBtn:
                        strength = 1;
                        checked = true;
                        Toast.makeText(GenerateActivity.this,
                                "Strength Level: Easy", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.modRadioBtn:
                        strength = 2;
                        checked = true;
                        Toast.makeText(GenerateActivity.this,
                                "Strength Level: Medium", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.hardRadioBtn:
                        strength = 3;
                        checked = true;
                        Toast.makeText(GenerateActivity.this,
                                "Strength Level: Tough", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        strength = -1;
                }
            }
        });

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Generating..");
                generatedPass.setText("");
                copyBtn.setVisibility(View.GONE);
                serviceNameEdt.setVisibility(View.GONE);
                saveBtn.setVisibility(View.GONE);
                if(!checked) {
                    Toasty.info(GenerateActivity.this, "Choose strength first",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    progressDialog.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Generate generate = new Generate(strength, easyLen, medLen, hardLen);
                            password = generate.generate();
                            generatedPass.setText(password);
                            copyBtn.setVisibility(View.VISIBLE);
                            serviceNameEdt.setVisibility(View.VISIBLE);
                            saveBtn.setVisibility(View.VISIBLE);
                            generateBtn.setText(getString(R.string.generate_btn_2));
                            progressDialog.dismiss();
                        }
                    }, 500);
                }
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Password", password);
                try {
                    clipboard.setPrimaryClip(clip);
                    Toasty.info(GenerateActivity.this, "Password Copied to Clipboard",
                            Toasty.LENGTH_SHORT, true).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        serviceNameEdt.setOnKeyListener(new View.OnKeyListener() {
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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = serviceNameEdt.getText().toString();
                if(name.equals("")) {
                    Toasty.info(GenerateActivity.this,
                            "Enter the name of Service for future Reference",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    databaseHelper = new DatabaseHelper(GenerateActivity.this);
                    boolean result = databaseHelper.addData(name, password);
                    if(result) {
                        Toasty.success(GenerateActivity.this,
                                "Data Saved Successfully", Toasty.LENGTH_SHORT,
                                true).show();
                    } else {
                        Toasty.error(GenerateActivity.this,
                                "An error occurred while saving Data", Toasty.LENGTH_SHORT,
                                true).show();
                    }
                    finish();
                }
            }
        });
    }

    private boolean isNumber(String s){
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }

    private void length() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String easyLenStr = preferences.getString(easy, "8").trim();
        String medLenStr = preferences.getString(med, "8").trim();
        String hardLenStr = preferences.getString(hard, "8").trim();

        if(easyLenStr.equals("")) {
            Toasty.error(this, "Length of password is empty/not valid\nGo to Settings and Enter Valid value",
                    Toasty.LENGTH_LONG, true).show();
            finish();
        } else {
            if(isNumber(easyLenStr)) {
                easyLen = Integer.parseInt(easyLenStr);
            } else {
                Toasty.error(this, "Length of password is empty/not valid\nGo to Settings and Enter Valid value",
                        Toasty.LENGTH_LONG, true).show();
                finish();
            }
        }

        if(medLenStr.equals("")) {
            Toasty.error(this, "Length of password is empty/not valid\nGo to Settings and Enter Valid value",
                    Toasty.LENGTH_LONG, true).show();
            finish();
        } else {
            if(isNumber(medLenStr)) {
                medLen = Integer.parseInt(medLenStr);
            } else {
                Toasty.error(this, "Length of password is empty/not valid\nGo to Settings and Enter Valid value",
                        Toasty.LENGTH_LONG, true).show();
                finish();
            }
        }

        if (hardLenStr.equals("")) {
            Toasty.error(this, "Length of password is empty/not valid\nGo to Settings and Enter Valid value",
                    Toasty.LENGTH_LONG, true).show();
            finish();
        } else {
            if(isNumber(hardLenStr)) {
                hardLen = Integer.parseInt(hardLenStr);
            } else {
                Toasty.error(this, "Length of password is empty/not valid\nGo to Settings and Enter Valid value",
                        Toasty.LENGTH_LONG, true).show();
                finish();
            }
        }
    }
}
