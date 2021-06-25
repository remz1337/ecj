package ec.vector;

import ec.EvolutionState;
import ec.Individual;
//import ec.app.rna.RNAProblem;
import ec.util.Code;
import ec.util.DecodeReturn;
import ec.util.MersenneTwisterFast;
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

public class BNKVectorIndividual extends VectorIndividual
    {
        private static final long serialVersionUID = 1;

        public static final String P_BNKVECTORINDIVIDUAL = "bnk-vect-ind";
        public boolean[] genome;

        public Parameter defaultBase()
        {
            return VectorDefaults.base().push(P_BNKVECTORINDIVIDUAL);
        }

        public Object clone()
        {
            BNKVectorIndividual myobj = (BNKVectorIndividual) (super.clone());

            // must clone the genome
            myobj.genome = (boolean[])(genome.clone());

            return myobj;
        }

/*        public void setup(final EvolutionState state, final Parameter base)
        {
            super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

            BitVectorSpecies s = (BitVectorSpecies)species;  // where my default info is stored
            genome = new boolean[s.genomeSize];
        }*/

        public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind)
        {
            BNKVectorSpecies s = (BNKVectorSpecies)species;  // where my default info is stored
            BNKVectorIndividual i = (BNKVectorIndividual) ind;
            boolean tmp;
            int point;

            int len = Math.min(genome.length, i.genome.length);
            if (len != genome.length || len != i.genome.length)
                state.output.warnOnce("Genome lengths are not the same.  Vector crossover will only be done in overlapping region.");

            switch(s.crossoverType)
            {
                case VectorSpecies.C_ONE_POINT:
                    //                point = state.random[thread].nextInt((len / s.chunksize)+1);
                    // we want to go from 0 ... len-1
                    // so that there is only ONE case of NO-OP crossover, not TWO
                    point = state.random[thread].nextInt((len / s.chunksize));
                    for(int x=0;x<point*s.chunksize;x++)
                    {
                        tmp = i.genome[x];
                        i.genome[x] = genome[x];
                        genome[x] = tmp;
                    }
                    break;
                case VectorSpecies.C_ONE_POINT_NO_NOP:
                    point = state.random[thread].nextInt((len / s.chunksize) - 1) + 1;  // so it goes from 1 .. len-1
                    for(int x=0;x<point*s.chunksize;x++)
                    {
                        tmp = i.genome[x];
                        i.genome[x] = genome[x];
                        genome[x] = tmp;
                    }
                    break;
                case VectorSpecies.C_TWO_POINT:
                {
                    //                int point0 = state.random[thread].nextInt((len / s.chunksize)+1);
                    //                point = state.random[thread].nextInt((len / s.chunksize)+1);
                    // we want to go from 0 to len-1
                    // so that the only NO-OP crossover possible is point == point0
                    // example; len = 4
                    // possibilities: a=0 b=0       NOP                             [0123]
                    //                                a=0 b=1       swap 0                  [for 1, 2, 3]
                    //                                a=0 b=2       swap 0, 1               [for 2, 3]
                    //                                a=0 b=3       swap 0, 1, 2    [for 3]
                    //                                a=1 b=1       NOP                             [1230]
                    //                                a=1 b=2       swap 1                  [for 2, 3, 0]
                    //                                a=1 b=3       swap 1, 2               [for 3, 0]
                    //                                a=2 b=2       NOP                             [2301]
                    //                                a=2 b=3       swap 2                  [for 3, 0, 1]
                    //                                a=3 b=3   NOP                         [3012]
                    // All intervals: 0, 01, 012, 0123, 1, 12, 123, 1230, 2, 23, 230, 2301, 3, 30, 301, 3012
                    point = state.random[thread].nextInt((len / s.chunksize));
                    int point0 = state.random[thread].nextInt((len / s.chunksize));
                    if (point0 > point) { int p = point0; point0 = point; point = p; }
                    for(int x=point0*s.chunksize;x<point*s.chunksize;x++)
                    {
                        tmp = i.genome[x];
                        i.genome[x] = genome[x];
                        genome[x] = tmp;
                    }
                }
                break;
                case VectorSpecies.C_TWO_POINT_NO_NOP:
                {
                    point = state.random[thread].nextInt((len / s.chunksize));
                    int point0 = 0;
                    do { point0 = state.random[thread].nextInt((len / s.chunksize)); }
                    while (point0 == point);  // NOP
                    if (point0 > point) { int p = point0; point0 = point; point = p; }
                    for(int x=point0*s.chunksize;x<point*s.chunksize;x++)
                    {
                        tmp = i.genome[x];
                        i.genome[x] = genome[x];
                        genome[x] = tmp;
                    }
                }
                break;
                case BNKVectorSpecies.C_ANY_POINT:
                    for(int x=0;x<len/s.chunksize;x++)
                        if (state.random[thread].nextBoolean(s.crossoverProbability))
                            for(int y=x*s.chunksize;y<(x+1)*s.chunksize;y++)
                            {
                                tmp = i.genome[y];
                                i.genome[y] = genome[y];
                                genome[y] = tmp;
                            }
                    break;
                default:
                    state.output.fatal("In valid crossover type in BNKVectorIndividual.");
                    break;
            }
        }

