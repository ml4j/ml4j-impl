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

package org.ml4j.nn.axons.mocks;

import org.ml4j.Matrix;
import org.ml4j.nn.axons.AxonsActivation;
import org.ml4j.nn.neurons.NeuronsActivation;

/**
 * Encapsulates the artifacts produced when pushing NeuronsActivations
 * through an Axons instance.
 * 
 * @author Michael Lavelle
 */
public class AxonsActivationMock implements AxonsActivation {

  private NeuronsActivation outputActivations;
  
  public AxonsActivationMock(NeuronsActivation outputActivations) {
    this.outputActivations = outputActivations;
  }
  
  @Override
  public NeuronsActivation getOutput() {
    return outputActivations;
  }

  @Override
  public Matrix getInputDropoutMask() {
    return null;
  }
}
