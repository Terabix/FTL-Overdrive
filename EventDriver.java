import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.event.ChangeListener;

public abstract class EventDriver implements Runnable{
	private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();

	public EventDriver(){
		
	}

	public abstract void run();

	public int addListener(ChangeListener l){
		this.addListener(l);
		listeners.add(l);
		return listeners.indexOf(l);
	}

	public void removeListener(int index){
		listeners.remove(index);
	}

}