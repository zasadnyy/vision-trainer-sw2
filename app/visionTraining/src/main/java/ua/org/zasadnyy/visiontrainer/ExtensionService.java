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

package ua.org.zasadnyy.visiontrainer;

import android.os.Handler;
import android.util.Log;

import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

public class ExtensionService extends com.sonyericsson.extras.liveware.extension.util.ExtensionService {

    public static final String LOG_TAG = "EyesTrainer";

    public ExtensionService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "onCreate: ExtensionService");
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new ExtensionRegistrationInformation(this);
    }

    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    @Override
    public ControlExtension createControlExtension(String hostAppPackageName) {
        // First we check if the host application API level and screen size
        // is supported by the extension.
        boolean advancedFeaturesSupported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(
                this, hostAppPackageName);
        if (advancedFeaturesSupported) {
            return new ScreenControl(hostAppPackageName, this, new Handler());
        } else {
            throw new IllegalArgumentException("No control for: " + hostAppPackageName);
        }
    }
}
