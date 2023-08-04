import javax.swing.*;
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
                if(mess.isEmpty()){
                    return;
                }

                try{
                    send(mess);
                    mainArea.setText(mainArea.getText()+"[Bạn]: "+mess+"\n");
                    mainArea.setCaretPosition(mainArea.getDocument().getLength());

                    txtInput.setText("");

                }catch(Exception ex){
                    ex.printStackTrace();
                }


            }
        });
    }

    public void Form(){

        this.setContentPane(mainPanel);
        this.setSize(700,500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainArea.setEditable(false);

        ConnectToServer();
    }

    private void ConnectToServer(){
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    String serverHost = "localhost";
                    int serverPort = 12345; // Đổi thành cổng mà server đang lắng nghe

                    socket = new Socket(serverHost, serverPort);
                    System.out.println("Kết nối thành công");

                    // Luồng đọc từ server
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Luồng ghi tới server
                    out = new PrintWriter(socket.getOutputStream(), true);

                    String message;
                    while (true) {
                        message = in.readLine();
                        if (message == null) {
                            break;
                        }

                        mainArea.setText(mainArea.getText()+message+"\n");
                        mainArea.setCaretPosition(mainArea.getDocument().getLength());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void send(String message) throws IOException{
        out.write(message);
        out.flush();
    }
}
