import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class LoginForm extends JFrame{
    private JPanel mainPanel;
    private JPanel rightPanel;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel register;
    private JCheckBox showPassword;
    private JButton btnRegister;


    private TreeMap<String, String> data = new TreeMap<>();

    public LoginForm() {
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                RegisterForm rf = new RegisterForm();
                rf.RegisterForm();

            }
        });
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();

                char[] pass = txtPassword.getPassword();
                String password = new String(pass);

                if(Check(username,password) == true){
                    JOptionPane.showMessageDialog(null, "OK");
                }else{
                    JOptionPane.showMessageDialog(null, "tên đăng nhập hoặc mật khẩu không chính xác");
                }
            }
        });
        showPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char echoChar = showPassword.isSelected() ? '\u0000' : '\u2022';
                txtPassword.setEchoChar(echoChar);
            }
        });
    }

    public void LoginForm(){

        this.setContentPane(this.mainPanel);

        this.setTitle("Login");
        this.setSize(400,300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    public boolean Check(String username, String password){
        String filePath = "./users.txt";

        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            Scanner scanner = new Scanner(fis);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(" ", 2);
                if (tokens.length == 2) {
                    String key = tokens[0];
                    String value = tokens[1];
                    data.put(key, value);
                }
            }

            // đăng nhập thành công
            if(data.containsKey(username) && data.get(username).equals(password)){
                scanner.close();
                fis.close();
                return true;
            }else{

                scanner.close();
                fis.close();
                return false;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


}
