package io.github.zkhan93.remotekeyboardandmouse;

import java.io.PrintWriter;
import java.net.Socket;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class MainFragment extends Fragment {
	EditText edt;
	Socket s;
	PrintWriter put;

	public MainFragment() {
		new setNw().execute();
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
		edt.addTextChangedListener(new TextWatcher() {
			int ch;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				switch (s.length()) {
				case 0:
					// delete pressed
					s.append("_");
					ch = 8;
					put.println("0:" + ch);
					break;
				case 2:
					// a key pressed
					ch = (int) s.charAt(1);
					s.replace(1, s.length(), "", 0, 0);
					put.println("0:" + ch);
					break;
				case 1:
					break;
				default:
					s.replace(1, s.length(), "", 0, 0);
					break;
				}
			}
		});
		super.onStart();
	}

	class setNw extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String ip = getActivity().getSharedPreferences(
						getString(R.string.pref_file_name),
						Context.MODE_PRIVATE)
						.getString(getString(R.string.pref_server_ip),
								Constants.SERVER_IP);
				if (ip != null) {
					s = new Socket(ip, Constants.PORT);
					put = new PrintWriter(s.getOutputStream(), true);
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(getActivity(), "connected to server",
						Toast.LENGTH_SHORT).show();
				
			} else {
				Toast.makeText(getActivity(),
						"uanble to connect! check server IP in setting",
						Toast.LENGTH_SHORT).show();
				
			}
			super.onPostExecute(result);
		}
	}
}
