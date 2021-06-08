package ec.vector;

import ec.EvolutionState;
import ec.vector.VectorIndividual;


/**
 * Base class for integer- and float-based CGP individuals.
 * 
 * @author David Oranchak, doranchak@gmail.com, http://oranchak.com
 * 
 */
public abstract class BaselineVectorIndividual extends VectorIndividual {

	/** Temporary storage for displaying the full program */
	public StringBuffer expression;

	/** Return the genome. */
	public abstract Object getGenome();

	public abstract void baselineMutate(EvolutionState state, int thread);

}
