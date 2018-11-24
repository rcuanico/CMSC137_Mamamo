import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class Client{
	private static int mainMenu(){
		Scanner sc = new Scanner(System.in);
		System.out.print("MENU:\n[1]Create new lobby\n[2]Connect to lobby\nChoice: ");
		return (sc.nextInt());
	}

	private static String createLobby (DataOutputStream out, InputStream inFromServer){
		String lobbyId="";
		try {
			TcpPacket.CreateLobbyPacket createPacket = TcpPacket.CreateLobbyPacket.newBuilder()
				.setType(TcpPacket.PacketType.CREATE_LOBBY)
				.setMaxPlayers(8)
				.build();
			out.write(createPacket.toByteArray());

			byte[] lobbyData = new byte[1024];	//getting server response
			int count = inFromServer.read(lobbyData);
			lobbyData = Arrays.copyOf(lobbyData, count);
			lobbyId = TcpPacket.CreateLobbyPacket.parseFrom(lobbyData).getLobbyId();	//get id of created lobby
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

	private static TcpPacket.ConnectPacket joinLobby (Player player, String lobbyId, DataOutputStream out, InputStream inFromServer){
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

	private static void chatLobby(Player player, DataOutputStream out, InputStream inFromServer){
		ChatSender sender = new ChatSender(player, out);
		sender.start();
		ChatReceiver receiver = new ChatReceiver(inFromServer);
		receiver.start();

		try{
			receiver.join();
			sender.join();
		}catch(InterruptedException e){}
	}

	public static void main(String[] args) {
		try { 
			//connecting to server
			String serverName = args[0];
			int port = Integer.parseInt(args[1]);
			Socket server = new Socket(serverName, port);
			System.out.println("Just connected to " + server.getRemoteSocketAddress());

			OutputStream outToServer = server.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			InputStream inFromServer = server.getInputStream();

			BufferedReader stdin=new BufferedReader(new InputStreamReader(System.in));
			PrintStream stdout=System.out;

			//getting player information
			stdout.print("Enter player name: ");
			String name=stdin.readLine();
			Player player = createPlayer(name);
			String lobbyId="";

			int choice = mainMenu();
			switch(choice){
				case 1:
					lobbyId=createLobby(out, inFromServer);
					stdout.print("New lobby created. Lobby ID: "+lobbyId+"\n");
					joinLobby(player, lobbyId, out, inFromServer);
					chatLobby(player, out, inFromServer);
					break;
				case 2:
					stdout.print("Enter lobby Id: ");
					lobbyId=stdin.readLine();
					joinLobby(player, lobbyId, out, inFromServer);
					chatLobby(player, out, inFromServer);
					break;
			}
			server.close();

		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
	}
}