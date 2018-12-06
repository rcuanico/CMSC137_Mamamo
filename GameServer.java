import proto.TcpPacketProtos.*;
import proto.PlayerProtos.*;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameServer {
    private int numRound;
    private static Player player;
    private static DataOutputStream out;
	private static InputStream inFromServer;
    private String lobbyId;
    private static String word="";

    public GameServer(int numRound, String lobbyId, DataOutputStream out, InputStream inFromServer){
        this.numRound=numRound;
        this.player=createPlayer("server");
        this.lobbyId=lobbyId;
        this.out=out;
        this.inFromServer=inFromServer;
        this.startGame();
    }

   	private TcpPacket.ConnectPacket joinLobby (){
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
					System.out.println("You have successfully connected to the lobby.");
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
    	//for(int i=0; i<numRound; i++){
    		Random rand=new Random();
    		//getting the word to draw
    		try{
	    		int randomNum = rand.nextInt(106);
	    		word = Files.readAllLines(Paths.get("wordpool.txt")).get(randomNum);
                chatLobby();
                TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
					chatPacket.setType(TcpPacket.PacketType.CHAT)
					.setPlayer(player)
					.setMessage("The word to guess is: "+word);
				out.write(chatPacket.build().toByteArray());
    		}catch(IOException e) { // error cannot connect to server
			  e.printStackTrace();
			  System.out.println("Cannot read file");
			}
    	//}
    }

	private static void chatLobby(){
		ServerReceiver receiver = new ServerReceiver(inFromServer, word, player, out);
		receiver.start();
	}
}
