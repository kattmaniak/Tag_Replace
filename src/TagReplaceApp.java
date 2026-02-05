import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class TagReplaceApp {
    JFrame frame;

    JButton openFileButton;
    JButton applyButton;

    JTextPane results;
    JScrollPane resultsPane;
    JPanel tagsPanel;
    JPanel controlPanel;

    String text = "";
    HashSet<String> tags = new HashSet<>();
    JFileChooser fileChooser = new JFileChooser();


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
                                tags.add(word);
                            }
                        }
                    }
                    text = sb.toString();
                    System.out.println(text);
                    System.out.println("Tags: ");
                    System.out.println(tags);
                } catch (FileNotFoundException ex) {
                    System.err.println("File not found.");
                }
            }
        });

        tagsPanel = new JPanel();
        tagsPanel.setLayout(new FlowLayout());
        tagsPanel.setPreferredSize(new Dimension(500, 400));
        tagsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        applyButton = new JButton("Apply");
        applyButton.setMaximumSize(new Dimension(100, 50));
        applyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        applyButton.addActionListener(e -> {
            // todo apply
        });

        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(openFileButton);
        controlPanel.add(tagsPanel);
        controlPanel.add(applyButton);

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
