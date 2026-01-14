import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Generador de Datos de Cilindros v2
 * Diseño elegante consistente con DensityDataGen
 */
public class CylinderDataGen {
    private static final DecimalFormat df3 = new DecimalFormat("0.000");
    private static final DecimalFormat df2 = new DecimalFormat("0.00");
    private static final DecimalFormat df0 = new DecimalFormat("#,###");
    private static final Random random = new Random();

    private static final double KN_TO_LB_FACTOR = 224.81;
    private static final double KG_TO_LB_WEIGHT = 2.20462;
    private static final double EXCEL_FIXED_AREA = 30.0;

    // Colores del tema elegante
    private static final Color PRIMARY = new Color(25, 118, 210);
    private static final Color PRIMARY_DARK = new Color(21, 101, 192);
    private static final Color BG_LIGHT = new Color(250, 250, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("VIVEL - Generador de Resistencia de Cilindros v2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG_LIGHT);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BG_LIGHT);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        // Config Card
        JPanel configCard = createCard("Configuración de Proyecto");
        configCard.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 15));

        configCard.add(createLabel("Resistencia Diseño (PSI):"));
        JTextField txtPsi = createTextField("3000", 8);
        txtPsi.setFont(new Font("Segoe UI", Font.BOLD, 16));
        configCard.add(txtPsi);

        configCard.add(createLabel("Unidad Peso:"));
        JComboBox<String> weightCombo = new JComboBox<>(new String[] { "kg", "lb" });
        weightCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        configCard.add(weightCombo);

        configCard.add(createLabel("Unidad Carga:"));
        JComboBox<String> loadCombo = new JComboBox<>(new String[] { "lb", "KN" });
        loadCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        configCard.add(loadCombo);

        JButton btnGenerate = createPrimaryButton("GENERAR DATOS", new Color(230, 230, 230), Color.BLACK);
        configCard.add(btnGenerate);

        contentPanel.add(configCard, BorderLayout.NORTH);

        // Tables Panel
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        tablesPanel.setBackground(BG_LIGHT);

        // Table A - Peso y Diámetro
        String[] colA = { "Peso", "Diámetro (plg)" };
        DefaultTableModel modelA = new DefaultTableModel(colA, 0);
        JTable tableA = new JTable(modelA);
        setupTableStyle(tableA);

        JPanel cardA = createCard("BLOQUE 1: Peso y Diámetro");
        cardA.setLayout(new BorderLayout());
        cardA.add(new JScrollPane(tableA), BorderLayout.CENTER);
        tablesPanel.add(cardA);

        // Table B - Falla y Carga
        String[] colB = { "Tipo de falla", "Carga" };
        DefaultTableModel modelB = new DefaultTableModel(colB, 0);
        JTable tableB = new JTable(modelB);
        setupTableStyle(tableB);

        JPanel cardB = createCard("BLOQUE 2: Tipo de Falla y Carga");
        cardB.setLayout(new BorderLayout());
        cardB.add(new JScrollPane(tableB), BorderLayout.CENTER);
        tablesPanel.add(cardB);

        contentPanel.add(tablesPanel, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        infoPanel.setBackground(BG_LIGHT);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        infoPanel.add(createInfoCard("Cilindro 1", "70% - 75%", new Color(255, 193, 7)));
        infoPanel.add(createInfoCard("Cilindro 2", "85% - 90%", new Color(33, 150, 243)));
        infoPanel.add(createInfoCard("Cilindro 3", "98% - 104%", new Color(76, 175, 80)));

        contentPanel.add(infoPanel, BorderLayout.SOUTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Action
        btnGenerate.addActionListener(e -> {
            try {
                modelA.setRowCount(0);
                modelB.setRowCount(0);

                double psiDiseno = Double.parseDouble(txtPsi.getText().replace(",", ""));
                boolean weightInLbs = weightCombo.getSelectedIndex() == 1;
                boolean loadInLbs = loadCombo.getSelectedIndex() == 0;

                tableA.getColumnModel().getColumn(0).setHeaderValue(weightInLbs ? "Peso (lb)" : "Peso (kg)");
                tableB.getColumnModel().getColumn(1).setHeaderValue(loadInLbs ? "Carga (lb)" : "Carga (kN)");
                tableA.getTableHeader().repaint();
                tableB.getTableHeader().repaint();

                double[][] targetPerc = {
                        { 0.70, 0.75 },
                        { 0.85, 0.90 },
                        { 0.98, 1.04 }
                };

                for (int i = 0; i < 3; i++) {
                    double diametro = randomInRange(5.90, 6.02);
                    double randomPercent = randomInRange(targetPerc[i][0], targetPerc[i][1]);
                    double cargaLbs = randomPercent * psiDiseno * EXCEL_FIXED_AREA;
                    double pesoKg = randomInRange(12.5, 13.20);

                    String pesoFinal = weightInLbs ? df3.format(pesoKg * KG_TO_LB_WEIGHT) : df3.format(pesoKg);
                    modelA.addRow(new Object[] { pesoFinal, df2.format(diametro) });

                    String cargaFinal = loadInLbs ? df0.format(cargaLbs) : df2.format(cargaLbs / KN_TO_LB_FACTOR);
                    modelB.addRow(new Object[] { random.nextInt(5) + 1, cargaFinal });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: Revisa la Resistencia de Diseño.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnGenerate.doClick();

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_DARK);
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("MULTISERVICIOS VIVEL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Generador de Datos de Resistencia de Cilindros");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(255, 255, 255, 200));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    private static JPanel createInfoCard(String title, String percentage, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel percLabel = new JLabel(percentage, JLabel.CENTER);
        percLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        percLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(percLabel, BorderLayout.CENTER);

        return card;
    }

    private static void setupTableStyle(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        table.setRowHeight(55);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private static JTextField createTextField(String text, int columns) {
        JTextField field = new JTextField(text, columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return field;
    }

    private static JButton createPrimaryButton(String text, Color bgColor, Color textColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(textColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100), 1, true),
                BorderFactory.createEmptyBorder(12, 30, 12, 30)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private static JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(new Color(220, 220, 220), 1, true),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_DARK),
                BorderFactory.createEmptyBorder(10, 15, 15, 15)));
        return card;
    }

    private static double randomInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
