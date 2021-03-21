package com.moto.googlemaps;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyNavigationService extends Service {
    public MyNavigationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}