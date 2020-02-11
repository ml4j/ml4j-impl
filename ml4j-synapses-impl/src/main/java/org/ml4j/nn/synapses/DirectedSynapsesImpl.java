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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ml4j.nn.activationfunctions.DifferentiableActivationFunction;
import org.ml4j.nn.axons.Axons;
import org.ml4j.nn.components.DirectedComponentsContext;
import org.ml4j.nn.components.NeuralComponentBaseType;
import org.ml4j.nn.components.NeuralComponentType;
import org.ml4j.nn.components.NeuralComponentVisitor;
import org.ml4j.nn.components.activationfunctions.DifferentiableActivationFunctionComponent;
import org.ml4j.nn.components.activationfunctions.DifferentiableActivationFunctionComponentActivation;
import org.ml4j.nn.components.factories.DirectedComponentFactory;
import org.ml4j.nn.components.manytoone.PathCombinationStrategy;
import org.ml4j.nn.components.onetone.DefaultChainableDirectedComponent;
import org.ml4j.nn.components.onetone.DefaultDirectedComponentBipoleGraph;
import org.ml4j.nn.components.onetone.DefaultDirectedComponentBipoleGraphActivation;
import org.ml4j.nn.components.onetone.DefaultDirectedComponentChain;
import org.ml4j.nn.neurons.Neurons;
import org.ml4j.nn.neurons.NeuronsActivation;
import org.ml4j.nn.neurons.NeuronsActivationFeatureOrientation;
import org.ml4j.nn.neurons.format.NeuronsActivationFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of DirectedSynapses.
 * 
 * @author Michael Lavelle
 */
public class DirectedSynapsesImpl<L extends Neurons, R extends Neurons> implements DirectedSynapses<L, R> {

	/**
	 * Default serialization id.
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DirectedSynapsesImpl.class);

	private DifferentiableActivationFunction activationFunction;
	private DifferentiableActivationFunctionComponent activationFunctionComponent;

	private DefaultDirectedComponentBipoleGraph axonsGraph;

	private DirectedComponentFactory directedComponentFactory;

	private L leftNeurons;
	private R rightNeurons;
	private String name;

	/**
	 * Create a new implementation of DirectedSynapses.
	 * 
	 * @param directedComponentFactory
	 * @param LeftNeurons
	 * @param rightNeurons
	 * @param axonsGraph
	 * @param activationFunction
	 */
	protected DirectedSynapsesImpl(String name, DirectedComponentFactory directedComponentFactory, L leftNeurons, R rightNeurons,
			DefaultDirectedComponentBipoleGraph axonsGraph, DifferentiableActivationFunction activationFunction) {
		super();
		this.activationFunction = activationFunction;
		this.activationFunctionComponent = directedComponentFactory
				.createDifferentiableActivationFunctionComponent(name + ":ActivationFunction", rightNeurons, activationFunction);
		this.axonsGraph = axonsGraph;
		this.directedComponentFactory = directedComponentFactory;
		this.leftNeurons = leftNeurons;
		this.rightNeurons = rightNeurons;
		Objects.requireNonNull(axonsGraph, "axonsGraph");
		this.name = name;

	}

	/**
	 * Create a new implementation of DirectedSynapses.
	 * 
	 * @param directedComponentFactory A factory implementation to create directed
	 *                                 components
	 * @param primaryAxons             The primary Axons within these synapses
	 * @param activationFunction       The activation function within these synapses
	 */
	public DirectedSynapsesImpl(String name,DirectedComponentFactory directedComponentFactory, Axons<L, R, ?> primaryAxons,
			DifferentiableActivationFunction activationFunction) {
		this(name, directedComponentFactory, primaryAxons.getLeftNeurons(), primaryAxons.getRightNeurons(),
				createGraph(name, directedComponentFactory, primaryAxons), activationFunction);
		this.directedComponentFactory = directedComponentFactory;
		this.name = name;
	}

