package TreeTable;

import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;

import javax.swing.JTree;
import javax.swing.JTable;
import java.awt.Component;
import java.util.EventObject;
import java.awt.AWTEvent;

/**
 * This editor is used when expansion of the expandable rows occur. It doesn't get used for editing the cells.
 *
 */
public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
    // The JTree part of the TreeTable
    private JTree tree;
    // The JTable part of the table
    private JTable table;
    // This stores the value that is getting edited (from the table)
    protected Object value;

    /**
     * Sets the references to the tree and the table. Sets default number of clicks needed to 2
     *
     * @param tree JTree for the TreeTable
     * @param table JTable for the TreeTable
     */
    public TreeTableCellEditor(JTree tree, JTable table) {
        this.tree = tree;
        this.table = table;
    }

    /**
     * Set the value for the editor component. This method is normally
     * overridden to set the value in the way, specific for the text
     * component, check box or combo box.
     *
     * @param value the value to set
     */
    public void setValue(Object value)
    {
        this.value = value;
    }

    /**
     * Return the value for the location in the table that is currently being edited.
     *
     * @return value from the table that is currently being edited 
     */
    @Override
    public Object getCellEditorValue() {
        return value;
    }

    /**
     * Sets an initial value for the editor (from the JTable). This will cause the editor to stopEditing and lose any partially edited value if the editor is editing when this method is called.
     * Returns the component that should be added to the client's Component hierarchy. Once installed in the client's hierarchy this component will then be able to draw and receive user input.
     *
     * @param table - the JTable that is asking the editor to edit; can be null
     * @param value - the value of the cell to be edited
     * @param isSelected - true if the cell is to be rendered with highlighting
     * @param row - the row of the cell being edited
     * @param column - the column of the cell being edited
     * 
     * @return the component for editing (tree)
     *
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Get the value from the table for the cell that is being edited
        this.setValue(table.getValueAt(row, column));
        // Return the tree
        return tree;
    }

    /**
     * Dispatches the event to the JTree so that the row will expand
     * 
     * @param event the event to check
     *
     * @return false - to stop the event propogating to the tree
     */
    @Override
    public boolean isCellEditable(EventObject event)
    {
        // Send the event to the JTree to be actioned
        tree.dispatchEvent((AWTEvent) event);
        // Ensure we never return true, this would send mouse events through to the tree that would always action the collapse/expand of the root node
        return false;
    }
}
