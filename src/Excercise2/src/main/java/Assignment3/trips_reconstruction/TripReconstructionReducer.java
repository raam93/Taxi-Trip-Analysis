package Assignment3.trips_reconstruction;

import Assignment3.helpers.RideSegment;
import Assignment3.helpers.ReconstructedTrip;
import Assignment3.helpers.AirportTrip;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
* The Reducer Class for trips reconstruction- all ride segments corressponding to a single taxiId go to the same reducer 
*
* @author  Ramaravind
* @created   30 March 2016
*/
public class TripReconstructionReducer extends Reducer<ReconstructedTripKey, RideSegment, Text, NullWritable> {

	boolean considerOverlappingSegments;
    boolean airportTripsOnly;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        Configuration conf = context.getConfiguration();
        considerOverlappingSegments = Boolean.parseBoolean(conf.get("Consider_Overlapping_Segment"));
        airportTripsOnly = Boolean.parseBoolean(conf.get("Airport_Trips_Only"));
    }

    @Override
    protected void reduce(ReconstructedTripKey key, Iterable<RideSegment> values, Context context) throws IOException, InterruptedException {
    
        TripReconstructor tripReconstructor = new TripReconstructor(considerOverlappingSegments);
        
        for (RideSegment segment : values) {
            
            ReconstructedTrip newTrip = tripReconstructor.constructTripUsing(segment); //returns null if this segment belongs to the same trip
            if (newTrip != null) { 
                
                if (airportTripsOnly) { 
                    
                    AirportTrip newAirportTrip = tripReconstructor.isAirportTrip(newTrip);
                    
                    if (newAirportTrip != null) {
                        context.write(new Text(newAirportTrip.toString()), NullWritable.get());
                    }
                } else {
                    context.write(new Text(newTrip.toString()), NullWritable.get());
                }
            }
        }
        
        //For any remaining trips
        ReconstructedTrip newTrip = tripReconstructor.getReconstructedTrip();
        
        if (newTrip != null) {
            if (airportTripsOnly) { 
                
                AirportTrip newAirportTrip = tripReconstructor.isAirportTrip(newTrip);
                
                if (newAirportTrip != null) {
                    context.write(new Text(newAirportTrip.toString()), NullWritable.get());
                }
            } else {
                context.write(new Text(newTrip.toString()), NullWritable.get());
            }
        }
    }
}
