package gov.iti.jets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import gov.iti.jets.config.RMIConfig;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class Images {

    String ip;
    int port;
    public Images(){
                RMIConfig p = null;
                try { 
            InputStream inputStream = getClass().getResourceAsStream("/rmi.xml");
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            p = (RMIConfig) unmarshaller.unmarshal(inputStream);
            inputStream.close();
            // System.out.println(p.getIp() +" " + p.getPort());
        } catch (JAXBException ex) {
            ex.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        ip =p.getIp();
        port = 3331;
    }
    public byte[] downloadPP(String fileName) {
        Socket s;
        InputStream sIn;
        OutputStream sOut;
        try {
            s = new Socket(ip, port);
            sIn = s.getInputStream();
            sOut = s.getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        DataOutputStream out = new DataOutputStream(sOut);
        DataInputStream in = new DataInputStream(sIn);

        try {
            out.writeUTF("download/pp");
            out.writeUTF(fileName);

            byte[] download;
            download = in.readAllBytes();
            return download;
        } catch (IOException ex) {
        } finally {

            try {
                s.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        return null;
    }

    public void uploadPP(String fileName, byte[] img) {
        Socket s;
        InputStream sIn;
        OutputStream sOut;
        try {
            s = new Socket(ip, port);
            sIn = s.getInputStream();
            sOut = s.getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        DataOutputStream out = new DataOutputStream(sOut);
        DataInputStream in = new DataInputStream(sIn);

        try {
            out.writeUTF("upload/pp");
            out.writeUTF(fileName);
            out.write(img);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            s.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void uploadAttachment(String fileName, byte[] img) {
        Socket s;
        InputStream sIn;
        OutputStream sOut;
        try {
            s = new Socket(ip, port);
            sIn = s.getInputStream();
            sOut = s.getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        DataOutputStream out = new DataOutputStream(sOut);
        DataInputStream in = new DataInputStream(sIn);

        try {
            out.writeUTF("upload/attachment");
            out.writeUTF(fileName);
            out.write(img);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            s.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    public byte[] downloadAttachment(String fileName){
     Socket s;
            InputStream sIn;
            OutputStream sOut;
            try {
                s = new Socket(ip, port);
                sIn = s.getInputStream();
                sOut = s.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            DataOutputStream out = new DataOutputStream(sOut);
            DataInputStream in = new DataInputStream(sIn);

            try {
                out.writeUTF("download/attachment");
                out.writeUTF(fileName);

                byte[] download ;
                download = in.readAllBytes();
                return download;
            // FileChooser fil_chooser = new FileChooser();
			// File file = fil_chooser.showSaveDialog(stage);
            // if(file !=null){

            //     FileOutputStream fOut = new FileOutputStream(file);
            //     fOut.write(download);
            //     fOut.close();
            // }
            
            } catch (IOException e) {
                
                e.printStackTrace();
            }finally{

                try {
                    s.close();
                } catch (IOException e) {
                   
                    e.printStackTrace();
                }
            }

            return null;
        }
}
