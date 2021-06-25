package ec.app.bnk;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.BNKVectorIndividual;
import org.jfree.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;

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
        //int n = ind2.genome.length;

        //Build the phenotype
        //for each state
            for(int state_it=0;state_it<Math.pow(2,ind2.gates);state_it++){
                String tmp_machine_state_str=Integer.toBinaryString(state_it);
                String machine_state_str= String.format("%1$" + ind2.gates + "s", tmp_machine_state_str).replace(' ', '0');

                String next_state=new String();
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
                    next_state+=output ? 1 : 0;
                }
                ind2.phenotype+=next_state;
            }
                                
        //fitness /= n;
        ((SimpleFitness)(ind2.fitness)).setFitness( state, fitness, false);
        ind2.evaluated = true; 
        }
    }
