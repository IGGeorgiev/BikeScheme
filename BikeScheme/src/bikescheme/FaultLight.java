package bikescheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Red Fault light output device.
 * @author ivo
 *
 */
public class FaultLight extends AbstractOutputDevice {
    private boolean isOn;
    public FaultLight(String instanceName) {
        super(instanceName);
        isOn = false;
    }
    
    public void turnOn() {
        logger.fine(getInstanceName());
        isOn = true;
        String deviceClass = "FaultLight";
        String deviceInstance = getInstanceName();
        String messageName = "turnedOn";
        List<String> valueList = new ArrayList<String>();
 
        super.sendEvent(
            new Event(
                Clock.getInstance().getDateAndTime(), 
                deviceClass,
                deviceInstance,
                messageName,
                valueList));
        
    }
    public void turnOff() {
        logger.fine(getInstanceName());
        isOn = false;
        String deviceClass = "FaultLight";
        String deviceInstance = getInstanceName();
        String messageName = "turnedOff";
        List<String> valueList = new ArrayList<String>();
 
        super.sendEvent(
            new Event(
                Clock.getInstance().getDateAndTime(), 
                deviceClass,
                deviceInstance,
                messageName,
                valueList));
        
    }
    public void flash() {
        logger.fine(getInstanceName());
        String deviceClass = "FaultLight";
        String deviceInstance = getInstanceName();
        String messageName = "flashed";
        List<String> valueList = new ArrayList<String>();
 
        super.sendEvent(
            new Event(
                Clock.getInstance().getDateAndTime(), 
                deviceClass,
                deviceInstance,
                messageName,
                valueList));
        
    }
    public boolean isOn(){
        return isOn;
    }

}
