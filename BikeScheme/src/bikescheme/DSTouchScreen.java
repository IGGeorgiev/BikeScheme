/**
 * 
 */
package bikescheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Model of a touch screen input & output device.
 * 
 * @author pbj
 *
 */
public class DSTouchScreen extends AbstractIODevice {

    private KeyReader keyReader;
    /**
     * 
     * @param instanceName  
     */
    public DSTouchScreen(String instanceName) {
        super(instanceName);
        
        //Used to ask for key input using method in KeyReader class
        
        keyReader = new KeyReader(instanceName + ".kr");
    }
    
    
    /** 
     *    Select device action based on input event message
     *    
     *    @param e
     */
    @Override
    public void receiveEvent(Event e) {
        
        if (e.getMessageName().equals("startReg") 
                && e.getMessageArgs().size() == 1) {
            
            String personalDetails = e.getMessageArg(0);
            startReg(personalDetails);
            
        } else if (e.getMessageName().equals("viewActivity") 
                    && e.getMessageArgs().size() == 0) {
                
            viewActivity();
                
        } else {
            super.receiveEvent(e);
        } 
    }
    
    /* 
     * 
     * SUPPORT FOR startReg TRIGGERING INPUT MESSAGE
     * 
     */
    
    private StartRegObserver startRegObserver;
    
    public void setStartRegObserver(StartRegObserver o) {
        startRegObserver = o;
    }
    
    /**
     * Model user starting a user registration operation and entering their
     * personal details.  Pass details on to the registered observer.
     * 
     * @param keyId
     */
    public void startReg(String personalDetails) {
        logger.fine(getInstanceName());
        
        startRegObserver.startRegReceived(personalDetails);    
    }
    
    /* 
     * 
     * SUPPORT FOR viewActivity TRIGGERING INPUT MESSAGE
     * 
     */
    
    private ViewActivityObserver viewActivityObserver;
    
    public void setViewActivityObserver(ViewActivityObserver o) {
        viewActivityObserver = o;
    }
    
    /**
     * Model user selecting a "view activity" option to see their completed
     * trips since the previous midnight.
     * 
     * @param keyId
     */
    public void viewActivity() {
        logger.fine(getInstanceName());
        
        showPrompt("Please insert key.");
        
        //Gets the user's key identification
        String keyId = keyReader.waitForKeyInsertion(); 
        
        logger.fine("Key" + keyId + "Received");

        //Prints User Activity
        
        showUserActivity(viewActivityObserver.viewActivityReceived(keyId));
    }
    
    /* 
     * 
     * SUPPORT FOR showPrompt OUTPUT MESSAGE
     * 
     */

    public void showPrompt(String prompt) {
        logger.fine(getInstanceName());
        
        String deviceClass = "DSTouchScreen";
        String deviceInstance = getInstanceName();
        String messageName = "viewPrompt";
        
        List<String> valueList = new ArrayList<String>();
        valueList.add(prompt); 
        
        
        super.sendEvent(
            new Event(
                Clock.getInstance().getDateAndTime(), 
                deviceClass,
                deviceInstance,
                messageName,
                valueList));
        
    }

    /* 
     * 
     * SUPPORT FOR showPrompt OUTPUT MESSAGE
     * 
     */

    public void showUserActivity(List<String> activityData) {
        logger.fine(getInstanceName());
        
        String deviceClass = "DSTouchScreen";
        String deviceInstance = getInstanceName();
        String messageName = "viewUserActivity";
        
        List<String> messageArgs = new ArrayList<String>();
        String[] preludeArgs = 
            {"ordered-tuples","4",
             "HireTime (min)","HireDS","ReturnDS","Price"}; //Changed tuples from Hire Time HireDS ReturnDS Duration (min)
        messageArgs.addAll(Arrays.asList(preludeArgs));
        messageArgs.addAll(activityData);
        
        super.sendEvent(
            new Event(
                Clock.getInstance().getDateAndTime(), 
                deviceClass,
                deviceInstance,
                messageName,
                messageArgs));
       
    }

     
}
