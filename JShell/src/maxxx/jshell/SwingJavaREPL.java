package maxxx.jshell;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SwingJavaREPL {
    private JTextArea consoleTextArea;
    private Process process;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingJavaREPL::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        SwingJavaREPL javaREPL = new SwingJavaREPL();
        javaREPL.setupGUI();
    }

    private void setupGUI() {
        JFrame frame = new JFrame("Swing JShell REPL :3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);

        consoleTextArea.append("Swing JShell REPL :3 - type some code!\n"); // Welcome message

        JTextField inputField = new JTextField();
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = inputField.getText();
                appendText(">>> " + userInput + "\n");
                executeCommand(userInput);
                inputField.setText("");
            }
        });

        JScrollPane scrollPane = new JScrollPane(consoleTextArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputField, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> consoleTextArea.append(text));
    }

    private void executeCommand(String command) {
        try {
            if (process == null || !process.isAlive()) {
                ProcessBuilder processBuilder = new ProcessBuilder("jshell");
                processBuilder.redirectErrorStream(true);
                process = processBuilder.start();

                BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                Thread outputThread = new Thread(() -> readOutput(processOutput));
                outputThread.start();

                Thread errorThread = new Thread(() -> readOutput(processError));
                errorThread.start();
            }

            process.getOutputStream().write((command + "\n").getBytes());
            process.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readOutput(BufferedReader reader) {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                appendText(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
