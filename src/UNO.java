import java.util.*;

public class UNO extends Spiel {
    // Instanzvariablen
    private final String anwenderNr = "1";
    private final String fettFormatieren, textUnterstreichen, konsolenReset;
    private final ArrayList<ArrayList<String>> spielerkartenListen;
    private final Scanner scanner;
    private final Random random;
    private final ArrayList<String> alleKarten, stapel;
    private final ArrayList<Integer> setzbareKartenIndizes;
    private int strafKartenAnzahl;
    private KartenFarbe liegendeFarbe;
    private KartenTyp liegenderTyp;
    boolean richtungIstUhrzeiger;

    private enum KartenFarbe {
        // ANSI-Farbcodes für die Konsolenausgabe
        // https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
        ROT("\u001B[31m" + "rot"),
        GRUEN("\u001B[32m" + "grün"),
        BLAU("\u001B[34m" + "blau"),
        GELB("\u001B[33m" + "gelb"),
        WUENSCHE("\u001B[37m" + "wünsche");

        private final String bezeichnung; // Die Bezeichnung dient zur ästhetischen Konsolenausgabe der Karten.

        // Enum Konstruktor
        KartenFarbe(String bezeichnung) {
            this.bezeichnung = bezeichnung;
        }

        public String getBezeichnung() {
            return bezeichnung;
        }
    }

    private enum KartenTyp {
        NULL("0"), EINS("1"), ZWEI("2"), DREI("3"), VIER("4"), FUENF("5"), SECHS("6"), SIEBEN("7"),
        ACHT("8"), NEUN("9"),
        AUSSETZEN("Aussetzen"),
        RICHTUNGSWECHSEL("Richtungswechsel"),
        ZIEHE2("Ziehe(+2)"),
        KARTENORMAL("Karte"),
        KARTEZIEHE4("Karte(+4)");


        private final String bezeichnung; // Die Bezeichnung dient zur ästhetischen Konsolenausgabe der Karten.

        // Enum Konstruktor
        KartenTyp(String bezeichnung) {
            this.bezeichnung = bezeichnung;
        }

        public String getBezeichnung() {
            return bezeichnung;
        }
    }

    // Konstruktor
    public UNO() {
        fettFormatieren = "\u001B[1m";
        textUnterstreichen = "\u001B[4m";
        konsolenReset = "\u001B[0m"; // Damit wird das Format und die Farbe auf der Konsole zurückgesetzt.
        richtungIstUhrzeiger = true;
        spielerkartenListen = new ArrayList<>();
        scanner = new Scanner(System.in);
        random = new Random();
        setzbareKartenIndizes = new ArrayList<>();
        stapel = new ArrayList<>();
        alleKarten = new ArrayList<>();  // in diese Liste werden alle verfügbaren Karten im Spiel gespeichert.
        // Diese for-Schleife speichert alle verfügbaren 108 Karten:
        for (int i = 0; i < 8; i++) {   // https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/UNO_cards_deck.svg/220px-UNO_cards_deck.svg.png
            String kartenFarbe;
            if (i < 4) {
                kartenFarbe = KartenFarbe.values()[i].toString();
            } else {
                kartenFarbe = KartenFarbe.values()[i - 4].toString();
            }
            for (KartenTyp typ : KartenTyp.values()) {
                if (typ == KartenTyp.KARTENORMAL) {
                    if (i < 4) {
                        alleKarten.add(KartenFarbe.WUENSCHE + typ.toString());
                    }
                } else if (typ == KartenTyp.KARTEZIEHE4) {
                    if (i >= 4) {
                        alleKarten.add(KartenFarbe.WUENSCHE + typ.toString());
                    }
                } else {
                    if (i >= 4 && typ == KartenTyp.NULL) {
                        continue;
                    }
                    alleKarten.add(kartenFarbe + typ.toString());
                }
            }

        }
        Collections.shuffle(alleKarten); // Die Karten werden gemischt.
        String liegendeKarte = alleKarten.getFirst();
        while (liegendeKarte.startsWith(KartenFarbe.WUENSCHE.toString())) {
            Collections.shuffle(alleKarten);
            liegendeKarte = alleKarten.getFirst();
        }
        alleKarten.removeFirst();
        stapel.add(liegendeKarte);
        liegendeKarteAktualisieren();
        setAktuellerSpieler(anwenderNr); // Der Anwender fängt an.
    }

