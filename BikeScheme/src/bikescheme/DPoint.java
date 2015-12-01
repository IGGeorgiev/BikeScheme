/**
 * 
 */
package bikescheme;

import java.util.Date;
import java.util.logging.Logger;

/**
 *  
 * Docking Point for a Docking Station.
 * 
 * @author pbj
 *
 */
public class DPoint implements KeyInsertionObserver, 
                               BikeDockingObserver,
                               FaultButtonObserver{
    public static final Logger logger = Logger.getLogger("bikescheme");
    private String bikeId;
    private BikeSensor bikeSensor;
    private BikeLock bikeLock;
    private KeyReader keyReader; 
    private OKLight okLight;
    private FaultLight faultLight;
    private FaultButton faultButton;
    private String instanceName;
    private int index;
    private Date bikeDocked;
    private boolean hasFaultyBike;
    /**
     * 
     * Construct a Docking Point object with a key reader and green ok light
     * interface devices.
     * 
     * @param instanceName a globally unique name
     * @param index of reference to this docking point  in owning DStation's
     *  list of its docking points.
     */
    public DPoint(String instanceName, int index) {

     // Construct and make connections with interface devices
        keyReader = new KeyReader(instanceName + ".kr");
        keyReader.setObserver(this);
        okLight = new OKLight(instanceName + ".ok");
        bikeLock = new BikeLock(instanceName+".bl");
        bikeSensor = new BikeSensor(instanceName+".bs");
        bikeSensor.setBikeDockingObserver(this);
        faultButton = new FaultButton(instanceName+".fb");
        faultButton.setFaultButtonObserver(this);
        faultLight = new FaultLight(instanceName+".fl");
        bikeId = "";
        this.instanceName = instanceName;
        this.index = index;
        this.bikeDocked = null;
        hasFaultyBike = false;
    }
    
    public void setDistributor(EventDistributor d) {
        keyReader.addDistributorLinks(d); 
        bikeSensor.addDistributorLinks(d);
        faultButton.addDistributorLinks(d);
    }
    
    public void setCollector(EventCollector c) {
        okLight.setCollector(c);
        faultLight.setCollector(c);
        bikeLock.setCollector(c);
        
    }
    
    public String getInstanceName() {
        return instanceName;
    }
    public int getIndex() {
        return index;
    }
    public boolean hasFaultyBike(){
        return this.hasFaultyBike;
    }
   //=========CODE FOR HANDLING HIRE BIKE USE-CASE=========
   private DPointObserver keyInserted;
   public void setKeyInsertedObserver(DPointObserver o){
       keyInserted = o;
   }
    /** 
     * Implementation of the key insertion at a DPoint:
     * Associate User with the Bike.
     * Unlock the bike.
     * Flash the OK light.
     * Flash Fault Light if something goes wrong along the way
     *
     */
    public void keyInserted(String keyId) {
        logger.fine(getInstanceName());
        if(!bikeId.equals("")&& !faultLight.isOn()){
            boolean shouldContinue = keyInserted.associateBikeToUser(keyId, this.bikeId);
            if(shouldContinue && (!hasFaultyBike || keyId.substring(0, 12).equals("CycleOps.Hub"))){
                bikeLock.unlock();
                bikeId = "";
                okLight.flash();
                hasFaultyBike = false;
            }else{
                faultLight.flash();
            }
        }else{
            okLight.flash();
        }
        
            
    }
    //=========CODE FOR HANDLING RETURN BIKE AND ADD BIKE USE-CASE=========
    private DPointObserver dPointObserver;
    
    public void setDPointObserver(DPointObserver o){
        dPointObserver = o;
    }
    
    @Override
    public void bikeDocked(String bikeId) {
        // TODO Auto-generated method stub
        logger.fine(getInstanceName());
        dPointObserver.disassociateBikeFromUser(bikeId);
        bikeLock.lock();
        this.bikeDocked = Clock.getInstance().getDateAndTime();
        this.bikeId = bikeId;
        logger.fine(getInstanceName()+" bikeId is : "+this.bikeId);
        okLight.flash();
    }
    //=========CODE FOR HANDLING FAULTY BIKE USE-CASE=========
    @Override
    public void onPress() {
        Date pressed = Clock.getInstance().getDateAndTime();
        int minutes = Clock.minutesBetween(this.bikeDocked, pressed);
        if(minutes <= 2){
            this.faultLight.turnOn();
            this.hasFaultyBike = true;
            dPointObserver.reportBikeFaulty(this.bikeId);
        }
    }
    
 
}
