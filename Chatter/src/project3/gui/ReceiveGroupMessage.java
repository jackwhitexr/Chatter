package project3.gui;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;

import project3.constant.Constant;
import project3.constant.NetConnection;

public class ReceiveGroupMessage implements Runnable {
	String broadcastGroupIP="225.0.0.0";
	ReceiveGroupMessage(){
		//此处将本地主机的群聊套接字加入广播组
		try {
			NetConnection.group=InetAddress.getByName(broadcastGroupIP);
			NetConnection.GROUP_SOCKET=new MulticastSocket(NetConnection.GROUP_PORT);
			NetConnection.GROUP_SOCKET.joinGroup(NetConnection.group);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		//此处接收广播组发来的消息

		byte flag=0;		//收到的消息判断标志位
		while(true){
			try {
				byte[] data =new byte[NetConnection.BYTE_MAX];	//空的发送内容数组，存放接收的内容
				DatagramPacket packet=new DatagramPacket(data,data.length,InetAddress.getLocalHost(),NetConnection.MESSAGE_PORT);
				NetConnection.GROUP_SOCKET.receive(packet);	//接收数据包
				String enemyIP=packet.getAddress().getHostAddress();
				byte[] receivebyte=packet.getData();	//拆包
				flag=receivebyte[0];					//读取消息属性
				
				//已经有窗口接收
				if(User.groupChatStatus){
					if(flag==NetConnection.ISMESSAGE){
						String enemymessage=new String(packet.getData(),1,packet.getLength()-1,"UTF-8");
						User.groupChat.displayMessage(enemymessage);
					}
					else if(flag==NetConnection.ISIMAGE){
						byte[] imagebyte=Constant.arrayDisconnect(data);
						ImageIcon enemyimage=new ImageIcon(imagebyte);
						User.groupChat.displayImage(enemyimage);
					}
				}		
				//否则创建新的窗口
				else{
					if(flag==NetConnection.ISMESSAGE){
						String enemymessage=new String(packet.getData(),1,packet.getLength()-1,"UTF-8");
						User.groupChat=new GroupChat(broadcastGroupIP);
						User.groupChatStatus=true;					//打开群聊天窗口
						User.groupChat.displayMessage(enemymessage);
					}
					else if(flag==NetConnection.ISIMAGE){
						byte[] imagebyte=Constant.arrayDisconnect(data);
						ImageIcon enemyimage=new ImageIcon(imagebyte);
						User.groupChat=new GroupChat(broadcastGroupIP);
						User.groupChatStatus=true;					//打开群聊天窗口
						User.groupChat.displayImage(enemyimage);
					}
				}
				} catch (IOException e) {
					e.printStackTrace();
			}
		}
	}
}