    // Methoden

    private void voreinstellung() {
        int milliSekunden = 50;
        try {
            for (char c : "Willkommen beim Spiel UNO!".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliSekunden);
            }
            System.out.println();
            for (char c : "In diesem Spiel können Sie UNO gegen 1 bis 9 Bots spielen.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliSekunden);
            }
            System.out.println();
            for (char c : "Der erste Spieler der 500 Punkte hat, gewinnt das Spiel.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliSekunden);
            }
            System.out.println();
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        spieleranzahlAngeben(2, 10);
        // Karten werden ausgeteilt:
        for (int i = 0; i < getAnzahlDerSpieler(); i++) {
            ArrayList<String> spielerKarten = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                spielerKarten.add(alleKarten.getFirst());
                alleKarten.removeFirst();
            }
            spielerkartenListen.add(spielerKarten);
            getSpielernamen().add(i + 1 + "");
            getSpielerpunkte().add(0);
        }
        ladevorgangAusgeben(8,"Karten werden ausgeteilt");
    }

    public void starteSpiel() {
        voreinstellung();
        // Spiel startet hier.
        boolean spielLaeuft = true;
        while (spielLaeuft) {
            uebersichtAusgeben();
            int eingabeNr;
            if (hatSetzbareKarten()) {
                kartenAusgeben();
                eingabeNr = karteAuswaehlenOderZiehenAuswaehlen();
                if (eingabeNr == spielerkartenListen.get(aktuellerSpielerIndex()).size()) { // Indem man die größe der Kartenliste angibt, kann man "Ziehen" auswählen.
                    ziehenUndGezogeneKartePruefen();
                } else {
                    // Die ausgewählte Karte wird gesetzt.
                    if (!anwenderIstDran()) {
                        ladevorgangAusgeben(5, "Der Bot " + fettFormatieren + textUnterstreichen + "setzt eine" + konsolenReset + " Karte");
                    }
                    karteSetzen(eingabeNr);
                    kartenAuswirkungDurchfuehren();
                }
            } else {
                // Wenn man keine Karte zum setzen hat:
                ziehenUndGezogeneKartePruefen();
            }
            if (spielerkartenListen.get(aktuellerSpielerIndex()).isEmpty()) {
                if (anwenderIstDran()) {
                    System.out.println("Gratuliere, Sie haben die Runde gewonnen!");
                } else {
                    System.out.println("Der Bot " + aktuellerSpielerIndex() + " hat die Runde gewonnen!");
                }
                int punkte = punkteAusrechnen();
                erhoehePunkteDesAktuellenSpielers(punkte);
                System.out.println();
                System.out.println("Punktetabelle:");
                for (int i = 0; i < getSpielerpunkte().size(); i++) {
                    if (i == 0) {
                        System.out.println("Sie:   " + getSpielerpunkte().get(i) + " Punkte");
                    } else {
                        System.out.println("Bot " + i + ": " + getSpielerpunkte().get(i) + " Punkte");
                    }
                }
                if (spielGewonnen()) {
                    spielLaeuft = false; // Spiel wird beendet.
                } else {
                    naechsteRundeStarten();
                }
            }
            spielerWechseln();
        }
    }

    private void ladevorgangAusgeben(int sekunden, String lademeldung) {
        System.out.print(lademeldung);
        try {
            for (int i = 0; i < sekunden; i++) {
                System.out.print(".");
                Thread.sleep(1000);
            }
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void uebersichtAusgeben() {
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
        for (int i = 0; i < getAnzahlDerSpieler(); i++) {
            if (i != 0) {
                for (int j = 0; j < 12; j++) {
                    System.out.print("-");
                }
            }
        }
        System.out.println();
        for (int i = 0; i < getAnzahlDerSpieler(); i++) {
            if (i != 0) {
                if (i == aktuellerSpielerIndex()) {
                    System.out.print(fettFormatieren + textUnterstreichen);
                }
                System.out.print("Bot " + i + ": " +  spielerkartenListen.get(i).size());
                System.out.print(konsolenReset + "   ");
            }
        }
        System.out.println();
        System.out.println("Liegende Karte: " + fettFormatieren + textUnterstreichen + liegendeFarbe.getBezeichnung() + liegenderTyp.getBezeichnung() + konsolenReset);
        for (int i = 0; i < getAnzahlDerSpieler(); i++) {
            if (i != 0) {
                for (int j = 0; j < 12; j++) {
                    System.out.print("-");
                }
            }
        }
        System.out.println();
        System.out.println();
    }

    private boolean hatSetzbareKarten() {
        boolean setzbareKarteVorhanden = false;
        for (KartenFarbe farbe : KartenFarbe.values()) {
            for (KartenTyp typ : KartenTyp.values()) {
                for (String karte : spielerkartenListen.get(aktuellerSpielerIndex())) {
                    if (karte.startsWith(farbe.toString()) && karte.endsWith(typ.toString())) {
                        if (strafKartenAnzahl == 0) {
                            if (karte.startsWith(liegendeFarbe.toString()) || karte.endsWith(liegenderTyp.toString()) || karte.startsWith(KartenFarbe.WUENSCHE.toString())) {
                                setzbareKarteVorhanden = true;
                                setzbareKartenIndizes.add(spielerkartenListen.get(aktuellerSpielerIndex()).indexOf(karte));
                            }
                        } else {
                            if (karte.endsWith(KartenTyp.ZIEHE2.toString())) {
                                setzbareKarteVorhanden = true;
                                setzbareKartenIndizes.add(spielerkartenListen.get(aktuellerSpielerIndex()).indexOf(karte));
                            }
                        }
                    }
                }
            }
        }
        return setzbareKarteVorhanden;
    }

    private void kartenAusgeben() {
        if (anwenderIstDran()) {
            System.out.print("Ihre Karten:");
            int anzahlAusgegebenerKarten = 0;
            for (KartenFarbe farbe : KartenFarbe.values()) {
                for (KartenTyp typ : KartenTyp.values()) {
                    for (String karte : spielerkartenListen.get(aktuellerSpielerIndex())) {
                        if (karte.startsWith(farbe.toString()) && karte.endsWith(typ.toString())) {
                            if (anzahlAusgegebenerKarten >= 1) {
                                System.out.print(",");
                            }
                            if (setzbareKartenIndizes.contains(spielerkartenListen.get(aktuellerSpielerIndex()).indexOf(karte))) {
                                int kartenIndex = spielerkartenListen.get(aktuellerSpielerIndex()).indexOf(karte);
                                System.out.print(" (" + kartenIndex + ")" + fettFormatieren + textUnterstreichen + farbe.getBezeichnung() + typ.getBezeichnung() + konsolenReset);
                            } else {
                                System.out.print(" " + farbe.getBezeichnung() + typ.getBezeichnung() + konsolenReset);
                            }
                            anzahlAusgegebenerKarten++;
                        }
                    }
                }
            }
        }
    }

    private int karteAuswaehlenOderZiehenAuswaehlen() { // Gibt eine gültige Eingabenummer zurück.
        int eingabeNr = -1;
        if (anwenderIstDran()) {
            // Optional kann man das "Ziehen" auswählen, wenn man keine Karte setzen möchte.
            if (strafKartenAnzahl == 0) {
                System.out.println("       Optional: Eine Karte ziehen(" + spielerkartenListen.getFirst().size() + ").");
            } else {
                System.out.println("       Optional: " + strafKartenAnzahl + " Karten ziehen(" + spielerkartenListen.getFirst().size() + ").");
            }
            while (eingabeNr == -1) {
                try {
                    int eingabe = Integer.parseInt(scanner.nextLine()); // Indem man die größe der Kartenliste angibt, kann man "Ziehen" auswählen.
                    if (setzbareKartenIndizes.contains(eingabe) || (eingabe == spielerkartenListen.get(aktuellerSpielerIndex()).size())) {
                        eingabeNr = eingabe;
                    } else {
                        System.out.println("Geben Sie eine gültige Ziffer an.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Geben Sie eine Ziffer an.");
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Geben Sie eine gültige Ziffer an.");
                }
            }
        } else {
            // In dem Rumpf wird dem Bot eine Auswahl Logik angegeben, damit der Bot nicht einfach zu besiegen ist.
            ArrayList<Integer> normaleKartenIndizes = new ArrayList<>(); // Liste zur Speicherung der Indizes normaler Karten (keine Wünschekarten).
            ArrayList<Integer> wuenscheKartenIndizes = new ArrayList<>();
            for (int index : setzbareKartenIndizes) {
                String karte = spielerkartenListen.get(aktuellerSpielerIndex()).get(index);
                if (karte.startsWith(KartenFarbe.WUENSCHE.toString())) {
                    wuenscheKartenIndizes.add(index);
                } else {
                    normaleKartenIndizes.add(index);
                }
            }
            if (!normaleKartenIndizes.isEmpty()) {
                // Falls normale Karten vorhanden sind, wähle zufällig eine aus.
                eingabeNr = normaleKartenIndizes.get(random.nextInt(normaleKartenIndizes.size()));
            } else {
                int vorhandeneFarbenAnzahl = 0; // Zählt die Anzahl der vorhandenen Farben. Z. B. wenn rote und blaue Karten vorhanden sind, dann ist die Anzahl 2.
                for (KartenFarbe farbe : KartenFarbe.values()) {
                    if (farbe.equals(KartenFarbe.WUENSCHE)) {
                        continue;
                    }
                    for (String karte : spielerkartenListen.get(aktuellerSpielerIndex())) {
                        if (karte.startsWith(farbe.toString())) {
                            vorhandeneFarbenAnzahl++;
                            break;
                        }
                    }
                }
                // Entscheide basierend auf den vorhandenen Farben und Wünschekarten
                if (vorhandeneFarbenAnzahl <= wuenscheKartenIndizes.size()) {
                    // Der Bot soll eine normale Wünschekarte bevorzugen.
                    if (spielerkartenListen.get(aktuellerSpielerIndex()).contains(KartenFarbe.WUENSCHE.toString() + KartenTyp.KARTENORMAL)) {
                        eingabeNr = spielerkartenListen.get(aktuellerSpielerIndex()).indexOf(KartenFarbe.WUENSCHE.toString() + KartenTyp.KARTENORMAL);
                    } else  {
                        eingabeNr = setzbareKartenIndizes.getFirst();  // Eine 4-Zieh Karte wird ausgewählt.
                    }
                } else {
                    // Der Bot wählt "Ziehen" aus.
                    eingabeNr = spielerkartenListen.get(aktuellerSpielerIndex()).size();
                }
            }
        }
        setzbareKartenIndizes.clear();
        return eingabeNr;
    }

    private void ziehenUndGezogeneKartePruefen() {
        System.out.println();
        if (strafKartenAnzahl == 0) {
            if (anwenderIstDran()) {
                ladevorgangAusgeben(4, "Sie " + fettFormatieren + textUnterstreichen + "ziehen eine" + konsolenReset + " Karte");
            } else {
                ladevorgangAusgeben(4, "Der Bot " + fettFormatieren + textUnterstreichen + "zieht eine" + konsolenReset + " Karte");
            }
            kartenZiehen(1);
            if (hatSetzbareKarten()) {
                int kartenNr = spielerkartenListen.get(aktuellerSpielerIndex()).size() - 1;
                if (setzbareKartenIndizes.contains(kartenNr)) {
                    if (anwenderIstDran()) {
                        String kartenFarbe = "";
                        System.out.println();
                        for (KartenFarbe farbe : KartenFarbe.values()) {
                            if (spielerkartenListen.get(aktuellerSpielerIndex()).get(kartenNr).startsWith(farbe.toString())) {
                                kartenFarbe = farbe.getBezeichnung();
                            }
                        }
                        String kartenTyp = "";
                        for (KartenTyp typ : KartenTyp.values()) {
                            if (spielerkartenListen.get(aktuellerSpielerIndex()).get(kartenNr).endsWith(typ.toString())) {
                                kartenTyp = typ.getBezeichnung();
                            }
                        }
                        System.out.println();
                        System.out.println("Möchten Sie die gezogene Karte(" + kartenFarbe + kartenTyp + konsolenReset + ") setzen? ja/nein");
                    }
                    String antwort = "";
                    while (!antwort.equals("ja") && !antwort.equals("nein")) {
                        if (anwenderIstDran()) {
                            antwort = scanner.nextLine();
                            if (!antwort.equals("ja") && !antwort.equals("nein")) {
                                System.out.println("Geben Sie bitte ja oder nein ein.");
                            }
                        } else {
                            // In dem Rumpf wird dem Bot eine Auswahl Logik angegeben, damit der Bot nicht einfach zu besiegen ist.
                            if (spielerkartenListen.get(aktuellerSpielerIndex()).get(kartenNr).startsWith(KartenFarbe.WUENSCHE.toString())) {
                                int vorhandeneFarbenAnzahl = 0; // Zählt die Anzahl der vorhandenen Farben. Z. B. wenn rote und blaue Karten vorhanden sind, dann ist die Anzahl 2.
                                int wuenscheKartenAnzahl = 0;
                                for (String karte : spielerkartenListen.get(aktuellerSpielerIndex())) {
                                    for (KartenFarbe farbe : KartenFarbe.values()) {
                                        if (karte.startsWith(farbe.toString())) {
                                            if (farbe == KartenFarbe.WUENSCHE) {
                                                wuenscheKartenAnzahl++;
                                            } else {
                                                vorhandeneFarbenAnzahl++;
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (vorhandeneFarbenAnzahl <= wuenscheKartenAnzahl) {
                                    ladevorgangAusgeben(5, "Der Bot " + fettFormatieren + textUnterstreichen + "setzt die gezogene" + konsolenReset + " Karte");
                                    antwort = "ja";
                                } else {
                                    antwort = "nein";
                                }
                            } else {
                                ladevorgangAusgeben(5, "Der Bot " + fettFormatieren + textUnterstreichen + "setzt die gezogene" + konsolenReset + " Karte");
                                antwort = "ja";
                            }
                        }
                    }
                    if (antwort.equals("ja")) {
                        karteSetzen(kartenNr);
                        kartenAuswirkungDurchfuehren();
                    }
                }
                setzbareKartenIndizes.clear();
            }
        } else {
            if (anwenderIstDran()) {
                ladevorgangAusgeben(5, "Sie " + fettFormatieren + textUnterstreichen + "ziehen " + strafKartenAnzahl + konsolenReset + " Karten");
            } else {
                ladevorgangAusgeben(5, "Der Bot " + fettFormatieren + textUnterstreichen + "zieht " + strafKartenAnzahl + konsolenReset + " Karten");
            }
            kartenZiehen(strafKartenAnzahl);
            strafKartenAnzahl = 0;
        }
    }

    private void kartenAuswirkungDurchfuehren() {
        // Regeln: https://www.meinspiel.de/blog/uno-regeln/?srsltid=AfmBOop1puidm6nouVdqZMB6-YRm6_r7plptOiRcgr2K5xBn139CNDSW
        if (spielerkartenListen.get(aktuellerSpielerIndex()).isEmpty()) {
            return;
        }
        switch (liegenderTyp) {
            case AUSSETZEN -> {
                spielerWechseln();
                uebersichtAusgeben();
                if (anwenderIstDran()) {
                    ladevorgangAusgeben(4, "Sie " + fettFormatieren + textUnterstreichen + "setzen aus" + konsolenReset);
                } else {
                    ladevorgangAusgeben(4, "Der Bot " + fettFormatieren + textUnterstreichen + "setzt aus" + konsolenReset);
                }
            }
            case RICHTUNGSWECHSEL -> {
                if (getAnzahlDerSpieler() == 2) {
                    // Aussetzen
                    spielerWechseln();
                    uebersichtAusgeben();
                    if (anwenderIstDran()) {
                        ladevorgangAusgeben(4, "Sie " + fettFormatieren + textUnterstreichen + "setzen aus" + konsolenReset);
                    } else {
                        ladevorgangAusgeben(4, "Der Bot " + fettFormatieren + textUnterstreichen + "setzt aus" + konsolenReset);
                    }
                } else {
                    richtungIstUhrzeiger = !richtungIstUhrzeiger; // Richtung wird gewechselt.
                }
            }
            case ZIEHE2 -> strafKartenAnzahl = strafKartenAnzahl + 2;

        }
        if (liegendeFarbe.equals(KartenFarbe.WUENSCHE)) {
            if (anwenderIstDran()) {
                System.out.println("Geben Sie die Farbe (Ziffer) an die Sie sich wünschen.");
                for (int i = 0; i < KartenFarbe.values().length - 1; i++) { // Die Farbe "Wünsche" soll nicht auswählbar sein.
                    System.out.println(KartenFarbe.values()[i].getBezeichnung() + "(" + i + ")" + konsolenReset);
                }
                int farbenIndex = -1;
                while (farbenIndex < 0 || farbenIndex >= KartenFarbe.values().length - 1) {
                    try {
                        farbenIndex = Integer.parseInt(scanner.nextLine());
                        if (farbenIndex >= 0 && farbenIndex < KartenFarbe.values().length - 1) {
                            liegendeFarbe = KartenFarbe.values()[farbenIndex];
                            break;
                        } else {
                            System.out.println("Geben Sie eine gültige Ziffer an.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Geben Sie eine Ziffer an.");
                    }
                }
            } else {
                ladevorgangAusgeben(4, "Der Bot " + fettFormatieren + textUnterstreichen + "wünscht" + konsolenReset + " eine Farbe");
                ArrayList<Integer> kartenFarbenAnzahl = new ArrayList<>(); // Je nachdem wie viele Karten einer Farbe vorhanden sind, wird die Farbe ausgewählt.
                int farbenAnzahl = 0;
                for (KartenFarbe farbe : KartenFarbe.values()) {
                    for (String karte : spielerkartenListen.get(aktuellerSpielerIndex())) {
                        if (karte.startsWith(farbe.toString())) {
                            farbenAnzahl++;
                        }
                    }
                    kartenFarbenAnzahl.add(farbenAnzahl);
                    farbenAnzahl = 0;
                }
                int farbenNr = kartenFarbenAnzahl.indexOf(Collections.max(kartenFarbenAnzahl)); // Die Farbe mit den meisten Karten wird ausgewählt.
                liegendeFarbe = KartenFarbe.values()[farbenNr];
            }
            if (liegenderTyp.equals(KartenTyp.KARTEZIEHE4)) {
                spielerWechseln();
                uebersichtAusgeben();
                strafKartenAnzahl = strafKartenAnzahl + 4;
                ziehenUndGezogeneKartePruefen();
            }
        }
    }

    private int punkteAusrechnen() {
        // Punkte der einzelnen Karten: https://www.meinspiel.de/app/uploads/2019/11/Punkte-UNO-1.png
        ladevorgangAusgeben(7, "Die Punkte werden ausgerechnet");
        int punkte = 0;
        for (ArrayList<String> spielerkartenListe : spielerkartenListen) {
            for (String karte : spielerkartenListe) {
                for (int i = 0; i < 10; i++) {
                    if (karte.endsWith(i + "")) {
                        punkte = punkte + i;
                    }
                }
                if (karte.endsWith(KartenTyp.AUSSETZEN.toString()) || karte.endsWith(KartenTyp.ZIEHE2.toString()) || karte.endsWith(KartenTyp.RICHTUNGSWECHSEL.toString())) {
                    punkte = punkte + 20;
                }
                if (karte.startsWith(KartenFarbe.WUENSCHE.toString())) {
                    punkte = punkte + 50;
                }
            }
        }
        System.out.println("Dem Gewinner werden " + punkte + " Punkte vergeben.");
        return punkte;
    }

    private boolean spielGewonnen() {
        if (getSpielerpunkte().get(aktuellerSpielerIndex()) >= 500) {
            if (anwenderIstDran()) {
                System.out.println("Gratuliere, Sie haben das Spiel gewonnen!");
            } else {
                System.out.println("Gratuliere, der Bot " + aktuellerSpielerIndex() + " hat das Spiel gewonnen!");
            }
            return true;
        } else {
            return false;
        }
    }

    public void naechsteRundeStarten() {
        System.out.println("Klicken Sie auf die Enter-Taste um die nächste Runde zu beginnen.");
        String antwort = " ";
        while (!antwort.isEmpty()) {
            antwort = scanner.nextLine();
        }
        for (ArrayList<String> spielerkarten : spielerkartenListen) {
            alleKarten.addAll(spielerkarten);
            spielerkarten.clear();
        }
        alleKarten.addAll(stapel);
        Collections.shuffle(alleKarten);
        for (int i = 0; i < getAnzahlDerSpieler(); i++) {
            kartenZiehen(7);
            spielerWechseln();
        }
        String liegendeKarte = alleKarten.getFirst();
        while (liegendeKarte.startsWith(KartenFarbe.WUENSCHE.toString())) {
            Collections.shuffle(alleKarten);
            liegendeKarte = alleKarten.getFirst();
        }
        alleKarten.removeFirst();
        stapel.add(liegendeKarte);
        liegendeKarteAktualisieren();
    }

    private void liegendeKarteAktualisieren() {
        for (KartenFarbe farbe : KartenFarbe.values()) {
            if (stapel.getLast().startsWith(farbe.toString())) {
                liegendeFarbe = farbe;
                break;
            }
        }
        for (KartenTyp typ : KartenTyp.values()) {
            if (stapel.getLast().endsWith(typ.toString())) {
                liegenderTyp = typ;
                break;
            }
        }
    }

    private void karteSetzen(int eingabeNr) {
        stapel.add(spielerkartenListen.get(aktuellerSpielerIndex()).get(eingabeNr));
        spielerkartenListen.get(aktuellerSpielerIndex()).remove(eingabeNr);
        liegendeKarteAktualisieren();
    }

    private void kartenZiehen(int anzahl) {
        for (int i = 0; i < anzahl; i++) {
            spielerkartenListen.get(aktuellerSpielerIndex()).add(alleKarten.getFirst());
            alleKarten.removeFirst();
            if (alleKarten.isEmpty()) {
                alleKarten.addAll(stapel);
                stapel.clear();
                Collections.shuffle(alleKarten);
            }
        }
    }

    private boolean anwenderIstDran() {
        return getAktuellerSpieler().equals(anwenderNr);
    }

    private int aktuellerSpielerIndex() {
        return Integer.parseInt(getAktuellerSpieler()) - 1;
    }


    @Override
    public void spielerWechseln() {
        if (richtungIstUhrzeiger) {
            if (Integer.parseInt(getAktuellerSpieler()) == getAnzahlDerSpieler()) {
                setAktuellerSpieler("1");
            } else {
                setAktuellerSpieler(Integer.parseInt(getAktuellerSpieler()) + 1 + "");
            }
        } else {
            if (anwenderIstDran()) {
                setAktuellerSpieler(getAnzahlDerSpieler() + "");
            } else {
                setAktuellerSpieler(Integer.parseInt(getAktuellerSpieler()) - 1 + "");
            }
        }
    }
}