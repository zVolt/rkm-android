package io.github.zkhan93.lanmak;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.lanmak.models.Host;
import io.github.zkhan93.lanmak.utility.Util;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static final String TAG = ScanActivity.class.getSimpleName();
    @BindView(R.id.zXingScannerView)
    ZXingScannerView zXingScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest
                    .permission.CAMERA)) {
                Log.d(TAG, "show permission request for " + Manifest.permission.CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .CAMERA}, 0);
            }
        } else {
            zXingScannerView.startCamera();
        }
    }

    @Override
    public void handleResult(Result result) {
        Log.d(TAG, result.getText());
        if (result.getBarcodeFormat() == BarcodeFormat.QR_CODE) {
            Host host = null;
            try {
                host = new Gson().fromJson(result.getText(), Host.class);
                if (host == null) {
                    showInvalidCodeMsg();
                    return;
                }
                SharedPreferences.Editor spfEditor = PreferenceManager
                        .getDefaultSharedPreferences(this.getApplicationContext
                                ()).edit();
                if (Util.isIPv4Address(host.getIp()))
                    spfEditor.putString("server_ip", host.getIp());
                if (Util.isValidPort(String.valueOf(host.getPort())))
                    spfEditor.putString("port", String.valueOf(host.getPort()));
                spfEditor.apply();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } catch (JsonSyntaxException ex) {
                Log.d(TAG, "" + ex.getLocalizedMessage());
                showInvalidCodeMsg();
            }
        } else {
            showInvalidCodeMsg();
            Log.d(TAG, "not in the format, I am looking for");
        }
    }

    private void showInvalidCodeMsg() {
        Toast.makeText(this.getApplicationContext(), "Invalid/Unexpected QR Code", Toast
                .LENGTH_SHORT).show();
        zXingScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                zXingScannerView.startCamera();
            else
                finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SearchActivity.class));
        finish();
        super.onBackPressed();
    }
}
