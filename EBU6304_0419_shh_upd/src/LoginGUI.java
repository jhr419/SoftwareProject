import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI {
    private JFrame frame;
    private JPanel panel;

    public LoginGUI() {
        frame = new JFrame("登录");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);  // 居中显示

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 255, 255));

        // 创建并设置样式
        JLabel titleLabel = new JLabel("欢迎登录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(33, 150, 243));

        // 用户名输入框
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // 密码输入框
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 登录按钮事件
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

        // 将组件添加到面板
        panel.add(Box.createVerticalStrut(30)); // 添加空白区域
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(loginButton);

        // 将面板添加到窗口中
        frame.add(panel);
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
