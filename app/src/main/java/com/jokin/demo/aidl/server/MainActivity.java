package com.jokin.demo.aidl.server;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jokin.demo.aidl.sdk.IAction;
import com.jokin.demo.aidl.sdk.Isdk;
import com.jokin.demo.aidl.sdk.actions.CloseAction;
import com.jokin.demo.aidl.sdk.actions.OpenAction;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ActionService.class);
        intent.setAction("");
        startService(intent);

        initLocalService();
        initClient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
    }

    /////////////// Local Service /////////////////

    private void initLocalService() {
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, mLocalServiceConnection, BIND_AUTO_CREATE);

        findViewById(R.id.openActionLocal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocalService == null) {
                    return;
                }
                mLocalService.open(Color.BLACK, 10);
            }
        });
        findViewById(R.id.closeActionLocal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocalService == null) {
                    return;
                }
                mLocalService.close("本地要关闭拉！");
            }
        });
    }

    private LocalService mLocalService;
    private ServiceConnection mLocalServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalService.LocalBinder localBinder = (LocalService.LocalBinder) service;
            mLocalService = localBinder.getLocalService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocalService = null;
        }
    };


    /////////////// Remote Service /////////////////

    private void initClient() {
        Intent intentService = new Intent(this, ActionService.class);
        intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bindService(intentService, mServiceConnection, BIND_AUTO_CREATE);

        findViewById(R.id.openAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mIsdk.doAction("open", new IAction(new OpenAction(Color.BLACK, 10)));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.closeAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mIsdk.doAction("close", new IAction(new CloseAction("要关闭拉！")));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }

    private Isdk mIsdk;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
            mIsdk = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");
            //通过服务端onBind方法返回的binder对象得到IMyService的实例，得到实例就可以调用它的方法了
            mIsdk = Isdk.Stub.asInterface(service);
        }
    };
}
