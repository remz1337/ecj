package ec.gep.symbols;

import ec.Problem;
import ec.app.gep.Ant.Ant;
import ec.gep.GEPFunctionSymbol;import ec.gep.GEPProblem;
import ec.gep.GEPFunctionSymbolAnt;
import ec.gep.GEPProblem;

import static java.lang.Math.abs;

/**
 * If (x == y) then x else y
 */

public class Ant_IfFoodAhead extends GEPFunctionSymbol {

	/**
	 * If (x == y) then x else y
	 */
	public Ant_IfFoodAhead()
	{
		super("I", 2);
	}

	/**
	 * Evaluate Ifeq2 with the 2 parameters x,y such that
	 * If (x == y) then x else y.
	 * 
	 * @param params double array with the required parameter(s)
	 * @return Ifeq2(params[0], params[1])
	 */
	public double eval(double params[], GEPProblem... prob)
	{
		//should check that there are 2 params
		int p1 = (int)params[0];
		int p2 = (int)params[1];

		String p1s="";
		String p2s="";

		switch (p1){
			case -1:
				p1s="L";
				break;
			case -2:
				p1s="R";
				break;
			case -3:
				p1s="M";
				break;
			default:
				p1s="Unknown";
		}

		switch (p2){
			case -1:
				p2s="L";
				break;
			case -2:
				p2s="R";
				break;
			case -3:
				p2s="M";
				break;
			default:
				p2s="Unknown";
		}

		double ret = 0;

		Ant p = (Ant)prob[0];
		/*p.phenotype+="WOW";
		switch (p.orientation){

		}*/

		//return "F_IFA";
		switch (p.orientation) {
			case Ant.O_UP:
				if (p.map[p.posx][(p.posy - 1 + p.maxy) % p.maxy] == Ant.FOOD) {
					//if (arg[0].isLeaf) {
					if (p1<0) {
						ExecuteAction(p1s, p);
					}
					//moves += arg[0].input;
					ret=abs(p1);
				} else {
					//if (arg[1].isLeaf) {
					if (p2<0) {
						ExecuteAction(p2s, p);
					}
					//moves += arg[1].input;
					ret=abs(p2);
				}
				break;
			case Ant.O_LEFT:
				if (p.map[(p.posx - 1 + p.maxx) % p.maxx][p.posy] == Ant.FOOD) {
					//if (arg[0].isLeaf) {
					if (p1<0) {
						ExecuteAction(p1s, p);
					}
					//moves += arg[0].input;
					ret=abs(p1);
				} else {
					//if (arg[1].isLeaf) {
					if (p2<0) {
						ExecuteAction(p2s, p);
					}
					//moves += arg[1].input;
					ret=abs(p2);
				}
				break;
			case Ant.O_DOWN:
				if (p.map[p.posx][(p.posy + 1) % p.maxy] == Ant.FOOD) {
					//if (arg[0].isLeaf) {
					if (p1<0) {
						ExecuteAction(p1s, p);
					}
					//moves += arg[0].input;
					ret=abs(p1);
				} else {
					//if (arg[1].isLeaf) {
					if (p2<0) {
						ExecuteAction(p2s, p);
					}
					//moves += arg[1].input;
					ret=abs(p2);
				}
				break;
			case Ant.O_RIGHT:
				if (p.map[(p.posx + 1) % p.maxx][p.posy] == Ant.FOOD) {
					//if (arg[0].isLeaf) {
					if (p1<0) {
						ExecuteAction(p1s, p);
					}
					//moves += arg[0].input;
					ret=abs(p1);
				} else {
					//if (arg[1].isLeaf) {
					if (p2<0) {
						ExecuteAction(p2s, p);
					}
					//moves += arg[1].input;
					ret=abs(p2);
				}
				break;
			default:  // whoa!
				//state.output.fatal("Whoa, somehow I got a bad orientation! (" + p.orientation + ")");
				throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
		}

		return ret;
		//return (p1 == p2) ? p1 : p2;
	}


	/** Parse and execute actions */
	public static void ExecuteAction(String action, final GEPProblem prob)
	{
		Ant p = (Ant)prob;
		if(p.moves<p.maxMoves){
			//ProblemAnt2 p = (ProblemAnt2)prob;
			switch (action) {
				case "L":
					TurnLeft(prob);
					break;
				case "R":
					TurnRight(prob);
					break;
				case "M":
					MoveForward(prob);
					break;
			}
		}
	}

	/** Apply turn left action */
	public static void TurnLeft(final GEPProblem prob)
	{
		Ant p = (Ant)prob;
		p.phenotype+="l";
		switch (p.orientation)
		{
			case Ant.O_UP:
				p.orientation = Ant.O_LEFT;
				break;
			case Ant.O_LEFT:
				p.orientation = Ant.O_DOWN;
				break;
			case Ant.O_DOWN:
				p.orientation = Ant.O_RIGHT;
				break;
			case Ant.O_RIGHT:
				p.orientation = Ant.O_UP;
				break;
			default:  // whoa!
				throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
		}
		p.moves++;
	}

	/** Apply turn right action */
	public static void TurnRight(final GEPProblem prob)
	{
		Ant p = (Ant)prob;
		p.phenotype+="r";
		switch (p.orientation)
		{
			case Ant.O_UP:
				p.orientation = Ant.O_RIGHT;
				break;
			case Ant.O_LEFT:
				p.orientation = Ant.O_UP;
				break;
			case Ant.O_DOWN:
				p.orientation = Ant.O_LEFT;
				break;
			case Ant.O_RIGHT:
				p.orientation = Ant.O_DOWN;
				break;
			default:  // whoa!
				throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
		}
		p.moves++;
	}

	/** Apply move forward action */
	public static void MoveForward(final GEPProblem prob)
	{
		Ant p = (Ant)prob;
		p.phenotype+="m";
		switch (p.orientation)
		{
			case Ant.O_UP:
				p.posy--;
				if (p.posy<0) p.posy = p.maxy-1;
				break;
			case Ant.O_LEFT:
				p.posx--;
				if (p.posx<0) p.posx = p.maxx-1;
				break;
			case Ant.O_DOWN:
				p.posy++;
				if (p.posy>=p.maxy) p.posy=0;
				break;
			case Ant.O_RIGHT:
				p.posx++;
				if (p.posx>=p.maxx) p.posx=0;
				break;
			default:  // whoa!
				throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
		}

		p.moves++;
		if (p.map[p.posx][p.posy]== Ant.FOOD && p.moves < p.maxMoves )
		{
			p.sum++;
			p.map[p.posx][p.posy]= Ant.ATE;
		}
	}
	
	/**
	 * Ifeq2 is not a logical function.
	 * @return false
	 */
	public boolean isLogicalFunction()
	{
		return false;
	}

	/**
	 * The human readable form of the expression
	 */
	public String getMathExpressionAsString( String p[] )
	{
		return "ifa(" + p[0] + ", " + p[1] + ")";
	}
}
