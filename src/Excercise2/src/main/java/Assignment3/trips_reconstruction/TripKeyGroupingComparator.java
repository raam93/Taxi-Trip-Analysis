package Assignment3.trips_reconstruction;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class TripKeyGroupingComparator extends WritableComparator {

    public TripKeyGroupingComparator() {
        super(ReconstructedTripKey.class, true);
    }

    @Override
    public int compare(WritableComparable key1, WritableComparable key2) {
        return Long.compare(((ReconstructedTripKey) key1).getTaxiID(),((ReconstructedTripKey) key2).getTaxiID());
    }
}
