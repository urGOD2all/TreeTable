package TreeTable;
 
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.ListSelectionModel;

/**
 * This class overrides parts of the selection model used by the JTree and the JTable to ensure that
 * the full row and row text is selected properly.
 * The row is part of the JTable but the text is part of the JTree which is why this glue is needed.
 *
 */
public class TreeTableSelectionModel extends DefaultTreeSelectionModel {
 
    /**
     * This default constructor will setup a DefaultTreeSelectionModel and set an overriden ListSelectionListener.
     * The ListSelectionListener has its valueChanged overriden so that it is a no-op.
     */
    public TreeTableSelectionModel() {
        // Call the super constructor
        super();

        // Create a new ListSelectionListener object but override the valueChanged method.
        // This ensures that when the rows are selected, the text in the JTree is also selected
        ListSelectionListener lsl = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
            }
        };

        // Add our slightly modified ListSelectionListener for this SelectionModel object
        this.getListSelectionModel().addListSelectionListener(lsl);
    }
 
    /**
     * Return the current ListSelectionObject.
     * This is used by the JTable to get and set the correct SelectionModel
     *
     * @return ListSelectionModel - the current listSelectionModel
     */
    ListSelectionModel getListSelectionModel() {
        // This object comes from DefaultTreeSelectionModel source
        return listSelectionModel;
    }
}
