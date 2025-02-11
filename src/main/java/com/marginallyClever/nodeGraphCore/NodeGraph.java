package com.marginallyClever.nodeGraphCore;

import com.google.gson.annotations.JsonAdapter;
import com.marginallyClever.nodeGraphCore.builtInNodes.math.Add;
import com.marginallyClever.nodeGraphCore.json.NodeGraphJsonAdapter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link NodeGraph} contains the {@link Node}s, and {@link NodeConnection}s
 */
@JsonAdapter(NodeGraphJsonAdapter.class)
public class NodeGraph {
    private final List<Node> nodes = new ArrayList<>();
    private final List<NodeConnection> connections = new ArrayList<>();

    /**
     * Default constructor.  Creates an empty {@link NodeGraph}.
     */
    public NodeGraph() {
        super();
    }

    /**
     * <ul>
     * <li>Updates only dirty nodes.</li>
     * <li>Transmits dirty node outputs to connected inputs.</li>
     * <li>Sets all outputs to clean.</li>
     * </ul>
     * The method does not analyze the directed graph to run nodes in an "intelligent" way.
     */
    public void update() {
        for(Node n : nodes) n.updateIfNotDirty();
        for(NodeConnection c : connections) c.applyIfDirty();
        for(Node n : nodes) n.cleanAllOutputs();
    }

    /**
     * @return a {@link List} of all the {@link Node}s within this {@link NodeGraph}.
     * It is not a copy!  Use with caution.
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * @return a {@link List} of all the {@link NodeConnection}s within this {@link NodeGraph}.
     * It is not a copy!  Use with caution.
     */
    public List<NodeConnection> getConnections() {
        return connections;
    }

    /**
     * Adds a node to this graph.
     * @param node the subject
     * @return the same node for convenient method chaining.
     */
    public Node add(Node node) {
        nodes.add(node);
        return node;
    }

    /**
     * Remove a {@link Node} and all associated {@link NodeConnection}s from the model.
     * @param n the subject to be removed.
     */
    public void remove(Node n) {
        nodes.remove(n);
        removeConnectionsToNode(n);
    }

    /**
     * Adds a {@link NodeConnection} without checking if it already exists.
     * @param connection the item to add.
     * @return the same connection for convenient method chaining.
     */
    public NodeConnection add(NodeConnection connection) {
        connections.add(connection);
        return connection;
    }

    public void remove(NodeConnection c) {
        connections.remove(c);
    }

    /**
     * Remove all {@link NodeConnection}s from the model associated with a given {@link Node}
     * @param n the subject from which all connections should be removed.
     */
    public void removeConnectionsToNode(Node n) {
        ArrayList<NodeConnection> toKeep = new ArrayList<>();
        for(NodeConnection c : connections) {
            if(!c.isConnectedTo(n)) toKeep.add(c);
        }
        connections.clear();
        connections.addAll(toKeep);
    }

    /**
     * Searches this {@link NodeGraph} for an equivalent {@link NodeConnection}.
     * @param connection the item to match.
     * @return returns the matching {@link NodeConnection} or null.
     */
    public NodeConnection getMatchingConnection(NodeConnection connection) {
        for(NodeConnection c : connections) {
            if(c.equals(connection)) return c;
        }
        return null;
    }

    @Override
    public String toString() {
        return "NodeBasedEditorModel{" +
                "nodes=" + nodes +
                ", connections=" + connections +
                '}';
    }

    /**
     * Empty the model.
     */
    public void clear() {
        nodes.clear();
        connections.clear();
    }

    /**
     * Return the first connection point found within radius of a point
     * @param point center of search area
     * @param r radius limit
     * @return a {@link NodeConnectionPointInfo} describing the point found or null.
     */
    public NodeConnectionPointInfo getFirstNearbyConnection(Point point, double r) {
        double rr=r*r;
        for(Node n : nodes) {
            for(int i = 0; i < n.getNumVariables(); ++i) {
                NodeVariable<?> v = n.getVariable(i);
                if(v.getHasInput() && v.getInPosition().distanceSq(point) < rr) {
                    return new NodeConnectionPointInfo(n,i, NodeConnectionPointInfo.IN);
                }
                if(v.getHasOutput() && v.getOutPosition().distanceSq(point) < rr) {
                    return new NodeConnectionPointInfo(n,i, NodeConnectionPointInfo.OUT);
                }
            }
        }
        return null;
    }

