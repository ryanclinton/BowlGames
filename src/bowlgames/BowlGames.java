/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bowlgames;

import java.util.HashMap;

/**
 * @author Ryan
 */
class Player {

    int picks;
    int[] points;
    String name;
    int wins = 0;
    int score = 0; //temp holder for any given outcome
}

public class BowlGames {

    private static final int Kyle = 0;
    private static final int Ryan = 1;
    private static final int Tyler = 2;
    private static final int Colby = 3;
    private static final int Dad = 4;

    private static final int KylePicks =  0b010011111111010000;
    private static final int RyanPicks =  0b000001111100011011;  //read left to right
    private static final int TylerPicks = 0b000111110011000110;
    private static final int ColbyPicks = 0b000101110010011011;
    private static final int DadPicks =   0b010111111011101101;

    private static final int[] KylePoints = {18, 12, 9, 10, 16, 15, 11, 14, 8, 13, 1, 17, 5, 2, 6, 3, 7, 4};
    private static final int[] RyanPoints = {16, 12, 2, 15, 18, 6, 8, 13, 1, 14, 5, 17, 9, 11, 4, 7, 10, 3}; //read right to left
    private static final int[] TylerPoints = {15, 8, 4, 14, 7, 16, 6, 1, 17, 3, 10, 5, 9, 18, 11, 12, 2, 13};
    private static final int[] ColbyPoints = {2, 17, 1, 3, 11, 18, 12, 10, 4, 13, 8, 14, 9, 7, 6, 16, 5, 15};
    private static final int[] DadPoints = {7, 12, 4, 13, 11, 3, 2, 6, 1, 14, 17, 15, 18, 5, 8, 16, 10, 9};

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
        int gamesPlayed = 0b111111111000000000; //mask of games played
        int outcomes =    0b010101111111011100; //1 if spread was covered
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
