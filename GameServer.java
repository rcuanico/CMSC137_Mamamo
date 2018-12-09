import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameServer {
	private static Socket server;
	private static OutputStream outToServer;
    private static int numRound;
    private static Player player;
    private static DataOutputStream out;
	private static InputStream inFromServer;
    private static String lobbyId;
    private static String word="";

    public static void main(String[] args) {
		try { 
			String serverName = args[0];
			int port = Integer.parseInt(args[1]);
			server = new Socket(serverName, port);
			System.out.println("Just connected to " + server.getRemoteSocketAddress());

			outToServer = server.getOutputStream();
			out = new DataOutputStream(outToServer);
			inFromServer = server.getInputStream();

			numRound=Integer.parseInt(args[2]);
			player=createPlayer("server");
			lobbyId=args[3];
			joinLobby();

		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
	}

   	private static TcpPacket.ConnectPacket joinLobby (){
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
					startGame();
				}else{
					System.out.println("Connection to lobby failed.");
				}
		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
		return connectPacket;
	}

	private static Player createPlayer (String name){
		Player player = Player.newBuilder()
			.setName(name)
			.build();
		return player;
	}

    private static void startGame(){
        chatLobby();
    }

	private static void chatLobby(){
		ServerReceiver receiver1 = new ServerReceiver(inFromServer, player, out, numRound);
		receiver1.start();
	}
}