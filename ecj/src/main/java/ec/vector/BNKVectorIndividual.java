package ec.vector;

import ec.EvolutionState;
import ec.Individual;
import ec.app.rna.RNAProblem;
import ec.util.Code;
import ec.util.DecodeReturn;
import ec.util.Parameter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * BNKVectorIndividual is a VectorIndividual whose genome is an array of booleans.
 * The default mutation method simply flips bits with <tt>mutationProbability</tt>.
 *
 * Differs from BitVector since it enforces some contraints on parts of its genome
 */

public class BNKVectorIndividual extends BitVectorIndividual
    {
        public static final String P_N = "n";
        public static final String P_K = "k";

        int inputs;//number of inputs (K)
        int gates;//number of gate (N)

        int output_len;
        int genes_per_gate;

        public void setup(final EvolutionState state, final Parameter base)
        {
            super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

            BitVectorSpecies s = (BitVectorSpecies)species;  // where my default info is stored

            inputs = state.parameters.getInt(base.push(P_K), null, 1);
            gates = state.parameters.getInt(base.push(P_N), null, 1);

            //check correct genome size
            output_len= (int) Math.pow(2, inputs);//2^k
            genes_per_gate = gates+output_len;
            int trueGenomeSize=gates*genes_per_gate;
            if(trueGenomeSize != s.genomeSize){
                throw new IllegalStateException(String.format("Provided genome size doesn't fit with the problem definition (N=%d,K=%d), The correct size should be %d.", gates, inputs, trueGenomeSize));
            }

            genome = new boolean[s.genomeSize];
        }


        /** Initializes the individual by randomly flipping the bits */
        public void reset(EvolutionState state, int thread)
        {
            int output_len= (int) Math.pow(2, inputs);//2^k
            int genes_per_gate = gates+output_len;

            for (int gate_it=0;gate_it<gates;gate_it++){
                //Inputs, need to ensure the correct number of gates are activated (true)
                List<Integer> range = IntStream.range(0, gates).boxed().collect(Collectors.toList());
                Collections.shuffle(range);
                List<Integer> activated_inputs=range.subList(0,inputs);
                for (int gate_input_it=0;gate_input_it<gates;gate_input_it++){
                    int gene_pos=genes_per_gate*gate_it+gate_input_it;
                    boolean input_state=false;
                    if(activated_inputs.contains(gene_pos)){
                        input_state=true;
                    }
                    genome[gene_pos] = input_state;
                }
                //Outputs (aka truth table)

                for (int output_it=0;output_it<output_len;output_it++){
                    int gene_pos=genes_per_gate*gate_it+(output_it+gates);
                    genome[gene_pos] = state.random[thread].nextBoolean();
                }
            }
        }
    }
