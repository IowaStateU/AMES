/* ============================================================================
 * AMES Wholesale Power Market Test Bed (Java): A Free Open-Source Test-Bed
 *         for the Agent-based Modeling of Electricity Systems
 * ============================================================================
 *
 * (C) Copyright 2008, by Hongyan Li, Junjie Sun, and Leigh Tesfatsion
 *
 *    Homepage: http://www.econ.iastate.edu/tesfatsi/AMESMarketHome.htm
 *
 * LICENSING TERMS
 * The AMES Market Package is licensed by the copyright holders (Junjie Sun,
 * Hongyan Li, and Leigh Tesfatsion) as free open-source software under the
 * terms of the GNU General Public License (GPL). Anyone who is interested is
 * allowed to view, modify, and/or improve upon the code used to produce this
 * package, but any software generated using all or part of this code must be
 * released as free open-source software in turn. The GNU GPL can be viewed in
 * its entirety as in the following site: http://www.gnu.org/licenses/gpl.html
 */
// NDGenAgent.java
// Non Dispatchable Generator Agent
package amesmarket;

import fncs.JNIfncs;
import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import java.sql.*;

/**
 * Example showing what NDGenData[i] contains NDGenData //ID	atBus	block	LP
 * block	LP block	NDG	block	NDG 1	1	6	22	6	30	6	34	6	27
 *
 * // block: NDGen block for the next how many hours // NDG: NDGen profile
 * (fixed demand)
 */
public class NDGenAgent {

    private static final int ID = 0;
    private static final int AT_NODE = 1;
    private static final int BLOCK1 = 2;
    private static final int NDG1 = 3;
    private static final int BLOCK2 = 4;
    private static final int NDG2 = 5;
    private static final int BLOCK3 = 6;
    private static final int NDG3 = 7;
    private static final int BLOCK4 = 8;
    private static final int NDG4 = 9;

    private static final int HOURS_PER_DAY = 24;

    // LSE's data
    private int x;      // Coordinate x on trans grid
    private int y;      // Coordinate y on trans grid
    private double power;
    private double money;

    private int id;
    private int atBus;
    private double[] NDGProfile;
    private double[] NDGForecast;
    private double[] committedDispatch;

    private double[] dispatch;    // Real-Time hourly power dispatch quantity
    private double[] dayAheadLMP; // Day-Ahead hourly locational marginal price
    private double[] realTimeLMP; // Real-Time hourly locational marginal price

    // Constructor
    public NDGenAgent(double[] NDGData) {

        x = -1;
        y = -1;
        power = 0;
        money = 0;

        // Parse lseData
        id = (int) NDGData[ID];
        atBus = (int) NDGData[AT_NODE];
        NDGProfile = new double[HOURS_PER_DAY];
        NDGForecast = new double[HOURS_PER_DAY];

        committedDispatch = new double[HOURS_PER_DAY];

        for (int h = 2; h < NDGData.length; h++) {
            NDGProfile[h - 2] = NDGData[h];
            //System.out.println("h="+(h-2)+"\tNDGProfile="+NDGProfile[h-2]);
        }
    }
//method

    public double[] submitDAMForecast(int day, int lse, boolean IsFNCS) {

        double[] temp = new double[24];
        // Receives NDG forecast from fncs_player
        //System.out.println("In submitDAMforecast:");
        if (day > 1) { // previously day > 2
            if (IsFNCS) {
                String[] events = JNIfncs.get_events();
                //System.out.println("DAM events.len: " + events.length);
                for (int i = 0; i < events.length; ++i) {
                    //String value = JNIfncs.get_value(events[i]);
                    String[] values = JNIfncs.get_values(events[i]);

                    for (int j = 0; j < 24; j++) {
                        if (events[i].equals("ndgenforecastDAM_h" + String.valueOf(j))) {
                            System.out.println("receiving DAM NDGforecast: " + values[0]);
                            //System.out.println("i:"+i);
                            temp[j] = Double.parseDouble(values[0]);
                        }
                        //System.out.println("temp - NDGforecast: " + temp[j]);
                    }
                }
                System.arraycopy(temp, 0, NDGForecast, 0, 24); //ToDo- Double.parseDouble(rs.getString("LSE"+Integer.toString(psLse)));
                
                //Temp assignment 
                NDGForecast = NDGProfile;
                
                return NDGForecast;
            } else {
                return NDGProfile;
            }
        } else {
//            System.out.println("NDGProfile:");
//            for(int i=0; i<NDGProfile.length;i++)
//                System.out.print(": "+NDGProfile[i]);
//            System.out.println("");
            return NDGProfile;
        }
    }

    public double[] submitCommittedDispatch() {

        for (int i = 0; i < 24; i++) {
            committedDispatch[i] = NDGProfile[i];
            //System.out.println("Print"+NDGProfile[i]);
        }
        return committedDispatch;

    }

    // NDGen's get and set methods
    public void setXY(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public int getID() {
        return id;
    }

    public int getAtNode() {
        return atBus;
    }

    public double[] getNDGProfile() {
        return NDGProfile;
    }

    public double getPower() {
        return power;
    }

    public void report() {
        System.out.println(getID() + "at" + x + "," + y + "demands" + getPower()
                + "MWhs of power.");
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setDayAheadLMP(double[] lmprice) {
        dayAheadLMP = lmprice;
    }

}
