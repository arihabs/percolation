import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] thresholds;



    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials){
        if(n <= 0 || trials <=0){
            throw new IllegalArgumentException();
        }

        double thresh;
        thresholds = new double[trials];
        Percolation P;
        int[] subIdx;
        for(int iTrial = 0; iTrial < trials; iTrial++){
            P = new Percolation(n); //should object be created outside loop and have it reset?

            // Open new site using  Knuth’s method: when reading the ith cell, select it with probability 1/i
            // to be the champion, replacing the previous champion. After reading all of the cells, use the surviving champion.
            // Skip cells that are open.
            while(!P.percolates()){
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

                P.open(champ[0], champ[1]);
            }
            thresh = (double)P.numberOfOpenSites()/(double)P.numGridPts;
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
        int nGrid = Integer.parseInt(args[0]);
        int nTrials = Integer.parseInt(args[1]);
        PercolationStats Pstats = new PercolationStats(nGrid, nTrials);
        StdOut.println("mean = " + Pstats.mean());
        StdOut.println("stddev = " + Pstats.stddev());
        StdOut.println("95% confidence interval = ["+ Pstats.confidenceLo() + ", " + Pstats.confidenceHi() + "]");
    }
}
