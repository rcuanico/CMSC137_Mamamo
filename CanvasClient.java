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
	 String curColor = "black";

	 private static ImageIcon blackIcon = new ImageIcon("src/black.jpg");
    private static Image img = blackIcon.getImage() ;  
    private static Image newimg = img.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ; 
    
    private static ImageIcon blueIcon = new ImageIcon("src/blue.png");
    private static Image img2 = blueIcon.getImage() ;  
    private static Image newimg2 = img2.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ; 
 
    private static ImageIcon yellowIcon = new ImageIcon("src/yellow.png");
    private static Image img3 = yellowIcon.getImage() ;  
    private static Image newimg3 = img3.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ; 

    private static ImageIcon greenIcon = new ImageIcon("src/green.png");
    private static Image img4 = greenIcon.getImage() ;  
    private static Image newimg4 = img4.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ; 

    private static ImageIcon redIcon = new ImageIcon("src/red.png");
    private static Image img5 = redIcon.getImage() ;  
    private static Image newimg5 = img5.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ; 

    private static ImageIcon clearIcon = new ImageIcon("src/clear.png");
    private static Image img6 = clearIcon.getImage() ;  
    private static Image newimg6 = img6.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ; 
	

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
		
		JPanel pallettePanel = new JPanel();
		pallettePanel.setSize(100,480);
		frame.getContentPane().add(pallettePanel);
		//create the buffer
		offscreen=(BufferedImage)this.createImage(640, 480);
		graphics = offscreen.createGraphics();
		
		// twoD.setPaint(Color.decode("#c0392b"));
		clearIcon = new ImageIcon( newimg6 );
		JButton clearBtn = new JButton(clearIcon);
        clearBtn.setBorder(BorderFactory.createEmptyBorder());
        clearBtn.setContentAreaFilled(false);
		clearBtn.setPreferredSize(new Dimension(120,120));
        
        blackIcon = new ImageIcon( newimg );
		JButton blackBtn = new JButton(blackIcon);
        blackBtn.setBorder(BorderFactory.createEmptyBorder());
        blackBtn.setContentAreaFilled(false);
		blackBtn.setPreferredSize(new Dimension(120,120));

        redIcon = new ImageIcon( newimg5 );
		JButton redBtn = new JButton(redIcon);
        redBtn.setBorder(BorderFactory.createEmptyBorder());
        redBtn.setContentAreaFilled(false);
		redBtn.setPreferredSize(new Dimension(120,120));

        blueIcon = new ImageIcon( newimg2 );
		JButton blueBtn = new JButton(blueIcon);
        blueBtn.setBorder(BorderFactory.createEmptyBorder());
        blueBtn.setContentAreaFilled(false);
		blueBtn.setPreferredSize(new Dimension(120,120));
            
        yellowIcon = new ImageIcon( newimg3 );
		JButton yellowBtn = new JButton(yellowIcon);
        yellowBtn.setBorder(BorderFactory.createEmptyBorder());
        yellowBtn.setContentAreaFilled(false);
		yellowBtn.setPreferredSize(new Dimension(120,120));
        
        greenIcon = new ImageIcon( newimg4 );
		JButton greenBtn = new JButton(greenIcon);
        greenBtn.setBorder(BorderFactory.createEmptyBorder());
        greenBtn.setContentAreaFilled(false);
		greenBtn.setPreferredSize(new Dimension(120,120));

		clearBtn.setBounds(0,10,0,0);
		clearBtn.setSize(new Dimension(70,50));
		clearBtn.setBackground(Color.decode("#7f8c8d"));
		blackBtn.setBounds(0,80,0,0);
		blackBtn.setSize(new Dimension(70,50));
		blackBtn.setBackground(Color.black);
		redBtn.setBounds(0,140,0,0);
		redBtn.setSize(new Dimension(70,50));
		redBtn.setBackground(Color.red);
		blueBtn.setBounds(0,210,0,0);
		blueBtn.setSize(new Dimension(70,50));
		blueBtn.setBackground(Color.blue);
		yellowBtn.setBounds(0,280,0,0);
		yellowBtn.setSize(new Dimension(70,50));
		yellowBtn.setBackground(Color.yellow);
		greenBtn.setBounds(0,350,0,0);
		greenBtn.setSize(new Dimension(70,50));
		greenBtn.setBackground(Color.green);
		pallettePanel.add(clearBtn);
		pallettePanel.add(blackBtn);
		pallettePanel.add(redBtn);
		pallettePanel.add(blueBtn);
		pallettePanel.add(yellowBtn);
		pallettePanel.add(greenBtn);

		clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	clearCanvas();

            }
        });
        blackBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	changetoBlack();
            }
        });
        redBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	changetoRed();
            }
        });
        blueBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	changetoBlue();
            }
        });
        yellowBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	changetoYellow();
            }
        });
        greenBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	changetoGreen();
            }
        });

		//Some gui stuff again...
		frame.addKeyListener(new KeyHandler());	
		frame.addMouseMotionListener(new MouseMotionHandler());	
		frame.addMouseListener(new MouseAdapterHandler());
		frame.setResizable(false);
		//changetoBlue();
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
						String col = playerInfo[7];
						int x = Integer.parseInt(playerInfo[2]);
						int y = Integer.parseInt(playerInfo[3]);
						//draw on the offscreen image
						if(pressed == 1){
							if(col.equals("green")){
								changetoGreen();
							}else if(col.equals("blue")){
								changetoBlue();
							}else if(col.equals("red")){
								changetoRed();
							}else if(col.equals("yellow")){
								changetoYellow();
							}else if(col.equals("black")){
								changetoBlack();
							}else if(col.equals("clear")){
								graphics.setPaint(Color.white);
							    graphics.fillRect(0,0,getSize().width,getSize().height);
							    graphics.setPaint(Color.black);
							}
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
	    curColor="clear";
	    repaint();
	}

	public void changetoRed(){
	    graphics.setPaint(Color.decode("#c0392b"));
	    curColor="red";
	}
	public void changetoBlue(){
	    graphics.setPaint(Color.decode("#2980b9"));
	    curColor="blue";
	}
	public void changetoYellow(){
	    graphics.setPaint(Color.yellow);
	    curColor="yellow";
	}
	public void changetoGreen(){
	    graphics.setPaint(Color.decode("#27ae60"));
	    curColor="green";
	}
	public void changetoBlack(){
	    graphics.setPaint(Color.black);
	    curColor="black";
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
                send("PLAYER "+name+" "+x+" "+y+" "+prevX+" "+prevY+" "+1+" "+curColor);
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
				send("PLAYER "+name+" "+x+" "+y+" "+prevX+" "+prevY+" "+1+" "+curColor);
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
