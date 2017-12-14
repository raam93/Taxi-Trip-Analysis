package Assignment3;

import Assignment3.revenue_computation.AirportTripRevenueDriver;
import Assignment3.trips_reconstruction.TripReconstructionDriver;

import org.apache.hadoop.mapreduce.Job;
import java.io.IOException;

/**
* The Main Class to reconstruct all trips (or airport trips only) and compute their revenue
*
* @author  Ramaravind
* @created   30 March 2016
* done on 06 April 2016
*/

public class AirportRideRevenueMain {

    public static void main(String args[]) {
    
        if (args.length < 6) {
            throw new RuntimeException("Missing one or many - input file path, output file path, no. of reducers - stage 1, no. of reducers - stage 2, Consider overlapping segments ?, Reconstruct airport trips only ?");
        }
        
        String inputFilePath = args[0];
        String outputFilePath = args[1];
        
        int noOfReducersSt1 = Integer.parseInt(args[2]);
        int noOfReducersSt2 = Integer.parseInt(args[3]);
        
        Boolean considerOverlappingSegments = Boolean.parseBoolean(args[4]);
        Boolean ReconstructAirportTripsOnly = Boolean.parseBoolean(args[5]); 
        
        String pathToReconstructedTrips = outputFilePath + "_trips";
        
        try {
        
            Job job1 = TripReconstructionDriver.getJob(AirportRideRevenueMain.class, inputFilePath, pathToReconstructedTrips, considerOverlappingSegments, ReconstructAirportTripsOnly, noOfReducersSt1);
            
            if (job1.waitForCompletion(true)) {
            
                if (inputFilePath == null) {
                    System.err.println("Input file not found at the specified path!"); 
                    System.exit(1);
                } else {
                    Job job2 = AirportTripRevenueDriver.getJob(AirportRideRevenueMain.class, pathToReconstructedTrips, outputFilePath, considerOverlappingSegments, ReconstructAirportTripsOnly, noOfReducersSt2);
                    System.exit(job2.waitForCompletion(true) ? 0 : 1);
                }
                
            } else {
                System.err.println("Reconstruction of Trips failed..");
                System.exit(1);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
