import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Random;

public class CylinderDataGen {
    private static final DecimalFormat df3 = new DecimalFormat("0.000"); // Peso
    private static final DecimalFormat df2 = new DecimalFormat("0.00"); // Diámetro y KN
    private static final DecimalFormat df0 = new DecimalFormat("#,###"); // Libras de carga
    private static final Random random = new Random();

    // Constantes de conversión según el usuario y análisis del Excel
    private static final double KN_TO_LB_FACTOR = 224.81;
    private static final double KG_TO_LB_WEIGHT = 2.20462;
    private static final double EXCEL_FIXED_AREA = 30.0; // Factor divisor usado en su Excel para obtener PSI

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Generador de Datos Pro-Cylinder (Fórmulas Excel)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1150, 750);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- PANEL SUPERIOR: TÍTULOS Y CONTROLES ---
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));

        // Títulos
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Generador Inteligente de Datos de Resistencia", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel helpLabel = new JLabel(
                "Sincronizado con fórmulas de Excel (Factor 30.0 e incrementos según % de compactación)",
                JLabel.CENTER);
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        titlePanel.add(titleLabel);
        titlePanel.add(helpLabel);
        headerPanel.add(titlePanel, BorderLayout.NORTH);

        // Controles de Configuración
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Configuración de Proyecto"));

        // Resistencia de Diseño (PSI)
        JPanel pnlPsi = new JPanel();
        pnlPsi.add(new JLabel("Resistencia Diseño (PSI): "));
        JTextField txtPsi = new JTextField("3000", 6);
        txtPsi.setFont(new Font("Arial", Font.BOLD, 14));
        pnlPsi.add(txtPsi);

        // Unidad Peso
        JPanel pnlWeight = new JPanel();
        pnlWeight.add(new JLabel("Und. Peso: "));
        String[] wUnits = { "kg", "lb" };
        JComboBox<String> weightCombo = new JComboBox<>(wUnits);
        pnlWeight.add(weightCombo);

        // Unidad Carga
        JPanel pnlLoad = new JPanel();
        pnlLoad.add(new JLabel("Und. Carga: "));
        String[] lUnits = { "lb", "KN" };
        JComboBox<String> loadCombo = new JComboBox<>(lUnits);
        pnlLoad.add(loadCombo);

        controlsPanel.add(pnlPsi);
        controlsPanel.add(pnlWeight);
        controlsPanel.add(pnlLoad);
        headerPanel.add(controlsPanel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- PANEL CENTRAL: TABLAS (BLOQUE 1 Y 2) ---
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 30, 0));

        // Bloque 1 (Peso y Diámetro)
        String[] colA = { "Peso", "Diámetro (plg)" };
        DefaultTableModel modelA = new DefaultTableModel(colA, 0);
        JTable tableA = new JTable(modelA);
        setupTableStyle(tableA);
        JPanel wrapA = new JPanel(new BorderLayout());
        wrapA.add(new JLabel("BLOQUE 1 (Peso/Diam)", JLabel.CENTER), BorderLayout.NORTH);
        wrapA.add(new JScrollPane(tableA), BorderLayout.CENTER);

        // Bloque 2 (Falla y Carga)
        String[] colB = { "Tipo de falla", "Carga" };
        DefaultTableModel modelB = new DefaultTableModel(colB, 0);
        JTable tableB = new JTable(modelB);
        setupTableStyle(tableB);
        JPanel wrapB = new JPanel(new BorderLayout());
        wrapB.add(new JLabel("BLOQUE 2 (Falla/Carga)", JLabel.CENTER), BorderLayout.NORTH);
        wrapB.add(new JScrollPane(tableB), BorderLayout.CENTER);

        tablesPanel.add(wrapA);
        tablesPanel.add(wrapB);
        mainPanel.add(tablesPanel, BorderLayout.CENTER);

        // --- PANEL INFERIOR: ACCIÓN ---
        JButton btnGenerate = new JButton("GENERAR DATOS SEGÚN PORCENTAJES");
        btnGenerate.setFont(new Font("Arial", Font.BOLD, 18));
        btnGenerate.setBackground(new Color(46, 125, 50));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);
        btnGenerate.setPreferredSize(new Dimension(200, 60));

        btnGenerate.addActionListener(e -> {
            try {
                modelA.setRowCount(0);
                modelB.setRowCount(0);

                double psiDiseno = Double.parseDouble(txtPsi.getText().replace(",", ""));
                boolean weightInLbs = weightCombo.getSelectedIndex() == 1;
                boolean loadInLbs = loadCombo.getSelectedIndex() == 0;

                // Actualizar encabezados
                tableA.getColumnModel().getColumn(0).setHeaderValue(weightInLbs ? "Peso (lb)" : "Peso (kg)");
                tableB.getColumnModel().getColumn(1).setHeaderValue(loadInLbs ? "Carga (lb)" : "Carga (kN)");
                tableA.getTableHeader().repaint();
                tableB.getTableHeader().repaint();

                // Rangos de Porcentaje de Compactación (Solicitados)
                double[][] targetPerc = {
                        { 0.70, 0.75 }, // 1er Cilindro: 70 - 75%
                        { 0.85, 0.90 }, // 2do Cilindro: 85 - 90%
                        { 0.98, 1.04 } // 3er Cilindro: 98 - 104%
                };

                for (int i = 0; i < 3; i++) {
                    // 1. Diámetro base (plg)
                    double diametro = randomInRange(5.90, 6.02);

                    // 2. Cálculo de Carga (lb)
                    // Basado en: Carga = % * PSI_D * Factor_Fijo(30.0)
                    // Este factor 30.0 es el que usa el Excel para obtener la Resistencia.
                    double randomPercent = randomInRange(targetPerc[i][0], targetPerc[i][1]);
                    double cargaLbs = randomPercent * psiDiseno * EXCEL_FIXED_AREA;

                    // 3. Peso (kg base entre 12.5 y 13.20)
                    double pesoKg = randomInRange(12.5, 13.20);

                    // Llenar Bloque 1
                    String pesoFinal = weightInLbs ? df3.format(pesoKg * KG_TO_LB_WEIGHT) : df3.format(pesoKg);
                    modelA.addRow(new Object[] { pesoFinal, df2.format(diametro) });

                    // Llenar Bloque 2
                    String cargaFinal = loadInLbs ? df0.format(cargaLbs) : df2.format(cargaLbs / KN_TO_LB_FACTOR);
                    modelB.addRow(new Object[] { random.nextInt(5) + 1, cargaFinal });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error en los datos: Revisa la Resistencia de Diseño.");
            }
        });

        mainPanel.add(btnGenerate, BorderLayout.SOUTH);

        // Inicializar
        btnGenerate.doClick();

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static void setupTableStyle(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 22));
        table.setRowHeight(50);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private static double randomInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
