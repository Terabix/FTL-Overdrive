import java.util.ArrayList;
import javax.swing.event.ChangeListener;



class FrameDriver implements Runnable{
	int framesPerSecond, frameDelay;
	long frameCount;
	ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();
	
	public FrameDriver(int fps){
		framesPerSecond = fps;
		frameDelay = 1000/fps;
		frameCount = 0;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		frameCount++;
		try {
			Thread.sleep(frameDelay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(frameCount >= framesPerSecond) frameCount = 0;
	}

	public int addListener(ChangeListener l) {
		listeners.add(l);
		return listeners.indexOf(l);
	}

	public void removeListener(int index){
		listeners.remove(index);
	}
}