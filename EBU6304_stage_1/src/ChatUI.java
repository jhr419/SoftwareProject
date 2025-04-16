import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatUI {
    private JFrame frame;
    private JTextArea userInputArea;
    private JTextArea responseArea;
    private JButton submitButton;
    private ApiClient apiClient;

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
        apiClient = new ApiClient("sk-"); //api-key
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Question Answering System");
        frame.setBounds(100, 100, 500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        userInputArea = new JTextArea();
        userInputArea.setLineWrap(true);
        userInputArea.setWrapStyleWord(true);
        frame.getContentPane().add(new JScrollPane(userInputArea), BorderLayout.NORTH);

        responseArea = new JTextArea();
        responseArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(responseArea), BorderLayout.CENTER);

        submitButton = new JButton("Submit Question");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String question = userInputArea.getText();
                if (!question.isEmpty()) {
                    try {
                        String answer = apiClient.sendRequest(question);
                        responseArea.setText("Answer from API: \n" + answer);
                    } catch (Exception ex) {
                        responseArea.setText("Error: " + ex.getMessage());
                    }
                } else {
                    responseArea.setText("Please enter a question.");
                }
            }
        });

        frame.getContentPane().add(submitButton, BorderLayout.SOUTH);
    }
}
