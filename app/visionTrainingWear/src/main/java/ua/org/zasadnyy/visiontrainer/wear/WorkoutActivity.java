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

package ua.org.zasadnyy.visiontrainer.wear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;

import java.util.List;

import ua.org.zasadnyy.visiontrainer.R;
import ua.org.zasadnyy.visiontrainer.lib.controller.WorkoutController;
import ua.org.zasadnyy.visiontrainer.lib.model.Exercise;
import ua.org.zasadnyy.visiontrainer.lib.view.IVibratorHelper;
import ua.org.zasadnyy.visiontrainer.lib.view.IWorkoutView;

public class WorkoutActivity extends Activity implements IWorkoutView, IVibratorHelper, GridViewPager.OnPageChangeListener {

    public static final int DONT_REPEAT_VIBRATOR = -1;
    private GridViewPager _exercisesGrid;
    private WorkoutController _controller;
    private ExercisesGridAdapter _gridAdapter;
    private Vibrator _vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workout);
        _vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        _controller = new WorkoutController(WorkoutActivity.this, WorkoutActivity.this);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                _exercisesGrid = (GridViewPager) stub.findViewById(R.id.exercises_grid);
                _exercisesGrid.setOnPageChangeListener(WorkoutActivity.this);
                _controller.onStartWorkout();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        _controller.onCancelWorkout();
    }

    @Override
    public void setExercises(List<Exercise> exercises) {
        _gridAdapter = new ExercisesGridAdapter(this, exercises);
        _exercisesGrid.setAdapter(_gridAdapter);
    }

    @Override
    public void gotoToExercise(Exercise exercise) {
        int column = 0;
        int row = _gridAdapter.findExerciseRow(exercise);
        _exercisesGrid.setCurrentItem(row, column, true);
    }

    @Override
    public void updateTimer(Exercise exercise, long secondsLeft) {
        ExerciseView view = _gridAdapter.getViewForExercise(exercise);
        view.updateTimer(secondsLeft);
    }

    @Override
    public void showWelldone() {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.welldone));
        startActivity(intent);
        finish();
    }

    @Override
    public void vibrate(long[] pattern) {
        if (_vibrator.hasVibrator()) {
            _vibrator.vibrate(pattern, DONT_REPEAT_VIBRATOR);
        }
    }

    @Override
    public void onPageSelected(int row, int column) {
        Exercise exercise = _gridAdapter.getExerciseInRow(row);
        _controller.onExerciseChanged(exercise);
    }

    @Override
    public void onPageScrolled(int row, int column, float rowOffset, float columnOffset, int rowOffsetPixels, int columnOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}