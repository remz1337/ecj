package ec.app.bnk;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.BNKVectorIndividual;
import org.jfree.util.StringUtils;

import java.util.*;
//import java.util.List;

/**
 * A Binary NetworK Model (also called BNK model or “bonk” model) is a system of N logic gates
 * each with K inputs, with 0 ≤ K ≤ N. We can generalize the BNK model by allowing gates to have
 * different numbers of inputs, but we shall not do so here. We abbreviate “BNK model with N = n
 * and K = k” to “(n, k)-system”
 *
 *  Genotype
 * The genotype of a BNK is a binary string G = G1 · G2 · · · GN in which Gi specifies gate i and ·
 * stands for concatenation. Each Gi has the form I · O, and we refer to the binary strings I and O
 * as the input and output specifications, respectively.
 * Each input specification I has N bits of which K bits are 1s and the rest are 0s. If the ith bit of
 * gate j is 1, then the output of gate i provides one of the inputs of gate j.
 * The truth table for a gate with K inputs has one row for each combination of the inputs, that is,
 * 2
 * K rows. The output specification of a gate has 2K bits, with each bit corresponding to a row of
 * the truth table.
 * Each gate specification therefore has N +2K bits. The complete genotype has N such specifications
 * comprising N(N + 2K) bits. Figure 10 shows an example.
 *
 * PROBLEM:
 * A repressilator consists of three NOT gates connected in a loop. It is easily modelled as a (3, 1) BNK
 * model with genome 001 10 100 10 010 10, in which blanks are included to improve readability.
 * Figure 16 shows state transitions of the repressilator: there is a short loop 000 ↔ 111 and a longer
 * loop consisting of the remaining states.
 *
*/


