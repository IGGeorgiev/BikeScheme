/**
 * 
 */
package bikescheme;

import java.util.List;

/**
 * Interface for any class with objects that need to receive 
 * notifications  concerning viewActivity messages input to 
 * DSTouchScreen IO devices.
 * 
 * @author pbj
 * 
 */
public interface ViewActivityObserver {
    
    public void viewActivityReceived();

}
