package ec.cgp.representation;

import ec.EvolutionState;
import ec.util.Parameter;

/**
 * Integer-based genome representation of a Cartesian Genetic Program. Each
 * integer value is restricted to a range that is a function of its position in
 * the genome.
 * 
 * @author David Oranchak, doranchak@gmail.com, http://oranchak.com
 *
 */
public class IntegerVectorSpeciesCGP extends VectorSpeciesCGP {

	public final static String P_MUTATIONTYPE = "mutation-type";

	public final static String V_RESET_MUTATION = "reset";
	public final static String V_BASELINE_MUTATION = "baseline";
	protected boolean isBaselineMutation=false;

	public void setup(EvolutionState state, Parameter base) {

		Parameter def = defaultBase();
		String mtype = state.parameters.getStringWithDefault(base.push(P_MUTATIONTYPE), def.push(P_MUTATIONTYPE), null);
		if (mtype == null)
			state.output.warning("No global mutation type given for CharVectorSpecies, assuming 'reset' mutation", base.push(P_MUTATIONTYPE), def.push(P_MUTATIONTYPE));
		else if (mtype.equalsIgnoreCase(V_BASELINE_MUTATION))
			isBaselineMutation=true;
		else
			state.output.fatal("CharVectorSpecies given a bad mutation type: " + mtype, base.push(P_MUTATIONTYPE), def.push(P_MUTATIONTYPE));

		super.setup(state, base);
		state.parameters.set(new Parameter("pop.subpop.0.species.max-gene"), "10000000"); 
			/* arbitrarily large.  but computeMaxGene will usually limit gene to contain much smaller values. */		
	}

}
