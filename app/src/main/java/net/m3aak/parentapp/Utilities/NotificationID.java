package net.m3aak.parentapp.Utilities;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by RWS 6 on 12/1/2016.
 */
public class NotificationID {

    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}
