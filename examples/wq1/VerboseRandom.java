package wq1;

import java.util.Random;

public class VerboseRandom extends Random {
    private static final long serialVersionUID = 5808329721373115293L;
    private long doubleCalls = 0;

    public VerboseRandom(long l) {
        super(l);
    }

    public double nextDouble() {
        double d = super.nextDouble();
        doubleCalls++;
        System.out.println("Random.nextDouble(): " + d + " calls:" 
                + doubleCalls);
        return d;
    }
}
