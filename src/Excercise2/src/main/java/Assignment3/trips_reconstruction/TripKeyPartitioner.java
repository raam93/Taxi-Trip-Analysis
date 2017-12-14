package Assignment3.trips_reconstruction;

import Assignment3.helpers.RideSegment;
import org.apache.hadoop.mapreduce.Partitioner;

public class TripKeyPartitioner extends Partitioner<ReconstructedTripKey, RideSegment> {

    @Override
    public int getPartition(ReconstructedTripKey tripKey, RideSegment segment, int noOfPartitions) {
        return new Long(tripKey.getTaxiID()).hashCode() % noOfPartitions;
    }
}
