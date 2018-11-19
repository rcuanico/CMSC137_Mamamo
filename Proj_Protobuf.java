//run using java Proj_Protobuf 202.92.144.45 80

import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;


public class Proj_Protobuf{

	public static void main(String[] args) {
		try{
			String serverName = args[0];
			int port = Integer.parseInt(args[1]); //get port from second param
			Socket server = new Socket(serverName, port);
			server.setSoTimeout(10000);
			System.out.println("Just connected to " + server.getRemoteSocketAddress());

			OutputStream outToServer = server.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outToServer);
			InputStream inFromServer = server.getInputStream();
			ObjectInputStream in = new ObjectInputStream(inFromServer);

			Player.Builder host = Player.newBuilder();
				host.setName("Host")
				.build()
				.writeTo(outToServer);

			// TcpPacket.CreateLobbyPacket.Builder createPac = TcpPacket.CreateLobbyPacket.newBuilder();
			// 	createPac.setType(TcpPacket.PacketType.CREATE_LOBBY)
			// 	.setMaxPlayers(3)
			// 	.build();

			// TcpPacket.ConnectPacket.Builder conPac = TcpPacket.ConnectPacket.newBuilder();
			// 	conPac.setType(TcpPacket.PacketType.CONNECT)
			// 	.setPlayer(host)
			// 	.setLobbyId("AB3L")
			// 	.build()
			// 	.writeTo(outToServer);

			//out.write(conPac.build().toByteArray());

			//System.out.println("Server says " + TcpPacket.parseFrom(inFromServer));

			// TcpPacket.ChatPacket.Builder chatPac = TcpPacket.ChatPacket.newBuilder();
			// 	chatPac.setType(TcpPacket.PacketType.CHAT)
			// 	.setPlayer(host)
			// 	.setMessage("Hello!")
			// 	.build();
			//server.close();

		}catch(SocketTimeoutException s){
                System.out.println("Socket timed out!");
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Cannot find (or disconnected from) Server");
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Usage: java Proj_Protobuf <server-ip> <port-no.>");
        }
	}
}
