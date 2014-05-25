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
