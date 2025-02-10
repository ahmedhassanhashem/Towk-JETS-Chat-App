package gov.iti.jets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ThreadedFile implements Runnable {

    private Socket incoming;

    public ThreadedFile(Socket incomingSocket) {
        incoming = incomingSocket;
    }

    @Override
    public void run() {
        try (InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream()) {
            DataInputStream in1 = new DataInputStream(inStream);
            DataOutputStream out1 = new DataOutputStream(outStream);
            // PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"),
            // true /* autoFlush */);

            String line = in1.readUTF();
            String[] spl = line.split("/");

            if (spl[0].equals("download")) {

                String outFile = in1.readUTF();
                InputStream fin = null;

                // Define external storage path
                String userHome = System.getProperty("user.home");
                String externalFilePath = userHome + "/chatApp/";

                if (spl[1].equals("attachment")) {
                    externalFilePath += "Files/" + outFile;
                } else if (spl[1].equals("pp")) {
                    externalFilePath += "ProfileImages/" + outFile;
                }
                                File externalFile = new File(externalFilePath);
                if (externalFile.exists()) {
                    fin = new FileInputStream(externalFile);
                } else {
                    if (spl[1].equals("attachment")) {
                        fin = getClass().getResourceAsStream("/Files/" + outFile.strip());
                    } else if (spl[1].equals("pp")) {
                        fin = getClass().getResourceAsStream("/ProfileImages/" + outFile.strip());
                    }
                }
                if (fin != null) {
                    out1.write(fin.readAllBytes());
                    fin.close();fin.close();
                }
            } else {
                String inFile = in1.readUTF();
                byte[] inFileContent = in1.readAllBytes();

                // Define external storage path
                String userHome = System.getProperty("user.home");
                String outputFilePath = userHome + "/chatApp/";
                if (spl[1].equals("attachment")) {
                    outputFilePath += "Files/" + inFile;
                } else if (spl[1].equals("pp")) {
                    outputFilePath += "ProfileImages/" + inFile;
                }
                new File(outputFilePath).getParentFile().mkdirs();

                try (FileOutputStream ff = new FileOutputStream(outputFilePath)) {
                    ff.write(inFileContent);
                    System.out.println("Saved file: " + outputFilePath);
                } catch (Exception e) {
                    System.err.println("Error saving file: " + outputFilePath);
                    e.printStackTrace();
                }
                }
                // RandomAccessFile ff = new
                // RandomAccessFile(getClass().getResource("/Files".strip()).toString()+inFile
                //, "rw");

            
            
            // out1.writeUTF( line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
