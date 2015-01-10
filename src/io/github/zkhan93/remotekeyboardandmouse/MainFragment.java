package io.github.zkhan93.remotekeyboardandmouse;

import io.github.zkhan93.remotekeyboardandmouse.MainActivity.SetNw;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MainFragment extends Fragment {
	EditText edt;

	public MainFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		return rootView;
	}

	@Override
	public void onStart() {
		edt = (EditText) getActivity().findViewById(R.id.editText);
		edt.addTextChangedListener(new MyTextWatcher(this));
		new SetNw(getActivity()).execute(getActivity().getSharedPreferences(
				getString(R.string.pref_file_name), Context.MODE_PRIVATE)
				.getString(getString(R.string.pref_server_ip),
						Constants.SERVER_IP));
		super.onStart();
	}

	void sendKey(String cmd) {
		try {
			if (MainActivity.put != null)
				MainActivity.put
						.println(Constants.ZERO + Constants.COLON + cmd);
			else
				MainActivity.connected = false;
		} catch (Exception e) {
			// reconnect server
		}
	}

}
