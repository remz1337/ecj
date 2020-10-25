package ec.gep.symbols;

import ec.gep.GEPFunctionSymbol;import ec.gep.GEPProblem;
import ec.gep.GEPProblem;

/**
 * The logical nor of 2 parameters.
 */

public class Nor extends GEPFunctionSymbol {

	/**
	 * The logical nor of 2 parameters.
	 */
	public Nor() 
	{
		super("nor", 2);
	}

	/**
	 * Evaluate the logical nor of 2 parameters.
	 * 
	 * @param params double array with the required parameter(s) -- 0 is treated as false, other values as true
	 * @return !(params[0] || params[1])
	 */
	public double eval(double params[], GEPProblem... prob)
	{
		//should check that there are 2 params
		boolean p1 = params[0] == 0.0 ? false : true;
		boolean p2 = params[1] == 0.0 ? false : true;
		return (((p1 || p2) == true) ? 0.0 : 1.0);
	}
	
	/**
	 * Logical nor is a logical function.
	 * @return true
	 */
	public boolean isLogicalFunction()
	{
		return true;
	}

	/**
	 * The human readable form of the expression
	 */
	public String getMathExpressionAsString( String p[] )
	{
		return "(" + p[0] + " nor " + p[1] + ")";
	}
}
