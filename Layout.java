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

public class Layout{
    final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
    final static Canvas canvas = new Canvas();

    private static DataOutputStream out;
	private static InputStream inFromServer;
	private static Player player;
	private String lobbyId;
	private static JTextArea chats;
	private static JPanel msgArea;

	private Runnable chatSender;
	private Runnable chatReceiver;

    public Layout (Player player, String lobbyId, DataOutputStream out, InputStream inFromServer) {
        this.out=out;
        this.inFromServer=inFromServer;
        this.player=player;
        this.lobbyId=lobbyId;

        joinLobby();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                chatLobby();
            }
        });
    }

    public static void addComponentsToPane(JFrame frame) {
    	Container pane = frame.getContentPane();

    	JPanel mainPanel = new JPanel(new BorderLayout());
    	mainPanel.setPreferredSize(new Dimension(600,600));
    	pane.add(mainPanel);

    	JPanel topPanel = new JPanel(new BorderLayout());
    	topPanel.setPreferredSize(new Dimension(600,450));
    	mainPanel.add(topPanel, BorderLayout.NORTH);

    	JPanel leftPanel = new JPanel(new BorderLayout());
    	leftPanel.setPreferredSize(new Dimension(200,450));
    	topPanel.add(leftPanel, BorderLayout.WEST);

    	//=============USERNAME===============//
    	JTextArea name = new JTextArea(player.getName());
    	name.setPreferredSize(new Dimension(200,25));
    	name.setEditable(false);
    	name.setOpaque(true);
    	leftPanel.add(name, BorderLayout.NORTH);

    	//===============SCORE================//
    	JTextArea score = new JTextArea("Score: ");
    	score.setPreferredSize(new Dimension(200,25));
    	score.setEditable(false);
    	score.setOpaque(true);
    	leftPanel.add(score, BorderLayout.CENTER);

  //   	//============FOR ALL CHATS============//
    	JPanel chatPanel = new JPanel(new BorderLayout());
		chatPanel.setPreferredSize(new Dimension(200,400));
		chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		leftPanel.add(chatPanel, BorderLayout.SOUTH);

		chats=new JTextArea("");
		chats.setPreferredSize(new Dimension(200,300));
		chats.setEditable(false);
		chats.setOpaque(false);
		chatPanel.add(chats, BorderLayout.NORTH);

		msgArea=new JPanel(new BorderLayout());
		msgArea.setPreferredSize(new Dimension(200,50));
		msgArea.setOpaque(false);
		chatPanel.add(msgArea,BorderLayout.SOUTH);

		JTextArea msgHere = new JTextArea("");
		chats.setOpaque(false);
		msgHere.setPreferredSize(new Dimension(150,50));
		msgArea.add(msgHere, BorderLayout.CENTER);

		JButton sendMsg = new JButton("Send");
		sendMsg.setPreferredSize(new Dimension(50,50));
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
				}
				msgHere.setText("");
			}
		});

		//=============AREA TO DRAW==============//
		JPanel drawPanel = new JPanel(new BorderLayout());
    	drawPanel.setPreferredSize(new Dimension(400,450));
    	topPanel.add(drawPanel, BorderLayout.CENTER);
    	drawPanel.setBackground(new Color(85,107,47));

    	//======ALL PLAYER SCORES AND EXIT==========//
    	JPanel bottomPanel = new JPanel(new BorderLayout());
    	bottomPanel.setPreferredSize(new Dimension(600,150));
    	mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    	//=============ALL PLAYERS============//
    	System.out.println(getAllPlayers().length());
    	//getAllPlayers();
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GridBagLayoutDemo");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setPreferredSize(new Dimension(1200, 800));
        frame.setPreferredSize(new Dimension(600, 600));

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
				TcpPacket.ConnectPacket lobbyMsg = TcpPacket.ConnectPacket.parseFrom(lobbyData);
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
		ChatReceiver receiver = new ChatReceiver(inFromServer, chats);
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
}