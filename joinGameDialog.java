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

public class joinGameDialog extends JDialog {
	private final Font MYFONT = new Font("Arial", Font.BOLD, 15);
	private JTextArea nameMsg;
	private JTextArea name;
	private JTextArea lobbyMsg;
	private JTextArea lobby;

	private static DataOutputStream out;
	private static InputStream inFromServer;
	private Player player;
	private String lobbyId;

	public joinGameDialog(JFrame frame, DataOutputStream out, InputStream inFromServer){
		super(frame);
		setTitle("Picture This!");
		this.out=out;
		this.inFromServer=inFromServer;

		JPanel getInfo = new JPanel(new GridLayout(5,1));
		getInfo.setPreferredSize(new Dimension(350,200));
		getInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
		getInfo.setBackground(new Color(85,107,47));
		getContentPane().add(getInfo);

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

		lobbyMsg = new JTextArea("Enter lobby ID: ");
		lobbyMsg.setFont(MYFONT);
		lobbyMsg.setForeground(Color.WHITE);
		lobbyMsg.setEditable(false);
		lobbyMsg.setOpaque(false);
		getInfo.add(lobbyMsg);

		lobby = new JTextArea("");
		lobby.setFont(MYFONT);
		lobby.setForeground(Color.WHITE);
		lobby.setOpaque(false);
		getInfo.add(lobby);

		JButton joinLobby = new JButton("Join Game");
		joinLobby.setBackground(new Color(200,143,76));
		joinLobby.setForeground(Color.BLACK);
		getInfo.add(joinLobby);

		// ========== ACTION LISTENER ========== //
		joinLobby.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player = createPlayer(name.getText());
				lobbyId=lobby.getText();
				Layout layout = new Layout(player, lobbyId, out, inFromServer);
				frame.dispose();
			}
		});

		this.pack();
		this.setFocusable(true);
		this.setVisible(true);
		this.setLocationRelativeTo(null);	//to make it appear on center of screen
	}

	private static Player createPlayer (String name){
		Player player = Player.newBuilder()
			.setName(name)
			.build();
		return player;
	}
}