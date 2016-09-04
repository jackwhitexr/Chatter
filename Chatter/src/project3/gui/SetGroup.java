package project3.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.media.rtp.event.NewParticipantEvent;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.omg.CORBA.PRIVATE_MEMBER;

import project3.constant.Constant;
import project3.constant.GUI;
import project3.constant.NetConnection;
import project3.constant.SystemInfo;

public class SetGroup extends JFrame {
	private JPanel jpanelMain;
	private JPanel jpanelbottom;
	private JLabel jlabelGroupmember;
	private JLabel jlabelOnline;
	private JLabel jlabelGroupname;
	public static JTextField jtextGroupname;			//群名称输入框
	private JButton jbutSet;
	private JButton jbutYes;
	private JButton jbutCancel;
	private JScrollPane js1;
	private JScrollPane js2;
	private JList<String> jlistOnlinemember;
	private static DefaultListModel dlmOline;
	private JList<String> jlistGroupmember;
	private static DefaultListModel dlmGroup;
	private int width=600;
	private int height=500;
	public SetGroup() {
		// TODO 自动生成的构造函数存根
		super("创建讨论组");
		try { // 使用Windows的界面风格	
			   UIManager
			     .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			  } catch (Exception e) {
			   e.printStackTrace();
		}
		setBounds(600, 200, 600,500);
		initPanel();
		initFrame();
		setResizable(false);
		setVisible(true);
	}

	private void initFrame() {
		// TODO 自动生成的方法存根
		Container c=getContentPane();
		c.setLayout(null);        
		c.add(jpanelMain); 
		c.add(jpanelbottom);     
	}

