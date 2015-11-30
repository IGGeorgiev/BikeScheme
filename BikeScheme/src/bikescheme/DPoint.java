/**
 * 
 */
package bikescheme;

import java.util.logging.Logger;

/**
 *  
 * Docking Point for a Docking Station.
 * 
 * @author pbj
 *
 */
public class DPoint implements KeyInsertionObserver, BikeDockingObserver {
    public static final Logger logger = Logger.getLogger("bikescheme");
    private String bikeId;
    private BikeSensor bikeSensor;
    private BikeLock bikeLock;
    private KeyReader keyReader; 
    private OKLight okLight;
    private String instanceName;
    private int index;
    
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
        bikeId = "";
        this.instanceName = instanceName;
        this.index = index;
    }
    
    public void setDistributor(EventDistributor d) {
        keyReader.addDistributorLinks(d); 
        bikeSensor.addDistributorLinks(d);
    }
    
    public void setCollector(EventCollector c) {
        okLight.setCollector(c);
        bikeLock.setCollector(c);
        
    }
    
    public String getInstanceName() {
        return instanceName;
    }
    public int getIndex() {
        return index;
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
     *
     */
    public void keyInserted(String keyId) {
        logger.fine(getInstanceName());
        if(!bikeId.equals("")){
            // logger.fine("I AM HERE DAMMIT");
            boolean shouldContinue = keyInserted.associateBikeToUser(keyId, this.bikeId);
            //logger.fine("I AM HERE DAMMIT");
            if(shouldContinue){
                bikeLock.unlock();
                bikeId = "";
            }else{
                okLight.flash();
            }
        }
        okLight.flash();       
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
        this.bikeId = bikeId;
        logger.fine(getInstanceName()+" bikeId is : "+this.bikeId);
        okLight.flash();
    }
 
}
