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

public class newGameDialog extends JDialog {
	private final Font MYFONT = new Font("Arial", Font.BOLD, 15);
	private JTextArea nameMsg;
	private JTextArea name;
	private JTextArea numPlayMsg;
	private JTextArea numPlay;
	private JTextArea numRoundMsg;
	private static JTextArea numRound;
	private static JTextArea lobbyMsg;

	private static DataOutputStream out;
	private static InputStream inFromServer;
	private Player player;
	private String lobbyId;

	public newGameDialog(JFrame frame, DataOutputStream out, InputStream inFromServer){
		super(frame);
		setTitle("Picture This!");
		this.out=out;
		this.inFromServer=inFromServer;

		JPanel cardPanel=new JPanel(new CardLayout());
		cardPanel.setPreferredSize(new Dimension(350,200));
		cardPanel.setOpaque(false);
		getContentPane().add(cardPanel);

		JPanel getInfo = new JPanel(new GridLayout(7,1));
		getInfo.setPreferredSize(new Dimension(350,200));
		getInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
		getInfo.setBackground(Color.decode("#3498db"));

		nameMsg = new JTextArea("Enter your name: ");
		nameMsg.setFont(MYFONT);
		nameMsg.setForeground(Color.WHITE);
		nameMsg.setEditable(false);
		nameMsg.setOpaque(false);
		getInfo.add(nameMsg);

		name = new JTextArea("");
		name.setFont(MYFONT);
		name.setForeground(Color.WHITE);
		name.setOpaque(false);
		getInfo.add(name);

		numPlayMsg = new JTextArea("Enter the maximum number of players: ");
		numPlayMsg.setFont(MYFONT);
		numPlayMsg.setForeground(Color.WHITE);
		numPlayMsg.setEditable(false);
		numPlayMsg.setOpaque(false);
		getInfo.add(numPlayMsg);

		numPlay = new JTextArea("");
		numPlay.setFont(MYFONT);
		numPlay.setForeground(Color.WHITE);
		numPlay.setOpaque(false);
		getInfo.add(numPlay);

		numRoundMsg = new JTextArea("Enter the number of rounds: ");
		numRoundMsg.setFont(MYFONT);
		numRoundMsg.setForeground(Color.WHITE);
		numRoundMsg.setEditable(false);
		numRoundMsg.setOpaque(false);
		getInfo.add(numRoundMsg);

		numRound = new JTextArea("");
		numRound.setFont(MYFONT);
		numRound.setForeground(Color.WHITE);
		numRound.setOpaque(false);
		getInfo.add(numRound);

		JButton newLobby = new JButton("Create Lobby");
		newLobby.setBackground(Color.decode("#f1c40f"));
		newLobby.setForeground(Color.BLACK);
		getInfo.add(newLobby);
		cardPanel.add(getInfo, "getInfo");

		JPanel showID = new JPanel(new GridLayout(2,1));
		showID.setPreferredSize(new Dimension(350,200));
		showID.setBorder(new EmptyBorder(10, 10, 10, 10));
		showID.setBackground(Color.decode("#3498db"));

		lobbyMsg = new JTextArea("");
		lobbyMsg.setFont(MYFONT);
		lobbyMsg.setForeground(Color.WHITE);
		lobbyMsg.setEditable(false);
		lobbyMsg.setOpaque(false);
		showID.add(lobbyMsg);

		JButton startGame = new JButton("Start Game");
		startGame.setBackground(Color.decode("#f1c40f"));
		startGame.setForeground(Color.BLACK);
		showID.add(startGame);

		cardPanel.add(showID, "showID");

		// ========== ACTION LISTENER ========== //
		newLobby.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player = createPlayer(name.getText());
				lobbyId=createLobby(Integer.parseInt(numPlay.getText()));
				addID(lobbyId);
				CardLayout cl = (CardLayout)(cardPanel.getLayout());
    			cl.show(cardPanel, "showID");
			}
		});

		startGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Layout layout = new Layout(player, lobbyId, out, inFromServer);
				frame.dispose();
			}
		});

		this.pack();
		this.setFocusable(true);
		this.setVisible(true);
		this.setLocationRelativeTo(null);	//to make it appear on center of screen
	}

	private static void addID(String lobbyId){
		lobbyMsg.setText("Lobby successfully created: Lobby ID:\n"+lobbyId);
	}

	private static String createLobby(int numPlay){
		String lobbyId="";
		try {
			TcpPacket.CreateLobbyPacket createPacket = TcpPacket.CreateLobbyPacket.newBuilder()
				.setType(TcpPacket.PacketType.CREATE_LOBBY)
				.setMaxPlayers(numPlay)
				.build();
			out.write(createPacket.toByteArray());

			byte[] lobbyData = new byte[1024];	//getting server response
			int count = inFromServer.read(lobbyData);
			lobbyData = Arrays.copyOf(lobbyData, count);
			lobbyId = TcpPacket.CreateLobbyPacket.parseFrom(lobbyData).getLobbyId();	//get id of created lobby

			Runtime rt = Runtime.getRuntime();
			String str = "java GameServer 202.92.144.45 80 " + Integer.parseInt(numRound.getText()) +" " + lobbyId;
			Process pr = rt.exec(str);

		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
		return (lobbyId);
	}

	private static Player createPlayer (String name){
		Player player = Player.newBuilder()
			.setName(name)
			.build();
		return player;
	}
}
