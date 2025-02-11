package com.marginallyClever.nodeGraphSwing.actions;

import com.marginallyClever.nodeGraphCore.Node;
import com.marginallyClever.nodeGraphSwing.EditAction;
import com.marginallyClever.nodeGraphSwing.NodeEditPanel;
import com.marginallyClever.nodeGraphSwing.NodeFactoryPanel;
import com.marginallyClever.nodeGraphSwing.NodeGraphEditorPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Launches the "edit node" dialog.
 * @author Dan Royer
 * @since 2022-02-21
 */
public class ActionEditNodes extends AbstractAction implements EditAction {
    private final NodeGraphEditorPanel editor;

    public ActionEditNodes(String name, NodeGraphEditorPanel editor) {
        super(name);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<Node> nodes = editor.getSelectedNodes();
        if(nodes.isEmpty()) return;
        Node firstNode = nodes.get(0);
        NodeEditPanel.runAsDialog(firstNode,(JFrame)SwingUtilities.getWindowAncestor(editor));
    }

    @Override
    public void updateEnableStatus() {
        setEnabled(!editor.getSelectedNodes().isEmpty());
    }
}
