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

package org.ml4j.nn.activationfunctions;

import org.ml4j.Matrix;
import org.ml4j.nn.neurons.NeuronsActivation;
import org.ml4j.nn.neurons.NeuronsActivationContext;
import org.ml4j.nn.util.NeuralNetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default Sigmoid Activation Function.
 * 
 * @author Michael Lavelle
 *
 */
public class SoftmaxActivationFunction implements DifferentiableActivationFunction {

  private static final Logger LOGGER = LoggerFactory.getLogger(SoftmaxActivationFunction.class);

  @Override
  public NeuronsActivation activate(NeuronsActivation input, NeuronsActivationContext context) {
    LOGGER.debug("Activating through SoftmaxActivationFunction");
    Matrix softmaxOfInputActivationsMatrix = NeuralNetUtils
        .softmax(input.withBiasUnit(false, context).getActivations());
    return new NeuronsActivation(softmaxOfInputActivationsMatrix, false,
        input.getFeatureOrientation()).withBiasUnit(input.isBiasUnitIncluded(), context);
  }

  @Override
  public NeuronsActivation activationGradient(NeuronsActivation outputActivation,
      NeuronsActivationContext context) {
    LOGGER.debug("Performing softmax gradient of NeuronsActivation");
    Matrix gradient = NeuralNetUtils.softmaxGradient(outputActivation.withBiasUnit(false, 
        context).getActivations());
    if (outputActivation.isBiasUnitIncluded()) {
      throw new IllegalArgumentException(
          "Activation gradient of activations with bias unit not supported");
    }
    return new NeuronsActivation(
        gradient,
        outputActivation.isBiasUnitIncluded(), 
        outputActivation.getFeatureOrientation());
  }
}
