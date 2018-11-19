import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Host_Player{
	public static void main(String[] args) {
		try {
			String serverName = args[0];
			int port = Integer.parseInt(args[1]); //get port from second param
			Socket server = new Socket(serverName, port);
			server.setSoTimeout(10000);
			System.out.println("Just connected to " + server.getRemoteSocketAddress());

			OutputStream outToServer = server.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			InputStream inFromServer = server.getInputStream();

			//---------------------------------- Create host player-----------------------------//
			Player.Builder host = Player.newBuilder();
				host.setName("Host")
				.build();
			//---------------------------------------------------------------------------------//

			//------------------------------(1) Create lobby packet----------------------------//
			TcpPacket.CreateLobbyPacket.Builder createPacket = TcpPacket.CreateLobbyPacket.newBuilder();
				createPacket.setType(TcpPacket.PacketType.CREATE_LOBBY)
				.setMaxPlayers(8);
			out.write(createPacket.build().toByteArray());

			byte[] lobbyData = new byte[1024];	//getting server response
			int count = inFromServer.read(lobbyData);
			lobbyData = Arrays.copyOf(lobbyData, count);
			String lobbyId = TcpPacket.CreateLobbyPacket.parseFrom(lobbyData).getLobbyId();	//get id of created lobby
			System.out.println(lobbyId);
			//---------------------------------------------------------------------------------//

			//-----------------------------------(2) Join lobby--------------------------------//
			TcpPacket.ConnectPacket.Builder connectPacket = TcpPacket.ConnectPacket.newBuilder();
				connectPacket.setType(TcpPacket.PacketType.CONNECT)
				.setPlayer(host)
				.setLobbyId(lobbyId);
			out.write(connectPacket.build().toByteArray());

			// byte[] lobbyData = new byte[1024];	//getting server response
			// int count = inFromServer.read(lobbyData);
			// lobbyData = Arrays.copyOf(lobbyData, count);
			// TcpPacket.ConnectPacket lobbyMsg = TcpPacket.ConnectPacket.parseFrom(lobbyData);
			// System.out.println(lobbyMsg);
			//-----------------------------------------------------------------------------------//
			//-----------------------------------(3) Send message--------------------------------//
			TcpPacket.DisconnectPacket.Builder disconnectPacket = null;
			BufferedReader stdin=new BufferedReader(new InputStreamReader(System.in));
			PrintStream stdout=System.out;
			do{
				stdout.print("Enter message: ");
				String msg=stdin.readLine();

				//send typed message to lobby
				TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
					chatPacket.setType(TcpPacket.PacketType.CHAT)
					.setPlayer(host)
					.setMessage(msg);
				out.write(connectPacket.build().toByteArray());

				// lobbyData = new byte[1024];	//getting server response
				// count = inFromServer.read(lobbyData);
				// lobbyData = Arrays.copyOf(lobbyData, count);
				// TcpPacket.ChatPacket lobbyMsg = TcpPacket.ChatPacket.parseFrom(lobbyData);
				// System.out.println(lobbyMsg);

				if(msg.equals("quit")){	//start disconnecting when 'quit' is typed
					disconnectPacket = TcpPacket.DisconnectPacket.newBuilder();
						disconnectPacket.setType(TcpPacket.PacketType.DISCONNECT)
						.setPlayer(host);
					out.write(disconnectPacket.build().toByteArray());
				}
			}while(disconnectPacket==null);
			//-----------------------------------------------------------------------------------//
		}catch(SocketTimeoutException s){
                System.out.println("Socket timed out!");
        }catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
  }
}