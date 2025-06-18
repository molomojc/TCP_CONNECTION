package acsse.csc2b.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * Author JM Molomo
 * @Version P04
 */
public class ClientHandler implements Runnable {
    //constant
      	private final Socket sClient;
	    private BufferedReader lnReader;
	    private PrintWriter lWriter;
	    private DataInputStream ByteRead; //reading different types of data to
	    private DataOutputStream writeByte;  //Writing different types of data
	    
	    
	    public ClientHandler(Socket socketClient) {
	        this.sClient = socketClient;
	        try {
	            lnReader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
	            lWriter = new PrintWriter(socketClient.getOutputStream(), true);
	            ByteRead = new DataInputStream(socketClient.getInputStream());
	            writeByte = new DataOutputStream(socketClient.getOutputStream());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	@Override
	public void run() {
		try {
			String line[] = lnReader.readLine().split(" "); //Store the Command
			if(line[0].equals("LIST")) {
				//call the method to pull the imgList
				System.out.println("a request for list was made..");
				pull();
			}else if(line[0].equals("DOWN")) {
				System.out.println("a request for download was made..");
				download(line[1]);
			}
			else if(line[0].equals("UP")) {
				System.out.println("a request for UPLOADING was made..");
			
                upload(line[1], line[2], line[3]);
             
            }
			else {
                System.err.println("Invalid request from user");
              
            }
			lnReader.close(); //close the BufferReader
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				sClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	 public void download(String ID) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(new File("data/server/ImgList.txt")))) {
	            String line;

	            while ((line = reader.readLine()) != null) {
	                if (line.contains(ID)) {
	                    String tempArray[] = line.split(" ");
	                    String fileName = tempArray[1];

	                    // Sends the file name to the client
	                    lWriter.println(fileName);
                        lWriter.flush();

	                    File imageFile = new File("data/server/" + fileName); // Updates the path
	                    FileInputStream imageFis = new FileInputStream(imageFile);
	                    byte[] buffer = new byte[1024];
	                    int bytesRead;

	                    while ((bytesRead = imageFis.read(buffer)) != -1) {
	                        writeByte.write(buffer, 0, bytesRead);
	                        writeByte.flush();
	                    }
	                    imageFis.close();
	                    break;
	                }
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	 public void upload(String ID, String Name, String Size) {
		    try {
		        // Create a file output stream to write the image to the server directory
		        File outputFile = new File("data/server/" + Name + ".png");
		        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
		        
		        // Append image information to a list file
		       PrintWriter fileWriter = new PrintWriter(new FileOutputStream("data/server/ImgList.txt", true));
		        fileWriter.write("\n" + ID + " " + Name + ".png");
		        fileWriter.flush();
		        fileWriter.close();

		        // Buffer for reading data
		        byte[] byteArray = new byte[1024];
		        int bytesRead;

		        // Read from the input stream and write to the file output stream
		        while ((bytesRead = ByteRead.read(byteArray)) != -1) {
		            bos.write(byteArray, 0, bytesRead);
		        }
		        
		        bos.flush();
		        bos.close(); // Close the output stream once writing is done

		        System.out.println("Upload successful: " + outputFile.getAbsolutePath());

		    } catch (FileNotFoundException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}

    /**
     * Sends a list of files to the client.
     */
    public void pull() {
        try (BufferedReader readFile = new BufferedReader(new InputStreamReader(new FileInputStream(new File("data/server/ImgList.txt"))))) {
            String line;
            while ((line = readFile.readLine()) != null) {
            	lWriter.write(line + "\n");
            	lWriter.flush();
            }
            readFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
