import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UPDServer extends Thread{
	
	public static List<String> list = new ArrayList<>();
	
	public static Map<String,String> map;
	
	public static Map<String,String> ID_token = new HashMap<>();
	
	public static Map<String,Integer> portMap = new HashMap<>();
	
	//public static Map<String,Long> TimeOut = new HashMap<>();
	public static ConcurrentHashMap<String, Long> TimeOut = new ConcurrentHashMap<String, Long>();
	
	public static Map<String,String> ipAddress = new HashMap<>();
	
	public static void holdToken(String token) {
		list.add(token);
	}
	
	public static void holdID_token(String ID, String token) {
		ID_token.put(ID, token);
	}
	
	public static void holdPort(String ID, int port) {
		portMap.put(ID, port);
	}
	
	public static void setTime(String ID, Long time) {
		TimeOut.put(ID, time);
	}
	
	public static void holdIP(String ID, String IP) {
		ipAddress.put(ID, IP);
	}
	
	public static void logoff(InetAddress address,byte[] msg, DatagramSocket socket, String userID, String output) {
		DatagramPacket reply = new DatagramPacket(msg, msg.length, address, portMap.get(userID));
		ID_token.remove(userID);
		portMap.remove(userID);
		TimeOut.remove(userID);
		System.out.println(userID+ output);
		try {
			socket.send(reply);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public static DatagramPacket packet;
	
	public static DatagramSocket socket;
	
	//public static String userID;
	
	//public static byte[] msg;

	public UPDServer(DatagramSocket socket) {
		//this.packet = packet;
		this.socket = socket;
		//this.userID = userID;
		//this.msg = msg;
	}
	
//    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
//    private Lock writeLock = lock.writeLock();
//    private Lock readLock = lock.readLock();
	
	
	public void run() {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dNow = new Date( );
			Date now = df.parse(df.format(dNow));
			Set<String> keyset = TimeOut.keySet();
			for(String userID: keyset) {
//				System.out.println((now.getTime()- TimeOut.get(userID))/1000);
//				System.out.println(userID);
				if((now.getTime()- TimeOut.get(userID))/1000 > 300) {
					
//					TimeOut.remove(userID);
//					ID_token.remove(userID);
//					portMap.remove(userID);
					InetAddress addr = InetAddress.getByName(ipAddress.get(userID));
//					System.out.println(addr.toString());
					logoff(addr,new String("server->"+userID+"#Success<"+ID_token.get(userID)+">Clientlogoff5mins Time out").getBytes()
							,socket,userID," has time out");
					//System.out.println(userID+" has time out");
					break;
				}
			}
//			System.out.println(java.time.LocalDateTime.now());
		} catch (ParseException | UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("thread wait, access HashMap at the same time");
		}
	}
	
	public static String randomGenerated() {
        String SALTCHARS = "0123456789?@ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length()<6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
	}
	
	public static void account(File file){
		map = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			  String st,strName="",strPassword="";
			  Boolean name = false,password = false;
			  while ((st = br.readLine()) != null) {
				  if(st.contains("username")) {
					  strName = st;
					  name = true;
				  }
				  if(st.contains("password")) {
					  strPassword = st;
					  password = true;
				  }
				  if(password==true && name == true) {
					  map.put(strName.substring(10,strName.length()), strPassword.substring(10,strPassword.length()));
					  name = false;
					  password = false;
				  }
			  }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	}
	
	public static Boolean validation(Map<String,String> map, String information) {
		String[] arr = information.split(":");
		if(map.get(arr[0]).equals(arr[1])) return true;
		return false;
	}
	
	public static String[] splitInformation(String information) {
		String[] arr = information.split(":");
		return arr;
	}
	
	public static void main(String[] args) {

		DatagramSocket socket = null;
		ServerSocket server = null;
		byte[] data = new byte[4096];
		File file = new File("account.txt");
		account(file);
		System.out.println("Server has Started");
		try {
			socket = new DatagramSocket(9999);
			server = new ServerSocket(9999);
			TCPServer_r TCP = new TCPServer_r(server);
			TCP.start();
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			while(true) {
				DatagramPacket packet = new DatagramPacket(data, 0, data.length);
				try {
					//test timeout
					socket.setSoTimeout(10000);
					socket.receive(packet);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
//					System.out.println("time out");
					new UPDServer(socket).start();
					continue;
				}
				String information = new String(data, 0, packet.getLength());
				//System.out.println(information);
				String [] split = splitInformation(information);
				if(split[3].equals("login")) {
					if (validation(map,information)) {
						//thread check last time user active
						try {
							Date dNow = new Date( );
							Date d1 = df.parse(df.format(dNow));
							System.out.println(df.format(dNow));
							TimeOut.put(split[0], d1.getTime());

							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						System.out.println(split[0]+" has login");
						String token = randomGenerated();
//						UDPServerThread thread = new UDPServerThread(socket, packet, data, token,split[0]);
						holdID_token(split[0],token);
						holdPort(split[0],packet.getPort());
						holdIP(split[0],split[4]);
//						System.out.println(ID_token.keySet());
//						System.out.println(ID_token.values());
//						System.out.println(portMap.keySet());
//						System.out.println(portMap.values());
//						holdToken(token);
//						System.out.println(list);
//						thread.start();
						byte[] msg = new String("server->"+split[0]+"#Success<"+token+">").getBytes();
						InetAddress address = packet.getAddress();
						int port = packet.getPort();
//						System.out.println("ip address: "+packet.getAddress().getHostAddress());
//						System.out.println("port: "+port);
						DatagramPacket reply = new DatagramPacket(msg, msg.length, address, port);
						try {
							socket.send(reply);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						byte[] msg = new String("server->"+split[0]+"#Error:password does not match!").getBytes();
						InetAddress address = packet.getAddress();
						int port = packet.getPort();
						DatagramPacket reply = new DatagramPacket(msg, msg.length, address, port);
						try {
							socket.send(reply);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else if(split[3].equals("backToServerAfterRec")) {
					System.out.println(split[4]+":"+split[5]);
					
				}else {
					//client_a:client_b:XYZABC:msg_id:msg
					//check if token correct
					System.out.println(split[0]);
					if(ID_token.containsKey(split[0])) {
						if(ID_token.get(split[0]).equals(split[2])) {
							//return log off
							if(split[3].equals("logoff")) {
//								InetAddress address = packet.getAddress();
//								byte[] msg = new String("server->"+split[0]+"#Success<"+ID_token.get(split[0])+">Clientlogoff").getBytes();
//								DatagramPacket reply = new DatagramPacket(msg, msg.length, address, portMap.get(split[0]));
//								ID_token.remove(split[0]);
//								portMap.remove(split[0]);
//								TimeOut.remove(split[0]);
//								System.out.println(ID_token.keySet());
//								System.out.println(split[0]+ " has log off");
//								try {
//									socket.send(reply);
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
								logoff(packet.getAddress(),new String("server->"+split[0]+"#Success<"+ID_token.get(split[0])+">Clientlogoff").getBytes()
										,socket,split[0]," has log out");
							}else {
								if(ID_token.containsKey(split[1])) {
									System.out.println("send successfully");
									Date dNow = new Date( );
									try {
										Date d2 = df.parse(df.format(dNow));
//										System.out.println(df.format(dNow));
//										System.out.println(d2.getTime());
										TimeOut.put(split[0], d2.getTime());
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									//send back
									InetAddress address = packet.getAddress();
									byte[] msg = new String("server->"+split[0]+"<"+ID_token.get(split[0])+">"+
									"<"+split[3]+">"+"Success: "+split[4]).getBytes();
									int port = packet.getPort();
//									System.out.println(port);
									DatagramPacket reply = new DatagramPacket(msg, msg.length, address, portMap.get(split[0]));
									try {
										socket.send(reply);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									//send to
									//InetAddress address = packet.getAddress();
									byte[] tranfer_msg = new String(split[0]+"->"+split[1]+"#<"+ID_token.get(split[1])+">"+
									"<" + split[3] +">%"+ split[4]).getBytes();
									DatagramPacket tranfpacket = new DatagramPacket(tranfer_msg, tranfer_msg.length, address, portMap.get(split[1]));
//									UDPServerThread tranf = new UDPServerThread(socket, tranfpacket, tranfer_msg, portMap.get(split[1]), split[0]);
//									tranf.start();
									try {
										socket.send(tranfpacket);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									//
								}else {
//									System.out.println("server->client_a#<XYZABC><msg_id>Error: destination offline!");
//									System.out.println("no");
									byte[] msg = new String("server->"+split[0]+"#<"+ID_token.get(split[0])+">"+"Error: destination offline!").getBytes();
									InetAddress address = packet.getAddress();
//									System.out.println(split[0]);
//									System.out.println(portMap.get(split[0]));
									DatagramPacket reply = new DatagramPacket(msg, msg.length, address, portMap.get(split[0]));
									try {
										socket.send(reply);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}else {
							System.out.println("server->client_a#<XYZABC><msg_id>Error: token error!");
							byte[] msg = new String("server->"+split[0]+"#<"+ID_token.get(split[0])+"><"+split[3]+">Error: token error!").getBytes();
							InetAddress address = packet.getAddress();
//							System.out.println(split[0]);
//							System.out.println(portMap.get(split[0]));
							DatagramPacket reply = new DatagramPacket(msg, msg.length, address, portMap.get(split[0]));
							try {
								socket.send(reply);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}else System.out.println("sender is offline");
				}

			}
	}
	
	
	
	
}

class TCPServer_r extends Thread{
	public ServerSocket server;
	
	public TCPServer_r(ServerSocket server) {
		this.server = server;
	}
	
	public void run() {
		while(true){
            try {
				Socket s = server.accept();
		        String ip = s.getInetAddress().getHostAddress();
		        
	        	//get name
	        	InputStream receive_name = s.getInputStream();
	            byte buf[] = new byte[4096];
	            int length_name = receive_name.read(buf);
	            String total_name = new String(buf, 0, length_name);
	            //downloadfile:content
	            String split_download[] = total_name.split(":");
	            String fileName = split_download[0];
	            String userID = split_download[1];
	            //download file from server
	            if(split_download[0].equals("downloadfile")) {
	            	
	            	File dir = new File("\\server\\"+split_download[1]);
		            BufferedInputStream bin = null;
		            OutputStream out = null;
		            int total = 0;
		            
		            bin = new BufferedInputStream(new FileInputStream("server\\"+split_download[1]));

	                out = s.getOutputStream();

	                buf = new byte[4096];
	                int len=0;
	                while((len=bin.read(buf))!=-1){
	                    out.write(buf, 0, len);
	                    total = len+total;
	                }

	                s.shutdownOutput();
	                
	                System.out.println("server have sent total of "+ total + " bytes to "+split_download[2]);
	                
//		            InputStream in = s.getInputStream();
//	                
//	                byte[] fileName_buf = new String(" have sent total of "+ total + " bytes to user\n").getBytes();
//		            
//		            OutputStream send_back = s.getOutputStream();
//		            
//		            send_back.write(fileName_buf);
//	                
	                
		            out.close();
		            bin.close();
		            s.close();
	            	
	            }else{
	            	
	            	//upload file to server
			        try {
			            //send back ok
			            buf = null;
			            OutputStream send_back_ok = s.getOutputStream();
			            send_back_ok.write( "ok".getBytes() );
			        	
			            BufferedInputStream bin = new BufferedInputStream(s.getInputStream());

			            File dir = new File("server");
			            //D:\\VirtualBox Share\\copy\\ml.jpg
			            if(!dir.exists()){
			                dir.mkdir();
			            }

			            int count=1;
			            File file = new File(dir, fileName);

			            while(file.exists()){
			            	String[] split = fileName.split("\\.");
			                file = new File(dir,split[0]+"("+(count++) +")."+split[1]);
			            }

			            FileOutputStream fout = new FileOutputStream(file);

			            buf = new byte[4096];
			            //byte buf[] = new byte[1024];
			            int total = 0;
			            int len=0;
			            while( (len=bin.read(buf))!=-1){
			                fout.write(buf, 0, len);
			                total = len + total;
			            }

			            OutputStream out = s.getOutputStream();
			            System.out.println("server has received "+total + " bytes from "+userID);
			            out.write(new String("have received "+total+" bytes").getBytes() );

			            fout.close();
			            s.close();

			        } catch (IOException e) {
			            e.printStackTrace();
			        }
	            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
