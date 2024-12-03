import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class TicTacToe extends Spiel {
    // Instanzvariablen
    private final ArrayList<String> felder;
    private final Scanner scanner;
    private final Random random;
    private final SchereSteinPapier schStPa;
    private String spielerSymbol;

    // Enum
    private enum SpielErgebnis {
        RUNDE_UNENTSCHIEDEN, RUNDE_GEWONNEN, SPIEL_GEWONNEN, KEIN_ERGEBNIS
    }

    // Konstruktor
    public TicTacToe() {
        felder = new ArrayList<>();
        for (int i = 0; i < 9; i++) { // Das Spiel besteht aus 9 Feldern.
            felder.add(String.valueOf(i + 1));
        }
        scanner = new Scanner(System.in);
        random = new Random();
        schStPa = new SchereSteinPapier();
    }

    // Methoden

    private void voreinstellung() {
        try {
            int milleSekunden = 50;
            for (char c : "Willkommen beim Spiel TicTacToe!".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milleSekunden);
            }
            System.out.println();
            for (char c : "Der erste Spieler der 3 Punkte hat, gewinnt das Spiel.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milleSekunden);
            }
            System.out.println();
            for (char c : "Möchten Sie SchereSteinPapier spielen, um den Startspieler zu bestimmen? ja/nein".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milleSekunden);
            }
            System.out.println();
            for (char c : "Wenn nicht dann wird der Startspieler zufällig bestimmt.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milleSekunden);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String antwort = "";
        System.out.println();
        System.out.println();
        while (!antwort.equals("ja") && !antwort.equals("nein")) {
            antwort = scanner.nextLine();
            switch (antwort) {
                case "ja" -> {
                    schStPa.setAnzahlDerSpieler(2);
                    schStPa.starteSpiel();
                    setAnzahlDerSpieler(2); // Tictactoe kann man nur mit 2 Spielern spielen.
                    for (int i = 0; i < getAnzahlDerSpieler(); i++) {
                        getSpielerpunkte().add(0); // Jeder Spieler bekommt einen Punktestand.
                    }
                    setSpielernamen(schStPa.getSpielernamen());
                    setAktuellerSpieler(schStPa.getAktuellerSpieler());
                }
                case "nein" -> {
                    setAnzahlDerSpieler(2);
                    spielerBenennen();
                    // Zufallsbestimmung des Spielanfängers.
                    setAktuellerSpieler(getSpielernamen().get(random.nextInt(getAnzahlDerSpieler())));
                }
                default -> System.out.println("Geben Sie bitte ja oder nein ein.");
            }
        }
        spielerSymbolAktualisieren();
    }


    public void starteSpiel() {
        voreinstellung();
        // Spiel startet hier.
        boolean spielLaeuft = true;
        while (spielLaeuft) {
            feldAusgeben();
            System.out.println(getAktuellerSpieler() + " ist dran. ");
            int feldNrAuswahl = feldNrAuswahlValidieren();
            felder.set(feldNrAuswahl - 1, spielerSymbol);
            SpielErgebnis spielErgebnis = spielstandAuswerten();
            switch (spielErgebnis) {
                case RUNDE_GEWONNEN, RUNDE_UNENTSCHIEDEN -> {
                    System.out.println();
                    System.out.println("Punktetabelle:");
                    for (int i = 0; i < getSpielerpunkte().size(); i++) {
                        System.out.println(getSpielernamen().get(i) + ": " + getSpielerpunkte().get(i) + " Punkte");
                    }
                    System.out.println("Klicken Sie auf die Enter-Taste um die nächste Runde zu beginnen.");
                    String antwort = scanner.nextLine();
                    while (!antwort.isEmpty()) {
                        antwort = scanner.nextLine();
                    }
                    System.out.println();
                    resetFelder();
                }
                case SPIEL_GEWONNEN -> {
                    spielLaeuft = false; // Spiel wird beendet.
                }
            }
            spielerWechseln();
            spielerSymbolAktualisieren();
        }
    }

    private void spielerSymbolAktualisieren() {
        if (getAktuellerSpieler().equals(getSpielernamen().getFirst())) {
            spielerSymbol = "X";
        } else {
            spielerSymbol = "O";
        }
    }

    private void feldAusgeben() {
        System.out.println();
        for (int i = 0; i < felder.size(); i++) {
            System.out.print(felder.get(i));
            if (i % 3 == 2) {
                System.out.println();
            } else {
                System.out.print(" | ");
            }
        }
        System.out.println();
    }

    private int feldNrAuswahlValidieren() { // Gibt eine gültige Feldnummer zurück.
        int feldNrAuswahl = 0;
        while (feldNrAuswahl == 0) {
            try {
                int eingabe = Integer.parseInt(scanner.nextLine());
                if (felder.get(eingabe - 1).equals("X") || felder.get(eingabe - 1).equals("O")) {
                    System.out.println("Bitte geben Sie eine Feldnummer an, die verfügbar ist.");
                } else {
                    feldNrAuswahl = eingabe; // Gültige Eingabe gefunden.
                }
            } catch (NumberFormatException e) {
                System.out.println("Bitte geben Sie eine Feldnummer an, keine Zeichenketten.");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Bitte geben Sie eine gültige Feldnummer an.");
            }
        }
        return feldNrAuswahl;

    }

    private SpielErgebnis spielstandAuswerten() { // Gibt den Spielstand zurück.
        if (zeilenGewinn() || spaltenGewinn() || diagonalenGewinn()) {
            System.out.println("Gratuliere, " + getAktuellerSpieler() + " hat die Runde gewonnen und somit ein Punkt bekommen!");
            erhoehePunkteDesAktuellenSpielers(1);
            if (spielGewonnen()) {
                System.out.println("Gratuliere, " + getAktuellerSpieler() + " hat " + punkteDesAktuellenSpielers() + " Punkte und somit das Spiel gewonnen!");
                return SpielErgebnis.SPIEL_GEWONNEN;
            }
            return SpielErgebnis.RUNDE_GEWONNEN;
        } else if (istUnentschieden()) {
            System.out.println("Sie haben ein Unentschieden. Niemand bekommt ein Punkt.");
            return SpielErgebnis.RUNDE_UNENTSCHIEDEN;
        } else {
            return SpielErgebnis.KEIN_ERGEBNIS;
        }
    }

    private boolean istUnentschieden() {
        // Überprüfung ob Unentschieden
        int anzahlVollerFelder = 0;
        for (String feld : felder) {
            if (feld.equals("X") || feld.equals("O")) {
                anzahlVollerFelder++;
                if (anzahlVollerFelder == 9) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean zeilenGewinn() {
        // Überprüfung der Zeilen
        for (int feldIndex = 0; feldIndex < felder.size(); feldIndex = feldIndex + 3) {
            if (felder.get(feldIndex).equals(spielerSymbol) && felder.get(feldIndex + 1).equals(spielerSymbol) && felder.get(feldIndex + 2).equals(spielerSymbol)) {
                return true;
            }
        }
        return false;
    }

    private boolean spaltenGewinn() {
        // Überprüfung der Spalten
        for (int feldIndex = 0; feldIndex < 3; feldIndex++) {
            if (felder.get(feldIndex).equals(spielerSymbol) && felder.get(feldIndex + 3).equals(spielerSymbol) && felder.get(feldIndex + 6).equals(spielerSymbol)) {
                return true;
            }
        }
        return false;
    }

    private boolean diagonalenGewinn() {
        // Überprüfung der Diagonalen
        return (felder.get(0).equals(spielerSymbol) && felder.get(4).equals(spielerSymbol) && felder.get(8).equals(spielerSymbol)) ||
                (felder.get(2).equals(spielerSymbol) && felder.get(4).equals(spielerSymbol) && felder.get(6).equals(spielerSymbol));
    }

    private boolean spielGewonnen() {
        return punkteDesAktuellenSpielers() >= 3;
    }

    private void resetFelder() {
        felder.clear();
        for (int i = 0; i < 9; i++) {
            felder.add(String.valueOf(i + 1));
        }
    }
}