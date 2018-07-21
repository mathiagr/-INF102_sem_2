/******************************************************************************
 *  Compilation:  javac Heap.java
 *  Execution:    java Heap < input.txt
 *  Dependencies: StdOut.java StdIn.java
 *  Data files:   http://algs4.cs.princeton.edu/24pq/tiny.txt
 *                http://algs4.cs.princeton.edu/24pq/words3.txt
 *
 *  Sorts a sequence of strings from standard input using heapsort.
 *
 *  % more tiny.txt
 *  S O R T E X A M P L E
 *
 *  % java Heap < tiny.txt
 *  A E E L M O P R S T X                 [ one string per line ]
 *
 *  % more words3.txt
 *  bed bug dad yes zoo ... all bad yet
 *
 *  % java Heap < words3.txt
 *  all bad bed bug dad ... yes yet zoo   [ one string per line ]
 *
 ******************************************************************************/

package oblig2;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.Random;

/**
 *  The {@code Heap} class provides a static methods for heapsorting
 *  an array.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class HeapModified {

    // This class should not be instantiated.
    private HeapModified() { }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param pq the array to be sorted
     */
    public static void sort(Comparable[] pq) {
        int n = pq.length;
//Creating the Heap
        for (int k = n/2; k >= 1; k--)
            sink(pq, k, n);
//Sorting
        while (n > 1) {
            exch(pq, 1, n--); //Exchange first and last element
            sinkToBottomAndSwimUp(pq, 1, n); //we sink the upper element to the bottom, without comparing if it's on the right position
                                             //From the bottom we swim it up to it's correct position for Max Heap Orientation
        }
    }

    /***************************************************************************
     * Helper functions to restore the heap invariant.
     ***************************************************************************/
    private static void swim(Comparable[] pq, int k) {
        while(k>1 && less(pq, k/2, k)){
            exch(pq, k/2, k);
            k = k/2;
        }
    }

//We use sink only to create the Max Heap Orientation initially
    private static void sink(Comparable[] pq, int k, int n) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && less(pq, j, j+1)) j++;
            if (!less(pq, k, j)) break;
            exch(pq, k, j);
            k = j;
        }
    }
//We use sinkToTheBottom to sink first element to the bottom without comparing that it has reached it correct position along the way
    private static void sinkToBottomAndSwimUp(Comparable[] pq, int k, int n) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && less(pq, j, j+1)) j++;
            //if (!less(pq, k, j)) break; //We remove the check if the element has sunk to correct position
                                          //so that it rather sinks to the bottom
            exch(pq, k, j);
            k = j;
        }
        swim(pq, k); //When reached the bottom we swim it up to its correct position
    }


    /***************************************************************************
     * Helper functions for comparisons and swaps.
     * Indices are "off-by-one" to support 1-based indexing.
     ***************************************************************************/
    private static boolean less(Comparable[] pq, int i, int j) {
        return pq[i-1].compareTo(pq[j-1]) < 0;
    }

    private static void exch(Object[] pq, int i, int j) {
        Object swap = pq[i-1];
        pq[i-1] = pq[j-1];
        pq[j-1] = swap;
    }

    // is v < w ?
    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }


    /***************************************************************************
     *  Check if array is sorted - useful for debugging.
     ***************************************************************************/
    private static boolean isSorted(Comparable[] a) {
        for (int i = 1; i < a.length; i++)
            if (less(a[i], a[i-1])) return false;
        return true;
    }


    // print array to standard output
    private static void show(Comparable[] a) {
        for (int i = 0; i < a.length; i++) {
            StdOut.println(a[i]);
        }
    }

    /**
     * Reads in a sequence of strings from standard input; heapsorts them;
     * and prints them to standard output in ascending order.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        boolean modified = true;
        int[] stringLengths = new int[]{2, 10, 20, 40, 50}; //Length of strings

        //Running both modifies and then normal heapsort
        for (int o = 0; o < 2; o++) {
            if(o==0){
                System.out.println("Modified Heapsort: ");
            }
            if(o==1){
                modified = false;
                System.out.println("Normal Heapsort: ");
            }
            //Running both algorithms for various string lengths
            for (int m = 0; m < stringLengths.length; m++) {

                int keyLength = stringLengths[m]; //String lengths
                int keys = 10000; //Number of keys
                String alphabet = "abcdefghijklmnopqrstuvwxyz"; //Alphabet from which we generate the keys

                String[] a = new String[keys]; //Array containing random key strings

                //Generating a string of 10000 random strings of various length
                Random r = new Random();
                for (int i = 0; i < keys; i++) {
                    String key = "";
                    for (int j = 0; j < keyLength; j++) {
                        key += alphabet.charAt(r.nextInt(alphabet.length()));
                    }
                    a[i] = key;
                    //System.out.println(key);
                }

                int N = 500; //number of runs to take average of
                int warmUpRuns = 100; //"warmup" runs to improve the runtime estimate
                double totalTime = 0; //Accumulated runtime to be divided by N

                boolean shuffle = false; //Turn pre-shuffled array on/off

                for (int j = 0; j < N + warmUpRuns; j++) {
                    Heap.sort(a);
                    if(shuffle) StdRandom.shuffle(a);
                    if (j >= warmUpRuns) { //Only use the N runs, excluding the "warm ups" when taking the time

                        if(modified){
                            //Heapsort with modification
                            Stopwatch stopwatch = new Stopwatch();
                            HeapModified.sort(a);
                            totalTime += stopwatch.elapsedTime();
                            assert isSorted(a);
                            if(shuffle) StdRandom.shuffle(a);
                        }
                        else{
                            //Normal heapsort
                            Stopwatch stopwatch = new Stopwatch();
                            Heap.sort(a);
                            totalTime += stopwatch.elapsedTime();
                            assert isSorted(a);
                            if(shuffle) StdRandom.shuffle(a);
                        }
                    }
                }
                System.out.println(totalTime / N);
            }
        }
    }
}

/******************************************************************************
 *  Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
