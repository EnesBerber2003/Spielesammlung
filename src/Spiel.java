import java.util.ArrayList;
import java.util.Scanner;

abstract class Spiel {
    // Instanzvariablen
    private ArrayList<String> spielernamen;
    private ArrayList<Integer> spielerpunkte;
    private final Scanner scanner;
    private String aktuellerSpieler;
    private int anzahlDerSpieler;

    // Konstruktor
    public Spiel() {
        spielernamen = new ArrayList<>();
        spielerpunkte = new ArrayList<>();
        scanner = new Scanner(System.in);
        aktuellerSpieler = "";
        anzahlDerSpieler = 0;
    }


    // Methoden

    abstract void starteSpiel();

    public void spieleranzahlAngeben(int min, int max) {
        if (anzahlDerSpieler == 0) {
            System.out.println("Geben Sie bitte eine Spieleranzahl zwischen " + min + " und " + max + " ein.");
            while (anzahlDerSpieler < min || anzahlDerSpieler > max) {
                try {
                    anzahlDerSpieler = Integer.parseInt(scanner.nextLine());
                    if (anzahlDerSpieler < min || anzahlDerSpieler > max) {
                        System.out.println("Es dürfen mindestens " + min + " und maximal " + max + " Spieler spielen.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Bitte geben Sie eine Ziffer ein.");
                }
            }
        }
    }

    public void spielerBenennen() {
        while (spielernamen.size() < anzahlDerSpieler) {
            System.out.println("Wie heißt Spieler Nr. " + (spielernamen.size() + 1) + "?");
            String name = scanner.nextLine();
            while (name.isEmpty() || spielernamen.contains(name)) {
                if (name.isEmpty()) {
                    System.out.println("Geben Sie bitte ein Namen ein.");
                } else if (spielernamen.contains(name)) {
                    System.out.println("Dieser Name ist bereits vorhanden.");
                    System.out.println("Falls Sie den gleichen Namen wie eines Ihrer Mitspieler haben,");
                    System.out.println("könnten Sie sich z. B. wie folgt bennenen: " + name + "2.");
                } else {
                    break;
                }
                name = scanner.nextLine();
            }
            spielernamen.add(name);
            spielerpunkte.add(0); // Jeder Spieler bekommt ein Punktestand.
        }
        System.out.println();
    }

    public void spielerWechseln() {
        if (spielernamen.indexOf(aktuellerSpieler) == (anzahlDerSpieler - 1)) {
            aktuellerSpieler = spielernamen.getFirst();
        } else {
            aktuellerSpieler = spielernamen.get(spielernamen.indexOf(aktuellerSpieler) + 1);
        }
    }

    public int punkteDesAktuellenSpielers() {
        int indexDesAktuellenSpielers = spielernamen.indexOf(aktuellerSpieler);
        return spielerpunkte.get(indexDesAktuellenSpielers);
    }

    public void erhoehePunkteDesAktuellenSpielers(int punkte) {
        // Erhöht die Punkte des aktuellen Spielers um die angegebene Anzahl.
        int indexDesAktuellenSpielers = spielernamen.indexOf(aktuellerSpieler);
        int punkteDesAktuellenSpielers = spielerpunkte.get(indexDesAktuellenSpielers);
        spielerpunkte.set(indexDesAktuellenSpielers, punkteDesAktuellenSpielers + punkte);
    }


    // Getters und Setters

    public ArrayList<String> getSpielernamen() {
        return spielernamen;
    }

    public void setSpielernamen(ArrayList<String> spielernamen) {
        this.spielernamen = spielernamen;
    }

    public ArrayList<Integer> getSpielerpunkte() {
        return spielerpunkte;
    }

    public void setSpielerpunkte(ArrayList<Integer> spielerpunkte) {
        this.spielerpunkte = spielerpunkte;
    }

    public String getAktuellerSpieler() {
        return aktuellerSpieler;
    }

    public void setAktuellerSpieler(String aktuellerSpieler) {
        this.aktuellerSpieler = aktuellerSpieler;
    }

    public int getAnzahlDerSpieler() {
        return anzahlDerSpieler;
    }

    public void setAnzahlDerSpieler(int anzahlDerSpieler) {
        this.anzahlDerSpieler = anzahlDerSpieler;
    }

}