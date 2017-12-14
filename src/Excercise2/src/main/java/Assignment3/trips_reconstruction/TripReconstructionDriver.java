package Assignment3.trips_reconstruction;

import Assignment3.helpers.RideSegment;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
* The Driver Class for trips reconstruction 
*
* @author  Ramaravind
* @created   30 March 2016
*/
public class TripReconstructionDriver {

    public static final String MAPREDUCE_OUTPUT_BASENAME = "Reconstructed_Trips";

    public static Job getJob(Class mainClass, String inputFilePath, String outputFilePath, boolean considerOverlappingSegments, boolean airportTripsOnly, int noOfReducers) throws IOException, ClassNotFoundException, InterruptedException {
    
        Configuration conf = new Configuration();
        conf.set("Consider_Overlapping_Segment", "" + considerOverlappingSegments);
		conf.set("Airport_Trips_Only", "" + airportTripsOnly);

        Job job = Job.getInstance(conf, "trip reconstruction"); 
        
        job.setJarByClass(mainClass); 
        job.setOutputValueClass(NullWritable.class);
        job.setOutputKeyClass(Text.class);
        
        job.setReducerClass(TripReconstructionReducer.class);
        job.setMapperClass(TripReconstructionMapper.class);
        
        job.setSortComparatorClass(TripKeySortComparator.class);
        job.setGroupingComparatorClass(TripKeyGroupingComparator.class);
        job.setPartitionerClass(TripKeyPartitioner.class);
        
        job.setMapOutputKeyClass(ReconstructedTripKey.class);
        job.setMapOutputValueClass(RideSegment.class);
        job.setNumReduceTasks(noOfReducers);
        
        job.getConfiguration().set("mapreduce.output.basename", MAPREDUCE_OUTPUT_BASENAME);
        
        FileInputFormat.addInputPath(job, new Path(inputFilePath));
        FileOutputFormat.setOutputPath(job, new Path(outputFilePath));
        
        return job;
    }
}
