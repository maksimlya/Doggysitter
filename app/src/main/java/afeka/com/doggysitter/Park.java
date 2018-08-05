package afeka.com.doggysitter;

import android.location.Location;
import android.support.annotation.NonNull;

import com.firebase.geofire.GeoLocation;

public class Park implements Comparable<Park> {
    private String name;
    private int dogsAmount;
    private GeoLocation location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        int maxLength = (name.length() < 20)?name.length():20;
        this.name = name.substring(0,maxLength);
    }

    public int getDogsAmount() {
        return dogsAmount;
    }

    public void setDogsAmount(int dogsAmount) {
        this.dogsAmount = dogsAmount;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public Double getDistanceToPark(){
        Location parkLocation = new Location("");
        Location myLocation = new Location("");
        parkLocation.setLatitude(location.latitude);
        parkLocation.setLongitude(location.longitude);
        myLocation.setLatitude(MainActivity.MY_LOCATION.latitude);
        myLocation.setLongitude(MainActivity.MY_LOCATION.longitude);


        float distance = parkLocation.distanceTo(myLocation);
        return ((int)(distance/10))/100.0;



    }


    @Override
    public int compareTo(@NonNull Park o) {
        if(getDistanceToPark() < o.getDistanceToPark())
            return -1;
        else return 1;
    }
}
