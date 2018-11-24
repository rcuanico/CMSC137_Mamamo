import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;
import java.io.*;
import java.net.*;

public class ChatSender extends Thread{
	private Player player;
	private DataOutputStream out;

	public ChatSender (Player player, DataOutputStream out){
		this.player=player;
		this.out=out;
	}

	public void run() {
		try{
			BufferedReader stdin=new BufferedReader(new InputStreamReader(System.in));
			TcpPacket.DisconnectPacket.Builder disconnectPacket = null;
			do{
				String msg=stdin.readLine();

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
			}while(disconnectPacket==null);

		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot find (or disconnected from) Server");
		}
	}
}