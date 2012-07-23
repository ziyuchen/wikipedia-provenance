/**
 * 
 */
package uk.ac.ncl.ziyu.gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author Ziyu CHEN
 * Jul 19, 2012
 *
 */
public class ErrorMessage {

	private JFrame frame;
	private static ErrorMessage singlton=null;
	
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ErrorMessage window = new ErrorMessage();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	private ErrorMessage() {
			initialize();
	}

	public static JFrame generateErrorMessage(){
		if(singlton==null){
			singlton=new ErrorMessage();
			return singlton.frame;
		}
		else
			return singlton.frame;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblCheckConnect = new JLabel("Cannot connect to the Neo4j, please check the connect.....");
		lblCheckConnect.setFont(new Font("Traditional Arabic", Font.PLAIN, 17));
		lblCheckConnect.setForeground(SystemColor.windowBorder);
		lblCheckConnect.setBounds(10, 66, 393, 39);
		frame.getContentPane().add(lblCheckConnect);
	}

}
