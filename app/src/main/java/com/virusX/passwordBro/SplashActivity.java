package com.virusX.passwordBro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class SplashActivity extends AppCompatActivity {

    private boolean lockPref;
    private Intent intent;
    private ImageView appIcon, fingerprintImg, lockImg;
    private TextView splashTile, fingerprintMessage;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        intent = new Intent(SplashActivity.this, MainActivity.class);
        appIcon = findViewById(R.id.appIconSplash);
        fingerprintImg = findViewById(R.id.fingerprintImg);
        lockImg = findViewById(R.id.lockImg);
        splashTile = findViewById(R.id.splashTitle);
        fingerprintMessage = findViewById(R.id.fingerprintMessage);

        appIcon.setTranslationY(500);
        appIcon.animate().translationYBy(-500).setDuration(1000);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        lockPref = sharedPreferences.getBoolean("fingerprint", false);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextAction();
            }
        }, 2000);
    }

    private void nextAction() {
        if(!lockPref) {
            startActivity(intent);
            finish();
        } else {
            appIcon.setImageResource(0);
            lockImg.setImageResource(R.drawable.ic_lock);
            splashTile.setText(getString(R.string.splash_title));
            fingerprintImg.setImageResource(R.drawable.ic_fingerprint);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                if(!fingerprintManager.isHardwareDetected()){
                    fingerprintMessage.setText(getString(R.string.fingerprint_error_1));
                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.USE_FINGERPRINT)
                        != PackageManager.PERMISSION_GRANTED){
                    fingerprintMessage.setText(getString(R.string.fingerprint_error_2));
                } else if (!keyguardManager.isKeyguardSecure()){
                    fingerprintMessage.setText(getString(R.string.fingerprint_error_3));
                } else if (!fingerprintManager.hasEnrolledFingerprints()){
                    fingerprintMessage.setText(getString(R.string.fingerprint_error_4));
                } else {
                    fingerprintMessage.setText(getString(R.string.fingerprint_success));
                    generateKey();
                    if (cipherInit()){
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                        FingerprintHandler fingerprintHandler = new FingerprintHandler(this);
                        fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }

    }
}