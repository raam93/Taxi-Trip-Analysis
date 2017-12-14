package Assignment3.trips_reconstruction;

import Assignment3.helpers.AirportTrip;
import Assignment3.helpers.Position;
import Assignment3.helpers.RideSegment;
import Assignment3.helpers.ReconstructedTrip;
import Assignment3.helpers.DistanceCalculator;

/**
* The Class which actually constructs a trip from given segments after removing any erroneous segments.  
* 
* A segment belongs to a trip if
* 
* 1. It has the same taxiID as the previous
* 
* 2. It's start time is equal to the end time of the previous segment (or)
*    It's start time is equal to the end time of a segment with latest end time
* 
* 3. It overlaps with the previous segment (or)
*    It overlaps with a segment with latest end time
*
* @author  Ramaravind
* @created   30 March 2016
*/
public class TripReconstructor {

    protected ReconstructedTrip currentTrip; 
    protected RideSegment latestEndTimeSegment; 
    protected RideSegment previousSegment; 
    protected boolean considerOverlappingSegments;

    public TripReconstructor(boolean overlappingSegments) {
    	considerOverlappingSegments = overlappingSegments;
        currentTrip = null;
        latestEndTimeSegment = new RideSegment(-1, -1, new Position(0.0, 0.0), false, -1, new Position(0.0, 0.0), false);
        previousSegment = new RideSegment(-1, -1, new Position(0.0, 0.0), false, -1, new Position(0.0, 0.0), false);
    }
    
    public boolean isPartOfCurrentTrip(RideSegment currentSegment) { 
    
        if (currentSegment == null) {
            return false;
        }
        if (currentSegment.getTaxiID() != previousSegment.getTaxiID()) {
            return false;
        }
        boolean isPreviousSegment = previousSegment.isPreviousSegmentOf(currentSegment);
        boolean isLatestPreviousSegment = latestEndTimeSegment.isPreviousSegmentOf(currentSegment);
        
        boolean isPreviousOverlapping = previousSegment.overlapsWith(currentSegment);
        boolean isLatestEndTimeSegmentOverlapping = latestEndTimeSegment.overlapsWith(currentSegment);
         
        return (isPreviousSegment || isLatestPreviousSegment || isPreviousOverlapping || isLatestEndTimeSegmentOverlapping);
    }

    
    //Current trip is returned if the current segment is not a part of it and a new trip is created.
    public ReconstructedTrip constructTripUsing(RideSegment currentSegment) {
    
        ReconstructedTrip previousTrip = null;
        
        if (!previousSegment.isSameAs(currentSegment)) {
        
            if ((currentSegment.isTaxiGettingFull() || previousSegment.isTaxiGettingEmpty() || !isPartOfCurrentTrip(currentSegment)) ) {
            	
                previousTrip = currentTrip;
                currentTrip = new ReconstructedTrip(currentSegment.getTaxiID());
                latestEndTimeSegment = new RideSegment(currentSegment.getTaxiID(), -1, new Position(0.0, 0.0), false, -1, new Position(0.0, 0.0), false);
            }
            
            currentTrip.add(currentSegment);
            previousSegment = new RideSegment(currentSegment);
            
            if (currentSegment.getEndTime() >= latestEndTimeSegment.getEndTime())
                latestEndTimeSegment = new RideSegment(currentSegment);
           
        }
        
        return previousTrip;
    }

    public ReconstructedTrip getReconstructedTrip() {
        return currentTrip;
    }
    
    
    // Checks if a reconstructed trip is an airport trip and returns null if not
    public AirportTrip isAirportTrip(ReconstructedTrip currentTrip) {
    
        boolean passesThroughAirport = false;
        boolean containsInvalidSegment = false;
        boolean ignoreOverlappingSegments = false;

        if (currentTrip != null) {
        
            RideSegment firstSegment = currentTrip.getFirstSegment();
            RideSegment lastSegment = currentTrip.getLastSegment();
           
            DistanceCalculator distanceCalculator = new DistanceCalculator();
            
            if (firstSegment != null && lastSegment != null) {
            
                if (!firstSegment.getStartState() && firstSegment.getEndState() && lastSegment.getStartState() && !lastSegment.getEndState()) { 
                
                    RideSegment previousSegment = new RideSegment(-1, -1, null, false, -1, null, false);
                    double distance = 0.0;
                    
                    for (RideSegment segment : currentTrip) {
                        
                    	if (!segment.isValid()) {
                            containsInvalidSegment = true;
                            break;
                        }
                    	
                    	if (previousSegment.overlapsWith(segment)) {  // subtracting overlapping distance
                    	
                     		if(considerOverlappingSegments) { 
                     		
		                		if(previousSegment.getStartTime() < segment.getStartTime())
		                			 distance -= distanceCalculator.compute(previousSegment.getEndPosition(), segment.getStartPosition());
		                		else
		                			 distance -= distanceCalculator.compute(segment.getEndPosition(), previousSegment.getStartPosition());
		                	}
		                	else {
		                		ignoreOverlappingSegments = true;
		                		break;
		                	}				 
                        }
                         
                        if (segment.passesThroughAirport(AirportTrip.AIRPORT_CENTER, AirportTrip.AIRPORT_RADIUS)) {
                            passesThroughAirport = true;
                        }
                         
                         distance += segment.getDistance();
                                                
                        previousSegment = segment; 
                    }
                    
                    if (passesThroughAirport && !containsInvalidSegment && !ignoreOverlappingSegments) {
                    
                        return new AirportTrip(
                                currentTrip.getTaxiID(),
                                currentTrip.getStartTime(),
                                currentTrip.getStartPosition(),
                                currentTrip.getEndTime(),
                                currentTrip.getEndPosition(),
                                distance);
                    }
                }
            }
        }
        return null;
    }
}
