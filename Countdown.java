import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import proto.PlayerProtos.*;
import proto.TcpPacketProtos.*;
import java.io.*;
import java.net.*;

public class Countdown{
	private static int interval;
	private static Timer timer;
	private static Player player;
	private static DataOutputStream out;
	private static boolean stop;

	public Countdown(Player player, DataOutputStream out){
		this.interval=60;
		this.player=player;
		this.out=out;
		this.stop=false;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
	        public void run() {
	        	setInterval();
	        }
	    }, 1000, 1000);
	}

	private static final int setInterval() {
		try{
			TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
				chatPacket.setType(TcpPacket.PacketType.CHAT)
				.setPlayer(player)
				.setMessage("Time left: "+interval);
			out.write(chatPacket.build().toByteArray());
		}catch(IOException e) { // error cannot connect to server
			  e.printStackTrace();
			  System.out.println("Cannot send");
		}

	    if (interval == 1){
	    	System.out.println("Time's Up!");
	    	stop=true;
	        timer.cancel();
	    }
	    return --interval;
	}

	public int getSecs(){
		return this.interval;
	}

	public boolean didStop(){
		return this.stop;
	}
}