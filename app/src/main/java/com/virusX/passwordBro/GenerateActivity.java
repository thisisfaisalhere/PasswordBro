package com.virusX.passwordBro;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class GenerateActivity extends AppCompatActivity{

    private TextView generatedPass;
    private int strength;
    private boolean checked = false;
    private String password = "";
    private DatabaseHelper databaseHelper;
    private EditText serviceNameEdt, usernameHintEdt;
    private ProgressBar progressBar;
    private Handler handler;
    private Button generateBtn;
    private static final String med = "med_length";
    private static final String easy = "easy_length";
    private static final String hard = "hard_length";
    private int easyLen;
    private int medLen;
    private int hardLen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        setTitle("Generate");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RadioGroup strengthRadioGroup = findViewById(R.id.strengthRadioGroup);
        generateBtn = findViewById(R.id.generateButton);
        generatedPass = findViewById(R.id.generatedPass);
        final ImageButton copyBtn = findViewById(R.id.copyBtn);
        serviceNameEdt = findViewById(R.id.serviceNameEdt);
        Button saveBtn = findViewById(R.id.saveBtn);
        usernameHintEdt = findViewById(R.id.usernameHintEdt);
        progressBar = findViewById(R.id.generatePgBar);
        handler = new Handler();

        length();

        strengthRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.easyRadioBtn:
                        strength = 1;
                        checked = true;
                        break;
                    case R.id.modRadioBtn:
                        strength = 2;
                        checked = true;
                        break;
                    case R.id.hardRadioBtn:
                        strength = 3;
                        checked = true;
                        break;
                    default:
                        strength = -1;
                }
            }
        });

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatedPass.setText("");
                if(!checked) {
                    Toasty.info(GenerateActivity.this, "Choose strength first",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Generate generate = new Generate(strength, easyLen, medLen, hardLen);
                            password = generate.generate();
                            generatedPass.setText(password);
                            generateBtn.setText(getString(R.string.generate_btn_2));
                            copyBtn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
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
                    assert clipboard != null;
                    clipboard.setPrimaryClip(clip);
                    Toasty.info(GenerateActivity.this, "Password Copied to Clipboard",
                            Toasty.LENGTH_SHORT, true).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        usernameHintEdt.setOnKeyListener(new View.OnKeyListener() {
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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceName = serviceNameEdt.getText().toString();
                String username = usernameHintEdt.getText().toString();
                if(serviceName.equals("")) {
                    Toasty.info(GenerateActivity.this,
                            "Enter the name of Service for future Reference",
                            Toasty.LENGTH_SHORT, true).show();
                } else {
                    databaseHelper = new DatabaseHelper(GenerateActivity.this);
                    boolean result = databaseHelper.addData(serviceName, password, username);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        String medLenStr = preferences.getString(med, "12").trim();
        String hardLenStr = preferences.getString(hard, "16").trim();

        if(easyLenStr.equals("") || medLenStr.equals("") || hardLenStr.equals("")) {
            Toasty.error(this, "Length of password is empty/not valid\nGo to Settings and Enter Valid value",
                    Toasty.LENGTH_LONG, true).show();
            finish();
        } else {
            if(isNumber(easyLenStr) && isNumber(hardLenStr) && isNumber(medLenStr)) {
                easyLen = Integer.parseInt(easyLenStr);
                medLen = Integer.parseInt(medLenStr);
                hardLen = Integer.parseInt(hardLenStr);
            } else {
                Toasty.error(this, "Length of password is empty/not valid\nGo to Settings and Enter Valid value",
                        Toasty.LENGTH_LONG, true).show();
                finish();
            }
        }
    }
}