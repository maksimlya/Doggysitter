package afeka.com.doggysitter.ListViews;

import android.location.Location;
import com.firebase.geofire.GeoLocation;
import java.util.Objects;

public class DoggysitterOffer {
    private String name;
    private String phoneNumber;
    private String address;
    private int month;
    private int day;
    private int startHour;
    private int endHour;
    private GeoLocation targetLocation;
    private GeoLocation myHomeLocation;

    public DoggysitterOffer(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMonth(int month) {
        this.month = month;
    }


    public void setDay(int day) {
        this.day = day;
    }


    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public String getDate(){
        return String.valueOf(this.day) +
                '/' +
                month +
                '/' +
                2018;
    }

    public String getHours(){
        return "From " +
                this.startHour + ":00" +
                " To " +
                this.endHour + ":00";
    }

    public Double getDistanceToDoggysitter(){
        Location doggysitterLocation = new Location("");
        Location myLocation = new Location("");
        doggysitterLocation.setLatitude(targetLocation.latitude);
        doggysitterLocation.setLongitude(targetLocation.longitude);
        myLocation.setLatitude(myHomeLocation.latitude);
        myLocation.setLongitude(myHomeLocation.longitude);


        float distance = doggysitterLocation.distanceTo(myLocation);
        return ((int)(distance/10))/100.0;



    }

    public void setTargetLocation(GeoLocation location) {
        this.targetLocation = location;
    }
    public void setHomeLocation(GeoLocation location) {
        this.myHomeLocation = location;
    }

    public GeoLocation getLocation() {
        return targetLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoggysitterOffer that = (DoggysitterOffer) o;
        return month == that.month &&
                day == that.day &&
                startHour == that.startHour &&
                endHour == that.endHour &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, month, day, startHour, endHour);
    }
}
