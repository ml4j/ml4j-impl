/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ml4j.nn.unsupervised;

import org.ml4j.MatrixFactory;
import org.ml4j.nn.layers.UndirectedLayerContext;
import org.ml4j.nn.layers.UndirectedLayerContextImpl;

public class RestrictedBoltzmannMachineContextImpl implements RestrictedBoltzmannMachineContext {

  /**
   * Default serialization id.
   */
  private static final long serialVersionUID = 1L;

  private UndirectedLayerContext layerContext;

  private int trainingEpochs;

  private double trainingLearningRate;

  private Integer trainingMiniBatchSize;

  public RestrictedBoltzmannMachineContextImpl(MatrixFactory matrixFactory) {
    this.layerContext = new UndirectedLayerContextImpl(0, matrixFactory);
  }

  @Override
  public UndirectedLayerContext getLayerContext(int layerIndex) {
    if (layerIndex == 0) {
      return layerContext;
    } else {
      throw new IllegalArgumentException(
          "Restricted Boltzmann Machines have only a single " + "layer accessible at layerIndex 0");
    }
  }

  @Override
  public int getTrainingEpochs() {
    return trainingEpochs;
  }

  @Override
  public double getTrainingLearningRate() {
    return trainingLearningRate;
  }

  @Override
  public Integer getTrainingMiniBatchSize() {
    return trainingMiniBatchSize;
  }

  @Override
  public void setTrainingEpochs(int trainingEpochs) {
    this.trainingEpochs = trainingEpochs;
  }

  @Override
  public void setTrainingLearningRate(double trainingLearningRate) {
    this.trainingLearningRate = trainingLearningRate;
  }

  @Override
  public void setTrainingMiniBatchSize(Integer trainingMiniBatchSize) {
    this.trainingMiniBatchSize = trainingMiniBatchSize;
  }

  @Override
  public MatrixFactory getMatrixFactory() {
    return layerContext.getMatrixFactory();
  }
}
