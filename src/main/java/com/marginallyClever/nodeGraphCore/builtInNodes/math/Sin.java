package com.marginallyClever.nodeGraphCore.builtInNodes.math;

import com.marginallyClever.nodeGraphCore.Node;
import com.marginallyClever.nodeGraphCore.NodeVariable;

public class Sin extends Node {
    private final NodeVariable<Number> a = NodeVariable.newInstance("A",Number.class,0,true,false);
    private final NodeVariable<Number> b = NodeVariable.newInstance("output",Number.class,0,false,true);

    public Sin() {
        super("Sin");
        addVariable(a);
        addVariable(b);
    }

    public Sin(double a,double b) {
        this();
        this.a.setValue(a);
    }

    @Override
    public Node create() {
        return new Sin();
    }

    @Override
    public void update() {
        double av = a.getValue().doubleValue();
        b.setValue(Math.sin(av));
        cleanAllInputs();
    }
}
