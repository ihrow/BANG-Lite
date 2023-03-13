package sk.stuba.fei.uim.oop.bang;

import sk.stuba.fei.uim.oop.cards.Card;
import sk.stuba.fei.uim.oop.deck.Deck;
import sk.stuba.fei.uim.oop.player.Player;
import sk.stuba.fei.uim.oop.utility.KeyboardInput;
import static sk.stuba.fei.uim.oop.utility.Colors.*;

import java.util.ArrayList;
import java.util.Random;


public class Game {
    private final Player[] players;
    private int currentPlayer;
    private Deck deck;

    public Game() {
        this.deck = new Deck();
        this.deck.shuffle();

        System.out.println(ANSI_RED_B + "\uD83C\uDF35 Bang! Bang! Bang! \uD83D\uDCA5" + ANSI_RESET);
        int numberOfPlayers = 0;
        while (numberOfPlayers < 2 || numberOfPlayers > 4) {
            numberOfPlayers = KeyboardInput.readInt(ANSI_GREEN + "\uD83E\uDD20 Enter amount of players (2-4)" + ANSI_RESET);
        }
        this.players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            this.players[i] = new Player(KeyboardInput.readString("Enter " + (i + 1) + " player name"));
        }

        // Each player starts with 4 cards from the deck
        for (Player player : players) {
            for (int i = 0; i < 4; i++) {
                player.addCard(this.deck.draw());
            }
        }

        this.startGame();
    }

    private int getNumberOfActivePlayers() {
        int numberOfActivePlayers = 0;
        for (Player player : this.players) {
            if (player.isAlive()) {
                numberOfActivePlayers++;
            }
        }
        return numberOfActivePlayers;
    }

    private void startGame() {
        System.out.println(ANSI_YELLOW_BI + "\n\uD83D\uDC02 Yeehaw! It's time to start a game of Bang!" + ANSI_RESET);
        Random random = new Random();

        while (this.getNumberOfActivePlayers() > 1) {
            Player activePlayer = this.players[this.currentPlayer];
            if (!activePlayer.isAlive()) {
                ArrayList<Card> cards = activePlayer.removeAllCards();
                for (Card card : cards) {
                    this.deck.addCard(card);
                }
                this.currentPlayer = (this.currentPlayer + 1) % this.players.length;
                continue;
            }
            this.announceTurn(activePlayer, random);
            this.makeTurn(activePlayer);
            break;
        }
    }

    private void announceTurn(Player activePlayer, Random rnd) {
        // only the following strings are generated by AI
        String[] TURN_VARIATIONS = {
                "The gunslinger's showdown is about to begin, and [player] is up next in the action!",
                "Hold on to your hats, folks! [player] is about to make their move in this wild west adventure.",
                "The dust is settling, and it looks like [player] is next in line to take their turn.",
                "Looks like it's time for [player] to reveal their true colors and show their hand.",
                "The tension is rising, and all eyes are on [player] as they plan their next move in this western standoff.",
                "The outlaws are on the prowl, and [player] is next in their sights. Will they make it out alive?",
                "The sheriff's badge is shining bright, and [player] is ready to uphold the law in this dusty town.",
                "The saloon doors swing open, and [player] steps up to take their turn in this epic western adventure.",
                "The sound of gunfire echoes in the distance, and it's [player]'s turn to join the fray and take on the competition.",
                "The sun is setting on this western town, and [player] is ready to make their mark in the history books."
        };
        int index = rnd.nextInt(TURN_VARIATIONS.length);
        String turnAnnouncement = TURN_VARIATIONS[index].replace("[player]", ANSI_RED_B + activePlayer.getName() + ANSI_PURPLE);
        System.out.println(ANSI_PURPLE + "\n\uD83D\uDCE2 " + turnAnnouncement + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\uD83E\uDDE1 " + activePlayer.getName() + " has " + ANSI_RED + activePlayer.getHealth() + " lives." + ANSI_RESET);
    }

    private void makeTurn(Player activePlayer) {
        /*
            Drawing cards - at the beginning of his turn, the given player draws 2 cards from the deck.
            If he has blue cards (Prison, Dynamite) in front of him, their effect is excecuted as first.
         */
        for (Card card : activePlayer.getBlueCards()) {
            // card.effect(activePlayer, this.players, this.deck);
        }
        for (int i = 0; i < 2; i++) {
            activePlayer.addCard(this.deck.draw());
        }
        activePlayer.displayCards();

        int choice = -1;
        while (true) {
            choice = this.selectCard(activePlayer);
            if (choice == 0 || activePlayer.getAllCards().size() == 0) {
                break;
            }
        }

    }

    private int selectCard(Player activePlayer) {
        int choice = KeyboardInput.readInt(ANSI_GREEN + "\uD83E\uDD20 Enter card number to play or 0 to end turn" + ANSI_RESET);
        if (choice == 0) {
            return choice;
        } else if (choice > activePlayer.getAllCards().size()) {
            return -1;
        }
        Card card = activePlayer.getCard(choice - 1);
        activePlayer.removeCard(card);
        this.deck.addCard(card);
        return choice;
    }
}
