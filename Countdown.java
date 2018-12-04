import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class Countdown{
	private static int interval = 60;
	private static Timer timer;
	private static JTextArea timeRemaining;

	public Countdown(JTextArea timeRemaining){
		this.timeRemaining=timeRemaining;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
	        public void run() {
	            System.out.println(setInterval());
	        }
	    }, 1000, 1000);
	}
	private static final int setInterval() {
	    if (interval == 1){
	    	System.out.println("Time's Up!");
	        timer.cancel();
	    }
	    timeRemaining.setText("Time Remaining: "+interval+" secs.");
	    return --interval;
	}

	public int getSecs(){
		return this.interval;
	}
}