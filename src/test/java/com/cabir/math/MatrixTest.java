package com.cabir.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatrixTest {
    @Test
    void constructorCopiesInputData() {
        double[][] source = {{1.0}};
        Matrix matrix = new Matrix(source);

        source[0][0] = 2.0;

        assertEquals(1.0, matrix.get(0, 0));
    }

    @Test
    void dotRejectsInvalidShapes() {
        Matrix left = new Matrix(1, 2);
        Matrix right = new Matrix(3, 1);

        assertThrows(IllegalArgumentException.class, () -> Matrix.dot(left, right));
    }
}
