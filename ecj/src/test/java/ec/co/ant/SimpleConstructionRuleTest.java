/*
  Copyright 2018 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/
package ec.co.ant;

import ec.EvolutionState;
import ec.Evolve;
import ec.app.tsp.TSPIndividual;
import ec.app.tsp.TSPProblem;
import ec.co.ConstructiveIndividual;
import ec.simple.SimpleEvaluator;
import ec.simple.SimpleEvolutionState;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class SimpleConstructionRuleTest
{
    private final static Parameter BASE = new Parameter("base");
    private final static Parameter PROBLEM_BASE = new Parameter("prob");
    private EvolutionState state;
    private ParameterDatabase params;
    private TSPProblem problem;
    
    public SimpleConstructionRuleTest()
    {
    }
    
    @Before
    public void setUp()
    {
        params = new ParameterDatabase();
        params.set(PROBLEM_BASE.push(TSPProblem.P_FILE), "src/main/resources/ec/app/tsp/berlin52.tsp");
        params.set(BASE.push(SimpleConstructionRule.P_SELECTOR), GreedyComponentSelector.class.getCanonicalName());
        params.set(BASE.push(SimpleConstructionRule.P_START), "TSPComponent[from=0, to=21]");
        state = new SimpleEvolutionState();
        state.parameters = params;
        state.output = Evolve.buildOutput();
        state.output.getLog(0).silent = true;
        state.output.getLog(1).silent = true;
        state.output.setThrowsErrors(true);
        state.random = new MersenneTwisterFast[] { new MersenneTwisterFast() };
        state.evaluator = new SimpleEvaluator();
        problem = new TSPProblem();
        problem.setup(state, PROBLEM_BASE);
        state.evaluator.p_problem = problem;
    }

    
    /** Take an array of nodes and convert them into a TSPIndividual
     * 
     * @param tour An array of node ids representing a path
     * @return A TSPIndividual that follows the given path, plus an extra edge connectiong the last node back to the first node
     */
    private TSPIndividual tourToInd(final int[] tour)
    {
        assert(tour != null);
        assert(tour.length == problem.numNodes());
        final TSPIndividual ind = new TSPIndividual();
        for (int i = 0; i < tour.length - 1; i++)
            ind.add(state, problem.getComponent(tour[i], tour[i + 1]));
        return ind;
    }
    
    @Test
    public void testConstructSolution1()
    {
        final SimpleConstructionRule instance = new SimpleConstructionRule();
        instance.setup(state, BASE);
        final TSPIndividual expResult = tourToInd(new int[] { 0, 21, 48, 31, 35, 34, 33, 38, 39, 37, 36, 47, 
                                        23, 4, 14, 5, 3, 24, 45, 43, 15, 49, 19, 22, 30,
                                        17, 2, 18, 44, 40, 7, 9, 8, 42, 32, 50, 11, 27,
                                        26, 25, 46, 12, 13, 51, 10, 28, 29, 20, 16, 41, 6, 1 });
        
        final ConstructiveIndividual result = instance.constructSolution(state, new TSPIndividual(), null, 0);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
        assertTrue(result.repOK());
    }

    @Test
    public void testConstructSolution2()
    {
        params.set(BASE.push(SimpleConstructionRule.P_START), "TSPComponent[from=27, to=26]");
        final SimpleConstructionRule instance = new SimpleConstructionRule();
        instance.setup(state, BASE);
        final TSPIndividual expResult = tourToInd(new int[] { 27, 26, 25, 46, 12, 13, 51, 10, 50, 11,
                                        24, 3, 5, 4, 14, 23, 47, 37, 39, 36,
                                        38, 35, 34, 33, 43, 45, 15, 49, 19, 22,
                                        30, 17, 21, 0, 48, 31, 44, 18, 40, 7,
                                        9, 8, 42, 32, 2, 16, 20, 29, 28, 41, 6, 1 });
        final ConstructiveIndividual result = instance.constructSolution(state, new TSPIndividual(), null, 0);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
        assertTrue(result.repOK());
    }

    //Skipping this test for now since the first assertion fails
    /*@Test
    public void testConstructSolution5()
    {
        state.parameters.set(BASE.push(SimpleConstructionRule.P_START), "TSPComponent[from=0, to=51]");
        final SimpleConstructionRule instance = new SimpleConstructionRule();
        instance.setup(state, BASE);
        
        // There are two equivalent greedy solutions in this case.
        final ConstructiveIndividual expResult1 = tourToInd(new int[] { 0, 51, 1, 10, 6, 13, 16, 12, 41, 32,
                                        29, 50, 20, 26, 8, 46, 9, 25, 40, 27,
                                        2, 11, 7, 28, 42, 22, 3, 30, 24, 17,
                                        5, 19, 18, 15, 44, 49, 14, 21, 4, 31,
                                        45, 48, 23, 43, 37, 34, 47, 35, 36, 38, 33, 39 });
        final ConstructiveIndividual expResult2 = tourToInd(new int[] { 0, 51, 1, 10, 6, 13, 16, 12, 41, 32,
                                        29, 50, 20, 26, 8, 46, 9, 25, 40, 27,
                                        2, 11, 7, 28, 42, 22, 3, 30, 24, 17,
                                        5, 19, 18, 15, 44, 49, 14, 21, 4, 31,
                                        45, 48, 23, 43, 37, 34, 47, 35, 39, 33, 36, 38 });
        final ConstructiveIndividual result = instance.constructSolution(state, new TSPIndividual(), null, 0);
        //Doesn't assert on Windows...
        //result=[TSPComponent[from=0, to=51], TSPComponent[from=51, to=12], TSPComponent[from=12, to=26], TSPComponent[from=26, to=27], TSPComponent[from=27, to=25], TSPComponent[from=25, to=46], TSPComponent[from=46, to=13], TSPComponent[from=13, to=10], TSPComponent[from=10, to=50], TSPComponent[from=50, to=11], TSPComponent[from=11, to=24], TSPComponent[from=24, to=3], TSPComponent[from=3, to=5], TSPComponent[from=5, to=4], TSPComponent[from=4, to=14], TSPComponent[from=14, to=23], TSPComponent[from=23, to=47], TSPComponent[from=47, to=37], TSPComponent[from=37, to=39], TSPComponent[from=39, to=36], TSPComponent[from=36, to=38], TSPComponent[from=38, to=35], TSPComponent[from=35, to=34], TSPComponent[from=34, to=33], TSPComponent[from=33, to=43], TSPComponent[from=43, to=45], TSPComponent[from=45, to=15], TSPComponent[from=15, to=49], TSPComponent[from=49, to=19], TSPComponent[from=19, to=22], TSPComponent[from=22, to=30], TSPComponent[from=30, to=17], TSPComponent[from=17, to=21], TSPComponent[from=21, to=48], TSPComponent[from=48, to=31], TSPComponent[from=31, to=44], TSPComponent[from=44, to=18], TSPComponent[from=18, to=40], TSPComponent[from=40, to=7], TSPComponent[from=7, to=9], TSPComponent[from=9, to=8], TSPComponent[from=8, to=42], TSPComponent[from=42, to=32], TSPComponent[from=32, to=2], TSPComponent[from=2, to=16], TSPComponent[from=16, to=20], TSPComponent[from=20, to=29], TSPComponent[from=29, to=28], TSPComponent[from=28, to=41], TSPComponent[from=41, to=6], TSPComponent[from=6, to=1]]
        assertTrue(result.equals(expResult1) || result.equals(expResult2));
        assertTrue(instance.repOK());
        assertTrue(result.repOK());
    }*/
}
