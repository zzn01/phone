package utils;

/**
 * Created by zzn on 5/16/15.
 */

public class Pair<A, B>{
    private A first;
    private B second;

    public Pair(A first, B second) {
        super();
        this.first = first;
        this.second = second;
    }
    public A first() {
        return this.first;
    }
    public B second() {
        return this.second;
    }
}