/*
        */
/** Splits the genome into n pieces, according to points, which *must* be sorted.
         pieces.length must be 1 + points.length *//*

        public void split(int[] points, Object[] pieces)
        {
            int point0, point1;
            point0 = 0; point1 = points[0];
            for(int x=0;x<pieces.length;x++)
            {
                pieces[x] = new boolean[point1-point0];
                System.arraycopy(genome,point0,pieces[x],0,point1-point0);
                point0 = point1;
                if (x >=pieces.length-2)
                    point1 = genome.length;
                else point1 = points[x+1];
            }
        }

        */
/** Joins the n pieces and sets the genome to their concatenation.*//*

        public void join(Object[] pieces)
        {
            int sum=0;
            for(int x=0;x<pieces.length;x++)
                sum += ((boolean[])(pieces[x])).length;

            int runningsum = 0;
            boolean[] newgenome = new boolean[sum];
            for(int x=0;x<pieces.length;x++)
            {
                System.arraycopy(pieces[x], 0, newgenome, runningsum, ((boolean[])(pieces[x])).length);
                runningsum += ((boolean[])(pieces[x])).length;
            }
            // set genome
            genome = newgenome;
        }
*/

/*
        */
/** Destructively mutates the individual in some default manner.  The default form
         does a bit-flip with a probability depending on parameters. *//*

        public void defaultMutate(EvolutionState state, int thread)
        {
            BitVectorSpecies s = (BitVectorSpecies)species;  // where my default info is stored
            for(int x=0;x<genome.length;x++)
            {
                if (state.random[thread].nextBoolean(s.mutationProbability(x)))
                {
                    boolean old = genome[x];
                    for(int retries = 0; retries < s.duplicateRetries(x) + 1; retries++)
                    {
                        switch(s.mutationType(x))
                        {
                            case BitVectorSpecies.C_FLIP_MUTATION:
                                genome[x] = !genome[x];
                                break;
                            case BitVectorSpecies.C_RESET_MUTATION:
                                genome[x] = state.random[thread].nextBoolean();
                                break;
                            default:
                                state.output.fatal("In BitVectorIndividual.defaultMutate, default case occurred when it shouldn't have");
                                break;
                        }
                        if (genome[x] != old) break;
                        // else genome[x] = old;  // try again
                    }
                }
            }
        }

        */
