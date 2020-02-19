package com.virusX.passwordbro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        setTitle("Share");

        Button shareBtn = findViewById(R.id.shareBtn);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Password Bro");
                    String shareMsg = getString(R.string.share_message);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
                    startActivity(Intent.createChooser(sendIntent, "Hey..."));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
