package ua.org.zasadnyy.visiontrainer;

import java.util.Arrays;
import java.util.List;

import ua.org.zasadnyy.visiontrainer.model.Exercise;

/**
 * Created by vitaliyzasadnyy on 26.05.14.
 */
public final class ExerciseConfig {

    public static final int DEFAULT_EXERCISE_DURATION = 20;

    public static final List<Exercise> EXERCISES = Arrays.asList(
        new Exercise("Рух направо-наліво", DEFAULT_EXERCISE_DURATION),
        new Exercise("Рух вверх-вниз", DEFAULT_EXERCISE_DURATION),
        new Exercise("Кругові обороти за годинниковою стрілкою", DEFAULT_EXERCISE_DURATION),
        new Exercise("Кругові обороти проти годинникової стрілки", DEFAULT_EXERCISE_DURATION),
        new Exercise("Сильне зжимання і розкриття очей", DEFAULT_EXERCISE_DURATION),
        new Exercise("Рух по діагоналі", DEFAULT_EXERCISE_DURATION * 2),
        new Exercise("Погляд на ніс", DEFAULT_EXERCISE_DURATION),
        new Exercise("Швидке моргання", DEFAULT_EXERCISE_DURATION),
        new Exercise("Фокусування на відстані", DEFAULT_EXERCISE_DURATION)
    );

}
