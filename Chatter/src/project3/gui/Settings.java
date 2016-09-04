package project3.gui;


import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import project3.constant.*;
import sun.tools.jar.resources.jar;


class Myframe1 extends JFrame{                            //头像设置信息
	private JPanel jpanelSettings;
	private JPanel jpanelMain;
	private JTabbedPane jpanelPersonalSetting;
	private JTabbedPane jpanelSystemSetting;
	private JPanel jpanelInfom;
	private JPanel jpanelSave;
	private JPanel jpanelSystemsettings;
	private JButton jbutPersonalSetting;
	private JButton jbutSystemSetting;
	private JButton jbutHead;
	private JButton jbutSave;
	private JButton jbutCancel;
	private JLabel jlabelhead;
	private JLabel jlabelSettingsName;						//设置界面昵称
	private JLabel jlabelPersonality;
	private JLabel jlabelInformation;
	private JLabel jlabelschool;
	private JLabel jlabelsdept;
	private JLabel jlabelsno;
	private JLabel jlabelphonenum;
	private JLabel jlabel;
	
	private ImageIcon image1;
	private ImageIcon image2;
	private CardLayout card;
	public Myframe1(){
		super("设置");
		setResizable(false);
		setBounds(GUI.Setting_X,GUI.Setting_Y,GUI.Setting_Width,GUI.Setting_Height);
		init();
		setVisible(true);
	}
	void init(){
		Container c=getContentPane();
		c.setLayout(null);
		jpanelSettings=new JPanel();
		jpanelSettings.setBounds(0, 0,GUI.Setting_Width,90);
		jpanelSettings.setLayout(null);
		jpanelSettings.setBackground(new Color(99, 184, 226));
		c.add(jpanelSettings);
		image1=new ImageIcon("src/project3/resource/设置.png");
		jbutPersonalSetting=new JButton(image1);
		jbutPersonalSetting.setBackground(new Color(99, 184, 226));
		jbutPersonalSetting.setBounds(20, 20, 70, 50);
		jbutPersonalSetting.setBorder(null);
		jbutPersonalSetting.addActionListener(new Listener_Personalsetting());
		jpanelSettings.add(jbutPersonalSetting);
		image2=new ImageIcon("src/project3/resource/系统.png");
		jbutSystemSetting=new JButton(image2);
		jbutSystemSetting.setBackground(new Color(99, 184, 226));
		jbutSystemSetting.setBounds(110, 20, 70, 50);
		jbutSystemSetting.setBorder(null);
		jbutSystemSetting.addActionListener(new Listener_Systemsetting());
		jpanelSettings.add(jbutSystemSetting);
		
		jpanelMain=new JPanel();
		jpanelMain.setBackground(new Color(168, 214, 237));
		card=new CardLayout();
		jpanelMain.setLayout(card);
		jpanelMain.setBounds(0, 90, GUI.Setting_Width, GUI.Setting_Height-90);
		c.add(jpanelMain);
		
		//个人信息设置
		jpanelPersonalSetting=new JTabbedPane(JTabbedPane.LEFT);
		jpanelInfom=new JPanel();
		jpanelInfom.setBackground(Color.white);
		jpanelInfom.setLayout(null);
		jlabelhead=new JLabel("头像设置:");
		jlabelhead.setBounds(50, 20,100,20);
		jlabelPersonality=new JLabel("个性签名:");
		jlabelPersonality.setBounds(50, 100,100,20);
		jlabelInformation=new JLabel("基本信息:");
		jlabelInformation.setBounds(50, 200, 100, 20);
		jlabelSettingsName=new JLabel("姓	名:");
		jlabelSettingsName.setBounds(150,200,100,20);
		jlabelschool=new JLabel("学	   校:");
		jlabelschool.setBounds(150, 220,100,20);
		jlabelsdept=new JLabel("系:");
		jlabelsdept.setBounds(150, 240, 100, 20);
		jlabelsno=new JLabel("学号:");
		jlabelsno.setBounds(150, 260, 100, 20);
		jlabelphonenum=new JLabel("联系电话:");
		jlabelphonenum.setBounds(150, 280, 100, 20);
		jbutHead=new JButton();
		jbutHead.setBounds(150, 20, 60, 60);
		jbutHead.addActionListener(new Listener_ChangeHead());
		setIcon(SystemInfo.getMyPortrait(), jbutHead);
		GUI.jtextareaPersonalizedSignature=new JTextArea(GUI.jtPersonalizedSignature.getText());
		GUI.jtextareaPersonalizedSignature.setBounds(150, 100, 300, 90);
		GUI.jtextareaPersonalizedSignature.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		GUI.jtextfieldName=new JTextField(SystemInfo.getMyName());
		GUI.jtextfieldName.setBounds(250, 200, 200, 20);
		GUI.jtextfieldSchool=new JTextField(GUI.jtextfieldSchool.getText());
		GUI.jtextfieldSchool.setBounds(250, 220, 200, 20);
		GUI.jtextfieldSdept=new JTextField(GUI.jtextfieldSdept.getText());
		GUI.jtextfieldSdept.setBounds(250, 240, 200, 20);
		GUI.jtextfieldSno=new JTextField(GUI.jtextfieldSno.getText());
		GUI.jtextfieldSno.setBounds(250, 260, 200, 20);
		GUI.jtextfieldPhonenum=new JTextField(GUI.jtextfieldPhonenum.getText());
		GUI.jtextfieldPhonenum.setBounds(250, 280, 200, 20);
		jpanelInfom.add(jlabelhead);
		jpanelInfom.add(jlabelPersonality);
		jpanelInfom.add(jlabelInformation);
		jpanelInfom.add(jbutHead);
		jpanelInfom.add(GUI.jtextareaPersonalizedSignature);
		jpanelInfom.add(jlabelSettingsName);
		jpanelInfom.add(jlabelschool);
		jpanelInfom.add(jlabelsdept);
		jpanelInfom.add(jlabelsno);
		jpanelInfom.add(jlabelphonenum);
		jpanelInfom.add(GUI.jtextfieldName);
		jpanelInfom.add(GUI.jtextfieldSchool);
		jpanelInfom.add(GUI.jtextfieldSdept);
		jpanelInfom.add(GUI.jtextfieldSno);
		jpanelInfom.add(GUI.jtextfieldPhonenum);
		
		jpanelPersonalSetting.add(jpanelInfom,"个人信息");
		jpanelSystemSetting=new JTabbedPane(JTabbedPane.LEFT);
		jpanelSystemsettings=new JPanel();
		jpanelSystemsettings.setLayout(null);
		jpanelSystemsettings.setBackground(Color.white);
		jlabel=new JLabel("敬请期待...");
		jlabel.setBounds(200, 150, 100, 20);
		jpanelSystemsettings.add(jlabel);
		jpanelSystemSetting.add(jpanelSystemsettings, "系统设置");
		
		jpanelSave=new JPanel();
		jpanelSave.setBounds(0, 320, GUI.Setting_Width, GUI.Setting_Height-300);
		jpanelSave.setBackground(new Color(217, 241, 249));
		jpanelSave.setVisible(true);
		jpanelSave.setLayout(null);
		jbutSave=new JButton("保存");
		jbutSave.setBounds(300, 5,80, 25);
		jbutSave.setBackground(new Color(242, 245, 255));
		jbutSave.addActionListener(new Listener_Save());
		jbutCancel=new JButton("取消");
		jbutCancel.setBackground(new Color(242, 245, 255));
		jbutCancel.setBounds(400, 5,80, 25);
		jbutCancel.addActionListener(new Listener_Cancel());
		jpanelSave.add(jbutSave);
		jpanelSave.add(jbutCancel);
		jpanelInfom.add(jpanelSave);
		jpanelMain.add("个人设置", jpanelPersonalSetting);
		jpanelMain.add("系统设置", jpanelSystemSetting);
	}
	public void setIcon(ImageIcon icon, JButton iconButton) {   
        Image temp = icon.getImage().getScaledInstance(iconButton.getWidth(),  
                iconButton.getHeight(), icon.getImage().SCALE_DEFAULT);  
        icon = new ImageIcon(temp);  
        iconButton.setIcon(icon);  
    } 
	private class Listener_ChangeHead implements ActionListener{		//更改头像
		public void actionPerformed(ActionEvent e) {
			// TODO 自动生成的方法存根
			JFileChooser chooser = new JFileChooser();
		    chooser.setMultiSelectionEnabled(false);
		    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    chooser.setDialogTitle("选择图片");
		    int result = chooser.showOpenDialog(getParent());
		    if(result == JFileChooser.APPROVE_OPTION){
		    File file1 = chooser.getCurrentDirectory();
		    String filepath = file1.getPath();
		    String filename=chooser.getSelectedFile().getName();
		    String picturePath=filepath.replace("\\", "/");
		    ImageIcon icon=new ImageIcon(picturePath+"/"+filename);
		    setIcon(icon, jbutHead);
		    GUI.headTemp=GUI.headPath;
		    GUI.headPath=picturePath+"/"+filename;

		    }
	}
}
	private class Listener_Personalsetting implements ActionListener{		//个人设置
		public void actionPerformed(ActionEvent e) {
			// TODO 自动生成的方法存根
			card.first(jpanelMain);
		}
	}
	private class Listener_Systemsetting implements ActionListener{		//系统设置
		public void actionPerformed(ActionEvent e) {
			// TODO 自动生成的方法存根
			card.last(jpanelMain);
		}
	}
private class Listener_Save implements ActionListener{		//保存信息设置
		public void actionPerformed(ActionEvent e) {
			GUI.jlableHomeName.setText(GUI.jtextfieldName.getText());
			GUI.jtPersonalizedSignature.setText(GUI.jtextareaPersonalizedSignature.getText());
			GUI.jtextfieldName.setText(GUI.jtextfieldName.getText());
			GUI.jtextfieldSchool.setText(GUI.jtextfieldSchool.getText());
			GUI.jtextfieldSdept.setText(GUI.jtextfieldSdept.getText());
			GUI.jtextfieldSno.setText(GUI.jtextfieldSno.getText());
			GUI.jtextfieldPhonenum.setText(GUI.jtextfieldPhonenum.getText());
			GUI.headTemp=GUI.headPath;
			//保存更改，检查字段
			SystemInfo.setName(GUI.jtextfieldName.getText());
			SystemInfo.setSignature(GUI.jtextareaPersonalizedSignature.getText());
			SystemInfo.setPortrait(Constant.resizeImage(new ImageIcon(GUI.headPath),NetConnection.WANT_HEADSHOW));
			
		    Myframe.setHead(GUI.headPath);
			dispose();
		}
	}
private class Listener_Cancel implements ActionListener{	//取消
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		GUI.headPath=GUI.headTemp;
		dispose();
	}
}
}
