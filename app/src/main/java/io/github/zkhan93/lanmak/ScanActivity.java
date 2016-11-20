package io.github.zkhan93.lanmak;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.lanmak.events.CodeReadEvents;
import io.github.zkhan93.lanmak.models.Host;
import io.github.zkhan93.lanmak.utility.Util;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.R.attr.host;

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
        zXingScannerView.startCamera();
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
                SharedPreferences.Editor spfEditor = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext
                        ()).edit();
                if (Util.isIPv4Address(host.getIp()))
                    spfEditor.putString("server_ip", host.getIp());
                if (Util.isValidPort(String.valueOf(host.getPort())))
                    spfEditor.putString("port", String.valueOf(host.getPort()));
                spfEditor.commit();
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
        Toast.makeText(this.getApplicationContext(), "Invalid/Unexpected QR Code", Toast.LENGTH_SHORT).show();
        zXingScannerView.resumeCameraPreview(this);
    }
}
