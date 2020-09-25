package TreeTable;

import javax.swing.table.AbstractTableModel;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

/**
 * Glues the JTable and the JTree together.
 */
public class TreeTableModelGlue extends AbstractTableModel {
    // Reference to the tree
    JTree tree;
    // Reference to the TreeTableModel
    TreeTableModel treeTableModel;

    /**
     * Constructor that will accept the TreeTableModel and the JTree.
     * Here we setup the tree expansion listener to fire table change events that will draw the expanded content
     */
    public TreeTableModelGlue(TreeTableModel treeTableModel, JTree tree) {
        // Store reference to the tree
        this.tree = tree;
        // Store reference to the model
        this.treeTableModel = treeTableModel;

        // Set the expansion listener and get it to fire the table events to update the view
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();
            }

            public void treeCollapsed(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
        });
    }

    /**
     * Returns the column count from the TreeTable
     *
     * @return int - number of columns in the TreeTable
     */
    public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }

    /**
     * Returns the column name for the column number passed in the parameter
     *
     * @param column - the column in the view being queried
     * @return String - name of the column
     */
    public String getColumnName(int column) {
        return treeTableModel.getColumnName(column);
    }

    /**
     * Returns the column class for the column number passed in the parameter
     *
     * @param column - the column in the view being queried
     * @return The type of the column at position column in the view where the first column is column 0
     */
    public Class<?> getColumnClass(int column) {
        return treeTableModel.getColumnClass(column);
    }

    /**
     * Returns the number of rows in the TreeTable
     *
     * @return int - number of rows in the TreeTable
     */
    public int getRowCount() {
        // Return the row count from the JTree, this will be equal to the number of rows in the table.
        return tree.getRowCount();
    }

    /**
     * Find the object at position row. This will query the JTree and find the the TreePath for the row, then return the Object
     *
     * @param row - the row in the view being queried
     * @return Object - Object that contains the tree data at the specified row
     */
    protected Object nodeForRow(int row) {
        // Get the path for the object at that row
        TreePath treePath = tree.getPathForRow(row);
        // Return the Object at that position in the path
        return treePath.getLastPathComponent();
    }

    /**
     * Get the value from the Tree for the object at the row position and column specified.
     *
     * @param row - the row in the view being queried
     * @param column - the column in the view being queried
     * @return Object - Object at the specified location
     */
    public Object getValueAt(int row, int column) {
        return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    /**
     * Returns true if the cell at row and column is editable.
     *
     * @param row - the row in the view being queried
     * @param column - the column in the view being queried
     * @return boolean - true if the cell is editable
     */
    public boolean isCellEditable(int row, int column) {
        return treeTableModel.isCellEditable(nodeForRow(row), column);
    }
}
