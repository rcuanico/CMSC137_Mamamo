import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.awt.*;
import javax.swing.*;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerReceiver extends Thread{
	private static InputStream inFromServer;
	private Boolean listen=true;
    private String word;
    private static Player player;
    private static DataOutputStream out;
    private static int numRound;

	public ServerReceiver (InputStream inFromServer, Player player, DataOutputStream out, int numRound){
		this.inFromServer = inFromServer;
        this.player=player;
        this.out=out;
        this.numRound=numRound;
	}

	public void run() {
		int i=0;
		try{
			while(i<numRound){
				Countdown cd = new Countdown(player, out);
				Random rand=new Random();
				int randomNum = rand.nextInt(106);
	    		String word = Files.readAllLines(Paths.get("wordpool.txt")).get(randomNum);
	    		TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
						chatPacket.setType(TcpPacket.PacketType.CHAT)
						.setPlayer(player)
						.setMessage("The word to guess is: "+word);
					out.write(chatPacket.build().toByteArray());

				try{
					while(!cd.didStop()){
						byte[] lobbyData = new byte[1024];	//getting server response
						int count = inFromServer.read(lobbyData);
						lobbyData = Arrays.copyOf(lobbyData, count);
						TcpPacket packet = TcpPacket.parseFrom(lobbyData);
						if(packet.getType()==TcpPacket.PacketType.CONNECT){
							chatPacket = TcpPacket.ChatPacket.newBuilder();
								chatPacket.setType(TcpPacket.PacketType.CHAT)
								.setPlayer(player)
								.setMessage("The word to guess is: "+word);
							out.write(chatPacket.build().toByteArray());
						}
					}

					chatPacket = TcpPacket.ChatPacket.newBuilder();
						chatPacket.setType(TcpPacket.PacketType.CHAT)
						.setPlayer(player)
						.setMessage("Time's up! The correct word is: "+word+". Starting a new round...");
					out.write(chatPacket.build().toByteArray());
					i++;
				}catch(IOException e) { // error cannot connect to server
				  e.printStackTrace();
				  System.out.println("Cannot read file");
				}
			}
			TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
				chatPacket.setType(TcpPacket.PacketType.CHAT)
				.setPlayer(player)
				.setMessage("THE GAME IS OVER!");
			out.write(chatPacket.build().toByteArray());
		}catch(IOException e) { // error cannot connect to server
		  e.printStackTrace();
		  System.out.println("Cannot read file");
		}
	}
}