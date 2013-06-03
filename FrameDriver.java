import java.util.ArrayList;
import javax.swing.event.ChangeListener;



class FrameDriver extends Listenable implements Runnable{
	private int framesPerSecond, frameDelay;
	private Long frameCount;
	
	public FrameDriver(int fps){
		setFramesPerSecond(fps);
		setFrameDelay(1000/fps);
		frameCount = new Long(0);
	}
	
	@Override
	public void run() {
		while(true){
			// TODO Auto-generated method stub
			setFrameCount(getFrameCount() + 1);
			try {
				Thread.sleep(getFrameDelay());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(getFrameCount() >= getFramesPerSecond()) setFrameCount(0);
			
		}
	}

	public int getFramesPerSecond() {
		return framesPerSecond;
	}

	public void setFramesPerSecond(int framesPerSecond) {
		this.framesPerSecond = framesPerSecond;
		this.frameDelay = (1000/framesPerSecond);
	}

	public long getFrameCount() {
		return frameCount;
	}

	public void setFrameCount(long frameCount) {
		this.frameCount = frameCount;
	}

	public int getFrameDelay() {
		return frameDelay;
	}

	public void setFrameDelay(int frameDelay) {
		this.frameDelay = frameDelay;
		this.framesPerSecond = (1000/frameDelay);
	}
}