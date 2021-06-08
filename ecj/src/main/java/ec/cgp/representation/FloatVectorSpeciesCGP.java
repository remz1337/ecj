package ec.cgp.representation;


import ec.EvolutionState;
import ec.util.Parameter;

/**
 * Float-based genome representation of a Cartesian Genetic Program. Gene values
 * are restricted to floats in the range [0,1]. During program evaluation, each
 * float value is scaled to integers in the acceptable range that is imposed by
 * the gene's position.
 *
 * @author David Oranchak, doranchak@gmail.com, http://oranchak.com
 *
 */
public class FloatVectorSpeciesCGP extends VectorSpeciesCGP {

	public final static String P_MUTATIONTYPE = "mutation-type";

	public final static String V_RESET_MUTATION = "reset";
	public final static String V_BASELINE_MUTATION = "baseline";
	protected boolean isBaselineMutation=false;

	/** Added setup step that automatically sets max-gene to 1.0. */
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
		state.parameters.set(new Parameter("pop.subpop.0.species.max-gene"), "1"); 		
	}
	
}
