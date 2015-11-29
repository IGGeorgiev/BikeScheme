package bikescheme;
/**
 * Interface observer for a bike being returned 
 * to a DPoint
 * @author ivo
 *
 */
public interface DPointObserver {
    public void disassociateBikeFromUser(String bikeId);
    public void associateBikeToUser(String bikeId, String keyId);
}
