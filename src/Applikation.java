import java.util.Scanner;

public class Applikation {
    // Instanzvariablen
    private final Scanner scanner;

    // Konstruktor
    public Applikation() {
        scanner = new Scanner(System.in);
    }

    // Methoden

    public void playTicTacToe() {
        gameStart(TicTacToe.class);
    }

    public void playRockPaperScissors() {
        gameStart(RockPaperScissors.class);
    }

    public void playUNO() {
        gameStart(UNO.class);
    }

    public void playHangmanArcade() {
        gameStart(HangmanArcade.class);
    }

    private void gameStart(Class<? extends Game> gameClass) {
        try {
            String answer = "ja";
            while (answer.equals("ja")) {
                Game game = gameClass.getDeclaredConstructor().newInstance();
                game.startGame();
                answer = "keine antwort";
                System.out.println("Möchten Sie nochmal spielen? ja/nein");
                while (!answer.equals("ja") && !answer.equals("nein")) {
                    answer = scanner.nextLine();
                    if (!answer.equals("ja") && !answer.equals("nein")) {
                        System.out.println("Bitte geben Sie 'ja' oder 'nein' ein.");
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            System.err.println(e.getMessage());
        }
    }
}