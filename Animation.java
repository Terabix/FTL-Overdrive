import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Animation extends BufferedImage implements ChangeListener{

	private BufferedImage[] frame;
	private Graphics2D g;
	private boolean shuffle;
	private int frames, currentFrame;
	
	public Animation(int width, int height, int imageType, BufferedImage animSheet, int frames, boolean shuffle, FrameDriver driver) {
		super(width, height, imageType);
		this.frames = frames;
		frame = new BufferedImage[frames];
		this.shuffle = shuffle;
		g = (Graphics2D) this.getGraphics();
		currentFrame = 0;
		
		driver.addListener(this);
		
		for(int x = 0; x<frames; x++){
			frame[x] = animSheet.getSubimage(x*width, 0, width, height);
		}
		
		g.drawImage(frame[currentFrame],0,0,null);
	}

	private void advanceAnimation(){
		if(shuffle){
			currentFrame = (int)Math.random()*frames;
		}else{
			currentFrame++;
		}
		g.drawImage(frame[currentFrame],0,0,null);
	}
	
	public void stateChanged(ChangeEvent e) {
		advanceAnimation();
	}

	
}