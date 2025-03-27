import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class TicTacToe extends Game {
    // Instanzvariablen
    private final ArrayList<String> cells;
    private final Scanner scanner;
    private String currentPlayerSymbol;

    // Enum
    private enum GameResult {
        ROUND_DRAW, ROUND_WON, GAME_WON, NO_RESULT
    }

    // Konstruktor
    public TicTacToe() {
        cells = new ArrayList<>();
        for (int i = 0; i < 9; i++) { // Das Spiel besteht aus 9 Feldern.
            cells.add(String.valueOf(i + 1));
        }
        System.out.println("Willkommen beim Spiel TicTacToe!");
        System.out.println("Der erste Spieler der 3 Punkte hat, gewinnt das Spiel.");
        System.out.println("Möchten Sie SchereSteinPapier spielen, um den Startspieler zu bestimmen? ja/nein");
        System.out.println("Wenn nicht dann wird der Startspieler zufällig bestimmt.");
        scanner = new Scanner(System.in);
        Random random = new Random();
        String answer = "";
        while (!answer.equals("ja") && !answer.equals("nein")) {
            answer = scanner.nextLine();
            switch (answer) {
                case "ja" -> {
                    RockPaperScissors roPaSciGame = new RockPaperScissors();
                    roPaSciGame.setPlayerCount(2);
                    roPaSciGame.startGame();
                    setPlayerCount(2); // Tictactoe kann man nur mit 2 Spielern spielen.
                    for (int i = 0; i < getPlayerCount(); i++) {
                        getPlayerScores().add(0); // Jeder Spieler bekommt einen Punktestand.
                    }
                    setPlayerNames(roPaSciGame.getPlayerNames());
                    setCurrentPlayer(roPaSciGame.getCurrentPlayer());
                }
                case "nein" -> {
                    setPlayerCount(2);
                    namePlayers();
                    // Zufallsbestimmung des Spielanfängers.
                    setCurrentPlayer(getPlayerNames().get(random.nextInt(getPlayerCount())));
                }
                default -> System.out.println("Geben Sie bitte ja oder nein ein.");
            }
        }
        updatePlayerSymbol();
    }

    // Methoden

    public void startGame() {
        // Spiel startet hier.
        boolean gameInProgress = true;
        while (gameInProgress) {
            printBoard();
            System.out.println(getCurrentPlayer() + " ist dran. ");
            int cellNumberSelection = validateCellNumberSelection();
            cells.set(cellNumberSelection - 1, currentPlayerSymbol);
            GameResult gameResult = evaluateGameResult();
            switch (gameResult) {
                case ROUND_WON, ROUND_DRAW -> {
                    System.out.println();
                    System.out.println("Punktetabelle:");
                    for (int i = 0; i < getPlayerScores().size(); i++) {
                        System.out.println(getPlayerNames().get(i) + ": " + getPlayerScores().get(i) + " Punkte");
                    }
                    System.out.println("Klicken Sie auf die Enter-Taste um die nächste Runde zu beginnen.");
                    String answer = scanner.nextLine();
                    while (!answer.isEmpty()) {
                        answer = scanner.nextLine();
                    }
                    System.out.println();
                    resetBoard();
                }
                case GAME_WON -> gameInProgress = false; // Spiel wird beendet.
            }
            switchPlayer();
            updatePlayerSymbol();
        }
    }

    private void updatePlayerSymbol() {
        if (getCurrentPlayer().equals(getPlayerNames().getFirst())) {
            currentPlayerSymbol = "X";
        } else {
            currentPlayerSymbol = "O";
        }
    }

    private void printBoard() {
        System.out.println();
        for (int i = 0; i < cells.size(); i++) {
            System.out.print(cells.get(i));
            if (i % 3 == 2) {
                System.out.println();
            } else {
                System.out.print(" | ");
            }
        }
        System.out.println();
    }

    private int validateCellNumberSelection() { // Gibt eine gültige Feldnummer zurück.
        int cellNumberSelection = 0;
        while (cellNumberSelection == 0) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (cells.get(input - 1).equals("X") || cells.get(input - 1).equals("O")) {
                    System.out.println("Bitte geben Sie eine Feldnummer an, die verfügbar ist.");
                } else {
                    cellNumberSelection = input; // Gültige Eingabe gefunden.
                }
            } catch (NumberFormatException e) {
                System.out.println("Bitte geben Sie eine Feldnummer an, keine Zeichenketten.");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Bitte geben Sie eine gültige Feldnummer an.");
            }
        }
        return cellNumberSelection;

    }

    private GameResult evaluateGameResult() { // Gibt den Spielstand zurück.
        if (rowWin() || columnWin() || diagonalWin()) {
            System.out.println("Gratuliere, " + getCurrentPlayer() + " hat die Runde gewonnen und somit ein Punkt bekommen!");
            increaseCurrentPlayerScore(1);
            if (gameWon()) {
                System.out.println("Gratuliere, " + getCurrentPlayer() + " hat " + getCurrentPlayerScore() + " Punkte und somit das Spiel gewonnen!");
                return GameResult.GAME_WON;
            }
            return GameResult.ROUND_WON;
        } else if (isDraw()) {
            System.out.println("Sie haben ein Unentschieden. Niemand bekommt ein Punkt.");
            return GameResult.ROUND_DRAW;
        } else {
            return GameResult.NO_RESULT;
        }
    }

    private boolean isDraw() {
        // Überprüfung ob Unentschieden
        int countFilledCells  = 0;
        for (String cell : cells) {
            if (cell.equals("X") || cell.equals("O")) {
                countFilledCells ++;
                if (countFilledCells  == 9) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean rowWin() {
        // Überprüfung der Zeilen
        for (int i = 0; i < cells.size(); i = i + 3) {
            if (cells.get(i).equals(currentPlayerSymbol) && cells.get(i + 1).equals(currentPlayerSymbol) && cells.get(i + 2).equals(currentPlayerSymbol)) {
                return true;
            }
        }
        return false;
    }

    private boolean columnWin() {
        // Überprüfung der Spalten
        for (int i = 0; i < 3; i++) {
            if (cells.get(i).equals(currentPlayerSymbol) && cells.get(i + 3).equals(currentPlayerSymbol) && cells.get(i + 6).equals(currentPlayerSymbol)) {
                return true;
            }
        }
        return false;
    }

    private boolean diagonalWin() {
        // Überprüfung der Diagonalen
        return (cells.get(0).equals(currentPlayerSymbol) && cells.get(4).equals(currentPlayerSymbol) && cells.get(8).equals(currentPlayerSymbol)) ||
                (cells.get(2).equals(currentPlayerSymbol) && cells.get(4).equals(currentPlayerSymbol) && cells.get(6).equals(currentPlayerSymbol));
    }

    private boolean gameWon() {
        return getCurrentPlayerScore() >= 3;
    }

    private void resetBoard() {
        cells.clear();
        for (int i = 0; i < 9; i++) {
            cells.add(String.valueOf(i + 1));
        }
    }
}