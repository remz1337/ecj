/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.gep;

import ec.EvolutionState;
import ec.Statistics;
//import ec.gp.GPIndividual;
import ec.Individual;
import ec.app.gep.Ant.Ant;
import ec.gp.GPIndividual;
import ec.util.Output;
import ec.util.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VREStats extends Statistics
    {
    // The parameter string and log number of the file for our CSV export
    public static final String P_VRECSV = "VRE-CSV";
    public int vreLog;

    /** compress? */
    public static final String P_COMPRESS = "gzip";

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
                                        //vreLog = state.output.addLog(infoFile,true);
                                        vreLog = state.output.addLog(infoFile, Output.V_NO_GENERAL-1,false,
                                                !state.parameters.getBoolean(base.push(P_COMPRESS),null,false),
                                                state.parameters.getBoolean(base.push(P_COMPRESS),null,false));
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
                //state.output.println("Genotype,Fitness,Phenotype", vreLog);
                state.output.println("Genotype,Fitness,Phenotype", Output.V_NO_GENERAL,vreLog);
                csvHeaderWritten=true;
            }

            // Population
            //state.output.println("-----------------------\nGENERATION " + state.generation + "\n-----------------------", vreLog);
            for(int i = 0; i < state.population.subpops[0].individuals.length; i++ ){
                if(state.population.subpops[0].individuals[i].evaluated){
                    state.output.print("\"", Output.V_NO_GENERAL,vreLog);
                    //best_of_run[i].genotypeToStringForHumans()
                    state.output.print(state.population.subpops[0].individuals[i].genotypeToStringForHumans(),Output.V_NO_GENERAL, vreLog);
                    state.output.print("\"," + state.population.subpops[0].individuals[i].fitness.fitness(),Output.V_NO_GENERAL, vreLog);
                    state.output.println(",\"" + ((GEPIndividual)state.population.subpops[0].individuals[i]).phenotype+"\"",Output.V_NO_GENERAL, vreLog);
                    /*for( Float output : (ArrayList<Float>)state.population.subpops.get(0).individuals.get(i).phenotype){
                        state.output.print("," + output, vreLog);
                    }
                    state.output.println("", vreLog);*/
                }
            }

        }
    }
