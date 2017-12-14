
import java.io._
import scala.io.Source
import scala.collection.mutable.ListBuffer

object LengthDistributionLocal {

  def main(args: Array[String]) {
    
	val t1 = System.nanoTime

	val inputFile = args(0)

	val outputFile = args(1)

	val noOfBins = args(2)

	val findOutlier = args(3)

	val trips = Source.fromFile(inputFile).getLines

	var distances = new ListBuffer[Double]()

	for (line <- Source.fromFile(inputFile).getLines) {

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
        
		      if( speed < 200.0 ) distances += distance else distances += -1 

		  } else distances += distance
			
	}	
				
	val allFlatDist = distances.toList
					
	val flatDist = allFlatDist.filter( dist => dist > -1)

	val maxDist = flatDist reduce {(x,y) => if(x > y) x else y}

	val factorToFindBinIndex = (noOfBins.toInt)/ (maxDist.toFloat)

	val histogram1 = flatDist.map { dist => {

									val binIndex = Math.min( (Math.floor(dist * factorToFindBinIndex)).toInt, (noOfBins.toInt - 1) );

									val key = binIndex / factorToFindBinIndex;

									( key, 1 )  } }


	val histogram2 = histogram1.groupBy(_._1).map(dist => (dist._1, dist._2.map(_._2).reduce(_+_)))

	val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)))

	for (x <- histogram2) {
	  writer.write(x + "\n") 
	}

	writer.close()

	val duration = (System.nanoTime - t1) / 1e9d

	println("Total time taken : " + duration)

  }
}


