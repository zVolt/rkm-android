package io.github.zkhan93.lanmak.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.lanmak.R;
import io.github.zkhan93.lanmak.models.Host;
import io.github.zkhan93.lanmak.models.viewholders.HostVH;

/**
 * Created by Zeeshan Khan on 11/2/2016.
 */

public class HostAdapter extends RecyclerView.Adapter<HostVH> {

    List<Host> hosts;

    public HostAdapter() {
        hosts = new ArrayList<>();
    }

    @Override
    public HostVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.host, parent, false));
    }

    @Override
    public void onBindViewHolder(HostVH holder, int position) {
        holder.setHost(hosts.get(position));
    }

    @Override
    public int getItemCount() {
        return hosts.size();
    }

    public void addHost(Host host) {
        if (host == null || hosts == null)
            return;
        hosts.add(host);
        notifyDataSetChanged();
    }
}
