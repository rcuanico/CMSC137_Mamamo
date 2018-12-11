import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class Main {
	private static Socket server;
	private static OutputStream outToServer;
	private static DataOutputStream out;
	private static InputStream inFromServer;

	public static void showMainMenu(JFrame frame) {
		//main panel
		JPanel mainpanel = new JPanel(new BorderLayout());
		mainpanel.setPreferredSize(new Dimension(600,600));
		mainpanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainpanel.setBackground(Color.decode("#3498db"));

		//empty panel
		JPanel northPanel = new JPanel();
		northPanel.setPreferredSize(new Dimension(600,80));
		mainpanel.add(northPanel, BorderLayout.NORTH);
		northPanel.setOpaque(false);

		//panel for the game title
		JPanel titlePanel = new JPanel();
		titlePanel.setPreferredSize(new Dimension(600,100));
		titlePanel.setOpaque(false);

		JTextArea title=new JTextArea("Picture This!");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Arial", Font.BOLD, 60));
		title.setEditable(false);
		title.setOpaque(false);

		titlePanel.add(title);
		mainpanel.add(titlePanel, BorderLayout.CENTER);

		JPanel cardPanel=new JPanel(new CardLayout());
		cardPanel.setPreferredSize(new Dimension(100,400));
		cardPanel.setOpaque(false);
		mainpanel.add(cardPanel, BorderLayout.SOUTH);
		// ========= Card for the main menu buttons ========== //
		//panel for the buttons
		JPanel options = new JPanel(new GridLayout(4,3));
		options.setPreferredSize(new Dimension(100,350));
		cardPanel.add(options, "Options");
		options.setOpaque(false);

		Border border=BorderFactory.createMatteBorder(10, 100, 20, 100, Color.decode("#3498db"));
		Font buttonFont=new Font("Arial", Font.PLAIN, 20);

		//newGame button
		JButton newGame = new JButton("Start Game");
		options.add(newGame);
		newGame.setBorder(border);
		newGame.setFont(buttonFont);
		newGame.setBackground(Color.decode("#f1c40f"));
		newGame.setForeground(Color.BLACK);
		//joinGame button
		JButton joinGame = new JButton("Join Game");
		options.add(joinGame);
		joinGame.setBorder(border);
		joinGame.setFont(buttonFont);
		joinGame.setBackground(Color.decode("#f1c40f"));
		joinGame.setForeground(Color.BLACK);
		//see instructions
		JButton instrucs = new JButton("Instructions");
		options.add(instrucs);
		instrucs.setBorder(border);
		instrucs.setFont(buttonFont);
		instrucs.setBackground(Color.decode("#f1c40f"));
		instrucs.setForeground(Color.BLACK);
		//exitGame button
		JButton exitGame = new JButton("Exit Game");
		options.add(exitGame);
		exitGame.setBorder(border);
		exitGame.setFont(buttonFont);
		exitGame.setBackground(Color.decode("#f1c40f"));
		exitGame.setForeground(Color.BLACK);
		// ===================================================== //

		// ================ Card for the instructions ========== //
		JPanel instrucPanel=new JPanel(new BorderLayout());
		instrucPanel.setPreferredSize(new Dimension(100,400));
		cardPanel.add(instrucPanel, "Instructions");
		instrucPanel.setOpaque(false);

		JTextArea howToPlay= new JTextArea("HOW TO PLAY\n");
		howToPlay.setFont(new Font("Arial", Font.BOLD, 17));
		howToPlay.setEditable(false);
		howToPlay.setOpaque(false);
		howToPlay.setForeground(Color.decode("#3498db"));
		instrucPanel.add(howToPlay, BorderLayout.NORTH);

		JTextArea instrucText=new JTextArea("There will be two player types: the guessers and the artist. \n The artist will draw on the space provided the word that will be given to them. \n The guesser must guess the word being drawn as quick as possible. \n\n Instructions:\nThe host player will select the number of rounds to be played. The minimum number of rounds is 3.\nA new artist will be selected per round. This is to be done randomly.\nThe selected artist will be assigned a word that they will draw while the guessers try and guess using the chat box. Each round lasts one minute.\n\nScoring:\nThe points a guesser will receive will depend on how fast they were able to guess the word. The score will start from 60, and as the timer starts, one point will be deducted per second.\nThe points an artist will receive depends on how many players were able to guess the word. The artist will receive 60% of the points that each guesser received.\n\nEndgame:\nOnce the specified number of rounds have been reached, the scores will be totaled and the player with the most points wins.");
		instrucText.setLineWrap(true);
		instrucText.setWrapStyleWord(true);
		instrucText.setEditable(false);
		instrucText.setOpaque(false);
		instrucText.setFont(new Font("Arial", Font.PLAIN, 14));
		instrucText.setForeground(Color.WHITE);
		instrucPanel.add(instrucText, BorderLayout.CENTER);

		JButton back = new JButton("Back to Main Menu");
		instrucPanel.add(back, BorderLayout.SOUTH);
		back.setFont(buttonFont);
		back.setBackground(Color.decode("#f1c40f"));

		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout)(cardPanel.getLayout());
    			cl.show(cardPanel, "Options");
			}
		});

		// ===================================================== //

		Container container = frame.getContentPane();
		container.add(mainpanel);

		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGameDialog ng = new newGameDialog(frame, out, inFromServer);
			}
		});

		joinGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				joinGameDialog soln = new joinGameDialog(frame, out, inFromServer);
			}
		});

		instrucs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout)(cardPanel.getLayout());
    			cl.show(cardPanel, "Instructions");
			}
		});

		exitGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
	}

	public static void createAndShowGUI() {
		// window frame
		JFrame frame = new JFrame("Picture This!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		showMainMenu(frame);
		frame.pack();
		//to make it appear on center of screen
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		try { 
			String serverName = args[0];
			int port = Integer.parseInt(args[1]);
			server = new Socket(serverName, port);
			System.out.println("Just connected to " + server.getRemoteSocketAddress());

			outToServer = server.getOutputStream();
			out = new DataOutputStream(outToServer);
			inFromServer = server.getInputStream();

			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI();
				}
			});
		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
	}
}
