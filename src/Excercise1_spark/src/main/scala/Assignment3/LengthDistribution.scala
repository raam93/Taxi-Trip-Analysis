package Assignment3

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object LengthDistribution {

  def main(args: Array[String]) {
    
	val inputFile = args(0)

	val outputPath = args(1)

	val noOfBins = args(2)

	val findOutlier = args(3)

	val sc = new SparkContext()

	val tripsRDD = sc.textFile(inputFile) 

	val allFlatDist = tripsRDD.map { line => { 
									  val args = line.split(" "); 

									  val delLat = args(5).toFloat - args(2).toFloat;

									  val delLong = args(6).toFloat - args(3).toFloat; 

									  val phiM = Math.toRadians( (args(2).toFloat + args(5).toFloat )/ 2.0 );

									  val K1 = 111.13209 - 0.56605 * Math.cos(2 * phiM) + 0.0012 * Math.cos(4 * phiM);

									  val K2 = 111.41513 * Math.cos(phiM) - 0.09455 * Math.cos(3 * phiM) + 0.00012 * Math.cos(5 * phiM);

									  val d1 = K1 *delLat;

									  val d2 = K2 *delLong;

									  val distance = Math.sqrt( (d1 * d1) + (d2 * d2) );

									  if( findOutlier == "true") {
							
										  val time = ( args(4).toFloat - args(1).toFloat ) / 3600.0; 

										  val speed = distance / time; 
                                    
									      if( speed < 200.0 ) distance else -1 
 
									  } else distance
			
									} }	
									
	val flatDist = allFlatDist.filter( dist => dist > -1)

	flatDist.cache()

	val maxDist = flatDist.max

	val factorToFindBinIndex = (noOfBins.toInt)/ (maxDist.toFloat)	

	val histogram = flatDist.map { dist => {

									val binIndex = Math.min( (Math.floor(dist * factorToFindBinIndex)).toInt, (noOfBins.toInt - 1) );

									val key = binIndex / factorToFindBinIndex;

									( key, 1 )  } }

									.reduceByKey(_ + _)  


	histogram.coalesce(1).saveAsTextFile(outputPath)

  }
}


