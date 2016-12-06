package com.example.roman_yu.helloservicetryer.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by roman-yu on 06/12/2016.
 */

public class MessengerService extends Service {

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_VALUE = 3;

    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    int mValue = 0;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_VALUE:
                    mValue = msg.arg1;
                    for (int i=mClients.size()-1; i>=0; i--) {
                        try {
                            mClients.get(i).send(Message.obtain(null,
                                    MSG_SET_VALUE, mValue, 0));
                        } catch (RemoteException e) {
                            // The client is dead.  Remove it from the list;
                            // we are going through the list from back to front
                            // so this is safe to do inside the loop.
                            mClients.remove(i);
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger .getBinder();
    }
}
