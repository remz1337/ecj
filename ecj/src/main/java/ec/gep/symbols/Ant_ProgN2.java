package ec.gep.symbols;

import ec.app.gep.Ant.Ant;
import ec.gep.GEPFunctionSymbol;
import ec.gep.GEPProblem;

import static java.lang.Math.abs;

public class Ant_ProgN2 extends GEPFunctionSymbol {

	public Ant_ProgN2()
	{
		super("P", 2);
	}

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

		Ant p = (Ant)prob[0];

		if (p1<0) {
			ExecuteAction(p1s, p);
		}

		if (p2<0) {
			ExecuteAction(p2s, p);
		}

		return abs(p1)+abs(p2);
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
