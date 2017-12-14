package Assignment3.helpers;

import Assignment3.helpers.Position;
import Assignment3.helpers.DistanceCalculator;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
* A Class to represent a Ride Segment
*
* @author  Ramaravind
* @created   30 March 2016
*/

public class RideSegment implements WritableComparable<RideSegment> {

    protected static final double MAX_AVERAGE_SPEED = 200;
    protected static final double MILLISECONDS_2_HOUR = 3600000;

    protected long taxiID;
    protected Position startPosition;
    protected Position endPosition;
    protected long startTime;
    protected long endTime;
    protected boolean startState;
    protected boolean endState;
    protected DistanceCalculator distanceCalculator = new DistanceCalculator(); 

    public long getTaxiID() {
        return taxiID;
    }
    
    public Position getStartPosition() {
        return startPosition;
    }

    public Position getEndPosition() {
        return endPosition;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean getStartState() {
        return startState;
    }

    public boolean getEndState() {
        return endState;
    }

    public long getDuration() {
        return endTime - startTime;
    }

    public double getDistance() {
        return distanceCalculator.compute(endPosition, startPosition); 
    }
    
    public double getAverageSpeed() {
        if (getDuration() > 0) {
            return getDistance() / (getDuration() / MILLISECONDS_2_HOUR);
        }
        return Double.POSITIVE_INFINITY;
    }

    public boolean isValid() { 
        double averageSpeed = getAverageSpeed();
        return averageSpeed >= 0 && averageSpeed < MAX_AVERAGE_SPEED;
    }  

    public boolean isTaxiGettingEmpty() {   
        return getStartState() && !getEndState();
    }

    public boolean isTaxiGettingFull() {  
        return !getStartState() && getEndState();
    }

    public boolean isRideInProgress() { 
        return getStartState() && getEndState();
    } 

    public boolean isAccountable() {
        return getStartPosition() != null && getEndPosition() != null && (isTaxiGettingFull() || isRideInProgress() || isTaxiGettingEmpty());
    }
    
    public boolean overlapsWith(RideSegment segment) { 
        return Math.max(segment.getStartTime(), getStartTime()) < Math.min(segment.getEndTime(), getEndTime());
    }
 
    public boolean isPreviousSegmentOf(RideSegment segment) {
        return getEndTime() == segment.getStartTime();
    } 
    
    public boolean passesThroughAirport(Position center, double radius) {
        return distanceCalculator.compute(getStartPosition(), center) < radius || distanceCalculator.compute(getEndPosition(), center) < radius;
    } 

    public RideSegment() {
    
        taxiID = -1L;
        startPosition = null;
        endPosition = null;
        startTime = -1L;
        endTime = -1L;
        startState = false;
        endState = false;
    }

    public RideSegment(RideSegment segment) {
    
        taxiID = segment.getTaxiID();
        startPosition = new Position(segment.getStartPosition());
        endPosition = new Position(segment.getEndPosition());
        startTime = segment.getStartTime();
        endTime = segment.getEndTime();
        startState = segment.getStartState();
        endState = segment.getEndState();
    }

    public RideSegment(long taxiID, long startTime, Position startPosition, boolean startState, long endTime, Position endPosition, boolean endState) {
    
        this.taxiID = taxiID;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startState = startState;
        this.endState = endState;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        taxiID = in.readLong();
        startTime = in.readLong();
        startPosition = new Position(in);
        startState = in.readBoolean();
        endTime = in.readLong();
        endPosition = new Position(in);
        endState = in.readBoolean();
    }
    
    public RideSegment(DataInput in) throws IOException {
        readFields(in);
    }
    
    @Override
    public void write(DataOutput out) throws IOException {
    
        out.writeLong(taxiID);
        out.writeLong(startTime);
        startPosition.write(out);
        out.writeBoolean(startState);
        out.writeLong(endTime);
        endPosition.write(out);
        out.writeBoolean(endState);
    }
    
    @Override
    public String toString() {
        return "" + getTaxiID() + ","
                + getStartTime() + ","
                + getStartPosition() + ","
                + getStartState() + ","
                + getEndTime() + ","
                + getEndPosition() + ","
                + getEndState() + ","
                + getDuration() + ","
                + getDistance() + ","
                + getAverageSpeed();
    }
 
    public boolean isSameAs(RideSegment segment) {
        return getStartTime() == segment.getStartTime() && getEndTime() == segment.getEndTime();
    }
 
    //First sort by taxi ID, then by Start Time
    @Override
    public int compareTo(RideSegment segment) {
        return new CompareToBuilder()
                .append(this.getTaxiID(), segment.getTaxiID())
                .append(this.getStartTime(), segment.getStartTime()).toComparison();
    }
}
