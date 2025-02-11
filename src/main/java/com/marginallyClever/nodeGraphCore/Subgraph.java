package com.marginallyClever.nodeGraphCore;

import com.marginallyClever.nodeGraphCore.PrintWithGraphics;
import com.marginallyClever.nodeGraphCore.SupergraphInput;
import com.marginallyClever.nodeGraphCore.SupergraphOutput;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A {@link Subgraph} is a {@link Node} which contains another graph.
 */
public class Subgraph extends Node implements SupergraphInput, SupergraphOutput, PrintWithGraphics {
    private final NodeGraph graph = new NodeGraph();

    private class VariablePair {
        public NodeVariable<?> superVariable;
        public NodeVariable<?> subVariable;

        public VariablePair(NodeVariable subVariable) {
            this.superVariable = subVariable.createInverse();
            this.subVariable = subVariable;
        }
    }

    private ArrayList<VariablePair> pairs = new ArrayList<>();

    public Subgraph() {
        super("SubGraph");
    }

    public Subgraph(NodeGraph graph) {
        this();
        setGraph(graph);
    }

    /**
     * Stores a deep copy of the given graph and exposes the {@link SupergraphInput}s and {@link SupergraphOutput}s to
     * the supergraph.
     * @param graph the {@link NodeGraph} to store.
     */
    public void setGraph(NodeGraph graph) {
        this.graph.clear();
        this.graph.add(graph.deepCopy());

        for(Node n : this.graph.getNodes()) {
            extractSupergraphInputs(n);
            extractSupergraphOutputs(n);
        }

        // sort and add the pairs.
        Collections.sort(pairs,(a, b)->sortVariables(a,b));
        for(VariablePair p : pairs) {
            this.addVariable(p.superVariable);
        }

        this.updateBounds();
    }

    private void extractSupergraphOutputs(Node n) {
        if(n instanceof SupergraphOutput) {
            System.out.println("SupergraphOutput "+n.getUniqueName());
            for(int i=0;i<n.getNumVariables();++i) {
                NodeVariable<?> v = n.getVariable(i);
                if(v.getHasInput()) {
                    System.out.println("found output "+v.getName());
                    addToPairs(v);
                }
            }
        }
    }

    private void extractSupergraphInputs(Node n) {
        if(n instanceof SupergraphInput) {
            System.out.println("SupergraphInput "+n.getUniqueName());
            for(int i=0;i<n.getNumVariables();++i) {
                NodeVariable<?> v = n.getVariable(i);
                if(v.getHasOutput()) {
                    System.out.println("found input "+v.getName());
                    addToPairs(v);
                }
            }
        }
    }

    private int sortVariables(VariablePair a, VariablePair b) {
        // all input first
        int aIn = (a.subVariable.getHasInput())?1:0;
        int bIn = (a.subVariable.getHasInput())?1:0;
        if(aIn != bIn) return aIn-bIn;
        // then sort by name alphabetically
        return a.subVariable.getName().compareTo(b.subVariable.getName());
    }

    private void addToPairs(NodeVariable<?> v) {
        VariablePair p = new VariablePair(v);
        pairs.add(p);
    }

    public NodeGraph getGraph() {
        return graph;
    }

    @Override
    public Node create() {
        return new Subgraph();
    }

    @Override
    public void update() {
        for(VariablePair p : pairs) {
            if(p.superVariable.getHasInput()) {
                if (p.superVariable.getIsDirty()) {
                    p.subVariable.setValue(p.superVariable.getValue());
                }
            }
            if(p.superVariable.getHasOutput()) {
                if (p.subVariable.getIsDirty()) {
                    p.superVariable.setValue(p.subVariable.getValue());
                }
            }
        }

        graph.update();
        cleanAllInputs();
    }

    @Override
    public void print(Graphics g) {
        for(Node n : this.graph.getNodes()) {
            if(n instanceof PrintWithGraphics) {
                ((PrintWithGraphics)n).print(g);
            }
        }
    }
}
