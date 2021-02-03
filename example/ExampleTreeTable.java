package TreeTable.examples;

import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JScrollPane;

import TreeTable.TreeTable;

public class ExampleTreeTable {
    public void ExampleMain() {
    }

    public static void main(String[] args) {
        System.out.println("HEllo");

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("The Java Series");
        DefaultMutableTreeNode next1 = new DefaultMutableTreeNode("DERP1");
        DefaultMutableTreeNode next12 = new DefaultMutableTreeNode("DERP12");
        DefaultMutableTreeNode next13 = new DefaultMutableTreeNode("DERP13");
        DefaultMutableTreeNode next2 = new DefaultMutableTreeNode("DERP2");
        DefaultMutableTreeNode next3 = new DefaultMutableTreeNode("DERP3");
        DefaultMutableTreeNode next4 = new DefaultMutableTreeNode("DERP4");
        next1.add(next12);
        next1.add(next13);
        top.add(next1);
        top.add(next2);
        top.add(next3);
        top.add(next4);

        for(int i = 0; i < 100; i++) {
        System.out.println("Adding HERP" + i);
            top.add(new DefaultMutableTreeNode("HERP" + i));
        }


        ExampleTreeTableModel model = new ExampleTreeTableModel(top);
        // TODO: need a TreeTable here that will take model
        TreeTable tt = new TreeTable(model);

        frame.add(new JScrollPane(tt));
        frame.setSize(1024, 768);
        frame.setVisible(true);
    }
}
