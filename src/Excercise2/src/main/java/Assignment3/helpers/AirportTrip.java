package Assignment3.helpers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
* A Class to represent an Airport trip separately
*
* @author  Ramaravind
* @created   30 March 2016
*/
public class AirportTrip extends RideSegment {

    protected static final double COST_PER_KM = 1.71;
    protected static final double STARTING_FEE = 3.5;
    public static final Position AIRPORT_CENTER = new Position(37.62131, -122.37896);
    public static final double AIRPORT_RADIUS = 1; //approx
    public double distance;

    public AirportTrip() {
        super();
        distance = 0.0;
    }
    
    public AirportTrip(DataInput in) throws IOException {
        super(in);
        distance = in.readDouble();
    }

    public AirportTrip(long TaxiID, long startTime, Position startPosition, long endTime, Position endPosition, double distance) {
        super(TaxiID, startTime, startPosition, true, endTime, endPosition, true);
        this.distance = distance;
    }

    public double getAirportTripRevenue() {
        if (getDistance() > 0) {
            return STARTING_FEE + getDistance() * COST_PER_KM;
        }
        return 0;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
        out.writeDouble(distance);
    }
    
    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return super.toString() + "," + getAirportTripRevenue();
    }
}