	private void initPanel() {
		// TODO 自动生成的方法存根
		jpanelMain=new JPanel();
		jpanelMain.setBounds(0, 0, width, 400);
		jpanelMain.setLayout(null);
		jpanelMain.setBackground(Color.white);
		jlabelOnline=new JLabel("在线联系人");
		jlabelOnline.setBounds(50, 10, 100,40);
		jpanelMain.add(jlabelOnline);
		jlabelGroupmember=new JLabel("群成员");
		jlabelGroupmember.setBounds(350, 10, 100, 40);
		jpanelMain.add(jlabelGroupmember);
		jpanelbottom=new JPanel();
		jpanelbottom.setBounds(0,400 , width, 100);
		jpanelbottom.setBackground(new Color(232,245,249));
		jpanelbottom.setLayout(null);
		final ImageIcon imgVoice=new ImageIcon("src/project3/resource/yuyin.png");
		dlmOline=new DefaultListModel();
		
		jlistOnlinemember=new JList<String>();
		jlistOnlinemember.setModel(dlmOline);
		jlistOnlinemember.setCellRenderer(new listCellRenderer());
		jlistOnlinemember.addMouseListener(new ClickOnList());
		
		js1=new JScrollPane(jlistOnlinemember);
		js1.setBounds(50,50,200,250);
		jpanelMain.add(js1);
		
		dlmGroup=new DefaultListModel();
		jlistGroupmember=new JList<String>();
		jlistGroupmember.setModel(dlmGroup);
		jlistGroupmember.setCellRenderer(new listCellRenderer());
		
		js2=new JScrollPane(jlistGroupmember);
		js2.setBounds(350,50,200,250);
		jpanelMain.add(js2);
		jbutSet=new JButton();
		jbutSet.setBounds(270, 150, 60, 30);
		jbutSet.addActionListener(new Listener_insertGuys());
		setIcon("src/project3/resource/右箭头.jpg", jbutSet);
		jpanelMain.add(jbutSet);
		jlabelGroupname=new JLabel("讨论组名称:");
		jlabelGroupname.setBounds(50, 330,100,20);
		jpanelMain.add(jlabelGroupname);
		jtextGroupname=new JTextField();
		jtextGroupname.setBounds(180, 330, 200, 20);
		jpanelMain.add(jtextGroupname);
		jbutYes=new JButton("确定");
		jbutYes.setBounds(400, 15, 80, 30);
		jbutYes.addActionListener(new Listener_yes());
		jbutCancel=new JButton("取消");
		jbutCancel.setBounds(500,15,80,30);
		jbutCancel.addActionListener(new Listener_cancel());
		jpanelbottom.add(jbutYes);
		jpanelbottom.add(jbutCancel);
		flushIpSet();
	}
	
	
	 class listCellRenderer extends DefaultListCellRenderer {
	        /**
	         * 新建群时在线好友列表中与群成员列表中根据ip显示用户的昵称
	         */
	    	private static final long serialVersionUID = 1L; 
	        public Component getListCellRendererComponent(
	                javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	            if(User.usermap.size()!=0){
		        	
		            if (value instanceof String) {
		            	setIcon(User.usermap.get(value).getIcon());  //设置列表的图片
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
	
	private void setIcon(String file, JButton iconButton) {  
        ImageIcon icon = new ImageIcon(file);  
        Image temp = icon.getImage().getScaledInstance(iconButton.getWidth(),  
                iconButton.getHeight(), icon.getImage().SCALE_DEFAULT);  
        icon = new ImageIcon(temp);  
        iconButton.setIcon(icon);  
    } 
	private class Listener_insertGuys implements ActionListener{//添加好友
		public void actionPerformed(ActionEvent e) {
			dlmGroup.addElement(User.selectedIP);				//将好友加入群成员队列中
			User.groupMembers.add(User.selectedIP);
			
		}
	}
	private class Listener_yes implements ActionListener{		//确定
		public void actionPerformed(ActionEvent e) {
			User.groupMembers.add(SystemInfo.getLocalIP());		//将创建方加入到群成员队列中
			User.groupName=jtextGroupname.getText();			//保存群名称
			Myframe.addGroup(User.groupName);														
			try {												//向该好友发送加群邀请
			byte[] flag=new byte[1];
			flag[0]=NetConnection.ISGROUPINVENTION;
			byte[] sendGroupName=User.groupName.getBytes();
			byte[] data=Constant.arrayConnect(flag, sendGroupName);
			for(int i=0;i<User.groupMembers.size();i++){
				DatagramPacket contentpacket=new DatagramPacket(data,data.length,InetAddress.getByName(User.groupMembers.get(i)),NetConnection.MESSAGE_PORT);
				NetConnection.MESSAGE_SOCKET.send(contentpacket);
			}
			} catch (IOException e1) {
			e1.printStackTrace();
			}
						
			Thread receiveFromGroup=new Thread(new ReceiveGroupMessage());	//群的创建方开启接收群消息的线程
			receiveFromGroup.start();
			dispose();
		}
	}
	private class Listener_cancel implements ActionListener{	//取消
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
	class ClickOnList implements MouseListener{
		public void mouseClicked(MouseEvent e){
			if(jlistOnlinemember.getSelectedIndex() != -1 ){
				if(e.getButton()==MouseEvent.BUTTON1){ //左键
					if(e.getClickCount()==1)
						oneClick(jlistOnlinemember.getSelectedValue());
					else if(e.getClickCount()==2){
						twoClick(jlistOnlinemember.getSelectedValue());
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
	}
	 //单机左键
    private void oneClick(Object enemyIP) {
        //单击处理    
    	//System.out.println(iplist.getSelectedValue());
    	User.selectedIP= enemyIP.toString();
    }
    //双击左键
    private void twoClick(Object enemyIP) {
    	//双击处理   
    	//System.out.println("双击");
        
        
    }
    
	//将ipset中的内容更新到JList当中
	public static void flushIpSet(){
		dlmOline.clear();										//将好友列表清空
		ArrayList<String> list=User.getIP_ArrayList();
		for(int i=0;i<list.size();i++){
			try {
				if(list.get(i).equals(InetAddress.getLocalHost().getHostAddress())){
				}
				else{
					dlmOline.addElement(list.get(i));
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}
}
