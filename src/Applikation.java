import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.util.List;

public class Applikation {
    // Instanzvariablen
    private final Scanner scanner;
    private final List<Class<? extends Game>> games;

    // Konstruktor
    public Applikation() {
        scanner = new Scanner(System.in);
        games = List.of(TicTacToe.class, RockPaperScissors.class, UNO.class, HangmanArcade.class);
    }

    // Methoden
    public void chooseAGame() {
        JFrame frame = new JFrame("Wählen Sie bitte ein Spiel aus.");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(games.size(), 1));
        frame.setMinimumSize(new Dimension(340, games.size() * 50)); // Setzt die Größe beim aufploppen fest.
        frame.setLocationRelativeTo(null);
        for (Class<? extends Game> gameClass : games) {
            JButton button = new JButton(gameClass.getSimpleName());
            button.addActionListener(e -> {
                frame.dispose(); // Schließt das Fenster
                gameStart(gameClass);
            });
            frame.add(button);
        }

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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