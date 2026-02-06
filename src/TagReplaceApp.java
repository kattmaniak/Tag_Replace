import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TagReplaceApp {
    JFrame frame;

    JButton openFileButton;
    JButton applyButton;
    JButton copyButton;

    JTextPane results;
    JScrollPane resultsPane;
    JPanel tagsPanel;
    JComboBox<String> scriptsCombo = new JComboBox<>();
    JPanel controlPanel;

    String text = "";
    String originalText = "";
    HashMap<String, JTextField> tags = new HashMap<>();
    JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));

    void openFile(File file) {
        try {
            Scanner scanner = new Scanner(file);
            StringBuilder sb = new StringBuilder();
            tags.clear();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sb.append(line);
                sb.append("\n");
                for (String word : line.split(" ")){
                    if (word.matches("<.+>")) {
                        tags.put(word, new JTextField());
                    }
                }
            }
            originalText = sb.toString();
            text = originalText;
            results.setText(text);
            tagsPanel.removeAll();
            tags.forEach((tag, field) -> {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 0.25;
                gbc.gridx = 0;
                gbc.gridy = tagsPanel.getComponents().length;
                gbc.insets = new Insets(10, 10, 5, 5);
                tagsPanel.add(new JLabel(tag), gbc);
                gbc.weightx = 0.5;
                gbc.gridx = 1;
                gbc.insets = new Insets(10, 5, 5, 10);
                tagsPanel.add(field, gbc);
            });
            copyButton.setEnabled(true);
            tagsPanel.revalidate();
        } catch (FileNotFoundException ex) {
            System.err.println("File not found.");
        }
    }

    public TagReplaceApp() {
        frame = new JFrame("Tag Replace");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        openFileButton = new JButton("Open File");
        openFileButton.setMaximumSize(new Dimension(100, 50));
        openFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        openFileButton.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                openFile(file);
                scriptsCombo.setSelectedIndex(0);
            }
        });

        tagsPanel = new JPanel();
        tagsPanel.setLayout(new GridBagLayout());
        tagsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        applyButton = new JButton("Apply");
        applyButton.setMaximumSize(new Dimension(100, 50));
        applyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        applyButton.addActionListener(e -> {
            text = originalText;
            tags.forEach((tag, field) -> {
                text = text.replaceAll(tag, field.getText());
            });
            results.setText(text);
        });

        copyButton = new JButton("Copy to Clipboard");
        copyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyButton.setEnabled(false);

        copyButton.addActionListener(e -> {
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        });


        File scriptsFolder = new File("./Scripts");
        if (scriptsFolder.exists() && scriptsFolder.isDirectory()) {
            File[] scripts = scriptsFolder.listFiles();
            if (scripts != null && scripts.length != 0) {
                scriptsCombo.addItem("Select a script...");
                for (File script : scripts) {
                    if (script.isFile()) {
                        scriptsCombo.addItem(script.getName());
                    }
                }
            } else {
                scriptsCombo.setEnabled(false);
                scriptsCombo.addItem("No scripts found.");
            }
        } else {
            scriptsCombo.setEnabled(false);
            scriptsCombo.addItem("'Scripts' folder not found.");
        }

        scriptsCombo.addActionListener(e -> {
            if (scriptsCombo.getSelectedIndex() != 0) {
                File selectedScript = new File("./Scripts/"+scriptsCombo.getSelectedItem());
                openFile(selectedScript);
            }
        });

        controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        gbc.weightx = 0.25;
        gbc.insets = new Insets(5,5,5,0);
        controlPanel.add(openFileButton, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.75;
        gbc.anchor = GridBagConstraints.LINE_END;
        controlPanel.add(scriptsCombo);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 2;
        controlPanel.add(applyButton, gbc);
        gbc.gridx = 1;
        controlPanel.add(copyButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 0.8;
        gbc.weightx= 1;
        gbc.fill = GridBagConstraints.BOTH;
        controlPanel.add(tagsPanel, gbc);

        results = new JTextPane();
        results.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        results.setPreferredSize(new Dimension(500, 600));
        results.setEditable(false);

        resultsPane = new JScrollPane(results);
        resultsPane.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        frame.setLayout(new GridLayout(1,2));
        frame.add(controlPanel);
        frame.add(resultsPane);

        frame.setVisible(true);
    }

}
