/**
 *   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
 *   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
 *   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
 *   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
 *   de correo seginfo@eglobal.com.mx                                                         *
 **/
package com.eglobal.tools.validation;

import javax.swing.*;
import javax.swing.table.*;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ComparisonWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private DirectorySelector leftDirectorySelector;
	private DirectorySelector rightDirectorySelector;
	private PhotoLoader photoLoader;
	private CsvComparator csvComparator;
	private JTable leftTable;
	private JTable rightTable;
	private JPanel mainPanel;
	private JPanel tablesPanel;
	private JPanel consolidatedPanel;
	private JTable consolidatedTable;

	public ComparisonWindow(String env) {
		setTitle("Comparador de Fotos");
		setBounds(100, 100, 1300, 700);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		mainPanel = new JPanel(new GridBagLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		JPanel topPanel = new JPanel(new GridBagLayout());
		topPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 0, 70));
		GridBagConstraints gbc_topPanel = new GridBagConstraints();
		gbc_topPanel.gridx = 0;
		gbc_topPanel.gridy = 0;
		gbc_topPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_topPanel.anchor = GridBagConstraints.NORTH;
		gbc_topPanel.weightx = 1.0;
		gbc_topPanel.insets = new Insets(0, 0, 10, 0);
		mainPanel.add(topPanel, gbc_topPanel);
		leftDirectorySelector = new DirectorySelector("Base:");
		rightDirectorySelector = new DirectorySelector("A comparar (Referencia):");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 10);
		leftDirectorySelector.addLabel(topPanel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 5, 0, 10);
		leftDirectorySelector.addField(topPanel, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.EAST;
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.anchor = GridBagConstraints.WEST;
		leftDirectorySelector.addButton(topPanel, gbc);
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 0, 0, 180);
		topPanel.add(Box.createHorizontalStrut(10), gbc);
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 10, 0, 10);
		rightDirectorySelector.addLabel(topPanel, gbc);
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 5, 0, 10);
		rightDirectorySelector.addField(topPanel, gbc);
		gbc.gridx = 6;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.EAST;
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 5, 0, 120);
		rightDirectorySelector.addButton(topPanel, gbc);

		leftTable = createTable();
		rightTable = createTable();
		leftTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = leftTable.getSelectedRow();
				if (selectedRow >= 0 && selectedRow < rightTable.getRowCount()) {
					rightTable.setRowSelectionInterval(selectedRow, selectedRow);
				} else {
					rightTable.clearSelection();
				}
			}
		});
		rightTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = rightTable.getSelectedRow();
				if (selectedRow >= 0 && selectedRow < leftTable.getRowCount()) {
					leftTable.setRowSelectionInterval(selectedRow, selectedRow);
				} else {
					leftTable.clearSelection();
				}
			}
		});

		JScrollPane leftScrollPane = new JScrollPane(leftTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane rightScrollPane = new JScrollPane(rightTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		leftScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
			rightScrollPane.getVerticalScrollBar().setValue(e.getValue());
		});
		rightScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
			leftScrollPane.getVerticalScrollBar().setValue(e.getValue());
		});
		tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		tablesPanel.add(leftScrollPane, new GridBagConstraints());
		tablesPanel.add(rightScrollPane, new GridBagConstraints());
		GridBagConstraints gbc_tablesPanel = new GridBagConstraints();
		gbc_tablesPanel.gridx = 0;
		gbc_tablesPanel.gridy = 1;
		gbc_tablesPanel.fill = GridBagConstraints.BOTH;
		gbc_tablesPanel.weightx = 1.0;
		gbc_tablesPanel.weighty = 1.0;
		gbc_tablesPanel.insets = new Insets(0, 20, 10, 20);
		mainPanel.add(tablesPanel, gbc_tablesPanel);
		csvComparator = new CsvComparator(leftTable, rightTable);
		photoLoader = new PhotoLoader(env, mainPanel, csvComparator);
		GridBagConstraints gbc_photoLoader = new GridBagConstraints();
		gbc_photoLoader.gridx = 0;
		gbc_photoLoader.gridy = 2;
		gbc_photoLoader.anchor = GridBagConstraints.SOUTHEAST;
		gbc_photoLoader.insets = new Insets(10, 0, 10, 20);
		mainPanel.add(photoLoader, gbc_photoLoader);
		SweepPanel sweepPanel = new SweepPanel(photoLoader);
		GridBagConstraints gbc_sweepPanel = new GridBagConstraints();
		gbc_sweepPanel.gridx = 0;
		gbc_sweepPanel.gridy = 3;
		gbc_sweepPanel.anchor = GridBagConstraints.SOUTHEAST;
		gbc_sweepPanel.insets = new Insets(0, 20, 20, 20);
		mainPanel.add(sweepPanel, gbc_sweepPanel);
		leftDirectorySelector.addDirectoryChangeListener(photoLoader::setLeftDirectory);
		rightDirectorySelector.addDirectoryChangeListener(photoLoader::setRightDirectory);
		photoLoader.setLoadActionListener(e -> loadCsvFiles());
		consolidatedPanel = new JPanel(new BorderLayout());
		consolidatedPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
	}

	public void repintarConsolidatedTable(JScrollPane consolidatedScrollPane) {
		mainPanel.remove(tablesPanel);
		consolidatedPanel.removeAll();
		consolidatedPanel.add(consolidatedScrollPane, BorderLayout.CENTER);
		GridBagConstraints gbc_consolidatedPanel = new GridBagConstraints();
		gbc_consolidatedPanel.gridx = 0;
		gbc_consolidatedPanel.gridy = 1;
		gbc_consolidatedPanel.fill = GridBagConstraints.BOTH;
		gbc_consolidatedPanel.weightx = 1.0;
		gbc_consolidatedPanel.weighty = 1.0;
		mainPanel.add(consolidatedPanel, gbc_consolidatedPanel);
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	private void adjustColumnWidths(JTable table) {
		final int MIN_WIDTH = 120;
		final int PADDING = table.getFontMetrics(table.getFont()).charWidth('W') * 2;

		TableColumnModel columnModel = table.getColumnModel();
		FontMetrics fontMetrics = table.getFontMetrics(table.getFont());

		for (int col = 0; col < table.getColumnCount(); col++) {
			int maxWidth = MIN_WIDTH;

			TableColumn column = columnModel.getColumn(col);
			String headerText = (String) column.getHeaderValue();
			int headerWidth = fontMetrics.stringWidth(headerText) + PADDING;
			maxWidth = Math.max(maxWidth, headerWidth);

			for (int row = 0; row < table.getRowCount(); row++) {
				Object cellValue = table.getValueAt(row, col);
				if (cellValue != null) {
					int cellWidth = fontMetrics.stringWidth(cellValue.toString()) + PADDING;
					maxWidth = Math.max(maxWidth, cellWidth);
				}
			}

			column.setPreferredWidth(maxWidth);
		}

		int totalColumnWidth = 0;
		for (int i = 0; i < table.getColumnCount(); i++) {
			totalColumnWidth += columnModel.getColumn(i).getPreferredWidth();
		}

		table.setPreferredScrollableViewportSize(
				new Dimension(totalColumnWidth, table.getRowHeight() * table.getRowCount()));

		table.revalidate();
		table.repaint();
	}

	private JTable createTable() {
		JTable table = new JTable() {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				if (rowIndex == -1 || colIndex == -1)
					return null;

				Object value = getValueAt(rowIndex, colIndex);
				if (value != null) {
					String text = value.toString();
					TableCellRenderer renderer = getCellRenderer(rowIndex, colIndex);
					Component cell = prepareRenderer(renderer, rowIndex, colIndex);
					if (cell.getPreferredSize().width > getColumnModel().getColumn(colIndex).getWidth()) {
						return text;
					}
				}
				return null;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFont(new Font("Roboto", Font.PLAIN, 14));
		table.setRowHeight(25);

		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(JLabel.CENTER);
		cellRenderer.setForeground(Color.DARK_GRAY);
		table.setDefaultRenderer(Object.class, cellRenderer);
		return table;
	}

	private void loadCsvFiles() {
		String selectedPhotoName = photoLoader.getSelectedPhoto();
		String leftDir = leftDirectorySelector.getCanonicalDirectoryPathSelected();
		String rightDir = rightDirectorySelector.getCanonicalDirectoryPathSelected();
		if ("Seleccionar foto".equals(selectedPhotoName) || leftDir == null || rightDir == null) {
			JOptionPane.showMessageDialog(this, "Por favor seleccione todos los campos requeridos.", "Advertencia",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (consolidatedPanel.isShowing()) {
			mainPanel.remove(consolidatedPanel);
			GridBagConstraints gbc_tablesPanel = new GridBagConstraints();
			gbc_tablesPanel.gridx = 0;
			gbc_tablesPanel.gridy = 1;
			gbc_tablesPanel.fill = GridBagConstraints.BOTH;
			gbc_tablesPanel.weightx = 1.0;
			gbc_tablesPanel.weighty = 1.0;
			gbc_tablesPanel.insets = new Insets(0, 20, 10, 20);
			mainPanel.add(tablesPanel, gbc_tablesPanel);
			revalidate();
			repaint();
		}
		try {
			BarridoGeneralCsv barrido = new BarridoGeneralCsv(Paths.get(leftDir), Paths.get(rightDir));
			List<Object[]> rows = barrido.compare();
			List<Object[]> filteredRows = rows.stream().filter(row -> row[1].toString().contains(selectedPhotoName))
					.collect(Collectors.toList());
			String[] columnNames = {"#", "Archivo", "Total Base", "Total A Comparar", "Diferencias", "Alertas"};
			DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex == 5) {
						// Columna de alertas
						return Alert.class;
					}
					return Object.class;
				}
			};
			filteredRows.forEach(model::addRow);
			if (consolidatedTable == null) {
				consolidatedTable = new JTable(model);
				consolidatedTable.getColumnModel().getColumn(5).setCellRenderer(getAlertColumnRenderer());
				JScrollPane scrollPane = new JScrollPane(consolidatedTable);
				consolidatedPanel.add(scrollPane, BorderLayout.CENTER);
			} else {
				consolidatedTable.setModel(model);
				consolidatedTable.getColumnModel().getColumn(5).setCellRenderer(getAlertColumnRenderer());
			}
		} catch (InvalidPathException e) {
			JOptionPane.showMessageDialog(this, "Ruta no valida: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al cargar los archivos CSV: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		adjustColumnWidths(leftTable);
		adjustColumnWidths(rightTable);
	}

	DefaultTableCellRenderer getAlertColumnRenderer() {
		return new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				if (value instanceof Alert) {
					Alert alert = (Alert) value;
					cell.setText(alert.getText());
					cell.setForeground(new Color(255, 255, 255, 137));
					cell.setBackground(alert.getColor());
					cell.setOpaque(true);
				} else {
					cell.setText("Sin datos");
					cell.setBackground(new Color(173, 216, 230, 223));
					cell.setOpaque(true);
				}
				if (isSelected) {
					cell.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
				} else {
					cell.setBorder(null);
				}
				return cell;
			}
		};
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(() -> {
			ComparisonWindow window = new ComparisonWindow("ATM");
			window.setVisible(true);
		});
	}

	@java.lang.SuppressWarnings("all")
	public DirectorySelector getLeftDirectorySelector() {
		return this.leftDirectorySelector;
	}

	@java.lang.SuppressWarnings("all")
	public DirectorySelector getRightDirectorySelector() {
		return this.rightDirectorySelector;
	}
}
