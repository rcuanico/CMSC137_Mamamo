// package ph.edu.uplb.ics.cmsc137;
import java.net.InetAddress;

/**
 * This class encapsulates a network players
 * @author Joseph Anthony C. Hermocilla
 *
 */

public class NetPlayer {
	/**
	 * The network address of the player
	 */
	private InetAddress address;
	
	/**
	 * The port number of  
	 */
	private int port;
	
	/**
	 * The name of the player
	 */
	private String name;
	
	/**
	 * The position of player
	 */
	private int x,y,prevX,prevY,pressed;

	/**
	 * Constructor
	 * @param name
	 * @param address
	 * @param port
	 */
	public NetPlayer(String name,InetAddress address, int port){
		this.address = address;
		this.port = port;
		this.name = name;
	}

	/**
	 * Returns the address
	 * @return
	 */
	public InetAddress getAddress(){
		return address;
	}

	/**
	 * Returns the port number
	 * @return
	 */
	public int getPort(){
		return port;
	}

	/**
	 * Returns the name of the player
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Sets the X coordinate of the player
	 * @param x
	 */
	public void setX(int x){
		this.x=x;
	}
	
	public void setPrevX(int x){
		this.prevX=x;
	}

	public void setPrevY(int x){
		this.prevY=x;
	}
	public void setPressed(int x){
		this.pressed=x;
	}
	
	/**
	 * Returns the X coordinate of the player
	 * @return
	 */
	public int getX(){
		return x;
	}
	
	
	/**
	 * Returns the y coordinate of the player
	 * @return
	 */
	public int getY(){
		return y;
	}
	
	/**
	 * Sets the y coordinate of the player
	 * @param y
	 */
	public void setY(int y){
		this.y=y;		
	}

	/**
	 * String representation. used for transfer over the network
	 */
	public String toString(){
		String retval="";
		retval+="PLAYER ";
		retval+=name+" ";
		retval+=x+" ";
		retval+=y+" ";
		retval+=prevX+" ";
		retval+=prevY+" "+pressed;
		// retval+=pressed;
		return retval;
	}	
}
