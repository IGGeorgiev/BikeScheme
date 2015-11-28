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
     * Dummy implementation of docking station functionality for 
     * "register user" use case.
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
  
    //======================HANDLES ADD USER REQUESTS=========================
    
    private AddUserObserver addUserObserver;
    
    public void setAddUserObserver(AddUserObserver o){
        addUserObserver = o;
    }
    
    //Bike-User actions observers:-----------------------------------------------------------
    private ActionsForBikeAndUserObserver  removeBikeObserver ;
    
    public void setRemoveBikeObserver(ActionsForBikeAndUserObserver o){
        removeBikeObserver = o;
    }
    
    public void viewActivityRecieved(){
        //TODO
    }
    
    @Override
    public void disassociateBikeFromUser(String bikeId) {
        // TODO Auto-generated method stub
        logger.fine(getInstanceName());
        removeBikeObserver.returnBike(bikeId);
    }
 

}
