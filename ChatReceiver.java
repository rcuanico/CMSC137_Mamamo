import proto.TcpPacketProtos.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.awt.*;
import javax.swing.*;

public class ChatReceiver extends Thread{
	private static InputStream inFromServer;
	private Boolean listen=true;
	private JTextArea chats;

	public ChatReceiver (InputStream inFromServer, JTextArea chats){
		this.chats=chats;
		this.inFromServer = inFromServer;
	}

	public void run() {
		while(listen){
			try{
				byte[] lobbyData = new byte[1024];	//getting server response
				int count = inFromServer.read(lobbyData);
				if(count>=0){
					lobbyData = Arrays.copyOf(lobbyData, count);
					TcpPacket packet = TcpPacket.parseFrom(lobbyData);
					if(packet.getType()==TcpPacket.PacketType.CHAT){
						TcpPacket.ChatPacket lobbyMsg = TcpPacket.ChatPacket.parseFrom(lobbyData);
						chats.setText(chats.getText()+lobbyMsg.getPlayer().getName()+": "+lobbyMsg.getMessage()+"\n");
						System.out.println(lobbyMsg.getPlayer().getName()+": "+lobbyMsg.getMessage());
					}else if(packet.getType()==TcpPacket.PacketType.CONNECT){
						TcpPacket.ConnectPacket lobbyMsg = TcpPacket.ConnectPacket.parseFrom(lobbyData);
						chats.setText(chats.getText()+lobbyMsg.getPlayer().getName()+" has connected to the lobby."+"\n");
						System.out.println(lobbyMsg.getPlayer().getName()+" has connected to the lobby.");
					}else if(packet.getType()==TcpPacket.PacketType.DISCONNECT){
						TcpPacket.DisconnectPacket lobbyMsg = TcpPacket.DisconnectPacket.parseFrom(lobbyData);
						chats.setText(chats.getText()+lobbyMsg.getPlayer().getName()+" has disconnected from the lobby."+"\n");
						System.out.println(lobbyMsg.getPlayer().getName()+" has disconnected from the lobby.");
					}
					chats.update(chats.getGraphics());
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