import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Form extends JFrame{
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JTextPane mainArea;
    private JTextField txtInput;
    private JPanel leftArea;
    private JTextArea listOnline;
    private JButton btnLogout;
    private JButton btnSend;

    private Thread thread;
    private Socket socket;
    BufferedReader in;
    PrintWriter out;

    public Form() {
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mess = txtInput.getText();
                String id = thread.getName();
                if(mess.isEmpty()){
                    return;
                }

                try{
                    sendToServer("MESSAGE", id, mess);
                    mainArea.setText(mainArea.getText()+"[Bạn]: "+mess+"\n");
                    txtInput.setText("");

                }catch(Exception ex){
                    ex.printStackTrace();
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
                    sendToServer("MESSAGE", id, mess);
                    mainArea.setText(mainArea.getText()+"[Bạn]: "+mess+"\n");

                    txtInput.setText("");

                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginForm lg = new LoginForm();
                lg.LoginForm();
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
                    String serverHost = "localhost";
                    int serverPort = 12345;

                    socket = new Socket(serverHost, serverPort);
                    System.out.println("Kết nối thành công");


                    // Luồng đọc từ server
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Luồng ghi tới server
                    out = new PrintWriter(socket.getOutputStream(), true);


                    sendToServer("USERS", thread.getName(), "connect");

                    String messageFromServer;
                    while (true) {
                        messageFromServer = in.readLine();
                        if (messageFromServer == null) {
                            break;
                        }

                        String[] mess = messageFromServer.split("`");
                        if(mess[0].equals("USERS")){
                            String users = messageFromServer.replace("USERS`", "");
                            String[] user = users.split("`");
                            for(String u : user){
                                listOnline.setText( u + "\n");
                            }
                        }

                        if(mess[0].equals("MESSAGE")){
                            String isYou = thread.getName();
                            if(!mess[1].equals(isYou)){
                                mainArea.setText(mainArea.getText()+ "[" + mess[1] + "]:" + mess[2] +"\n");
                            }
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void sendToServer(String tittle, String id, String message) throws IOException{
        out.println(tittle + "`" + id + "`" + message);
        out.flush();
    }
}
