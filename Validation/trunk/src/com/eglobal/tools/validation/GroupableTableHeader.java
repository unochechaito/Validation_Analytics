/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class GroupableTableHeader extends JTableHeader {
    private static final long serialVersionUID = 1L;
    protected List<ColumnGroup> columnGroups = new ArrayList<>();

    public GroupableTableHeader(TableColumnModel model) {
        super(model);
    }

    public void addColumnGroup(ColumnGroup g) {
        columnGroups.add(g);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Rectangle clipBounds = g.getClipBounds();
        if (columnGroups.isEmpty()) return;

        int column = 0;
        Dimension size = this.getSize();
        Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
            cellRect.width = getColumnModel().getColumn(i).getWidth();
            if (cellRect.intersects(clipBounds)) {
                for (ColumnGroup cGroup : columnGroups) {
                    Rectangle groupRect = (Rectangle) cellRect.clone();
                    int groupWidth = cGroup.getGroupWidth(getColumnModel(), column);
                    groupRect.width = groupWidth;
                    cGroup.paint(g, groupRect, i);
                    cellRect.width = groupWidth;
                    cellRect.x += groupWidth;
                }
            } else {
                cellRect.x += cellRect.width;
            }
            column++;
        }
    }
}

class ColumnGroup {
    protected String text;
    protected List<TableColumn> columns = new ArrayList<>();
    protected Font font = new Font("Arial", Font.BOLD, 12);

    public ColumnGroup(String text) {
        this.text = text;
    }

    public void add(TableColumn column) {
        columns.add(column);
    }

    public void paint(Graphics g, Rectangle cellRect, int column) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int fontHeight = fm.getHeight();
        int width = cellRect.width;
        int height = cellRect.height / 2;
        int y = cellRect.y + height - fontHeight / 2 + fm.getAscent();
        g.drawRect(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
        g.drawString(text, cellRect.x + (width - fm.stringWidth(text)) / 2, y);
    }

    public int getGroupWidth(TableColumnModel model, int column) {
        int width = 0;
        for (TableColumn c : columns) {
            width += c.getWidth();
        }
        return width;
    }
}


//package com.eglobal.tools.validation;
//
//import javax.swing.table.JTableHeader;
//import javax.swing.table.TableColumn;
//import javax.swing.table.TableColumnModel;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
//
//class GroupableTableHeader extends JTableHeader {
//    private static final long serialVersionUID = 1L;
//    protected List<ColumnGroup> columnGroups = new ArrayList<>();
//
//    public GroupableTableHeader(TableColumnModel model) {
//        super(model);
//    }
//
//    public void addColumnGroup(ColumnGroup g) {
//        columnGroups.add(g);
//    }
//
//    @Override
//    public void paint(Graphics g) {
//        super.paint(g);
//        Rectangle clipBounds = g.getClipBounds();
//        if (columnGroups.isEmpty()) return;
//
//        int column = 0;
//        Dimension size = this.getSize();
//        Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
//        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
//            cellRect.width = getColumnModel().getColumn(i).getWidth();
//            if (cellRect.intersects(clipBounds)) {
//                for (ColumnGroup cGroup : columnGroups) {
//                    Rectangle groupRect = (Rectangle) cellRect.clone();
//                    int groupWidth = cGroup.getGroupWidth(getColumnModel(), column);
//                    groupRect.width = groupWidth;
//                    cGroup.paint(g, groupRect, i);
//                    cellRect.width = groupWidth;
//                    cellRect.x += groupWidth;
//                }
//            } else {
//                cellRect.x += cellRect.width;
//            }
//            column++;
//        }
//    }
//}
//
//class ColumnGroup {
//    protected String text;
//    protected List<TableColumn> columns = new ArrayList<>();
//    protected Font font = new Font("Arial", Font.BOLD, 12);
//
//    public ColumnGroup(String text) {
//        this.text = text;
//    }
//
//    public void add(TableColumn column) {
//        columns.add(column);
//    }
//
//    public void paint(Graphics g, Rectangle cellRect, int column) {
//        g.setFont(font);
//        FontMetrics fm = g.getFontMetrics();
//        int fontHeight = fm.getHeight();
//        int width = cellRect.width;
//        int height = cellRect.height / 2;
//        int y = cellRect.y + height - fontHeight / 2 + fm.getAscent();
//        g.drawRect(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
//        g.drawString(text, cellRect.x + (width - fm.stringWidth(text)) / 2, y);
//    }
//
//    public int getGroupWidth(TableColumnModel model, int column) {
//        int width = 0;
//        for (TableColumn c : columns) {
//            width += c.getWidth();
//        }
//        return width;
//    }
//}
