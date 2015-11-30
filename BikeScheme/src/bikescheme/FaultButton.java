package bikescheme;

public class FaultButton extends AbstractInputDevice {
    public FaultButton(String instance) { 
        super(instance);
    }
    //TODO
    private FaultButtonObserver observer;
    public void setFaultButtonObserver(FaultButtonObserver o){
        this.observer = o;
    }
    
    @Override
    public void receiveEvent(Event e) {
        
        if (e.getMessageName().equals("pressed") 
                && e.getMessageArgs().size() == 0) {
            
            press();
            
        } else {
            super.receiveEvent(e);
        }
    }
    public void press(){
        observer.onPress();
    }
}
