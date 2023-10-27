package com.toyon.democnn.util;

import android.util.Log;

import com.atakmap.comms.CommsLogger;
import com.atakmap.coremap.cot.event.CotEvent;

/**
 * This class is an example template which just print incoming CoT messages
 * to the console. This is not very efficient and will negatively impact performance.
 *
 * Use by creating an instance
 *      DemoCommLogger logger = new DemoCommLogger();
 *
 * Register Logger when pane is visible or on plugin loaded
 *      CommsMapComponent.getInstance().registerCommsLogger(logger);
 *
 * Unregister Logger when pane is not visible or plugin is disposed
 *      CommsMapComponent.getInstance().unregisterCommsLogger(logger);
 */
public class DemoCommLogger implements CommsLogger {

    private final String TAG = DemoCommLogger.class.getSimpleName();

    public void logSend(CotEvent msg, String destination) {
        // handle a CoT event being sent to the specified destination
    }
    public void logSend(CotEvent msg, String[] toUIDs) {
        // handle a CoT event being sent to the specified UIDs
    }
    public void logReceive(CotEvent msg, String rxid, String server) {
        // handle a CoT event being received
        Log.d(TAG, "REC: " + msg.toString());
    }
    public void dispose() {
        // perform any cleanup
    }

}
