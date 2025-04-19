import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatUI {
    private JFrame frame;
    private JTextArea userInputArea;
    private JTextArea responseArea;
    private JButton submitButton;
    private ApiQA apiClient;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ChatUI window = new ChatUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ChatUI() {
        apiClient = new ApiQA("sk-cbzpgeqjquxjgusngdklsmrzikmptukukbrvzbjhibsosfyf"); // api-key
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Chat Interface");
        frame.setBounds(100, 100, 600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        // Create response area with scrollable text area for chat
        responseArea = new JTextArea();
        responseArea.setEditable(false);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setBackground(Color.LIGHT_GRAY);
        responseArea.setFont(new Font("微软雅黑", Font.PLAIN, 14)); // 设置支持中文的字体
        frame.getContentPane().add(new JScrollPane(responseArea), BorderLayout.CENTER);

        // Create user input area with scrollable text area
        userInputArea = new JTextArea();
        userInputArea.setLineWrap(true);
        userInputArea.setWrapStyleWord(true);
        userInputArea.setBackground(Color.WHITE);
        userInputArea.setFont(new Font("微软雅黑", Font.PLAIN, 14)); // 设置支持中文的字体
        userInputArea.setPreferredSize(new Dimension(500, 80)); // 输入框的高度设置为适中
        frame.getContentPane().add(userInputArea, BorderLayout.SOUTH);

        // Create submit button
        submitButton = new JButton("Send");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        submitButton.setBackground(new Color(51, 122, 183)); // 按钮颜色
        submitButton.setForeground(Color.WHITE);
        submitButton.setPreferredSize(new Dimension(100, 40)); // 设置按钮大小
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String question = userInputArea.getText().trim();
                if (!question.isEmpty()) {
                    try {
                        // Display question in response area
                        responseArea.append("You: " + question + "\n");

                        // Call API client to get the response and display it character by character
                        new Thread(() -> {
                            try {
                                apiClient.sendRequest(question, responseArea);  // Pass JTextArea to the method
                            } catch (Exception ex) {
                                responseArea.append("Error: " + ex.getMessage() + "\n\n");
                            }
                        }).start();

                        userInputArea.setText(""); // Clear the input area after sending
                    } catch (Exception ex) {
                        responseArea.append("Error: " + ex.getMessage() + "\n\n");
                    }
                } else {
                    responseArea.append("Please enter a question.\n\n");
                }
            }
        });

        // Bottom panel with user input area and button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(userInputArea, BorderLayout.CENTER);
        bottomPanel.add(submitButton, BorderLayout.EAST);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }
}

//待开发：stop button, 对话框绘制，回复格式选择