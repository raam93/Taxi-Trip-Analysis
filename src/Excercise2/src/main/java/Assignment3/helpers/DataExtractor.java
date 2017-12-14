package Assignment3.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
* A Class to extract data from other formats
*
* @author  Ramaravind
* @created   30 March 2016
*/
 
public class DataExtractor {

	public static String extractDate(Long timeInMillseconds) {
	 
	    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/San Francisco"));
        calendar.setLenient(false);
        
        calendar.setTimeInMillis(timeInMillseconds);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        return (new SimpleDateFormat("yyyy-MM-dd")).format(calendar.getTime());        
	}

    public static RideSegment extractFromRawSegment(String segment) throws ParseException {
    
        String[] data = segment.split(",");
        
        if (data.length != 9) {
            throw new ParseException("Error - Missing information or incorrect format for raw segment", 0);
        }
        
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        time.setTimeZone(TimeZone.getTimeZone("America/San Francisco"));
        
        boolean startSt = data[4].replaceAll("^'|'$", "").equals("M");
        boolean endSt = data[8].replaceAll("^'|'$", "").equals("M");
        
        return new RideSegment(
                Long.parseLong(data[0]),
                time.parse(data[1].replaceAll("^'|'$", "")).getTime(),
                new Position(Double.parseDouble(data[2]), Double.parseDouble(data[3])),
                startSt,
                time.parse(data[5].replaceAll("^'|'$", "")).getTime(),
                new Position(Double.parseDouble(data[6]), Double.parseDouble(data[7])),
                endSt
        );
    }

    public static AirportTrip extractFromAirportTrip(String trip) throws ParseException {  
    
        String[] data = trip.split(",");
        
        if (data.length != 13) {  
            throw new ParseException("Error - Missing information or incorrect format for airport trip", 0);
        }
        
        Long taxiID = Long.parseLong(data[0]);
        Long startTime = Long.parseLong(data[1]);
        Position startPosition = new Position(Double.parseDouble(data[2]), Double.parseDouble(data[3]));
        Long endTime = Long.parseLong(data[5]);
        Position endPosition = new Position(Double.parseDouble(data[6]), Double.parseDouble(data[7]));
        Double distance = Double.parseDouble(data[10]); 
        
        return new AirportTrip(taxiID, startTime, startPosition, endTime, endPosition, distance);
    }

    public static ReconstructedTrip extractFromReconstructedTrip(String trip) throws ParseException { 
    
        String[] tripData = trip.split(" ");
        
        if (tripData.length != 3) {
            throw new ParseException("Error - Missing information or incorrect format for reconstructed trip", 0);
        }
        
        Long taxiID = Long.parseLong(tripData[0]);
        Long NoOfSegments = Long.parseLong(tripData[1]);
        String[] segments = tripData[2].split(";"); // used ';' as a delimiter after each ride segment
        
        if (segments.length != NoOfSegments) {
            throw new ParseException("Error - wrong number of segments", 0);
        }
        
        ReconstructedTrip reconstructedTrip = new ReconstructedTrip(taxiID);
        
        for (int i = 0; i < segments.length; i++) {
        
            String[] data = segments[i].split(",");
            
            if (data.length != 12) {
                throw new ParseException("Error - Missing information or incorrect format for reconstructed trip", 0);
            }
            
            Long startTime = Long.parseLong(data[1]);
            Position startPosition = new Position(Double.parseDouble(data[2]), Double.parseDouble(data[3]));
            Boolean startState = Boolean.parseBoolean(data[4]);
            Long endTime = Long.parseLong(data[5]);
            Position endPosition = new Position(Double.parseDouble(data[6]), Double.parseDouble(data[7]));
            Boolean endState = Boolean.parseBoolean(data[8]);
            
            reconstructedTrip.add(new RideSegment(taxiID, startTime, startPosition, startState, endTime, endPosition, endState));
        }
        return reconstructedTrip;
    }

 
}
