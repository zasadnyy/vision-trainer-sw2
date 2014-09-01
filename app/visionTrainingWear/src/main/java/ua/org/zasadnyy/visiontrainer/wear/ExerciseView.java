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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ua.org.zasadnyy.visiontrainer.R;
import ua.org.zasadnyy.visiontrainer.utils.UiUtils;
import ua.org.zasadnyy.visiontrainer.model.Exercise;

/**
 * Created by vitaliyzasadnyy on 17.08.14.
 */
public class ExerciseView extends RelativeLayout {

    private TextView _headerTextView;
    private TextView _descriptionTextView;
    private TextView _timerTextView;

    public ExerciseView(Context context) {
        this(context, null);
    }

    public ExerciseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_exercise, this, true);

        _headerTextView = (TextView) view.findViewById(R.id.exercise_header);
        _descriptionTextView = (TextView) findViewById(R.id.exercise_description);
        _timerTextView = (TextView) findViewById(R.id.exercise_timer);
    }

    public void bind(Exercise exercise, String header) {
        _headerTextView.setText(header);
        _descriptionTextView.setText(exercise.getName());
        _timerTextView.setText(UiUtils.formatTimeLeft(exercise.getSecondsDuration()));
    }

    public void updateTimer(long timeLeft) {
        _timerTextView.setText(UiUtils.formatTimeLeft(timeLeft));
    }

}
