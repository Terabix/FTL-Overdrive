import java.util.ArrayList;

import javax.swing.event.ChangeListener;


public class Listenable {
	private ArrayList<ChangeListener> listeners;
	
	public Listenable(){
		listeners = new ArrayList<ChangeListener>();
	}
	
	public int addListener(ChangeListener l) {
		listeners.add(l);
		return listeners.indexOf(l);
	}

	public void removeListener(int index){
		listeners.remove(index);
	}
}
