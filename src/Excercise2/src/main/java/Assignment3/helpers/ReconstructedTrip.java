package Assignment3.helpers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
* A Class to represent a Reconstructed trip as an array of Ride Segments
*
* @author  Ramaravind
* @created   30 March 2016
*/

public class ReconstructedTrip extends RideSegment implements Iterable<RideSegment> { 

    protected long taxiID;
    protected ArrayList<RideSegment> segments;

    @Override
    public long getTaxiID() {
        return taxiID;
    }

    @Override
    public Position getStartPosition() {
        if (segments.isEmpty()) return null;
        return segments.get(0).getStartPosition();
    }

    @Override
    public Position getEndPosition() {
        if (segments.isEmpty()) return null;
        return segments.get(segments.size() - 1).getEndPosition();
    }

    @Override
    public long getStartTime() {
        if (segments.isEmpty()) return -1;
        return segments.get(0).getStartTime();
    }

    @Override
    public long getEndTime() {
        if (segments.isEmpty()) return -1;
        return segments.get(segments.size() - 1).getEndTime();
    }

    // start and end state of an entire reconstructed trip
    @Override
    public boolean getStartState() {
        return true;
    }

    @Override
    public boolean getEndState() {
        return false;
    }

    @Override
    public long getDuration() {
        return getEndTime() - getStartTime();
    }

    // used for debugging with .trips dataset
    @Override
    public double getDistance() {
    
        if (segments.isEmpty()) return 0.0;
        double distance = 0;
        
        for (RideSegment segment : segments) {
            if (segment.isRideInProgress()) {
                distance += segment.getDistance();
            }
        }
        return distance;
    }

    // used for debugging with .trips dataset
    @Override
    public boolean passesThroughAirport(Position center, double radius) {
    
        boolean result = false;
        for (RideSegment segment : segments) {
            if (segment.passesThroughAirport(center, radius)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public RideSegment getFirstSegment() {
        if (segments.isEmpty()) return null;
        return segments.get(0);
    }

    public RideSegment getLastSegment() {
        if (segments.isEmpty()) return null;
        return segments.get(segments.size() - 1);
    }

    public void add(RideSegment segment) {
        segments.add(new RideSegment(segment));
    }
    
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(taxiID);
        out.writeInt(segments.size());
        for (RideSegment segment : segments) {
            segment.write(out);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        taxiID = in.readLong();
        int segsize = in.readInt();
        segments = new ArrayList<>(segsize);
        for (int i = 0; i < segsize; i++) {
            segments.add(new RideSegment(in));
        }
    }

    public ReconstructedTrip(long TaxiID, ArrayList<RideSegment> segments) {
        this.taxiID = TaxiID;
        this.segments = segments;
    }

    public ReconstructedTrip(long pTaxiID) {
        taxiID = pTaxiID;
        segments = new ArrayList<>();
    }

    public ReconstructedTrip(DataInput in) throws IOException {
        readFields(in);
    }

    @Override
    public Iterator<RideSegment> iterator() {
        return segments.iterator();
    }
 
    @Override  
    public String toString() {
        String result = "" + taxiID + " " + segments.size() + " ";
                
        for (int i = 0; i < segments.size(); i++) {
            result += segments.get(i).toString();
            if (i < segments.size() - 1) result += ";";
        }
        return result;
    }
}
