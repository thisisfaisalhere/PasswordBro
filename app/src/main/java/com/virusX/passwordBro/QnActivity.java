package com.virusX.passwordBro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QnActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qn);

        setTitle("Backup Question");

        Intent receivedData = getIntent();
        boolean fromAddAc = receivedData.getBooleanExtra("fromAddAcActivity", false);
        username = receivedData.getStringExtra("username");

        textView = findViewById(R.id.security_subtitle);
        EditText q5Edt = findViewById(R.id.q5Edt);
        button = findViewById(R.id.qnBtn);

        q5Edt.setOnKeyListener(new View.OnKeyListener() {
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

        if(fromAddAc) {
            checkSecurityQuestion();
        } else {
            addSecurityQuestion();
        }
    }

    private void addSecurityQuestion() {
        textView.setText(getText(R.string.security_question_subtitle));
        button.setText(getText(R.string.save_btn));
        AddBackupAns addBackupAns = new AddBackupAns(this);
        addBackupAns.checkAns();
        addBackupAns.addAns();
    }

    private void checkSecurityQuestion() {
        textView.setText(getText(R.string.security_question_subtitle_2));
        button.setText(getText(R.string.check));
        MatchSecurityAns matchSecurityAns = new MatchSecurityAns(this);
        matchSecurityAns.getUserAns(username);
    }

    public void qnTapped(View view) {
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
