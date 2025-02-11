package com.marginallyClever.nodeGraphSwing.actions;

import com.marginallyClever.nodeGraphCore.NodeGraph;
import com.marginallyClever.nodeGraphSwing.NodeGraphEditorPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Uses the {@link NodeGraphEditorPanel#printAll(Graphics)} to generate a {@link BufferedImage} and then saves that to
 * a default path.
 * TODO add a file selection dialog?
 * @author Dan Royer
 * @since 2022-02-21
 */
public class ActionPrintGraph extends AbstractAction {
    public static final String SAVE_PATH = "saved.png";

    private final NodeGraphEditorPanel editor;

    public ActionPrintGraph(String name, NodeGraphEditorPanel editor) {
        super(name);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BufferedImage awtImage = new BufferedImage(editor.getWidth(), editor.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = awtImage.getGraphics();
        editor.printAll(g);
/*
        if(popupBar.isVisible()) {
            g.translate(popupPoint.x, popupPoint.y);
            popupBar.printAll(g);
            g.translate(-popupPoint.x, -popupPoint.y);
        }
 */
        // TODO file selection dialog here
        File outputFile = new File(SAVE_PATH);
        String extension = SAVE_PATH.substring(SAVE_PATH.lastIndexOf(".")+1);

        try {
            ImageIO.write(awtImage, extension, outputFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
