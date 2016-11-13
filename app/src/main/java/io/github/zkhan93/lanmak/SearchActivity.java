package io.github.zkhan93.lanmak;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.DatagramSocket;
import java.net.SocketException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.lanmak.adapter.HostAdapter;
import io.github.zkhan93.lanmak.events.HostEvents;
import io.github.zkhan93.lanmak.tasks.ServerBroadcastReceiverTask;
import io.github.zkhan93.lanmak.tasks.ServerBroadcastTask;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = SearchActivity.class.getSimpleName();

    @BindView(R.id.hosts)
    RecyclerView hosts;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        hosts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        hosts.setAdapter(new HostAdapter());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HostEvents event) {
        ((HostAdapter) hosts.getAdapter()).addHost(event.getHost());
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        try {
            DatagramSocket socket = new DatagramSocket(2222);
            new ServerBroadcastReceiverTask(socket).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new ServerBroadcastTask(socket).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (SocketException ex) {
            Log.d(TAG, "cannot create DatagramSocket " + ex.getLocalizedMessage());
        }

    }
}
