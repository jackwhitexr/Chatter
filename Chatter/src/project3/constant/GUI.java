package project3.constant;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 窗体的常量参数类
 * @author Mr.Bubbles
 *
 */
public class GUI{

	public static Dimension SCREENSIZE=Toolkit.getDefaultToolkit().getScreenSize(); //屏幕大小	
	//public static int LOCATION_WIDTH=(int)SCREENSIZE.getWidth()/2-MAIN_WIDTH/2;    //窗体的合适显示位置
	//public static int LOCATION_HEIGHT=(int)SCREENSIZE.getHeight()/2-MAIN_HEIGHT/2;
	
	public static int mainJFrame_Start_X=750;
	public static int mainJFrame_Start_Y=200;
	public static int mainJFrame_Width=350;
	public static int mainJFrame_Height=700;
	public static int InformationHeight=100;
	public static int SearchHeight=30;
	public static int FunctionHeight=80;
	public static int Setting_X=660;
	public static int Setting_Y=300;
	public static int Setting_Width=600;
	public static int Setting_Height=480;
	
	public static String Nickname="ZERO";
	public static JLabel jlableHomeName;							//主机面昵称
	public static JTextField jtextfieldName=new JTextField();		//设置界面昵称
	public static JTextArea jtPersonalizedSignature; 				//个性签名
	public static JTextArea jtextareaPersonalizedSignature=new JTextArea();
	public static JTextField jtextfieldSchool=new JTextField();
	public static JTextField jtextfieldSdept=new JTextField();
	public static JTextField jtextfieldSno=new JTextField();
	public static JTextField jtextfieldPhonenum=new JTextField();
	
	public static String headPath="src/project3/resource/yuyin.png";  //头像正真路径
	public static String headTemp;							//头像暂时路径
	
	public static int IMAGE_MAXSIZE=90000;
	public static int IMAGE_WIDTH=300;	//网络传输图片的宽
	public static int IMAGE_HEIGHT=300;	//网络传输图片的高
	public static int PORTRAIT_WIDTH=50;
	public static int PORTRAIT_HEIGHT=52;
	
}
