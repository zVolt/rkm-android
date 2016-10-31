package io.github.zkhan93.lanmak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.lanmak.events.CodeReadEvents;
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
        zXingScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        Log.d(TAG, result.getText());
        if (result.getBarcodeFormat() == BarcodeFormat.QR_CODE) {
            EventBus.getDefault().post(new CodeReadEvents(result.getText()));
            finish();
        } else {
            Log.d(TAG, "not in the format, I am looking for");
        }
    }
}
