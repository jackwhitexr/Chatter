package project3.constant;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * 工具类
 * @author Mr.Bubbles
 *
 */
public class Constant{ 		  
	//连接两个数组
	public static byte[] arrayConnect(byte[] array1,byte[] array2){
		byte[] array3=new byte[array1.length+array2.length];
		System.arraycopy(array1, 0, array3, 0, array1.length);
		System.arraycopy(array2, 0, array3, array1.length,array2.length);
		return array3;
	}
	//分割数组array，舍弃第一个字符
	public static byte[] arrayDisconnect(byte[] array){
		byte[] newarray=new byte[array.length-1];
		System.arraycopy(array, 1, newarray, 0, newarray.length);
		return newarray;
	}
	//对图片重新划定大小， 聊天时显示图片
	public static ImageIcon resizeImage(ImageIcon sourceimage){
		Image img=sourceimage.getImage();
		int height=img.getHeight(null);
		int width=img.getWidth(null);
		int size=height*width;
		if(size>GUI.IMAGE_MAXSIZE){
			height=GUI.IMAGE_WIDTH*height/width;
			width=GUI.IMAGE_WIDTH;
			BufferedImage bufferedimage=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			bufferedimage.getGraphics().drawImage(img,0,0,width,height,null);
			ImageIcon imageicon=new ImageIcon(bufferedimage);
			return imageicon;
		}
		else
			return sourceimage;
		
	}
	
	//头像重新划定大小
	public static ImageIcon resizeImage(ImageIcon headshow,int want){
		Image img=headshow.getImage();
		BufferedImage bufferedimage=new BufferedImage(GUI.PORTRAIT_WIDTH,GUI.PORTRAIT_HEIGHT,BufferedImage.TYPE_INT_RGB);
		bufferedimage.getGraphics().drawImage(img,0,0,GUI.PORTRAIT_WIDTH,GUI.PORTRAIT_HEIGHT,null);
		ImageIcon imageicon=new ImageIcon(bufferedimage);
		return imageicon;	
	}
}