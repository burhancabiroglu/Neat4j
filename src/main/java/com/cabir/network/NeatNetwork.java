package com.cabir.network;


import com.cabir.core.base.Layer;
import com.cabir.loss.MSELoss;
import com.cabir.math.Matrix;
import com.cabir.skeleton.LayerSkeleton;
import com.cabir.skeleton.NetworkSkeleton;

import java.util.ArrayList;

public class NeatNetwork {
    private final ArrayList<Layer> layers;
    public double fitness;
    private final NetworkSkeleton skeleton;

    public NeatNetwork(ArrayList<Layer> layers,double fitness,NetworkSkeleton skeleton){
        this.fitness = fitness;
        this.skeleton = skeleton;
        this.layers = new ArrayList<>();
        for (LayerSkeleton ls: skeleton.get()) this.layers.add(ls.generate());
        copyWeights(layers, this.layers);
    }

    public NeatNetwork(NetworkSkeleton skeleton){
        layers = new ArrayList<>();
        fitness = 0.0;
        for (LayerSkeleton ls: skeleton.get()) layers.add(ls.generate());
        this.skeleton = skeleton.clone();
    }

    public Matrix forward(Matrix inputData){
        Matrix output = inputData.clone();
        for (Layer layer: layers) output = layer.forward(output);
        return output;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Matrix forward(Matrix inputData, Matrix yTrue){
        double[][] data = {};
        Matrix output = new Matrix(data);
        for (int i = 0; i < inputData.rowCount() ;i++) {
            Matrix result = inputData.clone().getRow(i);
            for (Layer layer: layers)
                result = layer.forward(result);
            output.appendRow(result.data);
        }
        loss(yTrue,output);
        return output;
    }


    @SuppressWarnings("UnusedReturnValue")
    public double loss(Matrix yTrue, Matrix yPred){
        fitness = 1/(1+ MSELoss.calc(yTrue,yPred));
        return fitness;
    }


    public int size(){
        return layers.size();
    }

    public Layer getLayer(int index){
        return layers.get(index);
    }

    @Override
    public String toString() {
        return "NeatNetwork{" +
                "layers=" + layers +
                ", fitness=" + fitness +
                ", skeleton=" + skeleton +
                '}';
    }

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    @SuppressWarnings("UnusedReturnValue")
    public NeatNetwork clone(){
        return new NeatNetwork(this.layers,this.fitness,this.skeleton.clone());
    }

    private void copyWeights(ArrayList<Layer> sourceLayers, ArrayList<Layer> targetLayers) {
        for (int i = 0; i < sourceLayers.size(); i++) {
            Matrix sourceWeight = sourceLayers.get(i).weight();
            Matrix targetWeight = targetLayers.get(i).weight();
            if (sourceWeight != null && targetWeight != null) copyMatrix(sourceWeight, targetWeight);

            Matrix sourceBias = sourceLayers.get(i).bias();
            Matrix targetBias = targetLayers.get(i).bias();
            if (sourceBias != null && targetBias != null) copyMatrix(sourceBias, targetBias);
        }
    }

    private void copyMatrix(Matrix source, Matrix target) {
        for (int row = 0; row < source.shape(0); row++) {
            for (int column = 0; column < source.shape(1); column++) {
                target.data[row][column] = source.data[row][column];
            }
        }
    }
}
