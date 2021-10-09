package com.openCV;


import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class Camera extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JLabel cameraScreen;
	private JButton btnCapture;
	private VideoCapture capture;
	private static MatOfByte frameB;
	private static Mat image;
	private static SpriteSheet newImage;
	private static String name;
	
	
	private boolean clicked = false;
	
	
	//metodo construtor 
	public Camera() {
		
		frameB = new MatOfByte();
		setLayout(null);
		
		cameraScreen = new JLabel();
		cameraScreen.setBounds(0,0,640,480);
		add(cameraScreen);
		
		
		//inicia botoes
		this.addButoons();
		
		btnCapture.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				clicked = true;				
			}
			
			
		});
		
		setSize(new Dimension(640,560));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	//faz a captura da camera padrao do sistema 
	public void startCamera () throws IOException{
		
		capture = new VideoCapture(0);
		image = new Mat();
		byte[] imageData;
		
		
		ImageIcon icon;
		
		while(true) {
			
			capture.read(image);
			
			
			final MatOfByte buf = new MatOfByte();
			Imgcodecs.imencode(".jpg",image,buf);
			
			imageData = buf.toArray();
			icon = new ImageIcon(imageData);
			cameraScreen.setIcon(icon);
			
			
			if(clicked) {
				
				name = JOptionPane.showInputDialog(this,"Insira o nome da pessoa");
				if(name == null) {
					JOptionPane.showMessageDialog(null,"erro");
					continue;
				}
				
				Imgcodecs.imwrite("images/" + name + ".jpg",image);
				this.localiza_face(image, name);
				clicked = false;
				
			}
			
			
		}
	}

	//adiciona botoes 
	public void addButoons() {
		btnCapture = new JButton("Cadastrar");
		btnCapture.setBounds(0,480,100,40);
		add(btnCapture);
	}
	
	
	//funcao que tenta extrai a face 
	public void localiza_face(Mat test,String name) throws IOException{
		/*
		 * ainda não está pronto!!
		 */
		int x = 0,y=0,l=0,a=0;
		Mat src = test;
		
		
		String xmlFile = "haarcascades\\haarcascade_frontalface_default.xml";
		CascadeClassifier cc = new CascadeClassifier(xmlFile);
		
		MatOfRect faceDetection = new MatOfRect();
		cc.detectMultiScale(src, faceDetection);
		System.out.println(String.format("Detected faces: %d", faceDetection.toArray().length));
		
		for(Rect rect: faceDetection.toArray()) {
			Imgproc.rectangle(src, new Point(x= rect.x,y= rect.y), new Point(rect.x + rect.width, rect.y + rect.height) , new Scalar(0, 0, 255), 3);
			
		
		}
		String path = "images/" + name + ".jpg";
		String face = "images/" + name + "Face.jpg";
		
		Imgcodecs.imwrite(path,src);
		
		
		

		if(faceDetection.toArray().length >= 1 ) {
			JOptionPane.showMessageDialog(this, "Rosto do "+ name + " cadastrado com sucesso");
		}else {
			JOptionPane.showMessageDialog(this, "Erro rosto não encontrado");
		}
		
		
	}
	
	public static byte[] getImgBytes(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "JPEG", baos);
        } catch (IOException ex) {
            //handle it here.... not implemented yet...
        }
        
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        
        return baos.toByteArray();
    }
	
	public static void exibiImagemLabel(byte[] minhaimagem, javax.swing.JLabel label) {
	        //primeiro verifica se tem a imagem
	        //se tem convert para inputstream que é o formato reconhecido pelo ImageIO
	       
	        if(minhaimagem!=null)
	        {
	            InputStream input = new ByteArrayInputStream(minhaimagem);
	            try {
	                BufferedImage imagem = ImageIO.read(input);
	                label.setIcon(new ImageIcon(imagem));
	            } catch (IOException ex) {
	            }
	            
	        
	        }
	        else
	        {
	            label.setIcon(null);
	            
	        }

	}

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		EventQueue.invokeLater(new Runnable() {
		
			public void run() {
				Camera camera = new Camera();
				new Thread (new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							camera.startCamera();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}).start();
			}
		});;
	}

}
