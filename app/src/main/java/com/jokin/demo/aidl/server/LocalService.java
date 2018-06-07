package com.jokin.demo.aidl.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {
    private static final String TAG = "LocalService";

    public class LocalBinder extends Binder {
        LocalService getLocalService() {
            return LocalService.this;
        }
    }

    public void open(int color, int size) {
        Log.e(TAG, String.format("open with color=%d size=%d", color, size));
    }

    public void close(String tip) {
        Log.e(TAG, "close with tip:" + tip);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new LocalBinder();
    }
}
