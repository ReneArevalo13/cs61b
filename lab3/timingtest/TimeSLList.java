package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        int[] elements = {1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        AList<Double> timeSeconds = new AList<Double>();
        AList<Integer> elementsN = new AList<Integer>();
        AList<Integer> opCount = new AList<Integer>();
        for (int N: elements){
            int count = 0;
            SLList<Integer> SL = new SLList<Integer>();
            while (count < N){
                SL.addLast(count);
                count += 1;
            }

            Stopwatch sw = new Stopwatch();
            int M = 10000;
            for(int i = 0; i < M; i++){
                SL.getLast();
            }

            double timeInSeconds = sw.elapsedTime();

            timeSeconds.addLast(timeInSeconds);
            elementsN.addLast(N);
            opCount.addLast(M);
        }
        printTimingTable(elementsN, timeSeconds, opCount);
    }

}
