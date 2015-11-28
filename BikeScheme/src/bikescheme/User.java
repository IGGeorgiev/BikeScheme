package bikescheme;

import java.util.Date;
import java.util.List;

public class User {
    
    private String personalDetails;
    private String cardDetails;
    private String keyId;
    private Date startDate;
    private List<Trip> trips;
    
    
    
    public User(String keyId,String personalDetails,String cardDetails){
        this.setKeyId(keyId);
        this.setCardDetails(cardDetails);
        this.setPersonalDetails(personalDetails);
    }

    public String getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(String cardDetails) {
        this.cardDetails = cardDetails;
    }

    public String getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(String personalDetails) {
        this.personalDetails = personalDetails;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
    
    public void startUsage(Date date){
        this.startDate = date;
    }
    
    public void endUsage(Date date){
        Trip tr = new Trip(startDate, date);
        trips.add(tr);
    }
    
}
