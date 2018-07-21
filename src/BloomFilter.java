package oblig2;
import java.io.*;
import java.math.BigInteger;

public class BloomFilter {

    public static int M = 4594154; //Uses k=3 (optimally k=3.3), FPR: 0.0985403, Score: 6.4233542
    
    public static void main(String[] args) throws IOException {
        int hashFunctions = 3; //Number of hash functions (1-4)
        int[] indexes;
        int linesInInputFile = 0;
//Initializing Hash ST
        int[] hashST = new int[M];
        for (int i = 0; i < M; i++) hashST[i] = 0;
//Compressing data from input
            String sCurrentLine;
            File fileIn = new File("Ecoli.1M.36mer.txt"); //Input file
            BufferedReader br = new BufferedReader(new FileReader(fileIn));
            while ((sCurrentLine = br.readLine()) != null) {
                linesInInputFile++;
                indexes = hashFunctions(hashFunctions, sCurrentLine); //Creating hash ST indexes
                for (int i = 0; i < hashFunctions; i++){ //Setting corresponding hash ST indexes to 1
                    hashST[indexes[i]] = 1;
                }
            }
        double P = 0; //Number of positives (false or true)
        double queriedLines = 0; //Lines in queried file
        boolean positive = false; //Used to check if if all k indexes are 1 or not

//Querying data from other data set
            BufferedReader br2 = new BufferedReader(new FileReader("Ecoli.2M.36mer.txt"));
            while ((sCurrentLine = br2.readLine()) != null) {
                queriedLines++; //counting lines in queried file

                //Generating hash indexes of current string
                indexes = hashFunctions(hashFunctions, sCurrentLine);

                //Checking if each index already is in our Hash ST
                for (int i = 0; i < hashFunctions; i++){
                    if(hashST[indexes[i]] == 1){
                        positive = true;
                    }
                    else{
                //If one of hash ST elements is 0 we break.
                        positive = false;
                        break;
                    }
                }
                //If all elements were 1, we have a positive (can be true or false positive)
                if(positive) P++;
            }
//Writing compressed data to file
            File fileOut = new File("compressed_data.txt");
            PrintWriter pr = new PrintWriter(fileOut);

            for (int i = 0; i < hashST.length; i++) {
                pr.print(hashST[i]);
            }
            pr.close();

//Parameters for determining score
        long Si = fileIn.length();
        long Sc = fileOut.length();
        double FP = P - linesInInputFile;
        double FPR = FP/(FP + linesInInputFile);
        double S_ratio = queriedLines/linesInInputFile;
//Score
        double score = Si/Sc*(1-(S_ratio*FPR));
//Output print of run summary
        System.out.println("HashST size (M): " + M);
        System.out.println("Number of hash functions (k): " + hashFunctions);
        System.out.println("Initial file size [bytes]: " + Si);
        System.out.println("Compressed file size [bytes]: " + Sc);
        System.out.println("FPR: " + FPR);
        System.out.println("S_ratio: " + S_ratio);
        System.out.println("Score: " + score);
    }
/**
 * Method for hash functions
 * Takes input number of hash functions used and the the string that is to be hashed
 * Uses a switech statement and returns respective hash indexes depending on how many hash functions were used
* */
    public static int[] hashFunctions(int hashFunctions, String sCurrentLine){
        //Initializing 4 different hashing functions
                FNV fnv = new FNV();
                Murmur1 murmur1 = new Murmur1();
                Murmur2 murmur2 = new Murmur2();
                Murmur3 murmur3 = new Murmur3();
        //Initializing index array
                int[] indexes = new int[hashFunctions];
        //Big integer for FNV hashing
                BigInteger i;
        //Long for Murmur hashing
                long j, k, l;
        //Hash functions in switch statement depending on how many we require
        switch (hashFunctions){
            case 1:
                i = fnv.fnv1_32(sCurrentLine.getBytes());
                i = i.mod(new BigInteger(Integer.toString(M)));
                indexes[0] = i.intValue();
                break;
            case 2:
                i = fnv.fnv1_32(sCurrentLine.getBytes());
                i = i.mod(new BigInteger(Integer.toString(M)));
                j = murmur1.hash(sCurrentLine.getBytes(), sCurrentLine.getBytes().length, 4) % M;
                indexes[0] = i.intValue();
                indexes[1] = (int)j;
                break;
            case 3:
                i = fnv.fnv1_32(sCurrentLine.getBytes());
                i = i.mod(new BigInteger(Integer.toString(M)));
                j = murmur1.hash(sCurrentLine.getBytes(), sCurrentLine.getBytes().length, 4) % M;
                k = murmur2.hash(sCurrentLine.getBytes(), sCurrentLine.getBytes().length, 4) % M;
                indexes[0] = i.intValue();
                indexes[1] = (int)j;
                indexes[2] = (int)k;
                break;
            case 4:
                i = fnv.fnv1_32(sCurrentLine.getBytes());
                i = i.mod(new BigInteger(Integer.toString(M)));
                j = murmur1.hash(sCurrentLine.getBytes(), sCurrentLine.getBytes().length, 4) % M;
                k = murmur2.hash(sCurrentLine.getBytes(), sCurrentLine.getBytes().length, 4) % M;
                l = murmur3.hash_x86_32(sCurrentLine.getBytes(), sCurrentLine.getBytes().length, 4) % M;
                indexes[0] = i.intValue();
                indexes[1] = (int)j;
                indexes[2] = (int)k;
                indexes[3] = (int)l;
                break;
        }
        return indexes;
    }
}