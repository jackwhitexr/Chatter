package project3.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;
/**
 * 用户信息类 处理用户的ip，昵称，
 * @author Mr.Bubbles
 *
 */
public class User {
	//public static HashSet<String> ipset=new HashSet<String>();   //在线人数列表
	public static HashMap<String,User> usermap=new HashMap<String,User>();					//用户列表
	public static HashMap<String,ChatGUI> historyFrame=new HashMap<String,ChatGUI>();  		//当前活动窗口映射表
	public static boolean groupChatStatus=false;											//当前群活动窗口状态
	public static GroupChat groupChat=null;													//群聊天窗口
	public static ArrayList<String> groupMembers=new ArrayList<String>();					//群成员队列
	public static String groupName=new String();											//讨论组名称
	public static String selectedIP=new String();											//被选中将要加入讨论组的成员IP
	
	//添加一个用户到用户列表
	public static void addUser(User user){
		if(usermap.containsKey(user.getIP())){
			usermap.remove(user.getIP());
		}
		usermap.put(user.getIP(), user);
	}
	
	//清空在线用户列表
	public static void usermapClear(){
		usermap.clear();
	}
	
	//根据当前用户列表获取IP清单
	public static ArrayList<String> getIP_ArrayList(){
		ArrayList<String> Iplist =new ArrayList<String>();
		Iterator it=usermap.entrySet().iterator(); //entryset?
		while(it.hasNext()){
			Map.Entry entry=(Map.Entry)it.next();
			Iplist.add((String) entry.getKey());
		}
		Collections.sort(Iplist);
		return Iplist;
		
	}
	
	
	private String ip;
	private String name;
	private String signature;
	private ImageIcon headshow;
	public String getIP(){
		return ip;
	}
	public String getName(){
		return name;
	}
	public String getSignature(){
		return signature;
	}
	public ImageIcon getIcon(){
		return headshow;
	}
	public void setIP(String ip){
		this.ip=ip;
	}
	public void setName(String name){
		this.name=name;
	}
	public void setSignature(String signature){
		this.signature=signature;
	}
	public void setIcon(ImageIcon icon){
		this.headshow=icon;
	}
}
