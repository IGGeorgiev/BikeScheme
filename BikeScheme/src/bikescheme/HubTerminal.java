/**
 * 
 */
package bikescheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Model of a terminal with a keyboard, mouse and monitor.
 * 
 * @author pbj
 *
 */
public class HubTerminal extends AbstractIODevice {

    /**
     * 
     * @param instanceName  
     */
    public HubTerminal(String instanceName) {
        super(instanceName);   
    }
    
    // Fields and methods for device input function
    
    private AddDStationObserver observer;
    
    public void setObserver(AddDStationObserver o) {
        observer = o;
    }
    
    private HubTerminalStatReqObserver htsro;   //Hub Terminal Statistic Request Observer
    public void setStatObserver(HubTerminalStatReqObserver o){
        htsro = o;
    }
    
    private HubTerminalStatReqObserver htfbo;   //Hub Terminal Faulty Bike Observer
    public void setFaultObserver(HubTerminalStatReqObserver o){
        htfbo = o;
    }
    
    /** 
     *    Select device action based on input event message
     *    
     *    @param e
     */
    @Override
    public void receiveEvent(Event e) {
        
        if (e.getMessageName().equals("addDStation") 
                && e.getMessageArgs().size() == 4) {
            
            String instanceName = e.getMessageArgs().get(0);
            int eastPos = Integer.parseInt(e.getMessageArg(1));
            int northPos =  Integer.parseInt(e.getMessageArg(2));
            int numPoints =  Integer.parseInt(e.getMessageArg(3));
            
            addDStation(instanceName, eastPos, northPos, numPoints);
            
        } else if (e.getMessageName().equals("showStats")
                && e.getMessageArgs().size() == 0){
            
            htsro.populateStatsList();
            
        } else if ((e.getMessageName().equals("showFaulty")
                && e.getMessageArgs().size() == 0)){
            
            htfbo.populateFaultyDStationList();
            
        }else{    
            super.receiveEvent(e);
        } 
    }
    /**
     * Handle request to add a new docking station
     */
    public void addDStation(
            String instanceName, 
            int eastPos, 
            int northPos,
            int numPoints) {
        logger.fine(getInstanceName());
        
        
        observer.addDStation(instanceName, eastPos, northPos, numPoints);
    }
    
    
    // Insert here support for operations generating output on the 
    // touch screen display.
    
    
  /**
   * This function prints out the overall statistics of the system
   * over a given time interval.
   * @param stats
   */
   public void showStats(List<String> stats){
       logger.fine(getInstanceName());
       
       String deviceClass = "HubTerminal";
       String deviceInstance = getInstanceName();
       String messageName = "showStats";
       
       List<String> messageArgs = new ArrayList<String>();
       String[] preludeArgs = 
           {"unordered-tuples","4",
            "#Trips", "#Users","Total Distance Travelled (m)","Average Journey Time (min)"};
       messageArgs.addAll(Arrays.asList(preludeArgs));
       messageArgs.addAll(stats);
       
       super.sendEvent(
           new Event(
               Clock.getInstance().getDateAndTime(), 
               deviceClass,
               deviceInstance,
               messageName,
               messageArgs));
       
   }
   
   /**
    * This function prints out the stations in which faulty bikes
    * can be found and their coordinates on the map
    * @param faulty
    */
   public void showFaulty(List<String> faulty){
       logger.fine(getInstanceName());
       
       String deviceClass = "HubTerminal";
       String deviceInstance = getInstanceName();
       String messageName = "showFaulty";
       
       List<String> messageArgs = new ArrayList<String>();
       String[] preludeArgs = 
           {"unordered-tuples","3",
            "DStation", "East","North"};
       messageArgs.addAll(Arrays.asList(preludeArgs));
       messageArgs.addAll(faulty);
       
       super.sendEvent(
           new Event(
               Clock.getInstance().getDateAndTime(), 
               deviceClass,
               deviceInstance,
               messageName,
               messageArgs));
   }
}
