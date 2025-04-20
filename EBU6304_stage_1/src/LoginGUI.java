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
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240)); // 更柔和的背景色

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // 添加空白区域

        // 创建并设置样式
        JLabel titleLabel = new JLabel("欢迎登录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;  // 占两列
        panel.add(titleLabel, gbc);

        // 用户名输入框
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;  // 只占一列
        panel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(200, 30));  // 固定宽度
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // 密码输入框
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(200, 30)); // 固定宽度
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(100, 40)); // 固定按钮宽度
        loginButton.setFocusPainted(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;  // 占一列
        panel.add(loginButton, gbc);

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
