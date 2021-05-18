package ec.cgp.functions;

//import ec.util.MersenneTwisterFast;

//import com.sun.org.apache.xpath.internal.operations.Bool;
import ec.Problem;
import ec.cgp.Argument;
import ec.cgp.problems.ProblemAnt2;

import java.util.ArrayList;

public class FunctionsAnt2 implements Functions {

	/** turn left */
	//static int F_LEFT = 1;
	/** turn right */
	//static int F_RIGHT = 2;
	/** move forward. */
	//static int F_MOVE = 3;
	/** If food ahead */
	static int F_IFA = 0;
	/** prog N2 */
	static int F_PN2 = 1;
	/** prog N3 */
	//static int F_PN3 = 5;
	/** prog N4 */
	//static int F_PN4 = 6;

	/** Interpret the given function and apply it to the given inputs. */
	//first input is context of problem
	public Object callFunction(Object[] inputs, int function, int numFunctions, final Problem prob) {

		Argument[] arg = (Argument[]) inputs;

		/*String[] arg = new String[inputs.length];
		for (int i = 0; i < inputs.length; i++){
			if (!(inputs[i] instanceof String))
				arg[i] = "";
			else
				arg[i] = (String) inputs[i];
		}*/

		//Debug inputs
		//String i1 = arg[0];

		ProblemAnt2 p = (ProblemAnt2)prob;

		String moves="";

		if (function == F_IFA) {
			//return "F_IFA";
			switch (p.orientation)
			{
				case ProblemAnt2.O_UP:
					if (p.map[p.posx][(p.posy-1+p.maxy)%p.maxy]== ProblemAnt2.FOOD) {
						if(arg[0].isLeaf){
							ExecuteAction((String)arg[0].input,p);
						}
						moves+=arg[0].input;
					}
					else {
						if(arg[1].isLeaf){
							ExecuteAction((String)arg[1].input,p);
						}
						moves+=arg[1].input;
					}
					break;
				case ProblemAnt2.O_LEFT:
					if (p.map[(p.posx-1+p.maxx)%p.maxx][p.posy]== ProblemAnt2.FOOD) {
						if(arg[0].isLeaf){
							ExecuteAction((String)arg[0].input,p);
						}
						moves+=arg[0].input;
					}
					else {
						if(arg[1].isLeaf){
							ExecuteAction((String)arg[1].input,p);
						}
						moves+=arg[1].input;
					}
					break;
				case ProblemAnt2.O_DOWN:
					if (p.map[p.posx][(p.posy+1)%p.maxy]== ProblemAnt2.FOOD) {
						if(arg[0].isLeaf){
							ExecuteAction((String)arg[0].input,p);
						}
						moves+=arg[0].input;
					}
					else {
						if(arg[1].isLeaf){
							ExecuteAction((String)arg[1].input,p);
						}
						moves+=arg[1].input;
					}
					break;
				case ProblemAnt2.O_RIGHT:
					if (p.map[(p.posx+1)%p.maxx][p.posy]== ProblemAnt2.FOOD) {
						if(arg[0].isLeaf){
							ExecuteAction((String)arg[0].input,p);
						}
						moves+=arg[0].input;
					}
					else {
						if(arg[1].isLeaf){
							ExecuteAction((String)arg[1].input,p);
						}
						moves+=arg[1].input;
					}
					break;
				default:  // whoa!
					//state.output.fatal("Whoa, somehow I got a bad orientation! (" + p.orientation + ")");
					throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
			}
			//p.moves++;
			/*
		} else if (function == F_LEFT) {
			//return "F_LEFT";
			p.phenotype+="l";
			switch (p.orientation)
			{
				case ProblemAnt2.O_UP:
					p.orientation = ProblemAnt2.O_LEFT;
					break;
				case ProblemAnt2.O_LEFT:
					p.orientation = ProblemAnt2.O_DOWN;
					break;
				case ProblemAnt2.O_DOWN:
					p.orientation = ProblemAnt2.O_RIGHT;
					break;
				case ProblemAnt2.O_RIGHT:
					p.orientation = ProblemAnt2.O_UP;
					break;
				default:  // whoa!
					throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
			}
			p.moves++;
		} else if (function == F_RIGHT) {
			//return "F_RIGHT";
			p.phenotype+="r";
			switch (p.orientation)
			{
				case ProblemAnt2.O_UP:
					p.orientation = ProblemAnt2.O_RIGHT;
					break;
				case ProblemAnt2.O_LEFT:
					p.orientation = ProblemAnt2.O_UP;
					break;
				case ProblemAnt2.O_DOWN:
					p.orientation = ProblemAnt2.O_LEFT;
					break;
				case ProblemAnt2.O_RIGHT:
					p.orientation = ProblemAnt2.O_DOWN;
					break;
				default:  // whoa!
					throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
			}
			p.moves++;
		} else if (function == F_MOVE) {
			//return "F_MOVE";
			p.phenotype+="m";
			switch (p.orientation)
			{
				case ProblemAnt2.O_UP:
					p.posy--;
					if (p.posy<0) p.posy = p.maxy-1;
					break;
				case ProblemAnt2.O_LEFT:
					p.posx--;
					if (p.posx<0) p.posx = p.maxx-1;
					break;
				case ProblemAnt2.O_DOWN:
					p.posy++;
					if (p.posy>=p.maxy) p.posy=0;
					break;
				case ProblemAnt2.O_RIGHT:
					p.posx++;
					if (p.posx>=p.maxx) p.posx=0;
					break;
				default:  // whoa!
					throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
			}

			p.moves++;
			if (p.map[p.posx][p.posy]== ProblemAnt2.FOOD && p.moves < p.maxMoves )
			{
				p.sum++;
				p.map[p.posx][p.posy]= ProblemAnt2.ATE;
			}*/
		} else if (function == F_PN2) {
			//return "F_PN2";
			if(arg[0].isLeaf){
				ExecuteAction((String)arg[0].input,p);
			}
			moves+=arg[0].input;

			if(arg[1].isLeaf){
				ExecuteAction((String)arg[1].input,p);
			}
			moves+=arg[1].input;
			//p.moves++;
			//p.moves++;
		} else
			throw new IllegalArgumentException("Function #" + function + " is unknown.");

		return moves;
	}