    public Node findNodeWithUniqueName(String uniqueName) {
        for(Node n : nodes) {
            if(n.getUniqueName().equals(uniqueName)) return n;
        }
        return null;
    }

    /**
     * Every {@link Node} and {@link NodeConnection} has a unique ID.  If the model has just been restored from a file
     * then the static unique ID will be wrong.  This method bumps the first available unique ID up to the largest value
     * found.  Then the next attempt to create a unique item will be safe.
     */
    public void bumpUpIndexableID() { //TODO THIS SHOULDN'T BE PUBLIC!
        int id=0;
        for(Node n : nodes) {
            id = Math.max(id, n.getUniqueID());
        }
        Node.setUniqueIDSource(id);
    }

    /**
     * Add all {@link Node}s and {@link NodeConnection}s from one model to this model.
     * @param b the model to add.
     */
    public void add(NodeGraph b) {
        assignNewUniqueIDs(0);
        b.assignNewUniqueIDs(Node.getUniqueIDSource());

        nodes.addAll(b.nodes);
        connections.addAll(b.connections);

        bumpUpIndexableID();
    }

    private void assignNewUniqueIDs(int startingIndex) {
        for(Node n : nodes) n.setUniqueID(++startingIndex);
    }

    /**
     * Returns a deep copy of this {@link NodeGraph} by using the JSON serialization methods.
     * @return the {@link NodeGraph} copy
     */
    public NodeGraph deepCopy() {
        return JSONHelper.deepCopy(this);
    }

    /**
     * Return the index within the list of nodes of the first occurrence of class c.
     * Comparisons are done using {@code isInstance()} and may return subclasses.
     * @param c the class to match.
     * @return the index within the list of nodes of the first occurrence of class c, or -1.
     */
    public int indexOfNode(Class<Add> c) {
        return indexOfNode(c,0);
    }

    /**
     * Return the index within the list of nodes of the first occurrence of class c, starting the search at the
     * specified index.
     * Comparisons are done using {@code isInstance()} and may return subclasses.
     * @param c the class to match.
     * @param fromIndex the index to start the search from.
     * @return the index within the list of nodes of the first occurrence of class c, or -1.
     */
    public int indexOfNode(Class<Add> c, int fromIndex) {
        for(int i=fromIndex;i<nodes.size();++i) {
            if(c.isInstance(nodes.get(i))) return i;
        }
        return -1;
    }

    /**
     * Return the number of instances of class c or its subclasses.
     * Comparisons are done using {@code isInstance()} and may count subclasses.
     * @param c the class to match
     * @return the number of instances of class c or its subclasses.
     */
    public int countNodesOfClass(Class<?> c) {
        int i=0;
        for(Node n : nodes) {
            if(c.isInstance(n)) i++;
        }
        return i;
    }

    public List<Node> getNodesInRectangle(Rectangle2D selectionArea) {
        if(selectionArea==null) throw new InvalidParameterException("selectionArea cannot be null.");
        ArrayList<Node> found = new ArrayList<>();
        for(Node n : nodes) {
            Rectangle r = n.getRectangle();
            if(selectionArea.intersects(r)) found.add(n);
        }
        return found;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Returns all {@link NodeConnection}s that are only connected between the given set of nodes.
     * @param selectedNodes the set of nodes to check
     * @return all {@link NodeConnection}s that are only connected between the given set of nodes.
     */
    public List<NodeConnection> getConnectionsBetweenTheseNodes(List<Node> selectedNodes) {
        List<NodeConnection> list = new ArrayList<>();
        if(selectedNodes.size()<2) return list;

        for(NodeConnection c : connections) {
            if(selectedNodes.contains(c.getOutNode()) &&
                selectedNodes.contains(c.getInNode())) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Calculates and returns the smallest {@link Rectangle} that contains all {@link Node}s.  If there are no Nodes
     * in this graph then nothing is done.
     * @return the smallest {@link Rectangle} that contains all {@link Node}s, or null.
     */
    public Rectangle getBounds() {
        if(nodes.size()==0) return null;

        Rectangle r=new Rectangle(nodes.get(0).getRectangle());
        for(Node n : nodes) {
            r.union(n.getRectangle());
            // for very small graphs this is a redundant union with self.
            // For very large graphs this avoids any 'if' in the loop and saves time.
        }
        return r;
    }
}
