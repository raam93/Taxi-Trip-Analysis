package Assignment3.trips_reconstruction;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class TripKeySortComparator extends WritableComparator {

    public TripKeySortComparator() {
        super(ReconstructedTripKey.class, true);
    }

    @Override
    public int compare(WritableComparable key1, WritableComparable key2) {
        return ((ReconstructedTripKey) key1).compareTo((ReconstructedTripKey) key2);
    }
}
