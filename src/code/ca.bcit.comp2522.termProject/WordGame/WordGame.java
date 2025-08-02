package ca.bcit.comp2522.termProject.WordGame;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Implements the Word Game logic.
 * Groups scores for all games played in a single session and displays the highest score ever recorded.
 *
 * @author Pranjal Sharma [A01396406]
 * @version 2.0
 */
public class WordGame
{
    // Constants for game configuration
    private static final int NUM_QUESTIONS       = 10;
    private static final int NUM_QUESTION_TYPES  = 3;
    private static final int QUESTION_BY_CAPITAL = 0;
    private static final int QUESTION_BY_COUNTRY = 1;
    private static final int QUESTION_BY_FACT    = 2;

    // Game state
    private final World world;
    private final Random random;
    private List<Country> countries;

    // Session-level accumulators
    private int sessionGamesPlayed;
    private int sessionCorrectFirstAttempt;
    private int sessionCorrectSecondAttempt;
    private int sessionIncorrectAnswers;

    /**
     * Initializes a new instance of the WordGame.
     */
    public WordGame()
    {
        this.world                       = new World();
        this.random                      = new Random();
        this.sessionGamesPlayed          = 0;
        this.sessionCorrectFirstAttempt  = 0;
        this.sessionCorrectSecondAttempt = 0;
        this.sessionIncorrectAnswers     = 0;
    }

    /**
     * Starts the game by presenting questions to the player.
     * Groups scores for all games in a single session.
     */
    public void play()
    {
        final Scanner scanner = new Scanner(System.in);
        boolean keepPlaying = true;

        while(keepPlaying)
        {
            System.out.println("Starting a new game!");

            // Per-game counters
            int correctFirstAttempt = 0;
            int correctSecondAttempt = 0;
            int incorrectAnswers = 0;

            countries = new ArrayList<>(world.getAllCountries().values());

            for(int i = 0; i < NUM_QUESTIONS; i++)
            {
                final Country country;
                final int questionType;
                boolean correct;

                country = countries.get(random.nextInt(countries.size()));
                questionType = random.nextInt(NUM_QUESTION_TYPES);
                correct = false;

                switch(questionType)
                {
                    case QUESTION_BY_CAPITAL:
                        System.out.println("Ques: What country has the capital " +
                                country.getCapitalCityName() + "?");
                        correct = handleAnswer(scanner, country.getName());
                        break;

                    case QUESTION_BY_COUNTRY:
                        System.out.println("Ques: What is the capital of " +
                                country.getName() + "?");
                        correct = handleAnswer(scanner, country.getCapitalCityName());
                        break;

                    case QUESTION_BY_FACT:
                        String randomFact = country.getFacts()[random.nextInt(country.
                                getFacts().length)];
                        System.out.println("Ques: Which country has fact: " + randomFact);
                        correct = handleAnswer(scanner, country.getName());
                        break;
                }

                if(correct)
                {
                    System.out.println("CORRECT!");
                    correctFirstAttempt++;
                }
                else
                {
                    System.out.println("Try again:");
                    correct = handleAnswer(scanner, country.getName());

                    if(correct)
                    {
                        System.out.println("CORRECT!");
                        correctSecondAttempt++;
                    }
                    else
                    {
                        incorrectAnswers++;
                        System.out.println("The correct answer was: " +
                                (questionType == QUESTION_BY_COUNTRY ?
                                        country.getCapitalCityName() : country.getName()));
                    }
                }
            }

            // Update session totals with per-game results
            sessionGamesPlayed++;
            sessionCorrectFirstAttempt  += correctFirstAttempt;
            sessionCorrectSecondAttempt += correctSecondAttempt;
            sessionIncorrectAnswers     += incorrectAnswers;

            System.out.println("Do you want to play another game? (Y/N)");

            String response;

            while(true)
            {
                response = scanner.nextLine().trim().toUpperCase();
                if(response.equals("Y") || response.equals("N"))
                {
                    break; // Valid input, exit the loop
                }
                else
                {
                    System.out.println("Invalid input. Please enter 'Y' for yes or 'N' for no:");
                }
            }
            keepPlaying = response.equals("Y");
        }
        saveSessionScore();
    }

    /**
     * Handles the player's answer and checks if it matches the correct answer.
     *
     * @param scanner       the scanner for user input
     * @param correctAnswer the correct answer to the question
     * @return true if the player's answer matches the correct answer, false otherwise
     */
    private boolean handleAnswer(final Scanner scanner,
                                 final String correctAnswer)
    {
        final String userAnswer = scanner.nextLine().trim(); // Trim extra spaces
        return userAnswer.equalsIgnoreCase(correctAnswer.trim()); // Case-insensitive comparison
    }

    /**
     * Saves the aggregated score for the session to the file and displays session and high
     * score details.
     */
    private void saveSessionScore()
    {
        // Create a Score object for the session
        Score sessionScore = new Score(
                LocalDateTime.now(),
                sessionGamesPlayed,
                sessionCorrectFirstAttempt,
                sessionCorrectSecondAttempt,
                sessionIncorrectAnswers
        );

        final String scoreFile;
        final double sessionAverageScore;
        final List<Score> scores;
        final int highScore;

        scoreFile = "score.txt";

        Score.appendScoreToFile(sessionScore, scoreFile);

        sessionAverageScore = sessionScore.calculateAverageScore();
        System.out.println("Your average score for this session was: " +
                sessionAverageScore);

        scores = Score.readScoresFromFile(scoreFile);
        highScore = scores.stream().mapToInt(Score::getScore).max().orElse(0);
        System.out.println("The highest score in this game is: " + highScore);

        if(sessionScore.getScore() >= highScore)
        {
            System.out.println("CONGRATULATIONS! You achieved the highest score with " +
                    sessionScore.getScore() + " points!");
        }
    }
}
