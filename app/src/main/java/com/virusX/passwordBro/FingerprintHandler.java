package com.virusX.passwordBro;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private TextView fingerprintMessage;
    private ImageView fingerprintImg;
    private static final String TAG = "fingerprint";

    FingerprintHandler(Context context){
        this.context = context;
    }

    void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        fingerprintMessage = ((Activity)context).findViewById(R.id.fingerprintMessage);
        fingerprintImg = ((Activity)context).findViewById(R.id.fingerprintImg);
        fingerprintMessage.setText(errString);
        fingerprintMessage.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        fingerprintImg.setImageResource(R.drawable.ic_error);
        Log.d(TAG, "onAuthenticationError");
    }

    @Override
    public void onAuthenticationFailed() {
        Log.d(TAG, "onAuthenticationFailed");
        fingerprintMessage = ((Activity)context).findViewById(R.id.fingerprintMessage);
        fingerprintImg = ((Activity)context).findViewById(R.id.fingerprintImg);
        fingerprintMessage.setText("Authentication Failed");
        fingerprintMessage.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        fingerprintImg.setImageResource(R.drawable.ic_error);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fingerprintMessage.setText(context.getString(R.string.fingerprint_success));
                fingerprintMessage.setTextColor(ContextCompat.getColor(context, R.color.text_color));
                fingerprintImg.setImageResource(R.drawable.ic_fingerprint);
            }
        }, 1000);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Log.d(TAG, "onAuthenticationHelp");
        fingerprintMessage = ((Activity)context).findViewById(R.id.fingerprintMessage);
        fingerprintImg = ((Activity)context).findViewById(R.id.fingerprintImg);
        fingerprintMessage.setText(helpString);
        fingerprintMessage.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        fingerprintImg.setImageResource(R.drawable.ic_error);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Log.d(TAG, "onAuthenticationSucceeded");
        fingerprintMessage = ((Activity)context).findViewById(R.id.fingerprintMessage);
        fingerprintImg = ((Activity)context).findViewById(R.id.fingerprintImg);
        fingerprintMessage.setTextColor(ContextCompat.getColor(context, R.color.text_color));
        fingerprintImg.setImageResource(R.drawable.ic_done);
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}
