import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class Form extends JFrame{
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JTextPane mainArea;
    private JTextField txtInput;
    private JPanel leftArea;
    private JTextArea listOnline;
    private JButton btnLogout;
    private JButton btnAddFile;
    private JButton btnUpload;

    private Thread thread;
    private Socket socket;
    BufferedReader in;
    PrintWriter out;

    InputStream inputStream;
    OutputStream outputStream;
    private JFileChooser fileChooser;
    private File selectedFile;

    String serverHost = "localhost";
    int serverPort = 12345;

    public Form() {
        btnAddFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(Form.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    txtInput.setText(selectedFile.getName());
                }
            }
        });
        txtInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mess = txtInput.getText();
                String id = thread.getName();

                if(mess.isEmpty()){
                    return;
                }
                try{
                    sendToServer("MESS", id, mess);
                    String myMess = mess +"\n";
                    //mainArea.setText(mainArea.getText() + myMess);

                    txtInput.setText("");
                    StyledDocument doc = mainArea.getStyledDocument();

                    // center align
                    SimpleAttributeSet rightAlignStyle = new SimpleAttributeSet();
                    StyleConstants.setAlignment(rightAlignStyle, StyleConstants.ALIGN_RIGHT);

                    doc.insertString(doc.getLength(), myMess, rightAlignStyle);
                    doc.setParagraphAttributes(doc.getLength() - myMess.length(), myMess.length(), rightAlignStyle, false);



                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    uploadFile();
                } else {
                    JOptionPane.showMessageDialog(null,"Bạn chưa chọn file");
                }
            }
        });
    }

    public void Form(String username){

        this.setContentPane(mainPanel);
        this.setSize(700,500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        mainArea.setEditable(false);
        ConnectToServer();
        thread.setName(username);


    }

    private void ConnectToServer(){
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    socket = new Socket(serverHost, serverPort);
                    System.out.println("Kết nối thành công");

                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();

                    sendToServer("NAME", thread.getName(), "connect");

                    String messageFromServer;
                    while (true) {
                        byte[] buffer = new byte[1024];
                        int bytesRead = inputStream.read(buffer);

                        String title = new String(buffer, 0, 4);
                        messageFromServer = new String(buffer, 0, bytesRead);

                        System.out.println("res from sv: " + messageFromServer);
                        if (messageFromServer == null) {
                            break;
                        }

                        switch (title){
                            case "NAME":
                                String responName = messageFromServer.replace("NAME`", "");
                                String[] listUsers = responName.split("`");
                                listOnline.setText("");
                                for(String user : listUsers){
                                    listOnline.setText(listOnline.getText() + user + "\n");
                                }
                                break;
                            case "MESS":
                                String responMess = messageFromServer.replace("MESS`", "");
                                String[] mess = responMess.split("`");
                                String isYou = thread.getName();
                                if(!mess[0].equals(isYou)){
                                    mainArea.setText(mainArea.getText()+ "[" + mess[0] + "]:" + mess[1] +"\n");
                                }
                                break;
                            case "ALER":
                                String responAler = messageFromServer.replace("ALER`", "");
                                String sysMess = responAler + "\n";

                                // red color
                                StyledDocument doc = mainArea.getStyledDocument();
                                SimpleAttributeSet redStyle = new SimpleAttributeSet();
                                StyleConstants.setForeground(redStyle, java.awt.Color.RED);

                                // center align
                                SimpleAttributeSet rightAlignStyle = new SimpleAttributeSet();
                                StyleConstants.setAlignment(rightAlignStyle, StyleConstants.ALIGN_CENTER);

                                doc.insertString(doc.getLength(), sysMess, redStyle);
                                doc.setParagraphAttributes(doc.getLength() - sysMess.length(), sysMess.length(), rightAlignStyle, false);

                                break;
                            case "FILE":
                                String saveDirectory = "./Share/";
                                File saveDir = new File(saveDirectory);
                                if (!saveDir.exists()) {
                                    saveDir.mkdir();
                                }

                                String responFile = messageFromServer.replace("FILE`", "");

                                String[] info = responFile.split("`");

                                String fileName = info[1];
                                int fileNameLength = fileName.length();

                                String fileContent = info[2];
                                int fileContentLength = fileName.length();


                                File receivedFile = new File(saveDirectory + fileName);
                                FileOutputStream fileOutputStream = new FileOutputStream(receivedFile);

                                fileOutputStream.write(buffer, 5 + fileNameLength, bytesRead);
                                break;
                            default:
                                System.out.println("TITLE không tồn tại");
                        }

                        /*
                        if(title.equals("NAME")){
                            String respon = messageFromServer.replace("NAME`", "");
                            String[] listUsers = respon.split("`");

                            listOnline.setText("");
                            for(String user : listUsers){
                                listOnline.setText(listOnline.getText() + user + "\n");
                            }
                        }

                        if(title.equals("MESS")){
                            String respon = messageFromServer.replace("MESS`", "");
                            String[] mess = respon.split("`");
                            String isYou = thread.getName();

                            if(!mess[0].equals(isYou)){
                                mainArea.setText(mainArea.getText()+ "[" + mess[0] + "]:" + mess[1] +"\n");
                            }
                        }

                        if(title.equals("ALER")){
                            String aler = messageFromServer.replace("MESS`", "");
                            String sysMess = mainArea.getText()+ aler + "\n";

                            StyledDocument doc = mainArea.getStyledDocument();
                            SimpleAttributeSet redStyle = new SimpleAttributeSet();
                            StyleConstants.setForeground(redStyle, java.awt.Color.RED);

                            doc.insertString(doc.getLength(), sysMess, redStyle);
                        }
                        */
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void sendToServer(String tittle, String id, String message) throws IOException{

        String dataToServer = tittle + "`" + id + "`" + message;
        System.out.println("đoạn này sẽ được gửi đến server: " + dataToServer);
        outputStream.write(dataToServer.getBytes());
    }

    private void sendFileToServer(String tittle, String username, String fileName, String message) throws IOException{

        String dataToServer = tittle + "`" + username + "`" + fileName + "`" + message;
        System.out.println("file này sẽ được gửi đến server: " + dataToServer);
        outputStream.write(dataToServer.getBytes());
    }

    private void uploadFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream(selectedFile);
            String fileName = selectedFile.getName();
            byte[] buffer = new byte[4096];
            int bytesRead = fileInputStream.read(buffer);
            String fileContent = new String(buffer,0, bytesRead);
            sendFileToServer("FILE",thread.getName(),fileName,fileContent);

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Upload file error: " + ex.getMessage());
        }
    }
}
