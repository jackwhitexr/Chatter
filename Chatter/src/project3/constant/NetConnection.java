package project3.constant;

import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.swing.ImageIcon;

/**
 * 网络连接的常量参数类
 * @author Mr.Bubbles
 *
 */
public class NetConnection {
	public static DatagramSocket MESSAGE_SOCKET=null;		//聊天消息Socket
	public static MulticastSocket GROUP_SOCKET=null;		//群聊消息Socket
	public static InetAddress group=null;					//群聊广播组
	public static int BYTE_MAX=100000;
	public static int STATUS_PORT=2425;			//状态端口
	public static int MESSAGE_PORT=2426;		//消息端口
	public static int FILE_PORT=2427;			//文件端口
	public static int GROUP_PORT=2428;			//群聊端口
	public static final int WANT_IP=0;			//接收数据包需求标记
	public static final int WANT_NAME=1;
	public static final int WANT_SIGNATURE=2;
	public static final int WANT_PORTRAIT=3;
	public static final int WANT_HEADSHOW=4;	//规范化头像大小
	
	public static byte ISMESSAGE=0;				//标志收到的数据包是消息
	public static byte ISIMAGE=1;				//标志收到的数据包是图片
	public static byte ISGROUPINVENTION=2;		//标志收到的数据包是加群邀请
	public static byte ISVIDEOINVITATION=3;		//标志收到的数据包是视频邀请
	public static byte ISVIDEOACCEPT=4;			//标志收到的数据包是接受视频邀请
	public static byte ISVIDEOREFUSE=5;			//标志收到的数据包是拒绝视频邀请
	public static byte BYTE_SEPERATOR='\t';									//数据包中的字节分割符
	public static byte[] BYTEARR_SEPERATOR={BYTE_SEPERATOR};
	public static String STRING_SEPERATOR=new String(BYTEARR_SEPERATOR);	//字符串分隔符
	//public static String STRING_SEPERATOR="  ";
	
	//准备本地的广播数据包
	public synchronized static byte[] prepareMyInformation(){
		byte[] byteinfo;	//所有的个人信息
		byte[] bytestring;	//可用字符串表示的个人信息 IP 昵称 个性签名
		byte[] byteimage;	//图像字节数组，个人头像的信息
		String myip=SystemInfo.getLocalIP();
		String name=SystemInfo.getMyName();
		String signature=SystemInfo.getSignature();
		String stringinfo=myip+STRING_SEPERATOR+name+STRING_SEPERATOR+signature+STRING_SEPERATOR;	//IP+昵称+个性签名字符串

		bytestring=new byte[stringinfo.getBytes().length];	
		bytestring=stringinfo.getBytes();		//IP+昵称+个性签名字节数组
		byteimage=SystemInfo.getMyPortraitByte();
		byteinfo=Constant.arrayConnect(bytestring, byteimage);
		return byteinfo;
	}
	
	//解析广播数据包
	public synchronized static Object getInfo(DatagramPacket receivepacket,int want){
		byte[] receiveByte=receivepacket.getData();
		String splitString[]=new String(receiveByte).split(STRING_SEPERATOR);	//分割信息
		switch(want){
			case WANT_IP:{
				return splitString[0];
			}
			case WANT_NAME:{
				return splitString[1];
			}
			case WANT_SIGNATURE:{
				return splitString[2];
			}
			case WANT_PORTRAIT:{
				byte[] byteimage=new byte[NetConnection.BYTE_MAX];
				int flag=0;
				int count=0;	//记录分隔符的个数
				for(int i=0;i<=receiveByte.length;i++){
					if(receiveByte[i]==BYTE_SEPERATOR){
						count++;
						if(count==3){
							flag=i+1;
							break;
						}
					}
				}
				System.arraycopy(receiveByte, flag, byteimage, 0, receiveByte.length-flag);
				ImageIcon icon=new ImageIcon(byteimage);
				return icon;
			}
			default:{
				return null;
			}
		}
	}
}
