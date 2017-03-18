package io.github.zkhan93.lanmak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.lanmak.callbacks.MyTextWatcherClblk;
import io.github.zkhan93.lanmak.events.ActionDisconnectEvent;
import io.github.zkhan93.lanmak.events.SocketEvents;
import io.github.zkhan93.lanmak.utility.Constants;
import io.github.zkhan93.lanmak.utility.MyTextWatcher;

import static android.view.View.GONE;


public class MainFragment extends Fragment implements View.OnClickListener, OnLongClickListener {

    public static final String TAG = MainFragment.class.getSimpleName();

    @BindView(R.id.SpecialButtons)
    TableLayout specialButtonsLayout;
    @BindView(R.id.editText)
    EditText edt;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.error_view)
    View errorView;
    @BindView(R.id.main_view)
    View mainView;
    @BindView(R.id.buttonspecial11)
    ImageButton b11;
    @BindView(R.id.buttonspecial12)
    ImageButton b12;
    @BindView(R.id.buttonspecial13)
    ImageButton b13;
    @BindView(R.id.buttonspecial14)
    ImageButton b14;
    @BindView(R.id.buttonspecial15)
    ImageButton b15;
    @BindView(R.id.buttonspecial16)
    ImageButton b16;
    @BindView(R.id.buttonspecial21)
    ImageButton b21;
    @BindView(R.id.buttonspecial22)
    ImageButton b22;
    @BindView(R.id.buttonspecial23)
    ImageButton b23;
    @BindView(R.id.buttonspecial24)
    ImageButton b24;
    @BindView(R.id.buttonspecial25)
    ImageButton b25;
    @BindView(R.id.buttonspecial26)
    ImageButton b26;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    boolean isSpecialBtnPanelVisible, isProgressVisible, isRetryVisible;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        edt.addTextChangedListener(new MyTextWatcher((MyTextWatcherClblk) getActivity()));
        if (savedInstanceState != null) {
            isSpecialBtnPanelVisible = savedInstanceState.getBoolean("isSpecialBtnPanelVisible");
            isProgressVisible = savedInstanceState.getBoolean("isProgressVisible");
            isRetryVisible = savedInstanceState.getBoolean("isRetryVisible");
        }
        specialButtonsLayout.setVisibility(isSpecialBtnPanelVisible ? View.VISIBLE : View.GONE);
//        progress.setVisibility(isProgressVisible ? View.VISIBLE : View.GONE);
        errorView.setVisibility(isRetryVisible ? View.VISIBLE : View.GONE);
        b11.setOnLongClickListener(this);
        b12.setOnLongClickListener(this);
        b13.setOnLongClickListener(this);
        b14.setOnLongClickListener(this);
        b15.setOnLongClickListener(this);
        b16.setOnLongClickListener(this);
        b21.setOnLongClickListener(this);
        b22.setOnLongClickListener(this);
        b23.setOnLongClickListener(this);
        b24.setOnLongClickListener(this);
        b25.setOnLongClickListener(this);
        b26.setOnLongClickListener(this);
        errorView.findViewById(R.id.err_button).setOnClickListener(this);
        EventBus.getDefault().register(this);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSpecialBtnPanelVisible", isSpecialBtnPanelVisible);
        outState.putBoolean("isProgressVisible", isProgressVisible);
        outState.putBoolean("isRetryVisible", isRetryVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity().getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.action_toggle:
                toggleSpecialButtons();
                return true;
            case R.id.action_disconnect:
                EventBus.getDefault().post(new ActionDisconnectEvent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "unregistered");
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.err_button:
                Intent intent = new Intent(getActivity().getApplicationContext(), SearchActivity
                        .class);
                intent.putExtra("auto_connect", false);
                startActivity(intent);
                getActivity().finish();
                break;
            default:
                Log.d(TAG, "click not implemented");
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.buttonspecial11:
                Toast.makeText(getActivity(), Constants.Button11,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial12:
                Toast.makeText(getActivity(), Constants.Button12,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial13:
                Toast.makeText(getActivity(), Constants.Button13,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial14:
                Toast.makeText(getActivity(), Constants.Button14,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial15:
                Toast.makeText(getActivity(), Constants.Button15,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial16:
                Toast.makeText(getActivity(), Constants.Button16,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial21:
                Toast.makeText(getActivity(), Constants.Button21,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial22:
                Toast.makeText(getActivity(), Constants.Button22,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial23:
                Toast.makeText(getActivity(), Constants.Button23,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial24:
                Toast.makeText(getActivity(), Constants.Button24,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial25:
                Toast.makeText(getActivity(), Constants.Button25,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.buttonspecial26:
                Toast.makeText(getActivity(), Constants.Button26,
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                Log.d(TAG, "long click not implemented");
                return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SocketEvents event) {
        updateConnectionStatus(event.getSocketState());
    }

    /**
     * state of service from {@link io.github.zkhan93.lanmak.utility.Constants.SERVICE_STATE}
     *
     * @param state
     */
    public void updateConnectionStatus(int state) {
        if (progress == null || errorView == null)
            return;
        switch (state) {
            case Constants.SERVICE_STATE.CONNECTED:
                Log.d(TAG, "connected");
                isProgressVisible = false;
                isRetryVisible = false;
                break;
            case Constants.SERVICE_STATE.CONNECTING:
                Log.d(TAG, "connecting");
                isProgressVisible = true;
                isRetryVisible = false;
                break;
            case Constants.SERVICE_STATE.DISCONNECTED:
                Log.d(TAG, "disconnected");
                isProgressVisible = false;
                isRetryVisible = true;
                break;
            default:
                Log.d(TAG, "invalid service state");
                break;
        }
        progress.setVisibility(isProgressVisible ? View.VISIBLE : View.GONE);
        errorView.setVisibility(isRetryVisible ? View.VISIBLE : View.GONE);
        mainView.setVisibility(isRetryVisible || isProgressVisible ? View.GONE : View.VISIBLE);
    }

    public void retry() {
        if (getActivity() != null)
            ((MainActivity) getActivity()).reconnect();
        if (progress == null || errorView == null)
            return;
        progress.setVisibility(View.VISIBLE);
        isProgressVisible = true;
        errorView.setVisibility(GONE);
        isRetryVisible = true;
        mainView.setVisibility(isRetryVisible || isProgressVisible ? View.GONE : View.VISIBLE);
    }

    public void toggleSpecialButtons() {
        if (specialButtonsLayout != null) {
            if (isSpecialBtnPanelVisible)
                specialButtonsLayout.setVisibility(GONE);
            else
                specialButtonsLayout.setVisibility(View.VISIBLE);
            isSpecialBtnPanelVisible = !isSpecialBtnPanelVisible;
        }
    }

}
