/*
 * Copyright (c) 2006 by National Research Council of Canada.
 *
 * This software is the confidential and proprietary information of
 * the National Research Council of Canada ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into
 * with the National Research Council of Canada.
 *
 * THE NATIONAL RESEARCH COUNCIL OF CANADA MAKES NO REPRESENTATIONS OR
 * WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT.
 * THE NATIONAL RESEARCH COUNCIL OF CANADA SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 *
 */

package ec.app.gep.Ant;

import ec.EvolutionState;
import ec.Individual;
import ec.gep.*;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

import java.io.*;
import java.util.StringTokenizer;

/**
 * @author Bob Orchard
 *
 *  The problem is to find the equation x*x + y 
 *  
 *  Data and variable names are in the data file test1.txt
 */

public class Ant extends GEPProblem implements SimpleProblemForm
{
    public static final String P_FILE = "file";
    public static final String P_MOVES = "moves";

    // map point descriptions
    public static final int ERROR = 0;
    public static final int FOOD = -1;
    public static final int EMPTY = 1;
    public static final int TRAIL = 2;
    public static final int ATE = 3;

    // orientations
    public static final int O_UP = 0;
    public static final int O_LEFT = 1;
    public static final int O_DOWN = 2;
    public static final int O_RIGHT = 3;

    // maximum number of moves
    public int maxMoves;

    // how much food we have
    public int food;

    // our map
    public int map[][];

    // store the positions of food so we can reset our map
    // don't need to be deep-cloned, they're read-only
    public int foodx[];
    public int foody[];

    // map[][]'s bounds
    public int maxx;
    public int maxy;

    // our position
    public int posx;
    public int posy;

    // how many points we've gotten
    public int sum;

    // our orientation
    public int orientation;

    // how many moves we've made
    public int moves;

    // print modulo for doing the abcdefg.... thing at print-time
    public int pmod;

    public String phenotype;

    //public static final double IDEAL_FITNESS_MINIMUM = 0;

   /* public Object clone()
    {
        ProblemAnt2 myobj = (ProblemAnt2) (super.clone());
        myobj.map = new int[map.length][];
        for(int x=0;x<map.length;x++)
            myobj.map[x] = (int[])(map[x].clone());
        return myobj;
    }*/

    public double Lvalues[] = {-1};
    public double Rvalues[] = {-2};
    public double Mvalues[] = {-3};
    public double zvalues[] = {0};

    public double[] getDataValues( String label )
    {
        if (label.equals("L"))
            return (Lvalues);
        else if (label.equals("R"))
            return (Rvalues);
        else if (label.equals("M"))
            return (Mvalues);
        else if (label.equals("dependentVariable")) // always called 'dependentVariable'
            return (zvalues);
        else
            return null;
    }

    public void setup(final EvolutionState state,
                      final Parameter base)
    {
        // very important, remember this
        super.setup(state,base);

        // No need to verify the GPData object

        // not using any default base -- it's not safe

        // how many maxMoves?
        maxMoves = state.parameters.getInt(base.push(P_MOVES),null,1);
        if (maxMoves==0)
            state.output.error("The number of moves an ant has to make must be >0");

        // load our file
        File filename = state.parameters.getFile(base.push(P_FILE),null);
        if (filename==null)
            state.output.fatal("Ant trail file name not provided.");
        /*InputStream str = state.parameters.getResource(base.push(P_FILE), null);
        if (str == null)
            state.output.fatal("Error loading file or resource", base.push(P_FILE), null);*/

        food = 0;
        LineNumberReader lnr = null;
        try
        {
            lnr =
                    new LineNumberReader(new FileReader(filename));
                    //new LineNumberReader(new InputStreamReader(str));

            StringTokenizer st = new StringTokenizer(lnr.readLine()); // ugh
            maxx = Integer.parseInt(st.nextToken());
            maxy = Integer.parseInt(st.nextToken());
            map = new int[maxx][maxy];
            int y;
            for(y=0;y<maxy;y++)
            {
                String s = lnr.readLine();
                if (s==null)
                {
                    state.output.warning("Ant trail file ended prematurely");
                    break;
                }
                int x;
                for(x=0;x<s.length();x++)
                {
                    if (s.charAt(x)==' ')
                        map[x][y]=EMPTY;
                    else if (s.charAt(x)=='#')
                    { map[x][y]=FOOD; food++; }
                    else if (s.charAt(x)=='.')
                        map[x][y]=TRAIL;
                    else state.output.error("Bad character '" + s.charAt(x) + "' on line number " + lnr.getLineNumber() + " of the Ant trail file.");
                }
                // fill out rest of X's
                for(int z=x;z<maxx;z++)
                    map[z][y]=EMPTY;
            }
            // fill out rest of Y's
            for (int z=y;z<maxy;z++)
                for(int x=0;x<maxx;x++)
                    map[x][z]=EMPTY;
        }
        catch (NumberFormatException e)
        {
            state.output.fatal("The Ant trail file does not begin with x and y integer values.");
        }
        catch (IOException e)
        {
            state.output.fatal("The Ant trail file could not be read due to an IOException:\n" + e);
        }
        finally
        {
            try { if (lnr != null) lnr.close(); } catch (IOException e) { }
        }
        state.output.exitIfErrors();

        // load foodx and foody reset arrays
        foodx = new int[food];
        foody = new int[food];
        int tmpf = 0;
        for(int x=0;x<map.length;x++)
            for(int y=0;y<map[0].length;y++)
                if (map[x][y]==FOOD)
                { foodx[tmpf] = x; foody[tmpf] = y; tmpf++; }
    }

