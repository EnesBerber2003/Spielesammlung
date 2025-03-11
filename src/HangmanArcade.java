import java.net.URISyntaxException;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HangmanArcade extends Spiel {
    // Instanzvariablen
    private List<String> words;
    private String word;  // Das zu erratene Wort.
    private int allowedMistakes, score, record;
    private final ArrayList<Character> wordProgress;  // Speichert jeweils entweder ein Buchstabe oder ein Leerzeichen des zu erratenen Wortes.
    private final ArrayList<Character> incorrectLetters;
    private final Scanner scanner;
    private final Random random;

    private enum GameResult {
        LETTER_FOUND, LETTER_NOT_FOUND, WORD_GUESSED, WORD_NOT_GUESSED
    }

    public HangmanArcade() {
        try {
            Path filePath = Path.of(Objects.requireNonNull(getClass().getResource("/woerter.txt")).toURI());
            words = Files.readAllLines(filePath); // Alle vorhandenen Wörter in der Textdatei werden gelesen und initialisiert.
        } catch (URISyntaxException e) {
            System.err.println("Fehler beim Konvertieren der URI: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }
        allowedMistakes = 10;
        score = 0;
        record = 0;
        wordProgress = new ArrayList<>();
        incorrectLetters = new ArrayList<>();
        scanner = new Scanner(System.in);
        random = new Random();
        System.out.println("Willkommen bei Hangman Arcade!");
        System.out.println("Sie haben bis zu 10 Fehlversuche, um ein Wort zu erraten.");
        System.out.println("Für jeden ungenutzten Fehlversuch erhalten Sie 10 Punkte, wenn Sie das Wort richtig erraten.");
        System.out.println("Falls Sie das Wort nicht erraten, werden Ihre Punkte auf 0 zurückgesetzt.");
        System.out.println("Klicken Sie auf die Enter-Taste um das Spiel zu starten.");
        scanner.nextLine();
    }

    public void startGame() {
        boolean gameInProgress = true;
        while (gameInProgress) {
            word = words.get(random.nextInt(words.size()));
            // Initialisierung der Platzhalter:
            for (int i = 0; i < word.length(); i++) {
                wordProgress.add('_');
            }
            boolean isGuessing = true;
            while (isGuessing) {
                printGameBoard();
                System.out.println("Geben Sie einen Buchstaben oder das ganze Wort an:");
                String input = scanner.nextLine().toLowerCase();
                GameResult result = evaluateInput(input);
                switch (result) {
                    case WORD_GUESSED, WORD_NOT_GUESSED -> {
                        awardPoints(result);
                        allowedMistakes = 10;
                        wordProgress.clear();
                        incorrectLetters.clear();
                        System.out.println("Möchten Sie weiter spielen? ja/nein");
                        String answer = scanner.nextLine();
                        while (!answer.equals("ja") && !answer.equals("nein")) {
                            System.out.println("Bitte geben Sie 'ja' oder 'nein' ein.");
                            answer = scanner.nextLine();
                        }
                        if (answer.equals("nein")) {
                            gameInProgress = false;
                        }
                        isGuessing = false;
                    }
                }
            }
        }
        System.exit(0);
    }

    private void printGameBoard() {
        for(int i = 0; i < 10; i++) {
            System.out.println();
        }
        System.out.print("Verbleibende Fehlversuche: " + allowedMistakes);
        System.out.print("    ");
        System.out.print("Punktestand: " + score);
        System.out.print("    ");
        System.out.print("Rekord: " + record);
        System.out.print("    ");
        if (!incorrectLetters.isEmpty()) {
            System.out.print("Falsche Buchstaben: ");
            for (Character letter : incorrectLetters) {
                System.out.print(letter);
                if (letter != incorrectLetters.getLast()) {
                    System.out.print(", ");
                }
            }
        }
        System.out.println();
        System.out.println();
        for (Character buchstabe : wordProgress) {
            System.out.print(buchstabe + " ");
        }
        System.out.println();
        System.out.println();
    }

    private GameResult evaluateInput(String input) {
        if (input.equalsIgnoreCase(word)) {
            System.out.println();
            System.out.println("Sie haben das Wort " + word + " erraten.");
            return GameResult.WORD_GUESSED;
        } else if (!letterFound(input)) {
            if (wordNotGuessed()) {
                return GameResult.WORD_NOT_GUESSED;
            }
            return GameResult.LETTER_NOT_FOUND;
        } else {
            if (allLettersFound()) {
                return GameResult.WORD_GUESSED;
            }
            return GameResult.LETTER_FOUND;
        }
    }

    private boolean letterFound(String input) {
        if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            allowedMistakes--;
            return false;
        } else if (word.toLowerCase().contains(input)) {
            for (int i = 0; i < word.length(); i++) {
                if (word.toLowerCase().charAt(i) == input.charAt(0)) {
                    wordProgress.set(i, word.charAt(i));
                }
            }
            return true;
        } else {
            if (!incorrectLetters.contains(input.charAt(0))) {
                incorrectLetters.add(input.charAt(0));
                allowedMistakes--;
            }
            return false;
        }
    }

    private boolean allLettersFound() {
        StringBuilder currentWord = new StringBuilder();
        for (Character character : wordProgress) {
            currentWord.append(character);
        }
        if (word.contentEquals(currentWord)) {
            System.out.println();
            System.out.println("Sie haben das Wort " + word + " gefunden.");
            return true;
        }
        return false;
    }

    private boolean wordNotGuessed() {
        if (allowedMistakes == 0) {
            System.out.println();
            System.out.println("Sie haben keine Versuche mehr. Sie haben verloren. Das Wort war: " + word);
            return true;
        }
        return false;
    }

    private void awardPoints(GameResult result) {
        if (result == GameResult.WORD_GUESSED) {
            score = score + allowedMistakes * 10;
            System.out.println("Sie bekommen " + allowedMistakes * 10 + " Punkte, weil Sie " + allowedMistakes + " Fehlversuche übrig hatten.");
            if (score > record) {
                record = score;
            }
            System.out.println();
        } else {
            score = 0;
        }
    }
}