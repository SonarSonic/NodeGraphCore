package com.marginallyClever.nodeGraphCore.builtInNodes.math;

import com.marginallyClever.nodeGraphCore.Node;
import com.marginallyClever.nodeGraphCore.NodeVariable;

public class Cos extends Node {
    private final NodeVariable<Number> a = NodeVariable.newInstance("A",Number.class,0,true,false);
    private final NodeVariable<Number> b = NodeVariable.newInstance("output",Number.class,0,false,true);

    public Cos() {
        super("Cos");
        addVariable(a);
        addVariable(b);
    }

    public Cos(double a,double b) {
        this();
        this.a.setValue(a);
    }

    @Override
    public Node create() {
        return new Cos();
    }

    @Override
    public void update() {
        double av = a.getValue().doubleValue();
        b.setValue(Math.cos(av));
        cleanAllInputs();
    }
}
