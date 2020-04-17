package com.virusX.passwordBro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;

    FingerprintHandler(Context context){
        this.context = context;
    }

    void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update(errString + "", false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Authentication Failed. ", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update(helpString + "", false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("You can now access the app.", true);
    }

    private void update(String s, boolean b) {
        final TextView fingerprintMessage = ((Activity)context).findViewById(R.id.fingerprintMessage);
        final ImageView fingerprintImg = ((Activity)context).findViewById(R.id.fingerprintImg);
        fingerprintMessage.setText(s);
        if(!b){
            fingerprintMessage.setTextColor(ContextCompat.getColor(context, R.color.textColor));
            fingerprintImg.setImageResource(R.drawable.ic_error);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fingerprintMessage.setText(context.getString(R.string.fingerprint_success));
                    fingerprintMessage.setTextColor(ContextCompat.getColor(context, R.color.errorColor));
                    fingerprintImg.setImageResource(R.drawable.ic_fingerprint);
                }
            }, 1000);
        } else {
            fingerprintMessage.setTextColor(ContextCompat.getColor(context, R.color.textColor));
            fingerprintImg.setImageResource(R.drawable.ic_done);
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }
}
