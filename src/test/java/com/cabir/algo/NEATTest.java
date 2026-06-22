package com.cabir.algo;

import com.cabir.skeleton.LayerSkeleton;
import com.cabir.skeleton.NetworkSkeleton;
import org.junit.jupiter.api.Test;
import com.cabir.math.Matrix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NEATTest {
    @Test
    void createNewGenerationsKeepsPopulationSizeWhenFitnessesAreEqual() {
        NetworkSkeleton skeleton = new NetworkSkeleton();
        skeleton.add(new LayerSkeleton(1, 1));
        NEAT neat = new NEAT(skeleton, 6, 0.1, 1);

        neat.getPopulation().forEach(network -> network.fitness = 0.0);

        assertDoesNotThrow(neat::createNewGenerations);
        assertEquals(6, neat.getPopulation().size());
    }

    @Test
    void fitLeavesFinalPopulationEvaluated() {
        NetworkSkeleton skeleton = new NetworkSkeleton();
        skeleton.add(new LayerSkeleton(1, 1));
        NEAT neat = new NEAT(skeleton, 6, 0.1, 3);
        Matrix inputData = new Matrix(new double[][]{{0}, {1}});
        Matrix outputData = new Matrix(new double[][]{{0}, {1}});

        neat.fit(inputData, outputData);

        assertTrue(neat.getPopulation().stream().allMatch(network -> network.fitness > 0.0));
    }
}
