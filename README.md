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

## Requirements

- Java 21;
- Git;
- a terminal or an IDE with Gradle support, such as IntelliJ IDEA.

Check your Java version before building:

```bash
java -version
```

If several JDKs are installed, point Gradle to Java 21:

```bash
export JAVA_HOME=/path/to/jdk-21
```

On macOS with Homebrew OpenJDK 21, that may look like:

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.11/libexec/openjdk.jdk/Contents/Home
```

## Quick Start

Clone the repository, enter the project directory, and run the tests:

```bash
git clone https://github.com/burhancabiroglu/Neat4j.git
cd Neat4j
./gradlew test
```

The project is a library, so it does not start a graphical application by itself. The fastest way to verify the code is the test suite. The sandbox project shows the library in a game-like environment.

## Development Commands

Run the tests:

```bash
./gradlew test
```

Build the library:

```bash
./gradlew build
```

Publish to your local Maven repository:

```bash
./gradlew publishToMavenLocal
```

Publish to GitHub Packages:

```bash
./gradlew publish
```

Publishing to GitHub Packages requires credentials. For local publishing, add a GitHub personal access token, classic, to `~/.gradle/gradle.properties`:

```properties
gpr.user=your-github-username
gpr.token=your-github-token
```

The token needs `write:packages` for publishing. It needs `read:packages` when the package is only consumed as a dependency.

For GitHub Actions, the workflow uses `GITHUB_TOKEN` and runs automatically on pushes to `main`.

## Using Neat4j as a Dependency

Current package coordinates:

```kotlin
implementation("com.cabir:neat4j:1.1.0")
```

When consuming the package from GitHub Packages, add the package repository and provide credentials through Gradle properties or environment variables:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/burhancabiroglu/Neat4j")
        credentials {
            username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

For Gradle Groovy DSL:

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/burhancabiroglu/Neat4j")
        credentials {
            username = findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = findProperty("gpr.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation "com.cabir:neat4j:1.1.0"
}
```

## IntelliJ IDEA

Open the repository root as a Gradle project:

```text
File > Open > Neat4j
```

After Gradle sync finishes, run:

```text
Tasks > verification > test
```

You can also run individual JUnit tests from `src/test/java`.

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
