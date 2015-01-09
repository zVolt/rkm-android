package io.github.zkhan93.remotekeyboardandmouse;

import android.text.Editable;
import android.text.TextWatcher;

public class MyTextWatcher implements TextWatcher {
	MainFragment mf;
	int ch;

	public MyTextWatcher(MainFragment f) {
		mf = f;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		switch (s.length()) {
		case 0:
			// delete pressed
			s.append("_");
			ch = 8;
			mf.sendKey(ch + ""); // yes we are on main thread :P
			break;
		case 2:
			// a key pressed
			ch = (int) s.charAt(1);
			s.replace(1, s.length(), "", 0, 0);
			mf.sendKey(ch + ""); // yes we are on main thread :P
			break;
		case 1:
			break;
		default:
			s.replace(1, s.length(), "", 0, 0);
			break;
		}
	}
}
