package TreeTable;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.Graphics;
// Only imported to override repaint for performance
import java.awt.Rectangle;

/**
 * This class is responsible for rendering individual cells in the TreeTable. Unlike the DefaultTableCellRenderer which extends JLabel, we 
 * extend JTree here so that rows are expandable.
 *
 */
public class TreeTableCellRenderer extends JTree implements TableCellRenderer {
    // Store a reference to the TreeTable
    private TreeTable treeTable;
    // Store a reference to the row that was rendered last
    private int lastRenderedRow;
    // Store a reference to the JTree cell renderer
    private DefaultTreeCellRenderer treeRenderer;

    /**
     * Constructor takes a TreeTable and TreeTableModel.
     * Sets up the model and ensures that the JTree and JTable componets have the same row height.
     * This is important to ensure that the TreeTable renders correctly
     *
     */
    public TreeTableCellRenderer(TreeTable treeTable, TreeTableModel treeTableModel) {
        // Set the model of the TreeTable using the supers constructor
        super(treeTableModel);
        // Set the reference to the treeTable that we are being used with
        this.treeTable = treeTable;

        // Set the row height of the JTree to match that of the JTable when initialized
        super.setRowHeight(treeTable.getRowHeight());

        // Get the JTree renderer
        treeRenderer = (DefaultTreeCellRenderer) this.getCellRenderer();
    }

    // TODO: All JTree methods need to be checked (like isRowSelected) to see if they also require having row input converted to the view row
    /**
     * Overrides this method from JTree so that the row is converted to the correct
     * row from the View. This will ensure after sorting that the correct rows are
     * selected.
     *
     * @param int - row to test for selection in the view
     * @return boolean - true if selected in the view, else false.
     */
    @Override
    public boolean isRowSelected(int row) {
        // Only bother trying if the row could exist and the treeTable reference is not null (this can happen if this method is called when invoking the JTree constructor with the model)
        if(row >= 0 && treeTable != null) {
            // Pass the corrected row to the supers method
            return super.isRowSelected(treeTable.convertRowIndexToView(row));
        }
        return false;
    }

    /**
     * When setting the bounds of the TreeTable ensure that the JTree and JTable bounds match.
     * This is needed to ensure that the rows are highlighted properly when selected
     *
     * @param x - the new x-coordinate of this component
     * @param y - the new y-coordinate of this component
     * @param width - the new width of this component
     * @param height - the new height of this component
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(treeTable.getX(), treeTable.getY(), treeTable.getWidth(), treeTable.getHeight());
    }

    /**
     * When setting the row height of the TreeTable ensure that the JTree and the JTable match
     * This is needed to ensure that the rows are highlighted properly when selected
     * The new height is in pixels
     *
     * @param rowHeight - new row height
     */
    @Override
    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
        if(treeTable != null) treeTable.setRowHeight(super.getRowHeight());
    }

    /**
     * Returns the component used for drawing the cell. This method is used to configure the renderer appropriately before drawing.
     *
     * @param table - the JTable that is asking the renderer to draw; can be null
     * @param value - the value of the cell to be rendered
     * @param isSelected - true if the cell is to be rendered with the selection highlighted; otherwise false
     * @param hasFocus - if true, render cell appropriately. For example, put a special border on the cell, if the cell can be edited, render in the color used to indicate editing
     * @param row - the row index of the cell being drawn. When drawing the header, the value of row is -1
     * @param column - the column index of the cell being drawn
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Update the reference to the last row that was drawn. This is used to ensure the indented rows are renderer in the correct location
        lastRenderedRow = row;
        // Check if this item was selected, if it was set the background and foreground of the cell to be selected otherwise only the text is
        // TODO: Check this over here. What I think needs to happen is that when the TreeTable selection colours are changed it should be reflected in both the JTree and JTable. 
        // TODO:    I need to double check that this is whats going on here, this is whisky fueled rush code
        if(isSelected) {
            this.setBackground(table.getSelectionBackground());
            this.setForeground(this.getForeground());
        }
        else {
            this.setBackground(table.getBackground());
            this.setForeground(table.getForeground());
        }

        return this;
    }

    /**
     * Overrides JComponent.setForeground to assign the specified color to the foreground. This works in the same way that it does in a JTable.
     */
    @Override
    public void setForeground(Color c) {
        treeRenderer.setTextNonSelectionColor(c);
        treeRenderer.setTextSelectionColor(c);
    }

    /**
     * Gets the foreground of this component
     */
    @Override
    public Color getForeground() {
        // TODO: I think I want this referencing a common point like the TreeTable (JTable). This is similar to the TODO in getTableCellRendererComponent above
        return treeRenderer.getTextNonSelectionColor();
    }

    /**
     * Translate the original draw position for this cell slightly to the right. This is used to indent child objects of expanded rows
     *
     * @param g - the Graphics context in which to paint
     */
    @Override
    public void paint(Graphics g) {
        g.translate(0, super.getRowHeight() * -lastRenderedRow);
 
        super.paint(g);
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void invalidate() {} 
    @Override
    public boolean isOpaque() { return true;}
    @Override
    public void repaint() {} 
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {} 
    @Override
    public void repaint(Rectangle r) {} 
    @Override
    public void revalidate() {} 
    @Override
    public void validate() {} 
}
