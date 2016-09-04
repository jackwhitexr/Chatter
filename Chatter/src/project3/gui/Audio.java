package project3.gui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.media.Manager;
import javax.media.Player;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Audio {

	boolean stopCapture = false; //控制录音标志
	AudioFormat audioFormat; //录音格式
	
	//读取数据：从 TargetDataLine 写入 ByteArrayOutputStream 录音
	ByteArrayOutputStream byteArrayOutputStream;
	int totaldatasize = 0;
	TargetDataLine targetDataLine; 
	
	//播放数据：从 AudioInputStream 写入 SourceDataLine 播放
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	
	public Audio(ByteArrayOutputStream byteArrayOutputStream) {
		this.byteArrayOutputStream = byteArrayOutputStream;
	}
	
	

	public Audio() {
		super();
		// TODO Auto-generated constructor stub
		audioFormat = null;
		byteArrayOutputStream = null;
		targetDataLine = null;
		audioInputStream = null;
		sourceDataLine = null;
	}



	//（1）录音事件，保存到 ByteArrayOutputStream 中
	public void capture() {
		try {
			//打开录音
			audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,
					audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
			
			//创建独立线程进行录音
			Thread captureThread = new Thread(new CaptureThread());
			captureThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	//（2）播放 ByteArrayOutputStream 中的数据
	public void play() {
		try {
			//取得录音数据
			byte audioData[] = byteArrayOutputStream.toByteArray();
			
			//转换成输入流
			InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
			AudioFormat audioFormat = getAudioFormat();
			audioInputStream = new AudioInputStream(byteArrayInputStream,
					audioFormat, audioData.length / audioFormat.getFrameSize());
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();

			//创建独立线程进行播放
			Thread playThread = new Thread(new PlayThread());
			playThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	//（3）停止录音
	public void stop() {
		stopCapture = true;
		targetDataLine.close();
	}
	
	//（4）保存文件
	public void save(String path) {
		String file_path = path;
		
		//取得录音输入流
		AudioFormat audioFormat = getAudioFormat();
		byte audioData[] = byteArrayOutputStream.toByteArray();
		InputStream  byteArrayInputStream  =  new
				ByteArrayInputStream(audioData);
		audioInputStream = new AudioInputStream(byteArrayInputStream,
				audioFormat, audioData.length / audioFormat.getFrameSize());

		//写入文件
		try {
			File file = new File(file_path);
			AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//（5）播放录音文件
	public static void playVedio(String path) {
		File f = new File(path);// 根据音频文件的路径创建File对象
		try {
			URL url = f.toURI().toURL();// 得到音频文件的URL地址
			// 调用管理器创建Player对象的方法
			Player player = Manager.createRealizedPlayer(url);
			// 加载多媒体音频的数据
			player.prefetch();
			// 调用播放的方法
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//取得 AudioFormat(音频格式)
	public AudioFormat getAudioFormat() {
		float sampleRate = 16000.0F;
		
		//8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		
		//8,16
		int channels = 1;
		
		//1,2
		boolean signed = true;

		//true,false
		boolean bigEndian = false;

		//true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}
	
	
	class CaptureThread implements Runnable {
		//临时数组
		byte tempBuffer[] = new byte[10000];
		public void run() {
			byteArrayOutputStream = new ByteArrayOutputStream();
			totaldatasize = 0;
			stopCapture = false;
			
			try {
				//循环执行，直到按下停止录音按钮
				while (!stopCapture) {
					//读取10000个数据
					int cnt = targetDataLine.read(tempBuffer, 0,
							tempBuffer.length);
					if (cnt > 0) {
						//保存该数据
						byteArrayOutputStream.write(tempBuffer, 0, cnt);
						totaldatasize += cnt;
					}
				}
				byteArrayOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	class PlayThread implements Runnable {
		byte tempBuffer[] = new byte[10000];
		public void run() {
			try {
				int cnt;
				//读取数据到缓存数据
				while ((cnt = audioInputStream.read(tempBuffer, 0,
						tempBuffer.length)) != -1) {
					if (cnt > 0) {
						//写入缓存数据
						sourceDataLine.write(tempBuffer, 0, cnt);
					}
				}
				//Block 等待临时数据被输出为空
				sourceDataLine.drain();
				sourceDataLine.close();
			
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		Audio au = new Audio();
		String path = "D:\\软件\\酷狗\\downloads\\彭青 - 这么近那么远 - 单曲版.mp3";
		au.playVedio(path);
	}*/
}
