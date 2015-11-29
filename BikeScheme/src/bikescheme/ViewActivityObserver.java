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
 * This interface was modified to serve as a two way transaction
 * for the DSTouchScreen -> DStation -> Hub and its inverse
 * to simplify the request to a single function
 * 
 */
public interface ViewActivityObserver {
    
    List<String> viewActivityReceived(String s);

}
