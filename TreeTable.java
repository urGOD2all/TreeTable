package TreeTable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;

/**
 * This class is the TreeTable!
 * To use the TreeTable, first create an appropriate TreeTableModel then pass to TreeTable.
 * The TreeTableModel must return one of the columns type as the TreeTableModel.class so we can detect that it should be expandable and use the listener we set here.
 * If you don't do this then you wont get expandable rows.
 *
 */
public class TreeTable extends JTable {
    private TreeTableCellRenderer tree;
    private TreeTableCellEditor treeEditor;

    private AbstractTreeTableModel treeTableModel;

    /**
     * Contructor for the TreeTable. This takes a TreeTableModel object and configures and glues the JTree and JTable components together.
     *
     * See examples for how it might be done
     *
     * @param treeTableModel - TreeTableModel object
     */
    public TreeTable(AbstractTreeTableModel treeTableModel) {
        // Call the super class constructor without the model (models are not compatible)
        super();

        this.treeTableModel = treeTableModel;

        // Setup the renderer for the TreeTable. This renderer must be returned for the expandable column in the model implementation
        tree = new TreeTableCellRenderer(this, treeTableModel);
        // Set the renderer on the TreeTable. This is used when the expandable column (see comment above) is returned by getCellRenderer(...) which is going to be our renderer!
        setDefaultRenderer(TreeTableModel.class, tree);

        // Create a TreeTableSelectionModel
        TreeTableSelectionModel selectionModel = new TreeTableSelectionModel();
        // Set the SelectionModel on the tree
        tree.setSelectionModel(selectionModel);
        // Set the SelectionModel on the table
        setSelectionModel(selectionModel.getListSelectionModel());

        // Setup the editor for the TreeTable, pass the renderer as the tree and this as the table
        treeEditor = new TreeTableCellEditor(tree, this);
        setDefaultEditor(TreeTableModel.class, treeEditor);

        // Configure the JTree in the TreeTableModel
        treeTableModel.setupJTree(tree);
        // Set the model on the table that was passed as parameter
        super.setModel(treeTableModel);
    }

    /**
     * Returns the TableModel that provides the data displayed by this JTable.
     * 
     * @return the TreeTableModel that provides the data displayed by this TreeTable
     */
    @Override
    public AbstractTreeTableModel getModel() {
        return treeTableModel;
    }

    /**
     * Determines whether or not the root node from the TreeTableModel is visible.
     * This is a bound property.
     *
     * @param rootVisible - true if the root node of the TreeTable is to be displayed
     */
    public void setRootVisible(boolean rootVisible) {
        tree.setRootVisible(rootVisible);
    }

    /**
     * Sets the value of the showsRootHandles property, which specifies whether the node handles should be displayed. The default is true.
     *
     * This is a bound property.
     *
     * @param newValue - true if root handles should be displayed; otherwise, false
     */
    public void setShowsRootHandles(boolean newValue) {
        // Call the JTree version of this to action it.
        tree.setShowsRootHandles(newValue);
    }

    /**
     * Overrides the prepare renderer from JTable to ensure that the row and column
     * passed to the renderer gets looked up in the view instead of passing them raw
     * which would be for the moedel order, This only needs to happen for the call to
     * renderer.getTableCellRendererComponent because it wont do it itself and the call
     * to getValueAt in JTable will do this correctly.
     *
     * @param renderer - the renderer to prepare
     * @param row - the row of the cell to render, where 0 is the first row
     * @param column - the column of the cell to render, where 0 is the first column
     * @return - the Component under the event location
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        // Get the requested value, from JTable.getValueAt (this will convert the indexes from view to model)
        Object value = getValueAt(row, column);

        boolean isSelected = false;
        boolean hasFocus = false;

        // Only indicate the selection and focused cell if not printing
        if (!isPaintingForPrint()) {
            isSelected = isCellSelected(row, column);

            boolean rowIsLead = (selectionModel.getLeadSelectionIndex() == row);
            boolean colIsLead = (columnModel.getSelectionModel().getLeadSelectionIndex() == column);

            hasFocus = (rowIsLead && colIsLead) && isFocusOwner();
        }

        // When calling the renderer, send the converted row and column indexes. This is because it is our responsibility to convert this, not the renderer and if we don't, it will use the wrong indexes
        return renderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, convertRowIndexToModel(row), convertColumnIndexToModel(column));
    }
}
