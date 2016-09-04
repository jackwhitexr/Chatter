package project3.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


import project3.constant.*;


class GroupChat extends JFrame{				//聊天窗口
	private JPanel jpanelTop;
	private JPanel jpanelChat;
	private JPanel jpanelInput;
	private JPanel jpanelRight;
	private JPanel jpanelBottom;
	private JPanel jpanelFontSetting;
	
	
	private JButton jbutHead;
	
	private JButton jbutClose;
	private JButton jbutInput;
	private JButton jbutSentImage;						//发送图片
	private JLabel jlabelNickname;
	
	private JScrollPane js1;							//存放显示文本panel，输入文本panel和语音列表panel的滚动panel
	private JScrollPane js2;
	private JScrollPane js3;
	private JList<String> jlistGroupmember;				//群成员列表
	private DefaultListModel dlm;
	private JLabel jl;
	private JTextPane text;
	private Box box; 									// 放输入组件的容器
	private JComboBox fontName, fontSize, fontStyle,fontColor,fontBackColor; // 字体名称;字号大小;文字样式;文字颜色;文字背景颜色
	private StyledDocument doc;
	
	private JTextArea jtextInput;
	private int x=600;
	private int y=200;
	private int width=800;    							//这里修改聊天窗口大小
	private int height=700;								//这里修改聊天窗口大小
	
	private String myaddress=null; 		//本机地址
	private String groupAddress=null;   //群广播地址
	private Audio myRecord;             //声明录音类的对象
	
	public HashMap<Integer,String> historyVoice=new HashMap<Integer,String>();  //用户语音记录
	public int voiceCount=0;                                                    //语音记录数
	
	public GroupChat(String address) {
			groupAddress=address;
			initPanel();  //初始化面板信息及组件
			initFrame();  //初始化窗体信息
			initSocket(); //创建时建立连接
			myRecord=new Audio();
			setVisible(true);
	}
	void initFrame()
	{	setTitle("讨论组");
		setBounds(x,y,width,height);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //同时关闭进程
		Container c=getContentPane();
		c.setLayout(null);
		c.add(jpanelTop);
		c.add(jpanelChat);	
		c.add(jpanelFontSetting);
		c.add(jpanelInput);
		c.add(jpanelRight);
		c.add(jpanelBottom);
		addWindowListener(new deleteFromMap());
	}
	
