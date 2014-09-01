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

package ua.org.zasadnyy.visiontrainer.controller;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;


/**
 * Created by vitaliyzasadnyy on 31.08.14.
 */

/**
 * This is modified version of native Android CountDownTimer
 */
public abstract class CountDownTimer {

    private static final int MSG = 1;

    /**
     * Millis since epoch when alarm should stop.
     */
    private long _millisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private long _countdownInterval;
    private long _stopTimeInFuture;
    private long _postDelayMillis;
    private boolean _cancelled = false;


    /**
     * Cancel the countdown.
     * <p/>
     * Do not call it from inside CountDownTimer threads
     */
    public final void cancel() {
        _handler.removeMessages(MSG);
        _cancelled = true;
    }

    /**
     * Start the countdown.
     * * @param millisInFuture    The number of millis in the future from the call
     * to startDelayed() until the countdown is done and {@link #onFinish()}
     * is called.
     */
    public synchronized final CountDownTimer startDelayed(long millisInFuture, long countDownInterval, long preDelayMillis, long postDelayMillis) {
        _millisInFuture = millisInFuture;
        _countdownInterval = countDownInterval;
        _postDelayMillis = postDelayMillis;

        if (_millisInFuture <= 0) {
            onFinish();
            return this;
        }

        _stopTimeInFuture = SystemClock.elapsedRealtime() + _millisInFuture + preDelayMillis + _postDelayMillis;

        _handler.removeMessages(MSG);
        _handler.sendMessageDelayed(_handler.obtainMessage(MSG), preDelayMillis);
        _cancelled = false;

        return this;
    }

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();

    // handles counting down
    private Handler _handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (CountDownTimer.this) {
                if (_cancelled) {
                    return;
                }

                final long millisLeft = _stopTimeInFuture - SystemClock.elapsedRealtime() - _postDelayMillis;

                if (millisLeft <= 0) {
                    final long postDelayLeft = _stopTimeInFuture - SystemClock.elapsedRealtime();
                    if (postDelayLeft < 0) {
                        onFinish();
                    } else {
                        onTick(0);
                        sendMessageDelayed(obtainMessage(MSG), postDelayLeft);
                    }
                } else if (millisLeft < _countdownInterval) {
                    onTick(millisLeft);
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft);

                    // take into account user's onTick taking time to execute
                    long delay = lastTickStart + _countdownInterval - SystemClock.elapsedRealtime();

                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0) delay += _countdownInterval;

                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };
}