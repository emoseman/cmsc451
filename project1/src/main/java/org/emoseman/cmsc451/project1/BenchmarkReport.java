package org.emoseman.cmsc451.project1;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkReport
    extends JFrame {

    private final JTable table;
    private final DefaultTableModel model;

    public BenchmarkReport() {
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
        add(buildToolbar(), BorderLayout.NORTH);
        setJMenuBar(buildMenuBar());

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 400));
        pack();
        setLocationRelativeTo(null);
    }

    private JToolBar buildToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(new AbstractAction("Open…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                doOpenFile();
            }
        });
        tb.add(new AbstractAction("Clear") {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTable();
            }
        });
        return tb;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu file = new JMenu("File");

        JMenuItem open = new JMenuItem(new AbstractAction("Open…") {
            @Override
            public void actionPerformed(ActionEvent e) {
                doOpenFile();
            }
        });
        open.setAccelerator(KeyStroke.getKeyStroke('O',
                                                   Toolkit.getDefaultToolkit()
                                                          .getMenuShortcutKeyMaskEx()));

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
        quit.setAccelerator(KeyStroke.getKeyStroke('Q',
                                                   Toolkit.getDefaultToolkit()
                                                          .getMenuShortcutKeyMaskEx()));

        file.add(open);
        file.add(clear);
        file.addSeparator();
        file.add(quit);

        mb.add(file);
        return mb;
    }

    private void clearTable() {
        model.setRowCount(0);
        model.setColumnCount(0);
    }

    private void doOpenFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open Data File");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        try {
            LoadedData data = loadCsvFile(file);
            applyToTable(data.headers, data.rows);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                                          "Failed to load file:\n" +
                                          ex.getMessage(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyToTable(String[] headers, List<String[]> rows) {
        clearTable();
        for (String h : headers) {
            model.addColumn(h);
        }
        for (String[] r : rows) {
            model.addRow(padOrTrim(r, headers.length));
        }
        applyColumnAlignment(rows);
    }

    private static String[] padOrTrim(String[] row, int len) {
        if (row.length == len) {
            return row;
        }
        String[] out = new String[len];
        for (int i = 0; i < len; i++) {
            out[i] = i < row.length ? row[i] : "";
        }
        return out;
    }

    /**
     * Minimal CSV/TSV reader:
     * - Detects delimiter by first line (comma vs tab).
     * - Treats first row as headers.
     * - Handles simple quoted fields with commas/tabs.
     */
    private static LoadedData loadCsvFile(File file)
        throws Exception {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file,
                                                                   StandardCharsets.UTF_8))) {
            String s;
            while ((s = br.readLine()) != null) {
                lines.add(s);
            }
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        char delimiter = detectDelimiter(lines.get(0));
        String[] headers = parseLine(lines.get(0), delimiter);

        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            rows.add(parseLine(lines.get(i), delimiter));
        }
        return new LoadedData(headers, rows);
    }

    private static char detectDelimiter(String firstLine) {
        int commas = count(firstLine, ',');
        int tabs = count(firstLine, '\t');
        return tabs > commas ? '\t' : ',';
    }

    private static int count(String s, char c) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                n++;
            }
        }
        return n;
    }

    /**
     * CSV parser
     *
     * @param line
     * @param delim
     *
     * @return
     */
    private static String[] parseLine(String line, char delim) {
        List<String> cells = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes &&
                    i + 1 < line.length() &&
                    line.charAt(i + 1) == '"') {
                    // escaped quote
                    cur.append('"');
                    i++;
                }
                else {
                    inQuotes = !inQuotes;
                }
            }
            else if (ch == delim && !inQuotes) {
                cells.add(cur.toString());
                cur.setLength(0);
            }
            else {
                cur.append(ch);
            }
        }
        cells.add(cur.toString());
        return cells.toArray(new String[0]);
    }

    private void applyColumnAlignment(List<String[]> rows) {
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);

        int columnCount = table.getColumnModel().getColumnCount();
        for (int col = 1; col < columnCount; col++) {
            boolean numeric = isNumericColumn(rows, col);
            table.getColumnModel()
                 .getColumn(col)
                 .setCellRenderer(numeric ? right : left);
        }
    }

    private static boolean isNumericColumn(List<String[]> rows,
                                           int columnIndex) {
        for (String[] row : rows) {
            if (columnIndex >= row.length) {
                continue;
            }
            String cell = row[columnIndex].trim();
            if (cell.isEmpty()) {
                continue;
            }
            if (!isNumeric(cell)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNumeric(String value) {
        try {
            Double.parseDouble(value.replaceAll(",", ""));
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private record LoadedData(String[] headers, List<String[]> rows) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BenchmarkReport().setVisible(true));
    }
}