	void initPanel(){
		
		jpanelTop=new JPanel();
		jpanelTop.setLayout(null);
		jpanelTop.setBounds(0, 0, width, 90);
		jpanelTop.setBackground(new Color(99, 184, 226));
		
		jbutHead=new JButton();
		jbutHead.setBounds(10, 10, 50, 50);
		setIcon("src/project3/resource/群头像.gif", jbutHead);
		jpanelTop.add(jbutHead);
		jlabelNickname=new JLabel(User.groupName);
		jlabelNickname.setFont(new Font("楷体", Font.BOLD, 20));
		jlabelNickname.setBounds(90, 10, 300, 25);
		jpanelTop.add(jlabelNickname);
		
		jpanelChat=new JPanel();
		jpanelChat.setLayout(null);
		jpanelChat.setBounds(0,90,width-200,height-300);
		jpanelChat.setBackground(Color.white);
		text = new JTextPane();
		 text.setEditable(false);
		 doc = text.getStyledDocument(); // 获得JTextPane的Document
		 text.setBounds(0, 0, width-200, height-300);
		js1=new JScrollPane(text);
		js1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js1.setBounds(0, 0, width-200, height-300);
		jpanelChat.add(js1);
		
		jpanelFontSetting=new JPanel();
		jpanelFontSetting.setLayout(null);
		jpanelFontSetting.setBounds(0,height-210,width-200,40);				//字体设置
		jpanelFontSetting.setBackground(new Color(239,246,244));
		String[] str_name = { "宋体", "黑体", "Dialog", "Gulim" };
		String[] str_Size = { "20", "22", "25", "30", "40", "45" };
		String[] str_Style = { "常规", "斜体", "粗体", "粗斜体" };
		String[] str_Color = { "黑色", "红色", "蓝色", "黄色", "绿色" };
		String[] str_BackColor = { "无色", "灰色", "淡红", "淡蓝", "淡黄", "淡绿" };
		fontName = new JComboBox(str_name); // 字体名称
		fontSize = new JComboBox(str_Size); // 字号
	    fontStyle = new JComboBox(str_Style); // 样式
	    fontColor = new JComboBox(str_Color); // 颜色
		fontBackColor = new JComboBox(str_BackColor); // 背景颜色
		
		jbutSentImage=new JButton("发送图片");
		jbutSentImage.addActionListener(new Listener_SentImage());
		box=Box.createHorizontalBox();
		box.setBounds(0, 5,width-200, 30);
		box.add(new JLabel("字体：")); 			// 加入标签
		box.add(fontName); 						// 加入组件
		box.add(Box.createHorizontalStrut(8)); 	// 间距
		box.add(new JLabel("样式："));
		box.add(fontStyle);
		box.add(Box.createHorizontalStrut(8));
		box.add(new JLabel("字号："));
		box.add(fontSize);
		box.add(Box.createHorizontalStrut(8));
		box.add(new JLabel("字体颜色："));
		box.add(fontColor);
		box.add(Box.createHorizontalStrut(8));
		box.add(jbutSentImage);
		jpanelFontSetting.add(box);
		
		jpanelInput=new JPanel();
		jpanelInput.setLayout(null);
		jpanelInput.setBounds(0, height-170,width-200, 100);				//输入
		jpanelInput.setBackground(Color.white);
		jtextInput=new JTextArea();
		jtextInput.setLineWrap(true);
		jtextInput.setBounds(0, 0, width-200, 100);
		js2=new JScrollPane(jtextInput);
		js2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js2.setBounds(0, 0, width-200, 100);
		jpanelInput.add(js2);
		
		jpanelRight=new JPanel();
		jpanelRight.setLayout(null);
		jpanelRight.setBounds(width-200,90,200,height-100);
		jpanelRight.setBackground(new Color(239, 246, 249));           //右侧信息栏
		final ImageIcon imgVoice=new ImageIcon("src/project3/resource/yuyin.png");
		jl=new JLabel("群成员");
		jl.setBackground(new Color(239, 246, 249));
		jl.setBounds(80, 0, width-600, 30);
		jpanelRight.add(jl);
		dlm=new DefaultListModel();
//		dlm.addElement("1");
		
		jlistGroupmember=new JList();
		jlistGroupmember.setModel(dlm);
		
		jlistGroupmember.setCellRenderer(new DefaultListCellRenderer(){
			 private static final long serialVersionUID = 1L;  
			    public Component getListCellRendererComponent(JList list,  
			        Object value, int index, boolean isSelected,  
			        boolean cellHasFocus) {  
			    	 if(User.usermap.size()!=0){
				        	
				            if (value instanceof String) {
				            	ImageIcon memberImgHead=null;
				            	memberImgHead=User.usermap.get(value).getIcon();
				            	setIcon(memberImgHead);  //设置列表的图片
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
		});
		
		js3=new JScrollPane(jlistGroupmember);
		js3.setBounds(0, 30, width-500, height-100);
		jpanelRight.add(js3);
		
		jpanelBottom=new JPanel();
		jpanelBottom.setBounds(0, height-70, width-200,70);
		jpanelBottom.setBackground(new Color(220, 238, 247));
		jpanelBottom.setLayout(new FlowLayout(2,20,5));					//底栏
		jbutClose=new JButton("关闭");
		jbutClose.setBackground(new Color(245, 236, 247));
		jbutClose.addActionListener(new Listener_Close());
		jbutInput=new JButton("发送");
		jbutInput.setBackground(new Color(245, 238, 247));
		jbutInput.addActionListener(new Listener_SendMessage());
		jpanelBottom.add(jbutClose);
		jpanelBottom.add(jbutInput);
		this.getRootPane().setDefaultButton(jbutInput); // 默认回车按钮
		
		addGroupMember();								//将群成员加入到群成员列表中
	}
	
	//初始化通信
	void initSocket(){
		try {
			myaddress=InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//在jlistGroupmember里增加群好友
	public void addGroupMember(){
		String groupMember=new String();
		for(int i=0;i<User.groupMembers.size();i++){
			groupMember=User.groupMembers.get(i);
			dlm.addElement(groupMember);
		}
		
	}
	
	//构造面板上的系统图标
	private void setIcon(String file, JButton iconButton) {  
        ImageIcon icon = new ImageIcon(file);  
        Image temp = icon.getImage().getScaledInstance(iconButton.getWidth(),  
                iconButton.getHeight(), icon.getImage().SCALE_DEFAULT);  
        icon = new ImageIcon(temp);  
        iconButton.setIcon(icon);  
    } 
	
	//在本聊天窗口上显示message信息
	public void displayMessage(String message){
  	  	insert(getFontAttrib(message));
	}
	
	//显示图像
	public void displayImage(ImageIcon enemyimage){
		insertIcon(enemyimage); 						// 插入图片
	}
	
	private void insertIcon(ImageIcon inserticon) {
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		try {
			doc.insertString(doc.getLength(), time+"\n", null);	//在getlengh处插入时间，后面是时间的属性
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		inserticon=Constant.resizeImage(inserticon);		//对图片重新划定大小
		text.setCaretPosition(doc.getLength()); 			// 设置插入位置
		text.insertIcon(inserticon); 	                    // 插入图片
		try {
			doc.insertString(doc.getLength(), "\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		//insert(new FontAttrib()); 							// 这样做可以换行
	 }
	
	//将一条包装信息显示在面板上
	private void insert(FontAttrib attrib) {
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		try {
			doc.insertString(doc.getLength(), time+"\n"+attrib.getText()+"\n", attrib.getAttrSet());	//在getlengh处插入时间，后面是时间的属性
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 以下是本类用到的监听器
	 * @author Mr.Bubbles
	 *
	 */
	//关闭窗口监听器
	private class Listener_Close implements ActionListener{		
			public void actionPerformed(ActionEvent e) {
				User.groupChatStatus=false;					//群聊天窗口关闭
				dispose();
			}
		}
	//发送消息监听器
	private class Listener_SendMessage implements ActionListener{	
		private byte[] flag=new byte[1];
		public void actionPerformed(ActionEvent e) {
			try{
				if(jtextInput.getText().equals("")){
					JOptionPane.showMessageDialog(null, "发送消息不能为空！","发送文本为空少年",1);
				}
				else{
					flag[0]=NetConnection.ISMESSAGE;
					String sendString=jtextInput.getText();
					byte[] textbyte=sendString.getBytes();
					byte[] data=Constant.arrayConnect(flag, textbyte);
					DatagramPacket contentpacket=new DatagramPacket(data,data.length,NetConnection.group,NetConnection.GROUP_PORT);
					{
						NetConnection.GROUP_SOCKET.send(contentpacket);
						//在本机显示我发送的消息
						//displayMessage(jtextInput.getText());
						jtextInput.setText(null);
					}
				}
				}
			catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
	
	//发送图片监听器
	private class Listener_SentImage implements ActionListener{	
		private byte[] flag=new byte[1];
		public void actionPerformed(ActionEvent e) {
			flag[0]=NetConnection.ISIMAGE;			//标志发送的是图片
			JFileChooser f = new JFileChooser(); // 查找文件
			FileSystemView fsv=FileSystemView.getFileSystemView();
			f.setCurrentDirectory(fsv.getHomeDirectory());     //默认路径为桌面
			int flagImage=f.showOpenDialog(null);
			if(flagImage==JFileChooser.APPROVE_OPTION){
				ImageIcon insertimage=new ImageIcon(f.getSelectedFile().getPath());
				displayImage(insertimage);
				//insertIcon(f.getSelectedFile(),getFontAttrib(jtextInput.getText())); // 本地显示图片
				try {
					ImageIcon imageicon=new ImageIcon(f.getSelectedFile().getPath());//获取图片，构造ImageIcon
					imageicon=Constant.resizeImage(imageicon);				//发送前对图片重新划定大小
					BufferedImage bufferedimage=new BufferedImage(	//BufferedImage可以访问图片
							imageicon.getImage().getWidth(null),imageicon.getImage().getHeight(null), BufferedImage.TYPE_INT_BGR);
					bufferedimage.getGraphics().drawImage(imageicon.getImage(),0,0,imageicon.getIconWidth(),imageicon.getIconHeight(),null);
					ByteArrayOutputStream imageStream=new ByteArrayOutputStream();
					ImageIO.write(bufferedimage, "JPEG", imageStream);
					byte[] imagebyte=imageStream.toByteArray();		//图片格式化byte数组
					byte[] data=Constant.arrayConnect(flag, imagebyte);	//打包好的arrayConnect
					DatagramPacket contentpacket=new DatagramPacket(data,data.length,NetConnection.group,NetConnection.GROUP_PORT);
					NetConnection.GROUP_SOCKET.send(contentpacket);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	//关闭聊天窗口时，将当前窗口在historyFrame当前活动窗口映射Map中清除
	private class deleteFromMap extends WindowAdapter{    
		public void windowClosing(WindowEvent e){
			User.groupChatStatus=false;					//群聊天窗口关闭
		}
	}

	//发送文件监听器
//	private class Listener_SendFile implements ActionListener{
//		public void actionPerformed(ActionEvent e) {
//			//更改文件选择器的外观
//			if(UIManager.getLookAndFeel().isSupportedLookAndFeel()){
//				final String platform = UIManager.getSystemLookAndFeelClassName();
//				// If the current Look & Feel does not match the platform Look & Feel,
//				// change it so it does.
//				if (!UIManager.getLookAndFeel().getName().equals(platform)) {
//					try {
//						UIManager.setLookAndFeel(platform);
//					} catch (Exception exception) {
//						exception.printStackTrace();
//					}
//				}
//			}
//			JFileChooser jfc = new JFileChooser();
//			FileSystemView fsv=FileSystemView.getFileSystemView();
//			jfc.setCurrentDirectory(fsv.getHomeDirectory());     //默认路径为桌面
//			int flag = jfc.showOpenDialog(null);
//			if(flag==JFileChooser.APPROVE_OPTION){
//				File sorFile = jfc.getSelectedFile();
//				Thread sendFile=new Thread(new SendFile(enemyaddress,sorFile));
//				sendFile.start();
//			}
//		}
//	}
	
	
	
	//信息包装类	内部类
	private class FontAttrib {
		  public static final int GENERAL = 0; 	// 常规
		  public static final int BOLD = 1; 	// 粗体
		  public static final int ITALIC = 2; 	// 斜体
		  public static final int BOLD_ITALIC = 3; // 粗斜体
		  
		  private SimpleAttributeSet attrSet = null; // 属性集
		  private String text = null, name = null; // 要输入的文本和字体名称
		  private int style = 0, size = 0; // 样式和字号
		  private Color color = null, backColor = null; // 文字颜色和背景颜色
		  
		  public SimpleAttributeSet getAttrSet() {  //获取当前用户的属性集
		   attrSet = new SimpleAttributeSet();
		   if (name != null) {
		    StyleConstants.setFontFamily(attrSet, name);
		   }
		   if (style == FontAttrib.GENERAL) {
		    StyleConstants.setBold(attrSet, false);		//StyleConstant是一个常用类，提供了一系列设置属性集中属性的方法
		    StyleConstants.setItalic(attrSet, false);
		   } else if (style == FontAttrib.BOLD) {
		    StyleConstants.setBold(attrSet, true);
		    StyleConstants.setItalic(attrSet, false);
		   } else if (style == FontAttrib.ITALIC) {
		    StyleConstants.setBold(attrSet, false);
		    StyleConstants.setItalic(attrSet, true);
		   } else if (style == FontAttrib.BOLD_ITALIC) {
		    StyleConstants.setBold(attrSet, true);
		    StyleConstants.setItalic(attrSet, true);
		   }
		   StyleConstants.setFontSize(attrSet, size);
		   if (color != null) {
		    StyleConstants.setForeground(attrSet, color);
		   }
		   if (backColor != null) {
		    StyleConstants.setBackground(attrSet, backColor);
		   }
		   return attrSet;
		  }
		  public void setAttrSet(SimpleAttributeSet attrSet) {
		   this.attrSet = attrSet;
		  }
		  public String getText() {
		   return text;
		  }
		  public void setText(String text) {
		   this.text = text;
		  }
		  public Color getColor() {
		   return color;
		  }
		  public void setColor(Color color) {
		   this.color = color;
		  }
		  public Color getBackColor() {
		   return backColor;
		  }
		  public void setBackColor(Color backColor) {
		   this.backColor = backColor;
		  }
		  public String getName() {
		   return name;
		  }
		  public void setName(String name) {
		   this.name = name;
		  }
		  public int getSize() {
		   return size;
		  }
		  public void setSize(int size) {
		   this.size = size;
		  }
		  public int getStyle() {
		   return style;
		  }
		  public void setStyle(int style) {
		   this.style = style;
		  }
	 	}
	//根据text构造FontAttrib对象
		private FontAttrib getFontAttrib(String text) {
		  	  FontAttrib att = new FontAttrib();
		  	  att.setText(text);
		  	  att.setName((String) fontName.getSelectedItem());						//获取选择的字体名称
		  	  att.setSize(Integer.parseInt((String) fontSize.getSelectedItem()));	//获取选择的字号大小
		  	  String temp_style = (String) fontStyle.getSelectedItem();				//获取选择的样式
		  	  if (temp_style.equals("常规")) {
		  	   att.setStyle(FontAttrib.GENERAL);
		  	  } else if (temp_style.equals("粗体")) {
		  	   att.setStyle(FontAttrib.BOLD);
		  	  } else if (temp_style.equals("斜体")) {
		  	   att.setStyle(FontAttrib.ITALIC);
		  	  } else if (temp_style.equals("粗斜体")) {
		  	   att.setStyle(FontAttrib.BOLD_ITALIC);
		  	  }
		  	  String temp_color = (String) fontColor.getSelectedItem();
		  	  if (temp_color.equals("黑色")) {
		  	   att.setColor(new Color(0, 0, 0));
		  	  } else if (temp_color.equals("红色")) {
		  	   att.setColor(new Color(255, 0, 0));
		  	  } else if (temp_color.equals("蓝色")) {
		  	   att.setColor(new Color(0, 0, 255));
		  	  } else if (temp_color.equals("黄色")) {
		  	   att.setColor(new Color(255, 255, 0));
		  	  } else if (temp_color.equals("绿色")) {
		  	   att.setColor(new Color(0, 255, 0));
		  	  }
		  	  String temp_backColor = (String) fontBackColor.getSelectedItem();
		  	  if (!temp_backColor.equals("无色")) {
		  	   if (temp_backColor.equals("灰色")) {
		  	    att.setBackColor(new Color(200, 200, 200));
		  	   } else if (temp_backColor.equals("淡红")) {
		  	    att.setBackColor(new Color(255, 200, 200));
		  	   } else if (temp_backColor.equals("淡蓝")) {
		  	    att.setBackColor(new Color(200, 200, 255));
		  	   } else if (temp_backColor.equals("淡黄")) {
		  	    att.setBackColor(new Color(255, 255, 200));
		  	   } else if (temp_backColor.equals("淡绿")) {
		  	    att.setBackColor(new Color(200, 255, 200));
		  	   }
		  	  }
	  	  return att;
	  	 } 
	}