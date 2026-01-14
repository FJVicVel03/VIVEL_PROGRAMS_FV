import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Generador de Datos de Densidad de Suelos v3
 * Con compactaciones únicas y distribuidas
 */
public class DensityDataGen {
    private static final DecimalFormat df2 = new DecimalFormat("0.00");
    private static final DecimalFormat df3 = new DecimalFormat("0.000");
    private static final DecimalFormat df1 = new DecimalFormat("0.0");
    private static final Random random = new Random();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Generador de Densidades v3 (Compactaciones Únicas)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 700);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ===== PANEL SUPERIOR: CONFIGURACIÓN =====
        JPanel configPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuración"));

        // Fila 1: Parámetros PROCTOR
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        row1.add(new JLabel("PUS Lab (lb/pie³):"));
        JTextField txtPUS = new JTextField("102.8", 5);
        row1.add(txtPUS);
        row1.add(new JLabel("Humedad Óptima (%):"));
        JTextField txtHumedad = new JTextField("12.5", 4);
        row1.add(txtHumedad);
        row1.add(new JLabel("% Compactación:"));
        JTextField txtCompMin = new JTextField("97", 3);
        row1.add(txtCompMin);
        row1.add(new JLabel("a"));
        JTextField txtCompMax = new JTextField("99.5", 3);
        row1.add(txtCompMax);

        // Fila 2: Parámetros de Campo
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        row2.add(new JLabel("Densidad Arena (lb/pie³):"));
        JTextField txtDensArena = new JTextField("79.31", 5);
        row2.add(txtDensArena);
        row2.add(new JLabel("Tara (lb):"));
        JTextField txtTara = new JTextField("0.40", 4);
        row2.add(txtTara);
        row2.add(new JLabel("Peso Picnómetro 1 inicial (lb):"));
        JTextField txtPicno1 = new JTextField("10.92", 5);
        row2.add(txtPicno1);

        // Fila 3: Cantidad de pruebas
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        row3.add(new JLabel("Cantidad de Pruebas:"));
        JComboBox<Integer> cmbPruebas = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5 });
        cmbPruebas.setSelectedIndex(3); // Default: 4 pruebas
        row3.add(cmbPruebas);

        JButton btnGenerate = new JButton("GENERAR DATOS");
        btnGenerate.setFont(new Font("Arial", Font.BOLD, 14));
        btnGenerate.setBackground(new Color(0, 121, 107));
        btnGenerate.setForeground(Color.WHITE);
        row3.add(btnGenerate);

        configPanel.add(row1);
        configPanel.add(row2);
        configPanel.add(row3);
        mainPanel.add(configPanel, BorderLayout.NORTH);

        // ===== PANEL CENTRAL: TABLAS DINÁMICAS =====
        JPanel tablesContainer = new JPanel();
        tablesContainer.setLayout(new BoxLayout(tablesContainer, BoxLayout.X_AXIS));
        JScrollPane scrollPane = new JScrollPane(tablesContainer);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ===== ACCIÓN DEL BOTÓN =====
        btnGenerate.addActionListener(e -> {
            try {
                tablesContainer.removeAll();

                double pesoUnitarioSecoLab = Double.parseDouble(txtPUS.getText().replace(",", "."));
                double humedadOptimaLab = Double.parseDouble(txtHumedad.getText().replace(",", "."));
                double compMin = Double.parseDouble(txtCompMin.getText().replace(",", "."));
                double compMax = Double.parseDouble(txtCompMax.getText().replace(",", "."));
                double densidadArena = Double.parseDouble(txtDensArena.getText().replace(",", "."));
                double tara = Double.parseDouble(txtTara.getText().replace(",", "."));
                double picno1Inicial = Double.parseDouble(txtPicno1.getText().replace(",", "."));
                int numPruebas = (Integer) cmbPruebas.getSelectedItem();

                double picno1Actual = picno1Inicial;

                // === GENERAR COMPACTACIONES ÚNICAS (rango completo para cada una) ===
                double[] compactaciones = new double[numPruebas];
                for (int i = 0; i < numPruebas; i++) {
                    double newComp;
                    boolean unique;
                    int attempts = 0;
                    do {
                        // Cada prueba puede tener cualquier valor del rango completo
                        newComp = randomInRange(compMin, compMax);
                        // Redondear a 1 decimal para comparación
                        newComp = Math.round(newComp * 10.0) / 10.0;
                        unique = true;
                        // Verificar que sea único (diferente a las anteriores)
                        for (int j = 0; j < i; j++) {
                            if (Math.abs(compactaciones[j] - newComp) < 0.3) {
                                unique = false;
                                break;
                            }
                        }
                        attempts++;
                    } while (!unique && attempts < 50);
                    compactaciones[i] = newComp;
                }

                for (int i = 0; i < numPruebas; i++) {
                    String[] columns = { "Campo", "Valor" };
                    DefaultTableModel model = new DefaultTableModel(columns, 0);
                    JTable table = new JTable(model);
                    setupTableStyle(table);

                    // Generar datos para esta prueba con compactación única
                    generateTestData(model, pesoUnitarioSecoLab, humedadOptimaLab,
                            compactaciones[i], densidadArena, tara, picno1Actual);

                    // Decrementar peso picnómetro 1 para la siguiente prueba (0.03 a 0.05)
                    picno1Actual -= randomInRange(0.03, 0.05);

                    JPanel wrap = new JPanel(new BorderLayout());
                    wrap.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    JLabel lbl = new JLabel("PRUEBA " + (i + 1), JLabel.CENTER);
                    lbl.setFont(new Font("Arial", Font.BOLD, 16));
                    wrap.add(lbl, BorderLayout.NORTH);
                    wrap.add(new JScrollPane(table), BorderLayout.CENTER);
                    wrap.setPreferredSize(new Dimension(220, 400));

                    tablesContainer.add(wrap);
                }

                tablesContainer.revalidate();
                tablesContainer.repaint();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        // Generar inicial
        btnGenerate.doClick();

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Genera datos basándose en una compactación objetivo específica.
     * Fórmulas según el Excel del usuario.
     */
    private static void generateTestData(DefaultTableModel model, double PUSLab, double humOptLab,
            double targetCompaction, double densidadArena,
            double tara, double pesoPicno1) {
        // === Fórmula Excel: Compactación = (PUS_campo / PUS_lab) * 100 ===
        // Despejando: PUS_campo = (Compactación / 100) * PUS_lab
        double PUScampo = (targetCompaction / 100.0) * PUSLab;

        // === Humedad de campo: ±0.5 de la humedad óptima ===
        double humedadCampo = randomInRange(humOptLab - 0.5, humOptLab + 0.5);

        // === Fórmula Excel: PUS_campo = PUH / (humedad + 100) * 100 ===
        // Despejando: PUH = PUS_campo * (humedad + 100) / 100
        double PUH = PUScampo * (humedadCampo + 100.0) / 100.0;

        // === Volumen objetivo: 0.024 a 0.026 pie³ ===
        double volumenAgujero = randomInRange(0.024, 0.026);

        // === Fórmula Excel: PUH = PNH / volumen ===
        // Despejando: PNH = PUH * volumen
        double PNH = PUH * volumenAgujero;

        // === Fórmula Excel: PNH = PBH - tara ===
        // Despejando: PBH = PNH + tara
        double PBH = PNH + tara;

        // === Picnómetros ===
        // PesoPicno2 = PesoPicno1 - (0.78 a 0.82)
        double deltaEmbudo = randomInRange(0.78, 0.82);
        double pesoPicno2 = pesoPicno1 - deltaEmbudo;

        // === Fórmula Excel: volumen = pesoArenaAdentro / densidadArena ===
        // Despejando: pesoArenaAdentro = volumen * densidadArena
        double pesoArenaAdentro = volumenAgujero * densidadArena;

        // === pesoArenaTotal = pesoArenaAdentro + pesoEmbudo ===
        double pesoEmbudo = deltaEmbudo;
        double pesoArenaTotal = pesoArenaAdentro + pesoEmbudo;

        // === pesoPicno3 = pesoPicno2 - pesoArenaTotal ===
        double pesoPicno3 = pesoPicno2 - pesoArenaTotal;

        // === MOSTRAR DATOS PARA COPIAR ===
        model.addRow(new Object[] { "Peso Picnómetro 1", df2.format(pesoPicno1) });
        model.addRow(new Object[] { "Peso Picnómetro 2", df2.format(pesoPicno2) });
        model.addRow(new Object[] { "Peso Picnómetro 3", df2.format(pesoPicno3) });
        model.addRow(new Object[] { "Densidad Arena", df2.format(densidadArena) });
        model.addRow(new Object[] { "Volumen Agujero", df3.format(volumenAgujero) });
        model.addRow(new Object[] { "───────────", "──────" });
        model.addRow(new Object[] { "Peso Bruto Húm.", df2.format(PBH) });
        model.addRow(new Object[] { "Tara", df2.format(tara) });
        model.addRow(new Object[] { "Humedad Campo %", df1.format(humedadCampo) });
        model.addRow(new Object[] { "═══════════", "══════" });
        model.addRow(new Object[] { "% COMPACTACIÓN", df1.format(targetCompaction) + "%" });
    }

    private static void setupTableStyle(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(24);
        table.setShowGrid(true);
        table.setGridColor(Color.GRAY);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getColumnModel().getColumn(0).setPreferredWidth(130);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
    }

    private static double randomInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
