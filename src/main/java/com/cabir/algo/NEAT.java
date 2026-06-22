package com.cabir.algo;

import com.cabir.core.base.Layer;
import com.cabir.layer.Linear;
import com.cabir.math.Matrix;
import com.cabir.network.NeatNetwork;
import com.cabir.skeleton.NetworkSkeleton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public final class NEAT {
    private final NetworkSkeleton skeleton;
    private final int populationCount;
    private final double mutationRate;
    private ArrayList<NeatNetwork> population;
    private final int generationLimit;

    public NEAT(NetworkSkeleton skeleton, int populationCount, double mutationRate, int generationLimit){
        this.skeleton = skeleton;
        this.mutationRate = mutationRate;
        this.populationCount = populationCount;
        population = new ArrayList<>();
        this.generationLimit = generationLimit;
        for (int i = 0; i < populationCount; i++) population.add(new NeatNetwork(skeleton));
    }

    public NeatNetwork crossingOver(NeatNetwork nn1,NeatNetwork nn2){
        NeatNetwork child = new NeatNetwork(skeleton);
        for (int i = 0; i < child.size(); i++) {
            Layer layer = child.getLayer(i);
            if(!(layer instanceof Linear)) continue;
            for (int j = 0; j < layer.weight().shape(0); j++) {
                for (int k = 0; k < layer.weight().shape(1); k++) {
                    if(Math.random()>mutationRate){
                        if(Math.random()<parentChance(nn1, nn2)){
                            child.getLayer(i).weight().data[j][k] = nn1.getLayer(i).weight().data[j][k];
                        }
                        else{
                            child.getLayer(i).weight().data[j][k] = nn2.getLayer(i).weight().data[j][k];
                        }
                    }
                }
            }
        }

        for (int i = 0; i < child.size(); i++) {
            Layer layer = child.getLayer(i);
            if(!(layer instanceof Linear)) continue;
            for (int j = 0; j < layer.bias().shape(0); j++) {
                for (int k = 0; k < layer.bias().shape(1); k++) {
                    if(Math.random()>mutationRate){
                        if(Math.random()<parentChance(nn1, nn2)){
                            child.getLayer(i).bias().data[j][k] = nn1.getLayer(i).bias().data[j][k];
                        }
                        else{
                            child.getLayer(i).bias().data[j][k] = nn2.getLayer(i).bias().data[j][k];
                        }
                    }
                }
            }
        }

        return child;
    }

    private double parentChance(NeatNetwork nn1, NeatNetwork nn2) {
        double totalFitness = nn1.fitness + nn2.fitness;
        if (totalFitness <= 0) return 0.5;
        return nn1.fitness / totalFitness;
    }

    public void sortPopulation(final boolean reversed){
        population.sort(new Comparator<NeatNetwork>() {
            @Override
            public int compare(NeatNetwork o1, NeatNetwork o2) {
                if(reversed){
                    return Double.compare(o2.fitness,o1.fitness);
                }
                else {
                    return Double.compare(o1.fitness,o2.fitness);
                }
            }
        });
    }


    public void createNewGenerations(){
        ArrayList<NeatNetwork> nextGen = new ArrayList<>();
        sortPopulation(true);
        Optional<NeatNetwork> minFitNeuron = population.stream().min(Comparator.comparingDouble(o -> o.fitness));
        double minFit = minFitNeuron.map(neatNetwork -> neatNetwork.fitness).orElse(0.0);

        if (!population.isEmpty()) nextGen.add(population.get(0).clone());

        while (nextGen.size()<populationCount){
            NeatNetwork parent1 = selectParent(minFit);
            NeatNetwork parent2 = selectParent(minFit);
            nextGen.add(crossingOver(parent1,parent2));
        }
        population.clear();
        population.addAll(nextGen);

    }

    private NeatNetwork selectParent(double minFit) {
        double totalWeight = 0.0;
        for (NeatNetwork network : population) {
            totalWeight += selectionWeight(network, minFit);
        }

        if (totalWeight <= 0) {
            return population.get((int) Matrix.uniformRandom(0, population.size() - 1));
        }

        double pick = Math.random() * totalWeight;
        double cursor = 0.0;
        for (NeatNetwork network : population) {
            cursor += selectionWeight(network, minFit);
            if (pick <= cursor) return network;
        }
        return population.get(population.size() - 1);
    }

    private double selectionWeight(NeatNetwork network, double minFit) {
        return Math.pow(network.fitness - minFit + 1.0, 4);
    }


    public void fit(Matrix xVals, Matrix yTrue){
        for (int i = 0; i < generationLimit; i++) {
            for (NeatNetwork neatNetwork : population) {
                neatNetwork.forward(xVals, yTrue);
            }
            createNewGenerations();
        }
    }

    public NeatNetwork best(){
        sortPopulation(true);
        return population.get(0);
    }

    public ArrayList<NeatNetwork> getPopulation() {
        return population;
    }

    public void setPopulation(ArrayList<NeatNetwork> population) {
        this.population = population;
    }
}
