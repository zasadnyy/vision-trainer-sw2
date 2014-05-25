package ua.org.zasadnyy.visiontrainer.model;

/**
 * Created by vitaliyzasadnyy on 25.05.14.
 */
public class Exercise {

    private String _name;
    private int _secondsDuration;

    public Exercise(String name, int secondsDuration) {
        this._name = name;
        this._secondsDuration = secondsDuration;
    }

    public String getName() {
        return _name;
    }

    public int getSecondsDuration() {
        return _secondsDuration;
    }
}
