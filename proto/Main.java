import proto.PlayerProtos.Player;
import proto.TcpPacketProtos.TcpPacket;
import java.net.*;
import java.io.*;


public class Main{
    public static void main(String[] args){
        try{
            String serverName = args[0];
            int port = Integer.parseInt(args[1]); //get port from second param
            Socket server = new Socket(serverName, port);

            Player.Builder player = Player.newBuilder();
            player.setName("P1").build();

            TcpPacket.Builder tcppacket = TcpPacket.newBuilder();
            tcppacket.setType(TcpPacket.PacketType.CONNECT).setPlayer(player).setLobbyId("AB3L").build();




            // OutputStream outToServer = server.getOutputStream();
            // ObjectOutputStream out = new ObjectOutputStream(outToServer);
            // out.writeObject(tcppacket);
            // lobby.setType();
            // lobby.build().writeTo(out);
            // InputStream inFromServer = server.getInputStream();
            // ObjectInputStream in = new ObjectInputStream(inFromServer);
            // System.out.println("Server says " + in.readUTF());

        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Cannot find (or disconnected from) Server");
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Usage: java GreetingClient <server-ip> <port-no.>");
        }
    }
}