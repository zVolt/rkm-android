package io.github.zkhan93.lanmak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.SocketException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.lanmak.adapter.HostAdapter;
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

    @Override
    protected void onStart() {
        super.onStart();
        try {
            DatagramSocket socket = new DatagramSocket();
            new ServerBroadcastTask(socket).execute();
            new ServerBroadcastReceiverTask(socket).execute();
        } catch (SocketException ex) {
            Log.d(TAG, "" + ex.getLocalizedMessage());
        }
    }
}
