package com.jokin.demo.aidl.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {
    private static final String TAG = "MessengerService";

    public static final int OPEN_ACTION = 1000;
    public static final int CLOSE_ACTION = 1001;

    public static final int OPEN_RESULT = 2000;
    public static final int CLOSE_RESULT = 2001;

    public static final String KEY_OF_COLOR = "key.color";
    public static final String KEY_OF_SIZE = "key.size";
    public static final String KEY_OF_TIP = "key.tip";

    private Messenger mMessenger;

    public MessengerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBackgroundThread();
        mMessenger = new Messenger(mHandler);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyBackgroundThread();
    }

    ///////////// Background Thread //////////////
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private void initBackgroundThread() {
        mHandlerThread = new HandlerThread("messenger-thread",  Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mHandler = new MessengerHandler(mHandlerThread.getLooper());
    }

    private void destroyBackgroundThread() {
        mHandler.removeCallbacksAndMessages(null);
        mHandlerThread.quitSafely();
    }

    private class MessengerHandler extends Handler {
        public MessengerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == OPEN_ACTION) {
                int color = msg.getData().getInt(KEY_OF_COLOR, -1);
                int size = msg.getData().getInt(KEY_OF_SIZE, -1);
                Log.e(TAG, String.format("open with color=%d size=%d", color, size));

                Message message = Message.obtain();
                message.what = OPEN_RESULT;
                try {
                    msg.replyTo.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == CLOSE_ACTION) {
                Log.e(TAG, String.format("close with tip=%s", msg.getData().getString(KEY_OF_TIP)));
                Message message = Message.obtain();
                message.what = CLOSE_RESULT;
                try {
                    msg.replyTo.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