public class BNK extends Problem implements SimpleProblemForm
    {
    public void setup(final EvolutionState state, final Parameter base) 
        {
        super.setup(state, base);
        }
        
    public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation, final int threadnum)
        {
        BNKVectorIndividual ind2 = (BNKVectorIndividual) ind;
        double fitness =0;
            ind2.phenotype="";

        //Build the phenotype
            int STATES_NUM= (int) Math.pow(2,ind2.gates);
            ArrayList<Integer> state_diagram = new ArrayList<Integer>();
        //for each state
            for(int state_it=0;state_it<STATES_NUM;state_it++){
                String tmp_machine_state_str=Integer.toBinaryString(state_it);
                String machine_state_str= String.format("%1$" + ind2.gates + "s", tmp_machine_state_str).replace(' ', '0');
                //Integer next_state;//= new boolean[ind2.gates];

                String next_state_str=new String();
                for(int gate_it=0;gate_it<ind2.gates;gate_it++){
                    String truth_entry_bin = new String();
                    
                    //Gate input
                    for(int input_it=0;input_it< ind2.gates;input_it++){
                        //mask with input binary string to find the corresponding truth table entry
                        //for each input, if ==1 and state[input_it]==1...\

                        int gene_pos=gate_it*ind2.genes_per_gate+input_it;
                        if(ind2.genome[gene_pos]){
                            if(machine_state_str.toCharArray()[input_it]=='1'){
                                truth_entry_bin+="1";
                            }else{
                                truth_entry_bin+="0";
                            }
                        }
                    }

                    if(truth_entry_bin.length()>ind2.inputs){
                        throw new IllegalStateException(String.format("Truth table entry (%d) doesn't fit with the number of inputs (%d).", truth_entry_bin.length(), ind2.inputs));
                    }

                    //find truth table output for current state
                    int truth_entry=Integer.parseUnsignedInt(truth_entry_bin,2);

                    int truth_output_pos=gate_it*ind2.genes_per_gate+(ind2.gates+truth_entry);
                    boolean output = ind2.genome[truth_output_pos];
                    next_state_str+=output ? 1 : 0;
                    //next_state[gate_it]=output;
                    //next_state=Integer.parseUnsignedInt(truth_entry_bin,2);
                }
                ind2.phenotype+=next_state_str;
                Integer next_state=Integer.parseUnsignedInt(next_state_str,2);
                state_diagram.add(next_state);
            }
                                
        //Evaluate fitness for an oscillator
            //the STD (State Transition Diagram) needs to end in a loop/cycle
            //Check if any gate oscillates within the loop :
//            G1: (0 => 1 = >1 => 0)R, i.e., a symmetric square wave of period 2 (ideal, slow)
//            G2: (1 => 0=> 1 => 0)R, i.e.,  a symmetric square wave of period 1 (ideal, fast)
            //Do this for all states, and count how many achieved oscillatory behaviour
            //divide by total number of states
            int oscilliary_states_count=0;
            double oscilliary_states_weight=0;
            Map<String,Integer> regimes = new HashMap<String,Integer>();
            for(int state_it=0;state_it<STATES_NUM;state_it++){
                ArrayList<Integer> visited = new ArrayList<Integer>();
                boolean looped=false;
                Integer current_state=state_it;
                while (!looped){
                    if(visited.contains(current_state)){
                        looped=true;
                        //check oscillation

                        String[] gates_states=new String[ind2.gates];
                        for (int gate_it=0;gate_it<ind2.gates;gate_it++){
                            gates_states[gate_it]="";//init with empty string
                        }

                        //int loop_start_idx=visited.indexOf(current_state);
                        for (int loop_idx=visited.indexOf(current_state);loop_idx<visited.size();loop_idx++){
                            String tmp_machine_state_str=Integer.toBinaryString(visited.get(loop_idx));
                            String machine_state_str= String.format("%1$" + ind2.gates + "s", tmp_machine_state_str).replace(' ', '0');
                            char[] machine_state = machine_state_str.toCharArray();

                            for (int gate_it=0;gate_it<ind2.gates;gate_it++){
                                gates_states[gate_it]+=machine_state[gate_it];
                            }
                        }

                        //For each gate, check the oscillation between 0 and 1, and make sure the period is symmetrical
                        boolean perfect_oscillator=false;
                        boolean asymmetrical_oscillator=false;
                        Set<String> local_regimes=new HashSet<String>();
                        for(int gate_it=0;gate_it<ind2.gates;gate_it++){
                            char[] states=gates_states[gate_it].toCharArray();
                            ArrayList<Integer> periods=new ArrayList<Integer>();
                            int current_period=1;
                            char last_char=states[0];
                            for(int char_it=1;char_it<states.length;char_it++){
                                if(last_char==states[char_it]) {
                                    current_period++;
                                }else{
                                    periods.add(current_period);
                                    current_period=1;
                                }
                                last_char=states[char_it];
                            }
                            //add last period
                            if(last_char==states[0]){
                                if(periods.size()>0){
                                    int tmp_period=periods.get(0)+current_period;
                                    periods.set(0,tmp_period);
                                }else{
                                    periods.add(current_period);
                                }
                            }else{
                                periods.add(current_period);
                            }

                            //ensure we have 2 periods
                            if(periods.size()>=2){
                                int first_period= periods.get(0);
                                int second_period= periods.get(1);
                                boolean all_first_periods_equal=true;
                                boolean all_second_periods_equal=true;

                                for (int period_it=0;period_it<periods.size();period_it++) {
                                    if (period_it%2==0){
                                        if(periods.get(period_it)!=first_period){
                                            all_first_periods_equal=false;
                                        }
                                    }else{
                                        if(periods.get(period_it)!=second_period){
                                            all_second_periods_equal=false;
                                        }
                                    }
                                }

                                if(all_first_periods_equal && all_second_periods_equal){
                                    if(first_period==second_period){
                                        perfect_oscillator=true;
                                    }else{
                                        asymmetrical_oscillator=true;
                                    }

                                    String regime_str="";
                                    if(first_period>=second_period){//period order doesn't matter, so sort desc
                                        regime_str=first_period+";"+second_period;
                                    }else{
                                        regime_str=second_period+";"+first_period;
                                    }
                                    local_regimes.add(regime_str);
                                }
                            }
                        }
                        for (String unique_regime:local_regimes) {
                            Integer regime_count = regimes.getOrDefault(unique_regime,0);
                            regime_count++;
                            regimes.put(unique_regime,regime_count);
                        }

                        if(perfect_oscillator){
                            oscilliary_states_weight++;
                            oscilliary_states_count++;
                        }else if(asymmetrical_oscillator){
                            oscilliary_states_weight+=0.5;
                            oscilliary_states_count++;
                        }
                    }else{
                        visited.add(current_state);
                        current_state=state_diagram.get(current_state);
                    }
                }
            }

            //fitness=oscilliary_states/STATES_NUM;

            double max_count_same_regime=0.0;
            for(String key : regimes.keySet()) {
                Integer value = regimes.get(key);
                if(value>max_count_same_regime){
                    max_count_same_regime=value;
                }
            }
            double max_prob=max_count_same_regime/STATES_NUM;
            //double max_prob=max_count_same_regime/oscilliary_states_count;

            fitness=(oscilliary_states_weight/STATES_NUM)* max_prob;

            Boolean isIdeal=false;
            if(fitness==1.0){
                isIdeal=true;
            }

        ((SimpleFitness)(ind2.fitness)).setFitness(state, fitness, isIdeal);
        ind2.evaluated = true; 
        }

//        public boolean verifyAllEqualUsingALoop(List<Integer> list) {
//            for (Integer s : list) {
//                if (!s.equals(list.get(0)))
//                    return false;
//            }
//            return true;
//        }
    }
