import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.awt.*;
import javax.swing.*;

public class ServerReceiver extends Thread{
	private static InputStream inFromServer;
	private Boolean listen=true;
    private String word;
    private static Player player;
    private static DataOutputStream out;

	public ServerReceiver (InputStream inFromServer, String word, Player player, DataOutputStream out){
		this.inFromServer = inFromServer;
        this.word=word;
        this.player=player;
        this.out=out;
	}

	public void run() {
		Countdown cd = new Countdown(player, out);
		while(listen){
			try{
				byte[] lobbyData = new byte[1024];	//getting server response
				int count = inFromServer.read(lobbyData);
				if(cd.getSecs()==0){
					lobbyData = Arrays.copyOf(lobbyData, count);
					TcpPacket packet = TcpPacket.parseFrom(lobbyData);
					if(packet.getType()==TcpPacket.PacketType.CONNECT){
						TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
							chatPacket.setType(TcpPacket.PacketType.CHAT)
							.setPlayer(player)
							.setMessage("The word to guess is: "+word);
						out.write(chatPacket.build().toByteArray());
					}
				}else{
					listen=false;
				}
			}catch(IOException e) { // error cannot connect to server
			  e.printStackTrace();
			  System.out.println("Cannot find (or disconnected from) Server");
			}
		}
	}
}