    private void reset(){
        sum = 0;
        posx = 0;
        posy = 0;
        orientation = O_RIGHT;
        phenotype = "";
        moves=0;
    }

    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum)
    {
        if (!ind.evaluated)  // don't bother reevaluating
        {
            /*sum = 0;
            posx = 0;
            posy = 0;
            orientation = O_RIGHT;
            phenotype = "";*/
            reset();


            // sensitivity/specificity fitness is normalized between 0 and 1000  (1000 * raw SS)
            //double fitness = GEPFitnessFunction.SSfitness(true, (GEPIndividual)ind);
            double fitness = GEPFitnessFunction.AntFitness((GEPIndividual)ind,this);

            // the fitness better be SimpleFitness!
            SimpleFitness f = ((SimpleFitness)ind.fitness);
            f.setFitness(state,(float)fitness, fitness >= food);
            ind.evaluated = true;
            ((GEPIndividual) ind).phenotype=phenotype;

            if (fitness >= food)
            {
                ((GEPIndividual)ind).printIndividualForHumans(state, 1, 1);
            }

            // clean up array
            for (int y = 0; y < food; y++)
                map[foodx[y]][foody[y]] = FOOD;
        }

    }


        /*static double IDEAL_FITNESS_MINIMUM = 999.9999;

    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum)
    {
        if (!ind.evaluated)  // don't bother reevaluating
        {
            // Mean Squared Error (MSE) fitness is normalized between 0 and 1000 (1000 * (1/(1+MSE))
            double fitness = GEPFitnessFunction.MSEfitness(true, (GEPIndividual)ind);

            // the fitness better be SimpleFitness!
            SimpleFitness f = ((SimpleFitness)ind.fitness);
            f.setFitness(state,(float)fitness, fitness >= IDEAL_FITNESS_MINIMUM);
            ind.evaluated = true;

            if (fitness >= IDEAL_FITNESS_MINIMUM)
            {
                ((GEPIndividual)ind).printIndividualForHumans(state, 1, 1);
            }
        }
    }*/

//
//    /** Evaluate the CGP and compute fitness. */
//    public void evaluate(EvolutionState state, Individual ind,
//                         int subpopulation, int threadnum) {
//        if (!ind.evaluated) {
//            sum = 0;
//            posx = 0;
//            posy = 0;
//            orientation = O_RIGHT;
//            phenotype = "";
//
//            GEPSpecies s = (GEPSpecies) ind.species;
//            GEPIndividual ind2 = (GEPIndividual) ind;
//
//            float diff = 0f;
//
//            String[] inputs = new String[3];
//
//            inputs[0] = "L"; // Left
//            inputs[1] = "R"; // Right
//            inputs[2] = "M"; // Move
//
//            for (moves = 0; moves < maxMoves && sum < food; ) {
//                Object[] outputs = GEPEvaluator.evaluate(state, threadnum, inputs, ind2, this);
//                //((VectorIndividualCGP)ind).eval(state,threadnum,input,stack,((VectorIndividualCGP)ind),this);
//
//            }
//            diff += Math.abs(food - sum);
//
//            ((FitnessCGP) ind.fitness).setFitness(state, diff, diff == 0); // stop if error is less than 1%.
//
//            // the fitness better be KozaFitness!
//				/*KozaFitness f = ((KozaFitness)ind.fitness);
//				f.setStandardizedFitness(state,(food - sum));
//				f.hits = sum;*/
//            ind.evaluated = true;
//            ind.phenotype = phenotype;
//
//            // clean up array
//            for (int y = 0; y < food; y++)
//                map[foodx[y]][foody[y]] = FOOD;
//        }
//    }
//
//    public void describe(
//            final EvolutionState state,
//            final Individual ind,
//            final int subpopulation,
//            final int threadnum,
//            final int log)
//    {
//        state.output.println("\n\nBest Individual's Map\n=====================", log);
//
//        sum = 0;
//        pmod = 97; /** ascii a */
//        posx = 0;
//        posy = 0;
//        orientation = O_RIGHT;
//        phenotype = "";
//
//        VectorIndividualCGP ind2 = (VectorIndividualCGP) ind;
//
//        int[][] map2 = new int[map.length][];
//        for(int x=0;x<map.length;x++)
//            map2[x] = (int[])(map[x].clone());
//
//        map2[posx][posy] = pmod; pmod++;
//
//        String[] inputs = new String[3];
//
//        inputs[0] = "L"; // Left
//        inputs[1] = "R"; // Right
//        inputs[2] = "M"; // Move
//
//        for(moves=0; moves<maxMoves && sum<food; ){
//            Object[] outputs = Evaluator.evaluate(state, threadnum, inputs, ind2, this);
//        }
//
//        // print out the map
//        for(int y=0;y<map2.length;y++)
//        {
//            for(int x=0;x<map2.length;x++)
//            {
//                switch(map2[x][y])
//                {
//                    case FOOD:
//                        state.output.print("#",log);
//                        break;
//                    case EMPTY:
//                        state.output.print(".",log);
//                        break;
//                    case TRAIL:
//                        state.output.print("+",log);
//                        break;
//                    case ATE:
//                        state.output.print("?",log);
//                        break;
//                    default:
//                        state.output.print(""+((char)map2[x][y]),log);
//                        break;
//                }
//            }
//            state.output.println("",log);
//        }
//    }
}
