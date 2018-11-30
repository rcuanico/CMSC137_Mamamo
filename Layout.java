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
    	//=============USERNAME===============//
    	System.out.println(player.getName());

    	//=============ALL PLAYERS============//
    	getAllPlayers();

    	//============FOR ALL CHATS============//
    	JPanel chatPanel = new JPanel(new BorderLayout());
		chatPanel.setPreferredSize(new Dimension(600,600));
		chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		pane.add(chatPanel);

		chats=new JTextArea("");
		chats.setPreferredSize(new Dimension(600,400));
		chats.setEditable(false);
		chats.setOpaque(false);
		chatPanel.add(chats, BorderLayout.NORTH);

		msgArea=new JPanel(new BorderLayout());
		msgArea.setPreferredSize(new Dimension(600,50));
		msgArea.setOpaque(false);
		chatPanel.add(msgArea,BorderLayout.SOUTH);

		JTextArea msgHere = new JTextArea("");
		chats.setOpaque(false);
		msgHere.setPreferredSize(new Dimension(400,50));
		msgArea.add(msgHere, BorderLayout.CENTER);

		JButton sendMsg = new JButton("Send");
		sendMsg.setPreferredSize(new Dimension(200,50));
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
		//================================//

  //       if (RIGHT_TO_LEFT) {
  //           pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
  //       }

  //       JButton button;
		// pane.setLayout(new GridBagLayout());
		// GridBagConstraints c = new GridBagConstraints();
		// if (shouldFill) {
		// //natural height, maximum width
		// c.fill = GridBagConstraints.HORIZONTAL;
		// }

		// button = new JButton("Username");
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.gridx = 0;
		// c.gridy = 0;
		// c.ipady = 20;
		// pane.add(button, c);

		// button = new JButton("Score");
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.gridx = 0;
		// c.gridy = 1;
		// c.ipady = 60;
		// pane.add(button, c);

		// button = new JButton("Chats");
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.gridx = 0;
		// c.gridy = 2;
		// c.ipady = 450;
		// pane.add(button, c);

		// button = new JButton("Message:");
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.weightx = 0.2;
		// c.gridx = 0;
		// c.gridy = 3;
		// c.ipady = 10;
		// pane.add(button, c);

		// button = new JButton("Canvas");
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.weightx = 0.79;
		// c.gridheight = 4;
		// c.gridx = 1;
		// c.gridy = 0;
		// c.ipady = 615;
		// pane.add(canvas, c);

		// button = new JButton("Scores and Status");
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.gridwidth = 3;
		// c.gridx = 0;
		// c.gridy = 4;
		// c.ipady = 100;
		// pane.add(button, c);

		// JPanel pallettePanel = new JPanel();
		// c.fill = GridBagConstraints.HORIZONTAL;
		// // pallettePanel.setBackground(Color.parseColor("#2c3e50"));
		// c.weightx = 0.01;
		// c.gridheight = 4;
		// c.gridx = 2;
		// c.gridy = 0;
		// c.ipady = 615;
		// pane.add(pallettePanel, c);

		// button = new JButton("EXIT");
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.weightx = 0.01;
		// c.gridx = 2;
		// c.gridy = 4;
		// c.ipady = 100;
		// pane.add(button, c);

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