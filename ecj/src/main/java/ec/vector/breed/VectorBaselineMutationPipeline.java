/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.vector.breed;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.cgp.representation.VectorIndividualCGP;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.VectorDefaults;

import java.util.ArrayList;
import java.util.HashMap;

/* 
 * VectorMutationPipeline.java
 * 
 * Created: Tue Mar 13 15:03:12 EST 2001
 * By: Sean Luke
 */


/**
 *
 VectorMutationPipeline is a BreedingPipeline which implements a simple default Mutation
 for VectorIndividuals.  Normally it takes an individual and returns a mutated 
 child individual. VectorMutationPipeline works by calling defaultMutate(...) on the 
 parent individual.
 
 <p><b>Typical Number of Individuals Produced Per <tt>produce(...)</tt> call</b><br>
 (however many its source produces)

 <p><b>Number of Sources</b><br>
 1

 <p><b>Default Base</b><br>
 vector.mutate (not that it matters)

 * @author Sean Luke
 * @version 1.0
 */

public class VectorBaselineMutationPipeline extends BreedingPipeline
    {
    private static final long serialVersionUID = 1;

    public static final String P_BASELINEMUTATION = "baselinemutation";
    public static final int NUM_SOURCES = 1;

        private Boolean keepBest=false;

    public Parameter defaultBase() { return VectorDefaults.base().push(P_BASELINEMUTATION); }
    
    /** Returns 1 */
    public int numSources() { return NUM_SOURCES; }


        public void setup(final EvolutionState state, final Parameter base)
        {
            super.setup(state,base);
            //Parameter p= new Parameter("gep.baselinemutation.keepbest");
            Parameter p= new Parameter("vre.baselinemutation.keepbest");

            //String temp=state.parameters.getStringWithDefault(p,null,"hello");
            //String temp2=state.parameters.getStringWithDefault(base.push(P_KEEPBEST),null,"hello");
            keepBest = state.parameters.getBoolean(p,null,false);
        }

    public int produce(final int min,
        final int max,
        final int subpopulation,
        final ArrayList<Individual> inds,
        final EvolutionState state,
        final int thread, HashMap<String, Object> misc)
        {
            int start = inds.size();
            /*

        
        // grab individuals from our source and stick 'em right into inds.
        // we'll modify them from there
        int n = sources[0].produce(min,max,subpopulation,inds, state,thread, misc);

        // should we use them straight?
        if (!state.random[thread].nextBoolean(likelihood))
            {
            return n;
            }

        // else mutate 'em
        for(int q=start;q<n+start;q++)
            {
            ((VectorIndividual)inds.get(q)).defaultMutate(state,thread);
            ((VectorIndividual)inds.get(q)).evaluated=false;
            }

        return n;*/

            // grab individuals from our source and stick 'em right into inds.

            // we'll modify them from there -- for gep we force all of the population to
            // be dealt with at once so min is set to max .. should be the entire population
            // (excluding the elite individual(s) that are passed on unaltered)
            //int n = sources[0].produce(max,max,start,subpopulation,inds,state,thread, misc);
            int n = sources[0].produce(min,max,subpopulation,inds, state,thread, misc);


            //MersenneTwisterFast srt = state.random[thread];
            //VectorSpeciesCGP s = (VectorSpeciesCGP) inds.get(start).species;

            for(int q=start;q<n+start;q++)
            {

                try {
                    VectorIndividualCGP oldind = (VectorIndividualCGP)inds.get(q).clone();
                    VectorIndividualCGP ind = (VectorIndividualCGP)inds.get(q); // the genome (chromosome) to mutate

                    //((VectorIndividualCGP)inds.get(q)).baselineMutate(state,thread);
                    //((VectorIndividualCGP)inds.get(q)).evaluated=false;
                    //ind.baselineMutate(state,thread);
                    ind.defaultMutate(state,thread);
                    ind.evaluated=false;

                    if(keepBest){
                        // EVALUATION
                        SimpleProblemForm p = (SimpleProblemForm)(state.evaluator.p_problem.clone());
                        p.evaluate(state,ind,subpopulation,thread);

                        //Need to check other Fitness class implementation
                        if(oldind.fitness.betterThan(ind.fitness)){
                            //state.output.message("Old ind is better");
                            //ind=oldind;
                            inds.set(q,oldind);
                        }/*else{
                            state.output.message("New ind is better");
                        }*/
                    }
                    //state.output.message("Test");

                } catch (Exception e) { e.printStackTrace(); }

            }

            //if(s.baselineMutationProbability>0){


            //}

            return n;
        }

    }
    
    
