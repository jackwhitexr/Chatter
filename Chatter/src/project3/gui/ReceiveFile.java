package project3.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import project3.constant.NetConnection;
import project3.constant.SystemInfo;

public class ReceiveFile implements Runnable{

	String enemyIP;
	String defaultFileName;
	ServerSocket receiveSocket;
	Socket socket;
	File desFile;
	ReceiveFile(){
	}
	public synchronized void run(){
		while(true){
			try {
				receiveSocket= new ServerSocket(NetConnection.FILE_PORT);
				socket = receiveSocket.accept();
				InputStream in =  socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				byte[] reply = new byte[100];
				in.read(reply);
				String nameLen = new String(reply).trim();
				String[] str = nameLen.split("  ");
				defaultFileName=str[1];
				long fileSize = Long.parseLong(str[2]);
				out.write("ok".getBytes());
				enemyIP=str[3];
				System.out.println(enemyIP);
				//显示聊天窗口
				if(User.historyFrame.containsKey(enemyIP)){
					
				}
				else{
					ChatGUI chatgui=new ChatGUI(enemyIP);
					User.historyFrame.put(enemyIP,chatgui);
				}
					
				//接收文件
				if(str[0].equals("文件"))
				{
					//更改文件选择器的外观
					if(UIManager.getLookAndFeel().isSupportedLookAndFeel()){
						final String platform = UIManager.getSystemLookAndFeelClassName();
						// If the current Look & Feel does not match the platform Look & Feel,
						// change it so it does.
						if (!UIManager.getLookAndFeel().getName().equals(platform)) {
							try {
								UIManager.setLookAndFeel(platform);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}
					}
					JFileChooser jfc = new JFileChooser();   //文件保存
					FileSystemView fsv=FileSystemView.getFileSystemView();
					jfc.setCurrentDirectory(fsv.getHomeDirectory());     //默认路径为桌面
					jfc.setSelectedFile(new File(defaultFileName));      //默认文件名
					int flag = jfc.showSaveDialog(null);
					if(flag==JFileChooser.APPROVE_OPTION){
							desFile = jfc.getSelectedFile();
						}
					ProgressMonitorInputStream inputProgress = new ProgressMonitorInputStream(null,"文件传输中",socket.getInputStream());
					ProgressMonitor monitor = inputProgress.getProgressMonitor();
					monitor.setMillisToDecideToPopup(0);         //设置弹出进度监视器之前的等待时间为0
					monitor.setMillisToPopup(0);                 //设置显示进度监视器话费的我时间为0
					monitor.setMaximum((int)fileSize);           //设置进度条增长的最大值
					
					FileOutputStream fos = new FileOutputStream(new File(desFile.getParent(),str[1]));
					byte[] dividedFile = new byte[1024*1024*15];
					long len = 0;
					long size = 0;
					while(true)
						{
							len = inputProgress.read(dividedFile);
							fos.write(dividedFile,0,(int)len);
							size+=len;
							if(size>=fileSize)
								{
									break;
								}
						}
					out.write("over ！".getBytes());
					fos.flush();
					fos.close();
					JOptionPane.showMessageDialog(null,  "over!", "接受报告", 1);
				}
				//接收语音
				else if(str[0].equals("语音")){
					Audio saveRecord=new Audio();
					AudioFormat audioFormat = saveRecord.getAudioFormat();
					AudioInputStream audioInputStream = new AudioInputStream(socket.getInputStream(),
							audioFormat, Integer.valueOf(str[2])/ audioFormat.getFrameSize());
					try{
						//String file_path="D:\\语音消息\\"+defaultFileName;
						String file_path=SystemInfo.DEFAULT_VOICE_FILE_PATH+defaultFileName;
						File file = new File(file_path);
						AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);   //将语音文件写入硬盘
						User.historyFrame.get(enemyIP).addVoice("对方");            //在相应聊天窗口里添加一条语音记录
						User.historyFrame.get(enemyIP).historyVoice.put(User.historyFrame.get(enemyIP).voiceCount, file_path);
						User.historyFrame.get(enemyIP).voiceCount++;
						
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				socket.close();
				receiveSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
