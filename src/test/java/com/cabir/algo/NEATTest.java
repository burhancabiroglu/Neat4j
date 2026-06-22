package com.cabir.algo;

import com.cabir.skeleton.LayerSkeleton;
import com.cabir.skeleton.NetworkSkeleton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
}
