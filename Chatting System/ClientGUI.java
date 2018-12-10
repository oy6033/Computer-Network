import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.Insets;

import javax.net.ssl.SSLEngineResult.Status;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JRadioButton;

public class ClientGUI {

	private JFrame frame;
	private JTextField textField_1;
	private JLabel lblUserLogin;
	private InetAddress address;
	private int count = 0;
	private JTextArea textArea;
	public static JTextArea textArea_1;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private DatagramSocket socket = null;
	private String check_global_token = "";
	private JRadioButton rdbtnSendFile;
	private JRadioButton rdbtnNewRadioButton;
	private String ip = "";
	private String userID = "null";
	private JRadioButton rdbtnNewRadioButton_1;
	
	public static void getList(List<String> list, String information){
		String [] arr = information.split("->");
		for(int i = 0; i < arr[1].length();i++) {
			if(arr[1].charAt(i) == '<') {
				String res = "";
				while(arr[1].charAt(i+1)!='>') {
					res = res +arr[1].charAt(i+1);
					i=i+1;
				}
				list.add(res);
			}
		}
	}
	
	public static String getMessage(String information) {
		int count = 0;
		for(int i = 0 ; i < information.length(); i ++) {
			if(information.charAt(i)=='>') count =i;
		}
		return information.substring(count+1, information.length());
	}
	
