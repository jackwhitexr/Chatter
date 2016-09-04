package project3.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.security.ntlm.Client;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Video
{    
	OpenCVFrameGrabber grabber;
	IplImage image;
	CanvasFrame canvas;
	Graphics2D bGraphics;
	byte[] videoByte = null;
	int width;    
    int height;
	
	//@SuppressWarnings("null")
    public void Open() throws org.bytedeco.javacv.FrameGrabber.Exception {
		grabber = new OpenCVFrameGrabber(0);    
        grabber.start();   //开始获取摄像头数据  
        //grabber.toString();
        image =grabber.grab(); //将所获取摄像头数据放入IplImage 
	}
    
    
	public void Capture() throws Exception   
    {     
        
        width = image.width();    
        height = image.height();  
        System.out.println(width);
        System.out.println(height);
        
        final BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);    
        bGraphics = bImage.createGraphics();                         

        if ( (image=grabber.grab()) != null) {
    	   BufferedImage a2=image.getBufferedImage();
    	   byte[] data=imageToBytes(a2);
    	   BASE64Encoder encoder = new BASE64Encoder();
    	   String str1= encoder.encode(data);
    	   BASE64Decoder decoder = new BASE64Decoder();
    	   
    	   videoByte = decoder.decodeBuffer(str1);
    	   
    	   if(Arrays.equals(videoByte, data)){					//在比较两个字符数组是否相等 可以用函数Arrays.equals(字符数组1，字符数组2）
    		   System.out.println("right");
    	   }
    	   else{
    		   System.out.println("wrong");
    	   }
//    	   ByteArrayOutputStream out = new ByteArrayOutputStream();
//    	   out.write(b);
        }             
    }  
      
	public void Stop()  {
		try {
			grabber.stop();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	} 
       
	public void startPlay(){
		canvas = new CanvasFrame("Camera",1);  
        canvas.setCanvasSize(width, height);
	}
	public void Play(ByteArrayInputStream in) throws IOException {
		
		if ( canvas.isVisible() ) {  
			BufferedImage image2 = ImageIO.read( in);
			canvas.showImage(image2);   
//			bGraphics.drawImage(image.getBufferedImage(),null,0,0);  
		}
		
	}
	
	public void overPlay(){
		canvas.dispose();
	}
	
    public byte[] imageToBytes (BufferedImage value) {  
    	BufferedImage bImage=value;
        ByteArrayOutputStream out = new ByteArrayOutputStream();   
        try {   
            ImageIO.write(bImage, "jpg", out);   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
        return out.toByteArray();   
    }   

    /**  
     * 转换byte数组为Image  
     * @param bytes  
     * @return Image  
     */  
    public Image bytesToImage(byte[] bytes) {   
        Image image = Toolkit.getDefaultToolkit().createImage(bytes);   
        try {   
            MediaTracker mt = new MediaTracker(new Label());   
            mt.addImage(image, 0);   
            mt.waitForAll();   
        } catch (InterruptedException e) {   
            e.printStackTrace();   
        }   

        return image;   
    }
}

class VideoCapture extends Thread {
	Video vCapture;
	InputStream in;
	OutputStream out;
	Socket client;
	int videoPort = 5000;
	private int videoRecord=0;			//当前窗口的视频用户数量
	
	public void add(){
		videoRecord=1;
	}
	public void dec(){
		videoRecord=0;
	}
	
	public VideoCapture(String ip) {
		// TODO Auto-generated constructor stub
		System.out.println("开始");
		vCapture = new Video();
		add();
		try {
			client = new Socket(ip, videoPort);
			in = client.getInputStream();
			out = client.getOutputStream();
			out.write("hello".getBytes());
			byte[] temp = new byte[100];	
			in.read(temp);
			String s = new String(temp).trim();
			System.out.println(s);
			
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			vCapture.Open();
			while (true) {
				if(videoRecord>0){
					vCapture.Capture();
					if ( vCapture.videoByte != null) {
						out.write(vCapture.videoByte);
					}
				}
				else{
					break;
				}
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			vCapture.Stop();		//结束捕捉
			in.close();
			out.close();
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class VideoPlay extends Thread {
	Video vPlay;
	InputStream in;
	OutputStream out;
	ServerSocket serverSocket;
	byte[] playByte=new byte[1024*1024*15];
	int port = 5000;
	private int videoRecord=0;			//当前窗口的视频用户数量
	
	public void add(){
		videoRecord=1;
	}
	public void dec(){
		videoRecord=0;
	}
	public VideoPlay() {
		// TODO Auto-generated constructor stub
		vPlay = new Video();
		add();
		try {
			serverSocket = new ServerSocket(port);
			Socket socket = serverSocket.accept();
			in = socket.getInputStream();
			out = socket.getOutputStream();
			byte[] message=new byte[100];
			in.read(message);
			String st=new String(message).trim();
			System.out.println(st);
			out.write("sheet".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		vPlay.startPlay();
		try {
			while ( true ) {
				if(videoRecord>0){
					in.read(playByte);
					ByteArrayInputStream play = new ByteArrayInputStream(playByte);
					vPlay.Play(play);
				}
				else{
					break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		vPlay.overPlay();		//结束放映
		
		try {
			serverSocket.close();
			in.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}