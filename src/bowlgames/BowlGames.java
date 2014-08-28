/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bowlgames;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author Ryan
 */
class Player {
    int picks;
    int[] points;
    int wins = 0;
    int score = 0; //temp holder for any given outcome
}

public class BowlGames {

    private static final int[] tieMask   = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    
    private static int calcTotal(int wins, int[] bets) {
        int total = 0;

        for (int i = 0; i < bets.length; i++) {
            int mask = ((1 << i) & wins);
            if (mask != 0 && tieMask[i] == 0) {
                total += bets[i];
            }
        }
        return total;
    }

    private static int calWins(int outcomes, int predictions) {
        return ~(outcomes ^ predictions);
    }

    private int calcGamesPlayed(HashMap<String, Boolean> outcomes) {
        int played = 0; 
        String[] games = BowlXMLParser.buildBowlsTable("./src/bowls.xml");
        Boolean outcome;
        for (int i = 0; i < games.length; i++) {
            outcome = outcomes.get(games[i]);

        }
        return 0;
    }

    public static void main(String[] args) {
//        int gamesPlayed = 0b11111110000000000; //mask of games played
//        int outcomes    = 0b010000000000000000; //1 if spread was covered
        int gamesPlayed = 0b111111111110000000; //mask of games played
        int outcomes =    0b010101110001011100; //1 if spread was covered
        int bowlGames = 0b111111111111111111;

//here is where we will start parsing the XML and get the number of players
        Player[] players = new Player[5];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        //read XML and populate the data objects
        players[Kyle].name = "Kyle";
        players[Ryan].name = "Ryan";
        players[Tyler].name = "Tyler";
        players[Colby].name = "Colby";
        players[Dad].name = "Dad";

        players[Kyle].picks = KylePicks;
        players[Ryan].picks = RyanPicks;
        players[Tyler].picks = TylerPicks;
        players[Colby].picks = ColbyPicks;
        players[Dad].picks = DadPicks;

        players[Kyle].points = KylePoints;
        players[Ryan].points = RyanPoints;
        players[Tyler].points = TylerPoints;
        players[Colby].points = ColbyPoints;
        players[Dad].points = DadPoints;
//done with XML

        for (Player p : players) {
            p.score = 0;
            p.wins = 0;
        }
        for (int i = 0; i <= (~gamesPlayed & bowlGames); i++) {
            int max = 0;

            for (Player p : players) {
                int winsMask = calWins((i | (gamesPlayed & outcomes)), p.picks);
                p.score = calcTotal(winsMask, p.points);
            }

            for (Player p : players) {
                if (p.score > max) {
                    max = p.score;
                }
            }

            for (Player p : players) {
                if (p.score == max) {
                    p.wins++;
                }
            }
        }

        for (Player p : players) {
            System.out.print(p.name + " : ");
            int pos = (~gamesPlayed & bowlGames) + 1;
            System.out.println(p.wins + " / " + pos);
            System.out.println(String.format("%.2f", 100.0 * p.wins / pos) + "%");
        }
    }
}
