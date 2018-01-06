package net.m3aak.parentapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Android Developer-1 on 03-09-2016.
 */
public class UpdateBlinkService extends Service {

    private final IBinder binder = new LocalBinder();
    // Registered callbacks
    private UpdateBlinkServiceCallback serviceCallback1;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        try {
            serviceCallback1.UpdateBlink();
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
        public UpdateBlinkService getService() {
            // Return this instance of MyService so clients can call public methods
            return UpdateBlinkService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(UpdateBlinkServiceCallback callbacks) {
        serviceCallback1 = callbacks;
    }
}