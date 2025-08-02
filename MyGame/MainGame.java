package ca.bcit.comp2522.termProject.MyGame;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The entry point of the "Escape the Room" game application.
 * This class initializes the JavaFX application and launches the game UI.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 1.0
 */
public class MainGame extends Application
{
    /**
     * Starts the JavaFX application and initializes the game UI.
     *
     * @param primaryStage The primary stage for the JavaFX application.
     */
    @Override
    public void start(final Stage primaryStage)
    {
        final GameUI gameUI = new GameUI();
        gameUI.start(primaryStage);
    }
}
