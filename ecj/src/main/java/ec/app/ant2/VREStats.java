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

public class VREStats extends Statistics
    {
    // The parameter string and log number of the file for our CSV export
    public static final String P_VRECSV = "VRE-CSV";
    public int vreLog;

    private boolean csvHeaderWritten =false;

    public void setup(final EvolutionState state, final Parameter base)
        {
            // DO NOT FORGET to call super.setup(...) !!
            super.setup(state,base);

            // set up infoFile
            File infoFile = state.parameters.getFile(
                base.push(P_VRECSV),null);
            if (infoFile!=null) try
                                    {
                                        vreLog = state.output.addLog(infoFile,true);
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

            // Header
            if(!csvHeaderWritten){
                state.output.println("Genotype,Fitness,Phenotype", vreLog);
                csvHeaderWritten=true;
            }

            // Population
            //state.output.println("-----------------------\nGENERATION " + state.generation + "\n-----------------------", vreLog);
            for(int i = 0; i < state.population.subpops.get(0).individuals.size(); i++ ){
                if(state.population.subpops.get(0).individuals.get(i).evaluated){
                    state.output.print("\"", vreLog);
                    ((GPIndividual) state.population.subpops.get(0).individuals.get(i)).trees[0].child.printRootedTree(state,vreLog,0);
                    state.output.print("\"," + ((GPIndividual) state.population.subpops.get(0).individuals.get(i)).fitness.fitness(), vreLog);
                    state.output.println(",\"" + ((GPIndividual) state.population.subpops.get(0).individuals.get(i)).phenotype+"\"", vreLog);
                }
            }

        }
    }
