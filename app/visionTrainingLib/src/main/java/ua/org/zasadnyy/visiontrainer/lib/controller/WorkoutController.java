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

package ua.org.zasadnyy.visiontrainer.lib.controller;

import java.util.List;

import ua.org.zasadnyy.visiontrainer.lib.ExerciseConfig;
import ua.org.zasadnyy.visiontrainer.lib.model.Exercise;
import ua.org.zasadnyy.visiontrainer.lib.utils.UiUtils;
import ua.org.zasadnyy.visiontrainer.lib.view.IVibratorHelper;
import ua.org.zasadnyy.visiontrainer.lib.view.IWorkoutView;

/**
 * Created by vitaliyzasadnyy on 21.08.14.
 */
public class WorkoutController {

    private static final long[] FINISH_EXERCISE_VIBRATOR_PATTERN = new long[]{0, 300, 0};
    private static final long[] FINISH_WORKOUT_VIBRATOR_PATTERN = new long[]{0, 300, 100, 300, 0};

    private IWorkoutView _workoutView;
    private IVibratorHelper _vibrator;
    private List<Exercise> _exercises;
    private int _currentExerciseIndex;
    private CountDownTimer _countDownTimer = new CountDownTimer() {

        public void onTick(long millisUntilFinished) {
            long secondsLeft = millisUntilFinished / UiUtils.MILISECONDS_IN_SECOND;
            _workoutView.updateTimer(_exercises.get(_currentExerciseIndex), secondsLeft);
        }

        public void onFinish() {
            _vibrator.vibrate(FINISH_EXERCISE_VIBRATOR_PATTERN);
            gotoNextExercise();
        }
    };


    public WorkoutController(IWorkoutView view, IVibratorHelper vibrator) {
        _exercises = ExerciseConfig.EXERCISES;
        _workoutView = view;
        _vibrator = vibrator;
    }

    public void onStartWorkout() {
        _workoutView.setExercises(_exercises);

        _currentExerciseIndex = 0;
        if (_currentExerciseIndex < _exercises.size()) {
            startTimer(_exercises.get(_currentExerciseIndex));
        }
    }

    public void onCancelWorkout() {
        _countDownTimer.cancel();
    }

    public void onExerciseChanged(Exercise exercise) {
        _currentExerciseIndex = _exercises.indexOf(exercise);
        startTimer(exercise);
    }

    public void onPauseWorkout() {
        //TODO implement method
    }

    private void gotoNextExercise() {
        _currentExerciseIndex++;
        if (_currentExerciseIndex < _exercises.size()) {
            startExercise(_exercises.get(_currentExerciseIndex));
        } else {
            finishWorkout();
        }
    }

    private void startExercise(Exercise exercise) {
        _workoutView.gotoToExercise(exercise);
        startTimer(exercise);
    }

    private void startTimer(final Exercise exercise) {
        long duration = exercise.getSecondsDuration() * UiUtils.MILISECONDS_IN_SECOND;
        long interval = UiUtils.MILISECONDS_IN_SECOND;
        long preDelay = UiUtils.MILISECONDS_IN_SECOND;
        long postDelay = UiUtils.MILISECONDS_IN_SECOND / 2;

        _countDownTimer.cancel();
        _countDownTimer.startDelayed(duration, interval, preDelay, postDelay);
    }

    private void finishWorkout() {
        _workoutView.showWelldone();
        _vibrator.vibrate(FINISH_WORKOUT_VIBRATOR_PATTERN);
    }

}