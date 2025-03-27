import java.util.ArrayList;
import java.util.Scanner;

abstract class Game {
    // Instanzvariablen
    private ArrayList<String> playerNames;
    private ArrayList<Integer> playerScores;
    private final Scanner scanner;
    private String currentPlayer;
    private int playerCount;

    // Konstruktor
    public Game() {
        playerNames = new ArrayList<>();
        playerScores = new ArrayList<>();
        scanner = new Scanner(System.in);
        currentPlayer = "";
        playerCount = 0;
    }


    // Methoden

    abstract void startGame();

    public void enterPlayerCount(int min, int max) {
        if (playerCount == 0) {
            System.out.println("Geben Sie bitte eine Spieleranzahl zwischen " + min + " und " + max + " ein.");
            while (playerCount < min || playerCount > max) {
                try {
                    playerCount = Integer.parseInt(scanner.nextLine());
                    if (playerCount < min || playerCount > max) {
                        System.out.println("Es dürfen mindestens " + min + " und maximal " + max + " Spieler spielen.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Bitte geben Sie eine Ziffer ein.");
                }
            }
        }
    }

    public void namePlayers() {
        while (playerNames.size() < playerCount) {
            System.out.println("Wie heißt Spieler Nr. " + (playerNames.size() + 1) + "?");
            String name = scanner.nextLine();
            while (name.isEmpty() || playerNames.contains(name)) {
                if (name.isEmpty()) {
                    System.out.println("Geben Sie bitte ein Namen ein.");
                } else if (playerNames.contains(name)) {
                    System.out.println("Dieser Name ist bereits vorhanden.");
                    System.out.println("Falls Sie den gleichen Namen wie eines Ihrer Mitspieler haben,");
                    System.out.println("könnten Sie sich z. B. wie folgt bennenen: " + name + "2.");
                } else {
                    break;
                }
                name = scanner.nextLine();
            }
            playerNames.add(name);
            playerScores.add(0); // Jeder Spieler bekommt ein Punktestand.
        }
        System.out.println();
    }

    public void switchPlayer() {
        if (playerNames.indexOf(currentPlayer) == (playerCount - 1)) {
            currentPlayer = playerNames.getFirst();
        } else {
            currentPlayer = playerNames.get(playerNames.indexOf(currentPlayer) + 1);
        }
    }

    public int getCurrentPlayerScore() {
        int currentPlayerIndex = playerNames.indexOf(currentPlayer);
        return playerScores.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() {
        return Integer.parseInt(currentPlayer) - 1;
    }

    public void increaseCurrentPlayerScore(int points) {
        // Erhöht die Punkte des aktuellen Spielers um die angegebene Anzahl.
        int currentPlayerIndex = playerNames.indexOf(currentPlayer);
        playerScores.set(currentPlayerIndex, playerScores.get(currentPlayerIndex) + points);
    }


    // Getters und Setters

    public ArrayList<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(ArrayList<String> playerNames) {
        this.playerNames = playerNames;
    }

    public ArrayList<Integer> getPlayerScores() {
        return playerScores;
    }

    public void setPlayerScores(ArrayList<Integer> playerScores) {
        this.playerScores = playerScores;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

}