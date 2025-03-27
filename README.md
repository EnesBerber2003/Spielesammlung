# Spielsammlung
Dieses Projekt ist meine persönliche Lernplattform, um Programmieren mit Java zu erlernen und meine Fähigkeiten kontinuierlich zu erweitern. Das Projekt begann mit einem einfachen „Hallo Welt!“ und hat sich zu einem vielseitigen Spieleprojekt entwickelt. Dieses Projekt bleibt offen für neue Ideen und Verbesserungen.
## Enthaltene Spiele
- **Tic Tac Toe**: In diesem Game treten zwei Spieler gegeneinander an, um als Erster drei Felder in einer Reihe (horizontal, vertikal oder diagonal) zu besetzen. Der erste Spieler, der insgesamt drei Runden gewinnt, bei denen er drei gleiche Zeichen in einer Reihe platziert, gewinnt das Game.
- **Schere-Stein-Papier**: In diesem Game tritt jeder Spieler gegen einen Bot an. Ein Punkt wird vergeben, wenn der Spieler den Bot besiegt. Das Game endet, sobald ein Spieler mindestens 5 Punkte erreicht und dabei einen Vorsprung von mindestens 2 Punkten hat.
- **UNO**: In diesem Game treten Sie gegen 1 bis 9 Bots an. Ziel ist es, durch das Ablegen aller Karten Punkte zu sammeln. Nach jedem Sieg werden die Punkte basierend auf den übrig gebliebenen Karten der Gegner berechnet und dem Gesamtkonto des Siegers hinzugefügt. Der erste Spieler, der 500 Punkte erreicht, gewinnt das Game.
- **Hangman (bald)**
## Game starten
Um ein beliebiges Game zu starten, müssen Sie lediglich die `Main`-Klasse ausführen. In der `Applikation`-Klasse befinden sich Methoden, die es Ihnen ermöglichen, ein Game Ihrer Wahl zu starten.

Jedes Game kann durch Aufrufen der entsprechenden Methode in der Applikation-Klasse gestartet werden:
- Tic Tac Toe: `ticTacToeSpielen()`
- Schere-Stein-Papier: `schereSteinPapierSpielen()`
- UNO: `unoSpielen()`

**Ein Beispiel:**
```java
public class Main {
    public static void main(String[] args) {
        Applikation anwendung = new Applikation();
        anwendung.unoSpielen(); // Startet das Game
    }
}
