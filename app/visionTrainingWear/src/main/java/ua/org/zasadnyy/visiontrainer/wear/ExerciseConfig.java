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

import java.util.Arrays;
import java.util.List;

import ua.org.zasadnyy.visiontrainer.wear.model.Exercise;


/**
 * Created by vitaliyzasadnyy on 26.05.14.
 */
public final class ExerciseConfig {

    public static final int DEFAULT_EXERCISE_DURATION = 4;

    public static final List<Exercise> EXERCISES = Arrays.asList(
        new Exercise(R.string.ex1_move_left_right, DEFAULT_EXERCISE_DURATION),
        new Exercise(R.string.ex2_move_up_down, DEFAULT_EXERCISE_DURATION),
        new Exercise(R.string.ex3_cycles_clockwise, DEFAULT_EXERCISE_DURATION),
        new Exercise(R.string.ex4_cycles_counterclockwise, DEFAULT_EXERCISE_DURATION),
        new Exercise(R.string.ex5_tough_open_close, DEFAULT_EXERCISE_DURATION),
        new Exercise(R.string.ex6_move_diagonal, DEFAULT_EXERCISE_DURATION * 2),
        new Exercise(R.string.ex7_look_at_nose, DEFAULT_EXERCISE_DURATION),
        new Exercise(R.string.ex8_quickly_open_close, DEFAULT_EXERCISE_DURATION),
        new Exercise(R.string.ex9_focus_faraway, DEFAULT_EXERCISE_DURATION)
    );

}
