package project3.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import project3.constant.*;

/**
 * 发送自身在线信息
 * @author Mr.Bubbles
 *
 */
class Send implements Runnable{
	InetAddress group=null;
	MulticastSocket socket=null;
	//构造函数中初始化组播地址和组播socket
	Send(){
		try {
			group=InetAddress.getByName("224.0.0.0");
			socket=new MulticastSocket(NetConnection.STATUS_PORT);
			socket.joinGroup(group);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//线程当中将本机的IP封装到Socket
	public synchronized void run() {
		while(true){
			try {
				//InetAddress myaddress=InetAddress.getLocalHost(); //获取本机的ip
				//String myip=myaddress.getHostAddress();
				//byte[] data=myip.getBytes();
				byte[] data=NetConnection.prepareMyInformation();
				DatagramPacket packet=new DatagramPacket(data,data.length,group,NetConnection.STATUS_PORT);
				socket.send(packet);   //发送本机信息
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

/**
 * 接收在线信息
 * @author Mr.Bubbles
 *
 */
class Receive implements Runnable{
	InetAddress group=null;
	MulticastSocket socket=null;
	Receive(){
		try{
			group=InetAddress.getByName("224.0.0.0");
			socket=new MulticastSocket(NetConnection.STATUS_PORT);
			socket.joinGroup(group);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public synchronized void run() {
		int n=0;
		while(true){
			//User.ipset.clear();  //清空在线用户列表
			User.usermapClear();
			while(n<500){    //接收一段时间
				try {
					byte[] data=new byte[NetConnection.BYTE_MAX];
					DatagramPacket receivePacket=new DatagramPacket(data,data.length,group,NetConnection.STATUS_PORT);
					socket.receive(receivePacket);
					//String receiveIP=receivePacket.getAddress().getHostAddress();
					String receiveIP=(String)NetConnection.getInfo(receivePacket, NetConnection.WANT_IP);
					String receiveName=(String)NetConnection.getInfo(receivePacket, NetConnection.WANT_NAME);
					String receiveSig=(String)NetConnection.getInfo(receivePacket, NetConnection.WANT_SIGNATURE);
					ImageIcon receiveIcon=(ImageIcon)NetConnection.getInfo(receivePacket,NetConnection.WANT_PORTRAIT);
					//User.ipset.add(receiveIP);
					User user=new User();
					user.setIP(receiveIP);
					user.setName(receiveName);
					user.setSignature(receiveSig);
					user.setIcon(receiveIcon);
					User.addUser(user);
					n++;
					}
				 catch (Exception e) {
					e.printStackTrace();
				}
			}		
			n=0;
			Myframe.flushIpSet(); //刷新用户列表
		}		
	}
}

/**
 * 主界面
 * @author 5for3to1
 *
 */
class Myframe extends JFrame implements Runnable{
	/****************窗体变量声明、本窗体所含有的线程声明*****************/
	private static final long serialVersionUID = -2857486551217698316L;
	private JPanel jpanelInformation;
	private JPanel jpanelSearch;
	private JPanel jpanelFriendPanel;
	private JPanel jpanelButton;		//按钮面板
	private JPanel jpanelCenter;		//好友列表
	private JPanel jpanelFunction;		//
	private JPanel jpanelGuys; 			//好友Tab
	private JPanel jpanelGroups;		//群Tab
	private JPanel jpanelRecentContacts;//最近联系人Tab
	private static JButton jbutHead;			//头像
	private JButton jbutGuys;			//好友按钮	
	private JButton jbutGroup;			//群按钮
	private JButton jbutRecentContacts;	//最近联系人按钮
	private JButton jbutSetGroup;
	private JButton jbutRefresh;		//刷新按钮
	private JButton jbutSentFile;		//发送文件按钮
	private JButton jbutVoice;			//发送语音按钮
	private JButton jbutVideo;			//视频聊天按钮
	
	private JMenuBar menubar;
	private JMenu menu;	

	private JLabel jlabelSearch; 		//搜索
	private JLabel jlabel1;
	private JLabel jlabel2;
	private JTextField jtextSearchFriends;
	private static DefaultListModel dlmFriend;
	private JScrollPane js4;		//好友列表滚动面板
	private static JList<String> iplist=new JList<String>();;  //ip显示列表
	
	ImageIcon imgHead=new ImageIcon("src/project3/resource/1.png");                     //临时头像
	
	private static DefaultListModel dlmGroup;
	private JScrollPane js5;		//群列表滚动面板
	private JList<String> grouplist=new JList<String>();		//群列表
	public static DefaultListModel dlmRecentContacts;
	private JScrollPane js6;		//群列表滚动面板
	private JList<String> Recentlist=new JList<String>();		//最近联系人列表

	Thread send=new Thread(new Send());
	Thread receive=new Thread(new Receive()); 				//发送和接收在线状态的线程
	Thread hear=new Thread(this);                  			//接收消息的线程
	Thread receiveFile=new Thread(new ReceiveFile());  		//接收文件的线程
	Thread ReceiveFromGroup;								//接收群聊消息的线程
	public static GroupChat groupChat=null;					//群聊天窗口
	
	CardLayout mycard;
	/**********************方法声明********************************/
	public Myframe(){
		super("ipmsg");
		SystemInfo.initSystemInfo();		//初始化系统信息
		try { // 使用Windows的界面风格	
			   UIManager
			     .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			  } catch (Exception e) {
			   e.printStackTrace();
		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(GUI.mainJFrame_Start_X, GUI.mainJFrame_Start_Y,GUI.mainJFrame_Width, GUI.mainJFrame_Height);
		setResizable(false);
		initPanel();	//初始化面板
		initFrame();	//初始化窗体
		initSocket();	//初始化通信
		setVisible(true);
		//开启线程
		send.start();
		receive.start();  
		hear.start();
		receiveFile.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		flushIpSet();
	}
	//初始化窗体
	void initFrame(){
		
		Container c=getContentPane();
		c.setLayout(null);        //由良辰设置布局
		c.add(jpanelInformation); //添加个人信息面板
		c.add(jpanelSearch);      //添加搜索基友面板
		c.add(jpanelFriendPanel); //基友面板
		c.add(jpanelFunction);    //底部功能区
		addWindowListener(new deleteFromMap());
	}
	//初始化面板
	void initPanel(){
		GUI.jtPersonalizedSignature=new JTextArea(SystemInfo.getSignature());
		GUI.jtPersonalizedSignature.setBounds(100, 50, 150, 30);
		GUI.jtPersonalizedSignature.setFont(new Font("楷体",Font.BOLD, 15));
		GUI.jtPersonalizedSignature.setBackground(new Color(204, 255, 255));
		
		jpanelInformation=new JPanel();
		jpanelInformation.setLayout(null);					//头像信息面板
		jpanelInformation.setBounds(0, 0, GUI.mainJFrame_Width,GUI.InformationHeight);
		jpanelInformation.setBackground(new Color(204, 255, 255));
		jpanelInformation.setVisible(true);
		jpanelInformation.add(GUI.jtPersonalizedSignature);
		
		jbutHead=new JButton();		
		jpanelInformation.add(jbutHead);	//头像按钮
		jbutHead.setSize(70,70);
		jbutHead.setLocation(15, 15);                 
		jbutHead.setToolTipText("点击更换头像");
		jbutHead.addActionListener(new Listener_Setting());
		setIcon(SystemInfo.getMyPortrait(), jbutHead);
		GUI.jlableHomeName=new JLabel(SystemInfo.getMyName());
		GUI.jlableHomeName.setFont(new Font("楷体",Font.BOLD, 20));
		GUI.jlableHomeName.setBounds(100, 20,250, 20);
		jpanelInformation.add(GUI.jlableHomeName);
		
		jpanelSearch=new JPanel();			//搜索联系人面板
		jpanelSearch.setLayout(null);					
		jpanelSearch.setVisible(true);
		jpanelSearch.setBounds(0, GUI.InformationHeight, GUI.mainJFrame_Width,GUI.SearchHeight);
		jpanelSearch.setBackground(new Color(255, 255, 204));
		
		jlabelSearch=new JLabel("搜索:");
		jlabelSearch.setBounds(5, 5, 45, 20);
		jpanelSearch.add(jlabelSearch);
		jtextSearchFriends=new JTextField();
		jtextSearchFriends.setBounds(60, 5,250, 20);
		jtextSearchFriends.addActionListener(new Listener_Search());   //添加搜索监听
		jpanelSearch.add(jtextSearchFriends);
		
		jpanelFriendPanel=new JPanel();
		jpanelFriendPanel.setLayout(null);
		jpanelFriendPanel.setBounds(0,GUI.InformationHeight+GUI.SearchHeight, GUI.mainJFrame_Width, GUI.mainJFrame_Height-GUI.InformationHeight-GUI.SearchHeight-GUI.FunctionHeight);
		
		jpanelButton=new JPanel();
		jpanelButton.setLayout(new GridLayout(1, 3));
		jpanelButton.setBounds(0, 0, GUI.mainJFrame_Width, 40);
		jpanelFriendPanel.add(jpanelButton);
		jbutGuys=new JButton("好友");
		jbutGuys.setBackground(new Color(200, 221, 248));
		jbutGuys.setBorder(null);
		jbutGuys.addActionListener(new Listener_Guys());
		jbutGroup=new JButton("群");
		jbutGroup.setBackground(new Color(200, 221, 248));
		jbutGroup.setBorder(null);
		jbutGroup.addActionListener(new Listener_Group());
		jbutRecentContacts=new JButton("最近联系人");
		jbutRecentContacts.setBackground(new Color(200, 221, 248));
		jbutRecentContacts.setBorder(null);
		jbutRecentContacts.addActionListener(new Listener_RecentContacts());
		
		jpanelButton.add(jbutGuys);  //按钮面板 
		jpanelButton.add(jbutGroup);
		jpanelButton.add(jbutRecentContacts);
		jpanelGuys=new JPanel();
		jpanelGuys.setLayout(null);		//好友面板
		jpanelGuys.setBackground(Color.white);
		dlmFriend=new DefaultListModel();
		iplist.setModel(dlmFriend);
		iplist.setCellRenderer(new listCellRenderer_guys());
		
		js4=new JScrollPane(iplist);
		js4.setBounds(0, 0, GUI.mainJFrame_Width,  GUI.mainJFrame_Height-GUI.InformationHeight-GUI.SearchHeight-GUI.FunctionHeight);	
		jpanelGuys.add(js4);			//添加好友列表
		
		jpanelGroups=new JPanel();						//群面板
		jpanelGroups.setLayout(null);
		jpanelGroups.setBackground(Color.white);
		jlabel1=new JLabel("讨论组");
		jlabel1.setBounds(0, 0, 60, 20);
		jpanelGroups.add(jlabel1);
		jlabel2=new JLabel("创建讨论组");
		jlabel2.setBounds(260, 0, 80, 20);
		jpanelGroups.add(jlabel2);
		jbutSetGroup=new JButton();
		jbutSetGroup.setBounds(230, 0, 20, 20);
		jbutSetGroup.addActionListener(new Listener_setGroup());
		setIcon("src/project3/resource/加号.gif", jbutSetGroup);
		
		jpanelGroups.add(jbutSetGroup);
		dlmGroup=new DefaultListModel();
		grouplist.setModel(dlmGroup);
		grouplist.setCellRenderer(new  listCellRenderer_group());
		js5=new JScrollPane(grouplist);
		js5.setBounds(0, 20, GUI.mainJFrame_Width,  GUI.mainJFrame_Height-GUI.InformationHeight-GUI.SearchHeight-GUI.FunctionHeight);
		jpanelGroups.add(js5);
		jpanelRecentContacts=new JPanel();
		jpanelRecentContacts.setLayout(null);				//最近联系人面板
		jpanelRecentContacts.setBackground(Color.white);
		dlmRecentContacts=new DefaultListModel();
		Recentlist.setModel(dlmRecentContacts);
		Recentlist.setCellRenderer(new listCellRenderer_RecentContacts());
		js6=new JScrollPane(Recentlist);
		js6.setBounds(0, 0, GUI.mainJFrame_Width,  GUI.mainJFrame_Height-GUI.InformationHeight-GUI.SearchHeight-GUI.FunctionHeight);
		jpanelRecentContacts.add(js6);
		jpanelCenter=new JPanel();
		jpanelCenter.setBounds(0, 40, GUI.mainJFrame_Width, GUI.mainJFrame_Height-GUI.InformationHeight-GUI.SearchHeight-GUI.FunctionHeight);
		mycard=new CardLayout();
		jpanelCenter.setLayout(mycard);
		jpanelFriendPanel.add(jpanelCenter);
		jpanelCenter.add("好友",jpanelGuys);
		jpanelCenter.add("群",jpanelGroups);
		jpanelCenter.add("最近联系人",jpanelRecentContacts);
		
		jpanelFunction=new JPanel();
		jpanelFunction.setLayout(new FlowLayout(0,10,10));					//功能面板
		jpanelFunction.setVisible(true);
		jpanelFunction.setBackground(new Color(204,153, 204));
		jpanelFunction.setBounds(0, GUI.mainJFrame_Height-GUI.FunctionHeight, GUI.mainJFrame_Width,GUI.FunctionHeight);
		menubar=new JMenuBar();
		menu=new JMenu("菜单");
		//设置菜单栏里的内容
		JMenuItem Help=new JMenuItem("关于");
		menu.add(Help);
		menubar.add(menu);
		jpanelFunction.add(menubar);
		jbutRefresh=new JButton("刷新");
		jpanelFunction.add(jbutRefresh);
		
		jbutRefresh.addActionListener(new Listener_Flush());  	//刷新监听器
		iplist.addMouseListener(new ClickOnList());				//绑定好友列表鼠标监听器
		Recentlist.addMouseListener(new ClickOnList());
		grouplist.addMouseListener(new ClickOnGroup());			//绑定群列表鼠标监听器
		setVisible(true);
	}
	public static void setHead(String path){		//设置头像
		setIcon(path, jbutHead);
	}
	//初始化Socket
	void initSocket(){
		try {
			NetConnection.MESSAGE_SOCKET=new DatagramSocket(NetConnection.MESSAGE_PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//在好友列表中显示好友昵称，而getSelectedVaule获取的是好友IP
    class listCellRenderer_guys extends DefaultListCellRenderer {
        /**
         * 在好友列表中根据ip显示用户的昵称
         */
    	private static final long serialVersionUID = 1L; 
        public Component getListCellRendererComponent(
                javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if(User.usermap.size()!=0){
	        	
	            if (value instanceof String) {
	                setIcon(User.usermap.get(value.toString()).getIcon());  //设置列表的图片
	            	String userName=User.usermap.get(value).getName();
	                if (userName != null) {
	                    setText(userName);
	                }
	            }
	            if (isSelected) {  
	                setBackground(list.getSelectionBackground());  
	                setForeground(list.getSelectionForeground());  
	            } else {  
	                // 设置选取与取消选取的前景与背景颜色.  
	                setBackground(list.getBackground());  
	                setForeground(list.getForeground());  
	            }
            }
            return this;
        }
    }
    class listCellRenderer_group extends DefaultListCellRenderer {
        /**
         * 在群列表中根据ip显示用户的昵称
         */
    	private static final long serialVersionUID = 1L; 
        public Component getListCellRendererComponent(
                javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        	setIcon(imgHead);  //设置列表的图片
	           if (value instanceof String) {
	           String userName=value.toString();
	               if (userName != null) {
	                   setText(userName);
	               }
	           }
	           if (isSelected) {  
	               setBackground(list.getSelectionBackground());  
	               setForeground(list.getSelectionForeground());  
	           } else {  
	               // 设置选取与取消选取的前景与背景颜色.  
	               setBackground(list.getBackground());  
                setForeground(list.getForeground());  	            }
           return this;
        }
    }
    class listCellRenderer_RecentContacts extends DefaultListCellRenderer {
        /**
         * 在最近联系人列表中根据ip显示用户的昵称
         */
    	private static final long serialVersionUID = 1L; 
        public Component getListCellRendererComponent(
                javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        	
	           if (value instanceof String) {
		           if(User.usermap.containsKey(value)){
		        	   setIcon(User.usermap.get(value).getIcon());  		//设置列表的图片
		        	   String userName=User.usermap.get(value).getName();
		        	   if (userName != null) {
		        		   setText(userName);  
		        	   }
		           }
		           if (isSelected) {  
		        	   setBackground(list.getSelectionBackground());  
		        	   setForeground(list.getSelectionForeground());  
		           } else {  
		        	   // 设置选取与取消选取的前景与背景颜色.  
		        	   setBackground(list.getBackground());  
		        	   setForeground(list.getForeground()); 
		           }
	           }
           return this;
        }
    }
	
	//设置系统图标
	public static void setIcon(String file, JButton iconButton) {  
        ImageIcon icon = new ImageIcon(file);  
        Image temp = icon.getImage().getScaledInstance(iconButton.getWidth(),  
                iconButton.getHeight(), icon.getImage().SCALE_DEFAULT);  
        icon = new ImageIcon(temp);  
        iconButton.setIcon(icon);  
    }  
	public static void setIcon(ImageIcon icon, JButton iconButton) {  
        Image temp = icon.getImage().getScaledInstance(iconButton.getWidth(),  
                iconButton.getHeight(), icon.getImage().SCALE_DEFAULT);  
        icon = new ImageIcon(temp);  
        iconButton.setIcon(icon);  
    }  
	
	//将已聊过天的好友加入最近联系人
	public static void addChattedMember(String memberIP){
		dlmRecentContacts.addElement(memberIP);
	}
	
	//在群列表中添加已加入的群
	public static void addGroup(String groupName){
		dlmGroup.addElement(groupName);
	}
	
	/**
	 * 下面是各种监听器
	 * @author Mr.Bubbles
	 *
	 */
	private class Listener_Setting implements ActionListener{		//设置个人信息
				public void actionPerformed(ActionEvent e) {
			Myframe1 mf1=new Myframe1();
		}
}
	
	private class Listener_Search implements ActionListener{		//开始搜索
			public void actionPerformed(ActionEvent e) {
			}
	}
	
	private class Listener_Guys implements ActionListener{		//好友列表
		public void actionPerformed(ActionEvent e) {
			mycard.first(jpanelCenter);
		}
}
	private class Listener_Group implements ActionListener{		//群列表
		public void actionPerformed(ActionEvent e) {
			mycard.show(jpanelCenter, "群");
		}
}
	
	private class Listener_RecentContacts implements ActionListener{		//最近联系人列表
		public void actionPerformed(ActionEvent e) {
		mycard.last(jpanelCenter);
		}
}
	private class Listener_setGroup implements ActionListener{		//创建讨论组
		public void actionPerformed(ActionEvent e) {
			SetGroup sg=new SetGroup();
			//groupChat=new GroupChat("224.0.0.0");
		}
}

	//好友列表鼠标事件
	class ClickOnList implements MouseListener{
		public void mouseClicked(MouseEvent e){
			if(iplist.getSelectedIndex() != -1 ){
				if(e.getButton()==MouseEvent.BUTTON1){ //左键
					if(e.getClickCount()==1)
						oneClick(iplist.getSelectedValue());
					else if(e.getClickCount()==2){
						twoClick(iplist.getSelectedValue());
					}
				}
			}
		}

		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		//单机左键
	    private void oneClick(Object value) {
	        //单击处理    
	    	//System.out.println(iplist.getSelectedValue());
	    	
	    }
	    //双击左键
	    private void twoClick(Object enemyIP) {
	    	//双击处理   
	    	//System.out.println("双击");
	    	if(User.usermap.containsKey(enemyIP)){
		        ChatGUI chatgui=new ChatGUI(enemyIP.toString());
		        User.historyFrame.put(enemyIP.toString(), chatgui);
	    	}
	    	else{
	    		JOptionPane.showMessageDialog(null, "该好友不在线！","错误提示",1);
	    	}
	    }
	}
	 
    
    
  //群列表鼠标事件
  	class ClickOnGroup implements MouseListener{
  		public void mouseClicked(MouseEvent e){
  			if(grouplist.getSelectedIndex() != -1 ){
  				if(e.getButton()==MouseEvent.BUTTON1){ //左键
  					if(e.getClickCount()==1)
  						oneceClick(grouplist.getSelectedValue());
  					else if(e.getClickCount()==2){
  						doubleClick(grouplist.getSelectedValue());
  					}
  				}
  			}
  		}

  		public void mouseEntered(MouseEvent arg0) {
  			// TODO Auto-generated method stub
  		}

  		public void mouseExited(MouseEvent arg0) {
  			// TODO Auto-generated method stub
  			
  		}

  		public void mousePressed(MouseEvent arg0) {
  			// TODO Auto-generated method stub
  			
  		}

  		public void mouseReleased(MouseEvent arg0) {
  			// TODO Auto-generated method stub
  			
  		}
  		
  		//单机左键
       private void oneceClick(Object value) {
          //单击处理    
    	  //System.out.println(grouplist.getSelectedValue());
      	
       }
      //双击左键
       private void doubleClick(Object enemyIP) {
      	//双击处理   
      	//System.out.println("双击");
    	   User.groupChat=new GroupChat(enemyIP.toString());
    	   User.groupChatStatus=true;							//打开群聊天窗口
       }
  	}
  	 
    
	//将ipset中的内容更新到JList当中
	public static void flushIpSet(){
		dlmFriend.clear();										//将好友列表清空
		ArrayList<String> list=User.getIP_ArrayList();
		//ArrayList<String> list=new ArrayList<String>();
		//Collections.sort(list);
		for(int i=0;i<list.size();i++){                     
			dlmFriend.addElement(list.get(i));
		}
	}
	
	//刷新好友触发事件
	private class Listener_Flush implements ActionListener{
		public synchronized void actionPerformed(ActionEvent e) {
			//User.ipset.clear();                              //将收到的在线用户信息清空
			User.usermapClear();
			try {
				Thread.sleep(100);                           //重新收集在线用户信息
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			//ArrayList<String> list=new ArrayList<String>(User.ipset);
			ArrayList<String> list=User.getIP_ArrayList();
			Collections.sort(list);
			flushIpSet();									 //将在线用户信息刷新到好友列表中
			
		}
	}
		
		/*
		 * 接收消息的方法，接收消息判断标志位，进行不同的处理
		 * @see java.lang.Runnable#run()
		 */
			public void run(){
				byte flag=0;		//收到的消息判断标志位
				while(true){
					try {
						byte[] data =new byte[NetConnection.BYTE_MAX];	//空的发送内容数组，存放接收的内容
						DatagramPacket packet=new DatagramPacket(data,data.length,InetAddress.getLocalHost(),NetConnection.MESSAGE_PORT);
						NetConnection.MESSAGE_SOCKET.receive(packet);	//接收数据包
						String enemyIP=packet.getAddress().getHostAddress();
						byte[] receivebyte=packet.getData();	//拆包
						flag=receivebyte[0];		//读取消息属性
						//已经有窗口接收
						if(User.historyFrame.containsKey(enemyIP)){
							if(flag==NetConnection.ISMESSAGE){
								String enemymessage=new String(packet.getData(),1,packet.getLength(),"UTF-8");
								User.historyFrame.get(enemyIP).displayMessage(enemymessage);
							}
							else if(flag==NetConnection.ISIMAGE){
								byte[] imagebyte=Constant.arrayDisconnect(data);
								ImageIcon enemyimage=new ImageIcon(imagebyte);
								User.historyFrame.get(enemyIP).displayImage(enemyimage);
							}
							//接收到的消息是邀请视频的消息
							else if(flag==NetConnection.ISVIDEOINVITATION){
								User.historyFrame.get(enemyIP).setVisiable();//将接受视频和取消按钮设为可见
								
							}
							//接收到的消息是同意视频的消息
							else if(flag==NetConnection.ISVIDEOACCEPT){
								User.historyFrame.get(enemyIP).videoCapture=new VideoCapture(enemyIP);
								User.historyFrame.get(enemyIP).videoPlay=new VideoPlay();
								User.historyFrame.get(enemyIP).videoCapture.start();
								User.historyFrame.get(enemyIP).videoPlay.start();
								System.out.println("对方同意了视频请求");
							}
						}		
						//否则创建新的窗口
						else{
							if(flag==NetConnection.ISMESSAGE){
								String enemymessage=new String(packet.getData(),1,packet.getLength(),"UTF-8");
								ChatGUI chatgui=new ChatGUI(enemyIP);
								User.historyFrame.put(enemyIP,chatgui);
								chatgui.displayMessage(enemymessage);
							}
							else if(flag==NetConnection.ISIMAGE){
								byte[] imagebyte=Constant.arrayDisconnect(data);
								ImageIcon enemyimage=new ImageIcon(imagebyte);
								ChatGUI chatgui=new ChatGUI(enemyIP);
								User.historyFrame.put(enemyIP,chatgui);
								chatgui.displayImage(enemyimage);
							}
							//接收到的消息是邀请视频的消息
							else if(flag==NetConnection.ISVIDEOINVITATION){
								ChatGUI chatgui=new ChatGUI(enemyIP);
								User.historyFrame.put(enemyIP,chatgui);
								chatgui.setVisiable();
							}
							//接收到的消息是同意视频的消息
							else if(flag==NetConnection.ISVIDEOACCEPT){
								ChatGUI chatgui=new ChatGUI(enemyIP);
								User.historyFrame.put(enemyIP,chatgui);
								chatgui.videoCapture=new VideoCapture(enemyIP);
								chatgui.videoPlay=new VideoPlay();
								chatgui.videoCapture.start();
								chatgui.videoPlay.start();
								System.out.println("对方同意了视频请求");
							}
						}
						//接收到的消息是邀请加入群聊的消息
						if(flag==NetConnection.ISGROUPINVENTION){
							String groupName=new String(packet.getData(),1,packet.getLength()-1,"UTF-8");
							System.out.println(groupName);
							if(dlmGroup.isEmpty()){
								addGroup(groupName);
							}
							ReceiveFromGroup=new Thread(new ReceiveGroupMessage());
							ReceiveFromGroup.start();
						}
						//接收到的消息是拒绝视频的消息
						else if(flag==NetConnection.ISVIDEOREFUSE){
							JOptionPane.showMessageDialog(null, "对方拒绝了你的视频请求！","视频提示",1);
						}
						} catch (IOException e) {
							e.printStackTrace();
					}
				}
			}
		private class deleteFromMap extends WindowAdapter{    
			public void windowClosing(WindowEvent e){
				SystemInfo.saveSystemInfo();
			}
		}
}


public class Gui {
	public static void main(String[] args) {
		Myframe mf=new Myframe();
	}
}