import java.util.*;

public class UNO extends Game {
    // Instanzvariablen
    private final String USER;
    private final String formatBold, underlineText, resetConsole;
    private final ArrayList<ArrayList<String>> playerCardLists;
    private final Scanner scanner;
    private final Random random;
    private final ArrayList<String> allCards, stack;
    private final ArrayList<Integer> usableCardIndices;
    private int penaltyCardCount;
    private CardColor currentColor;
    private CardType currentType;
    boolean directionIsClockwise;

    private enum CardColor {
        // ANSI-Farbcodes für die Konsolenausgabe
        // https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
        RED("\u001B[31m" + "rot"),
        GREEN("\u001B[32m" + "grün"),
        BLUE("\u001B[34m" + "blau"),
        YELLOW("\u001B[33m" + "gelb"),
        WISH_COLOR("\u001B[37m" + "wünsche");

        private final String label; // Die Bezeichnung dient zur ästhetischen Konsolenausgabe der Karten.

        // Enum Konstruktor
        CardColor(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private enum CardType {
        ZERO("0"), ONE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"),
        SKIP("Aussetzen"), REVERSE_DIRECTION("Richtungswechsel"), DRAW2("Ziehe(+2)"),
        WISH_CARD_NORMAL("Karte"), WISH_CARD_DRAW4("Karte(+4)"); // Spezielle Typen für die Wünschefarbe

        private final String label; // Die Bezeichnung dient zur ästhetischen Konsolenausgabe der Karten.

        // Enum Konstruktor
        CardType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    // Konstruktor
    public UNO() {
        USER = "1";
        formatBold = "\u001B[1m";
        underlineText = "\u001B[4m";
        resetConsole = "\u001B[0m"; // Damit wird das Format und die Farbe auf der Konsole zurückgesetzt.
        directionIsClockwise = true;
        playerCardLists = new ArrayList<>();
        scanner = new Scanner(System.in);
        random = new Random();
        usableCardIndices = new ArrayList<>();
        stack = new ArrayList<>();
        allCards = new ArrayList<>();  // in diese Liste werden alle verfügbaren Karten im Spiel gespeichert.
        // Diese for-Schleife speichert alle verfügbaren 108 Karten:
        for (int i = 0; i < 8; i++) {   // https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/UNO_cards_deck.svg/220px-UNO_cards_deck.svg.png
            String cardColor;
            if (i < 4) {
                cardColor = CardColor.values()[i].toString();
            } else {
                cardColor = CardColor.values()[i - 4].toString();
            }
            for (CardType type : CardType.values()) {
                if (type == CardType.WISH_CARD_NORMAL) {
                    if (i < 4) {
                        allCards.add(CardColor.WISH_COLOR + type.toString());
                    }
                } else if (type == CardType.WISH_CARD_DRAW4) {
                    if (i >= 4) {
                        allCards.add(CardColor.WISH_COLOR + type.toString());
                    }
                } else {
                    if (i >= 4 && type == CardType.ZERO) {
                        continue;
                    }
                    allCards.add(cardColor + type.toString());
                }
            }

        }
        Collections.shuffle(allCards); // Die Karten werden gemischt.
        String currentCard = allCards.getFirst();
        while (currentCard.startsWith(CardColor.WISH_COLOR.toString())) {
            Collections.shuffle(allCards);
            currentCard = allCards.getFirst();
        }
        allCards.removeFirst();
        stack.add(currentCard);
        updateCurrentCard();
        setCurrentPlayer(USER); // Der Anwender fängt an.
        enterPlayerCount(2, 10);
        int milliseconds = 50;
        try {
            for (char c : "Willkommen beim Spiel UNO!".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliseconds);
            }
            System.out.println();
            for (char c : "In diesem Spiel können Sie UNO gegen 1 bis 9 Bots spielen.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliseconds);
            }
            System.out.println();
            for (char c : "Der erste Spieler der 500 Punkte hat, gewinnt das Spiel.".toCharArray()) {
                System.out.print(c);
                Thread.sleep(milliseconds);
            }
            System.out.println();
            System.out.println();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
        // Karten werden ausgeteilt:
        for (int i = 0; i < getPlayerCount(); i++) {
            ArrayList<String> playerCards = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                playerCards.add(allCards.getFirst());
                allCards.removeFirst();
            }
            playerCardLists.add(playerCards);
            getPlayerNames().add(i + 1 + "");
            getPlayerScores().add(0);
        }
        printLoadingScreen(8,"Karten werden ausgeteilt");
    }

    // Methoden

    public void startGame() {
        // Spiel startet hier.
        boolean gameInProgress = true;
        while (gameInProgress) {
            printGameOverview();
            int numberInput;
            if (hasUsableCards()) {
                if (isUserTurn()) {
                    printCards();
                }
                numberInput = selectCardOrDraw();
                if (numberInput == playerCardLists.get(getCurrentPlayerIndex()).size()) { // Indem man die größe der Kartenliste angibt, kann man "Ziehen" auswählen.
                    drawAndCheckDrawnCard();
                } else {
                    // Die ausgewählte Karte wird gesetzt.
                    if (!isUserTurn()) {
                        printLoadingScreen(5, "Der Bot " + formatBold + underlineText + "setzt eine" + resetConsole + " Karte");
                    }
                    useCard(numberInput);
                    applyCardEffect();
                }
            } else {
                // Wenn man keine Karte zum setzen hat:
                drawAndCheckDrawnCard();
            }
            if (playerCardLists.get(getCurrentPlayerIndex()).isEmpty()) {
                if (isUserTurn()) {
                    System.out.println("Gratuliere, Sie haben die Runde gewonnen!");
                } else {
                    System.out.println("Der Bot " + getCurrentPlayerIndex() + " hat die Runde gewonnen!");
                }
                int points = calculatePoints();
                increaseCurrentPlayerScore(points);
                System.out.println();
                System.out.println("Punktetabelle:");
                for (int i = 0; i < getPlayerScores().size(); i++) {
                    if (i == 0) {
                        System.out.println("Sie:   " + getPlayerScores().get(i) + " Punkte");
                    } else {
                        System.out.println("Bot " + i + ": " + getPlayerScores().get(i) + " Punkte");
                    }
                }
                if (gameWinnerExists()) {
                    gameInProgress = false; // Spiel wird beendet.
                } else {
                    startNextRound();
                }
            }
            switchPlayer();
        }
    }

    private void printLoadingScreen(int seconds, String loadingMessage) {
        System.out.print(loadingMessage);
        try {
            for (int i = 0; i < seconds; i++) {
                System.out.print(".");
                Thread.sleep(1000);
            }
            System.out.println();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    private void printGameOverview() {
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
        for (int i = 0; i < getPlayerCount(); i++) {
            if (i != 0) {
                for (int j = 0; j < 12; j++) {
                    System.out.print("-");
                }
            }
        }
        System.out.println();
        for (int i = 0; i < getPlayerCount(); i++) {
            if (i != 0) {
                if (i == getCurrentPlayerIndex()) {
                    System.out.print(formatBold + underlineText);
                }
                System.out.print("Bot " + i + ": " +  playerCardLists.get(i).size());
                System.out.print(resetConsole + "   ");
            }
        }
        System.out.println();
        System.out.println("Liegende Karte: " + formatBold + underlineText + currentColor.getLabel() + currentType.getLabel() + resetConsole);
        for (int i = 0; i < getPlayerCount(); i++) {
            if (i != 0) {
                for (int j = 0; j < 12; j++) {
                    System.out.print("-");
                }
            }
        }
        System.out.println();
        System.out.println();
    }

    private boolean hasUsableCards() {
        usableCardIndices.clear();
        boolean usableCardAvailable = false;
        for (CardColor color : CardColor.values()) {
            for (CardType type : CardType.values()) {
                for (String card : playerCardLists.get(getCurrentPlayerIndex())) {
                    if (card.startsWith(color.toString()) && card.endsWith(type.toString())) {
                        if (penaltyCardCount == 0) {
                            if (card.startsWith(currentColor.toString()) || card.endsWith(currentType.toString()) || card.startsWith(CardColor.WISH_COLOR.toString())) {
                                usableCardAvailable = true;
                                usableCardIndices.add(playerCardLists.get(getCurrentPlayerIndex()).indexOf(card));
                            }
                        } else {
                            if (card.endsWith(CardType.DRAW2.toString())) {
                                usableCardAvailable = true;
                                usableCardIndices.add(playerCardLists.get(getCurrentPlayerIndex()).indexOf(card));
                            }
                        }
                    }
                }
            }
        }
        return usableCardAvailable;
    }

    private void printCards() {
        System.out.print("Ihre Karten:");
        int printedCardsCount = 0;
        for (CardColor color : CardColor.values()) {
            for (CardType type : CardType.values()) {
                for (String card : playerCardLists.get(getCurrentPlayerIndex())) {
                    if (card.startsWith(color.toString()) && card.endsWith(type.toString())) {
                        if (printedCardsCount >= 1) {
                            System.out.print(",");
                        }
                        if (usableCardIndices.contains(playerCardLists.get(getCurrentPlayerIndex()).indexOf(card))) {
                            int cardIndex = playerCardLists.get(getCurrentPlayerIndex()).indexOf(card);
                            System.out.print(" (" + cardIndex + ")" + formatBold + underlineText + color.getLabel() + type.getLabel() + resetConsole);
                        } else {
                            System.out.print(" " + color.getLabel() + type.getLabel() + resetConsole);
                        }
                        printedCardsCount++;
                    }
                }
            }
        }
    }

    private int selectCardOrDraw() { // Gibt eine gültige Eingabenummer zurück.
        int numberInput = -1;
        if (isUserTurn()) {
            // Optional kann man das "Ziehen" auswählen, wenn man keine Karte setzen möchte.
            if (penaltyCardCount == 0) {
                System.out.println("       Optional: Eine Karte ziehen(" + playerCardLists.getFirst().size() + ").");
            } else {
                System.out.println("       Optional: " + penaltyCardCount + " Karten ziehen(" + playerCardLists.getFirst().size() + ").");
            }
            while (numberInput == -1) {
                try {
                    int input = Integer.parseInt(scanner.nextLine());
                    if (usableCardIndices.contains(input) || (input == playerCardLists.get(getCurrentPlayerIndex()).size())) { // Indem man die größe der Kartenliste angibt, kann man "Ziehen" auswählen.
                        numberInput = input;
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
            ArrayList<Integer> normalCardIndices = new ArrayList<>(); // Liste zur Speicherung der Indizes normaler Karten (keine Wünschekarten).
            ArrayList<Integer> wishCardIndices = new ArrayList<>();
            for (int cardIndex : usableCardIndices) {
                String card = playerCardLists.get(getCurrentPlayerIndex()).get(cardIndex);
                if (card.startsWith(CardColor.WISH_COLOR.toString())) {
                    wishCardIndices.add(cardIndex);
                } else {
                    normalCardIndices.add(cardIndex);
                }
            }
            if (!normalCardIndices.isEmpty()) {
                // Falls normale Karten vorhanden sind, wähle zufällig eine aus.
                numberInput = normalCardIndices.get(random.nextInt(normalCardIndices.size()));
            } else {
                int availableColorsCount = 0; // Zählt die Anzahl der vorhandenen Farben. Z. B. wenn rote und blaue Karten vorhanden sind, dann ist die Anzahl 2.
                for (CardColor color : CardColor.values()) {
                    if (color.equals(CardColor.WISH_COLOR)) {
                        continue;
                    }
                    for (String card : playerCardLists.get(getCurrentPlayerIndex())) {
                        if (card.startsWith(color.toString())) {
                            availableColorsCount++;
                            break;
                        }
                    }
                }
                // Entscheide basierend auf den vorhandenen Farben und Wünschekarten
                if (availableColorsCount <= wishCardIndices.size()) {
                    // Der Bot soll eine normale Wünschekarte bevorzugen:
                    if (playerCardLists.get(getCurrentPlayerIndex()).contains(CardColor.WISH_COLOR.toString() + CardType.WISH_CARD_NORMAL)) {
                        numberInput = playerCardLists.get(getCurrentPlayerIndex()).indexOf(CardColor.WISH_COLOR.toString() + CardType.WISH_CARD_NORMAL);
                    } else  {
                        numberInput = usableCardIndices.getFirst();  // Eine 4-Zieh Karte wird ausgewählt.
                    }
                } else {
                    // Der Bot wählt "Ziehen" aus.
                    numberInput = playerCardLists.get(getCurrentPlayerIndex()).size();
                }
            }
        }
        return numberInput;
    }

    private void drawAndCheckDrawnCard() {
        System.out.println();
        if (penaltyCardCount == 0) {
            if (isUserTurn()) {
                printLoadingScreen(4, "Sie " + formatBold + underlineText + "ziehen eine" + resetConsole + " Karte");
            } else {
                printLoadingScreen(4, "Der Bot " + formatBold + underlineText + "zieht eine" + resetConsole + " Karte");
            }
            drawCards(1);
            if (hasUsableCards()) {
                int cardIndex = playerCardLists.get(getCurrentPlayerIndex()).size() - 1;
                if (usableCardIndices.contains(cardIndex)) {
                    if (isUserTurn()) {
                        String cardColor = "";
                        System.out.println();
                        for (CardColor color : CardColor.values()) {
                            if (playerCardLists.get(getCurrentPlayerIndex()).get(cardIndex).startsWith(color.toString())) {
                                cardColor = color.getLabel();
                            }
                        }
                        String cardType = "";
                        for (CardType typ : CardType.values()) {
                            if (playerCardLists.get(getCurrentPlayerIndex()).get(cardIndex).endsWith(typ.toString())) {
                                cardType = typ.getLabel();
                            }
                        }
                        System.out.println();
                        System.out.println("Möchten Sie die gezogene Karte (" + cardColor + cardType + resetConsole + ") setzen? ja/nein");
                    }
                    String answer = "";
                    while (!answer.equals("ja") && !answer.equals("nein")) {
                        if (isUserTurn()) {
                            answer = scanner.nextLine();
                            if (!answer.equals("ja") && !answer.equals("nein")) {
                                System.out.println("Geben Sie bitte ja oder nein ein.");
                            }
                        } else {
                            // In dem Rumpf wird dem Bot eine Auswahl Logik angegeben, damit der Bot nicht einfach zu besiegen ist.
                            if (playerCardLists.get(getCurrentPlayerIndex()).get(cardIndex).startsWith(CardColor.WISH_COLOR.toString())) {
                                int availableColorsCount = 0; // Zählt die Anzahl der vorhandenen Farben. Z. B. wenn rote und blaue Karten vorhanden sind, dann ist die Anzahl 2.
                                int wishCardsCount = 0;
                                for (String card : playerCardLists.get(getCurrentPlayerIndex())) {
                                    for (CardColor color : CardColor.values()) {
                                        if (card.startsWith(color.toString())) {
                                            if (color == CardColor.WISH_COLOR) {
                                                wishCardsCount++;
                                            } else {
                                                availableColorsCount++;
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (availableColorsCount <= wishCardsCount) {
                                    printLoadingScreen(5, "Der Bot " + formatBold + underlineText + "setzt die gezogene" + resetConsole + " Karte");
                                    answer = "ja";
                                } else {
                                    answer = "nein";
                                }
                            } else {
                                printLoadingScreen(5, "Der Bot " + formatBold + underlineText + "setzt die gezogene" + resetConsole + " Karte");
                                answer = "ja";
                            }
                        }
                    }
                    if (answer.equals("ja")) {
                        useCard(cardIndex);
                        applyCardEffect();
                    }
                }
            }
        } else {
            if (isUserTurn()) {
                printLoadingScreen(5, "Sie " + formatBold + underlineText + "ziehen " + penaltyCardCount + resetConsole + " Karten");
            } else {
                printLoadingScreen(5, "Der Bot " + formatBold + underlineText + "zieht " + penaltyCardCount + resetConsole + " Karten");
            }
            drawCards(penaltyCardCount);
            penaltyCardCount = 0;
        }
    }

    private void applyCardEffect() {
        // Regeln: https://www.meinspiel.de/blog/uno-regeln/?srsltid=AfmBOop1puidm6nouVdqZMB6-YRm6_r7plptOiRcgr2K5xBn139CNDSW
        if (playerCardLists.get(getCurrentPlayerIndex()).isEmpty()) {
            return;
        }
        switch (currentType) {
            case SKIP -> {
                switchPlayer();
                printGameOverview();
                if (isUserTurn()) {
                    printLoadingScreen(4, "Sie " + formatBold + underlineText + "setzen aus" + resetConsole);
                } else {
                    printLoadingScreen(4, "Der Bot " + formatBold + underlineText + "setzt aus" + resetConsole);
                }
            }
            case REVERSE_DIRECTION -> {
                if (getPlayerCount() == 2) {
                    // Aussetzen
                    switchPlayer();
                    printGameOverview();
                    if (isUserTurn()) {
                        printLoadingScreen(4, "Sie " + formatBold + underlineText + "setzen aus" + resetConsole);
                    } else {
                        printLoadingScreen(4, "Der Bot " + formatBold + underlineText + "setzt aus" + resetConsole);
                    }
                } else {
                    directionIsClockwise = !directionIsClockwise; // Richtung wird gewechselt.
                }
            }
            case WISH_CARD_DRAW4, WISH_CARD_NORMAL -> {
                if (isUserTurn()) {
                    System.out.println("Geben Sie die Farbe (Ziffer) an die Sie sich wünschen.");
                    for (int i = 0; i < CardColor.values().length - 1; i++) { // Die Farbe "Wünsche" soll nicht auswählbar sein.
                        System.out.println(CardColor.values()[i].getLabel() + "(" + i + ")" + resetConsole);
                    }
                    int farbenIndex = -1;
                    while (farbenIndex < 0 || farbenIndex >= CardColor.values().length - 1) {
                        try {
                            farbenIndex = Integer.parseInt(scanner.nextLine());
                            if (farbenIndex >= 0 && farbenIndex < CardColor.values().length - 1) {
                                currentColor = CardColor.values()[farbenIndex];
                                break;
                            } else {
                                System.out.println("Geben Sie eine gültige Ziffer an.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Geben Sie eine Ziffer an.");
                        }
                    }
                } else {
                    printLoadingScreen(4, "Der Bot " + formatBold + underlineText + "wünscht" + resetConsole + " eine Farbe");
                    ArrayList<Integer> availableColorCardCounts = new ArrayList<>(); // Je nachdem wie viele Karten einer Farbe vorhanden sind, wird die Farbe ausgewählt.
                    int colorCount = 0;
                    for (CardColor color : CardColor.values()) {
                        for (String card : playerCardLists.get(getCurrentPlayerIndex())) {
                            if (card.startsWith(color.toString())) {
                                colorCount++;
                            }
                        }
                        availableColorCardCounts.add(colorCount);
                        colorCount = 0;
                    }
                    int colorIndex = availableColorCardCounts.indexOf(Collections.max(availableColorCardCounts)); // Die Farbe mit den meisten Karten wird ausgewählt.
                    currentColor = CardColor.values()[colorIndex];
                }
                if (currentType.equals(CardType.WISH_CARD_DRAW4)) {
                    switchPlayer();
                    printGameOverview();
                    penaltyCardCount = penaltyCardCount + 4;
                    drawAndCheckDrawnCard();
                }
            }
            case DRAW2 -> penaltyCardCount = penaltyCardCount + 2;

        }
    }

    private int calculatePoints() {
        // Punkte der einzelnen Karten: https://www.meinspiel.de/app/uploads/2019/11/Punkte-UNO-1.png
        printLoadingScreen(7, "Die Punkte werden ausgerechnet");
        int points = 0;
        for (ArrayList<String> playerCards : playerCardLists) {
            for (String card : playerCards) {
                for (int i = 0; i < 10; i++) {
                    if (card.endsWith(i + "")) {
                        points = points + i;
                    }
                }
                if (card.endsWith(CardType.SKIP.toString()) || card.endsWith(CardType.DRAW2.toString()) || card.endsWith(CardType.REVERSE_DIRECTION.toString())) {
                    points = points + 20;
                }
                if (card.startsWith(CardColor.WISH_COLOR.toString())) {
                    points = points + 50;
                }
            }
        }
        System.out.println("Dem Gewinner werden " + points + " Punkte vergeben.");
        return points;
    }

    private boolean gameWinnerExists() {
        if (getPlayerScores().get(getCurrentPlayerIndex()) >= 500) {
            if (isUserTurn()) {
                System.out.println("Gratuliere, Sie haben das Spiel gewonnen!");
            } else {
                System.out.println("Gratuliere, der Bot " + getCurrentPlayerIndex() + " hat das Spiel gewonnen!");
            }
            return true;
        } else {
            return false;
        }
    }

    public void startNextRound() {
        System.out.println("Klicken Sie auf die Enter-Taste um die nächste Runde zu beginnen.");
        String answer = " ";
        while (!answer.isEmpty()) {
            answer = scanner.nextLine();
        }
        for (ArrayList<String> playerCards : playerCardLists) {
            allCards.addAll(playerCards);
            playerCards.clear();
        }
        allCards.addAll(stack);
        stack.clear();
        Collections.shuffle(allCards);
        for (int i = 0; i < getPlayerCount(); i++) {
            drawCards(7);
            switchPlayer();
        }
        String currentCard = allCards.getFirst();
        while (currentCard.startsWith(CardColor.WISH_COLOR.toString())) {
            Collections.shuffle(allCards);
            currentCard = allCards.getFirst();
        }
        allCards.removeFirst();
        stack.add(currentCard);
        updateCurrentCard();
    }

    private void updateCurrentCard() {
        for (CardColor color : CardColor.values()) {
            if (stack.getLast().startsWith(color.toString())) {
                currentColor = color;
                break;
            }
        }
        for (CardType type : CardType.values()) {
            if (stack.getLast().endsWith(type.toString())) {
                currentType = type;
                break;
            }
        }
    }

    private void useCard(int cardIndex) {
        stack.add(playerCardLists.get(getCurrentPlayerIndex()).get(cardIndex));
        playerCardLists.get(getCurrentPlayerIndex()).remove(cardIndex);
        updateCurrentCard();
    }

    private void drawCards(int amount) {
        for (int i = 0; i < amount; i++) {
            playerCardLists.get(getCurrentPlayerIndex()).add(allCards.getFirst());
            allCards.removeFirst();
            if (allCards.isEmpty()) {
                allCards.addAll(stack);
                stack.clear();
                Collections.shuffle(allCards);
            }
        }
    }

    private boolean isUserTurn() {
        return getCurrentPlayer().equals(USER);
    }


    @Override
    public void switchPlayer() {
        if (directionIsClockwise) {
            if (Integer.parseInt(getCurrentPlayer()) == getPlayerCount()) {
                setCurrentPlayer(USER);
            } else {
                setCurrentPlayer(Integer.parseInt(getCurrentPlayer()) + 1 + "");
            }
        } else {
            if (isUserTurn()) {
                setCurrentPlayer(getPlayerCount() + "");
            } else {
                setCurrentPlayer(Integer.parseInt(getCurrentPlayer()) - 1 + "");
            }
        }
    }
}