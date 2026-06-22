# Neat4j

`Neat4j` is a small Java library for experimenting with neuroevolution. It evolves the weights and biases of feed-forward neural networks described by a fixed `NetworkSkeleton`.

The project is inspired by NEAT, NeuroEvolution of Augmenting Topologies, introduced by Stanley and Miikkulainen in 2002. The current implementation is intentionally narrower than full NEAT: it does not yet evolve network topology, track innovation numbers, perform speciation, or mutate node and connection genes. It is best understood as a fixed-topology neuroevolution library and a foundation for a fuller NEAT implementation.

## Research Context

NEAT is an influential neuroevolution method that combines three ideas:

- historical markings for meaningful crossover between different topologies;
- speciation to protect structural innovations during early evolution;
- incremental complexification, where networks begin simple and become more complex over time.

`Neat4j` currently focuses on the weight-evolution side of that family of ideas. This makes the code easier to inspect and useful for educational experiments, while leaving a clear path toward structural NEAT.

Reference:

- Kenneth O. Stanley and Risto Miikkulainen, ["Evolving Neural Networks through Augmenting Topologies"](http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf), Evolutionary Computation, 10(2), 99-127, 2002.

## Current Scope

Implemented:

- dense matrix operations for simple neural-network computation;
- linear layers with weight and bias parameters;
- ReLU and sigmoid activation layers;
- feed-forward network execution;
- population-based fitness evaluation;
- crossover and mutation over existing weight and bias values;
- elitism and weighted parent selection;
- JUnit tests for core training and matrix behavior.

Not implemented yet:

- topology-changing mutations;
- node genes and connection genes;
- innovation numbers;
- speciation;
- recurrent connections;
- deterministic seeding;
- model serialization.

## Project Structure

```text
src/main/java/com/cabir
├── algo        # Neuroevolution loop
├── core        # Base layer contracts and annotations
├── layer       # Linear and activation layers
├── loss        # Fitness/loss helpers
├── math        # Matrix and activation math
├── network     # Feed-forward network model
└── skeleton    # Declarative network construction
```

The main entry points are:

- `NEAT`: owns the population and generation loop;
- `NetworkSkeleton`: describes the architecture;
- `LayerSkeleton`: describes each linear or activation layer;
- `NeatNetwork`: executes a generated network and stores its fitness.

## Quick Example

The example below trains a small fixed-topology network on XOR. Because this is an evolutionary process, outputs vary between runs.

```java
import com.cabir.algo.NEAT;
import com.cabir.core.annotation.ActivationAnnotation;
import com.cabir.math.Matrix;
import com.cabir.network.NeatNetwork;
import com.cabir.skeleton.LayerSkeleton;
import com.cabir.skeleton.NetworkSkeleton;

Matrix inputData = new Matrix(new double[][]{
        {0, 0},
        {0, 1},
        {1, 0},
        {1, 1}
});

Matrix outputData = new Matrix(new double[][]{
        {0},
        {1},
        {1},
        {0}
});

NetworkSkeleton skeleton = new NetworkSkeleton();
skeleton.add(new LayerSkeleton(2, 4));
skeleton.add(new LayerSkeleton(ActivationAnnotation.ReLU));
skeleton.add(new LayerSkeleton(4, 4));
skeleton.add(new LayerSkeleton(ActivationAnnotation.ReLU));
skeleton.add(new LayerSkeleton(4, 3));
skeleton.add(new LayerSkeleton(ActivationAnnotation.ReLU));
skeleton.add(new LayerSkeleton(3, 1));

NEAT neat = new NEAT(skeleton, 60, 0.1, 500);
neat.fit(inputData, outputData);

NeatNetwork bestNetwork = neat.best();
Matrix prediction = bestNetwork.forward(inputData);
prediction.log();
```

## Build and Test

Use Java 21 or a compatible modern JDK.

```bash
./gradlew test
```

To publish locally:

```bash
./gradlew publishToMavenLocal
```

Current package coordinates:

```kotlin
implementation("com.cabir:neat4j:1.1.0-SNAPSHOT")
```

## Design Notes

The library keeps the network topology fixed during training. A generation evaluates every network against the provided target data, preserves the best individual, then builds the next population through weighted parent selection, crossover, and mutation.

Fitness is currently calculated from mean squared error:

```text
fitness = 1 / (1 + mse)
```

This keeps the value positive and gives better networks higher selection probability.

## Roadmap

The most important next step is to separate the neural-network phenotype from a genome representation. That would make it possible to add:

- node and connection genes;
- innovation tracking;
- structural mutation;
- meaningful crossover across different topologies;
- speciation and adjusted fitness;
- reproducible seeded experiments.

## License

This project is licensed under the terms included in `LICENSE`.
