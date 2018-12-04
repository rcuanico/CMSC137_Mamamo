import java.util.Timer;
import java.util.TimerTask;

public class Countdown{
	private static int interval = 60;
	private static Timer timer;

	public Countdown(){
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
	        public void run() {
	            System.out.println(setInterval());
	        }
	    }, 1000, 1000);
	}
	private static final int setInterval() {
	    if (interval == 1)
	        timer.cancel();
	    return --interval;
	}

	public int getSecs(){
		return this.interval;
	}
}