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
    private String word;
    private JTextArea timeRemaining;
    private Layout layout;

	public ChatReceiver (InputStream inFromServer, JTextArea chats, JTextArea timeRemaining, Layout layout){
		this.chats=chats;
		this.inFromServer = inFromServer;
		this.timeRemaining = timeRemaining;
		this.layout = layout;
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
						TcpPacket.ChatPacket lobbyMsg1 = TcpPacket.ChatPacket.parseFrom(lobbyData);
                        if(lobbyMsg1.getMessage().startsWith("The word to guess is: ")){
                            word = lobbyMsg1.getMessage().substring(lobbyMsg1.getMessage().lastIndexOf(" ")+1);
                            layout.changeWord(word);
                        }else if(lobbyMsg1.getMessage().startsWith("Time left: ")){
                        	timeRemaining.setText(lobbyMsg1.getMessage());
                        	String time = lobbyMsg1.getMessage().substring(lobbyMsg1.getMessage().lastIndexOf(" ")+1);
                        	layout.setTime(Integer.parseInt(time));
                        }else if(lobbyMsg1.getMessage().startsWith("Time's up!") || lobbyMsg1.getMessage().startsWith("THE GAME IS OVER")){
                        	chats.setText(chats.getText()+lobbyMsg1.getMessage()+"\n");
                        	layout.youCanGuess();
                        	if(lobbyMsg1.getMessage().startsWith("THE GAME IS OVER")) layout.broadcastScore();
                        }else if(lobbyMsg1.getMessage().startsWith(lobbyMsg1.getPlayer().getName()+" guessed") || lobbyMsg1.getMessage().startsWith(lobbyMsg1.getPlayer().getName()+" scored")){
                        	chats.setText(chats.getText()+lobbyMsg1.getMessage()+"\n");
                        }else if(lobbyMsg1.getMessage().startsWith("Starting")){
                        	chats.setText(chats.getText()+lobbyMsg1.getMessage()+"\n");
                        }else if(!lobbyMsg1.getMessage().equals(word) && !lobbyMsg1.getPlayer().getName().equals("server")){
                        	chats.setText(chats.getText()+lobbyMsg1.getPlayer().getName()+": "+lobbyMsg1.getMessage()+"\n");
                        }
					}else if(packet.getType()==TcpPacket.PacketType.CONNECT){
						TcpPacket.ConnectPacket lobbyMsg1 = TcpPacket.ConnectPacket.parseFrom(lobbyData);
						if(!lobbyMsg1.getPlayer().getName().equals("server")){
							chats.setText(chats.getText()+lobbyMsg1.getPlayer().getName()+" has connected to the lobby."+"\n");
							//System.out.println(lobbyMsg1.getPlayer().getName()+" has connected to the lobby.");
						}
					}else if(packet.getType()==TcpPacket.PacketType.DISCONNECT){
						TcpPacket.DisconnectPacket lobbyMsg1 = TcpPacket.DisconnectPacket.parseFrom(lobbyData);
						if(!lobbyMsg1.getPlayer().getName().equals("server")){
							chats.setText(chats.getText()+lobbyMsg1.getPlayer().getName()+" has disconnected from the lobby."+"\n");
							//System.out.println(lobbyMsg1.getPlayer().getName()+" has disconnected from the lobby.");
						}
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
