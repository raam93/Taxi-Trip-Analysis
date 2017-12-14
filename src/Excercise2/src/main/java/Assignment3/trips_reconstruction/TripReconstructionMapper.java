package Assignment3.trips_reconstruction;

import Assignment3.helpers.DataExtractor;
import Assignment3.helpers.RideSegment;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.text.ParseException;

/**
* The Mapper Class for trips reconstruction- emits key-ReconstructedTripKey (TaxiId and Start Time), value-RideSegment 
*
* @author  Ramaravind
* @created   30 March 2016
*/
public class TripReconstructionMapper extends Mapper<Object, Text, ReconstructedTripKey, RideSegment> {

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        try {
        
            RideSegment segment = DataExtractor.extractFromRawSegment(value.toString());
           
            if (segment.isAccountable()) { // only considering those trips which are accountable,i.e, when taxi is full
            	ReconstructedTripKey tripKey = new ReconstructedTripKey(segment); 
                context.write(tripKey, segment);
            }
        } catch (ParseException e) {
            System.err.println("Error in " + value.toString()); 
        }
    }
}
