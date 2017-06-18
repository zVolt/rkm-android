package io.github.zkhan93.lanmak.models.viewholders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.lanmak.R;
import io.github.zkhan93.lanmak.events.HostClickedEvent;
import io.github.zkhan93.lanmak.models.Host;

/**
 * Created by Zeeshan Khan on 11/2/2016.
 */

public class HostVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static final String TAG = HostVH.class.getSimpleName();

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.ip)
    TextView ip;

    private Host host;

    public HostVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public void setHost(Host host) {
        this.host = host;
        name.setText(host.getName());
        ip.setText(host.getIp());
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new HostClickedEvent(host));
        Log.d(TAG, "host cliecked" + host.toString());
    }
}
