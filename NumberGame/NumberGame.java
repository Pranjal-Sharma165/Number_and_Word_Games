package ca.bcit.comp2522.termProject.NumberGame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Concrete implementation of the Number Game.
 * Handles the game logic, UI components, and scoring for a number placement game.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 3.0
 */
public class NumberGame extends BaseNumberGame
{
    private static final int    BUTTON_SIZE               = 80;
    private static final int    IMAGE_SIZE                = 60;
    private static final double SCORE_INCREMENT           = 0.5;
    private static final int    RANDOM_NUMBER_LIMIT       = 1000;
    private static final int    LAYOUT_SPACING            = 10;
    private static final int    LAYOUT_PADDING            = 20;
    private static final int    GRID_HGAP                 = 5;
    private static final int    GRID_VGAP                 = 5;
    private static final double DEFAULT_AVERAGE           = 0.0;
    private static final int    NO_GAMES_PLAYED           = 0;
    private static final int    PREVIOUS_INDEX_OFFSET     = 1;
    private static final int    FIRST_COMPARISON_INDEX    = 1;
    private static final int    RANDOM_NUMBER_MIN         = 1;
    private static final int    CONTROL_BUTTON_SPACING    = 10;
    private int                 gamesWon                  = 0;
    private int                 gamesLost                 = 0;
    private int                 totalSuccessfulPlacements = 0;
    private Text                gameStatus;
    private double              currentScore;
    private int                 sessionGamesPlayed;
    private Stage               primaryStage;

    /**
     * Initializes the game layout and sets up the primary stage.
     *
     * @param primaryStage the primary stage of the application
     * @param layout       the main layout container
     */
    public void initializeGame(final Stage primaryStage,
                               final VBox layout)
    {
        this.primaryStage = primaryStage;

        layout.setSpacing(LAYOUT_SPACING);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(LAYOUT_PADDING));

        gameStatus = new Text("Click 'Try Again' to start.");
        gameStatus.getStyleClass().add("game-status");

        final GridPane gridPane;
        final HBox controlButtons;

        gridPane = createGridPane();
        controlButtons = createControlButtons();

