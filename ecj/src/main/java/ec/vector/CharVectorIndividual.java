/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.vector;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Code;
import ec.util.DecodeReturn;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.LineNumberReader;

/*
 * IntegerVectorIndividual.java
 * Created: Tue Mar 13 15:03:12 EST 2001
 */

/**
 * CharVectorIndividual is a VectorIndividual whose genome is an array of chars.
 * Gene values are comprised of the alphabet defined by the specie.
 * The default mutation method randomizes genes to new values in the alphabet,
 * with <tt>species.mutationProbability</tt>.
 */

public class CharVectorIndividual extends VectorIndividual
    {
    private static final long serialVersionUID = 1;

    public static final String P_CHARVECTORINDIVIDUAL = "char-vect-ind";
    public char[] genome;
    
    public Parameter defaultBase()
        {
        return VectorDefaults.base().push(P_CHARVECTORINDIVIDUAL);
        }

    public Object clone()
        {
        CharVectorIndividual myobj = (CharVectorIndividual) (super.clone());

        // must clone the genome
        myobj.genome = (char[])(genome.clone());
        
        return myobj;
        } 

    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

        Parameter def = defaultBase();
        
        if (!(species instanceof CharVectorSpecies))
            state.output.fatal("CharVectorIndividual requires an CharVectorSpecies", base, def);
        CharVectorSpecies s = (CharVectorSpecies) species;
        
        genome = new char[s.genomeSize];
        }
                
    public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind)
        {
        CharVectorSpecies s = (CharVectorSpecies) species;
        CharVectorIndividual i = (CharVectorIndividual) ind;
        char tmp;
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
            case VectorSpecies.C_ANY_POINT:
                for(int x=0;x<len/s.chunksize;x++) 
                    if (state.random[thread].nextBoolean(s.crossoverProbability))
                        for(int y=x*s.chunksize;y<(x+1)*s.chunksize;y++)
                            {
                            tmp = i.genome[y];
                            i.genome[y] = genome[y];
                            genome[y] = tmp;
                            }
                break;
/*            case VectorSpecies.C_LINE_RECOMB:
                {
                double alpha = state.random[thread].nextDouble() * (1 + 2*s.lineDistance) - s.lineDistance;
                double beta = state.random[thread].nextDouble() * (1 + 2*s.lineDistance) - s.lineDistance;
                long t,u;
                long min, max;
                for (int x = 0; x < len; x++)
                    {
                    min = s.minGene(x);
                    max = s.maxGene(x);
                    t = (long) Math.floor(alpha * genome[x] + (1 - alpha) * i.genome[x] + 0.5);
                    u = (long) Math.floor(beta * i.genome[x] + (1 - beta) * genome[x] + 0.5);
                    if (!(t < min || t > max || u < min || u > max))
                        {
                        genome[x] = (char) t;
                        i.genome[x] = (char) u;
                        }
                    }
                }
            break;*/
/*            case VectorSpecies.C_INTERMED_RECOMB:
                {
                long t,u;
                long min, max;
                for (int x = 0; x < len; x++)
                    {
                    do
                        {
                        double alpha = state.random[thread].nextDouble() * (1 + 2*s.lineDistance) - s.lineDistance;
                        double beta = state.random[thread].nextDouble() * (1 + 2*s.lineDistance) - s.lineDistance;
                        min = s.minGene(x);
                        max = s.maxGene(x);
                        t = (long) Math.floor(alpha * genome[x] + (1 - alpha) * i.genome[x] + 0.5);
                        u = (long) Math.floor(beta * i.genome[x] + (1 - beta) * genome[x] + 0.5);
                        } while (t < min || t > max || u < min || u > max);
                    genome[x] = (char) t;
                    i.genome[x] = (char) u;
                    }
                }
            break;*/
            }
        }

    /** Splits the genome into n pieces, according to points, which *must* be sorted. 
        pieces.length must be 1 + points.length */
    public void split(int[] points, Object[] pieces)
        {
        int point0, point1;
        point0 = 0; point1 = points[0];
        for(int x=0;x<pieces.length;x++)
            {
            pieces[x] = new int[point1-point0];
            System.arraycopy(genome,point0,pieces[x],0,point1-point0);
            point0 = point1;
            if (x >=pieces.length-2)
                point1 = genome.length;
            else point1 = points[x+1];
            }
        }
    
    /** Joins the n pieces and sets the genome to their concatenation.*/
    public void join(Object[] pieces)
        {
        int sum=0;
        for(int x=0;x<pieces.length;x++)
            sum += ((int[])(pieces[x])).length;
        
        int runningsum = 0;
        char[] newgenome = new char[sum];
        for(int x=0;x<pieces.length;x++)
            {
            System.arraycopy(pieces[x], 0, newgenome, runningsum, ((int[])(pieces[x])).length);
            runningsum += ((int[])(pieces[x])).length;
            }
        // set genome
        genome = newgenome;
        }


    /** Returns a random value from between min and max inclusive.  This method handles
        overflows that complicate this computation.  Does NOT check that
        min is less than or equal to max.  You must check this yourself. */
    public char randomValueFromAlphabet(char[] alphabet, MersenneTwisterFast random)
        {
            return alphabet[random.nextInt(alphabet.length)];
        }


    /** Destructively mutates the individual in some default manner.  The default form
        simply randomizes genes to a uniform distribution from the min and max of the gene values. */
    public void defaultMutate(EvolutionState state, int thread) {
        CharVectorSpecies s = (CharVectorSpecies) species;

        //check if baseline first
        if (s.isBaselineMutation) {
            MersenneTwisterFast srt = state.random[thread];
            int genePos = srt.nextInt(genome.length);

            //make sure it's different from initial value
            char new_val=genome[genePos];
            while (new_val == genome[genePos]){
                new_val = randomValueFromAlphabet(s.alphabet, state.random[thread]);
            }
            genome[genePos] = new_val;
        } else {
            for (int x = 0; x < genome.length; x++) {
                if (state.random[thread].nextBoolean(s.mutationProbability(x))) {
                    char old = genome[x];
                    for (int retries = 0; retries < s.duplicateRetries(x) + 1; retries++) {
                        switch (s.mutationType(x)) {
                            case CharVectorSpecies.C_RESET_MUTATION:
                                //genome[x] = randomValueFromClosedInterval((char)s.minGene(x), (char)s.maxGene(x), state.random[thread]);
                                genome[x] = randomValueFromAlphabet(s.alphabet, state.random[thread]);
                                break;
/*                        case CharVectorSpecies.C_RANDOM_WALK_MUTATION:
                            int min = (int)s.minGene(x);
                            int max = (int)s.maxGene(x);
                            if (!s.mutationIsBounded(x))
                                {
                                // okay, technically these are still bounds, but we can't go beyond this without weird things happening
                                max = Byte.MAX_VALUE;
                                min = Byte.MIN_VALUE;
                                }
                            do
                                {
                                char n = (char)(state.random[thread].nextBoolean() ? 1 : -1);
                                char g = genome[x];
                                if ((n == 1 && g < max) ||
                                    (n == -1 && g > min))
                                    genome[x] = (char)(g + n);
                                else if ((n == -1 && g < max) ||
                                    (n == 1 && g > min))
                                    genome[x] = (char)(g - n);
                                }
                            while (state.random[thread].nextBoolean(s.randomWalkProbability(x)));
                            break;*/
                            default:
                                state.output.fatal("In CharVectorIndividual.defaultMutate, default case occurred when it shouldn't have");
                                break;
                        }
                        if (genome[x] != old) break;
                        // else genome[x] = old;  // try again
                    }
                }
            }
        }
    }
        
    
    /** Initializes the individual by randomly choosing Integers uniformly from mingene to maxgene. */
    // notice that we bump to longs to avoid overflow errors
    public void reset(EvolutionState state, int thread)
        {
        CharVectorSpecies s = (CharVectorSpecies) species;
        for(int x=0;x<genome.length;x++)
            //genome[x] = (char)randomValueFromClosedInterval((char)s.minGene(x), (char)s.maxGene(x), state.random[thread]);
            genome[x] = randomValueFromAlphabet(s.alphabet, state.random[thread]);
        }

    public int hashCode()
        {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();

        hash = ( hash << 1 | hash >>> 31 );
        for(int x=0;x<genome.length;x++)
            hash = ( hash << 1 | hash >>> 31 ) ^ genome[x];

        return hash;
        }

    public String genotypeToStringForHumans()
        {
        StringBuilder s = new StringBuilder();
        for( int i = 0 ; i < genome.length ; i++ )
            { if (i > 0) s.append(" "); s.append(genome[i]); }
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
        
        // of course, even if it *is* an integer, we can't tell if it's a gene or a genome count, argh...
        if (d.type != DecodeReturn.T_CHAR)  // uh oh
            state.output.fatal("Individual with genome:\n" + s + "\n... does not have an char at the beginning indicating the genome count.");
        int lll = (int)(d.l);

        genome = new char[ lll ];

        // read in the genes
        for( int i = 0 ; i < genome.length ; i++ )
            {
            Code.decode( d );
            genome[i] = (char)(d.l);
            }
        }

    public boolean equals(Object ind)
        {
        if (ind == null) return false;
        if (!(this.getClass().equals(ind.getClass()))) return false; // SimpleRuleIndividuals are special.
        CharVectorIndividual i = (CharVectorIndividual)ind;
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
        { genome = (char[]) gen; }
    public int genomeLength()
        { return genome.length; }
        
    public void writeGenotype(final EvolutionState state,
        final DataOutput dataOutput) throws IOException
        {
        dataOutput.writeInt(genome.length);
        for(int x=0;x<genome.length;x++)
            dataOutput.writeInt(genome[x]);
        }

    public void readGenotype(final EvolutionState state,
        final DataInput dataInput) throws IOException
        {
        int len = dataInput.readInt();
        if (genome==null || genome.length != len)
            genome = new char[len];
        for(int x=0;x<genome.length;x++)
            genome[x] = dataInput.readChar();
        }

    /** Clips each gene value to be within its specified [min,max] range. */
/*    public void clamp()
        {
        CharVectorSpecies _species = (CharVectorSpecies)species;
        for (int i = 0; i < genomeLength(); i++)
            {
            char minGene = (char)_species.minGene(i);
            if (genome[i] < minGene)
                genome[i] = minGene;
            else 
                {
                char maxGene = (char)_species.maxGene(i);
                if (genome[i] > maxGene)
                    genome[i] = maxGene;
                }
            }
        }*/
                
    public void setGenomeLength(int len)
        {
        char[] newGenome = new char[len];
        System.arraycopy(genome, 0, newGenome, 0, 
            genome.length < newGenome.length ? genome.length : newGenome.length);
        genome = newGenome;
        }

    /** Returns true if each gene value is within is specified [min,max] range. */
/*    public boolean isInRange()
        {
        CharVectorSpecies _species = (CharVectorSpecies)species;
        for (int i = 0; i < genomeLength(); i++)
            if (genome[i] < _species.minGene(i) ||
                genome[i] > _species.maxGene(i)) return false;
        return true;
        }*/

        //SHOULD USE MED
/*    public double distanceTo(Individual otherInd)
        {               
        if (!(otherInd instanceof CharVectorIndividual))
            return super.distanceTo(otherInd);  // will return infinity!
                
        CharVectorIndividual other = (CharVectorIndividual) otherInd;
        char[] otherGenome = other.genome;
        double sumSquaredDistance =0.0;
        for(int i=0; i < other.genomeLength(); i++)
            {
            long dist = this.genome[i] - (long)otherGenome[i];
            sumSquaredDistance += dist*dist;
            }
        return StrictMath.sqrt(sumSquaredDistance);
        }*/
    }
