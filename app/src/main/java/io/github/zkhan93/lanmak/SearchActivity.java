package io.github.zkhan93.lanmak;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
import io.github.zkhan93.lanmak.events.HostClickedEvent;
import io.github.zkhan93.lanmak.events.HostSearchOverEvent;
import io.github.zkhan93.lanmak.events.HostSearchStartEvent;
import io.github.zkhan93.lanmak.events.SocketEvents;
import io.github.zkhan93.lanmak.tasks.ServerBroadcastReceiverTask;
import io.github.zkhan93.lanmak.tasks.ServerBroadcastTask;
import io.github.zkhan93.lanmak.utility.Constants;

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

    private boolean bound,auto_connect;
    private ServiceConnection serviceConnection;
    private SocketConnectionService socketConnectionService;

    {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                SocketConnectionService.LocalBinder localBinder = (SocketConnectionService.LocalBinder) service;
                socketConnectionService = localBinder.getService();
                bound = true;
                updateView(socketConnectionService.getServiceState());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };
    }

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
        auto_connect=getIntent().getBooleanExtra("auto_connect",true);
        startService(new Intent(this, SocketConnectionService.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SocketEvents event) {
        updateView(event.getSocketState());
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
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext
                ());
        if (spf.getString("server_ip", "").equals(event.getHost().getIp()) && spf.getString("port", "").equals(event.getHost().getPort())) {
            socketConnectionService.reconnect();
            startActivity(new Intent(this, MainActivity.class));
        }
        else
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                    .putString("server_ip", event.getHost().getIp())
                    .putString("port", String.valueOf(event.getHost().getPort()))
                    .apply();
//        startActivity(new Intent(this, MainActivity.class));
        //changing preference will trigger the service to send connection state update which will
        // be caught in this activity
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
                finish();
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
        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        hostAdapter.registerEvents();
        if (!searched)
            startHostSearch();
        Intent intent = new Intent(this, SocketConnectionService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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

    private void updateView(int state) {
        switch (state) {
            case Constants.SERVICE_STATE.CONNECTING:
                //progress only
                progress.setVisibility(View.VISIBLE);
                hosts.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                break;
            case Constants.SERVICE_STATE.CONNECTED:
                //start other activity
                if(auto_connect) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else{
                    //so that auto_connect can skip the connection for only one time
                    auto_connect=true;
                }
                break;
            case Constants.SERVICE_STATE.DISCONNECTED:
                //scan for servers
                progress.setVisibility(View.GONE);
                hosts.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                break;
            default:

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("searched", searched);
        hostAdapter.saveInstanceState(outState);
    }
}
