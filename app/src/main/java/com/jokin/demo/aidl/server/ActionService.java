package com.jokin.demo.aidl.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jokin.demo.aidl.sdk.IAction;
import com.jokin.demo.aidl.sdk.IActionListener;
import com.jokin.demo.aidl.sdk.Isdk;
import com.jokin.demo.aidl.sdk.actions.Action;
import com.jokin.demo.aidl.sdk.actions.CloseAction;
import com.jokin.demo.aidl.sdk.actions.OpenAction;

import java.util.HashMap;

public class ActionService extends Service {
    private static final String TAG = "ActionService";
    public ActionService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service running !!!");
    }

    // 2. 返回Binder
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    // 1. 创建AIDL接口Binder
    private final Isdk.Stub mBinder = new Isdk.Stub() {
        protected HashMap<String, IActionListener> mActionListenerMap;

        /**
         * 如果，客户端在主线程调用，那么这里就在主线程执行。
         * 如果，客户端在子线程调用，那么这里就在对应的子线程执行。
         */
        @Override
        public boolean doAction(String key, IAction action) throws RemoteException {
            Log.e(TAG, "doAction() called with: key = [" + key + "], action = [" + action + "]");
            Action actionReal = action.getAction();
            if (actionReal.getType() == Action.Type.CLOSE) {
                CloseAction closeAction = (CloseAction) actionReal;
                Log.e(TAG, String.format("close with tip=%s", closeAction.mTip));

            } else if (actionReal.getType() == Action.Type.OPEN) {
                OpenAction openAction = (OpenAction) actionReal;
                Log.e(TAG, String.format("open with penColor=%d penSize=%d", openAction.mPenColor, openAction.mPenSize));
            }
            return true;
        }

        @Override
        public void addActionListener(String key, IActionListener listener) throws RemoteException {
            mActionListenerMap.put(key, listener);
        }

        @Override
        public void removeActionListener(String key) throws RemoteException {
            mActionListenerMap.remove(key);
        }
    };


}
