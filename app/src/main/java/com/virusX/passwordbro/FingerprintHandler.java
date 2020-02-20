package com.virusX.passwordbro;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private boolean success;

    FingerprintHandler(Context context){
        this.context = context;
    }

    boolean startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        return success;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("There was an Auth Error. " + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Auth Failed. ", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("You can now access the app.", true);
    }

    private void update(String s, boolean b) {
        TextView fingerprintMessage = ((Activity)context).findViewById(R.id.fingerprintMessage);
        ImageView fingerprintImg = ((Activity)context).findViewById(R.id.fingerprintImg);
        fingerprintMessage.setText(s);
        if(!b){
            fingerprintMessage.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            fingerprintImg.setImageResource(R.drawable.ic_error);
        } else {
            fingerprintMessage.setTextColor(ContextCompat.getColor(context, R.color.text_color));
            fingerprintImg.setImageResource(R.drawable.ic_done);
        }
        success = b;
    }
}
