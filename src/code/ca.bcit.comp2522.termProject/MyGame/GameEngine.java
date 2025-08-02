package ca.bcit.comp2522.termProject.MyGame;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * The GameEngine class handles the core logic and state of the "Escape the Room" game.
 * It manages levels, scores, and the generation of passcode sequences.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 2.0
 */
public class GameEngine
{
    private static final int TOTAL_LEVELS           = 10;
    private static final int INITIAL_LEVEL          = 1;
    private static final int INITIAL_SCORE          = 0;
    private static final int RANDOM_INT_UPPER_BOUND = 10;
    private static final int RANDOM_INT_LOWER_BOUND = 0;

    private final Random random = new Random();
    private static final String[] WORDS = {
            "key", "lock", "door", "safe", "code", "clue", "trap", "exit", "map", "note"
    }; // Array of words for sequence generation

    private List<String> currentSequence;
    private int level = INITIAL_LEVEL;
    private int score = INITIAL_SCORE;

    /**
     * Generates a sequence for the current level.
     * The sequence length depends on the current level.
     *
     * @return The generated sequence as a list of strings.
     */
    public List<String> generateSequence()
    {
        currentSequence = random.ints(level, RANDOM_INT_LOWER_BOUND, RANDOM_INT_UPPER_BOUND)
                .mapToObj(i -> random.nextBoolean() ? String.valueOf(i) : WORDS[random.nextInt(WORDS.length)])
                .collect(Collectors.toList());
        return currentSequence;
    }

    /**
     * Validates the user's input against the current sequence.
     *
     * @param input The user's input sequence.
     * @return True if the input matches the current sequence, false otherwise.
     */
    public boolean validateInput(final List<String> input)
    {
        return input.equals(currentSequence);
    }

    /**
     * Increments the current level.
     */
    public void incrementLevel()
    {
        if(level < TOTAL_LEVELS)
        {
            level++;
        }
    }

    /**
     * Increments the player's score.
     */
    public void incrementScore()
    {
        score++;
    }

    /**
     * Gets the player's current score.
     *
     * @return The player's score.
     */
    public int getScore()
    {
        return score;
    }

    /**
     * Gets the current level.
     *
     * @return The current level.
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * Gets the total number of levels in the game.
     *
     * @return The total levels.
     */
    public int getTotalLevels()
    {
        return TOTAL_LEVELS;
    }

    /**
     * Checks if the player has reached the last level.
     *
     * @return True if the player is on the last level, false otherwise.
     */
    public boolean isLastLevel()
    {
        return level == TOTAL_LEVELS;
    }

    /**
     * Resets the game state to the initial level and score.
     */
    public void resetGame()
    {
        level = INITIAL_LEVEL;
        score = INITIAL_SCORE;
    }
}
