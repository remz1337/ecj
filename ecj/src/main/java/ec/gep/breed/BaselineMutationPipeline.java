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

package ec.gep.breed;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.gep.*;
import ec.simple.SimpleProblemForm;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

/* 
 * MutationPipeline.java
 * 
 * Created: Nov 2006
 * By: Bob Orchard
 */

/**
 * MutationPipeline implements ...
 *
 

 * @author Bob Orchard
 * @version 1.0 
 */

public class BaselineMutationPipeline extends GEPBreedingPipeline
{
    public static final int NUM_SOURCES = 1;
    public static final String P_BASLINEMUTATION_PIPE = "baselinemutation";
    //public static final String P_KEEPBEST = "keepbest";

    private Boolean keepBest=false;

    public Parameter defaultBase() { return GEPDefaults.base().push(P_BASLINEMUTATION_PIPE);/* */ }

    public int numSources() { return NUM_SOURCES; }

    public Object clone()
    {
        BaselineMutationPipeline c = (BaselineMutationPipeline)(super.clone());
        
        // deep-cloned stuff
        return c;
    }

    public void setup(final EvolutionState state, final Parameter base)
    {
        super.setup(state,base);
        //Parameter p= new Parameter("gep.baselinemutation.keepbest");
        Parameter p= new Parameter("gep.baselinemutation.keepbest");

        //String temp=state.parameters.getStringWithDefault(p,null,"hello");
        //String temp2=state.parameters.getStringWithDefault(base.push(P_KEEPBEST),null,"hello");
        keepBest = state.parameters.getBoolean(p,null,false);
    }


    public int produce(final int min, 
                       final int max, 
                       final int start,
                       final int subpopulation,
                       final Individual[] inds,
                       final EvolutionState state,
                       final int thread) 
    {
	    // grab individuals from our source and stick 'em right into inds.

        // we'll modify them from there -- for gep we force all of the population to
    	// be dealt with at once so min is set to max .. should be the entire population
    	// (excluding the elite individual(s) that are passed on unaltered)
        int n = sources[0].produce(max,max,start,subpopulation,inds,state,thread);

        // clone the individuals if necessary
        if (!(sources[0] instanceof BreedingPipeline))
            for(int q=start;q<n+start;q++)
                inds[q] = (Individual)(inds[q].clone());

        MersenneTwisterFast srt = state.random[thread];
        GEPSpecies s = (GEPSpecies) inds[start].species;

        if(s.baselineMutationProbability>0){
            // Implement standard 1 point mutation
            for (int g=0;g<n;g++){
                try {
                    GEPIndividual oldind = (GEPIndividual)inds[g].clone();
                    GEPIndividual ind = (GEPIndividual)inds[g]; // the genome (chromosome) to mutate
                    int numChromosomes = ind.chromosomes.length;
                    // do this for each chromosome in the individual
                    int chrom_it= srt.nextInt(numChromosomes);

                    //for (int i=0; i<numChromosomes; i++)
                    //{
                    GEPChromosome chromosome = ind.chromosomes[chrom_it];
                    int genome[][] = chromosome.genome;
                    // choose a gene in the genome
                    int gene[] = genome[srt.nextInt(s.numberOfGenes)];
                    // and the position within the gene
                    int genePos = srt.nextInt(gene.length);
                    // now set the new point to a random terminal or function
                    gene[genePos] = s.symbolSet.chooseFunctionOrTerminalSymbol(state, thread, genePos, s);
                    chromosome.parsedGeneExpressions = null;
                    //}
                    ind.evaluated = false;
                    ind.chromosomesParsed = false;

                    if(keepBest){
                        // EVALUATION
                        SimpleProblemForm p = (SimpleProblemForm)(state.evaluator.p_problem.clone());
                        p.evaluate(state,ind,subpopulation,thread);

                        //Need to check other Fitness class implementation
                        if(oldind.fitness.betterThan(ind.fitness)){
                            //state.output.message("Old ind is better");
                            //ind=oldind;
                            inds[g]=oldind;
                        }/*else{
                            state.output.message("New ind is better");
                        }*/
                    }
                    //state.output.message("Test");

                } catch (Exception e) { e.printStackTrace(); }
            }
        }

        return n;
     }
    
}
