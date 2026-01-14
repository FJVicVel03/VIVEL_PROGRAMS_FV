import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.text.DecimalFormat;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Generador de Datos de Densidad y PROCTOR v5
 * Diseño elegante con exportación a PDF/Imagen
 */
public class DensityDataGen {
    private static final DecimalFormat df2 = new DecimalFormat("0.00");
    private static final DecimalFormat df3 = new DecimalFormat("0.000");
    private static final DecimalFormat df1 = new DecimalFormat("0.0");
    private static final DecimalFormat df0 = new DecimalFormat("#,###");
    private static final Random random = new Random();

    // Colores del tema elegante
    private static final Color PRIMARY = new Color(25, 118, 210);
    private static final Color PRIMARY_DARK = new Color(21, 101, 192);
    private static final Color ACCENT = new Color(255, 152, 0);
    private static final Color BG_DARK = new Color(38, 50, 56);
    private static final Color BG_LIGHT = new Color(250, 250, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Custom UI defaults
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("VIVEL - Generador de Datos v5");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 900);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG_LIGHT);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();

        // Tabbed Pane con estilo
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(BG_LIGHT);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        tabbedPane.addTab("  DENSIDADES  ", createDensidadesPanel());
        tabbedPane.addTab("  PROCTOR  ", createProctorPanel());

        frame.setLayout(new BorderLayout());
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_DARK);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("MULTISERVICIOS VIVEL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Sistema de Generación de Datos de Laboratorio");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(255, 255, 255, 200));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    // ==================== DENSIDADES PANEL ====================
    private static JPanel createDensidadesPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Config Card
        JPanel configCard = createCard("Configuración de Densidades");
        configCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1
        gbc.gridy = 0;
        gbc.gridx = 0;
        configCard.add(createLabel("PUS Lab (lb/pie³):"), gbc);
        gbc.gridx = 1;
        JTextField txtPUS = createTextField("102.8", 8);
        configCard.add(txtPUS, gbc);
        gbc.gridx = 2;
        configCard.add(createLabel("Humedad Óptima (%):"), gbc);
        gbc.gridx = 3;
        JTextField txtHumedad = createTextField("12.5", 6);
        configCard.add(txtHumedad, gbc);
        gbc.gridx = 4;
        configCard.add(createLabel("% Compactación:"), gbc);
        gbc.gridx = 5;
        JTextField txtCompMin = createTextField("97", 5);
        configCard.add(txtCompMin, gbc);
        gbc.gridx = 6;
        configCard.add(new JLabel(" a "), gbc);
        gbc.gridx = 7;
        JTextField txtCompMax = createTextField("99.5", 5);
        configCard.add(txtCompMax, gbc);

        // Row 2
        gbc.gridy = 1;
        gbc.gridx = 0;
        configCard.add(createLabel("Densidad Arena (lb/pie³):"), gbc);
        gbc.gridx = 1;
        JTextField txtDensArena = createTextField("79.31", 8);
        configCard.add(txtDensArena, gbc);
        gbc.gridx = 2;
        configCard.add(createLabel("Tara (lb):"), gbc);
        gbc.gridx = 3;
        JTextField txtTara = createTextField("0.40", 6);
        configCard.add(txtTara, gbc);
        gbc.gridx = 4;
        configCard.add(createLabel("Peso Picnómetro 1 (lb):"), gbc);
        gbc.gridx = 5;
        JTextField txtPicno1 = createTextField("10.92", 8);
        configCard.add(txtPicno1, gbc);

        // Row 3
        gbc.gridy = 2;
        gbc.gridx = 0;
        configCard.add(createLabel("Cantidad de Pruebas:"), gbc);
        gbc.gridx = 1;
        JComboBox<Integer> cmbPruebas = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5 });
        cmbPruebas.setSelectedIndex(3);
        cmbPruebas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        configCard.add(cmbPruebas, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 2;
        JButton btnGenerate = createPrimaryButton("GENERAR DATOS", new Color(230, 230, 230), Color.BLACK);
        configCard.add(btnGenerate, gbc);

        mainPanel.add(configCard, BorderLayout.NORTH);

        // Tables Container
        JPanel tablesContainer = new JPanel();
        tablesContainer.setLayout(new BoxLayout(tablesContainer, BoxLayout.X_AXIS));
        tablesContainer.setBackground(BG_LIGHT);
        JScrollPane scrollPane = new JScrollPane(tablesContainer);
        scrollPane.setBorder(createRoundedBorder("Resultados"));
        scrollPane.getViewport().setBackground(BG_LIGHT);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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
                double[] compactaciones = generateUniqueCompactions(numPruebas, compMin, compMax);

                for (int i = 0; i < numPruebas; i++) {
                    JPanel testCard = createTestCard(i + 1, pesoUnitarioSecoLab, humedadOptimaLab,
                            compactaciones[i], densidadArena, tara, picno1Actual);
                    tablesContainer.add(testCard);
                    tablesContainer.add(Box.createHorizontalStrut(15));
                    picno1Actual -= randomInRange(0.03, 0.05);
                }
                tablesContainer.revalidate();
                tablesContainer.repaint();
            } catch (Exception ex) {
                showError(mainPanel, ex.getMessage());
            }
        });

        btnGenerate.doClick();
        return mainPanel;
    }

    private static JPanel createTestCard(int num, double PUSLab, double humOptLab,
            double targetCompaction, double densidadArena,
            double tara, double pesoPicno1) {
        JPanel card = new JPanel(new BorderLayout(5, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setPreferredSize(new Dimension(240, 420));
        card.setMaximumSize(new Dimension(240, 420));

        // Header
        JLabel header = new JLabel("PRUEBA " + num, JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(header, BorderLayout.NORTH);

        // Data
        String[] columns = { "Campo", "Valor" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        double PUScampo = (targetCompaction / 100.0) * PUSLab;
        double humedadCampo = randomInRange(humOptLab - 0.5, humOptLab + 0.5);
        double PUH = PUScampo * (humedadCampo + 100.0) / 100.0;
        double volumenAgujero = randomInRange(0.024, 0.026);
        double PNH = PUH * volumenAgujero;
        double PBH = PNH + tara;
        double deltaEmbudo = randomInRange(0.78, 0.82);
        double pesoPicno2 = pesoPicno1 - deltaEmbudo;
        double pesoArenaAdentro = volumenAgujero * densidadArena;
        double pesoPicno3 = pesoPicno2 - (pesoArenaAdentro + deltaEmbudo);

        model.addRow(new Object[] { "Peso Picnómetro 1", df2.format(pesoPicno1) });
        model.addRow(new Object[] { "Peso Picnómetro 2", df2.format(pesoPicno2) });
        model.addRow(new Object[] { "Peso Picnómetro 3", df2.format(pesoPicno3) });
        model.addRow(new Object[] { "Densidad Arena", df2.format(densidadArena) });
        model.addRow(new Object[] { "Volumen Agujero", df3.format(volumenAgujero) });
        model.addRow(new Object[] { "", "" });
        model.addRow(new Object[] { "Peso Bruto Húm.", df2.format(PBH) });
        model.addRow(new Object[] { "Tara", df2.format(tara) });
        model.addRow(new Object[] { "Humedad Campo %", df1.format(humedadCampo) });

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        card.add(new JScrollPane(table), BorderLayout.CENTER);

        // Footer - Compactación
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(PRIMARY);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel compLabel = new JLabel("COMPACTACIÓN: " + df1.format(targetCompaction) + "%", JLabel.CENTER);
        compLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        compLabel.setForeground(Color.WHITE);
        footer.add(compLabel);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    // ==================== PROCTOR PANEL ====================
    private static ProctorGraphPanel proctorGraph;
    private static DefaultTableModel proctorTableModel;
    private static JTextField txtGdOptimo, txtWOptima, txtPesoMolde, txtVolMolde;

    private static JPanel createProctorPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Config Card
        JPanel configCard = createCard("Parámetros del Ensayo PROCTOR");
        configCard.setLayout(new FlowLayout(FlowLayout.LEFT, 25, 15));

        configCard.add(createLabel("gd óptimo (lb/pie³):"));
        txtGdOptimo = createTextField("102.8", 8);
        txtGdOptimo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        configCard.add(txtGdOptimo);

        configCard.add(createLabel("w óptima (%):"));
        txtWOptima = createTextField("12.5", 6);
        txtWOptima.setFont(new Font("Segoe UI", Font.BOLD, 16));
        configCard.add(txtWOptima);

        configCard.add(createLabel("Peso Molde (g):"));
        txtPesoMolde = createTextField("4275", 7);
        configCard.add(txtPesoMolde);

        configCard.add(createLabel("Vol. Molde (cm³):"));
        txtVolMolde = createTextField("944", 6);
        configCard.add(txtVolMolde);

        JButton btnGenerate = createPrimaryButton("GENERAR CURVA", new Color(230, 230, 230), Color.BLACK);
        configCard.add(btnGenerate);

        JButton btnExportPDF = createPrimaryButton("EXPORTAR PDF", new Color(230, 230, 230), Color.BLACK);
        configCard.add(btnExportPDF);

        mainPanel.add(configCard, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        centerPanel.setBackground(BG_LIGHT);

        // Graph
        proctorGraph = new ProctorGraphPanel();
        JPanel graphCard = createCard("Curva de Compactación");
        graphCard.setLayout(new BorderLayout());
        graphCard.add(proctorGraph, BorderLayout.CENTER);
        centerPanel.add(graphCard);

        // Table
        String[] columns = { "Punto", "Humedad (%)", "Peso S+M (g)", "gd (lb/pie³)" };
        proctorTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(proctorTableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JPanel tableCard = createCard("Datos para Copiar al Excel");
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        centerPanel.add(tableCard);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Actions
        btnGenerate.addActionListener(e -> generateProctorData());
        btnExportPDF.addActionListener(e -> exportProctorToPDF(mainPanel));

        btnGenerate.doClick();
        return mainPanel;
    }

    private static void generateProctorData() {
        try {
            proctorTableModel.setRowCount(0);

            double gdOptimo = Double.parseDouble(txtGdOptimo.getText().replace(",", "."));
            double wOptima = Double.parseDouble(txtWOptima.getText().replace(",", "."));
            double pesoMolde = Double.parseDouble(txtPesoMolde.getText().replace(",", "."));
            double volMolde = Double.parseDouble(txtVolMolde.getText().replace(",", "."));

            double[] humedades = {
                    wOptima - 5.7, wOptima - 2.8, wOptima, wOptima + 2.7, wOptima + 5.5
            };
            double[] factores = { 0.94, 0.98, 1.00, 0.985, 0.96 };
            double[] gdValues = new double[5];
            double[] pesoSM = new double[5];

            for (int i = 0; i < 5; i++) {
                gdValues[i] = gdOptimo * factores[i];
                double gdGcm3 = gdValues[i] / 62.4296;
                double gammaHumedo = gdGcm3 * (1 + humedades[i] / 100.0);
                double pesoSueloHumedo = gammaHumedo * volMolde;
                pesoSM[i] = pesoSueloHumedo + pesoMolde;

                proctorTableModel.addRow(new Object[] {
                        i + 1, df1.format(humedades[i]), df0.format(pesoSM[i]), df1.format(gdValues[i])
                });
            }

            proctorGraph.setData(humedades, gdValues, wOptima, gdOptimo);
            proctorGraph.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    private static void exportProctorToPDF(JPanel parent) {
        try {
            // Create a panel to print
            JPanel printPanel = new JPanel(new BorderLayout(20, 20));
            printPanel.setBackground(Color.WHITE);
            printPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            printPanel.setPreferredSize(new Dimension(800, 600));

            // Title
            JLabel title = new JLabel("ENSAYO DE COMPACTACIÓN PROCTOR", JLabel.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 20));
            printPanel.add(title, BorderLayout.NORTH);

            // Graph copy
            BufferedImage graphImage = new BufferedImage(500, 350, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = graphImage.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, 500, 350);
            proctorGraph.setSize(500, 350);
            proctorGraph.paint(g2);
            g2.dispose();

            JLabel graphLabel = new JLabel(new ImageIcon(graphImage));
            printPanel.add(graphLabel, BorderLayout.CENTER);

            // Info
            String info = String.format("gd óptimo: %s lb/pie³  |  w óptima: %s%%",
                    txtGdOptimo.getText(), txtWOptima.getText());
            JLabel infoLabel = new JLabel(info, JLabel.CENTER);
            infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            printPanel.add(infoLabel, BorderLayout.SOUTH);

            // File chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar como imagen PNG");
            fileChooser.setSelectedFile(new File("PROCTOR_Report.png"));

            if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }

                // Render to image
                printPanel.setSize(printPanel.getPreferredSize());
                printPanel.doLayout();
                BufferedImage image = new BufferedImage(
                        printPanel.getWidth(), printPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, image.getWidth(), image.getHeight());
                printPanel.paint(g);
                g.dispose();

                ImageIO.write(image, "PNG", file);
                JOptionPane.showMessageDialog(parent,
                        "Imagen guardada exitosamente:\n" + file.getAbsolutePath(),
                        "Exportación Completada", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            showError(parent, "Error al exportar: " + ex.getMessage());
        }
    }

    // ==================== PROCTOR GRAPH ====================
    static class ProctorGraphPanel extends JPanel {
        private double[] humedades = {};
        private double[] gdValues = {};
        private double wOptima = 0;
        private double gdOptimo = 0;

        public void setData(double[] h, double[] gd, double wOpt, double gdOpt) {
            this.humedades = h;
            this.gdValues = gd;
            this.wOptima = wOpt;
            this.gdOptimo = gdOpt;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int margin = 70;

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);

            if (humedades.length == 0)
                return;

            double minH = humedades[0] - 2;
            double maxH = humedades[4] + 2;
            double minGd = 90;
            double maxGd = gdOptimo + 5;

            // Axes
            g2.setColor(TEXT_PRIMARY);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(margin, h - margin, w - margin, h - margin);
            g2.drawLine(margin, h - margin, margin, margin);

            // Axis labels
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString("Contenido de Humedad (%)", w / 2 - 80, h - 15);

            AffineTransform original = g2.getTransform();
            g2.rotate(-Math.PI / 2);
            g2.drawString("Peso Unitario Seco (lb/pie³)", -h / 2 - 80, 20);
            g2.setTransform(original);

            // Grid
            g2.setColor(new Color(230, 230, 230));
            g2.setStroke(new BasicStroke(1));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            for (int i = 0; i < 5; i++) {
                int x = margin + (int) ((humedades[i] - minH) / (maxH - minH) * (w - 2 * margin));
                g2.setColor(new Color(230, 230, 230));
                g2.drawLine(x, margin, x, h - margin);
                g2.setColor(TEXT_SECONDARY);
                g2.drawString(df1.format(humedades[i]), x - 12, h - margin + 20);
            }

            for (int i = 0; i <= 5; i++) {
                double gd = minGd + i * (maxGd - minGd) / 5;
                int y = h - margin - (int) ((gd - minGd) / (maxGd - minGd) * (h - 2 * margin));
                g2.setColor(new Color(230, 230, 230));
                g2.drawLine(margin, y, w - margin, y);
                g2.setColor(TEXT_SECONDARY);
                g2.drawString(df1.format(gd), 10, y + 5);
            }

            // Smooth curve using QuadCurve
            g2.setColor(PRIMARY);
            g2.setStroke(new BasicStroke(3));

            int[] xPoints = new int[5];
            int[] yPoints = new int[5];
            for (int i = 0; i < 5; i++) {
                xPoints[i] = margin + (int) ((humedades[i] - minH) / (maxH - minH) * (w - 2 * margin));
                yPoints[i] = h - margin - (int) ((gdValues[i] - minGd) / (maxGd - minGd) * (h - 2 * margin));
            }

            // Draw curve segments
            Path2D path = new Path2D.Double();
            path.moveTo(xPoints[0], yPoints[0]);
            for (int i = 1; i < 5; i++) {
                path.lineTo(xPoints[i], yPoints[i]);
            }
            g2.draw(path);

            // Points
            for (int i = 0; i < 5; i++) {
                if (i == 2)
                    continue; // Skip peak (drawn separately)
                g2.setColor(PRIMARY);
                g2.fillOval(xPoints[i] - 6, yPoints[i] - 6, 12, 12);
                g2.setColor(Color.WHITE);
                g2.fillOval(xPoints[i] - 3, yPoints[i] - 3, 6, 6);
            }

            // Peak point
            g2.setColor(ACCENT);
            g2.fillOval(xPoints[2] - 10, yPoints[2] - 10, 20, 20);
            g2.setColor(Color.WHITE);
            g2.fillOval(xPoints[2] - 5, yPoints[2] - 5, 10, 10);

            // Peak label
            g2.setColor(ACCENT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            String peakText = "ÓPTIMO: gd=" + df1.format(gdOptimo) + ", w=" + df1.format(wOptima) + "%";
            g2.drawString(peakText, xPoints[2] + 15, yPoints[2] - 10);
        }
    }

    // ==================== UI HELPERS ====================
    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private static JTextField createTextField(String text, int columns) {
        JTextField field = new JTextField(text, columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private static JButton createPrimaryButton(String text, Color bgColor) {
        return createPrimaryButton(text, bgColor, Color.WHITE);
    }

    private static JButton createPrimaryButton(String text, Color bgColor, Color textColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(textColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100), 1, true),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)));
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
        card.setBorder(createRoundedBorder(title));
        return card;
    }

    private static Border createRoundedBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(new Color(220, 220, 220), 1, true),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_DARK),
                BorderFactory.createEmptyBorder(10, 15, 15, 15));
    }

    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static double[] generateUniqueCompactions(int count, double min, double max) {
        double[] result = new double[count];
        for (int i = 0; i < count; i++) {
            double newVal;
            boolean unique;
            int attempts = 0;
            do {
                newVal = Math.round(randomInRange(min, max) * 10.0) / 10.0;
                unique = true;
                for (int j = 0; j < i; j++) {
                    if (Math.abs(result[j] - newVal) < 0.3) {
                        unique = false;
                        break;
                    }
                }
                attempts++;
            } while (!unique && attempts < 50);
            result[i] = newVal;
        }
        return result;
    }

    private static double randomInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
