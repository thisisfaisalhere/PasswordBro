package com.virusX.passwordBro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ShareFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share, container, false);
        Button shareBtn = view.findViewById(R.id.shareBtn);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/rtf");
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Password Bro");
                    String shareMsg = getString(R.string.share_message);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
                    startActivity(Intent.createChooser(sendIntent, "Share"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}
