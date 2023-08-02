import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RegisterForm extends JFrame{
    private JPanel mainPanel;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnRegister;
    private JButton btnLogin;

    public RegisterForm() {
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = "./users.txt";
                File file = new File(filePath);

                if(file.exists()){
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file,true)))
                    {
                        String username = txtUsername.getText();
                        char[] passwordChars = txtPassword.getPassword();
                        String password = new String(passwordChars);

                        bw.newLine();
                        bw.write(username + " " + password);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }else{
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
                    {
                        String username = txtUsername.getText();
                        char[] passwordChars = txtPassword.getPassword();
                        String password = new String(passwordChars);

                        bw.write(username + " " + password);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }


                JOptionPane.showMessageDialog(null, "Đăng kí thành công. Vui lòng đăng nhập");
                dispose();
                LoginForm lg = new LoginForm();
                lg.LoginForm();
            }
        });
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginForm lg = new LoginForm();
                lg.LoginForm();
            }
        });
    }

    public void RegisterForm(){
        this.setContentPane(this.mainPanel);

        this.setTitle("Register");
        this.setSize(400,270);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}
