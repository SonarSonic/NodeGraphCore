package com.marginallyClever.nodeGraphSwing.nodes.images;

import com.marginallyClever.nodeGraphCore.PrintWithGraphics;
import com.marginallyClever.nodeGraphCore.Node;
import com.marginallyClever.nodeGraphCore.NodeVariable;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This {@link Node} can print a {@link BufferedImage} using the {@link Graphics} context.
 * @author Dan Royer
 * @since 2022-02-23
 */
public class PrintImage extends Node implements PrintWithGraphics {
    private final NodeVariable<BufferedImage> image = NodeVariable.newInstance("image", BufferedImage.class,null,true,false);
    private final NodeVariable<Number> px = NodeVariable.newInstance("X",Number.class,0,true,false);
    private final NodeVariable<Number> py = NodeVariable.newInstance("Y",Number.class,0,true,false);

    public PrintImage() {
        super("PrintImage");
        addVariable(image);
        addVariable(px);
        addVariable(py);
    }

    public PrintImage(BufferedImage img,double x,double y) {
        this();
        this.image.setValue(img);
        this.px.setValue(x);
        this.py.setValue(y);
    }

    @Override
    public Node create() {
        return new PrintImage();
    }

    @Override
    public void update() {
        cleanAllInputs();
    }

    @Override
    public void print(Graphics g) {
        g.drawImage((BufferedImage)image.getValue(),px.getValue().intValue(),py.getValue().intValue(),null);
    }
}
