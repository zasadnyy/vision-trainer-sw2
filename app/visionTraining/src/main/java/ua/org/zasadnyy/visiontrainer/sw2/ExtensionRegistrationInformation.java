/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Vitaliy Zasadnyy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ua.org.zasadnyy.visiontrainer.sw2;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

import java.util.UUID;

import ua.org.zasadnyy.visiontrainer.R;

/**
 * Provides information needed during extension registration.
 */
public class ExtensionRegistrationInformation extends RegistrationInformation {

    private static final String EXTENSION_KEY_PREF = "EXTENSION_KEY_PREF";

    private final Context _context;
    private String _extensionKey;


    /**
     * Creates a control registration object.
     *
     * @param context The context.
     */
    protected ExtensionRegistrationInformation(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        _context = context;
    }

    @Override
    public int getRequiredControlApiVersion() {
        // This extension supports all accessories from Control API level 1 and
        // up.
        return 1;
    }

    @Override
    public int getTargetControlApiVersion() {
        return 2;
    }

    @Override
    public int getRequiredSensorApiVersion() {
        return API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredNotificationApiVersion() {
        return API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        return API_NOT_REQUIRED;
    }

    @Override
    public boolean supportsLowPowerMode() {
        return true;
    }

    /**
     * Return the extension registration information. Specify the properties of
     * the extension that will be used when it is registered.
     *
     * @return The registration configuration.
     */
    @Override
    public ContentValues getExtensionRegistrationConfiguration() {
        String iconHostApp = ExtensionUtils.getUriString(_context, R.drawable.ic_launcher);
        String iconExtension = ExtensionUtils.getUriString(_context, R.drawable.ic_extension);
        String iconExtension48 = ExtensionUtils.getUriString(_context, R.drawable.ic_extension48);

        ContentValues values = new ContentValues();

        values.put(Registration.ExtensionColumns.CONFIGURATION_ACTIVITY,
            PreferenceActivity.class.getName());
        values.put(Registration.ExtensionColumns.CONFIGURATION_TEXT,
            _context.getString(R.string.configuration_text));
        values.put(Registration.ExtensionColumns.NAME, _context.getString(R.string.extension_name));
        values.put(Registration.ExtensionColumns.EXTENSION_KEY, getExtensionKey());
        values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostApp);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI, iconExtension);
        values.put(Registration.ExtensionColumns.EXTENSION_48PX_ICON_URI, iconExtension48);
        values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
            getRequiredNotificationApiVersion());
        values.put(Registration.ExtensionColumns.PACKAGE_NAME, _context.getPackageName());

        return values;
    }

    @Override
    public boolean isDisplaySizeSupported(int width, int height) {
        return (width == ScreenControl.getSupportedControlWidth(_context) && height == ScreenControl
            .getSupportedControlHeight(_context));
    }

    /**
     * A basic implementation of get_extensionKey
     * Returns and saves a random string based on UUID.randomUUID()
     * <p/>
     * Note that this implementation doesn't guarantee random numbers on Android 4.3 and older. See <a href="https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html">https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html</a>
     *
     * @return A saved key if it exists, otherwise a randomly generated one.
     * @see com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation#get_extensionKey()
     */
    @Override
    public synchronized String getExtensionKey() {
        if (TextUtils.isEmpty(_extensionKey)) {
            // Retrieve key from preferences
            SharedPreferences pref = _context.getSharedPreferences(EXTENSION_KEY_PREF,
                Context.MODE_PRIVATE);
            _extensionKey = pref.getString(EXTENSION_KEY_PREF, null);
            if (TextUtils.isEmpty(_extensionKey)) {
                // Generate a random key if not found
                _extensionKey = UUID.randomUUID().toString();
                pref.edit().putString(EXTENSION_KEY_PREF, _extensionKey).commit();
            }
        }
        return _extensionKey;
    }
}
