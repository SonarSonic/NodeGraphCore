package com.marginallyClever.nodeGraphSwing.actions;

import com.marginallyClever.nodeGraphCore.Node;
import com.marginallyClever.nodeGraphSwing.EditAction;
import com.marginallyClever.nodeGraphSwing.NodeGraphEditorPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Forces all of the editor's selected {@link Node}s to {@link Node#update()}, regarless of {@link Node#isDirty()}
 * status.
 * @author Dan Royer
 * @since 2022-02-21
 */
public class ActionForciblyUpdateNodes extends AbstractAction implements EditAction {
    private final NodeGraphEditorPanel editor;

    public ActionForciblyUpdateNodes(String name, NodeGraphEditorPanel editor) {
        super(name);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(Node n : editor.getSelectedNodes()) {
            n.update();
        }
    }

    @Override
    public void updateEnableStatus() {
        setEnabled(!editor.getSelectedNodes().isEmpty());
    }
}
