package ua.org.zasadnyy.visiontrainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * The extension receiver receives the extension intents and starts the
 * extension service when they arrive.
 */
public class ExtensionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.d(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "onReceive: " + intent.getAction());
        intent.setClass(context, ExtensionService.class);
        context.startService(intent);
    }
}
