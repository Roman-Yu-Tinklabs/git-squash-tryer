package com.example.roman_yu.helloservicetryer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.roman_yu.helloservicetryer.services.MessengerService;

public class MainActivity extends AppCompatActivity {

    private static final String BINDING = "Binding.";
    private static final String UNBINDING = "Unbinding.";

    boolean mIsBound;
    Messenger mService = null;

    TextView mCallbackText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ((Button)findViewById(R.id.button)).setOnClickListener(mBindListener);
//        ((Button)findViewById(R.id.button2)).setOnClickListener(mUnbindListener);

        mCallbackText = (TextView) findViewById(R.id.textView);
    }

    private OnClickListener mBindListener = new OnClickListener() {
        public void onClick(View v) {
            doBindService();
        }
    };
    private OnClickListener mUnbindListener = new OnClickListener() {
        public void onClick(View v) {
            doUnbindService();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(this,
                MessengerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        mCallbackText.setText(BINDING);
    }

    void doUnbindService() {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message message =  Message.obtain(null, MessengerService.MSG_UNREGISTER_CLIENT);
                    message.replyTo = mMessenger;
                    mService.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            // Detach our existing connection.
            unbindService(mServiceConnection);
            mIsBound = false;
            mCallbackText.setText(UNBINDING);
        }
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessengerService.MSG_SET_VALUE:
                    mCallbackText.setText("Received from service: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);

            try {
                Message msg = Message.obtain(null,
                        MessengerService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null,
                        MessengerService.MSG_SET_VALUE, this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;

            mCallbackText.setText("Disconnected.");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
