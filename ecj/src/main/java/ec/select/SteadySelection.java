/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.select;

import ec.EvolutionState;
import ec.Individual;
import ec.SelectionMethod;
import ec.util.Parameter;
import ec.util.RandomChoice;

/* 
 * FitProportionateSelection.java
 * 
 * Created: Thu Feb 10 16:31:24 2000
 * By: Sean Luke
 */

/**
 * Picks individuals in a population in direct proportion to their
 * fitnesses as returned by their fitness() methods.  This is expensive to
 * set up and bring down, so it's not appropriate for steady-state evolution.
 * If you're not familiar with the relative advantages of 
 * selection methods and just want a good one,
 * use TournamentSelection instead.   Not appropriate for
 * multiobjective fitnesses.
 *
 * <p><b><font color=red>
 * Note: Fitnesses must be non-negative.  0 is assumed to be the worst fitness.
 * </font></b>

 <p><b>Typical Number of Individuals Produced Per <tt>produce(...)</tt> call</b><br>
 Always 1.

 <p><b>Default Base</b><br>
 select.fitness-proportionate

 *
 * @author Sean Luke
 * @version 1.0 
 */

public class SteadySelection extends SelectionMethod
    {
    /** Default base */
    public static final String P_STEADYSELECTION = "steadystate";
    /** Normalized, totalized fitnesses for the population */
    //public float[] fitnesses;
    int pos;

        @Override
        public void setup(EvolutionState state, Parameter base) {
            super.setup(state, base);
            pos=0;
        }

        public Parameter defaultBase()
        {
        return SelectDefaults.base().push(P_STEADYSELECTION);
        }

    // don't need clone etc. 

    public void prepareToProduce(final EvolutionState s,
        final int subpopulation,
        final int thread)
        {

        }

    public int produce(final int subpopulation,
        final EvolutionState state,
        final int thread)
        {
            if(pos>=state.population.subpops[subpopulation].individuals.length){
                state.output.fatal("no more individual in subpopulation(offending subpopulation #" + subpopulation + ")");
            }
        // Pick and return an individual from the population
            int curpos=pos;
            pos++;
        return curpos;
        }
    
    public void finishProducing(final EvolutionState s,
        final int subpopulation,
        final int thread)
        {
        // release the distributions so we can quickly 
        // garbage-collect them if necessary
        //pos++;
        }
    }
