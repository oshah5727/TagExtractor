import javax.swing.*;
public class TagExtractorRunner {
    public static void main(String[] args) {
        TagExtractorGUI TEFrame = new TagExtractorGUI();
        TEFrame.setSize(600, 400);
        TEFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TEFrame.setVisible(true);
    }
}