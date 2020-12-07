package Jemand;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

//stolen from https://github.com/ntpeters/Mastermind

/**
 * The game Mastermind
 * A player is given some number of tries to guess a secret code of some length created by the computer.
 */
public class Mastermind {
    private boolean _duplicatesAllowed;
    private int _numberOfGuesses;
    private int _codeLength;
    private int _minCodeValue;
    private int _maxCodeValue;
    private int _currentGuess;
    private boolean started = false;

    /*
        The secret code.
        Key   = A code value
        Value = A set of the indices this value exists at
        Storing the code in this manner provides constant time checks if a value is present in the code, and
        constant time checks for if that value exists at a given index. In this way we can always evaluate a guess from
        the user in linear time, relative to the length of the guess.
     */
    private HashMap<Integer, HashSet<Integer>> _code;

    /**
     * Default constructor. Defaults: duplicates = false, code length = 4, guesses = 10, value range = (1,6)
     */
    public Mastermind() {
        _codeLength = 4;
        _minCodeValue = 1;
        _maxCodeValue = 6;
        _numberOfGuesses = 10;
        _duplicatesAllowed = false;
        _currentGuess = 0;
    }

    public boolean hasStarted() {
        return started;
    }

    /**
     * Starts the game loop
     */
    public void play(final TextChannel channel, Long[] users) {
        started = true;
        generateCode();

        String content = "Welcome to Mastermind!\n"
                + String.format("I'm thinking of a %d digit code, with numbers between %d and %d.\n", _codeLength, _minCodeValue, _maxCodeValue)
                + String.format("Duplicate values are%sallowed.\n", (_duplicatesAllowed ? " ": " not "))
                + String.format("Can you break the code in just %d guesses?\n", _numberOfGuesses)
                + "'-' for number match only, and '+' for an exact (number and index)";

        channel.sendMessage(content).thenAcceptAsync(message -> {
            boolean winner = false;
            DiscordScanner input = new DiscordScanner(message, users);
            while (_currentGuess < _numberOfGuesses) {
                if (input.isTerminated()) break;
                if (input.nextIsReady()) {
                    String guess = input.next();

                    if (guess != null) {
                        if (guess.toLowerCase().equals("quit")) {
                            input.terminate();
                            break;
                        }
                        guess = func.WHITE_SPACE.matcher(guess).replaceAll("");

                        // Check the current guess, and exit if it is a perfect match
                        GuessResult result = submitGuess(guess);
                        if (result.isPerfectGuess()) {
                            winner = true;
                            input.terminate();
                            break;
                        }

                        // Output the score, or a message, if either were provided
                        if (!result.getScore().isEmpty()) {
                            String tryCount = " (try " + _currentGuess + "/" + _numberOfGuesses + ")";
                            channel.sendMessage(result.getScore() + tryCount).exceptionally(ExceptionLogger.get());
                        } else if (!result.getMessage().isEmpty()) {
                            channel.sendMessage(result.getMessage()).exceptionally(ExceptionLogger.get());
                        }
                    }
                } else {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            String endGameMessage = winner ? "You solved it!" : "You lose :(";
            channel.sendMessage(endGameMessage).exceptionally(ExceptionLogger.get());
        });
    }

    /**
     * Generates the secret code for the user to guess
     */
    private void generateCode() {
        if (!_duplicatesAllowed) {
            int codeRangeSize = _maxCodeValue - _minCodeValue;
            if (codeRangeSize < _codeLength) {
                throw new RuntimeException("Code value range must be larger than code length! Duplicate values are not permitted.");
            }
        }

        _code = new HashMap<>();
        for (int i = 0; i < _codeLength; i++) {
            int number = ThreadLocalRandom.current().nextInt(_minCodeValue, _maxCodeValue + 1);
            if (!_duplicatesAllowed) {
                // Recalculate number for current position if it already exists to prevent duplicates
                while (_code.containsKey(number)) {
                    number = ThreadLocalRandom.current().nextInt(_minCodeValue, _maxCodeValue + 1);
                }
            }

            HashSet<Integer> indices = _code.getOrDefault(number, new HashSet<>());
            indices.add(i);
            _code.put(number, indices);
        }
    }

    /**
     * Evaluates the given guess for correctness
     * @param guess The guess string given by the user
     * @return The result of the guess evaluation
     */
    private GuessResult submitGuess(String guess) {
        if (guess.length() != _codeLength) {
            String errorMessage = String.format("Guess must be %d numbers long!", _codeLength);
            return new GuessResult(errorMessage);
        }

        _currentGuess++;

        try {
            return getScore(guess);
        } catch (IllegalArgumentException e) {
            String errorMessage = String.format("Invalid Guess: %s", e.getMessage());
            return new GuessResult(errorMessage);
        }
    }

    /**
     * Scores the given guess
     * @param guess Guess string provided by the user
     * @return Result of the guess scoring
     */
    private GuessResult getScore(String guess) {
        int scorePluses = 0;
        int scoreMinuses = 0;
        boolean perfectGuess = true;

        // Track the number of matches for each value, to prevent double scoring
        HashMap<Integer, Integer> matches = new HashMap<>();
        for (int i = 0; i < guess.length(); i++) {
            char guessValue = guess.charAt(i);
            int number = Character.getNumericValue(guessValue);
            if (isValidGuessNumber(number)) {
                // Skip current value if we have exhausted value's possible matches
                int numberMatchCount = matches.getOrDefault(number, 0);
                int actualNumberCount = _code.getOrDefault(number, new HashSet<>()).size();
                if (actualNumberCount > 0 && numberMatchCount == actualNumberCount) {
                    perfectGuess = false;
                    continue;
                }

                // Check the current guess position and update scores accordingly
                switch (checkGuessValue(number, i)) {
                    case '+':
                        scorePluses++;
                        matches.put(number, matches.getOrDefault(number, 0) + 1);
                        break;
                    case '-':
                        scoreMinuses++;
                        matches.put(number, matches.getOrDefault(number, 0) + 1);
                        // Fallthrough intentional
                    default:
                        perfectGuess = false;
                }
            } else {
                throw new IllegalArgumentException(String.format("Guess values must be numbers between %d and %d!", _minCodeValue, _maxCodeValue));
            }
        }

        return new GuessResult(getScoreString(scorePluses, scoreMinuses), perfectGuess);
    }

    /**
     * Checks if a given number is a valid code value. (in the range 0-9)
     * @param number The number to validate
     * @return True if the number is valid
     */
    private boolean isValidCodeNumber(int number) {
        return number >= 0 && number <= 9;
    }

    /**
     * Checks if a given number is a valid guess value in the context of the current game. (in the configured range)
     * @param number The number to validate
     * @return True if the number is valid
     */
    private boolean isValidGuessNumber(int number) {
        return number >= _minCodeValue && number <= _maxCodeValue;
    }

    /**
     * Determine if the given guess was a match
     * @param number Number to check
     * @param index Index to check
     * @return Null char if no match, '-' for number match only, and '+' for an exact (number and index) match
     */
    private char checkGuessValue(int number, int index) {
        char score = '\0';

        if (_code.containsKey(number)) {
            HashSet<Integer> indices = _code.get(number);
            if (indices.contains(index)) {
                score = '+';
            } else {
                score = '-';
            }
        }

        return score;
    }

    /**
     * Constructs a score string composed of the given number of pluses and minuses
     * @param pluses Number of pluses to include in the string
     * @param minuses Number of minuses to include in the string
     * @return A score string of '+' and '-' symbols
     */
    private String getScoreString(int pluses, int minuses) {
        char[] score = new char[pluses + minuses];
        Arrays.fill(score, 0, pluses, '+');
        Arrays.fill(score, pluses, score.length, '-');
        return new String(score);
    }

    public boolean is_duplicatesAllowed() {
        return _duplicatesAllowed;
    }

    public void set_duplicatesAllowed(boolean _duplicatesAllowed) {
        this._duplicatesAllowed = _duplicatesAllowed;
    }

    public boolean get_duplicatesAllowed() {
        return _duplicatesAllowed;
    }

    public int get_numberOfGuesses() {
        return _numberOfGuesses;
    }

    public boolean set_numberOfGuesses(int _numberOfGuesses) {
        if (_numberOfGuesses != this._numberOfGuesses) {
            this._numberOfGuesses = _numberOfGuesses;
            return true;
        }
        return false;
    }

    public int get_codeLength() {
        return _codeLength;
    }

    public boolean set_codeLength(int _codeLength) {
        if (_codeLength != this._codeLength) {
            this._codeLength = _codeLength;
            return true;
        }
        return false;
    }

    public int get_maxCodeValue() {
        return _maxCodeValue;
    }

    public boolean set_maxCodeValue(int _maxCodeValue) {
        int newMinCodeVal = 1;
        if (_maxCodeValue == 10) {
            newMinCodeVal = 0;
        }
        _maxCodeValue = Math.max(newMinCodeVal + 1, Math.min(_maxCodeValue, 9));
        if (_maxCodeValue != this._maxCodeValue || newMinCodeVal != _minCodeValue) {
            this._maxCodeValue = _maxCodeValue;
            this._minCodeValue = newMinCodeVal;
            return true;
        }
        return false;
    }

    /**
     * Internal class used for handling the results of guess checks
     */
    private class GuessResult {
        private String _scoreValue;
        private boolean _perfectGuess;
        private String _message;

        /**
         * Default constructor.
         */
        public GuessResult() {
            _scoreValue = "";
            _perfectGuess = false;
            _message = "";
        }

        /**
         * Constructor to set only a message.
         * @param message Message to include in the result
         */
        public GuessResult(String message) {
            this();
            _message = message;
        }

        /**
         * Constructor to set the score and whether the current guess was perfect
         * @param scoreValue A score string of pluses and minuses
         * @param perfectGuess Whether this guess was perfect or not
         */
        public GuessResult(String scoreValue, boolean perfectGuess) {
            this();
            _scoreValue = scoreValue;
            _perfectGuess = perfectGuess;
        }

        /**
         * Constructor to all values.
         * @param scoreValue A score string of pluses and minuses
         * @param perfectGuess Whether this guess was perfect or not
         * @param message Message to include in the result
         */
        public GuessResult(String scoreValue, boolean perfectGuess, String message) {
            this(scoreValue, perfectGuess);
            _message = message;
        }

        /**
         * Get this results score string
         * @return Score string of pluses and minuses
         */
        public String getScore() {
            return _scoreValue;
        }

        /**
         * Get this results perfect guess value
         * @return Whether this guess was perfect or not
         */
        public boolean isPerfectGuess() {
            return _perfectGuess;
        }

        /**
         * Get this results message
         * @return A message included with this result
         */
        public String getMessage() {
            return _message;
        }
    }
}
