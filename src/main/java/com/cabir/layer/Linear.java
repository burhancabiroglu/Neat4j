package com.cabir.layer;

import com.cabir.core.annotation.LayerAnnotation;
import com.cabir.core.base.Layer;
import com.cabir.math.Matrix;
import com.cabir.math.Shape;


@LayerAnnotation(type = LayerAnnotation.Linear)
public class Linear extends Layer {
    private final Shape shape;
    private final Matrix weight;
    private final Matrix bias;

    public Linear(int inputSize,int outputSize){
        this(new Shape(inputSize,outputSize));
    }

    public Linear(Shape shape){
        this.shape = shape;
        weight = Matrix.randomMatrix(shape.input(), shape.output());
        bias = Matrix.randomMatrix(1, shape.output());
    }

    @Override
    public Matrix forward(Matrix m) {
        input = m.clone();
        Matrix weightCalc = Matrix.dot(m, weight);
        output = Matrix.addBias(weightCalc,bias);
        return output;
    }


    @Override
    public String toString() {
        return "\nLinear{" +
                "shape=" + shape +
                ", weight=" + weight.shape() +
                ", bias=" + bias.shape() +
                '}';
    }

    @Override
    public Matrix weight() {
        return weight;
    }

    @Override
    public Matrix bias() {
        return bias;
    }
}
