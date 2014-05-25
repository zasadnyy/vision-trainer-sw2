package ua.org.zasadnyy.visiontrainer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import ua.org.zasadnyy.visiontrainer.model.Exercise;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class ScreenControl extends ControlExtension {

    public static final String TIMER_FORMAT = "%02d:%02d";

    public enum AppScreen {
        START, EXERCISE, FINISH
    }

    private final ScheduledExecutorService _scheduler = Executors.newScheduledThreadPool(1);
    private AppScreen _currentAppScreen = AppScreen.START;
    private int _currentExerciseIndex = 0;

    private List<Exercise> _exercises = Arrays.asList(
        new Exercise("Ex 1", 5),
        new Exercise("Ex 2", 5),
        new Exercise("Ex 3", 5)
    );


    /**
     * Create control extension.
     *
     * @param hostAppPackageName Package name of host application.
     * @param context            The context.
     * @param handler            The handler to use.
     */
    ScreenControl(final String hostAppPackageName, final Context context, Handler handler) {
        super(context, hostAppPackageName);
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
    }

    /**
     * Return the width of the screen which this control extension supports.
     *
     * @param context The context.
     * @return The width in pixels.
     */
    public static int getSupportedControlWidth(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
    }

    /**
     * Return the height of the screen which this control extension supports.
     *
     * @param context The context.
     * @return The height in pixels.
     */
    public static int getSupportedControlHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
    }

    @Override
    public void onResume() {
        updateLayout();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "onDestroy: ScreenControl");
    }

    @Override
    public void onObjectClick(final ControlObjectClickEvent event) {
        Log.d(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG,
            "onObjectClick: ScreenControl click type: " + event.getClickType());

        // Check which view was clicked and then take the desired action.
        switch (event.getLayoutReference()) {
            case R.id.start_training_button:
                onStartTrainingButtonClicked();
                break;
            case R.id.pause_training_button:
                Log.i(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "PAUSE!!!");
                break;
            case R.id.restart_training_button:
                onRestartButtonClicked();
                break;
        }
    }

    private void onStartTrainingButtonClicked() {
        _currentAppScreen = AppScreen.EXERCISE;
        _currentExerciseIndex = 0;
        updateLayout();
    }

    private void onRestartButtonClicked() {
        _currentAppScreen = AppScreen.START;
        updateLayout();
    }

    private void updateLayout() {
        Log.i(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "updateLayout");
        switch (_currentAppScreen) {
            case START:
                renderStartScreen();
                break;
            case EXERCISE:
                renderExerciseScreen();
                break;
            case FINISH:
                renderFinishScreen();
                break;
        }
    }

    private void renderStartScreen() {
        showLayout(R.layout.welcome_screen, new Bundle[0]);
    }

    private void renderExerciseScreen() {
        if (_currentExerciseIndex >= _exercises.size()) {
            _currentAppScreen = AppScreen.FINISH;
            updateLayout();
            return;
        }

        Exercise currentExercise = _exercises.get(_currentExerciseIndex);
        String name = currentExercise.getName();
        String number = (_currentExerciseIndex + 1) + "/" + _exercises.size();

        Bundle bundle1 = new Bundle();
        bundle1.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.exercise_name);
        bundle1.putString(Control.Intents.EXTRA_TEXT, name);

        Bundle bundle2 = new Bundle();
        bundle2.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.exercise_number);
        bundle2.putString(Control.Intents.EXTRA_TEXT, number);

        Bundle bundle3 = new Bundle();
        bundle3.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.exercise_timer);
        bundle3.putString(Control.Intents.EXTRA_TEXT, formatCountdown(currentExercise.getSecondsDuration()));

        Bundle[] bundleData = new Bundle[3];
        bundleData[0] = bundle1;
        bundleData[1] = bundle2;
        bundleData[2] = bundle3;

        showLayout(R.layout.exercise_screen, bundleData);
        startExerciseTimer(currentExercise.getSecondsDuration());
    }

    private void renderFinishScreen() {
        setScreenState(Control.Intents.SCREEN_STATE_AUTO);
        startVibrator(500, 500, 2);
        showLayout(R.layout.finish_screen, new Bundle[0]);
    }

    private void startExerciseTimer(int secondsDuration) {
        int initialDelay = 1;
        final int[] countdown = {secondsDuration};

        final Runnable layoutUpdater = new Runnable() {
            public void run() {
                if(countdown[0] >= 0) {
                    Log.i(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "Updating timer " + countdown[0]);
                    updateTimer(countdown[0]);
                    countdown[0]--;
                }
            }
        };

        final ScheduledFuture beeperHandle = _scheduler.scheduleAtFixedRate(layoutUpdater, initialDelay, 1, TimeUnit.SECONDS);

        Runnable cancelTimer = new Runnable() {
            public void run() {
                Log.i(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "Finishing timer");
                _currentExerciseIndex++;
                startVibrator(500, 500, 1);
                updateLayout();
                beeperHandle.cancel(true);
            }
        };

        _scheduler.schedule(cancelTimer, secondsDuration + initialDelay * 2, TimeUnit.SECONDS);
    }

    private void updateTimer(int countdown) {
        String timerText = formatCountdown(countdown);
        Log.i(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "Rendering timer " + timerText);
        sendText(R.id.exercise_timer, timerText);
    }

    private String formatCountdown(int countdown) {
        int minutes = countdown / 60;
        int seconds = countdown % 60;
        return String.format(TIMER_FORMAT, minutes, seconds);
    }

}
