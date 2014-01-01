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
        int gamesPlayed = 0;
        int outcomes = 0;
        int bowlGames = 0b111111111111111111;
        
//here is where we will start parsing the XML and get the number of players
        //read XML and populate the data objects
        HashMap<String, HashMap<String,BowlPick>> picksMap = BowlXMLParser.buildPickTable("./src/bowlpicks.xml");
        HashMap<String, Boolean> resultsMap = BowlXMLParser.buildResultsTable("./src/results.xml");
        String[] bowls = BowlXMLParser.buildBowlsTable("./src/bowls.xml");
//done with XML

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
        for (Entry<String, HashMap<String, BowlPick>> player : picksMap.entrySet()){
            int points[] = new int[bowls.length];
            for(int i=0; i<bowls.length; i++)
                points[i] = player.getValue().get(bowls[bowls.length - 1 - i]).weight;
            players.get(player.getKey()).points = points;
        }
        //done calculating picks

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