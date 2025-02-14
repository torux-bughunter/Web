package com.github.toruxbughunter.web;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.*;

public class Runner extends JFrame {
    private JLabel lblA;
    private JLabel lblF;
    private JPanel grid;
    private JTextField txtInput;
    private JButton btnGo;
    private JLabel lblKB;
    private JProgressBar pBar;
    private JButton btnBack;
    private static char[][] ws;
    private Wordle w;
    private int tries;
    private Set<String> used;
    private JLabel[][] cells;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        ws = Utility.loadWords();
        SwingUtilities.invokeLater(() -> {
            Runner app = new Runner();
            app.setVisible(true);
        });
    }

    public Runner() {
        super("Wordle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(createMainMenuPanel(), "menu");
        getContentPane().add(mainPanel);
        cardLayout.show(mainPanel, "menu");
    }

    private JPanel createMainMenuPanel() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(20, 20, 20, 20));
        JButton play = new JButton("Play Game");
        JButton test = new JButton("Test Methods");
        JButton instructions = new JButton("Instructions");
        JButton quit = new JButton("Quit");
        play.setAlignmentX(Component.CENTER_ALIGNMENT);
        test.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        quit.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu.add(Box.createVerticalGlue());
        menu.add(play);
        menu.add(Box.createVerticalStrut(15));
        menu.add(test);
        menu.add(Box.createVerticalStrut(15));
        menu.add(instructions);
        menu.add(Box.createVerticalStrut(15));
        menu.add(quit);
        menu.add(Box.createVerticalGlue());

        play.addActionListener(e -> {
            mainPanel.add(createPlayPanel(), "play");
            cardLayout.show(mainPanel, "play");
        });
        test.addActionListener(e -> {
            mainPanel.add(createTestPanel(), "test");
            cardLayout.show(mainPanel, "test");
        });
        instructions.addActionListener(e -> {
            mainPanel.add(createInstructionsPanel(), "instructions");
            cardLayout.show(mainPanel, "instructions");
        });
        quit.addActionListener(e -> {
            System.exit(0);
        });
        return menu;
    }

    private JPanel createPlayPanel() {
        Random r = new Random();
        int sel = r.nextInt(ws.length);
        w = new Wordle(ws[sel]);
        tries = 0;
        used = new HashSet<>();
        JPanel playPanel = new JPanel(new BorderLayout(10, 10));
        playPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel top = new JPanel(new GridLayout(2, 1, 5, 5));
        lblA = new JLabel("Attempts left: 6", SwingConstants.CENTER);
        lblF = new JLabel("", SwingConstants.CENTER);
        top.add(lblA);
        top.add(lblF);
        playPanel.add(top, BorderLayout.NORTH);
        grid = new JPanel(new GridLayout(6, 5, 5, 5));
        cells = new JLabel[6][5];
        Border cellBorder = BorderFactory.createLineBorder(Color.BLACK);
        Font cellFont = new Font("SansSerif", Font.BOLD, 18);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                JLabel c = new JLabel(" ", SwingConstants.CENTER);
                c.setPreferredSize(new Dimension(40, 40));
                c.setBorder(cellBorder);
                c.setFont(cellFont);
                cells[i][j] = c;
                grid.add(c);
            }
        }
        playPanel.add(grid, BorderLayout.CENTER);

        JPanel bot = new JPanel();
        bot.setLayout(new BoxLayout(bot, BoxLayout.Y_AXIS));
        JPanel inputRow = new JPanel();
        inputRow.setLayout(new BoxLayout(inputRow, BoxLayout.X_AXIS));
        txtInput = new JTextField(10);
        btnGo = new JButton("Submit");
        inputRow.add(txtInput);
        inputRow.add(Box.createRigidArea(new Dimension(5, 0)));
        inputRow.add(btnGo);
        inputRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        bot.add(inputRow);
        lblKB = new JLabel(formatKeyboard(w.displayKeyboard()), SwingConstants.CENTER);
        lblKB.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblKB.setAlignmentX(Component.CENTER_ALIGNMENT);
        bot.add(lblKB);
        JLabel closeness = new JLabel("Wordle Closeness:", SwingConstants.CENTER);
        closeness.setFont(new Font("SansSerif", Font.BOLD, 16));
        closeness.setAlignmentX(Component.CENTER_ALIGNMENT);
        bot.add(closeness);
        pBar = new JProgressBar(0, 100);
        pBar.setValue(0);
        pBar.setStringPainted(true);
        JPanel progBox = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progBox.add(pBar);
        bot.add(progBox);

        btnBack = new JButton("Back to Menu");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        bot.add(btnBack);

        playPanel.add(bot, BorderLayout.SOUTH);
        btnBack.addActionListener(e -> {
            cardLayout.show(mainPanel, "menu");
        });
        btnGo.addActionListener(e -> {
            handleGuess(sel);
        });
        return playPanel;
    }

    private void handleGuess(int sel) {
        String guess = txtInput.getText().toLowerCase();
        if (guess.length() != ws[sel].length) {
            lblF.setText("Invalid word length! Try again.");
            return;
        }
        if (!findWord(ws, guess.toCharArray())) {
            lblF.setText("Invalid word! Try again.");
            return;
        }
        if (used.contains(guess)) {
            lblF.setText("You have already made this guess! Try again.");
            return;
        }
        used.add(guess);
        List<Pair<String, String>> disp = w.displayWord(guess.toCharArray());
        Color green = Color.GREEN;
        Color yellow = new Color(218, 165, 32);
        Color gray = Color.GRAY;
        for (int j = 0; j < disp.size(); j++) {
            Pair<String, String> pair = disp.get(j);
            cells[tries][j].setText(pair.getKey());
            String color = pair.getValue();
            if ("GREEN".equals(color)) {
                cells[tries][j].setForeground(green);
            } else if ("YELLOW".equals(color)) {
                cells[tries][j].setForeground(yellow);
            } else if ("GRAY".equals(color)) {
                cells[tries][j].setForeground(gray);
            }
        }
        lblKB.setText(formatKeyboard(w.displayKeyboard()));
        double sim = Utility.similarityMetric(guess, new String(ws[sel]));
        pBar.setValue((int) (sim * 100));
        if (Arrays.equals(guess.toCharArray(), ws[sel])) {
            lblF.setText("Congratulations! You guessed the word!");
            txtInput.setEnabled(false);
            btnGo.setEnabled(false);
            return;
        }
        tries++;
        lblA.setText("Attempts left: " + (6 - tries));
        if (tries >= 6) {
            lblF.setText("Game over! The correct word was: " + new String(ws[sel]));
            txtInput.setEnabled(false);
            btnGo.setEnabled(false);
        }
        txtInput.setText("");
    }

    private JPanel createTestPanel() {
        JPanel testPanel = new JPanel(new BorderLayout(10, 10));
        testPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton toStr = new JButton("toString");
        JButton rand = new JButton("randomize");
        JButton sortBtn = new JButton("sort");
        JButton back = new JButton("Back");
        btns.add(toStr);
        btns.add(rand);
        btns.add(sortBtn);
        btns.add(back);
        testPanel.add(btns, BorderLayout.NORTH);
        JTextArea area = new JTextArea();
        area.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(area);
        testPanel.add(scrollPane, BorderLayout.CENTER);
        toStr.addActionListener(e -> {
            area.setText(arrToStr(ws));
        });
        rand.addActionListener(e -> {
            shuffle(ws);
            area.setText("Successfully randomized!");
        });
        sortBtn.addActionListener(e -> {
            sort(ws);
            area.setText("Successfully sorted!");
        });
        back.addActionListener(e -> {
            cardLayout.show(mainPanel, "menu");
        });
        return testPanel;
    }

    private JPanel createInstructionsPanel() {
        JPanel yap_place = new JPanel(new BorderLayout(10, 10));
        yap_place.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextArea yap = new JTextArea();
        yap.setEditable(false);
        yap.setLineWrap(true);
        yap.setWrapStyleWord(true);
        yap.setText("Instructions:\n\n" +
                "1. You have 6 attempts to guess the correct 5-letter word.\n" +
                "2. Each guess must be a valid word from the dictionary.\n" +
                "3. After submitting a guess, each letter will be colored:\n" +
                "   - Green: Correct letter in the correct position.\n" +
                "   - Yellow: Correct letter but in the wrong position.\n" +
                "   - Gray: Letter is not in the word.\n" +
                "4. Use these clues to refine your next guess.\n" +
                "5. There is a similarity metric to tell you how close you are. \n" +
                "6. Good luck and have fun!");
        JScrollPane scrollPane = new JScrollPane(yap);
        yap_place.add(scrollPane, BorderLayout.CENTER);
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "menu");
        });
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.add(backButton);
        yap_place.add(backPanel, BorderLayout.SOUTH);
        return yap_place;
    }

    private String arrToStr(char[][] arr) {
        StringBuilder sb = new StringBuilder();
        for (char[] cs : arr) {
            sb.append(new String(cs)).append("\n");
        }
        return sb.toString();
    }

    private boolean findWord(char[][] arr, char[] g) {
        for (char[] word : arr) {
            if (Arrays.equals(word, g)) {
                return true;
            }
        }
        return false;
    }

    private void shuffle(char[][] arr) {
        List<char[]> list = Arrays.asList(arr);
        Collections.shuffle(list);
    }

    private void sort(char[][] arr) {
        Arrays.sort(arr, Comparator.comparing(String::new));
    }

    private String formatKeyboard(String kb) {
        return "<html><div style='width:100%; text-align:center;'>" + kb.replace("\n", "<br>") + "</div></html>";
    }
}
