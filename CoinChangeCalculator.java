import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class CoinChangeCalculator extends JFrame {
    private JTextField amountField;
    private JTextField denominationsField;
    private JTextArea outputArea;

    public CoinChangeCalculator() {
        setTitle("Currency Change Calculator");
		setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu detailsMenu = new JMenu("Details");
        JMenuItem detailsItem = new JMenuItem("About");
        detailsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(CoinChangeCalculator.this,
                        "CURRENCY CHANGE CALCULATOR\nPIYUSH BHUYAN\n1602-21-737-014");
            }
        });
        detailsMenu.add(detailsItem);
        menuBar.add(detailsMenu);
        setJMenuBar(menuBar);

        // Create content panel
        JPanel contentPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to content panel
        contentPanel.add(new JLabel("Enter Amount:"));
        amountField = new JTextField();
        contentPanel.add(amountField);

        contentPanel.add(new JLabel("Denominations (comma separated):"));
        denominationsField = new JTextField();
        contentPanel.add(denominationsField);

        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateCoinChange();
            }
        });
        contentPanel.add(calculateButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        contentPanel.add(scrollPane);

        // Set the layout and add content panel to the frame
        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void calculateCoinChange() {
    int amount = Integer.parseInt(amountField.getText());
    String[] denominationStrings = denominationsField.getText().split(",");
    int[] denominations = new int[denominationStrings.length];
    for (int i = 0; i < denominationStrings.length; i++) {
        denominations[i] = Integer.parseInt(denominationStrings[i].trim());
    }

    StringBuilder output = new StringBuilder();
    output.append("Denominations used (Feasible):\n");
    int[] feasibleChange = calculateFeasibleCoinChange(amount, denominations);
    output.append(formatChange(feasibleChange, denominations));

    output.append("\nDenominations used (Optimal):\n");
    int[] optimalChange = calculateOptimalCoinChange(amount);
    output.append(formatChange(optimalChange, new int[]{1, 2, 5, 10, 20, 50, 100, 500, 2000}));

    outputArea.setText(output.toString());
}

private int[] calculateFeasibleCoinChange(int amount, int[] denominations) {
    int[] dp = new int[amount + 1];
    int[] usedCoins = new int[amount + 1];
    Arrays.fill(dp, Integer.MAX_VALUE);
    dp[0] = 0;

    for (int denomination : denominations) {
        for (int i = denomination; i <= amount; i++) {
            if (dp[i - denomination] != Integer.MAX_VALUE && dp[i - denomination] + 1 < dp[i]) {
                dp[i] = dp[i - denomination] + 1;
                usedCoins[i] = denomination;
            }
        }
    }

    // Reconstruct the coins used
    int[] coinsUsed = new int[denominations.length];
    int remainingAmount = amount;
    while (remainingAmount > 0) {
        int denomination = usedCoins[remainingAmount];
        for (int i = 0; i < denominations.length; i++) {
            if (denominations[i] == denomination) {
                coinsUsed[i]++;
                break;
            }
        }
        remainingAmount -= denomination;
    }

    return coinsUsed;
}

private int[] calculateOptimalCoinChange(int amount) {
    int[] denominations = {1, 2, 5, 10, 20, 50, 100, 500, 2000};
    int[] coinsUsed = new int[denominations.length];

    for (int i = denominations.length - 1; i >= 0; i--) {
        coinsUsed[i] = amount / denominations[i];
        amount %= denominations[i];
    }

    return coinsUsed;
}

private String formatChange(int[] coinsUsed, int[] denominations) {
    StringBuilder sb = new StringBuilder();
    int totalCoins = 0;
    for (int i = 0; i < denominations.length; i++) {
        if (coinsUsed[i] > 0) {
            sb.append("Denomination ").append(denominations[i]).append(": ").append(coinsUsed[i]).append("\n");
            totalCoins += coinsUsed[i];
        }
    }
    sb.append("Total number of coins: ").append(totalCoins).append("\n");
    return sb.toString();
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CoinChangeCalculator app = new CoinChangeCalculator();
                app.setVisible(true);
            }
        });
    }
}
