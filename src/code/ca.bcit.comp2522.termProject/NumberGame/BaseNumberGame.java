package ca.bcit.comp2522.termProject.NumberGame;

import javafx.scene.control.Button;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class for the Number Game.
 * Provides common functionality and shared data structures
 * for managing the game grid and tracking game progress.
 * Subclasses must implement the specific game logic.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 2.0
 */
public abstract class BaseNumberGame implements NumberGameRules
{
    protected static final int   GRID_ROWS        = 4;
    protected static final int   GRID_COLS        = 5;
    protected static final int   EMPTY_CELL_VALUE = 0;
    protected static final int   FIRST_INDEX_ROW  = 0;
    protected static final int   FIRST_INDEX_COL  = 0;
    protected final Button[][]   gridButtons      = new Button[GRID_ROWS][GRID_COLS];
    protected final int[][]      gridValues       = new int[GRID_ROWS][GRID_COLS];
    protected final Set<Integer> usedNumbers      = new HashSet<>();
    protected int                successfulPlacements;
    protected static int         currentNumber;

    /**
     * Resets the game grid to its initial state for a new game.
     * Clears all cell values, resets UI buttons, and clears the set of used numbers.
     */
    public void resetGrid()
    {
        for(int row = FIRST_INDEX_ROW; row < GRID_ROWS; row++)
        {
            for(int col = FIRST_INDEX_COL; col < GRID_COLS; col++)
            {
                gridButtons[row][col].setText("[ ]");
                gridButtons[row][col].setDisable(false);
                gridValues[row][col] = EMPTY_CELL_VALUE;
            }
        }
        usedNumbers.clear();
        successfulPlacements = EMPTY_CELL_VALUE;
    }

    /**
     * Checks if the game grid is full (no empty cells remaining).
     *
     * @return true if all grid cells are filled, false otherwise
     */
    public boolean isGridFull()
    {
        for(int row = FIRST_INDEX_ROW; row < GRID_ROWS; row++)
        {
            for(int col = FIRST_INDEX_COL; col < GRID_COLS; col++)
            {
                if(gridValues[row][col] == EMPTY_CELL_VALUE)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Disables all buttons on the game grid.
     * This is typically used when the game is over or paused.
     */
    public void disableAllButtons()
    {
        for(int row = FIRST_INDEX_ROW; row < GRID_ROWS; row++)
        {
            for(int col = FIRST_INDEX_COL; col < GRID_COLS; col++)
            {
                gridButtons[row][col].setDisable(true);
            }
        }
    }

    /**
     * Handles a button click event on the game grid.
     * Subclasses must provide the specific behavior for button clicks.
     *
     * @param row    the row of the clicked button
     * @param col    the column of the clicked button
     * @param button the button that was clicked
     */
    public abstract void handleButtonClick(final int row,
                                           final int col,
                                           final Button button);
}
