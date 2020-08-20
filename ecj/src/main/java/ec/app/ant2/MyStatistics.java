/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.ant2;

import ec.EvolutionState;
import ec.Statistics;
import ec.gp.GPIndividual;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;

import java.io.File;
import java.io.IOException;

public class MyStatistics extends Statistics
    {
    // The parameter string and log number of the file for our best-genome-#3 individual
    public static final String P_INFOFILE = "info-file";
    public int infoLog;

    public void setup(final EvolutionState state, final Parameter base)
        {
            // DO NOT FORGET to call super.setup(...) !!
            super.setup(state,base);

            // set up infoFile
            File infoFile = state.parameters.getFile(
                base.push(P_INFOFILE),null);
            if (infoFile!=null) try
                                    {
                                    infoLog = state.output.addLog(infoFile,true);
                                    }
                catch (IOException i)
                    {
                    state.output.fatal("An IOException occurred while trying to create the log " +
                        infoFile + ":\n" + i);
                    }

        }

    public void postEvaluationStatistics(final EvolutionState state)
        {
            // be certain to call the hook on super!
            super.postEvaluationStatistics(state);

            state.output.println("-----------------------\nGENERATION " +
                    state.generation + "\n-----------------------", infoLog);
            for(int i = 0; i < state.population.subpops.get(0).individuals.size(); i++ ){
                if(state.population.subpops.get(0).individuals.get(i).evaluated){
                    state.output.println("size:" + String.valueOf(state.population.subpops.get(0).individuals.get(i).size()), infoLog);
                    //state.output.println("Geno:"+ ((GPIndividual) state.population.subpops.get(0).individuals.get(i)).genotypeToString(), infoLog);

                    ((Ant2Individual) state.population.subpops.get(0).individuals.get(i)).printTree(state,infoLog);
                    //state.population.subpops.get(0).individuals.get(i).printTesto(state,infoLog);
                }
            }

        }
    }
