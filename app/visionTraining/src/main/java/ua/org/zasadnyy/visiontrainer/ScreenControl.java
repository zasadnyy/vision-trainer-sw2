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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ua.org.zasadnyy.visiontrainer.model.Exercise;

class ScreenControl extends ControlExtension {

    public static final String TIMER_FORMAT = "%02d:%02d";
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MIDDAY = 13;

    public enum AppScreen {
        START, EXERCISE, FINISH
    }

    private final ScheduledExecutorService _scheduler = Executors.newScheduledThreadPool(1);
    private AppScreen _currentAppScreen = AppScreen.START;
    private int _currentExerciseIndex = 0;
    private boolean _isPaused = false;
    private List<Exercise> _exercises = ExerciseConfig.EXERCISES;
    private List<ScheduledFuture> _futures = new LinkedList<ScheduledFuture>();


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
                onPauseButtonClicked();
                break;
            case R.id.next_exercise_button:
                gotoNextExercise(false);
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

    private void onPauseButtonClicked() {
        _isPaused = !_isPaused;
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
        String itWillTakeOnly = mContext.getString(R.string.welcome_it_will_take_only, calculateTotalTrainingTime());

        Bundle bundle1 = new Bundle();
        bundle1.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.welcome_training_time);
        bundle1.putString(Control.Intents.EXTRA_TEXT, itWillTakeOnly);

        Bundle[] bundleData = new Bundle[1];
        bundleData[0] = bundle1;

        showLayout(R.layout.welcome_screen, bundleData);
    }

    private int calculateTotalTrainingTime() {
        int totalTimeSeconds = 0;
        for (Exercise exercise : ExerciseConfig.EXERCISES) {
            totalTimeSeconds += exercise.getSecondsDuration();
        }
        return totalTimeSeconds / SECONDS_IN_MINUTE;
    }

    private void renderExerciseScreen() {
        if (_currentExerciseIndex >= _exercises.size()) {
            _currentAppScreen = AppScreen.FINISH;
            updateLayout();
            return;
        }

        Exercise currentExercise = _exercises.get(_currentExerciseIndex);
        String name = mContext.getString(currentExercise.getName());
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
        int sewYouResId = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < MIDDAY ?
            R.string.see_you_in_the_evening : R.string.see_you_in_the_morning;
        String seeYou = mContext.getString(sewYouResId);

        Bundle bundle1 = new Bundle();
        bundle1.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.finish_see_you);
        bundle1.putString(Control.Intents.EXTRA_TEXT, seeYou);

        Bundle[] bundleData = new Bundle[1];
        bundleData[0] = bundle1;

        setScreenState(Control.Intents.SCREEN_STATE_AUTO);
        startVibrator(500, 500, 2);
        showLayout(R.layout.finish_screen, bundleData);
    }

    private void startExerciseTimer(int secondsDuration) {
        final int initialDelay = 1;
        final int[] countdown = {secondsDuration};

        final Runnable layoutUpdater = new Runnable() {
            public void run() {
                if (countdown[0] >= 0 && !_isPaused) {
                    Log.i(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "Updating timer " + countdown[0]);
                    updateTimer(countdown[0]);
                    countdown[0]--;
                }
            }
        };

        Runnable cancelTimer = new Runnable() {
            public void run() {
                if (countdown[0] < 0) {
                    Log.i(ua.org.zasadnyy.visiontrainer.ExtensionService.LOG_TAG, "Finishing timer");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gotoNextExercise(true);
                }
            }
        };

        ScheduledFuture timerHandle = _scheduler.scheduleAtFixedRate(layoutUpdater, initialDelay, 1, TimeUnit.SECONDS);
        _futures.add(timerHandle);

        ScheduledFuture cancelHandler = _scheduler.scheduleAtFixedRate(cancelTimer, 0, 500, TimeUnit.MILLISECONDS);
        _futures.add(cancelHandler);
    }

    private void gotoNextExercise(boolean vibrate) {
        cancelTimers();

        if(vibrate) {
            startVibrator(500, 500, 1);
        }
        _currentExerciseIndex++;
        updateLayout();
    }

    private void cancelTimers() {
        for (ScheduledFuture future : _futures) {
            future.cancel(true);
        }
        _futures.clear();
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
