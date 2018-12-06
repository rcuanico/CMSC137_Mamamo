import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatSender extends Thread{
	private Player player;
	private DataOutputStream out;
	private String msg;

	public ChatSender (Player player, DataOutputStream out, String msg){
		this.player=player;
		this.out=out;
		this.msg=msg;
	}

	public void run() {
		try{
			BufferedReader stdin=new BufferedReader(new InputStreamReader(System.in));
			TcpPacket.DisconnectPacket.Builder disconnectPacket = null;

			System.out.println(msg);
			
			if(msg.equals("quit")){	//start disconnecting when 'quit' is typed
				disconnectPacket = TcpPacket.DisconnectPacket.newBuilder();
					disconnectPacket.setType(TcpPacket.PacketType.DISCONNECT)
					.setPlayer(player);
				out.write(disconnectPacket.build().toByteArray());
			}else{
				//send typed message to lobby
				TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
					chatPacket.setType(TcpPacket.PacketType.CHAT)
					.setPlayer(player)
					.setMessage(msg);
				out.write(chatPacket.build().toByteArray());
			}

		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
	}
}