/** Initializes the individual by randomly flipping the bits *//*

        public void reset(EvolutionState state, int thread)
        {
            for(int x=0;x<genome.length;x++)
                genome[x] = state.random[thread].nextBoolean();
        }
*/

        public int hashCode()
        {
            // stolen from GPIndividual.  It's a decent algorithm.
            int hash = this.getClass().hashCode();

            hash = ( hash << 1 | hash >>> 31 ) ^ Arrays.hashCode(genome);

            return hash;
        }

        public String genotypeToStringForHumans()
        {
            StringBuilder s = new StringBuilder();
            for( int i = 0 ; i < genome.length ; i++ )
            {
                if( genome[i] )
                    s.append("1 ");
                else
                    s.append("0 ");
            }
            return s.toString();
        }

        public String genotypeToString()
        {
            StringBuilder s = new StringBuilder();
            s.append( Code.encode( genome.length ) );
            for( int i = 0 ; i < genome.length ; i++ )
                s.append( Code.encode( genome[i] ) );
            return s.toString();
        }

        protected void parseGenotype(final EvolutionState state,
                                     final LineNumberReader reader) throws IOException
        {
            // read in the next line.  The first item is the number of genes
            String s = reader.readLine();
            DecodeReturn d = new DecodeReturn(s);
            Code.decode( d );
            if (d.type != DecodeReturn.T_INTEGER)  // uh oh
                state.output.fatal("Individual with genome:\n" + s + "\n... does not have an integer at the beginning indicating the genome count.");
            int lll = (int)(d.l);

            genome = new boolean[ lll ];

            // read in the genes
            for( int i = 0 ; i < genome.length ; i++ )
            {
                Code.decode( d );
                genome[i] = (boolean)(d.l!=0);
            }
        }

        public boolean equals(Object ind)
        {
            if (ind==null) return false;
            if (!(this.getClass().equals(ind.getClass()))) return false; // SimpleRuleIndividuals are special.
            BNKVectorIndividual i = (BNKVectorIndividual)ind;
            if( genome.length != i.genome.length )
                return false;
            for( int j = 0 ; j < genome.length ; j++ )
                if( genome[j] != i.genome[j] )
                    return false;
            return true;
        }

        public Object getGenome()
        { return genome; }
        public void setGenome(Object gen)
        { genome = (boolean[]) gen; }
        public int genomeLength()
        { return genome.length; }

        public void setGenomeLength(int len)
        {
            boolean[] newGenome = new boolean[len];
            System.arraycopy(genome, 0, newGenome, 0,
                    genome.length < newGenome.length ? genome.length : newGenome.length);
            genome = newGenome;
        }

        public void writeGenotype(final EvolutionState state,
                                  final DataOutput dataOutput) throws IOException
        {
            dataOutput.writeInt(genome.length);
            for(int x=0;x<genome.length;x++)
                dataOutput.writeBoolean(genome[x]);  // inefficient: booleans are written out as bytes
        }

        public void readGenotype(final EvolutionState state,
                                 final DataInput dataInput) throws IOException
        {
            int len = dataInput.readInt();
            if (genome==null || genome.length != len)
                genome = new boolean[len];
            for(int x=0;x<genome.length;x++)
                genome[x] = dataInput.readBoolean();
        }

        /** Implements distance as hamming distance. */
        public double distanceTo(Individual otherInd)
        {
            if (!(otherInd instanceof BNKVectorIndividual))
                return super.distanceTo(otherInd);  // will return infinity!

            BNKVectorIndividual other = (BNKVectorIndividual) otherInd;
            boolean[] otherGenome = other.genome;
            double hammingDistance =0;
            for(int i=0; i < other.genomeLength(); i++)
            {
                if(genome[i] ^ otherGenome[i])  //^ is xor
                    hammingDistance++;
            }

            return hammingDistance;
        }



        public static final String P_N = "n";
        public static final String P_K = "k";

        public int inputs;//number of inputs (K)
        public int gates;//number of gate (N)

        public int output_len;
        public int genes_per_gate;

        public void setup(final EvolutionState state, final Parameter base)
        {
            super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

            BNKVectorSpecies s = (BNKVectorSpecies)species;  // where my default info is stored

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
                    if(activated_inputs.contains(gate_input_it)){
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

        /** Destructively mutates the individual in some default manner.  The default form
         does a bit-flip with a probability depending on parameters. */
        public void defaultMutate(EvolutionState state, int thread)
        {
            BNKVectorSpecies s = (BNKVectorSpecies)species;  // where my default info is stored
            //check if baseline first
            if (s.isBaselineMutation) {
                //Pick only 1 gene to mutate
                MersenneTwisterFast srt = state.random[thread];
                int genePos = srt.nextInt(genome.length);

                //check if pos is an input to a gate, if so a second mutation is needed to keep the same amount of inputs
                //1) check which gate is modified
                int gate_it=genePos/genes_per_gate;
                int gate_input_start_pos=gate_it*genes_per_gate;
                int gate_input_stop_pos=gate_input_start_pos+gates-1;
                if(genePos>=gate_input_start_pos && genePos<=gate_input_stop_pos){
                    //need to change another input of that gate
                    //first, check if we are adding or removing the input
                    if(genome[genePos]){//input is currently active. After removing it, we will need to add another one that is not already there
                        //pick a gate that is not yet connected
                        int swap_pos=srt.nextInt(gates-inputs);
                        int swap_pos_it=0;

                        for (int input_it=0;input_it<gates;input_it++){
                            int absolute_pos=gate_input_start_pos+input_it;
                            if(!genome[absolute_pos]){
                                if(swap_pos==swap_pos_it){
                                    //swap_pos_absolute=absolute_pos;
                                    boolean new_val_swap=!genome[absolute_pos];
                                    genome[absolute_pos] = new_val_swap;
                                }
                                swap_pos_it++;
                            }
                        }
                    }else{
                        //pick a gate that is currently connected
                        int swap_pos=srt.nextInt(inputs);
                        int swap_pos_it=0;

                        for (int input_it=0;input_it<gates;input_it++){
                            int absolute_pos=gate_input_start_pos+input_it;
                            if(genome[absolute_pos]){
                                if(swap_pos==swap_pos_it){
                                    //swap_pos_absolute=absolute_pos;
                                    boolean new_val_swap=!genome[absolute_pos];
                                    genome[absolute_pos] = new_val_swap;
                                }
                                swap_pos_it++;
                            }
                        }
                    }
                }

                //default flip
                boolean new_val=!genome[genePos];
                genome[genePos] = new_val;

            } else {
                for (int x = 0; x < genome.length; x++) {
                    if (state.random[thread].nextBoolean(s.mutationProbability(x))) {
                        boolean old = genome[x];
                        for (int retries = 0; retries < s.duplicateRetries(x) + 1; retries++) {
                            switch (s.mutationType(x)) {
                                case BNKVectorSpecies.C_FLIP_MUTATION:
                                    genome[x] = !genome[x];
                                    break;
                                case BNKVectorSpecies.C_RESET_MUTATION:
                                    genome[x] = state.random[thread].nextBoolean();
                                    break;
                                default:
                                    state.output.fatal("In BNKVectorIndividual.defaultMutate, default case occurred when it shouldn't have");
                                    break;
                            }
                            if (genome[x] != old) break;
                            // else genome[x] = old;  // try again
                        }
                    }
                }
            }
        }

    }
