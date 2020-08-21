package ec.app.ant2;

import ec.EvolutionState;
import ec.gp.GPIndividual;

public class Ant2Individual extends GPIndividual {

    public String pheno;

    @Override
    public String genotypeToString() {
        return "testy";
    }

    public void printTree(final EvolutionState state, final int log)
    {
        //state.output.println(EVALUATED_PREAMBLE + (evaluated ? "true" : "false"), log);
        //fitness.printFitnessForHumans(state,log);
        trees[0].printTreeForHumans(state,log);
    }

}
