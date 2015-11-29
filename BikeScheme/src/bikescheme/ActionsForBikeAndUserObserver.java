package bikescheme;
/**
 * Interface observer for when a bike needs
 * to be removed from the user it is
 * associated to.
 * @author ivo
 *
 */
public interface ActionsForBikeAndUserObserver {
    public void returnBike(String bikeId, String endPoint);
    public void addBike(String bikeId, String keyId, String startPoint);
}
