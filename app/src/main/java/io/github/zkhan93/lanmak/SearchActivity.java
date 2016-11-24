package io.github.zkhan93.lanmak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.DatagramSocket;
import java.net.SocketException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.lanmak.adapter.HostAdapter;
import io.github.zkhan93.lanmak.events.CodeReadEvents;
import io.github.zkhan93.lanmak.events.HostClickedEvent;
import io.github.zkhan93.lanmak.events.HostFoundEvents;
import io.github.zkhan93.lanmak.events.HostSearchOverEvent;
import io.github.zkhan93.lanmak.events.HostSearchStartEvent;
import io.github.zkhan93.lanmak.tasks.ServerBroadcastReceiverTask;
import io.github.zkhan93.lanmak.tasks.ServerBroadcastTask;
import io.github.zkhan93.lanmak.utility.Util;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = SearchActivity.class.getSimpleName();

    @BindView(R.id.hosts)
    RecyclerView hosts;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.error)
    View errorView;
    @BindView(R.id.err_msg)
    TextView errorText;
    @BindView(R.id.err_button)
    Button errorButton;

    private HostAdapter hostAdapter;
    private boolean searched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        hosts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        hostAdapter = new HostAdapter();
        hosts.setAdapter(hostAdapter);
        if (savedInstanceState == null) {
            searched = false;
        } else {
            searched = savedInstanceState.getBoolean("searched");
            hostAdapter.restoreInstanceState(savedInstanceState);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HostSearchStartEvent event) {
        startHostSearch();
        searched = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HostSearchOverEvent event) {
        searched = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HostClickedEvent event) {
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                startActivity(new Intent(this, ScanActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        hostAdapter.unregisterEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        hostAdapter.registerEvents();
        if (!searched)
            startHostSearch();
    }

    private void startHostSearch() {
//        progress.setVisibility(View.VISIBLE);
        try {
            DatagramSocket socket = new DatagramSocket(2222);
            new ServerBroadcastReceiverTask(socket).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new ServerBroadcastTask(socket).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (SocketException ex) {
            Log.d(TAG, "cannot create DatagramSocket " + ex.getLocalizedMessage());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("searched", searched);
        hostAdapter.saveInstanceState(outState);
    }
}