	public static String getCurrentTime() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dNow = new Date( );
		return "("+df.format(dNow)+")\n";
		
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 735, 581);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 183, 217, 175, 0};
		gridBagLayout.rowHeights = new int[]{28, 0, 0, 221, 28, 226, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblInputMessage = new JLabel(" Input Message:");
		GridBagConstraints gbc_lblInputMessage = new GridBagConstraints();
		gbc_lblInputMessage.anchor = GridBagConstraints.WEST;
		gbc_lblInputMessage.insets = new Insets(0, 0, 5, 5);
		gbc_lblInputMessage.gridx = 0;
		gbc_lblInputMessage.gridy = 0;
		frame.getContentPane().add(lblInputMessage, gbc_lblInputMessage);
		
		lblUserLogin = new JLabel("user login ");
		lblUserLogin.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblUserLogin = new GridBagConstraints();
		gbc_lblUserLogin.gridwidth = 2;
		gbc_lblUserLogin.insets = new Insets(0, 0, 5, 5);
		gbc_lblUserLogin.gridx = 1;
		gbc_lblUserLogin.gridy = 0;
		frame.getContentPane().add(lblUserLogin, gbc_lblUserLogin);
		
		JLabel lblInput = new JLabel(" Input:");
		GridBagConstraints gbc_lblInput = new GridBagConstraints();
		gbc_lblInput.anchor = GridBagConstraints.WEST;
		gbc_lblInput.insets = new Insets(0, 0, 5, 5);
		gbc_lblInput.gridx = 0;
		gbc_lblInput.gridy = 1;
		frame.getContentPane().add(lblInput, gbc_lblInput);
		
		textField_1 = new JTextField("yangkang->server#<123456><127.0.0.1><12345>");
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.gridwidth = 2;
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 1;
		frame.getContentPane().add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		
		rdbtnNewRadioButton = new JRadioButton("Send Message");
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(rdbtnNewRadioButton.isSelected()) {
					textField_1.setText("yangkang->server#<123456><127.0.0.1><12345>");
				}
			}
		});
		rdbtnNewRadioButton.setSelected(true);
		GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
		gbc_rdbtnNewRadioButton.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNewRadioButton.gridx = 1;
		gbc_rdbtnNewRadioButton.gridy = 2;
		frame.getContentPane().add(rdbtnNewRadioButton, gbc_rdbtnNewRadioButton);
		
		rdbtnSendFile = new JRadioButton("Upload File");
		rdbtnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(rdbtnSendFile.isSelected()) {
					textField_1.setText("D:\\VirtualBox Share\\TCP.zip");
				}
			}
		});
		GridBagConstraints gbc_rdbtnSendFile = new GridBagConstraints();
		gbc_rdbtnSendFile.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnSendFile.gridx = 2;
		gbc_rdbtnSendFile.gridy = 2;
		frame.getContentPane().add(rdbtnSendFile, gbc_rdbtnSendFile);
		
		rdbtnNewRadioButton_1 = new JRadioButton("Download File");
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnNewRadioButton_1.isSelected()) {
					textField_1.setText("ml.jpg");
				}
			}
		});
		GridBagConstraints gbc_rdbtnNewRadioButton_1 = new GridBagConstraints();
		gbc_rdbtnNewRadioButton_1.fill = GridBagConstraints.VERTICAL;
		gbc_rdbtnNewRadioButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnNewRadioButton_1.gridx = 3;
		gbc_rdbtnNewRadioButton_1.gridy = 2;
		frame.getContentPane().add(rdbtnNewRadioButton_1, gbc_rdbtnNewRadioButton_1);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(rdbtnNewRadioButton);
		buttonGroup.add(rdbtnSendFile);
		buttonGroup.add(rdbtnNewRadioButton_1);
		
		JButton btnEnter = new JButton("Enter");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<String> login = new ArrayList<>();
				
				if(rdbtnNewRadioButton.isSelected()) {
					String input = textField_1.getText().toString();
					System.out.println(input);
					textArea.append(input+"\n");
					textField_1.setText("");
					if(count == 0) {
						try {
							System.out.println("Please enter the information to login:");
							String information = input;
							if(information.equals("exit")) {
								System.out.println("done");
								//break;
							}
							String [] arr = information.split("->");
							getList(login,information);
							System.out.println(login);
							ip=login.get(1);
							if(login.size()!=3||!information.contains("#")) {
								System.out.println("Error: wrong format client_a->server#login<password><ip><port> ");
								throw new IOException("Error: wrong format client_a->server#login<password><ip><port>");
								//break;
							}
							address = InetAddress.getByName(login.get(1));
							String [] direction = information.split("#");
							byte[] data = new String(arr[0]+":"+login.get(0)+":"+ direction[0]+":login:"+login.get(1)).getBytes();
							DatagramPacket packet = new DatagramPacket(data, data.length, address, 9999);
							
							//2、DataframSocket
							socket = new DatagramSocket(Integer.parseInt(login.get(2)),address);
//							reciveMsg receive = new reciveMsg(socket,address,userID);
							//3、send to server
							socket.send(packet);
							//yangkang->server#<123456><127.0.0.1><12345>
							//lvduo->server#<65432><127.0.0.1><12342>
							//4、get the buf
							byte[] reciveMsg = new byte[4096];
							DatagramPacket reply = new DatagramPacket(reciveMsg, reciveMsg.length);
							//4. receive message by server
							socket.receive(reply);
							String msg = new String(reciveMsg, 0, reply.getLength());
							//5.thread 
							reciveMsg_GUI receive = new reciveMsg_GUI(socket,address,userID,textArea_1);
							receive.start();
							if(msg.contains("Success")) {
								userID = arr[0];
								lblUserLogin.setText("Welcome! "+userID);
//								reciveMsg_GUI receive = new reciveMsg_GUI(socket,address,userID,textArea_1);
								System.out.println(msg);
								textArea_1.setText(msg+getCurrentTime());
								System.out.println("Login successfully\n"+"Please enter the message to send");
								String [] split1 = msg.split("#");
			 					List<String> list = new ArrayList<>();
								
								for(int i = 0; i < split1[1].length();i++) {
									if(split1[1].charAt(i) == '<') {
										String res = "";
										while(split1[1].charAt(i+1)!='>') {
											res = res +split1[1].charAt(i+1);
											i=i+1;
										}
										list.add(res);
									}
								}
								check_global_token = list.get(0);
								System.out.println(check_global_token);
//								receive.start();
								count = count +1;
							}else {
								//String message = scan.nextLine();
								System.out.println(msg);
								textArea_1.setText(msg+getCurrentTime());
								socket.close();
							}
						}catch (UnknownHostException e) {
							e.printStackTrace();
						} catch (SocketException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else {
						//yangkang->server#<123456><127.0.0.1><12345>
						//lvduo->server#<65432><127.0.0.1><12342>
							//yangkang->lvduo#<XYZABC><messageID>some_message
							//yangkang->lvduo#<mvZ?#7><12345678>Hello
							//yangkang->server#logoff<pBqSED>
							String send = input;
							List<String> sendlist = new ArrayList<>();
							getList(sendlist,send);
//							long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
//							System.out.println(Long.toString(number));
//							System.out.println(sendlist);
							String [] direction_send = send.split("#");
							String [] toClient = direction_send[0].split("->");
							//input message
							String message = getMessage(direction_send[1]);
//							System.out.println(message);
							if(!send.contains("server#logoff")) {
								String toSend = toClient[0]+":" + toClient[1]+":" + sendlist.get(0) +":"+ sendlist.get(1)+":" + message;
								byte[] buf = new String(toClient[0]+":" + toClient[1]+":" + sendlist.get(0) +":"+ sendlist.get(1)
								+ ":" + message).getBytes();
								DatagramPacket send_packet = new DatagramPacket(buf, buf.length, address, 9999);
								DatagramSocket send_socket = null;
								try {
									send_socket = new DatagramSocket();
								} catch (SocketException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								try {
									send_socket.send(send_packet);
									
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								send_socket.close();
							}else {
								String toSend = toClient[0]+":" + toClient[1]+":" + sendlist.get(0)+":logoff";
								System.out.println(toSend);
								byte[] buf = new String(toSend).getBytes();
								DatagramPacket send_packet = new DatagramPacket(buf, buf.length, address, 9999);
								DatagramSocket send_socket = null;
								try {
									send_socket = new DatagramSocket();
								} catch (SocketException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								try {
									send_socket.send(send_packet);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								send_socket.close();
								if(sendlist.get(0).equals(check_global_token)) {
								}
							}
					}
				}else if(rdbtnSendFile.isSelected()) {
			        String str = textField_1.getText();
			        
			        File file = new File(str);
			        
			        if(!(file.exists()&& file.isFile())){
			            JOptionPane.showMessageDialog(null, "file not exists");
			            return ;
			        }
//			        if(!(file.getName().endsWith(".jpg")||file.getName().endsWith(".gif"))){
//			            JOptionPane.showMessageDialog(null, "文件格式不对,文件扩展名必须是jpg或gif！");
//			            return ;
//			        }
			        if( file.length()>=1024*1024*2){
			            JOptionPane.showMessageDialog(null, "file too large, please choose a small one");
			            return;
			        }
			//D:\\VirtualBox Share\\TCP.zip

			        try {
			        	
			        	//send name
			            Socket s = new Socket(ip, 9999);
			            
			            byte[] fileName_buf = new String(file.getName()+":"+userID).getBytes();
			            
			            OutputStream send_name = s.getOutputStream();
			            
			            send_name.write(fileName_buf);
			            
			            //read ok
			            byte [] buf= new byte[4096];
			            InputStream name_read = s.getInputStream();
			            int length_ok = name_read.read(buf);
			            System.out.println(new String(buf, 0, length_ok)+"\n");
			            
			            BufferedInputStream bin = null;
			            OutputStream out = null;
			            int total = 0;
			            if(new String(buf, 0, length_ok).equals("ok")) {
			            	
			                bin = new BufferedInputStream(new FileInputStream(str));

			                out = s.getOutputStream();

			                buf = new byte[4096];
			                int len=0;
			                while((len=bin.read(buf))!=-1){
			                    out.write(buf, 0, len);
			                    total = len+total;
			                }

			                s.shutdownOutput();
			            }

			            System.out.println(userID + " have sent total of "+ total + " bytes to server\n");
			            textArea_1.append(userID + " have sent total of "+ total + " bytes to server "+getCurrentTime());
			            
			            //read back
			            InputStream in = s.getInputStream();
			            byte buf2[] = new byte[4096];
			            int len2 = in.read(buf2);
			            textArea_1.append("server "+new String(buf2, 0, len2)+getCurrentTime());

			            //close
			            out.close();
			            bin.close();
			            s.close();

			        } catch (UnknownHostException e) {
			            e.printStackTrace();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
					
					
					
					
					
					
					
					
					
					
					
					
					
					
				}else if(rdbtnNewRadioButton_1.isSelected()) {
					//download file
					
		        	//send name
		            Socket s;
					try {
						s = new Socket(ip, 9999);
			            String fileName_text = "downloadfile:"+textField_1.getText()+":"+userID;
			            String fileName = textField_1.getText();
			            
			            byte[] fileName_buf = new String(fileName_text).getBytes();
			            
			            OutputStream send_name = s.getOutputStream();
			            
			            send_name.write(fileName_buf);
			            
			            File dir = new File("local");
			            //D:\\VirtualBox Share\\copy\\ml.jpg
			            if(!dir.exists()){
			                dir.mkdir();
			            }
			            BufferedInputStream bin = new BufferedInputStream(s.getInputStream());
			            
			            int count=1;
			            File file = new File(dir, fileName);

			            while(file.exists()){
			            	String[] split = fileName.split("\\.");
			                file = new File(dir,split[0]+"("+(count++) +")."+split[1]);
			            }

			            FileOutputStream fout = new FileOutputStream(file);

			            byte[] buf = new byte[4096];
			            //byte buf[] = new byte[1024];
			            int total = 0;
			            int len=0;
			            while( (len=bin.read(buf))!=-1){
			                fout.write(buf, 0, len);
			                total = len + total;
			            }

			            System.out.println(userID+" has received "+total+" bytes");
			            textArea_1.append(userID+" has downloaded "+total+" bytes"+getCurrentTime());
			            fout.close();
			            s.close();
			            
			            
					} catch (java.net.UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
				
		});
		GridBagConstraints gbc_btnEnter = new GridBagConstraints();
		gbc_btnEnter.insets = new Insets(0, 0, 5, 0);
		gbc_btnEnter.gridx = 3;
		gbc_btnEnter.gridy = 1;
		frame.getContentPane().add(btnEnter, gbc_btnEnter);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		JLabel lblReceivedMessage = new JLabel(" Received Message:");
		GridBagConstraints gbc_lblReceivedMessage = new GridBagConstraints();
		gbc_lblReceivedMessage.anchor = GridBagConstraints.WEST;
		gbc_lblReceivedMessage.insets = new Insets(0, 0, 5, 5);
		gbc_lblReceivedMessage.gridx = 0;
		gbc_lblReceivedMessage.gridy = 4;
		frame.getContentPane().add(lblReceivedMessage, gbc_lblReceivedMessage);
		
		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridwidth = 4;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 5;
		frame.getContentPane().add(scrollPane_1, gbc_scrollPane_1);
		
		textArea_1 = new JTextArea();
		textArea_1.setEditable(false);
		scrollPane_1.setViewportView(textArea_1);
		
		
		
	}
	
//	public void updateGUI( String msg) {
//		   textArea_1.append(msg+"\n");
//		}
	
	class reciveMsg_GUI extends Thread{
		
		public DatagramSocket socket;
		
		public InetAddress address;
		
		public String token;
		
		//public JTextArea textArea_1;
		
		public void getList(List<String> list, String information){
			String [] arr = information.split("->");
			for(int i = 0; i < arr[1].length();i++) {
				if(arr[1].charAt(i) == '<') {
					String res = "";
					while(arr[1].charAt(i+1)!='>') {
						res = res +arr[1].charAt(i+1);
						i=i+1;
					}
					list.add(res);
				}
			}
		}
		
		public reciveMsg_GUI(DatagramSocket socket, InetAddress address, String token, JTextArea textArea_1) {
			this.socket = socket;
			this.address = address;
			this.token = token;
			//this.textArea_1 = textArea_1;
		}
		
		public void run() {
			while(true) {
				try {
					byte[] reciveMsg = new byte[4096];
					DatagramPacket reply = new DatagramPacket(reciveMsg, reciveMsg.length);
					//4. receive message by server
					socket.receive(reply);
					String msg = new String(reciveMsg, 0, reply.getLength());
					String temp = msg.replace("%", "");
					System.out.println("Client received message: "+ temp);
					textArea_1.append(temp.replace("Clientlogoff","")+getCurrentTime());
					
					if(!msg.contains("server->")) {
						//yangkang->lvduo#<wRJrys><12345678> okssss
						String [] split1 = msg.split("#");
						//split2[1] = lvduo
						String [] split2 = split1[0].split("->");
						
						String [] split3 = msg.split("%");
	 					List<String> list = new ArrayList<>();
						
						for(int i = 0; i < split1[1].length();i++) {
							if(split1[1].charAt(i) == '<') {
								String res = "";
								while(split1[1].charAt(i+1)!='>') {
									res = res +split1[1].charAt(i+1);
									i=i+1;
								}
								list.add(res);
							}
						}
						
						
						byte[] buf = new String("yes:yes:yes:backToServerAfterRec:"+split2[1]+"->server#<"+list.get(0)+"><"+list.get(1)+">Success: "+split3[1]).getBytes();
						DatagramPacket send_back_server = new DatagramPacket(buf, buf.length, address, 9999);
						DatagramSocket send_back_server_socket = new DatagramSocket();
						send_back_server_socket.send(send_back_server);
					}else if(msg.contains("Clientlogoff")) {
						socket.close();
						count = 0;
						System.out.println("hello");
						lblUserLogin.setText("user login");
						textArea_1.append("Stop receiving message from server " + getCurrentTime());
					}

					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("thread stop, stop receive message from server");
					break;
				}
			}
		}
	}


}

