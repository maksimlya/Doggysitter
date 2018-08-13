package afeka.com.doggysitter.ListViews;

import android.support.annotation.NonNull;

import java.util.Objects;

public class DoggysitterInstance implements Comparable<DoggysitterInstance> {
    private int month;
    private int day;
    private int startHour;
    private int endHour;

    public DoggysitterInstance(){
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoggysitterInstance that = (DoggysitterInstance) o;
        return month == that.month &&
                day == that.day &&
                startHour == that.startHour &&
                endHour == that.endHour;
    }

    @Override
    public int hashCode() {

        return Objects.hash(month, day, startHour, endHour);
    }

    @Override
    public int compareTo(@NonNull DoggysitterInstance o) {
        if(this.month < o.month)
            return -1;
        else
            if(this.month > o.month)
                return 1;
        else if(this.day < o.day)
            return -1;
        else if(this.day > o.day)
            return 1;
        else if(this.startHour < o.startHour)
            return -1;
        else return 1;


    }
}
