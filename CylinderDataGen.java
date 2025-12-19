import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Random;

public class CylinderDataGen {
    private static final DecimalFormat df2 = new DecimalFormat("0.00");
    private static final Random random = new Random();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Generador de Datos de Cilindros (Arial 22 + Bordes)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500); // Aumentado para acomodar fuente más grande
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título e instrucciones
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Generador de Datos para Informe de Resistencias", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel helpLabel = new JLabel("Formato compatible con Excel: Arial 22, Centrado y con Bordes", JLabel.CENTER);
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        headerPanel.add(titleLabel);
        headerPanel.add(helpLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Contenedor de Tablas
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 30, 0));

        // --- Tabla A: Peso y Diámetro ---
        String[] colA = { "Peso (kg)", "Diámetro (cm)" };
        DefaultTableModel modelA = new DefaultTableModel(colA, 0);
        JTable tableA = new JTable(modelA);
        setupTableStyle(tableA);

        JPanel panelA = new JPanel(new BorderLayout());
        JLabel lblA = new JLabel("BLOQUE 1", JLabel.CENTER);
        lblA.setFont(new Font("Arial", Font.BOLD, 16));
        panelA.add(lblA, BorderLayout.NORTH);
        panelA.add(new JScrollPane(tableA), BorderLayout.CENTER);

        // --- Tabla B: Falla y Carga ---
        String[] colB = { "Tipo de falla", "Carga (kN)" };
        DefaultTableModel modelB = new DefaultTableModel(colB, 0);
        JTable tableB = new JTable(modelB);
        setupTableStyle(tableB);

        JPanel panelB = new JPanel(new BorderLayout());
        JLabel lblB = new JLabel("BLOQUE 2", JLabel.CENTER);
        lblB.setFont(new Font("Arial", Font.BOLD, 16));
        panelB.add(lblB, BorderLayout.NORTH);
        panelB.add(new JScrollPane(tableB), BorderLayout.CENTER);

        tablesPanel.add(panelA);
        tablesPanel.add(panelB);
        mainPanel.add(tablesPanel, BorderLayout.CENTER);

        // Botón Generar
        JButton btnGenerate = new JButton("GENERAR DATOS (ARIAL 22)");
        btnGenerate.setFont(new Font("Arial", Font.BOLD, 18));
        btnGenerate.setBackground(new Color(25, 118, 210));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);
        btnGenerate.setPreferredSize(new Dimension(200, 60));

        btnGenerate.addActionListener(e -> {
            modelA.setRowCount(0);
            modelB.setRowCount(0);

            double[] cargasMin = { 259.0, 331.0, 373.0 };
            double[] cargasMax = { 281.0, 353.0, 394.0 };

            for (int i = 0; i < 3; i++) {
                modelA.addRow(new Object[] {
                        df2.format(randomInRange(12.5, 13.20)),
                        df2.format(randomInRange(5.9, 6.02))
                });

                modelB.addRow(new Object[] {
                        random.nextInt(5) + 1,
                        df2.format(randomInRange(cargasMin[i], cargasMax[i]))
                });
            }
        });

        mainPanel.add(btnGenerate, BorderLayout.SOUTH);

        btnGenerate.doClick();

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static void setupTableStyle(JTable table) {
        // Estilo solicitado: Arial 22, Centrado, con Bordes
        Font arial22 = new Font("Arial", Font.PLAIN, 22);
        table.setFont(arial22);
        table.setRowHeight(45); // Altura suficiente para fuente 22
        table.setShowGrid(true); // Mostrar bordes
        table.setGridColor(Color.BLACK);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Estilo del encabezado
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Centrar contenido
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
