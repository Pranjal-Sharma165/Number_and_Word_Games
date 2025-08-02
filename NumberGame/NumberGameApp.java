package ca.bcit.comp2522.termProject.NumberGame;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Entry point for the Magical Number Challenge game application.
 * Manages the welcome screen and transitions to the main game.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 3.0
 */
public class NumberGameApp extends Application
{
    private static final int    SCENE_WIDTH           = 800;
    private static final int    SCENE_HEIGHT          = 600;
    private static final int    OVERLAY_SPACING       = 20;
    private static final double FADE_DURATION_SECONDS = 1.0;

    /**
     * The main entry point for the JavaFX application.
     * Initializes the primary stage and displays the welcome screen.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(final Stage primaryStage)
    {
        showWelcomeStage(primaryStage);
    }

    /*
     * Displays the welcome stage with a background, title, and buttons.
     */
    private void showWelcomeStage(final Stage primaryStage)
    {
        final StackPane welcomeLayout;
        welcomeLayout = new StackPane();
        welcomeLayout.setStyle("-fx-background-image: url('img.jpeg'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center;");

        final VBox overlay;
        overlay = new VBox();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        overlay.setAlignment(Pos.CENTER);
        overlay.setSpacing(OVERLAY_SPACING);

        Text title = new Text("Magical Number Challenge");
        title.setStyle("-fx-font-size: 50px; -fx-font-family: 'Georgia'; -fx-font-weight: bold; " +
                "-fx-text-fill: #8A2BE2; /* Deep Purple Text */ " +
                "-fx-effect: dropshadow(gaussian, #6A0DAD, 25, 0.8, 0, 0); /* Purple Glow Effect */");

        final Button startButton;
        startButton = new Button("Start");
        startButton.getStyleClass().add("start-button");
        startButton.setOnAction(event -> showGameStage(primaryStage));

        final Button quitButton;
        quitButton = new Button("Quit");
        quitButton.getStyleClass().add("quit-button");
        quitButton.setOnAction(event -> primaryStage.close());

        overlay.getChildren().addAll(title, startButton, quitButton);
        welcomeLayout.getChildren().add(overlay);

        final Scene welcomeScene;
        welcomeScene = new Scene(welcomeLayout, SCENE_WIDTH, SCENE_HEIGHT);
        welcomeScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Magical Number Challenge");
        primaryStage.show();
    }

    /*
     * Displays the main game stage.
     */
    private void showGameStage(Stage primaryStage)
    {
        final NumberGame numberGame = new NumberGame();

        final VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(OVERLAY_SPACING);
        mainLayout.getStyleClass().add("root");

        numberGame.initializeGame(primaryStage, mainLayout);

        Scene gameScene = new Scene(mainLayout, SCENE_WIDTH, SCENE_HEIGHT);
        gameScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Smooth transition to the game scene
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(FADE_DURATION_SECONDS),
                primaryStage.getScene().getRoot());
        fadeOut.setOnFinished(event -> primaryStage.setScene(gameScene));
        fadeOut.play();
    }
}
