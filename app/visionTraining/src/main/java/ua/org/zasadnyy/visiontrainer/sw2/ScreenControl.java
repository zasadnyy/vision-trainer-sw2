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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;

import java.util.Calendar;
import java.util.List;

import ua.org.zasadnyy.visiontrainer.R;
import ua.org.zasadnyy.visiontrainer.lib.ExerciseConfig;
import ua.org.zasadnyy.visiontrainer.lib.controller.WorkoutController;
import ua.org.zasadnyy.visiontrainer.lib.model.Exercise;
import ua.org.zasadnyy.visiontrainer.lib.utils.UiUtils;
import ua.org.zasadnyy.visiontrainer.lib.view.IVibratorHelper;
import ua.org.zasadnyy.visiontrainer.lib.view.IWorkoutView;

class ScreenControl extends ControlExtension implements IWorkoutView, IVibratorHelper {

    public static final int MIDDAY = 13;

    private List<Exercise> _exercises;
    private WorkoutController _controller;
    private Exercise _currentExercise;


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

        _controller = new WorkoutController(this, this);
        renderStartScreen();
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
    public void onPause() {
        super.onPause();
        _controller.onCancelWorkout();
    }

    @Override
    public void vibrate(long[] pattern) {
        if (pattern.length < 3) {
            return;
        }

        int repeatCount = (pattern.length - 1) / 2;
        startVibrator((int) pattern[1], (int) pattern[2], repeatCount);
    }

    @Override
    public void setExercises(List<Exercise> exercises) {
        _exercises = exercises;
    }

    @Override
    public void gotoToExercise(Exercise exercise) {
        renderExerciseScreen(exercise, exercise.getSecondsDuration());
    }

    @Override
    public void updateTimer(Exercise exercise, long secondsLeft) {
        renderExerciseScreen(exercise, secondsLeft);
    }

    @Override
    public void showWelldone() {
        renderFinishScreen();
    }

    @Override
    public void onObjectClick(final ControlObjectClickEvent event) {
        Log.d(ExtensionService.LOG_TAG,
            "onObjectClick: ScreenControl click type: " + event.getClickType());

        switch (event.getLayoutReference()) {
            case R.id.start_training_button:
                _controller.onStartWorkout();
                break;
            case R.id.pause_training_button:
                _controller.onPauseWorkout();
                break;
            case R.id.next_exercise_button:
                onNextExerciseClicked();
                break;
            case R.id.restart_training_button:
                renderStartScreen();
                break;
        }
    }

    private void onNextExerciseClicked() {
        if (_currentExercise != null) {
            int index = _exercises.indexOf(_currentExercise);
            index++;
            if (index < _exercises.size()) {
                _controller.onExerciseChanged(_exercises.get(index));
            } else {
                _controller.onCancelWorkout();
                renderFinishScreen();
            }
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

    private void renderExerciseScreen(Exercise exercise, long timeLeft) {
        _currentExercise = exercise;

        String name = mContext.getString(exercise.getName());
        String number = (_exercises.indexOf(exercise) + 1) + "/" + _exercises.size();

        Bundle bundle1 = new Bundle();
        bundle1.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.exercise_name);
        bundle1.putString(Control.Intents.EXTRA_TEXT, name);

        Bundle bundle2 = new Bundle();
        bundle2.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.exercise_number);
        bundle2.putString(Control.Intents.EXTRA_TEXT, number);

        Bundle bundle3 = new Bundle();
        bundle3.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.exercise_timer);
        bundle3.putString(Control.Intents.EXTRA_TEXT, UiUtils.formatTimeLeft(timeLeft));

        Bundle[] bundleData = new Bundle[3];
        bundleData[0] = bundle1;
        bundleData[1] = bundle2;
        bundleData[2] = bundle3;

        showLayout(R.layout.exercise_screen, bundleData);
    }

    private void renderFinishScreen() {
        _currentExercise = null;

        int sewYouResId = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < MIDDAY ?
            R.string.see_you_in_the_evening : R.string.see_you_in_the_morning;
        String seeYou = mContext.getString(sewYouResId);

        Bundle bundle1 = new Bundle();
        bundle1.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.finish_see_you);
        bundle1.putString(Control.Intents.EXTRA_TEXT, seeYou);

        Bundle[] bundleData = new Bundle[1];
        bundleData[0] = bundle1;

        setScreenState(Control.Intents.SCREEN_STATE_AUTO);
        showLayout(R.layout.finish_screen, bundleData);
    }

    private long calculateTotalTrainingTime() {
        int totalTimeSeconds = 0;
        for (Exercise exercise : ExerciseConfig.EXERCISES) {
            totalTimeSeconds += exercise.getSecondsDuration();
        }
        return totalTimeSeconds / UiUtils.SECONDS_IN_MINUTE;
    }
}
