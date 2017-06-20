package io.github.zkhan93.lanmak.models.viewholders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.lanmak.R;
import io.github.zkhan93.lanmak.events.HostSearchStartEvent;
import io.github.zkhan93.lanmak.events.StartScanActivityEvent;

/**
 * Created by zeeshan on 11/20/2016.
 */

public class RetryVH extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static final String TAG = RetryVH.class.getSimpleName();
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.scan)
    Button scan;

    public RetryVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        button.setOnClickListener(this);
        scan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                EventBus.getDefault().post(new HostSearchStartEvent());

                break;
            case R.id.scan:
                EventBus.getDefault().post(new StartScanActivityEvent());
                break;
            default:
                Log.d(TAG, "click not implemented");
        }
    }

    public void setMessage(boolean showMessage) {
        message.setVisibility(showMessage ? View.VISIBLE : View.GONE);
    }
}
