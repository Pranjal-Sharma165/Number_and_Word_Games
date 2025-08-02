package ca.bcit.comp2522.termProject.MyGame;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A countdown timer utility that updates a progress bar and executes a timeout action when the timer expires.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 2.0
 */
public class CountdownTimer
{
    private static final long TIMER_INITIAL_DELAY_MS = 0L;
    private static final long TIMER_PERIOD_MS = 1000L;
    private static final double FULL_PROGRESS = 1.0;
    private static final int TIME_EXPIRED = 0;
    private Timer timer;
    private int timeLeft;

    /**
     * Starts a countdown timer with the specified duration.
     * The timer updates the progress bar and invokes the provided action when time runs out.
     *
     * @param progressBar      The progress bar to visually represent the countdown.
     * @param onTimeout        The action to execute when the countdown reaches zero.
     * @param durationInSeconds The duration of the countdown in seconds.
     */
    public void startCountdown(final ProgressBar progressBar,
                               final Runnable onTimeout,
                               final int durationInSeconds)
    {
        timeLeft = durationInSeconds;
        progressBar.setProgress(FULL_PROGRESS); // Start at full progress
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                Platform.runLater(() -> {
                    if(timeLeft <= TIME_EXPIRED)
                    {
                        timer.cancel();
                        onTimeout.run(); // Timeout action
                    }
                    else
                    {
                        timeLeft--;
                        progressBar.setProgress(timeLeft / (double) durationInSeconds);
                    }
                });
            }
        }, TIMER_INITIAL_DELAY_MS, TIMER_PERIOD_MS); // Run every second
    }

    /**
     * Stops the countdown timer if it is running.
     */
    public void stopCountdown()
    {
        if(timer != null)
        {
            timer.cancel();
        }
    }
}
