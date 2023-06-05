import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private double[] thresholds;

    private static int[] indGrid2sub(int idx, int n){
        int row = idx/n;
        int col = idx - row*n;
//        int[] ans = {row,col}; //return 0-based indexing
        int[] ans = {row+1,col+1}; //return 1-based indexing
        return ans;
    }

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials){
        if(n <= 0 || trials <=0){
            throw new IllegalArgumentException();
        }

        double thresh;
        thresholds = new double[trials];
        Percolation P;
        int[] subIdx;
        int numGridPts = n*n;
        // Method 2, create array with random elements and shuffle
        int[] shuffleArr = new int[numGridPts];
        for(int i = 0; i < (n*n); i++){
            shuffleArr[i] = i;
        }
        for(int iTrial = 0; iTrial < trials; iTrial++){
            P = new Percolation(n); //should object be created outside loop and have it reset?

            // Open new site using  Knuth’s method: when reading the ith cell, select it with probability 1/i
            // to be the champion, replacing the previous champion. After reading all of the cells, use the surviving champion.
            // Skip cells that are open.
            StdRandom.shuffle(shuffleArr);
            int cnt = 0;
            while(!P.percolates()){
                /*
                int cnt = 1;
                double prob = 0.0;
                boolean sel = false;
                int[] champ = {0, 0};
                for (int iCell = 0; iCell < P.numGridPts; iCell++) {
                    subIdx = P.indGrid2sub(iCell, n);
                    if (!P.isOpen(subIdx[0], subIdx[1])) {
                        prob = 1.0d / (double) cnt;
                        sel = StdRandom.bernoulli(prob);
                        if (sel)
                            champ = subIdx;
                        cnt++;
                    }
                }

                 */

                int[] champ = indGrid2sub(shuffleArr[cnt],n);
                P.open(champ[0], champ[1]);
                cnt++;
            }
            thresh = (double)P.numberOfOpenSites()/(double)numGridPts;
            thresholds[iTrial] = thresh;
        }//iTrial
    }

    private double confidenceFactor(){
        return 1.96*stddev()/Math.sqrt(thresholds.length);
    }

    // sample mean of percolation threshold
    public double mean(){
        return StdStats.mean(thresholds);
    }

    // sample standard deviation of percolation threshold
    public double stddev(){
        return StdStats.stddev(thresholds);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo(){
        return mean() - confidenceFactor();
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi(){
        return mean() + confidenceFactor();
    }

    // test client (see below)
    public static void main(String[] args){
        Stopwatch t1 = new Stopwatch();
        int nGrid = Integer.parseInt(args[0]);
        int nTrials = Integer.parseInt(args[1]);
        PercolationStats Pstats = new PercolationStats(nGrid, nTrials);
        StdOut.println("mean = " + Pstats.mean());
        StdOut.println("stddev = " + Pstats.stddev());
        StdOut.println("95% confidence interval = ["+ Pstats.confidenceLo() + ", " + Pstats.confidenceHi() + "]");
        StdOut.println("Elapsed Time = " + t1.elapsedTime() + " seconds");
        // Print using something similar to this:
//        StdOut.printf("       min %10.3f\n", min(a));
//        StdOut.printf("      mean %10.3f\n", mean(a));
//        StdOut.printf("       max %10.3f\n", max(a));
//        StdOut.printf("    stddev %10.3f\n", stddev(a));
//        StdOut.printf("       var %10.3f\n", var(a));
//        StdOut.printf("   stddevp %10.3f\n", stddevp(a));
//        StdOut.printf("      varp %10.3f\n", varp(a));
    }
}
