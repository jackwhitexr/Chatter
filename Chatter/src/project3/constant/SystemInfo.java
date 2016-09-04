package project3.constant;

/**
 * 本机信息类
 */
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.sun.media.OutputConnector;

public class SystemInfo {

	//暂定的本机信息
	private static String NICKNAME="Bubbles";
	private static ImageIcon PORTRAIT=new ImageIcon("src/project3/resource/yuyin.png");
	private static String SIGNATURE="水电费";
	
	public static final String DEFAULT_CONFIG_ROOT=System.getProperty("user.home");			//用户的主目录名
	
	public static final String DEFAULT_PATH_SEPERATOR=System.getProperty("file.separator");	//用户主机路径分隔符名
	
	public static final String DEFAULT_CONFIG_DIR="Project3";								//用户配置文件夹名
	
	public static final String DEFAULT_CONFIG_FILE="Local.conf";							//用户配置文件名
	
	public static final String DEFAULT_VOICE_DIR="voice_cache";								//用户的语音缓存文件名
	
	public static final String DEFAULT_PORTRAIT_FILE="portrait.png"; 
	
	public static final String DEFAULT_VOICE_FILE_PATH=										//用户的语音文件路径
			DEFAULT_CONFIG_ROOT+DEFAULT_PATH_SEPERATOR+DEFAULT_CONFIG_DIR+DEFAULT_PATH_SEPERATOR+DEFAULT_VOICE_DIR;
	
	public static final String DEFAULT_PORTRAIT_PATH=										//用户的头像文件路径
			DEFAULT_CONFIG_ROOT+DEFAULT_PATH_SEPERATOR+DEFAULT_CONFIG_DIR+DEFAULT_PATH_SEPERATOR+DEFAULT_PORTRAIT_FILE;
	//获取本机IP
	public static String getLocalIP(){
		String localip = null;
		try {
			localip=InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return localip;
	}
	//获取本机昵称
	public static String getMyName(){
		return NICKNAME;
	}
	//获取本机头像
	public static ImageIcon getMyPortrait(){
		return PORTRAIT;
	}
	//获取本机头像的字节数组
	public synchronized static byte[] getMyPortraitByte(){
		byte[] bytePortrait=new byte[NetConnection.BYTE_MAX];
		BufferedImage buf=new BufferedImage(PORTRAIT.getIconWidth(), PORTRAIT.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		buf.getGraphics().drawImage(PORTRAIT.getImage(),0,0,PORTRAIT.getIconWidth(),PORTRAIT.getIconHeight(),null);
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		try {
			ImageIO.write(buf,"JPEG",out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bytePortrait=out.toByteArray();
		
		return bytePortrait;
	}
	
	//获取本机个性签名
	public static String getSignature(){
		return SIGNATURE;
	}
	//设置本机昵称
	public static void setName(String name){
		NICKNAME=name;
	}
	//设置本机个性签名
	public static void setSignature(String signature){
		SIGNATURE=signature;
	}
	//设置本机头像
	public static void setPortrait(ImageIcon icon){
		PORTRAIT=icon;
	}
	
	//初始化本机所有信息
	public static void initSystemInfo(){
		String configdirpath=
				DEFAULT_CONFIG_ROOT+DEFAULT_PATH_SEPERATOR+DEFAULT_CONFIG_DIR;
		String configfilepath=
				configdirpath+DEFAULT_PATH_SEPERATOR+DEFAULT_CONFIG_FILE;
		String voicedirpath=
				DEFAULT_CONFIG_ROOT+DEFAULT_PATH_SEPERATOR+DEFAULT_CONFIG_DIR+DEFAULT_PATH_SEPERATOR+DEFAULT_VOICE_DIR;
		
		File configdir=new File(configdirpath);		//配置文件的目录
		File voicedir=new File(voicedirpath);		//语音文件的目录
		File icon=new File(DEFAULT_PORTRAIT_PATH);				//头像文件
		//语音目录判断，不存在就创建
		
		
		//配置文件判断
		if(configdir.exists()){			//检测目录是否存在
			File configfile=new File(configfilepath);
				if(configfile.exists()){		//检测文件是否存在 
					try {
						BufferedReader reader=new BufferedReader(new FileReader(configfile));	//读取
						setName(reader.readLine());
						setSignature(reader.readLine());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else{		//建立
					configfile.mkdir();
				}
		}
		else{			//不存在创建默认的目录及文件
			configdir.mkdir();		//创建目录
			File configfile=new File(configfilepath);
			BufferedWriter writer=null;
			try {
				configfile.createNewFile();		//创建文件
				writer=new BufferedWriter(new FileWriter(configfile));
				writer.write("昵称");
				writer.newLine();
				writer.write("个性签名");
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		if(voicedir.exists()==false){
			voicedir.mkdir();
		}
		
		//头像文件的判断，不存在就创建
		if(icon.exists()==false){
			byte[] imagedata=new byte[NetConnection.BYTE_MAX];
			FileOutputStream output=null;
			try {
				FileInputStream input=new FileInputStream(new File("src/project3/resource/yuyin.png"));
				input.read(imagedata);
				output=new FileOutputStream(new File(DEFAULT_PORTRAIT_PATH));
				output.write(imagedata);
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				try {
					output.flush();
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else{
			ImageIcon portrait=new ImageIcon(DEFAULT_PORTRAIT_PATH);
			setPortrait(portrait);
		}
	}
	//保存配置信息
	public static void saveSystemInfo(){
		String dirpath=
				DEFAULT_CONFIG_ROOT+DEFAULT_PATH_SEPERATOR+DEFAULT_CONFIG_DIR;
		String filepath=
				dirpath+DEFAULT_PATH_SEPERATOR+DEFAULT_CONFIG_FILE;
		File config=new File(filepath);		//配置文件
		BufferedWriter writer=null;
		try {
			//保存配置文件
			writer=new BufferedWriter(new FileWriter(config,false));
			writer.write(getMyName());
			writer.newLine();
			writer.write(getSignature());
			//保存头像
			ImageIcon portrait=Constant.resizeImage(SystemInfo.PORTRAIT, NetConnection.WANT_HEADSHOW);
			BufferedImage buf=new BufferedImage(portrait.getIconWidth(), portrait.getIconHeight(), BufferedImage.TYPE_INT_RGB);
			buf.getGraphics().drawImage(portrait.getImage(), 0, 0, portrait.getIconWidth(), portrait.getIconHeight(),null);
			ImageIO.write(buf, "png",new File(DEFAULT_PORTRAIT_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
