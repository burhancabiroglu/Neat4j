package com.cabir.network;

import com.cabir.skeleton.LayerSkeleton;
import com.cabir.skeleton.NetworkSkeleton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class NeatNetworkTest {
    @Test
    void cloneCopiesWeightsIndependently() {
        NetworkSkeleton skeleton = new NetworkSkeleton();
        skeleton.add(new LayerSkeleton(1, 1));
        NeatNetwork network = new NeatNetwork(skeleton);

        NeatNetwork clone = network.clone();
        double originalWeight = network.getLayer(0).weight().data[0][0];

        clone.getLayer(0).weight().data[0][0] = originalWeight + 1.0;

        assertNotEquals(clone.getLayer(0).weight().data[0][0], network.getLayer(0).weight().data[0][0]);
    }
}
