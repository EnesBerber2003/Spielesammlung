import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.util.Collections;

public class SchereSteinPapier extends Spiel {
    // Instanzvariablen
    private final String fettFormatieren, textUnterstreichen, konsolenReset;
    private final Scanner scanner;
    private final Random random;

    // Enums
    private enum Option {
        SCHERE("Schere"), STEIN("Stein"), PAPIER("Papier");

        private final String bezeichnung; // Die Bezeichnung dient zur schönen Konsolenausgabe der Optionen.

        // Enum Konstruktor
        Option(String bezeichnung) {
            this.bezeichnung = bezeichnung;
        }

        public String getBezeichnung() {
            return bezeichnung;
        }
    }

    private enum SpielErgebnis {
        UNENTSCHIEDEN, RUNDE_GEWONNEN, RUNDE_VERLOREN, SPIELGEWINNER_VORHANDEN
    }


    // Konstruktor
    public SchereSteinPapier() {
        // https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
        fettFormatieren = "\u001B[1m";
        textUnterstreichen = "\u001B[4m";
        konsolenReset = "\u001B[0m"; // Damit wird die Farbe auf der Konsole zurückgesetzt.
        scanner = new Scanner(System.in);
        random = new Random();
    }

    // Methoden

    private void voreinstellung() {
        try {
            int milliSekunden = 50;
            for (char c : "Willkommen beim Spiel SchereSteinPapier!".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliSekunden);
            }
            System.out.println();
            for (char c : "Dieses SchereSteinPapier Spiel unterscheidet sich von dem üblichen".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliSekunden);
            }
            System.out.println();
            for (char c : "Spiel, den wir in der Realität kennen.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliSekunden);
            }
            System.out.println();
            for (char c : "In diesem Spiel, spielt jeder Spieler gegen einen Bot.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliSekunden);
            }
            System.out.println();
            for (char c : "Man bekommt einen Punkt, wenn man den Bot besiegt.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliSekunden);
            }
            System.out.println();
            for (char c : "Der Spieler, der mindestens 5 Punkte erzielt und dabei einen ".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliSekunden);
            }
            System.out.println();
            for (char c : "Vorsprung von mindestens 2 Punkten vor seinen Mitspielern hat, gewinnt das Spiel.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliSekunden);
            }
            System.out.println();
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        spieleranzahlAngeben(2,100);
        spielerBenennen();
        setAktuellerSpieler(getSpielernamen().getFirst());
    }

    public void starteSpiel() {
        voreinstellung();
        // Spiel startet hier.
        boolean spielLaeuft = true;
        while (spielLaeuft) {
            System.out.println();
            System.out.println(getAktuellerSpieler() + " ist dran.");
            System.out.print("Bitte geben Sie die Ziffer ");
            for (int i = 0; i < Option.values().length; i++) {
                System.out.print(i + " (" + Option.values()[i].getBezeichnung() + ")");
                if (i == 0) {
                    System.out.print(", ");
                } else if (i == 1) {
                    System.out.print(" oder ");
                }
            }
            System.out.println(" ein.");
            Option option = optionAuswahlValidieren();
            SpielErgebnis spielErgebnis = auswahlAuswerten(option);
            switch (spielErgebnis) {
                case RUNDE_GEWONNEN, RUNDE_VERLOREN -> spielerWechseln();
                case SPIELGEWINNER_VORHANDEN -> {
                    System.out.println("Punktetabelle:");
                    for (int i = 0; i < getSpielerpunkte().size(); i++) {
                        System.out.println(getSpielernamen().get(i) + ": " + getSpielerpunkte().get(i) + " Punkte");
                    }
                    System.out.println();
                    spielLaeuft = false; // Spiel wird beendet.
                }
            }
        }
    }

    private Option optionAuswahlValidieren() { // Gibt eine gültige Option auswahl zurück.
        int optionIndex = -1;
        while (optionIndex == -1) {
            try {
                int eingabe = Integer.parseInt(scanner.nextLine());
                if (eingabe >= 0 && eingabe < Option.values().length) {
                    optionIndex = eingabe; // Gültige Eingabe gefunden
                } else {
                    System.out.println("Geben Sie eine gültige Ziffer ein.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Geben Sie eine gültige Ziffer ein.");
            }
        }
        return Option.values()[optionIndex];
    }

    private SpielErgebnis auswahlAuswerten(Option optionVomAS) { // AS = Aktueller Spieler
        int optionIndex = random.nextInt(Option.values().length);
        Option optionVomBot = Option.values()[optionIndex];
        if (unentschieden(optionVomAS, optionVomBot)) {
            return SpielErgebnis.UNENTSCHIEDEN;
        } else if (rundeGewonnen(optionVomAS, optionVomBot)) {
            if (spielGewinnerVorhanden()) {
                return SpielErgebnis.SPIELGEWINNER_VORHANDEN;
            }
            return SpielErgebnis.RUNDE_GEWONNEN;
        } else {
            // Wenn verloren:
            System.out.println("Der Bot hat " + fettFormatieren + textUnterstreichen + Option.values()[optionIndex].getBezeichnung() + konsolenReset + " angegeben.");
            System.out.println(optionVomAS.getBezeichnung() + " < " + optionVomBot.getBezeichnung());
            System.out.println("Sie haben die Runde verloren.");
            if (spielGewinnerVorhanden()) {
                return SpielErgebnis.SPIELGEWINNER_VORHANDEN;
            }
            return SpielErgebnis.RUNDE_VERLOREN;
        }
    }

    private boolean unentschieden(Option optionVomAS, Option optionVomBot) {
        if (optionVomAS == optionVomBot) {
            // Wenn unentschieden:
            System.out.println("Der Bot hat " + fettFormatieren + textUnterstreichen + optionVomBot.getBezeichnung() + konsolenReset + " angegeben.");
            System.out.println(optionVomAS.getBezeichnung() + " = " + optionVomBot.getBezeichnung());
            System.out.println(getAktuellerSpieler() + ". Da Sie unentschieden haben, dürfen Sie nochmal eine Auswahl angeben.");
            return true;
        }
        return false;
    }

    private boolean rundeGewonnen(Option optionVomAS, Option optionVomBot) {
        if ((optionVomAS == Option.PAPIER && optionVomBot == Option.STEIN) ||
                (optionVomAS == Option.STEIN && optionVomBot == Option.SCHERE) ||
                (optionVomAS == Option.SCHERE && optionVomBot == Option.PAPIER)) {
            // Wenn Runde gewonnen:
            System.out.println("Der Bot hat " + fettFormatieren + textUnterstreichen + optionVomBot.getBezeichnung() + konsolenReset + " angegeben.");
            System.out.println(optionVomAS.getBezeichnung() + " > " + optionVomBot.getBezeichnung());
            System.out.println("Sie haben die Runde gewonnen und 1 Punkt erhalten.");
            erhoehePunkteDesAktuellenSpielers(1);
            System.out.println("Punktestand: " + punkteDesAktuellenSpielers());

            return true;
        }
        return false;
    }

    private boolean spielGewinnerVorhanden() {
        boolean letzterspielerIstDran = getAktuellerSpieler().equals(getSpielernamen().getLast());
        if (letzterspielerIstDran && Collections.max(getSpielerpunkte()) >= 5) {
            ArrayList<Integer> temp = new ArrayList<>(getSpielerpunkte());
            Integer punkteDesBestenSpielers = Collections.max(getSpielerpunkte());
            getSpielerpunkte().remove(punkteDesBestenSpielers);
            Integer punkteDesZweitbestenSpielers = Collections.max(getSpielerpunkte());
            setSpielerpunkte(temp); // Die Spielerpunkte werden wieder zurückgesetzt.
            if ((punkteDesBestenSpielers - punkteDesZweitbestenSpielers) >= 2) {
                // Wenn jemand das Spiel gewonnen hat:
                int indexDesGewinners = getSpielerpunkte().indexOf(punkteDesBestenSpielers);
                String gewinner = getSpielernamen().get(indexDesGewinners);
                setAktuellerSpieler(gewinner);
                System.out.println();
                System.out.println(gewinner + " hat " + punkteDesBestenSpielers + " Punkte und somit das Spiel gewonnen!");
                return true;
            }
        }
        return false;
    }
}