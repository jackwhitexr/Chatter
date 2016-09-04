package project3.gui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import project3.constant.NetConnection;


public class SendFile implements Runnable{

	String enemyaddress;
	File selectedFile;
	InputStream readyStream;                  //要发送的文件流或录音流
	String conRequest;
	int flagVoice=0;
	//发送文件的构造方法
	SendFile(String address,File readyFile){
		this.enemyaddress = address;
		this.selectedFile=readyFile;
		try {
			conRequest="文件"+"  "+selectedFile.getName()+"  "+selectedFile.length()+"  "+InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		try {
			readyStream = new FileInputStream(selectedFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	//发送语音的构造方法
	SendFile(String address,ByteArrayOutputStream byteArrayOutputStream,int totaldatasize){
		this.enemyaddress=address;
		//建立连接的消息
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String time=format.format(date);
		try {
			conRequest="语音"+"  "+time+".wav"+"  "+totaldatasize+"  "+InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//将传入的录音输出流转为输入流
		byte audioData[] = byteArrayOutputStream.toByteArray();
		readyStream = new ByteArrayInputStream(audioData);
		flagVoice=1;                         //标识下面发的是语音
	}
	
	public synchronized void run(){
		try {
			Socket sendSocket = new Socket(InetAddress.getByName(enemyaddress),NetConnection.FILE_PORT);
			InputStream input =  sendSocket.getInputStream();
			OutputStream output = sendSocket.getOutputStream();
			output.write(conRequest.getBytes());        //发送文件名和文件长度
			byte[] reply = new byte[100];
			input.read(reply);                          //接收对方的确认消息
			if(new String(reply).trim().equals("ok")){
				//用带进度条的流将源文件流细化
				ProgressMonitorInputStream cutFileUp = new ProgressMonitorInputStream(null,"文件传输中...",readyStream);
				ProgressMonitor monitor = cutFileUp.getProgressMonitor();
				monitor.setMillisToDecideToPopup(0);          //设置弹出进度监视器之前的等待时间为0
				monitor.setMillisToPopup(0);                  //设置显示进度监视器花费的时间为0
				byte[] dividedFile = new byte[1024*1024*10];  //用于传输文件的字节数组
				int len=cutFileUp.read(dividedFile);                //一次传输的数组长度
				while(len!=-1){
					output.write(dividedFile,0,len);
					output.flush();
					len=cutFileUp.read(dividedFile);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(flagVoice==0){
				input.read(reply);
				JOptionPane.showMessageDialog(null,  new String(reply).trim(), "发送报告", 1);
				}
				readyStream.close();
			}
			sendSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
