package TreeTable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import javax.swing.tree.TreePath;

import java.awt.Component;

import java.util.EventObject;
import java.awt.event.MouseEvent;

import javax.swing.UIManager;
import javax.swing.Icon;
import java.awt.Insets;


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

    // Stores the orientation of the tree
    private boolean leftToRight;
    // Stores the child indentation size
    private Integer childIndent;
    private Integer leftChildIndent;
    private Integer rightChildIndent;

    // Stores the Icon used for expanded rows
    private Icon expandedIcon;

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
        // Set the model on the table that was passed as parameter
        super.setModel(treeTableModel);

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
        // This is used for expanding
        treeEditor = new TreeTableCellEditor(tree, this);
        setDefaultEditor(TreeTableModel.class, treeEditor);

        // Configure the JTree in the TreeTableModel
        treeTableModel.setupJTree(tree);

        // TODO: Implement a property change listener for this
        // Get the orientation of the tree
        leftToRight = tree.getComponentOrientation().isLeftToRight();
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

    /**
     * Override from JTable so that we can instruct the tree to expand the element being edited.
     *
     * @param row - row being edited
     * @param column - column being edited
     * @param e - event to pass into shouldSelectCell; note that as of Java 2 platform v1.2, the call to shouldSelectCell is no longer made
     * @return - false if for any reason the cell cannot be edited, or if the indices are invalid
     */
    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        // Check for mouse event and expand if relevant
        try {
            MouseEvent me = (MouseEvent) e;
            // TODO: Need to expand on this to add drag and drop support
            // If the mouse click is within the expandable control area, expand the row in the view
            if(isLocationInExpandControl(tree.getPathForRow(convertRowIndexToModel(row)), me.getX(), me.getY())) tree.toggleExpandedState(convertRowIndexToModel(row));
        }
        // Catch any exceptions, do nothing
        catch(Exception ex) {
            //System.out.println(ex.getMessage());
        }
        return super.editCellAt(row, column, e);
    }

    // TODO: This is a bit of a dirty hack, see the TODO in nodesWereInserted
    @Override
    public int convertRowIndexToView(int modelRowIndex) {
        if(getRowCount() == 0) return 0;
        return super.convertRowIndexToView(modelRowIndex);
    }

    /**
     * Lifted from BasicTreeUI and adapted
     *
     * Returns {@code true} if {@code mouseX} and {@code mouseY} fall
     * in the area of row that is used to expand/collapse the node and
     * the node at {@code row} does not represent a leaf.
     *
     * @param path a tree path
     * @param mouseX an X coordinate
     * @param mouseY an Y coordinate
     * @return {@code true} if the mouse cursor fall in the area of row that
     *         is used to expand/collapse the node and the node is not a leaf.
     */
    protected boolean isLocationInExpandControl(TreePath path, int mouseX, int mouseY) {
        if(path != null && !treeTableModel.isLeaf(path.getLastPathComponent())) {
            int boxWidth;
            Insets i = tree.getInsets();

            if(getExpandedIcon() != null) boxWidth = getExpandedIcon().getIconWidth();
            else boxWidth = 8;

            int boxLeftX = getRowX(tree.getRowForPath(path), path.getPathCount() - 1);

            if (leftToRight) boxLeftX = boxLeftX + i.left - getRightChildIndent() + 1;
            else boxLeftX = tree.getWidth() - boxLeftX - i.right + getRightChildIndent() - 1;

            boxLeftX = findCenteredX(boxLeftX, boxWidth);

            return (mouseX >= boxLeftX && mouseX < (boxLeftX + boxWidth));
        }
        return false;
    }

    // TODO: Implement support for different icons for each row
    // TODO: Implement all the icons
    /**
     * Set the expanded icon for all rows to newIcon
     *
     * @param newIcon - Icon object to use for all rows
     */
    public void setExpandedIcon(Icon newIcon) {
        expandedIcon = newIcon;
    }

    /**
     * Returns the current expanded row Icon object
     *
     * @return Icon used for expanded rows
     */
    public Icon getExpandedIcon() {
        if(expandedIcon == null) setExpandedIcon((Icon)UIManager.get( "Tree.expandedIcon" ));
        return expandedIcon;
    }

    private int getRowX(int row, int depth) {
        return getChildIndent() * (depth + getDepthOffset());
    }

    /**
     * Updates the childIndent variable with the left and right indentation sizes
     */
    private void updateChildIndent() {
        childIndent = getLeftChildIndent() + getRightChildIndent();
    }

    /**
     * Update the leftChildIndent variable
     */
    private void updateLeftChildIndent() {
        leftChildIndent = (Integer) UIManager.get("Tree.leftChildIndent");
    }

    /**
     * Update the rightChildIndent variable
     */
    private void updateRightChildIndent() {
        rightChildIndent = (Integer) UIManager.get("Tree.rightChildIndent");
    }

    /**
     * Get the child indent size
     *
     * return int - left + right child indentation
     */
    private int getChildIndent() {
        if(childIndent == null) updateChildIndent();
        return childIndent;
    }

    /**
     * TODO: Add a property change listener
     *
     * Return the left child indentation size
     *
     * @return int - left child indent size
     */
    private int getLeftChildIndent() {
        if(leftChildIndent == null) updateLeftChildIndent();
        return leftChildIndent;
    }

    /**
     * TODO: Add a property change listener
     *
     * Return the right Child indent size
     *
     * @return int - right child indent size
     */
    private int getRightChildIndent() {
        if(rightChildIndent == null) updateRightChildIndent();
        return rightChildIndent;
    }

    /**
     * TODO: Turn this into an update and call it from the methods it calls
     * Adapted code from BasicTreeUI
     */
    private int getDepthOffset() {
        if(tree.isRootVisible()) {
            if(tree.getShowsRootHandles())
                //depthOffset = 1;
                return 1;
            else
                //depthOffset = 0;
                return 0;
        }
        else if(!tree.getShowsRootHandles())
            //depthOffset = -1;
            return -1;
        else
            //depthOffset = 0;
            return 0;
    }

    /**
     * Lifted from BasicTreeUI
     */
    private int findCenteredX(int x, int iconWidth) {
        return leftToRight
               ? x - (int)Math.ceil(iconWidth / 2.0)
               : x - (int)Math.floor(iconWidth / 2.0);
    }
}
