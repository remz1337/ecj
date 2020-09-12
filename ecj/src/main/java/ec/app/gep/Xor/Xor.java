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

package ec.app.gep.Xor;

import ec.*;
import ec.gep.*;
import ec.simple.*;
import ec.util.*;

/**
 * @author Bob Orchard
 *
 */

public class Xor extends GEPProblem implements SimpleProblemForm 
{
	// Attempt to discover the XOR function using and, or, not
	//
	// Best solution is:   not(p and q) and (p or q)
	
	public static final double IDEAL_FITNESS_MINIMUM = 999.99999999;
			
    public void setup(final EvolutionState state,
            final Parameter base)
    {
        super.setup(state, base);    
    }

	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) 
	{
        if (!ind.evaluated)  // don't bother reevaluating
        {
            // sensitivity/specificity fitness is normalized between 0 and 1000  (1000 * raw SS)
            double fitness = GEPFitnessFunction.SSfitness(true, (GEPIndividual)ind);
                        
            // the fitness better be SimpleFitness!
            SimpleFitness f = ((SimpleFitness)ind.fitness);
            f.setFitness(state,(float)fitness, fitness >= IDEAL_FITNESS_MINIMUM);
            ind.evaluated = true;
           
		    if (fitness >= IDEAL_FITNESS_MINIMUM)
		    {	
			    ((GEPIndividual)ind).printIndividualForHumans(state, 1, 1);
		    }	
        }
        
	}

}
