package com.infinitystudios.tcpip;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nhat on 4/10/18.
 */

public class App extends Application {

    private Map<String, String> map = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void setValue(String key, String value) {
        map.put(key, value);
    }

    public String getValue(String key) {
        return map.get(key);
    }
}
