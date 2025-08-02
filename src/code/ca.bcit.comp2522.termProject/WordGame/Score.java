package ca.bcit.comp2522.termProject.WordGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents game scores, supporting multiple games (e.g., Word Game and Number Game).
 * Tracks the number of games played, correct/incorrect answers, and calculates total
 * and average scores.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 3.0
 */
public class Score
{
    private static final int                FIRST_ATTEMPT_POINTS             = 2;
    private static final double             DEFAULT_AVERAGE_SCORE            = 0.0;
    private static final int                DEFAULT_ZERO_COUNT               = 0;
    private static final int                FIRST_INDEX                      = 1;
    private static final int                INDEX_DATE_TIME                  = 0;
    private static final int                INDEX_NUM_GAMES_PLAYED           = 1;
    private static final int                INDEX_NUM_CORRECT_FIRST_ATTEMPT  = 2;
    private static final int                INDEX_NUM_CORRECT_SECOND_ATTEMPT = 3;
    private static final int                INDEX_NUM_INCORRECT_TWO_ATTEMPTS = 4;
    private final LocalDateTime             dateTimePlayed;
    private final int                       numGamesPlayed;
    private final int                       numCorrectFirstAttempt;
    private final int                       numCorrectSecondAttempt;
    private final int                       numIncorrectTwoAttempts;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs a Score instance with the specified statistics.
     *
     * @param dateTimePlayed          the date and time the game was played
     * @param numGamesPlayed          the number of games played
     * @param numCorrectFirstAttempt  the number of questions answered correctly on the first attempt
     * @param numCorrectSecondAttempt the number of questions answered correctly on the second attempt
     * @param numIncorrectTwoAttempts the number of questions answered incorrectly after two attempts
     */
    public Score(final LocalDateTime dateTimePlayed,
                 final int numGamesPlayed,
                 final int numCorrectFirstAttempt,
                 final int numCorrectSecondAttempt,
                 final int numIncorrectTwoAttempts)
    {
        this.dateTimePlayed = dateTimePlayed;
        this.numGamesPlayed = numGamesPlayed;
        this.numCorrectFirstAttempt = numCorrectFirstAttempt;
        this.numCorrectSecondAttempt = numCorrectSecondAttempt;
        this.numIncorrectTwoAttempts = numIncorrectTwoAttempts;
    }

    /**
     * Formats the score details into a human-readable string.
     *
     * @return a formatted string representing the score details
     */
    @Override
    public String toString()
    {
        return String.format(
                "Date and Time: %s\nGames Played: %d\nCorrect First Attempts:" +
                        " %d\nCorrect Second Attempts: %d\nIncorrect Attempts: " +
                        "%d\nScore: %d points\n",
                dateTimePlayed.format(FORMATTER),
                numGamesPlayed,
                numCorrectFirstAttempt,
                numCorrectSecondAttempt,
                numIncorrectTwoAttempts,
                getScore()
        );
    }

    /**
     * Calculates the total points based on the number of correct answers.
     *
     * @return the total points scored
     */
    public int getScore()
    {
        return (numCorrectFirstAttempt * FIRST_ATTEMPT_POINTS) + numCorrectSecondAttempt;
    }

    /**
     * Appends a score to the specified file.
     *
     * @param score    the score to append
     * @param filePath the file path where the score will be saved
     */
    public static void appendScoreToFile(final Score score,
                                         final String filePath)
    {
        final Path path = Path.of(filePath);

        try
        {
            final String formattedScore;
            formattedScore = score.toString() + System.lineSeparator();
            Files.writeString(path, formattedScore, StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        }
        catch(final IOException e)
        {
            System.err.println("Error writing to score file: " + e.getMessage());
        }
    }

    /**
     * Reads all scores from the specified file.
     *
     * @param filePath the file path to read scores from
     * @return a list of scores
     */
    public static List<Score> readScoresFromFile(final String filePath)
    {
        final List<Score> scores;
        final Path path;
        final List<String> lines;
        final List<String> block;

        scores = new ArrayList<>();
        path = Path.of(filePath);

        try
        {
            lines = Files.readAllLines(path);
            block = new ArrayList<>();

            for(final String line : lines)
            {
                if(line.trim().isEmpty())
                {
                    if(!block.isEmpty())
                    {
                        scores.add(parseScore(block));
                        block.clear();
                    }
                }
                else
                {
                    block.add(line);
                }
            }
            if (!block.isEmpty()) {
                scores.add(parseScore(block));
            }
        }
        catch(final IOException e)
        {
            System.err.println("Error reading from score file: " + e.getMessage());
        }
        return scores;
    }

    /*
     * Parses a formatted score block into a Score object.
     */
    private static Score parseScore(final List<String> block)
    {
        final LocalDateTime dateTime;
        final int numGamesPlayed;
        final int numCorrectFirstAttempt;
        final int numCorrectSecondAttempt;
        final int numIncorrectTwoAttempts;

        dateTime = LocalDateTime.parse(block.get(INDEX_DATE_TIME).split(": ")
                [FIRST_INDEX], FORMATTER);
        numGamesPlayed = Integer.parseInt(block.get(INDEX_NUM_GAMES_PLAYED).
                split(": ")[FIRST_INDEX].trim());
        numCorrectFirstAttempt = Integer.parseInt(block.get(INDEX_NUM_CORRECT_FIRST_ATTEMPT).
                split(": ")[FIRST_INDEX].trim());
        numCorrectSecondAttempt = Integer.parseInt(block.get(INDEX_NUM_CORRECT_SECOND_ATTEMPT).
                split(": ")[FIRST_INDEX].trim());
        numIncorrectTwoAttempts = Integer.parseInt(block.get(INDEX_NUM_INCORRECT_TWO_ATTEMPTS).
                split(": ")[FIRST_INDEX].trim());

        return new Score(dateTime, numGamesPlayed, numCorrectFirstAttempt, numCorrectSecondAttempt,
                numIncorrectTwoAttempts);
    }

    /**
     * Calculates the total points from the number of correct answers.
     *
     * @return the total points
     */
    public int calculateTotalPoints()
    {
        return (numCorrectFirstAttempt * FIRST_ATTEMPT_POINTS) + numCorrectSecondAttempt;
    }

    /**
     * Calculates the average score per game.
     *
     * @return the average score
     */
    public double calculateAverageScore()
    {
        if(numGamesPlayed == DEFAULT_ZERO_COUNT )
        {
            return DEFAULT_AVERAGE_SCORE ;
        }
        return (double) calculateTotalPoints() / numGamesPlayed;
    }
}
