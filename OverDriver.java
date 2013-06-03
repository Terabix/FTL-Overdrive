import java.awt.Dimension;


public class OverDriver {
	static OverDriver driver;
	FrameDriver fdriver;
	Dimension resolution = new Dimension(1200,600);
	String title = "FTL:Overdrive";
	Boolean request_exit;
	
	
	public static void main(String[] args) {
// TODO Auto-generated method stub
		driver = new OverDriver();
	}
	
	public OverDriver(){
		init();
		runtime();
	}

	private void init(){
		fdriver = new FrameDriver(30);
		Window window = new Window(title, resolution,fdriver);
		fdriver.run();
	}
	
	private void runtime() {
		
	}	

}