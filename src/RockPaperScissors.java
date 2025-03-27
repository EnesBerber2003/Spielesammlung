import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.util.Collections;

public class RockPaperScissors extends Game {
    // Instanzvariablen
    private final String formatBold, underlineText, resetConsole;
    private final Scanner scanner;
    private final Random random;

    // Enums
    private enum Option {
        SCISSORS("Schere"), ROCK("Stein"), PAPER("Papier");

        private final String designation; // Die Bezeichnung dient zur schönen Konsolenausgabe der Optionen.

        // Enum Konstruktor
        Option(String designation) {
            this.designation = designation;
        }

        public String getDesignation() {
            return designation;
        }
    }

    private enum GameResult {
        DRAW, ROUND_WON, ROUND_LOST, GAME_WINNER_EXISTS
    }


    // Konstruktor
    public RockPaperScissors() {
        // https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
        formatBold = "\u001B[1m";
        underlineText = "\u001B[4m";
        resetConsole = "\u001B[0m"; // Damit wird die Farbe auf der Konsole zurückgesetzt.
        scanner = new Scanner(System.in);
        random = new Random();
        try {
            int milliseconds = 50;
            for (char c : "Willkommen beim Spiel SchereSteinPapier!".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliseconds);
            }
            System.out.println();
            for (char c : "Dieses SchereSteinPapier Spiel unterscheidet sich von dem üblichen".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliseconds);
            }
            System.out.println();
            for (char c : "Spiel, den wir in der Realität kennen.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliseconds);
            }
            System.out.println();
            for (char c : "In diesem Spiel, spielt jeder Spieler gegen einen Bot.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliseconds);
            }
            System.out.println();
            for (char c : "Man bekommt einen Punkt, wenn man den Bot besiegt.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliseconds);
            }
            System.out.println();
            for (char c : "Der Spieler, der mindestens 5 Punkte erzielt und dabei einen ".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliseconds);
            }
            System.out.println();
            for (char c : "Vorsprung von mindestens 2 Punkten vor seinen Mitspielern hat, gewinnt das Spiel.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliseconds);
            }
            System.out.println();
            System.out.println();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
        enterPlayerCount(2,100);
        namePlayers();
        setCurrentPlayer(getPlayerNames().getFirst());
    }

    // Methoden


    public void startGame() {
        // Spiel startet hier.
        boolean gameInProgress = true;
        while (gameInProgress) {
            System.out.println();
            System.out.println(getCurrentPlayer() + " ist dran.");
            System.out.print("Bitte geben Sie die Ziffer ");
            for (int i = 0; i < Option.values().length; i++) {
                System.out.print(i + " (" + Option.values()[i].getDesignation() + ")");
                if (i == 0) {
                    System.out.print(", ");
                } else if (i == 1) {
                    System.out.print(" oder ");
                }
            }
            System.out.println(" ein.");
            Option option = validateOptionSelection();
            GameResult gameResult = evaluateSelection(option);
            switch (gameResult) {
                case ROUND_WON, ROUND_LOST -> switchPlayer();
                case GAME_WINNER_EXISTS -> {
                    System.out.println("Punktetabelle:");
                    for (int i = 0; i < getPlayerScores().size(); i++) {
                        System.out.println(getPlayerNames().get(i) + ": " + getPlayerScores().get(i) + " Punkte");
                    }
                    System.out.println();
                    gameInProgress = false; // Spiel wird beendet.
                }
            }
        }
    }

    private Option validateOptionSelection() { // Gibt eine gültige Option auswahl zurück.
        int optionIndex = -1;
        while (optionIndex == -1) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= 0 && input < Option.values().length) {
                    optionIndex = input; // Gültige Eingabe gefunden
                } else {
                    System.out.println("Geben Sie eine gültige Ziffer ein.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Geben Sie eine gültige Ziffer ein.");
            }
        }
        return Option.values()[optionIndex];
    }

    private GameResult evaluateSelection(Option currentPlayerOption) {
        int optionIndex = random.nextInt(Option.values().length);
        Option botOption = Option.values()[optionIndex];
        if (isDraw(currentPlayerOption, botOption)) {
            return GameResult.DRAW;
        } else if (roundWon(currentPlayerOption, botOption)) {
            if (gameWinnerExists()) {
                return GameResult.GAME_WINNER_EXISTS;
            }
            return GameResult.ROUND_WON;
        } else {
            // Wenn verloren:
            System.out.println("Der Bot hat " + formatBold + underlineText + Option.values()[optionIndex].getDesignation() + resetConsole + " angegeben.");
            System.out.println(currentPlayerOption.getDesignation() + " < " + botOption.getDesignation());
            System.out.println("Sie haben die Runde verloren.");
            if (gameWinnerExists()) {
                return GameResult.GAME_WINNER_EXISTS;
            }
            return GameResult.ROUND_LOST;
        }
    }

    private boolean isDraw(Option currentPlayerOption, Option botOption) {
        if (currentPlayerOption == botOption) {
            // Wenn unentschieden:
            System.out.println("Der Bot hat " + formatBold + underlineText + botOption.getDesignation() + resetConsole + " angegeben.");
            System.out.println(currentPlayerOption.getDesignation() + " = " + botOption.getDesignation());
            System.out.println(getCurrentPlayer() + ". Da Sie unentschieden haben, dürfen Sie nochmal eine Auswahl angeben.");
            return true;
        }
        return false;
    }

    private boolean roundWon(Option currentPlayerOption, Option botOption) {
        if ((currentPlayerOption == Option.PAPER && botOption == Option.ROCK) ||
                (currentPlayerOption == Option.ROCK && botOption == Option.SCISSORS) ||
                (currentPlayerOption == Option.SCISSORS && botOption == Option.PAPER)) {
            // Wenn Runde gewonnen:
            System.out.println("Der Bot hat " + formatBold + underlineText + botOption.getDesignation() + resetConsole + " angegeben.");
            System.out.println(currentPlayerOption.getDesignation() + " > " + botOption.getDesignation());
            System.out.println("Sie haben die Runde gewonnen und 1 Punkt erhalten.");
            increaseCurrentPlayerScore(1);
            System.out.println("Punktestand: " + getCurrentPlayerScore());
            return true;
        }
        return false;
    }

    private boolean gameWinnerExists() {
        boolean isLastPlayerTurn  = getCurrentPlayer().equals(getPlayerNames().getLast());
        if (isLastPlayerTurn  && Collections.max(getPlayerScores()) >= 5) {
            ArrayList<Integer> temp = new ArrayList<>(getPlayerScores());
            Integer bestPlayerScore = Collections.max(getPlayerScores());
            getPlayerScores().remove(bestPlayerScore);
            Integer secondBestPlayerScore = Collections.max(getPlayerScores());
            setPlayerScores(temp); // Die Spielerpunkte werden wieder zurückgesetzt.
            if ((bestPlayerScore - secondBestPlayerScore) >= 2) {
                // Wenn jemand das Spiel gewonnen hat:
                int winnerIndex = getPlayerScores().indexOf(bestPlayerScore);
                String winner = getPlayerNames().get(winnerIndex);
                setCurrentPlayer(winner);
                System.out.println();
                System.out.println(winner + " hat " + bestPlayerScore + " Punkte und somit das Spiel gewonnen!");
                return true;
            }
        }
        return false;
    }
}