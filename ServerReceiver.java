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
			Countdown cd = new Countdown(player, out);
			Random rand=new Random();
			int randomNum = rand.nextInt(106);
			word = Files.readAllLines(Paths.get("wordpool.txt")).get(randomNum);
			TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
				chatPacket.setType(TcpPacket.PacketType.CHAT)
				.setPlayer(player)
				.setMessage("The word to guess is: "+word);
			out.write(chatPacket.build().toByteArray());

			while(i<numRound){
				try{
					byte[] lobbyData = new byte[1024];	//getting server response
					int count = inFromServer.read(lobbyData);
					if(cd.getSecs()==59){
						chatPacket = TcpPacket.ChatPacket.newBuilder();
							chatPacket.setType(TcpPacket.PacketType.CHAT)
							.setPlayer(player)
							.setMessage("The word to guess is: "+word);
						out.write(chatPacket.build().toByteArray());
					}
					if(cd.getSecs()!=0){
						lobbyData = Arrays.copyOf(lobbyData, count);
						TcpPacket packet = TcpPacket.parseFrom(lobbyData);
						if(packet.getType()==TcpPacket.PacketType.CONNECT){
							chatPacket = TcpPacket.ChatPacket.newBuilder();
								chatPacket.setType(TcpPacket.PacketType.CHAT)
								.setPlayer(player)
								.setMessage("The word to guess is: "+word);
							out.write(chatPacket.build().toByteArray());
						}
					}else{
						chatPacket = TcpPacket.ChatPacket.newBuilder();
							chatPacket.setType(TcpPacket.PacketType.CHAT)
							.setPlayer(player)
							.setMessage("Time's up! Starting a new round...");
						out.write(chatPacket.build().toByteArray());

						cd = new Countdown(player, out);
						rand=new Random();
						randomNum = rand.nextInt(106);
			    		word = Files.readAllLines(Paths.get("wordpool.txt")).get(randomNum);
			    		chatPacket = TcpPacket.ChatPacket.newBuilder();
								chatPacket.setType(TcpPacket.PacketType.CHAT)
								.setPlayer(player)
								.setMessage("The word to guess is: "+word);
							out.write(chatPacket.build().toByteArray());
						i++;
					}
				}catch(IOException e) { // error cannot connect to server
				  e.printStackTrace();
				  System.out.println("Cannot read file");
				}
			}
			chatPacket = TcpPacket.ChatPacket.newBuilder();
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