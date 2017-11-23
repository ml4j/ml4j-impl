/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ml4j.nn.synapses;

import org.ml4j.Matrix;
import org.ml4j.nn.activationfunctions.DifferentiableActivationFunction;
import org.ml4j.nn.axons.Axons;
import org.ml4j.nn.axons.AxonsActivation;
import org.ml4j.nn.axons.TrainableAxons;
import org.ml4j.nn.neurons.Neurons;
import org.ml4j.nn.neurons.NeuronsActivation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of DirectedSynapses.
 * 
 * @author Michael Lavelle
 */
public class AxonsOnlyDirectedSynapsesImpl<L extends Neurons, R extends Neurons> 
    implements DirectedSynapses<L, R> {

  /**
   * Default serialization id.
   */
  private static final long serialVersionUID = 1L;
  
  private static final Logger LOGGER = 
      LoggerFactory.getLogger(AxonsOnlyDirectedSynapsesImpl.class);
  
  private Axons<? extends L, ? extends R, ?> axons;
  
  /**
   * Create a new implementation of DirectedSynapses.
   * 
   * @param axons The Axons within these synapses
   */
  public AxonsOnlyDirectedSynapsesImpl(Axons<? extends L, ? extends R, ?> axons) {
    super();
    this.axons = axons;
  }

  /*
  @Override
  public Axons<? extends L, ? extends R, ?> getAxons() {
    return axons;
  }
  */

  @Override
  public DirectedSynapses<L, R> dup() {
    return new AxonsOnlyDirectedSynapsesImpl<L, R>(axons.dup());
  }

  /**.
  @Override
  public DifferentiableActivationFunction getActivationFunction() {
    return activationFunction;
  }
  **/

  @Override
  public DirectedSynapsesActivation forwardPropagate(DirectedSynapsesInput input,
      DirectedSynapsesContext synapsesContext) {
   
    NeuronsActivation inputNeuronsActivation = input.getInput();
    
    if (!inputNeuronsActivation.isBiasUnitIncluded() && axons.getLeftNeurons().hasBiasUnit()) {
      inputNeuronsActivation = inputNeuronsActivation.withBiasUnit(true, synapsesContext);
    }
   
    LOGGER.debug("Forward propagating through DirectedSynapses");
    AxonsActivation axonsActivation = 
        axons.pushLeftToRight(inputNeuronsActivation, null, 
            synapsesContext.createAxonsContext());
    
    NeuronsActivation axonsOutputActivation = axonsActivation.getOutput();
    
    return new DirectedSynapsesActivationImpl(this, 
        inputNeuronsActivation, axonsActivation, axonsOutputActivation);
  
  }
  
  @Override
  public DirectedSynapsesGradient backPropagate(DirectedSynapsesActivation activation,
      NeuronsActivation da,
      DirectedSynapsesContext context, boolean outerMostSynapses, double regularisationLamdba) {
   
    LOGGER.debug("Back propagating through synapses activation....");
    
    if (da.isBiasUnitIncluded()) {
      throw new IllegalArgumentException("Back propagated deltas must not contain bias unit");
    }
    
    if (axons.getRightNeurons().hasBiasUnit()) {
      throw new IllegalStateException(
          "Backpropagation through axons with a rhs bias unit not supported");
    }
    
    //NeuronsActivation axonsOutputActivation = activation.getAxonsActivation().getOutput();
    
    Matrix dz = null;
    
    dz = da.getActivations();
 
  
    if (da.getFeatureCountIncludingBias() != axons.getRightNeurons()
        .getNeuronCountExcludingBias()) {
      throw new IllegalArgumentException("Expected feature count to be:"
          + axons.getRightNeurons().getNeuronCountExcludingBias() + " but was:"
          + da.getFeatureCountIncludingBias());
    }
    
    // Does not contain output bias unit
    NeuronsActivation dzN = new NeuronsActivation(dz, 
        false,
        da.getFeatureOrientation());

    LOGGER.debug("Pushing data right to left through axons...");
    
    // Will contain bias unit if Axons have left  bias unit
    NeuronsActivation inputGradient =
        axons.pushRightToLeft(dzN, activation.getAxonsActivation(), 
            context.createAxonsContext()).getOutput();
    
     
    if (inputGradient.isBiasUnitIncluded()) {
      LOGGER.debug("Removing biases from back propagated deltas");
      inputGradient = new NeuronsActivation(inputGradient.getActivations(), 
          inputGradient.isBiasUnitIncluded(),
          inputGradient.getFeatureOrientation()).withBiasUnit(false, context);
    }
         
    Matrix totalTrainableAxonsGradient = null;
    
    if (axons instanceof TrainableAxons) {
     
      LOGGER.debug("Calculating Axons Gradients");

      totalTrainableAxonsGradient = 
          dz.mmul(activation.getAxonsActivation().getInput().getActivations());
      
      if (regularisationLamdba != 0) {
       
        LOGGER.debug("Calculating total regularisation Gradients");
   
        Matrix connectionWeightsCopy = axons.getDetachedConnectionWeights();

        Matrix firstRow = totalTrainableAxonsGradient.getRow(0);
        Matrix firstColumn = totalTrainableAxonsGradient.getColumn(0);

        totalTrainableAxonsGradient = totalTrainableAxonsGradient
            .addi(connectionWeightsCopy.muli(regularisationLamdba));
        
        if (axons.getLeftNeurons().hasBiasUnit()) {

          totalTrainableAxonsGradient.putRow(0, firstRow);
        }
        if (axons.getRightNeurons().hasBiasUnit()) {

          totalTrainableAxonsGradient.putColumn(0, firstColumn);
        }
      }
    }

    return new DirectedSynapsesGradientImpl(inputGradient, 
        totalTrainableAxonsGradient);
  }

  @Override
  public L getLeftNeurons() {
    return axons.getLeftNeurons();
  }

  @Override
  public R getRightNeurons() {
    return axons.getRightNeurons();
  }

  @Override
  public DifferentiableActivationFunction getActivationFunction() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Axons<?, ?, ?> getAxons() {
    return axons;
  }
  
  
}
