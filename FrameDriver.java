
class FrameDriver extends EventDriver{
	int framesPerSecond, frameDelay;
	long frameCount;
	
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
		if(frameCount == framesPerSecond) frameCount = 0;
	}
	

}