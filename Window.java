import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Window extends JFrame implements ChangeListener{
	JLabel content = new JLabel("");
	public Window(String title, Dimension resolution, FrameDriver fdriver){
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		content.setPreferredSize(resolution);
		this.getContentPane().add(content,BorderLayout.CENTER);
		
		this.pack();
		this.setVisible(true);
		
		fdriver.addListener(this);
	}
	@Override
	public void stateChanged(ChangeEvent arg0) {
		this.repaint();
		System.out.println("ding");
	}
}
