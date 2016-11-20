package io.github.zkhan93.lanmak.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.lanmak.R;
import io.github.zkhan93.lanmak.events.HostFoundEvents;
import io.github.zkhan93.lanmak.events.HostSearchOverEvent;
import io.github.zkhan93.lanmak.events.HostSearchStartEvent;
import io.github.zkhan93.lanmak.models.Host;
import io.github.zkhan93.lanmak.models.viewholders.HostVH;
import io.github.zkhan93.lanmak.models.viewholders.LoadingVH;
import io.github.zkhan93.lanmak.models.viewholders.RetryVH;

/**
 * Created by Zeeshan Khan on 11/2/2016.
 */

public class HostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = HostAdapter.class.getSimpleName();
    private List<Host> hosts;

    private boolean searchInProgress;
    private int insertPosition;

    public HostAdapter() {
        hosts = new ArrayList<>();
        searchInProgress = true;
        insertPosition = 0;//start inserting host from top
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.RETRY)
            return new RetryVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.retry_item, parent, false));
        else if (viewType == ITEM_TYPE.SEARCH)
            return new LoadingVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item, parent, false));
        return new HostVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.host_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HostVH)
            ((HostVH) holder).setHost(hosts.get(position));
    }

    @Override
    public int getItemCount() {
        return hosts.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < hosts.size())
            return ITEM_TYPE.NORMAL;
        else {
            if (searchInProgress)
                return ITEM_TYPE.SEARCH;
            return ITEM_TYPE.RETRY;
        }
    }

    public void addHost(Host host) {
        if (host == null || hosts == null)
            return;
        hosts.add(host);
        notifyDataSetChanged();
    }

    public void registerEvents() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEvents() {
        EventBus.getDefault().unregister(this);
    }

    public void saveInstanceState(Bundle outState) {
        outState.putBoolean("searchInProgress", searchInProgress);
        outState.putInt("insertPosition", insertPosition);
        outState.putParcelableArrayList("hosts", (ArrayList<Host>) hosts);
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        searchInProgress = savedInstanceState.getBoolean("searchInProgress");
        insertPosition = savedInstanceState.getInt("insertPosition");
        hosts = savedInstanceState.getParcelableArrayList("hosts");
        notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HostFoundEvents event) {
        if (event.getHost() == null || hosts == null)
            return;
        Host newHost = event.getHost();
        for (Host h : hosts) {
            if (h.getIp().equals(newHost.getIp())) {
                return;
            }
        }
        hosts.add(newHost);
        notifyItemInserted(insertPosition++);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HostSearchOverEvent event) {
        searchInProgress = false;
        notifyItemChanged(getItemCount());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HostSearchStartEvent event) {
        Log.d(TAG, "remove search item");
        int items = getItemCount();
        hosts.clear();
        searchInProgress = true;
        notifyItemRangeRemoved(0, items - 1);
        notifyItemChanged(items);
    }

    public interface ITEM_TYPE {
        int NORMAL = 1;
        int RETRY = 2;
        int SEARCH = 3;
    }
}
