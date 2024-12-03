import java.util.Scanner;

public class Anwendung {
    // Instanzvariablen
    private final Scanner scanner;

    // Konstruktor
    public Anwendung() {
        scanner = new Scanner(System.in);
    }

    // Methoden

    public void ticTacToeSpielen() {
        spielStarten(TicTacToe.class);
    }

    public void schereSteinPapierSpielen() {
        spielStarten(SchereSteinPapier.class);
    }

    public void unoSpielen() {
        spielStarten(UNO.class);
    }

    private void spielStarten(Class<?> spielKlasse) {
        try {
            String antwort = "ja";
            while (antwort.equals("ja")) {
                Spiel spiel = (Spiel) spielKlasse.getDeclaredConstructor().newInstance();
                spiel.starteSpiel();
                antwort = "keine antwort";
                System.out.println("Möchten Sie nochmal spielen? ja/nein");
                while (!antwort.equals("ja") && !antwort.equals("nein")) {
                    antwort = scanner.nextLine();
                    if (!antwort.equals("ja") && !antwort.equals("nein")) {
                        System.out.println("Bitte geben Sie 'ja' oder 'nein' ein.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}