	/** Parse and execute actions */
	public static void ExecuteAction(String action, final Problem prob)
	{
		ProblemAnt2 p = (ProblemAnt2)prob;
		if(p.moves < p.maxMoves){
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
	public static void TurnLeft(final Problem prob)
	{
		ProblemAnt2 p = (ProblemAnt2)prob;
		//p.phenotype+="l";
		switch (p.orientation)
		{
			case ProblemAnt2.O_UP:
				p.orientation = ProblemAnt2.O_LEFT;
				break;
			case ProblemAnt2.O_LEFT:
				p.orientation = ProblemAnt2.O_DOWN;
				break;
			case ProblemAnt2.O_DOWN:
				p.orientation = ProblemAnt2.O_RIGHT;
				break;
			case ProblemAnt2.O_RIGHT:
				p.orientation = ProblemAnt2.O_UP;
				break;
			default:  // whoa!
				throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
		}
		p.moves++;
	}

	/** Apply turn right action */
	public static void TurnRight(final Problem prob)
	{
		ProblemAnt2 p = (ProblemAnt2)prob;
		//p.phenotype+="r";
		switch (p.orientation)
		{
			case ProblemAnt2.O_UP:
				p.orientation = ProblemAnt2.O_RIGHT;
				break;
			case ProblemAnt2.O_LEFT:
				p.orientation = ProblemAnt2.O_UP;
				break;
			case ProblemAnt2.O_DOWN:
				p.orientation = ProblemAnt2.O_LEFT;
				break;
			case ProblemAnt2.O_RIGHT:
				p.orientation = ProblemAnt2.O_DOWN;
				break;
			default:  // whoa!
				throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
		}
		p.moves++;
	}

	/** Apply move forward action */
	public static void MoveForward(final Problem prob)
	{
		ProblemAnt2 p = (ProblemAnt2)prob;
		//p.phenotype+="m";
		switch (p.orientation)
		{
			case ProblemAnt2.O_UP:
				p.posy--;
				if (p.posy<0) p.posy = p.maxy-1;
				break;
			case ProblemAnt2.O_LEFT:
				p.posx--;
				if (p.posx<0) p.posx = p.maxx-1;
				break;
			case ProblemAnt2.O_DOWN:
				p.posy++;
				if (p.posy>=p.maxy) p.posy=0;
				break;
			case ProblemAnt2.O_RIGHT:
				p.posx++;
				if (p.posx>=p.maxx) p.posx=0;
				break;
			default:  // whoa!
				throw new IllegalArgumentException("Orientation " + p.orientation + " is unknown.");
		}

		p.moves++;
		if (p.map[p.posx][p.posy]== ProblemAnt2.FOOD && p.moves < p.maxMoves )
		{
			p.sum++;
			p.map[p.posx][p.posy]= ProblemAnt2.ATE;
			p.updatePhenotype();
		}
	}

	/**
	 * Interpret the given float as a boolean value. Any value > 0 is
	 * interpreted as "true".
	 */
	/*public static boolean f2b(float inp) {
		return inp > 0 ? true : false;
	}*/

	/** Convert the given boolean to float. "True" is 1.0; "false" is -1.0. */
	/*public static float b2f(boolean inp) {
		return inp ? 1f : -1f;
	}*/

	/**
	 * Return a function name, suitable for display in expressions, for the
	 * given function.
	 */
	public String functionName(int fn) {
		if (fn == F_IFA)
			return "IfFoodAhead";
		/*if (fn == F_LEFT)
			return "Left";
		if (fn == F_RIGHT)
			return "Right";
		if (fn == F_MOVE)
			return "Move";*/
		if (fn == F_PN2)
			return "ProgN2";
		/*if (fn == F_PN3)
			return "ProgN3";
		if (fn == F_PN4)
			return "ProgN4";*/
		else
			return "UNKNOWN FUNCTION";
	}

	/** Return the arity of the given function */
	public int arityOf(int fn) {
		if (fn == F_IFA)
			return 2;
		/*if (fn == F_LEFT)
			return 0;
		if (fn == F_RIGHT)
			return 0;
		if (fn == F_MOVE)
			return 0;*/
		if (fn == F_PN2)
			return 2;
		/*if (fn == F_PN3)
			return 3;
		if (fn == F_PN4)
			return 4;*/
		else
			return -1;
	}

	/** Return the name, suitable for display, for the given input. */ 
	public String inputName(int inp, Object val) {
		if (inp == 0)
			return "trail";
		return ""+val; // a constant value
	}

	/** Simple test of the function set. */
	/*public static void testFunctions() {
		FunctionsAnt2 f = new FunctionsAnt2();

		Float[] inputs;
		MersenneTwisterFast rand = new MersenneTwisterFast();

		for (int i = 0; i < 100; i++) {
			inputs = new Float[] { 2f * (.5f - rand.nextFloat()),
					2f * (.5f - rand.nextFloat()) };
			for (int j = 0; j < 15; j++) {
				System.out.println(inputs[0] + " " + f.functionName(j) + " "
						+ inputs[1] + " = " + f.callFunction(inputs, j, 15));

			}
		}
	}

	public static void main(String[] args) {
		testFunctions();
	}*/
}
