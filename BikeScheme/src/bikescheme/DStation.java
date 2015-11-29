/**
 * 
 */
package bikescheme;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *  
 * Docking Station.
 * 
 * @author pbj
 *
 */
public class DStation implements StartRegObserver, DPointObserver, ViewActivityObserver {
    public static final Logger logger = Logger.getLogger("bikescheme");

    private String instanceName;
    private int eastPos;
    private int northPos;
    private int freePoints;
    
    private DSTouchScreen touchScreen;
    private CardReader cardReader; 
    private KeyIssuer keyIssuer;
    private List<DPoint> dockingPoints;
         
 
    /**
     * 
     * Construct a Docking Station object with touch screen, card reader
     * and key issuer interface devices and a connection to a number of
     * docking points.
     * 
     * If the instance name is <foo>, then the Docking Points are named
     * <foo>.1 ... <foo>.<numPoints> . 
     * 
     * @param instanceName
     */
    public DStation(
            String instanceName,
            int eastPos,
            int northPos,
            int numPoints) {
        
     // Construct and make connections with interface devices
        
        this.instanceName = instanceName;
        this.eastPos = eastPos;
        this.northPos = northPos;
        
        touchScreen = new DSTouchScreen(instanceName + ".ts");
        touchScreen.setStartRegObserver(this);
        touchScreen.setViewActivityObserver(this);
        
        cardReader = new CardReader(instanceName + ".cr");
        
        keyIssuer = new KeyIssuer(instanceName + ".ki");
                
        freePoints = numPoints;
        
        dockingPoints = new ArrayList<DPoint>();
        
        for (int i = 1; i <= numPoints; i++) {
            DPoint dp = new DPoint(instanceName + "." + i, i - 1);
            dockingPoints.add(dp);
            dp.setDPointObserver(this); 
        }
    }
       
    void setDistributor(EventDistributor d) {
        touchScreen.addDistributorLinks(d); 
        cardReader.addDistributorLinks(d);
        for (DPoint dp : dockingPoints) {
            dp.setDistributor(d);
        }
    }
    
    void setCollector(EventCollector c) {
        touchScreen.setCollector(c);
        cardReader.setCollector(c);
        keyIssuer.setCollector(c);
        for (DPoint dp : dockingPoints) {
            dp.setCollector(c);
        }
    }
    
    /** 
     * 
     * Method called on docking station receiving a "start registration"
     * triggering input event at the touch screen.
     * 
     * @param personalInfo
     */
    public void startRegReceived(String personalInfo) {
        logger.fine("Starting on instance " + getInstanceName());
        
        cardReader.requestCard();  // Generate output event
        logger.fine("Card requested at " + getInstanceName());
        
        String cardDetails = cardReader.checkCard();    // Pull in non-triggering input event
        logger.fine("Card read at " + getInstanceName());
        
        String keyId = keyIssuer.issueKey(); // Generate output event
        
        addUserObserver.addUser(keyId, personalInfo, cardDetails);
    }
    
    public String getInstanceName() {
        return instanceName;
    }
    
    public int getEastPos() {
        return eastPos;
    }
    
    public int getNorthPos() {
        return northPos;
    }
    
    public int getNumberOfPoints(){
        return dockingPoints.size();
    }
    
    public int getFreePoints(){
        return freePoints;
    }
    
    public String getStatus(){
        double occupancyRatio = (double)(getNumberOfPoints()-getFreePoints())/getNumberOfPoints();
        if     (occupancyRatio>=0.85){return "HIGH";}
        else if(occupancyRatio<=0.15){return "LOW";}
        else{return "OK";}
        
    }
    //======================HANDLES ADD USER REQUESTS=========================
    
    private AddUserObserver addUserObserver;
    
    public void setAddUserObserver(AddUserObserver o){
        addUserObserver = o;
    }
    //=========CODE FOR HANDLING RETURN BIKE AND ADD BIKE USE-CASE=========
    private ActionsForBikeAndUserObserver removeBikeObserver ;

    public void setRemoveBikeObserver(ActionsForBikeAndUserObserver o){
        removeBikeObserver = o;
    }
    
    @Override
    public void disassociateBikeFromUser(String bikeId) {
        logger.fine(getInstanceName());
        removeBikeObserver.returnBike(bikeId, this.getInstanceName());
        freePoints--;
        //TODO add code for occupancy checking here
    }
    
    //=========CODE FOR HANDLING HIRE BIKE USE-CASE=========
    private ActionsForBikeAndUserObserver addBikeObserver;
    public void setAddBikeObserver(ActionsForBikeAndUserObserver o){
        addBikeObserver = o;
    }
    
    @Override
    public void associateBikeToUser(String bikeId, String keyId) {
        logger.fine(getInstanceName());
        addBikeObserver.addBike(bikeId, keyId, this.getInstanceName());
        freePoints++;
    }
    
    //==========CODE FOR HANDLING VIEW ACTIVITY USE CASE=========
    
    private ViewActivityObserver viewActivityObserver;
    
    public void setViewActivityObserver(ViewActivityObserver o){
        viewActivityObserver = o;
    }
    
    @Override
    public List<String> viewActivityReceived(String keyId) {
        logger.fine(getInstanceName());
        
        return viewActivityObserver.viewActivityReceived(keyId);
    }
 

}
