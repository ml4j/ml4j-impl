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

package org.ml4j.nn.supervised;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ml4j.EditableMatrix;
import org.ml4j.Matrix;
import org.ml4j.MatrixFactory;
import org.ml4j.nn.CostAndGradientsImpl;
import org.ml4j.nn.ForwardPropagation;
import org.ml4j.nn.ForwardPropagationImpl;
import org.ml4j.nn.LayeredFeedForwardNeuralNetworkBase;
import org.ml4j.nn.LayeredFeedForwardNeuralNetworkContext;
import org.ml4j.nn.components.DirectedComponentsContext;
import org.ml4j.nn.components.factories.DirectedComponentFactory;
import org.ml4j.nn.components.onetone.DefaultChainableDirectedComponent;
import org.ml4j.nn.layers.DirectedLayerChain;
import org.ml4j.nn.layers.DirectedLayerChainImpl;
import org.ml4j.nn.layers.FeedForwardLayer;
import org.ml4j.nn.neurons.Neurons;
import org.ml4j.nn.neurons.NeuronsActivation;

/**
 * Default implementation of LayeredSupervisedFeedForwardNeuralNetwork.
 *
 * @author Michael Lavelle
 */
public class LayeredSupervisedFeedForwardNeuralNetworkImpl extends
		LayeredFeedForwardNeuralNetworkBase<LayeredFeedForwardNeuralNetworkContext, LayeredSupervisedFeedForwardNeuralNetwork>
		implements LayeredSupervisedFeedForwardNeuralNetwork {

	/**
	 * Default serialization id.
	 */
	private static final long serialVersionUID = 1L;

	public LayeredSupervisedFeedForwardNeuralNetworkImpl(String name, DirectedComponentFactory directedComponentFactory,
			DirectedLayerChain<FeedForwardLayer<?, ?>> initialisingComponentChain) {
		super(name, directedComponentFactory, initialisingComponentChain);
	}

	public LayeredSupervisedFeedForwardNeuralNetworkImpl(String name, DirectedComponentFactory directedComponentFactory,
			List<FeedForwardLayer<?, ?>> layers) {
		super(name, directedComponentFactory, new DirectedLayerChainImpl<>(layers));
	}

	@Override
	public void train(NeuronsActivation trainingDataActivations, NeuronsActivation trainingLabelActivations,
			LayeredFeedForwardNeuralNetworkContext trainingContext) {
		super.train(trainingDataActivations, trainingLabelActivations, trainingContext);
	}

	/**
	 * Return the prediction accuracy.
	 * 
	 * @param inputActivations                 The input activations.
	 * @param desiredClassificationActivations The desired prediction activations.
	 * @param context                          The context.
	 * @return The accuracy
	 */
	@Override
	public float getClassificationAccuracy(NeuronsActivation inputActivations,
			NeuronsActivation desiredClassificationActivations, LayeredFeedForwardNeuralNetworkContext context) {

		// Forward propagate the trainingDataActivations
		ForwardPropagation forwardPropagation = forwardPropagate(inputActivations, context);

		Matrix predictions = getClassifications(
				forwardPropagation.getOutput().getActivations(context.getMatrixFactory()).transpose(),
				context.getMatrixFactory());

		return computeAccuracy(predictions,
				desiredClassificationActivations.getActivations(context.getMatrixFactory()).transpose());
	}

	private Matrix getClassifications(Matrix outputActivations, MatrixFactory matrixFactory) {

		EditableMatrix predictions = matrixFactory
				.createZeros(outputActivations.getRows(), outputActivations.getColumns()).asEditableMatrix();
		for (int row = 0; row < outputActivations.getRows(); row++) {

			int index = outputActivations.getRow(row).argmax();
			predictions.put(row, index, 1);
		}
		return predictions;
	}

	/**
	 * Helper function to compute the accuracy of predictions using calculated
	 * predictions predictions and correct output matrix.
	 *
	 * @param predictions The predictions
	 * @param Y           The desired output labels
	 * @return The accuracy of the network
	 */
	protected float computeAccuracy(Matrix predictions, Matrix outputs) {
		return ((predictions.mul(outputs)).sum()) * 100 / outputs.getRows();
	}

	@Override
	public LayeredSupervisedFeedForwardNeuralNetwork dup(DirectedComponentFactory directedComponentFactory) {
		throw new UnsupportedOperationException("Not yet implemented");
		// return new SupervisedFeedForwardNeuralNetworkImpl2(getLayer(0), getLayer(1));
	}

	@Override
	public CostAndGradientsImpl getCostAndGradients(NeuronsActivation inputActivations,
			NeuronsActivation desiredOutpuActivations, LayeredFeedForwardNeuralNetworkContext trainingContext) {
		return super.getCostAndGradients(inputActivations, desiredOutpuActivations, trainingContext);
	}

	@Override
	public List<DefaultChainableDirectedComponent<?, ?>> decompose() {
		throw new UnsupportedOperationException();

	}

	@Override
	public LayeredFeedForwardNeuralNetworkContext getContext(DirectedComponentsContext directedComponentsContext) {
		return directedComponentsContext.getContext(this, ()  -> new LayeredFeedForwardNeuralNetworkContextImpl(directedComponentsContext, 0, null, directedComponentsContext.isTrainingContext()),
				context -> new LayeredFeedForwardNeuralNetworkContextImpl(context.getDirectedComponentsContext(), context.getStartLayerIndex(), context.getEndLayerIndex(), directedComponentsContext.isTrainingContext()));
	}

	@Override
	public ForwardPropagation forwardPropagate(NeuronsActivation inputActivation,
			LayeredFeedForwardNeuralNetworkContext context) {
		return new ForwardPropagationImpl(inputActivation, trailingActivationFunctionComponentChain.forwardPropagate(inputActivation,
				context.getDirectedComponentsContext()));
	}

	@Override
	public Neurons getInputNeurons() {
		return initialisingComponentChain.getInputNeurons();
	}

	@Override
	public Neurons getOutputNeurons() {
		return initialisingComponentChain.getOutputNeurons();
	}

	@Override
	public Set<DefaultChainableDirectedComponent<?, ?>> flatten() {
		Set<DefaultChainableDirectedComponent<?, ?>> allComponentsIncludingThis = new HashSet<>(Arrays.asList(this));
		allComponentsIncludingThis.addAll(trailingActivationFunctionComponentChain.flatten());
		return allComponentsIncludingThis;
	}

}
