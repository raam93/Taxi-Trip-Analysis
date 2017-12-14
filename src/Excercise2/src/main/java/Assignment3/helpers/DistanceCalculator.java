package Assignment3.helpers;

/**
* A Class to calculate distance between two Positions
*
* @author  Ramaravind
* @created   30 March 2016
*/

public class DistanceCalculator {

	protected static final double DEG_2_RAD = Math.PI / 180.0;
    protected static final double EARTH_RADIUS_KM = 6371.008;
    
    
    public double EllipticalFlatEarthDistance(Position p1, Position p2) {
    
        double delLat = p2.getLatitude() - p1.getLatitude();
        double delLong = p2.getLongitude() - p1.getLongitude();
        
        double phiM = DEG_2_RAD * ((p1.getLatitude() + p2.getLatitude()) / 2.0);
        double K1 = 111.13209 - 0.56605 * Math.cos(2 * phiM) + 0.0012 * Math.cos(4 * phiM);
        double K2 = 111.41513 * Math.cos(phiM) - 0.09455 * Math.cos(3 * phiM) + 0.00012 * Math.cos(5 * phiM);
        
        double d1 = K1 * delLat;
        double d2 = K2 * delLong;
        
        return Math.sqrt((d1 * d1) + (d2 * d2));
    }

    public double SphericalFlatEarthDistance(Position p1, Position p2) {
    
        double delLat = p1.getLatitude() - p2.getLatitude();
        double delLong = p1.getLongitude() - p2.getLongitude();
        
        double phiM = DEG_2_RAD * ((p1.getLatitude() + p2.getLatitude()) / 2.0);
        double t1 = DEG_2_RAD * delLat;
        double t2 = Math.cos(phiM) * delLong;
        
        return EARTH_RADIUS_KM * Math.sqrt(t1 * t1 + t2 * t2);
    }
 
    public double PolarFlatEarthDistance(Position p1, Position p2) {
    
        double delLong = p1.getLongitude() - p2.getLongitude();
        double theta1Rad = DEG_2_RAD * (90 - p1.getLatitude());
        double theta2Rad = DEG_2_RAD * (90 - p2.getLatitude());
        
        return EARTH_RADIUS_KM * Math.sqrt(theta1Rad * theta1Rad + theta2Rad * theta2Rad + 2 * theta1Rad * theta2Rad * Math.cos(DEG_2_RAD * delLong));
    }

    // used  Elliptical Flat Earth Distance formula to compute distance as the sampling rate is approx 1 minute mostly
    // other distance calculations didn't give desired results
	public double compute(Position p1, Position p2) {		
		return EllipticalFlatEarthDistance(p1, p2); 
	}

}
