import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.swing.*;
import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.image.*;
import javax.imageio.*;

public class Layout{
    static Canvas canvas = new Canvas();

    private static DataOutputStream out;
	private static InputStream inFromServer;
	private static Player player;
	private static int totalScore=0;
	private static boolean canGuess=false;
	private String lobbyId;
	private static JTextArea chats;
	private static JPanel msgArea;
	private static JTextArea wordArea;

	private static String word="";
	private static JTextArea timeRemaining;
    private static JTextArea score;
    private static int time;

	private Runnable chatSender;
	private Runnable chatReceiver;
    private static ImageIcon buttonIcon = new ImageIcon("src/button.png");

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

    private static Layout layout;
    
        

    public Layout (Player player, String lobbyId, DataOutputStream out, InputStream inFromServer) {
        this.out=out;
        this.inFromServer=inFromServer;
        this.player=player;
        this.lobbyId=lobbyId;
        this.layout=this;

        joinLobby();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                chatLobby();
            }
        });
    }

    public Layout (int numRound, Player player, String lobbyId, DataOutputStream out, InputStream inFromServer) {
        this.out=out;
        this.inFromServer=inFromServer;
        this.player=player;
        this.lobbyId=lobbyId;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                joinLobby();
                chatLobby();
            }
        });
    }

    public static void addComponentsToPane(JFrame frame) {
    	Container pane = frame.getContentPane();

    	JPanel mainPanel = new JPanel(new BorderLayout());
    	mainPanel.setPreferredSize(new Dimension(1200,600));
        mainPanel.setBackground(Color.orange);
    	pane.add(mainPanel);


    	JPanel topPanel = new JPanel(new BorderLayout());
    	topPanel.setPreferredSize(new Dimension(1200,450));
    	mainPanel.add(topPanel, BorderLayout.NORTH);

    	JPanel leftPanel = new JPanel(new BorderLayout());
    	leftPanel.setPreferredSize(new Dimension(300,450));
    	topPanel.add(leftPanel, BorderLayout.WEST);

    	JPanel rightPanel = new JPanel(new BorderLayout());
    	rightPanel.setPreferredSize(new Dimension(70,450));
    	topPanel.add(rightPanel, BorderLayout.EAST);

    	//=============USERNAME===============//
    	JTextArea name = new JTextArea(player.getName());
    	name.setPreferredSize(new Dimension(300,25));
        name.setFont(new Font("Helvetica", Font.BOLD, 14));
    	name.setEditable(false);
        name.setBackground(Color.decode("#2ecc71"));
    	name.setOpaque(true);
    	leftPanel.add(name, BorderLayout.NORTH);

    	//===============SCORE================//
    	JPanel scoreAndTime = new JPanel(new BorderLayout());
        scoreAndTime.setOpaque(false);
        
      
    	scoreAndTime.setPreferredSize(new Dimension(300,50));
    	leftPanel.add(scoreAndTime, BorderLayout.CENTER);

    	score = new JTextArea("Score: ");
    	score.setPreferredSize(new Dimension(200,25));
    	score.setEditable(false);
    	score.setOpaque(true);
        score.setBackground(Color.decode("#2ecc71"));
    	scoreAndTime.add(score, BorderLayout.NORTH);

    	wordArea = new JTextArea("Word:");
    	wordArea.setPreferredSize(new Dimension(200,25));
    	wordArea.setEditable(false);
    	wordArea.setOpaque(true);
        wordArea.setBackground(Color.decode("#2ecc71"));
    	scoreAndTime.add(wordArea, BorderLayout.CENTER);

    	timeRemaining = new JTextArea("Time Remaining: ");
    	timeRemaining.setPreferredSize(new Dimension(200,25));
    	timeRemaining.setEditable(false);
    	timeRemaining.setOpaque(true);
        timeRemaining.setBackground(Color.decode("#2ecc71"));
    	scoreAndTime.add(timeRemaining, BorderLayout.SOUTH);

     	//============FOR ALL CHATS============//
    	JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(Color.decode("#bdc3c7"));
		chatPanel.setPreferredSize(new Dimension(300,350));
		chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		leftPanel.add(chatPanel, BorderLayout.SOUTH);

		chats=new JTextArea("");
        chats.setWrapStyleWord(true);
		chats.setPreferredSize(new Dimension(300,300));
		chats.setEditable(false);
		chats.setOpaque(false);
		chatPanel.add(chats, BorderLayout.NORTH);

		msgArea=new JPanel(new BorderLayout());
		msgArea.setPreferredSize(new Dimension(300,50));
		msgArea.setOpaque(false);
		chatPanel.add(msgArea,BorderLayout.SOUTH);

		JTextArea msgHere = new JTextArea("");
        msgHere.setWrapStyleWord(true);
		chats.setOpaque(false);
		msgHere.setPreferredSize(new Dimension(200,50));
		msgArea.add(msgHere, BorderLayout.CENTER);

		
        JButton sendMsg = new JButton (buttonIcon);
        sendMsg.setBorder(BorderFactory.createEmptyBorder());
        sendMsg.setContentAreaFilled(false);
		sendMsg.setPreferredSize(new Dimension(100,50));
		msgArea.add(sendMsg, BorderLayout.EAST);

		sendMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatSender sender = new ChatSender(player, out, msgHere.getText());
				sender.start();
				try{
					sender.join();
				}catch(InterruptedException err){}
				if(msgHere.getText().equals("quit")){
					frame.dispose();
					try{
						Runtime rt = Runtime.getRuntime();
						Process pr = rt.exec("java Main 202.92.144.45 80");
					}catch(IOException error) { // error cannot connect to server
					  error.printStackTrace();
					  System.out.println("Cannot open Main.java");
					}
				}else if(msgHere.getText().equals(word)&&!word.equals("")&&canGuess==true){
                    try{
                        TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
                            chatPacket.setType(TcpPacket.PacketType.CHAT)
                            .setPlayer(player)
                            .setMessage(player.getName()+" guessed the word! +" +getTime()+" points.");
                        out.write(chatPacket.build().toByteArray());
                    }catch(IOException a) { // error cannot connect to server
                      a.printStackTrace();
                      System.out.println("Cannot send to Server");
                    }

					System.out.println(player.getName()+" guessed the word! +" +getTime()+" points.");
					totalScore+=getTime();
					canGuess=false;
					score.setText("Score: "+Integer.toString(totalScore));
				}
				msgHere.setText("");
			}
		});

		//=============AREA TO DRAW==============//
    	canvas.setPreferredSize(new Dimension(600,450));
       
    	topPanel.add(canvas, BorderLayout.CENTER);
    	JPanel pallettePanel = new JPanel();
    	pallettePanel.setPreferredSize(new Dimension(200,200));
		pallettePanel.setLayout(null);
		pallettePanel.setBackground(Color.decode("#ecf0f1"));
        
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
		rightPanel.add(pallettePanel);
		// canvas.clearCanvas();


    	//======Button Listeners======//
    	 //button listeners
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	canvas.clearCanvas();

            }
        });
        blackBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	canvas.changetoBlack();
            }
        });
        redBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	canvas.changetoRed();
            }
        });
        blueBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	canvas.changetoBlue();
            }
        });
        yellowBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	canvas.changetoYellow();
            }
        });
        greenBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	canvas.changetoGreen();
            }
        });

    	//======ALL PLAYER SCORES AND EXIT==========//
    	JPanel bottomPanel = new JPanel(new BorderLayout());
    	bottomPanel.setPreferredSize(new Dimension(1000,150));
        bottomPanel.setBackground(Color.decode("#ecf0f1"));
    	mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    	//=============ALL PLAYERS============//
    	// System.out.println(getAllPlayers().length());
    	//getAllPlayers();
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GridBagLayoutDemo");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setPreferredSize(new Dimension(1200, 800));
        frame.setPreferredSize(new Dimension(1200, 600));
        frame.setBackground(Color.white);

        //Set up the content pane.
        addComponentsToPane(frame);

        //Display the window.
        frame.pack();
        frame.setFocusable(true);
        frame.setVisible(true);
        frame.setResizable(false);
    }

   	private TcpPacket.ConnectPacket joinLobby (){
		TcpPacket.ConnectPacket connectPacket = null;
		try {
				connectPacket = TcpPacket.ConnectPacket.newBuilder()
					.setType(TcpPacket.PacketType.CONNECT)
					.setPlayer(player)
					.setLobbyId(lobbyId)
					.build();
				out.write(connectPacket.toByteArray());

				byte[] lobbyData = new byte[1024];	//getting server response
				int count = inFromServer.read(lobbyData);
				lobbyData = Arrays.copyOf(lobbyData, count);
				TcpPacket lobbyMsg = TcpPacket.parseFrom(lobbyData);
				if(lobbyMsg.getType() == TcpPacket.PacketType.CONNECT){
					System.out.println("You have successfully connected to the lobby.");
				}else{
					System.out.println("Connection to lobby failed.");
				}
		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
		return connectPacket;
	}

	private static void chatLobby(){
		ChatReceiver receiver = new ChatReceiver(inFromServer, chats, timeRemaining, layout);
		receiver.start();
	}

	private static void getAllPlayers(){
		try{
			TcpPacket.PlayerListPacket.Builder listPacket = TcpPacket.PlayerListPacket.newBuilder();
			listPacket.setType(TcpPacket.PacketType.PLAYER_LIST);
			out.write(listPacket.build().toByteArray());

			byte[] lobbyData = new byte[1024];	//getting server response
			int count = inFromServer.read(lobbyData);
			lobbyData = Arrays.copyOf(lobbyData, count);
			TcpPacket.PlayerListPacket lobbyMsg = TcpPacket.PlayerListPacket.parseFrom(lobbyData);
			System.out.println(lobbyMsg);
		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
	}

    public static void changeWord(String newWord){
        word=newWord;
        System.out.println(word);
    }

    public static void youCanGuess(){
        canGuess=true;
    }

    public static void setTime(int timeLeft){
        time=timeLeft;
    }

    public static int getTime(){
        return(time);
    }

}
