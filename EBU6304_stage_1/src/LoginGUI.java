import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI {
    private JFrame frame;

    public LoginGUI() {
        frame = new JFrame("登录");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel usernameLabel = new JLabel("用户名:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("密码:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("登录");

        // 将组件添加到窗口
        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(loginButton);

        // 设置登录按钮的行为
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();

            if (authenticate(username, new String(password))) {
                frame.dispose(); // 关闭登录窗口
                new ExpenseTrackerGUI(); // 打开主界面
            } else {
                JOptionPane.showMessageDialog(frame, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    // 简单的身份验证方法
    private boolean authenticate(String username, String password) {
        // 为了演示，使用硬编码的用户名和密码
        return "1".equals(username) && "1".equals(password);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::new);
    }
}
