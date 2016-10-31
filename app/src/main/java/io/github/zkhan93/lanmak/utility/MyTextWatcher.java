package io.github.zkhan93.lanmak.utility;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.lang.ref.WeakReference;

import io.github.zkhan93.lanmak.callbacks.MyTextWatcherClblk;

public class MyTextWatcher implements TextWatcher {
    WeakReference<MyTextWatcherClblk> myTextWatcherClblkRef;
    private int ch;
    public static final String TAG = MyTextWatcher.class.getSimpleName();

    public MyTextWatcher(MyTextWatcherClblk myTextWatcherClblk) {
        this.myTextWatcherClblkRef = new WeakReference<>(myTextWatcherClblk);
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
                myTextWatcherClblkRef.get().sendKeyboardKeys(String.valueOf(ch)); // yes we are on main thread :P
                break;
            case 2:
                // a key pressed
                ch = (int) s.charAt(1);
//                Log.d(TAG, ch + "," + (char) ch);
                s.replace(1, s.length(), "", 0, 0);
                myTextWatcherClblkRef.get().sendKeyboardKeys(String.valueOf(ch)); // yes we are on main thread :P
                break;
            case 1:
                break;
            default:
                s.replace(1, s.length(), "", 0, 0);
                break;
        }
    }
}
