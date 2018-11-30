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

public class Layout {
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

    public Layout (Player player, String lobbyId, DataOutputStream out, InputStream inFromServer) {
        this.out=out;
        this.inFromServer=inFromServer;
        this.player=player;
        this.lobbyId=lobbyId;
        joinLobby();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void addComponentsToPane(Container pane) {
    	JPanel mainpanel = new JPanel(new BorderLayout());
		mainpanel.setPreferredSize(new Dimension(600,600));
		mainpanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		pane.add(mainpanel);

		chats=new JTextArea("Hello");
		chats.setPreferredSize(new Dimension(600,400));
		chats.setEditable(false);
		chats.setOpaque(false);
		mainpanel.add(chats, BorderLayout.NORTH);

		msgArea=new JPanel(new BorderLayout());
		msgArea.setPreferredSize(new Dimension(600,400));
		msgArea.setOpaque(false);
		mainpanel.add(msgArea,BorderLayout.CENTER);

		chatLobby();

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

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GridBagLayoutDemo");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setPreferredSize(new Dimension(1200, 800));
        frame.setPreferredSize(new Dimension(600, 600));

        //Set up the content pane.
        //Chat chat = new Chat(frame, player, out, inFromServer);
        addComponentsToPane(frame.getContentPane());

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
		ChatSender sender = new ChatSender(player, out);
		sender.start();
		ChatReceiver receiver = new ChatReceiver(inFromServer, chats);
		receiver.start();

		try{
			receiver.join();
			sender.join();
		}catch(InterruptedException e){}
	}
}