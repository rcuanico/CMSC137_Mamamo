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
		mainpanel.setBackground(new Color(85,107,47));

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

		Border border=BorderFactory.createMatteBorder(10, 100, 20, 100, new Color(85,107,47));
		Font buttonFont=new Font("Arial", Font.PLAIN, 20);

		//newGame button
		JButton newGame = new JButton("Start Game");
		options.add(newGame);
		newGame.setBorder(border);
		newGame.setFont(buttonFont);
		newGame.setBackground(new Color(200,143,76));
		newGame.setForeground(Color.BLACK);
		//joinGame button
		JButton joinGame = new JButton("Join Game");
		options.add(joinGame);
		joinGame.setBorder(border);
		joinGame.setFont(buttonFont);
		joinGame.setBackground(new Color(200,143,76));
		joinGame.setForeground(Color.BLACK);
		//see instructions
		JButton instrucs = new JButton("Instructions");
		options.add(instrucs);
		instrucs.setBorder(border);
		instrucs.setFont(buttonFont);
		instrucs.setBackground(new Color(200,143,76));
		instrucs.setForeground(Color.BLACK);
		//exitGame button
		JButton exitGame = new JButton("Exit Game");
		options.add(exitGame);
		exitGame.setBorder(border);
		exitGame.setFont(buttonFont);
		exitGame.setBackground(new Color(200,143,76));
		exitGame.setForeground(Color.BLACK);
		// ===================================================== //

		// ================ Card for the instructions ========== //
		JPanel instrucPanel=new JPanel(new BorderLayout());
		instrucPanel.setPreferredSize(new Dimension(100,400));
		cardPanel.add(instrucPanel, "Instructions");
		instrucPanel.setOpaque(false);

		JTextArea howToPlay= new JTextArea("HOW TO PLAY\n");
		howToPlay.setFont(new Font("Arial", Font.BOLD, 18));
		howToPlay.setEditable(false);
		howToPlay.setOpaque(false);
		howToPlay.setForeground(new Color(200,143,76));
		instrucPanel.add(howToPlay, BorderLayout.NORTH);

		JTextArea instrucText=new JTextArea("Sudoku is a logic-based,combinatorial, number-placement puzzle, where the objective is to fill out an n × n grid with digits such that each row, column, and subgrid contain all the digits from 1 to n . Commonly, n is a perfect square so that √n resolves to a whole number.\n\nSudoku X is a twist on the original Sudoku puzzle that requires the main diagonals, in addition to the rows, columns, and subgrids, to also have all the numbers from 1 to n.\n\nSudoku Y is another twist on the original Sudoku puzzle that requires the upper halves of the main diagonals and the lower half of the center column to also have all the numbers from 1 to n.\n\nSudoku XY requires a solution to fulfill both the Sudoku X and Sudoku Y conditions.");
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
		back.setBackground(new Color(200,143,76));

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