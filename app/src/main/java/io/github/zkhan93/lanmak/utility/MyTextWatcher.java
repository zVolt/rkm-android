package io.github.zkhan93.lanmak.utility;

import io.github.zkhan93.lanmak.MainFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class MyTextWatcher implements TextWatcher {
	MainFragment mf;
	int ch;
	public static String TAG="io.github.zkhan93.remotekeyboardandmouse.MyTextWatcher";
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
			s.append(Constants.UNDERSCORE);
			ch = 8;
			mf.sendKey(String.valueOf(ch)); // yes we are on main thread :P
			break;
		case 2:
			// a key pressed
			ch = (int) s.charAt(1);
			Log.d(TAG, ch+","+(char)ch);
			s.replace(1, s.length(), "", 0, 0);
			mf.sendKey(String.valueOf(ch)); // yes we are on main thread :P
			break;
		case 1:
			break;
		default:
			s.replace(1, s.length(), "", 0, 0);
			break;
		}
	}
}