        layout.getChildren().addAll(gameStatus, gridPane, controlButtons);
        resetGrid();
        generateNextNumber();
    }

    /**
     * Creates the game grid.
     * @return the GridPane layout for the game.
     */
    private GridPane createGridPane()
    {
        final GridPane gridPane;
        gridPane = new GridPane();

        gridPane.setHgap(GRID_HGAP);
        gridPane.setVgap(GRID_VGAP);
        gridPane.setAlignment(Pos.CENTER);

        for(int row = FIRST_INDEX_ROW; row < GRID_ROWS; row++)
        {
            for(int col = FIRST_INDEX_COL; col < GRID_COLS; col++)
            {
                final int currentRow = row;
                final int currentCol = col;
                final Button button = new Button();

                button.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
                button.setStyle("-fx-background-color: transparent;"); // Transparent background

                final ImageView placeholderImage;

                placeholderImage = new ImageView(new Image(getClass().getResource("/wand.png").toExternalForm()));
                placeholderImage.setFitWidth(IMAGE_SIZE);
                placeholderImage.setFitHeight(IMAGE_SIZE);
                button.setGraphic(placeholderImage);

                button.setOnAction(event -> handleButtonClick(currentRow, currentCol, button));
                gridButtons[row][col] = button;
                gridPane.add(button, col, row);
            }
        }
        return gridPane;
    }


    /*
     * Creates the control buttons (Restart and Quit).
     */
    private HBox createControlButtons()
    {
        final HBox controlButtons = new HBox(CONTROL_BUTTON_SPACING);
        controlButtons.setAlignment(Pos.CENTER);

        // Restart Button
        final Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> restartGame());
        restartButton.getStyleClass().add("restart-button");

        // Quit Button
        final Button quitButton = new Button("Quit");
        quitButton.setOnAction(event -> showFinalScoreAndExit());
        quitButton.getStyleClass().add("quit-button");

        controlButtons.getChildren().addAll(restartButton, quitButton);
        return controlButtons;
    }

    /**
     * Generates the next number to be placed on the grid.
     * Ensures no duplicate numbers are generated.
     */
    @Override
    public void generateNextNumber()
    {
        do
        {
            currentNumber = (int) (Math.random() * RANDOM_NUMBER_LIMIT) + RANDOM_NUMBER_MIN;
        }
        while(usedNumbers.contains(currentNumber)); // Ensure no duplicates

        usedNumbers.add(currentNumber);

        // Check if there are valid moves left
        if(!hasValidMoves())
        {
            final String message;
            message = "Impossible to place the next number: " + currentNumber;

            gameStatus.setText(message);
            disableAllButtons();
            showGameOverDialogWithNumber(false, currentNumber);
            return;
        }
        gameStatus.setText("Next number: " + currentNumber + " - Select a slot.");
    }

    /**
     * Validates if the current number can be placed in the specified grid cell.
     *
     * @param row the row of the grid
     * @param col the column of the grid
     * @return true if placement is valid, false otherwise
     */
    @Override
    public boolean isValidPlacement(final int row,
                                    final int col)
    {
        if(gridValues[row][col] != EMPTY_CELL_VALUE)
        {
            return false; // when the slot is already filled
        }

        final List<Integer> numbers;
        numbers = new ArrayList<>();

        for(int r = FIRST_INDEX_ROW; r < GRID_ROWS; r++)
        {
            for(int c = FIRST_INDEX_COL; c < GRID_COLS; c++)
            {
                numbers.add(gridValues[r][c]);
            }
        }

        // Place the current number into the temporary list
        final int index;
        index = row * GRID_COLS + col;
        numbers.set(index, currentNumber);

        // Remove all zeroes (empty slots) and check if the list is sorted
        final List<Integer> nonZeroNumbers;
        nonZeroNumbers = new ArrayList<>();

        for(final int num : numbers)
        {
            if(num != EMPTY_CELL_VALUE)
            {
                nonZeroNumbers.add(num);
            }
        }

        // Check if the non-zero numbers are sorted
        for(int i = FIRST_COMPARISON_INDEX; i < nonZeroNumbers.size(); i++)
        {
            if(nonZeroNumbers.get(i) < nonZeroNumbers.get(i - PREVIOUS_INDEX_OFFSET))
            {
                return false; // Not in ascending order
            }
        }
        return true; // Placement is valid
    }

    /**
     * Checks if there are valid moves remaining on the grid.
     *
     * @return true if valid moves exist, false otherwise
     */
    @Override
    public boolean hasValidMoves()
    {
        for(int row = FIRST_INDEX_ROW; row < GRID_ROWS; row++)
        {
            for(int col = FIRST_INDEX_COL; col < GRID_COLS; col++)
            {
                if(gridValues[row][col] == EMPTY_CELL_VALUE && isValidPlacement(row, col))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handles the button click event, updating the grid and checking game state.
     *
     * @param row    the row of the clicked button
     * @param col    the column of the clicked button
     * @param button the button that was clicked
     */
    @Override
    public void handleButtonClick(final int row,
                                  final int col,
                                  final Button button) 
    {
        if(!isValidPlacement(row, col)) 
        {
            gameStatus.setText("Invalid move! Game over.");
            disableAllButtons();
            showGameOverDialogWithNumber(false, currentNumber);
            return;
        }

        // Place the number in the grid
        gridValues[row][col] = currentNumber;

        // Display the number on the button
        button.setText(String.valueOf(currentNumber));
        button.setGraphic(null); // Remove the wand image after placing the number
        button.setStyle("-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #8A2BE2; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(gaussian, #6A0DAD, 15, 0.8, 0, 0);");

        button.setDisable(true);
        currentScore += SCORE_INCREMENT; // Increment the score for a valid placement
        successfulPlacements++;

        if(isGridFull()) 
        {
            showGameOverDialogWithNumber(true, currentNumber);
        } 
        else 
        {
            generateNextNumber();
        }
    }

    /*
     * Displays a game over dialog with the option to restart or quit.
     */
    private void showGameOverDialogWithNumber(final boolean success,
                                              final int nextNumber)
    {
        sessionGamesPlayed++;
        totalSuccessfulPlacements += successfulPlacements;

        if(success) 
        {
            gamesWon++;
        } 
        else 
        {
            gamesLost++;
        }

        final double               averagePlacements;
        final String               headerText;
        final String               message;
        final Alert                alert;
        final ButtonType           playAgain;
        final ButtonType           quit;
        final Optional<ButtonType> result;

        averagePlacements = sessionGamesPlayed > NO_GAMES_PLAYED ?
                (double) totalSuccessfulPlacements / sessionGamesPlayed : DEFAULT_AVERAGE;

        headerText = success ? "Congratulations!" : "Game Over!";

        message = success ?
                "You completed the grid! Your score: " + String.format("%02d", (int) currentScore) :
                "Game Over! Impossible to place the next number: " + nextNumber + ". Try again?";

        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(headerText);
        alert.setContentText(message + "\n\n" +
                "Score Summary:\n" +
                "Games Played: " + String.format("%02d", sessionGamesPlayed) + "\n" +
                "Games Won: " + String.format("%02d", gamesWon) + "\n" +
                "Games Lost: " + String.format("%02d", gamesLost) + "\n" +
                "Total Successful Placements: " + String.format("%02d", totalSuccessfulPlacements) + "\n" +
                "Average Placements Per Game: " + String.format("%.2f", averagePlacements));

        playAgain = new ButtonType("Try Again");
        quit = new ButtonType("Quit");

        alert.getButtonTypes().setAll(playAgain, quit);

        result = alert.showAndWait();

        if(result.isPresent() && result.get() == playAgain)
        {
            restartGame();
        }
        else
        {
            showFinalScoreAndExit();
        }
    }

    /*
     * Displays the final score summary and exits the application.
     */
    private void showFinalScoreAndExit()
    {
        final double averagePlacements = sessionGamesPlayed > NO_GAMES_PLAYED ?
                (double) totalSuccessfulPlacements / sessionGamesPlayed : DEFAULT_AVERAGE;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session Summary");
        alert.setHeaderText("Thank you for playing!");
        alert.setContentText("Final Score Summary:\n" +
                "Games Played: " + String.format("%02d", sessionGamesPlayed) + "\n" +
                "Games Won: " + String.format("%02d", gamesWon) + "\n" +
                "Games Lost: " + String.format("%02d", gamesLost) + "\n" +
                "Total Successful Placements: " + String.format("%02d", totalSuccessfulPlacements) + "\n" +
                "Average Placements Per Game: " + String.format("%.2f", averagePlacements));

        alert.showAndWait();

        if(primaryStage != null)
        {
            primaryStage.close();
        }
    }

    /*
     * Resets the game state and restarts the game.
     */
    private void restartGame()
    {
        resetGrid();
        for(int row = FIRST_INDEX_ROW; row < GRID_ROWS; row++)
        {
            for(int col = FIRST_INDEX_COL; col < GRID_COLS; col++)
            {
                final Button button = gridButtons[row][col];

                // Reset button text and graphic
                button.setText(""); // Clear text

                // Reset to placeholder image with proper size
                final ImageView placeholderImage = new ImageView(new Image
                        (getClass().getResource("/wand.png").toExternalForm()));

                placeholderImage.setFitWidth(IMAGE_SIZE);
                placeholderImage.setFitHeight(IMAGE_SIZE);
                button.setGraphic(placeholderImage);

                // Reset button size and style
                button.setMaxSize(BUTTON_SIZE, BUTTON_SIZE);
                button.setMinSize(BUTTON_SIZE, BUTTON_SIZE);
                button.setPrefSize(BUTTON_SIZE, BUTTON_SIZE); // Ensure consistent size
                button.setStyle("-fx-background-color: transparent;"); // Transparent background
            }
        }

        // Reset game variables
        currentScore = 0;
        successfulPlacements = 0;
        generateNextNumber();
        gameStatus.setText("Game restarted! Next number: " + currentNumber);
    }


}