package net.m3aak.parentapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Android Developer-1 on 23-08-2016.
 */
public class UpdateChatListService  extends Service {

    private final IBinder binder = new LocalBinder();
    // Registered callbacks
    private ServiceCallbacks serviceCallback1;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e("---------- ","In Service Start");
        try {
            serviceCallback1.UpdateChatList();
        }catch (Exception e) {e.printStackTrace();}
        stopSelf();
    }

    //Replacement of onStrat(Intent intent, int startId)
   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }*/


    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        public UpdateChatListService getService() {
            // Return this instance of MyService so clients can call public methods
            return UpdateChatListService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallback1 = callbacks;
    }
}