	private static DefaultDirectedComponentBipoleGraph createGraph(String name, DirectedComponentFactory directedComponentFactory,
			Axons<?, ?, ?> primaryAxons) {
		List<DefaultChainableDirectedComponent<?, ?>> components = new ArrayList<>();
		components.add(directedComponentFactory.createDirectedAxonsComponent(name + ":PrimaryAxons", primaryAxons));

		DefaultDirectedComponentChain chain = directedComponentFactory.createDirectedComponentChain(components);
		List<DefaultChainableDirectedComponent<?, ?>> chainsList = new ArrayList<>();
		chainsList.add(chain);
		// DefaultDirectedComponentChainBatch batch =
		// directedComponentFactory.createDirectedComponentChainBatch(chainsList);
		return directedComponentFactory.createDirectedComponentBipoleGraph(name, primaryAxons.getLeftNeurons(),
				primaryAxons.getRightNeurons(), chainsList, PathCombinationStrategy.ADDITION);
	}

	/**
	 * @return The Axons graph within these DirectedSynapses.
	 */
	public DefaultDirectedComponentBipoleGraph getAxonsGraph() {
		return axonsGraph;
	}

	@Override
	public DirectedSynapses<L, R> dup() {
		return new DirectedSynapsesImpl<>(name, directedComponentFactory, leftNeurons, rightNeurons, axonsGraph.dup(),
				activationFunction);
	}

	@Override
	public DifferentiableActivationFunction getActivationFunction() {
		return activationFunction;
	}

	@Override
	public DirectedSynapsesActivation forwardPropagate(NeuronsActivation input,
			DirectedComponentsContext directedComponentsContext) {

		LOGGER.debug("Forward propagating through DirectedSynapses");

		NeuronsActivation inputNeuronsActivation = input;

		DefaultDirectedComponentBipoleGraphActivation axonsActivationGraph = axonsGraph
				.forwardPropagate(inputNeuronsActivation, directedComponentsContext);

		NeuronsActivation totalAxonsOutputActivation = axonsActivationGraph.getOutput();

		DifferentiableActivationFunctionComponentActivation actAct = activationFunctionComponent
				.forwardPropagate(totalAxonsOutputActivation, directedComponentsContext);

		NeuronsActivation outputNeuronsActivation = actAct.getOutput();

		return new DirectedSynapsesActivationImpl(this, input, axonsActivationGraph, actAct, outputNeuronsActivation,
				directedComponentsContext);

	}

	@Override
	public L getLeftNeurons() {

		return leftNeurons;
	}

	@Override
	public R getRightNeurons() {
		return rightNeurons;
	}

	@Override
	public DirectedComponentsContext getContext(DirectedComponentsContext directedComponentsContext) {
		return directedComponentsContext;
	}

	@Override
	public List<DefaultChainableDirectedComponent<?, ?>> decompose() {

		List<DefaultChainableDirectedComponent<?, ?>> components = new ArrayList<>();
		components.addAll(axonsGraph.decompose().stream().collect(Collectors.toList()));
		components.addAll(activationFunctionComponent.decompose());
		return components;
	}

	@Override
	public NeuralComponentType getComponentType() {
		return NeuralComponentType.createSubType(NeuralComponentType.getBaseType(NeuralComponentBaseType.SYNAPSES),
				DirectedSynapses.class.getName());
	}

	@Override
	public Neurons getInputNeurons() {
		return leftNeurons;
	}

	@Override
	public Neurons getOutputNeurons() {
		return rightNeurons;
	}

	@Override
	public boolean isSupported(NeuronsActivationFormat<?> format) {
		return NeuronsActivationFeatureOrientation.ROWS_SPAN_FEATURE_SET.equals(format.getFeatureOrientation());
	}

	@Override
	public Optional<NeuronsActivationFormat<?>> optimisedFor() {
		return Optional.empty();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String accept(NeuralComponentVisitor<DefaultChainableDirectedComponent<?, ?>> visitor) {
		return visitor.visitParallelComponentBatch(name, axonsGraph.getEdges().getComponents(), PathCombinationStrategy.ADDITION);
	}

}
