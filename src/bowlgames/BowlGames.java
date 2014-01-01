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
    //String name;
    int wins = 0;
    int score = 0; //temp holder for any given outcome
}

public class BowlGames {

//    private static final int KylePicks =  0b010011111111010000;
//    private static final int RyanPicks =  0b000001111100011011;  //read left to right
//    private static final int TylerPicks = 0b000111110011000110;
//    private static final int ColbyPicks = 0b000101110010011011;
//    private static final int DadPicks =   0b010111111011101101;

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

    public static int GamesPlayed(HashMap<String, Boolean> outcomes){
        int gamesPlayed = 0;
        int bowlGames = 18;
        int played = outcomes.size();
        
        for (int i = 0; i < played; i++) {
            gamesPlayed = ((1 << i) | gamesPlayed);
        }
        gamesPlayed = gamesPlayed << (bowlGames-played);
        return gamesPlayed;
    }
    
    public static void main(String[] args) {
//        int gamesPlayed = 0b11111110000000000; //mask of games played
//        int outcomes    = 0b010000000000000000; //1 if spread was covered
//        int gamesPlayed = 0b111111111000000000; //mask of games played
        int gamesPlayed = 0;
//        int outcomes =    0b010101110111011100; //1 if spread was covered
        int outcomes = 0;
        int bowlGames = 0b111111111111111111;
        
//here is where we will start parsing the XML and get the number of players
        //read XML and populate the data objects
        HashMap<String, HashMap<String,BowlPick>> picksMap = BowlXMLParser.buildPickTable("./src/bowlpicks.xml");
        HashMap<String, Boolean> resultsMap = BowlXMLParser.buildResultsTable("./src/results.xml");
        String[] bowls = BowlXMLParser.buildBowlsTable("./src/bowls.xml");

        String[] playerNames = new String[picksMap.size()];
        picksMap.keySet().toArray(playerNames);
        HashMap<String, Player> players = new HashMap();
        
        //calculate gamesPlayed mask
        for (int i = 0; i < resultsMap.size(); i++) {
            gamesPlayed = ((1 << i) | gamesPlayed);
        }
        gamesPlayed = gamesPlayed << (bowls.length - resultsMap.size());
        //done calculating mask
        
        //calculate outcomes
        for (int i = 0; i < bowls.length; i++) {
            if(resultsMap.get(bowls[i]) != null){
                if(resultsMap.get(bowls[i]).booleanValue())
                    outcomes = ((outcomes << 1) | 1);
                else
                    outcomes = (outcomes << 1) ;
            }
        }
        outcomes = outcomes << (bowls.length - resultsMap.size());
        //done calculating outcomes
        
        //create all the players
        for (int i = 0; i < playerNames.length; i++) {
            Player player = new Player();   
            players.put(playerNames[i], player);
        }
        //done creating players
        
        //calculate picks
        for (Entry<String, HashMap<String,BowlPick>> player : picksMap.entrySet()){
            int picks = 0;
            for (int i = 0; i < bowls.length; i++) {
                if(player.getValue().get(bowls[i]) != null){
                    if(player.getValue().get(bowls[i]).cover)
                        picks = ((picks << 1) | 1);
                    else
                        picks = (picks << 1) ;
                }
            }
            players.get(player.getKey()).picks = picks;
        }
        //done calculating picks
        
       // players.get("Kyle").picks = KylePicks;
       // players.get("Ryan").picks = RyanPicks;
       // players.get("Tyler").picks = TylerPicks;
       // players.get("Colby").picks = ColbyPicks;
       // players.get("Dad").picks = DadPicks;

        players.get("Kyle").points = KylePoints;
        players.get("Ryan").points = RyanPoints;
        players.get("Tyler").points = TylerPoints;
        players.get("Colby").points = ColbyPoints;
        players.get("Dad").points = DadPoints;
//done with XML
        for (Entry<String, Player> entry : players.entrySet()){
            entry.getValue().score = 0;
            entry.getValue().wins = 0;
        }
        
        for (int i = 0; i <= (~gamesPlayed & bowlGames); i++) {
            int max = 0;

            for (Entry<String, Player> player : players.entrySet()) {
                int winsMask = calWins((i | (gamesPlayed & outcomes)), player.getValue().picks);
                player.getValue().score = calcTotal(winsMask, player.getValue().points);
            }

            for (Entry<String, Player> player : players.entrySet()) {
                if (player.getValue().score > max) {
                    max = player.getValue().score;
                }
            }

            for (Entry<String, Player> player : players.entrySet()) {
                if (player.getValue().score == max) {
                    player.getValue().wins++;
                }
            }
        }

        for (Entry<String, Player> player : players.entrySet()) {
            System.out.print(player.getKey() + " : ");
            int pos = (~gamesPlayed & bowlGames) + 1;
            System.out.println(player.getValue().wins + " / " + pos);
            System.out.println(String.format("%.2f", 100.0 * player.getValue().wins / pos) + "%");
        }
    }
}
