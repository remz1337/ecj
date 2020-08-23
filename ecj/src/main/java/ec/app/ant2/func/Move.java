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
 * Move.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class Move extends GPNode implements EvalPrint
    {
    public String toString() { return "m"; }

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
        p.phenotype+="m";
        switch (p.orientation)
            {
            case Ant2.O_UP:
                p.posy--;
                if (p.posy<0) p.posy = p.maxy-1;
                break;
            case Ant2.O_LEFT:
                p.posx--;
                if (p.posx<0) p.posx = p.maxx-1;
                break;
            case Ant2.O_DOWN:
                p.posy++;
                if (p.posy>=p.maxy) p.posy=0;
                break;
            case Ant2.O_RIGHT:
                p.posx++;
                if (p.posx>=p.maxx) p.posx=0;
                break;
            default:  // whoa!
                state.output.fatal("Whoa, somehow I got a bad orientation! (" + p.orientation + ")");
                break;
            }

        p.moves++;
        if (p.map[p.posx][p.posy]== Ant2.FOOD && p.moves < p.maxMoves )
            {
            p.sum++;
            p.map[p.posx][p.posy]= Ant2.ATE;
            }
        }

    /** Just like eval, but it retraces the map and prints out info */
    public void evalPrint(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem,
        final int[][] map2)
        {
        Ant2 p = (Ant2)problem;
        p.phenotype+="m";
        switch (p.orientation)
            {
            case Ant2.O_UP:
                p.posy--;
                if (p.posy<0) p.posy = p.maxy-1;
                break;
            case Ant2.O_LEFT:
                p.posx--;
                if (p.posx<0) p.posx = p.maxx-1;
                break;
            case Ant2.O_DOWN:
                p.posy++;
                if (p.posy>=p.maxy) p.posy=0;
                break;
            case Ant2.O_RIGHT:
                p.posx++;
                if (p.posx>=p.maxx) p.posx=0;
                break;
            default:  // whoa!
                state.output.fatal("Whoa, somehow I got a bad orientation! (" + p.orientation + ")");
                break;
            }

        p.moves++;
        if (p.map[p.posx][p.posy]== Ant2.FOOD && p.moves < p.maxMoves)
            {
            p.sum++;
            p.map[p.posx][p.posy]= Ant2.ATE;
            }

        if (p.moves<p.maxMoves)
            {
            if (++p.pmod > 122 /* ascii z */) p.pmod=97; /* ascii a */
            map2[p.posx][p.posy]=p.pmod;
            }
        }
    }



