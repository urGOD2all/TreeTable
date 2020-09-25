package TreeTable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * This class is the TreeTable!
 * To use the TreeTable, first create an appropriate TreeTableModel then pass to TreeTable.
 * The TreeTableModel must return one of the columns type as the TreeTableModel.class so we can detect that it should be expandable and use the listener we set here.
 * If you don't do this then you wont get expandable rows.
 *
 */
public class TreeTable extends JTable {
    private TreeTableCellRenderer treeRenderer;
    private TreeTableCellEditor treeEditor;

    /**
     * Contructor for the TreeTable. This takes a TreeTableModel object and configures and glues the JTree and JTable components together.
     *
     * See examples for how it might be done
     *
     * @param treeTableModel - TreeTableModel object
     */
    public TreeTable(TreeTableModel treeTableModel) {
        // Call the super class constructor without the model (models are not compatible)
        super();

        // Setup the renderer for the TreeTable. This renderer must be returned for the expandable column in the model implementation
        treeRenderer = new TreeTableCellRenderer(this, treeTableModel);
        // Set the renderer on the TreeTable. This is used when the expandable column (see comment above) is returned by getCellRenderer(...) which is going to be our renderer!
        setDefaultRenderer(TreeTableModel.class, treeRenderer);

        // Create a TreeTableSelectionModel
        TreeTableSelectionModel selectionModel = new TreeTableSelectionModel();
        // Set the SelectionModel on the tree
        treeRenderer.setSelectionModel(selectionModel);
        // Set the SelectionModel on the table
        setSelectionModel(selectionModel.getListSelectionModel());

        // Setup the editor for the TreeTable, pass the renderer as the tree and this as the table
        treeEditor = new TreeTableCellEditor(treeRenderer, this);
        setDefaultEditor(TreeTableModel.class, treeEditor);

        TreeTableModelGlue glue = new TreeTableModelGlue(treeTableModel, treeRenderer);
        treeTableModel.setTableModel(glue);
        // Set the model on the table that was passed as parameter
        super.setModel(glue);
    }
}
