// package ph.edu.uplb.ics.cmsc137;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 * The game client itself!
 * @author Joseph Anthony C. Hermocilla
 *
 */

public class CanvasClient extends JPanel implements Runnable{
	/**
	 * Main window
	 */
	JFrame frame= new JFrame();
	
	/**
	 * Player position, speed etc.
	 */
	int x=10,y=10,xspeed=2,yspeed=2,prevX,prevY,pressed;
	
	/**
	 * Game timer, handler receives data from server to update game state
	 */
	Thread t=new Thread(this);
	
	/**
	 * Nice name!
	 */
	String name="Joseph";
	
	/**
	 * Player name of others
	 */
	String pname;
	
	/**
	 * Server to connect to
	 */
	String server="localhost";

	/**
	 * Flag to indicate whether this player has connected or not
	 */
	boolean connected=false;
	
	/**
	 * get a datagram socket
	 */
    DatagramSocket socket = new DatagramSocket();

	
    /**
     * Placeholder for data received from server
     */
	String serverData;
	
	/**
	 * Offscreen image for double buffering, for some
	 * real smooth animation :)
	 */
	BufferedImage offscreen;
	 Image image;
	 Graphics2D graphics;
	

	 	public static final String APP_NAME="Circle Wars 0.01";
	
	/**
	 * Game states.
	 */
	public static final int GAME_START=0;
	public static final int IN_PROGRESS=1;
	public final int GAME_END=2;
	public final int WAITING_FOR_PLAYERS=3;
	
	/**
	 * Game port
	 */
	public static final int PORT=4444;

	
	/**
	 * Basic constructor
	 * @param server
	 * @param name
	 * @throws Exception
	 */
	public CanvasClient(String server,String name) throws Exception{
		this.server=server;
		this.name=name;
		
		frame.setTitle(APP_NAME+":"+name);
		//set some timeout for the socket
		socket.setSoTimeout(100);
		
		//Some gui stuff i hate.
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setVisible(true);
		
		//create the buffer
		offscreen=(BufferedImage)this.createImage(640, 480);
		graphics = offscreen.createGraphics();
		
		// twoD.setPaint(Color.decode("#c0392b"));
		
		//Some gui stuff again...
		frame.addKeyListener(new KeyHandler());	
		frame.addMouseMotionListener(new MouseMotionHandler());	
		frame.addMouseListener(new MouseAdapterHandler());
		changetoBlue();
		//tiime to play
		t.start();		
	}
	
	/**
	 * Helper method for sending data to server
	 * @param msg
	 */
	public void send(String msg){
		try{
			byte[] buf = msg.getBytes();
        	InetAddress address = InetAddress.getByName(server);
        	DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
        	socket.send(packet);
        }catch(Exception e){}
		
	}
	
	/**
	 * The juicy part!
	 */
	public void run(){
		while(true){
			try{
				Thread.sleep(1);
			}catch(Exception ioe){}
						
			//Get the data from players
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
     			socket.receive(packet);
			}catch(Exception ioe){/*lazy exception handling :)*/}
			
			serverData=new String(buf);
			serverData=serverData.trim();
			
			//if (!serverData.equals("")){
			//	System.out.println("Server Data:" +serverData);
			//}

			//Study the following kids. 
			if (!connected && serverData.startsWith("CONNECTED")){
				connected=true;
				System.out.println("Connected.");
			}else if (!connected){
				System.out.println("Connecting..");				
				send("CONNECT "+name);
			}else if (connected){
				// offscreen.getGraphics().clearRect(0, 0, 640, 480);
				if (serverData.startsWith("PLAYER")){
					String[] playersInfo = serverData.split(":");
					for (int i=0;i<playersInfo.length;i++){
						String[] playerInfo = playersInfo[i].split(" ");
						String pname =playerInfo[1];
						int prevX = Integer.parseInt(playerInfo[4]);
						int prevY = Integer.parseInt(playerInfo[5]);
						int pressed = Integer.parseInt(playerInfo[6]);
						int x = Integer.parseInt(playerInfo[2]);
						int y = Integer.parseInt(playerInfo[3]);
						//draw on the offscreen image
						if(pressed == 1){

							graphics.drawLine(prevX,prevY,x,y);
							
						}
						// offscreen.getGraphics().drawString(pname,x-10,y+30);					
					}
					//show the changes
					frame.repaint();
				}			
			}				
		}
	}
	
	/**
	 * Repainting method
	 */
	public void paintComponent(Graphics g){
		g.drawImage(offscreen, 0, 0, null);
	}

	class MouseAdapterHandler extends MouseAdapter{
		public void mousePressed(MouseEvent e) {
            prevX = e.getX();
            prevY = e.getY();
            // moves.add(e.getPoint());
            // repaint();
        }
	}

	public void clearCanvas(){
	    graphics.setPaint(Color.white);
	    graphics.fillRect(0,0,getSize().width,getSize().height);
	    graphics.setPaint(Color.black);
	    repaint();
	}

	public void changetoRed(){
	    graphics.setPaint(Color.decode("#c0392b"));
	}
	public void changetoBlue(){
	    graphics.setPaint(Color.decode("#2980b9"));
	}
	public void changetoYellow(){
	    graphics.setPaint(Color.yellow);
	}
	public void changetoGreen(){
	    graphics.setPaint(Color.decode("#27ae60"));
	}
	public void changetoBlack(){
	    graphics.setPaint(Color.black);
	}
	
	
	
	
	class MouseMotionHandler extends MouseMotionAdapter{
		// public void mouseMoved(MouseEvent me){
		// 	// prevX = x;prevY=y;
		// 	x=me.getX();y=me.getY();

		// 	if (prevX != x || prevY != y){
		// 		send("PLAYER "+name+" "+x+" "+y+" "+prevX+" "+prevY+" "+0);
		// 	}				
		// }
		 public void mousePressed(MouseEvent me) {
            x=me.getX();y=me.getY();
            prevX = me.getX();
            prevY = me.getY();
            if(graphics != null){
                graphics.drawLine(prevX,prevY,x,y);
                // send("PLAYER "+name+" "+x+" "+y+" "+prevX+" "+prevY+" "+1);

                frame.repaint();
                // old_x = current_x;
                // old_y = current_y;
            }
        }

        public void mouseDragged(MouseEvent me) {
            x = me.getX();
            y = me.getY();

		if(graphics != null){
                graphics.drawLine(prevX,prevY,x,y);
                send("PLAYER "+name+" "+x+" "+y+" "+prevX+" "+prevY+" "+1);
                frame.repaint();
                prevX = x;
                prevY = y;
            }

            // moves.add(e.getPoint());

        }
	}
	
	class KeyHandler extends KeyAdapter{
		public void keyPressed(KeyEvent ke){
			prevX=x;prevY=y;
			switch (ke.getKeyCode()){
			case KeyEvent.VK_DOWN:y+=yspeed;break;
			case KeyEvent.VK_UP:y-=yspeed;break;
			case KeyEvent.VK_LEFT:x-=xspeed;break;
			case KeyEvent.VK_RIGHT:x+=xspeed;break;
			}
			if (prevX != x || prevY != y){
				send("PLAYER "+name+" "+x+" "+y+" "+prevX+" "+prevY+" "+1);
			}	
		}
	}
	
	
	public static void main(String args[]) throws Exception{
		if (args.length != 2){
			System.out.println("Usage: java -jar CanvasClient-client <server> <player name>");
			System.exit(1);
		}

		new CanvasClient(args[0],args[1]);
	}
}
