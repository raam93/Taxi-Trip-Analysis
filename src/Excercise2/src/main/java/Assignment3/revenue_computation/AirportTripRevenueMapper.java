package Assignment3.revenue_computation;

import Assignment3.helpers.AirportTrip;
import Assignment3.helpers.DataExtractor;
import Assignment3.trips_reconstruction.TripReconstructor;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.text.ParseException;

/**
* The Mapper Class for computing revenue from airport trips- emits key: date of the trip, value: trip's revenue
*
* @author  Ramaravind
* @created   30 March 2016
*/
public class AirportTripRevenueMapper extends Mapper<Object, Text, Text, DoubleWritable> {

	double totalRevenue = 0;
	boolean considerOverlappingSegments;
    boolean gotAirportTripsOnly;
    
    public static final Text TOTAL_REVENUE_KEY = new Text("Total Revenue");
    
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
	    Configuration conf = context.getConfiguration();
	    considerOverlappingSegments = Boolean.parseBoolean(conf.get("Consider_Overlapping_Segment"));
        gotAirportTripsOnly = Boolean.parseBoolean(conf.get("Got_Airport_Trips_Only"));
    }

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
    
        try {
            AirportTrip trip = null;
            
            if (gotAirportTripsOnly) {
                trip = DataExtractor.extractFromAirportTrip(value.toString());
            } else {
            	TripReconstructor tripReconstructor = new TripReconstructor(considerOverlappingSegments);
                trip = tripReconstructor.isAirportTrip(DataExtractor.extractFromReconstructedTrip(value.toString()));
            }
            
            if (trip != null) {
                Long startTime = trip.getStartTime();
                
                if (startTime != null) {
                
                    Text Datekey = new Text(DataExtractor.extractDate(startTime)); 
                    totalRevenue += trip.getAirportTripRevenue();
                    DoubleWritable tripValue = new DoubleWritable(trip.getAirportTripRevenue());
                    context.write(Datekey, tripValue);
                }
            }
        } catch (ParseException e) {
            System.err.println("Error in " + value.toString());
            e.printStackTrace();
        }

    }

   @Override 
    protected void cleanup(Context context) throws IOException, InterruptedException {
        context.write(TOTAL_REVENUE_KEY, new DoubleWritable(totalRevenue));
    }
   
}
