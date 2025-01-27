package gov.iti.jets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
            if (line.equals("download")) {
                System.out.println(line);
                // String filePath =
                // getClass().getResource("/Files/"+line.strip()).toExternalForm();
                // System.out.println(filePath);
                // File f = new File(filePath);
                String outFile = in1.readUTF();
                // FileInputStream fin = new FileInputStream(f);
                InputStream fin = getClass().getResourceAsStream("/Files/" + outFile.strip());

                System.out.println(outFile);
                out1.write(fin.readAllBytes());
                fin.close();
            } else {
                String inFile = in1.readUTF();
                byte[] inFileContent = in1.readAllBytes();
                String rootPath = URLDecoder.decode(getClass().getResource("/screens").getFile(),
                        StandardCharsets.UTF_8.toString());
                        // System.out.println(getClass().getResource("/screens"));
                String outputFilePath = rootPath+"../Files/" + inFile;
                try (FileOutputStream ff = new FileOutputStream(outputFilePath)) {

                    ff.write(inFileContent);
                    ff.close();
                } catch (Exception e) {
                    // TODO: handle exception
                }
                // RandomAccessFile ff = new
                // RandomAccessFile(getClass().getResource("/Files".strip()).toString()+inFile
                //, "rw");

            }
            // out1.writeUTF( line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
