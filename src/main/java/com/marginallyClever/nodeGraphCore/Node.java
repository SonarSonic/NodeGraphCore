package com.marginallyClever.nodeGraphCore;

import com.google.gson.annotations.JsonAdapter;
import com.marginallyClever.nodeGraphCore.json.NodeJsonAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Node} is a collection of zero or more inputs and zero or more outputs connected by some operator.
 * The operator is defined by extending the {@link Node} class and defining the {@code update()} method.
 */
@JsonAdapter(NodeJsonAdapter.class)
public abstract class Node {
    /**
     * The default height of the title bar.
     */
    public static final int TITLE_HEIGHT = 25;

    /**
     * This is used to ensure all {@link Node}s in a
     */
    private static int uniqueIDSource=0;

    private int uniqueID;

    private String name;

    private String label;

    private Rectangle rectangle;

    private final List<NodeVariable<?>> variables;

    public Node(String name) {
        super();
        this.uniqueID = ++uniqueIDSource;
        this.name = name;
        this.label = "";
        this.rectangle = new Rectangle(0,0,150,50);
        this.variables = new ArrayList<>();
    }

    /**
     * Return one new instance of this type of {@link Node}.
     * Override this method in derived classes.
     * @return One new instance of this type of {@link Node}.
     */
    public abstract Node create();

    public static void setUniqueIDSource(int index) {
        uniqueIDSource=index;
    }

    public static int getUniqueIDSource() {
        return uniqueIDSource;
    }

    public void setUniqueID(int i) {
        uniqueID=i;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NodeVariable<?>> getVariables() {
        return variables;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public String getName() {
        return name;
    }

    public String getUniqueName() {
        return uniqueID+"-"+name;
    }

    /**
     * Override this method to provide the custom behavior of this node.
     * Runs regardless of dirty inputs or outputs.
     */
    public abstract void update();

    /**
     * Runs {@link Node#update()} only if the node is considered dirty.  It is up to individual nodes to decide
     * if they are done (no longer dirty)
     */
    public void updateIfNotDirty() {
        if(!isDirty()) return;
        update();
    }

    public void updateBounds() {
        int w=(int)rectangle.getWidth();
        int h=Node.TITLE_HEIGHT;
        int y=getRectangle().y;
        int x=getRectangle().x;
        for(NodeVariable v : variables) {
            Rectangle r = v.getRectangle();
            r.y=h+y;
            r.x=x;
            if(w < r.width) w = r.width;
            h += r.height;
        }
        rectangle.width=w;
        rectangle.height=h;
    }
    /**
     * Check if any variables are dirty.
     * @return true if any variables are dirty.
     */
    public boolean isDirty() {
        for(NodeVariable<?> v : variables) {
            if (v.getIsDirty()) return true;
        }
        return false;
    }

    /**
     * Makes all input variables not dirty.
     */
    protected void cleanAllInputs() {
        for(NodeVariable<?> v : variables) {
            if(v.getHasInput()) v.setIsDirty(false);
        }
    }

    public void cleanAllOutputs() {
        for(NodeVariable<?> v : variables) {
            if(v.getHasOutput()) v.setIsDirty(false);
        }
    }

    public void addVariable(NodeVariable v) {
        variables.add(v);
    }

    public void removeVariable(NodeVariable v) {
        variables.remove(v);
    }

    public int getNumVariables() {
        return variables.size();
    }

    public NodeVariable<?> getVariable(int index) throws IndexOutOfBoundsException {
        return variables.get(index);
    }

    @Override
    public String toString() {
        return "Node{" +
                "name=" + getName() +
                ", uniqueID=" + getUniqueID() +
                ", label=" + label +
                ", variables=" + variables +
                ", rectangle=" + rectangle +
                '}';
    }

    public Point getInPosition(int index) {
        Rectangle r = getRectangle();
        Point p = new Point(r.x,r.y+(int)getPointHeight(index));
        return p;
    }

    public Point getOutPosition(int index) {
        Rectangle r = getRectangle();
        Point p = new Point(r.x+r.width,r.y+(int)getPointHeight(index));
        return p;
    }

    private double getPointHeight(int index) {
        float y = TITLE_HEIGHT;
        Rectangle inr = getRectangle();
        for(int i=0;i<index;++i) {
            y += getVariable(i).getRectangle().height;
        }
        y += getVariable(index).getRectangle().height/2;
        return y;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String str) {
        label=str;
    }

    /**
     * Sets the top left corner of the {@link Node}'s rectangle.
     * @param point the new position of the top left corner.
     */
    public void setPosition(Point point) {
        rectangle.x=point.x;
        rectangle.y=point.y;
    }

    public void moveRelative(int dx, int dy) {
        rectangle.x += dx;
        rectangle.y += dy;
    }


}
