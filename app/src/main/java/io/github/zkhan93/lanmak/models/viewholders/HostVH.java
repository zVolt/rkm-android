package io.github.zkhan93.lanmak.models.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.lanmak.R;
import io.github.zkhan93.lanmak.models.Host;

/**
 * Created by Zeeshan Khan on 11/2/2016.
 */

public class HostVH extends RecyclerView.ViewHolder {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.ip)
    TextView ip;

    public HostVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setHost(Host host) {
        name.setText(host.getName());
        ip.setText(host.getIp());
    }
}
