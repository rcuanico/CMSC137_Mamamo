import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Players{
	public static void main(String[] args) {
		try {
			String serverName = args[0];
			int port = Integer.parseInt(args[1]); //get port from second param
			String lobbyId = args[2];
			Socket server = new Socket(serverName, port);
			server.setSoTimeout(10000);
			System.out.println("Just connected to " + server.getRemoteSocketAddress());

			OutputStream outToServer = server.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			InputStream inFromServer = server.getInputStream();
			//---------------------------------- Create new player-----------------------------//
			Player.Builder player = Player.newBuilder();
				player.setName("Player")
				.build();
			//---------------------------------------------------------------------------------//
			//-----------------------------------(2) Join lobby--------------------------------//
			TcpPacket.ConnectPacket.Builder connectPacket = TcpPacket.ConnectPacket.newBuilder();
				connectPacket.setType(TcpPacket.PacketType.CONNECT)
				.setPlayer(player)
				.setLobbyId(lobbyId);
			out.write(connectPacket.build().toByteArray());
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
					.setPlayer(player)
					.setMessage(msg);
				out.write(connectPacket.build().toByteArray());

				if(msg.equals("quit")){	//start disconnecting when 'quit' is typed
					disconnectPacket = TcpPacket.DisconnectPacket.newBuilder();
						disconnectPacket.setType(TcpPacket.PacketType.DISCONNECT)
						.setPlayer(player);
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