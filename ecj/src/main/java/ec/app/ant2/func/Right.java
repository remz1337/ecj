/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.ant2.func;

import ec.EvolutionState;
import ec.Problem;
import ec.app.ant2.Ant2;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/* 
 * Right.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */


public class Right extends GPNode implements EvalPrint
    {
    public String toString() { return "r"; }

    /*
      public void checkConstraints(final EvolutionState state,
      final int tree,
      final GPIndividual typicalIndividual,
      final Parameter individualBase)
      {
      super.checkConstraints(state,tree,typicalIndividual,individualBase);
      if (children.length!=0)
      state.output.error("Incorrect number of children for node " + 
      toStringForError() + " at " +
      individualBase);
      }
    */
    public int expectedChildren() { return 0; }

    public void eval(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem)
        {
        Ant2 p = (Ant2)problem;
        switch (p.orientation)
            {
            case Ant2.O_UP:
                p.orientation = Ant2.O_RIGHT;
                break;
            case Ant2.O_LEFT:
                p.orientation = Ant2.O_UP;
                break;
            case Ant2.O_DOWN:
                p.orientation = Ant2.O_LEFT;
                break;
            case Ant2.O_RIGHT:
                p.orientation = Ant2.O_DOWN;
                break;
            default:  // whoa!
                state.output.fatal("Whoa, somehow I got a bad orientation! (" + p.orientation + ")");
                break;
            }
        p.moves++;
        }

    public void evalPrint(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem,
        final int[][] map2)
        {
        eval(state,thread,input,stack,individual,problem);
        }
    }



