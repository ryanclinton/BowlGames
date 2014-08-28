/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bowlgames;

/**
 *
 * @author Ryan
 */
public class BowlPick {
  
    int weight;
    boolean cover;

    @Override
    public String toString() {
        return ("Weight:" + weight + " :: Cover:" + cover);
    }  
}
