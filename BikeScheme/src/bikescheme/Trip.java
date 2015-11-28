package bikescheme;

import java.util.Date;

public class Trip {
    
    private Date startDate;
    private Date endDate;
    private int price;
    
    public Trip(Date startDate, Date endDate){
        this.startDate = startDate;
        this.endDate = endDate;
        int minutes = Clock.minutesBetween(startDate, endDate);
        if (minutes % 30 == 0) this.price = 1;
        else this.price = 1 + (minutes % 30)*2;
    }
    
    public int getPrice(){
        return price;
    }
    
    public Date getStartDate(){
        return startDate;
    }
    
    public Date getEndDate(){
        return endDate;
    }
}
