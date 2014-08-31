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

package ua.org.zasadnyy.visiontrainer.wear.view;

import android.app.Activity;
import android.content.Context;
import android.support.wearable.view.GridPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.org.zasadnyy.visiontrainer.wear.R;
import ua.org.zasadnyy.visiontrainer.wear.model.Exercise;

/**
 * Created by vitaliyzasadnyy on 17.08.14.
 */
public class ExercisesGridAdapter extends GridPagerAdapter {

    private Map<Exercise, ExerciseView> _views;
    private List<Exercise> _exercises;
    private Context _context;

    public ExercisesGridAdapter(Activity activity, List<Exercise> exercises) {
        _views = new HashMap<>();
        _context = activity;
        _exercises = exercises;
    }

    @Override
    public int getRowCount() {
        return _exercises.size() + 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 1;
    }

    @Override
    protected Object instantiateItem(ViewGroup container, int row, int column) {
        View view;

        boolean isLastRow = row == getRowCount() - 1;
        if (isLastRow) {
            view = instantiateWelldoneView(container);
        } else {
            view = instantiateExerciseView(row);
        }

        container.addView(view);

        return view;
    }

    private View instantiateWelldoneView(ViewGroup container) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.view_welldone, container, false);
    }

    private ExerciseView instantiateExerciseView(int row) {
        Exercise exercise = _exercises.get(row);

        ExerciseView view = new ExerciseView(_context);
        view.bind(exercise, _context.getString(R.string.exercise_number, row + 1, _exercises.size()));

        _views.put(exercise, view);
        return view;
    }

    @Override
    protected void destroyItem(ViewGroup container, int row, int col, Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int findExerciseRow(Exercise exercise) {
        return _exercises.indexOf(exercise);
    }

    public ExerciseView getViewForExercise(Exercise exercise) {
        return _views.get(exercise);
    }

    public Exercise getExerciseInRow(int row) {
        return _exercises.get(row);
    }
}
