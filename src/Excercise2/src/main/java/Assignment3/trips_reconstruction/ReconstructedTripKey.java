package Assignment3.trips_reconstruction;

import Assignment3.helpers.RideSegment;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
* A Class represent the composite key (taxiID and start time) of a reconstructed trip or a ride segment 
*
* @author  Ramaravind
* @created   30 March 2016
*/

public class ReconstructedTripKey implements WritableComparable<ReconstructedTripKey> {

    protected long taxiID;
    protected long startTime;

    public long getTaxiID() {
        return taxiID;
    }

    public long getStartTime() {
        return startTime;
    }

    public ReconstructedTripKey() {
        taxiID = -1L;
        startTime = -1L;
    }
    
    public ReconstructedTripKey(RideSegment segment) {
        taxiID = segment.getTaxiID();
        startTime = segment.getStartTime();
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        taxiID = in.readLong();
        startTime = in.readLong();
    }
    
    public ReconstructedTripKey(DataInput in) throws IOException {
        readFields(in);
    }
    
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(taxiID);
        out.writeLong(startTime);
    }
    
    @Override
    public int compareTo(ReconstructedTripKey tripKey) {
        return new CompareToBuilder()
                .append(this.getTaxiID(), tripKey.getTaxiID())
                .append(this.getStartTime(), tripKey.getStartTime()).toComparison();
    }
    
}
