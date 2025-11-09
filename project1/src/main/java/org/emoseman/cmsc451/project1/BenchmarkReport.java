package org.emoseman.cmsc451.project1;

import static org.emoseman.cmsc451.project1.util.MathHelper.calculateAverage;
import static org.emoseman.cmsc451.project1.util.MathHelper.calculateCoV;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.ArrayList;
import java.util.List;

/**
 * Evan Moseman
 * CMSC-451
 * Project 1
 * November 11, 2025
 * <p>
 * Displays benchmark CSV data in a sortable Swing table and recomputes the
 * summary statistics for each row.
 */
public class BenchmarkReport
    extends JFrame {

    private static final String[]
        COLUMN_NAMES =
        {"Size", "Avg Count", "Coef Count", "Avg Time", "Coef Time"};

    private final DefaultTableModel model;
    private final JTable table;

    /**
     * Builds the benchmark report window, table, and menu bar.
     */
    public BenchmarkReport(String[] args) {
        super("Benchmark Report");

        // Setup look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ignored) {
        }

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        setJMenuBar(buildMenuBar());

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 400));
        pack();
        setLocationRelativeTo(null);

        if (args.length >= 1) {
            System.out.println("cliFilename = " + args[0]);
            try {
                applyToTable(loadCsvFile(new File(args[0])));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Builds the "File" menu containing open, clear, and quit actions.
     *
     * @return fully configured menu bar instance
     */
    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu file = new JMenu("File");

        JMenuItem open = new JMenuItem(new AbstractAction("Open…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                doOpenFile();
            }
        });

        JMenuItem clear = new JMenuItem(new AbstractAction("Clear Table") {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTable();
            }
        });

        JMenuItem quit = new JMenuItem(new AbstractAction("Quit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        file.add(open);
        file.add(clear);
        file.addSeparator();
        file.add(quit);

        mb.add(file);
        return mb;
    }

    /**
     * Remove all table contents so new data can be loaded cleanly.
     */
    private void clearTable() {
        model.setRowCount(0);
        model.setColumnCount(0);
    }

    /**
     * Show a file chooser, load the selected CSV, and display the parsed data.
     */
    private void doOpenFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open Data File");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        loadFile(chooser.getSelectedFile());
    }

    /**
     * Load a CSV file, populate the table, and surface any errors to the user.
     *
     * @param file file to load; ignored if {@code null}
     */
    private void loadFile(File file) {
        if (file == null) {
            return;
        }
        if (!file.isFile()) {
            JOptionPane.showMessageDialog(this,
                                          "File not found:\n" +
                                          file.getAbsolutePath(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<List<String>> data = loadCsvFile(file);
            applyToTable(data);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                                          "Failed to load file:\n" +
                                          e.getMessage(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Apply parsed CSV rows to the table model.
     *
     * @param rows table rows representing summary statistics for each data size
     */
    private void applyToTable(List<List<String>> rows) {
        clearTable();
        model.setColumnIdentifiers(COLUMN_NAMES);
        for (List<String> row : rows) {
            model.addRow(row.toArray());
        }
        alignColumns();
    }

    /**
     * Set the alignment for each table column (size left, metrics right).
     */
    private void alignColumns() {
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        int columnCount = table.getColumnModel().getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            if (i == 0) {
                table.getColumnModel().getColumn(i).setCellRenderer(left);
            }
            else {
                table.getColumnModel().getColumn(i).setCellRenderer(right);
            }
        }
    }

    /**
     * Parse the CSV file into rows of summary values.
     *
     * @param file CSV file to process
     *
     * @return parsed rows ready for display
     *
     * @throws Exception if the file cannot be read or parsed
     */
    private List<List<String>> loadCsvFile(File file)
        throws Exception {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file,
                                                                   UTF_8))) {
            String s;
            while ((s = br.readLine()) != null) {
                lines.add(s);
            }
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        List<List<String>> rows = new ArrayList<>();
        for (String line : lines) {
            rows.add(processDataRow(line));
        }
        return rows;
    }

    /**
     * Parse individual comma-separated line of text into summary values.
     *
     * @param line CSV line describing size and recorded counts/times
     *
     * @return processed row with averages and coefficients
     */
    private List<String> processDataRow(String line) {
        List<String> cells = new ArrayList<>();

        String[] parts = line.split("\\s*,\\s*");
        if (parts.length <= 1) {
            throw new RuntimeException("Failed to parse data.");
        }

        String size = parts[0];
        List<Long> counts = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String[] values = parts[i].split(":");
            counts.add(Long.valueOf(values[0]));
            times.add(Long.valueOf(values[1]));
        }
        System.out.println("times = " + times.size());
        System.out.println("counts.size() = " + counts.size());
        cells.add(size);

        String countAvg = String.format("%.02f", calculateAverage(counts));
        cells.add(countAvg);

        String
            countCoV =
            String.format("%.02f%%", calculateCoV(counts) * 100.0D);
        cells.add(countCoV);

        String timeAvg = String.format("%.02f", calculateAverage(times));
        cells.add(timeAvg);

        String timeCoV = String.format("%.02f%%", calculateCoV(times) * 100.0D);
        cells.add(timeCoV);

        return cells;
    }

    static String cliFilename;

    /**
     * Launch the Swing application on the Event Dispatch Thread.
     *
     * @param args CLI arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BenchmarkReport(args).setVisible(true));
    }
}
