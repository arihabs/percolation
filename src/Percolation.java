/*

 */
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int N;
    private boolean[] isOpen;
    private int numGridPts;
    private int numGridPtsVirtual;

    private int virtualTopIdx = 0;

    private int virtualBottomIdx;
    private WeightedQuickUnionUF wquf;

    private static boolean isInBounds(int row, int col, int n){
        row--; col--;
        if(row < 0 || row > n-1 || col < 0 || col > n-1)
            return false;
        else
            return true;
    };

    private static int sub2indGrid(int row, int col, int n){
        row--; col--;
        return n*row + col;
//        return n*(row-1) + (col-1);
    }

    private static int sub2indVirtual(int row, int col, int n){
        return  sub2indGrid(row,col,n) + 1;
    }

    private static int[] indGrid2sub(int idx, int n){
        int row = idx/n;
        int col = idx - row*n;
//        int[] ans = {row,col}; //return 0-based indexing
        int[] ans = {row+1,col+1}; //return 1-based indexing
        return ans;
    }

    private static int[] indVirtual2sub(int idx, int n){
        return indGrid2sub(idx-1,n);
    }

    private boolean isTopRow(int row){
        row--;
        return (row == 0);
    }

    private boolean isBottomRow(int row){
        row--;
        return (row == N-1);
    }


    // Create N x N grid with all sites initially blocked
    public Percolation(int n){
        if(n <= 0){
            throw new IllegalArgumentException("n must be greater than zero.");
        }
        N = n;
        numGridPts = N*N;
        numGridPtsVirtual = numGridPts + 2;
        virtualBottomIdx = numGridPtsVirtual - 1;
        isOpen = new boolean[numGridPtsVirtual];

        for(int i = 0; i < numGridPtsVirtual; i++)
            isOpen[i] = false;

        // Open virtual elements
        isOpen[virtualTopIdx] = true;
        isOpen[virtualBottomIdx] = true;

//        isOpen = new boolean[numGridPts];
//        for(int i = 0; i < numGridPts; i++)
//            isOpen[i] = false;

        // Create a Weighted Quick Union Find object and connect the top virtual site to the first row and bottom virtual site to the bottom row
        wquf = new WeightedQuickUnionUF(numGridPtsVirtual);
//        for(int i = 0; i < n; i++)
//            wquf.union(0,i+1);
//        for(int i = numGridPts - 1; i >= numGridPts - n; i--)
//            wquf.union(numGridPtsVirtual-1,i+1);
    }

    // opens the site (row, col) if it is not open already. Convention assumes row and col start with index 1.
    public void open (int row, int col){
        if(!isInBounds(row, col, N))
            throw new IllegalArgumentException("(row,col) not in bounds.");

        // Mark new site as open; Offset by 1 to account for virtual top element.
        int ind = sub2indVirtual(row,col,N);
        isOpen[ind] = true;

        // If top row, connect to virtual top site.
        if(isTopRow(row))
            wquf.union(ind,virtualTopIdx);

        // If bottom row, attach to virtual bottom site.
        if(isBottomRow(row))
            wquf.union(ind,virtualBottomIdx);

        // Connect to all of its adjacent open sites
        int[][] idx_neighbor = {{row-1, col}, {row, col-1}, {row+1, col},{row, col+1}};
        int currRow, currCol, neighborIdx = -1;
        for(int i = 0; i < 4; i++){
            currRow = idx_neighbor[i][0];
            currCol = idx_neighbor[i][1];
            if(isInBounds(currRow, currCol, N) && isOpen(currRow,currCol)) {
                neighborIdx = sub2indVirtual(currRow, currCol, N);
                wquf.union(ind, neighborIdx);
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col){
        if(!isInBounds(row, col, N))
            throw new IllegalArgumentException("(row,col) not in bounds.");
        return isOpen[sub2indVirtual(row,col,N)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col){
        if(!isInBounds(row, col, N))
            throw new IllegalArgumentException("(row,col) not in bounds.");

        int ind = sub2indVirtual(row,col,N);
        boolean isOp = isOpen[ind];
        if(isOp) {
            int rootp = wquf.find(virtualTopIdx);
            int rootq = wquf.find(ind);
            return (rootp == rootq);
        }
        else
            return false;

    }

    // returns the number of open sites
    public int numberOfOpenSites(){
        int cnt = 0;
        for(boolean i : isOpen){
            if(i) cnt++;
        }
        return cnt-2;// exclude virtual sites
    }

    // does the system percolate?
    public boolean percolates(){
        int rootq = wquf.find(virtualTopIdx);
        int rootp = wquf.find(virtualBottomIdx);
        return (rootq ==  rootp);
    }

    // test client
    public static void main(String[] args){
        int n = StdIn.readInt();
        Percolation P = new Percolation(n);
        StdOut.println("N: " + n + " # of Open Sites: " + P.numberOfOpenSites());
        int node = 0;
        int[] subIdx;
        while(!P.percolates()){
            subIdx = Percolation.indGrid2sub(node,n);
            StdOut.println("Opening Node " + node + ", at grid location (" +subIdx[0]+ "," +subIdx[1]+ ").");
            P.open(subIdx[0], subIdx[1]);
            StdOut.println("# of Open Sites: " + P.numberOfOpenSites());
            node++;
        }
    }
}
