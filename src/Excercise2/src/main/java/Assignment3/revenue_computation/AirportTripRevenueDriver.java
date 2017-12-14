package Assignment3.revenue_computation;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import Assignment3.trips_reconstruction.TripReconstructionDriver;

import java.io.IOException;

/**
* The Driver Class for computing revenue from airport trips
*
* @author  Ramaravind
* @created   30 March 2016
*/
public class AirportTripRevenueDriver {

	public static final String MAPREDUCE_OUTPUT_BASENAME = "Airport_Trips_Revenue";

    public static Job getJob(Class mainClass, String inputFilePath, String outputFilePath, boolean considerOverlappingSegments, boolean gotAirportTripsOnly,int noOfReducers) throws IOException, ClassNotFoundException, InterruptedException {
    
        Configuration conf = new Configuration();
        conf.set("Consider_Overlapping_Segment", "" + considerOverlappingSegments);
        conf.set("Got_Airport_Trips_Only",""+gotAirportTripsOnly);
        
        Job job = Job.getInstance(conf, "airport trip revenue");
        
        FileSystem hdfs = FileSystem.get(conf);
        FileStatus fs[] = hdfs.listStatus(new Path(inputFilePath));
        
        for (FileStatus file : fs) {
            if (file.getPath().getName().startsWith(TripReconstructionDriver.MAPREDUCE_OUTPUT_BASENAME)) {
                MultipleInputs.addInputPath(job, file.getPath(), TextInputFormat.class);
            }
        }

        FileOutputFormat.setOutputPath(job, new Path(outputFilePath));
        job.setJarByClass(mainClass); 
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        
        job.setMapperClass(AirportTripRevenueMapper.class);
        job.setCombinerClass(AirportTripRevenueReducer.class);
        job.setReducerClass(AirportTripRevenueReducer.class);
        
        job.setNumReduceTasks(noOfReducers);
        
        job.getConfiguration().set("mapreduce.output.basename", MAPREDUCE_OUTPUT_BASENAME);
        return job;
    }
}
