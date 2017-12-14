package Assignment3.helpers;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
* A Class to represent the position of a taxi in latitude and longitude
*
* @author  Ramaravind
* @created   30 March 2016
*/
public class Position implements Writable {

    protected double latitude;
    protected double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Position(double latitude, double longitude) {
        this.latitude= latitude;
        this.longitude= longitude;
    }

    public Position(Position position){
        this.latitude= position.getLatitude();
        this.longitude= position.getLongitude();
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        latitude= in.readDouble();
        longitude= in.readDouble();
    }
    
    public Position(DataInput in) throws IOException {
        readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(latitude);
        out.writeDouble(longitude);
    }

    @Override
    public String toString() {
        return "" + latitude+ "," + longitude;
    }
}
