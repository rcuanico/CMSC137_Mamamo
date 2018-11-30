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

public class Chat extends JDialog {
	private static DataOutputStream out;
	private static InputStream inFromServer;
	private static Player player;
	private Container pane;

	private static JTextArea chats;
	private JPanel msgArea;

	public Chat (JFrame frame, Player player, DataOutputStream out, InputStream inFromServer) {
	 	super(frame);
	 	this.out=out;
        this.inFromServer=inFromServer;
        this.player=player;

        JPanel mainpanel = new JPanel(new BorderLayout());
		mainpanel.setPreferredSize(new Dimension(600,600));
		mainpanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(mainpanel);

		chats=new JTextArea("");
		chats.setPreferredSize(new Dimension(600,400));
		chats.setEditable(false);
		chats.setOpaque(false);
		mainpanel.add(chats, BorderLayout.NORTH);

		msgArea=new JPanel(new BorderLayout());
		msgArea.setPreferredSize(new Dimension(600,400));
		msgArea.setOpaque(false);
		mainpanel.add(msgArea,BorderLayout.CENTER);

		chatLobby();

		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);	//to make it appear on center of screen
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