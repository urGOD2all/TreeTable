package TreeTable;

import javax.swing.table.AbstractTableModel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.util.EventListener;

/**
 * Glues the JTable and the JTree together.
 *
 * In the spirit of the Swing developers, this class is designed to implement certain functions for the TreeTable in the same way that the javax.swing.table.AbstractTableModel class does for JTables.
 *
 * To create a concrete TreeTabelModel as a subclass of AbstractTreeTableModel you need only provide implementations for the following methods:
 * public Object getChild(Object parent, int index)
 * public int getChildCount(Object parent)
 * public int getIndexOfChild(Object parent, Object child)
 * public Object getRoot()
 * public boolean isLeaf(Object node)
 * public int getColumnCount()
 * public Object getValueAt(Object node, int columnIndex)
 * public Class<?> getColumnClass(int column) - Must return TreeTableModel.class for the column ID that identifies the expandable row. See the TODO below!
 *
 * TODO: Can I make an implementation fail if they dont override getColumClass ? ATM you can implement without which will mean this wont work so I would rather it warn you or should I make the implementation here return TreeModel.clas for the first row and Object.class for everything else.... I think the later makes more sense
 */
public abstract class AbstractTreeTableModel extends AbstractTableModel implements TreeTableModel {
    // Set the version ID for serialized objects
    static final long serialVersionUID = 1L;

    // Storage for the listeners registered with this model. Instantiated here because it means users don't need to call this constructor in their implementation (like JTable)
    protected EventListenerList listenerList = new EventListenerList();

    // Reference to the JTree
    private JTree tree;

    /**
     * TODO: describe me
     * TODO: Should this be done in TreeTable ?
     */
    protected void setupJTree(JTree tree) {
        // Store reference to the JTree
        this.tree = tree;

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
     * Returns the name of the specified column.  This method generates default
     * names in a sequence (starting with column 0):  A, B, C, ..., Z, AA, AB,
     * AC, ..., AZ, BA, BB, BC, and so on.  Subclasses may override this method
     * to allow column names to be specified on some other basis.
     * Cribbed from AbstractTableModel
     *
     * @param columnIndex  the column index.
     *
     * @return The name of the column.
     */
    public String getColumnName(int columnIndex) {
        StringBuffer buffer = new StringBuffer();
        while (columnIndex >= 0) {
            buffer.insert(0, (char) ('A' + columnIndex % 26));
            columnIndex = columnIndex / 26 - 1;
        }
        return buffer.toString();
    }

    /**
     * Returns the Class for all Object instances
     * in the specified column.
     *
     * @param columnIndex the column index.
     * 
     * @return Object.class
     */
    // TODO: should I implement this or make it part of the interface ? The user does need to implement to get expandable rows so this doenst make sense doing it here like this!
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
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
    public Object nodeForRow(int row) {
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
        return getValueAt(nodeForRow(row), column);
    }

    /**
     * Returns true if the cell at row and column is editable.
     *
     * @param row - the row in the view being queried
     * @param column - the column in the view being queried
     * @return boolean - true if the cell is editable
     */
    public boolean isCellEditable(int row, int column) {
        return isCellEditable(nodeForRow(row), column);
    }

    /**
     * This implementation returns true for all arguments, subclasses can override the method
     * if necessary but ensure to return true otherwise the tree nodes will not expand!
     *
     * @param node  the node object for this row
     * @param columnIndex  the column index of the cell.
     *
     * @return true
     */
    public boolean isCellEditable(Object node, int columnIndex) {
        return true;
    }

    /**
     * fireTreeStructureChanged (from JTree). Only used internally, if needed to expose
     * publicly, should have the correct name (for TreeTable).
     * 
     * @param source the node where the model has changed
     * @param path the path to the root node
     * @param childIndices the indices of the affected elements
     * @param children the affected elements
     */
    private void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
      TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
      TreeModelListener[] listeners = getTreeModelListeners();

      for (int i = listeners.length - 1; i >= 0; --i) listeners[i].treeStructureChanged(event);
    }

    /**
     * Invoke this is you have changed the columns in the TreeTable
     * calls fireTableStructureChanged() on the JTable component
     */
    public void fireTreeTableStructureChanged() {
        // Should be enough to just reload the table.
        fireTableStructureChanged();
    }

    /**
     * Invoke this method if you've modified the data upon which this model
     * depends. The model will notify all of its listeners that the model has
     * changed. It will fire the events, necessary to update the layout caches and
     * repaint the TreeTable.
     *
     * This method will refresh the information about whole TreeTable from the root object underpinning the model.
     * </p>
     */
    public void reload() {
        int n = getChildCount(getRoot());
        int[] childIdx = new int[n];
        Object[] children = new Object[n];

        for (int i = 0; i < n; i++) {
            childIdx[i] = i;
            children[i] = getChild(getRoot(), i);
        }

        // Reload the JTree
        fireTreeStructureChanged(this, new Object[] { getRoot() }, childIdx, children);
        // Reload the JTable
        fireTableDataChanged();
    }

    /**
     * Invoke this method if you've modified the data upon which this model
     * depends. The model will notify all of its listeners that the model has
     * changed. It will fire the events necessary when adding rows and
     * repaints the TreeTable.
     *
     * This is fine as long as you dont have listeners that want to know where the
     * new data appeared.
     */
    public void nodesWereInserted() {
        // Reload the JTree
        fireTreeStructureChanged(this, new Object[] { getRoot() }, null, null);
        // Reload the JTable
        fireTableDataChanged();
    }

    /**
     * Adds a listener to the tree model. The listener will receive notification
     * of all changes to the tree model.
     *
     * @param listener the listener.
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    /**
     * Removes a listener from the tree model so that it will no longer receive
     * notification of changes to the tree model.
     *
     * @param listener the listener to remove.
     */
    public void removeTreeModelListener(TreeModelListener l) {
      listenerList.remove(TreeModelListener.class, l);
    }

    /**
     * Returns an array containing the listeners that have been added to the
     * tree model.
     *
     * @return Array of TreeModelListener objects.
     *
     */
    public TreeModelListener[] getTreeModelListeners() {
      return (TreeModelListener[]) listenerList.getListeners(TreeModelListener.class);
    }

    /**
     * Returns an array of listeners of the given type that are registered with
     * this model.
     * 
     * @param listenerType  the listener class.
     * 
     * @return An array of listeners (possibly empty).
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
      return listenerList.getListeners(listenerType);
    }

    /**
     * Messaged when the user has altered the value for the item identified by path to newValue. If newValue signifies a truly new value the model should post a treeNodesChanged event.
     * Because this abstract implementation is designed not to be editable it does nothing.
     *
     * @param path - path to the node that the user has altered
     * @param newValue - the new value from the TreeCellEditor
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
    }
}
