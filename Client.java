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

	private static String createLobby(DataOutputStream out, InputStream inFromServer){
		String lobbyId="";
		try {
			TcpPacket.CreateLobbyPacket.Builder createPacket = TcpPacket.CreateLobbyPacket.newBuilder();
				createPacket.setType(TcpPacket.PacketType.CREATE_LOBBY)
				.setMaxPlayers(8);
			out.write(createPacket.build().toByteArray());

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

	private static Player.Builder createPlayer(String name){
		Player.Builder player = Player.newBuilder();
			player.setName(name)
			.build();
		return player;
	}

	private static void joinLobby(Player.Builder player, String lobbyId, DataOutputStream out, InputStream inFromServer){
		try {
			TcpPacket.ConnectPacket.Builder connectPacket = TcpPacket.ConnectPacket.newBuilder();
				connectPacket.setType(TcpPacket.PacketType.CONNECT)
				.setPlayer(player)
				.setLobbyId(lobbyId);
			out.write(connectPacket.build().toByteArray());

			byte[] lobbyData = new byte[1024];	//getting server response
			int count = inFromServer.read(lobbyData);
			lobbyData = Arrays.copyOf(lobbyData, count);
			TcpPacket.ConnectPacket lobbyMsg = TcpPacket.ConnectPacket.parseFrom(lobbyData);
			System.out.println(lobbyMsg);
		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
	}

	private static void chatLobby(Player.Builder player, DataOutputStream out, InputStream inFromServer, BufferedReader stdin){
		try {
			TcpPacket.DisconnectPacket.Builder disconnectPacket = null;
			do{
				System.out.print("Enter message: ");
				String msg=stdin.readLine();

				//send typed message to lobby
				TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
					chatPacket.setType(TcpPacket.PacketType.CHAT)
					.setPlayer(player)
					.setMessage(msg);
				out.write(chatPacket.build().toByteArray());

				byte[] lobbyData = new byte[1024];	//getting server response
				int count = inFromServer.read(lobbyData);
				lobbyData = Arrays.copyOf(lobbyData, count);
				TcpPacket.ChatPacket lobbyMsg = TcpPacket.ChatPacket.parseFrom(lobbyData);
				System.out.println(lobbyMsg);

				if(msg.equals("quit")){	//start disconnecting when 'quit' is typed
					disconnectPacket = TcpPacket.DisconnectPacket.newBuilder();
						disconnectPacket.setType(TcpPacket.PacketType.DISCONNECT)
						.setPlayer(player);
					out.write(disconnectPacket.build().toByteArray());
				}
			}while(disconnectPacket==null);

		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
	}

	public static void main(String[] args) {
		try {
			//connecting to server
			String serverName = args[0];
			int port = Integer.parseInt(args[1]);
			Socket server = new Socket(serverName, port);
			server.setSoTimeout(10000);
			System.out.println("Just connected to " + server.getRemoteSocketAddress());

			OutputStream outToServer = server.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			InputStream inFromServer = server.getInputStream();

			BufferedReader stdin=new BufferedReader(new InputStreamReader(System.in));
			PrintStream stdout=System.out;

			//getting player information
			stdout.print("Enter player name: ");
			String name=stdin.readLine();
			Player.Builder player = createPlayer(name);
			String lobbyId="";

			int choice = mainMenu();
			switch(choice){
				case 1:
					lobbyId=createLobby(out, inFromServer);
					stdout.print(lobbyId);
					joinLobby(player, lobbyId, out, inFromServer);
					break;
				case 2:
					stdout.print("Enter lobby Id: ");
					lobbyId=stdin.readLine();
					joinLobby(player, lobbyId, out, inFromServer);
					chatLobby(player, out, inFromServer, stdin);
					break;
			}
			server.close();

		}catch(SocketTimeoutException s){
                System.out.println("Socket timed out!");
        }catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
  }
}