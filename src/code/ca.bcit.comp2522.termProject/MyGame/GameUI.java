package ca.bcit.comp2522.termProject.MyGame;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

/**
 * The GameUI class handles the user interface for the Escape Room game.
 * It includes methods to initialize the UI, handle user interactions, and manage game flow.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 3.0
 */
public class GameUI
{
    private static final int    WINDOW_WIDTH                 = 800;
    private static final int    WINDOW_HEIGHT                = 600;
    private static final int    CENTRAL_BOX_SPACING          = 10;
    private static final int    STORY_LABEL_MAX_WIDTH        = 600;
    private static final int    SEQUENCE_DISPLAY_DURATION_MS = 5000;
    private static final int    TIMER_DURATION_SECONDS       = 15;
    private static final double FULL_PROGRESS                = 1.0;
    private final GameEngine    gameEngine                   = new GameEngine();
    private CountdownTimer      countdownTimer               = new CountdownTimer();
    private Label               storyLabel, levelLabel, errorLabel, sequenceLabel;
    private TextField           inputField;
    private Button              startButton, submitButton, quitButton;
    private ProgressBar         progressBar;

    /**
     * Initializes and starts the game UI.
     *
     * @param primaryStage The primary stage for the JavaFX application.
     */
    public void start(final Stage primaryStage)
    {
        // Background Image
        final ImageView backgroundImage;
        backgroundImage = new ImageView(new Image(getClass().getResource("/background.jpeg").toExternalForm()));
        backgroundImage.setFitWidth(WINDOW_WIDTH);
        backgroundImage.setFitHeight(WINDOW_HEIGHT);
        backgroundImage.setPreserveRatio(false);

        // UI Components
        storyLabel = new Label("You are trapped in a mysterious room. Memorize the passcode to escape!\n" +
                "Rule: Make sure each word/digit is separated by space");
        storyLabel.setId("story-label");
        storyLabel.setWrapText(true);
        storyLabel.setMaxWidth(STORY_LABEL_MAX_WIDTH);
        storyLabel.setAlignment(Pos.CENTER);

        levelLabel = new Label("Click Start to Begin");
        levelLabel.setId("level-label");

        sequenceLabel = new Label();
        sequenceLabel.setId("sequence-label");
        sequenceLabel.setWrapText(true);
        sequenceLabel.setMaxWidth(STORY_LABEL_MAX_WIDTH);
        sequenceLabel.setAlignment(Pos.CENTER);

        errorLabel = new Label();
        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");

        inputField = new TextField();
        inputField.setPromptText("Enter the sequence...");
        inputField.setId("input-field");
        inputField.setDisable(true);

        startButton = new Button("Start");
        startButton.setId("start-button");

        submitButton = new Button("Submit");
        submitButton.setId("submit-button");
        submitButton.setDisable(true);

        quitButton = new Button("Quit");
        quitButton.setId("quit-button");

        progressBar = new ProgressBar(1.0);
        progressBar.setVisible(false);

        VBox centralBox = new VBox(CENTRAL_BOX_SPACING, storyLabel, levelLabel, progressBar,
                sequenceLabel, inputField, errorLabel, startButton, submitButton, quitButton);
        centralBox.setId("central-box");
        centralBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(backgroundImage, centralBox);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/stylesMyGame.css").toExternalForm());

        // Button Actions
        startButton.setOnAction(e -> startGame());
        submitButton.setOnAction(e -> handleSubmit());
        inputField.setOnAction(e -> handleSubmit());
        quitButton.setOnAction(e -> Platform.exit());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Escape the Room");
        primaryStage.show();
    }

