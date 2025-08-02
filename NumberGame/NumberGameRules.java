package ca.bcit.comp2522.termProject.NumberGame;

/**
 * Represents the rules and logic for the Number Game.
 * This interface defines the methods required to generate numbers,
 * validate moves, and check the game's current state.
 * Implementing classes must provide the concrete logic for these methods.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 1.0
 */
public interface NumberGameRules
{

    /**
     * Generates the next number to be placed on the board.
     */
    void generateNextNumber();

    /**
     * Validates if placing a number at the given row and column is allowed.
     *
     * @param row the row index for placement
     * @param col the column index for placement
     * @return true if placement is valid, false otherwise
     */
    boolean isValidPlacement(final int row, final int col);

    /**
     * Checks if there are valid moves left on the board.
     *
     * @return true if valid moves exist, false otherwise
     */
    boolean hasValidMoves();
}
