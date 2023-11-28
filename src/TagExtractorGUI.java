import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
public class TagExtractorGUI extends JFrame {
    private JTextArea resultTA;

    private JScrollPane scrollPane;

    private JButton chooseFileBtn;

    private JButton extractTagsBtn;

    private JButton saveTagsBtn;

    private Map<String, Integer> wordFrequencyMap;
    private Set<String> stopWordsSet;
    String selectedFileName;

    public TagExtractorGUI() {
        resultTA = new JTextArea(20, 50);
        resultTA.setEditable(false);
        scrollPane = new JScrollPane(resultTA);

        chooseFileBtn = new JButton("Choose File");
        chooseFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });

        extractTagsBtn = new JButton("Extract Tags");
        extractTagsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extractTags();
            }
        });

        saveTagsBtn = new JButton("Save Tags");
        saveTagsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTagsToFile();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(chooseFileBtn);
        buttonPanel.add(extractTagsBtn);
        buttonPanel.add(saveTagsBtn);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }
    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedFileName = selectedFile.getAbsolutePath();
            JOptionPane.showMessageDialog(this, "Selected File: " + selectedFileName);
        }
    }

    private void extractTags() {
        if (selectedFileName == null) {
            JOptionPane.showMessageDialog(this, "Please choose a file first.");
            return;
        }

        wordFrequencyMap = new HashMap<>();

        stopWordsSet = readStopWords("src/stopWords.txt");

        try (Scanner scanner = new Scanner(new File(selectedFileName))) {
            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase().replaceAll("[^a-zA-Z]+", "");

                if (!stopWordsSet.contains(word)) {
                    wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        displayTags();
    }
    private Set<String> readStopWords(String stopWordsFileName) {
        stopWordsSet = new TreeSet<>();
        File stopWordsFile = new File(stopWordsFileName);

        if (!stopWordsFile.exists()) {
            JOptionPane.showMessageDialog(this, "Stop words file not found.");
            return stopWordsSet;
        }

        try (Scanner scanner = new Scanner(new File(stopWordsFileName))) {
            while (scanner.hasNext()) {
                stopWordsSet.add(scanner.next().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stopWordsSet;
    }

    private void displayTags() {
        resultTA.setText("Tags extracted from: " + selectedFileName + "\n\n");

        for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
            resultTA.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private void saveTagsToFile() {
        if (wordFrequencyMap == null || wordFrequencyMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tags to save.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();

            try (PrintWriter writer = new PrintWriter(outputFile)) {
                for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
                    writer.println(entry.getKey() + ": " + entry.getValue());
                }
                JOptionPane.showMessageDialog(this, "Tags saved to: " + outputFile.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: File not found.");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred while saving tags.");
            }
        }
    }
}
