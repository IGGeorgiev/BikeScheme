package bikescheme;

import java.util.Date;

/**
 * This class aims to create an easily accessible container for
 * all required Trip information. Its constructor consisting of
 * start and end Date also calculates the full duration and 
 * price of the given Trip.
 * 
 * Getters and setters for the start and end station are also included.
 * NOTE: getters for price and duration return values of type String !
 * @author iggeorgiev
 */

public class Trip {
    
    private int duration;
    private int price;
    private String startStation;
    private String endStation;
    private String startTime;
    
    public Trip(Date startDate, Date endDate){
        this.startTime = startDate.toString();
        this.duration = Clock.minutesBetween(startDate, endDate);
        if (duration % 30 == 0) this.price = 1;
        else this.price = 1 + (duration % 30)*2;
    }
    
    public int getPrice(){
        return price;
    }
    
    public String getStartTime(){
        return startTime;
    }
    
    public String getDuration(){
        return Integer.toString(duration);
    }
    
    public void setStartStation(String s){
        this.startStation = s;
    }
    
    public void setEndStation(String s){
        this.endStation = s;
    }
    
    public String getStartStation(){
        return startStation;
    }
    
    public String getEndStation(){
        return endStation;
    }
}