    /*
     * Starts a new game level by generating a sequence and preparing the UI.
     */
    private void startGame()
    {
        resetTimer(); // Ensure no active timer is running
        startButton.setDisable(true); // Disable Start button
        levelLabel.setText("Level: " + gameEngine.getLevel());
        storyLabel.setText("Memorize the passcode!");

        final List<String> sequence;
        sequence = gameEngine.generateSequence();
        sequenceLabel.setText(String.join(" ", sequence)); // Display the sequence

        inputField.setDisable(true);
        submitButton.setDisable(true);
        progressBar.setVisible(false);

        new Thread(() -> {
            try
            {
                Thread.sleep(SEQUENCE_DISPLAY_DURATION_MS); // Display sequence for defined duration
                Platform.runLater(() -> {
                    sequenceLabel.setText(""); // Clear sequence
                    storyLabel.setText("Enter the passcode to escape:");
                    inputField.clear();
                    inputField.setDisable(false);
                    submitButton.setDisable(false);
                    progressBar.setVisible(true);
                    countdownTimer.startCountdown(progressBar, this::handleGameOverDueToTimeout,
                            TIMER_DURATION_SECONDS); // Start timer
                });
            }
            catch(final InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /*
     * Handles the user's input submission.
     * Checks the input against the generated sequence and updates the game state.
     */
    private void handleSubmit()
    {
        final List<String> userInput;
        userInput = List.of(inputField.getText().trim().split("\\s+"));

        if(gameEngine.validateInput(userInput))
        {
            gameEngine.incrementScore();
            storyLabel.setText("Correct! Proceeding to the next level.");
            resetForNextLevel();
        }
        else
        {
            handleGameOver("Incorrect passcode! Game Over.");
        }
    }

    /*
     * Handles the game over logic due to incorrect input.
     */
    private void handleGameOver(final String message)
    {
        Platform.runLater(() -> {
            resetTimer();
            showGameOverDialog(message);
            startButton.setDisable(false); // Re-enable Start button
        });
    }

    /*
     * Handles the game over logic due to a timeout.
     */
    private void handleGameOverDueToTimeout()
    {
        Platform.runLater(() -> {
            resetTimer();
            showGameOverDialog("Time's up! You failed to escape the room.");
        });
    }

    /*
     * Displays the game over dialog with options to restart or quit.
     */
    private void showGameOverDialog(final String message)
    {
        Platform.runLater(() -> {
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(message);
            alert.setContentText("Your Score: " + gameEngine.getScore() + "\nWould you like to play again?");
            final ButtonType playAgainButton = new ButtonType("Play Again");
            final ButtonType quitButton = new ButtonType("Quit");
            alert.getButtonTypes().setAll(playAgainButton, quitButton);

            alert.showAndWait().ifPresent(response -> {
                if(response == playAgainButton)
                {
                    resetGame();
                }
                else
                {
                    Platform.exit();
                }
            });
        });
    }

    /*
     * Resets the game to its initial state.
     */
    private void resetGame()
    {
        // Reset the game engine
        gameEngine.resetGame();

        // Reset the UI
        storyLabel.setText("You are trapped in a mysterious room. Memorize the passcode to escape!\n" +
                "Rule: Make sure each word/digit is separated by space");
        levelLabel.setText("Click Start to Begin");
        sequenceLabel.setText("");
        inputField.clear();
        inputField.setDisable(true);
        submitButton.setDisable(true);
        progressBar.setProgress(1.0);
        progressBar.setVisible(false);
        startButton.setDisable(false);
    }

    /*
     * Prepares the UI and game state for the next level.
     */
    private void resetForNextLevel()
    {
        resetTimer();
        inputField.clear();
        inputField.setDisable(true);
        submitButton.setDisable(true);

        // Check if the player has completed all levels
        if(gameEngine.isLastLevel())
        {
            handleGameCompletion("Congratulations! You have completed all " + gameEngine.getTotalLevels() +
                    " levels!");
            return;
        }

        gameEngine.incrementLevel();
        startGame(); // Start the next level
    }

    /*
     * Handles the logic when the game is completed.
     */
    private void handleGameCompletion(final String message)
    {
        Platform.runLater(() -> {
            countdownTimer.stopCountdown();
            showCompletionDialog(message);
        });
    }

    /*
     * Displays the game completion dialog.
     */
    private void showCompletionDialog(final String message)
    {
        // Create the dialog box
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Completed!");
        alert.setHeaderText(message);
        alert.setContentText("Your Score: " + gameEngine.getScore() + "\nWould you like to play again?");

        // Add Play Again and Quit options
        final ButtonType playAgainButton = new ButtonType("Play Again");
        final ButtonType quitButton = new ButtonType("Quit");
        alert.getButtonTypes().setAll(playAgainButton, quitButton);

        // Show the dialog and handle user input
        alert.showAndWait().ifPresent(response -> {
            if(response == playAgainButton)
            {
                resetGame();
            }
            else if(response == quitButton)
            {
                Platform.exit();
            }
        });
    }

    /*
     * Stops any active countdown timer and creates a new instance.
     */
    private void resetTimer()
    {
        if(countdownTimer != null)
        {
            countdownTimer.stopCountdown();
        }
        countdownTimer = new CountdownTimer();
    }
}
