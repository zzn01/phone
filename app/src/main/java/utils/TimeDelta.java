package utils;

/**
 * Created by zzn on 3/31/15.
 */
public class TimeDelta {
    public int day;
    public int hour;
    public int min;
    public int sec;

    public TimeDelta(int bigsec) {
        day = bigsec / (24 * 60 * 60);

        bigsec = bigsec % (24 * 60 * 60);
        hour = bigsec / (60 * 60);

        bigsec = bigsec % (60 * 60);

        min = bigsec / 60;

        sec = bigsec % 60;

    }
}
