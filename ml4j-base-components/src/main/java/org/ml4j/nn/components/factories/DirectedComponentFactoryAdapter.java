/*
 * Copyright 2019 the original author or authors.
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
package org.ml4j.nn.components.factories;

import java.util.List;

import org.ml4j.nn.activationfunctions.ActivationFunctionProperties;
import org.ml4j.nn.activationfunctions.ActivationFunctionType;
import org.ml4j.nn.activationfunctions.DifferentiableActivationFunction;
import org.ml4j.nn.axons.Axons;
import org.ml4j.nn.axons.AxonsContextConfigurer;
import org.ml4j.nn.axons.BatchNormAxonsConfig;
import org.ml4j.nn.axons.BiasVector;
import org.ml4j.nn.axons.ConvolutionalAxonsConfig;
import org.ml4j.nn.axons.FullyConnectedAxonsConfig;
import org.ml4j.nn.axons.PoolingAxonsConfig;
import org.ml4j.nn.axons.WeightsMatrix;
import org.ml4j.nn.components.NeuralComponentType;
import org.ml4j.nn.components.activationfunctions.DifferentiableActivationFunctionComponent;
import org.ml4j.nn.components.axons.BatchNormDirectedAxonsComponent;
import org.ml4j.nn.components.axons.DirectedAxonsComponent;
import org.ml4j.nn.components.axons.base.BatchNormDirectedAxonsComponentAdapter;
import org.ml4j.nn.components.axons.base.DirectedAxonsComponentAdapter;
import org.ml4j.nn.components.manytoone.ManyToOneDirectedComponent;
import org.ml4j.nn.components.manytoone.PathCombinationStrategy;
import org.ml4j.nn.components.manytoone.base.ManyToOneDirectedComponentAdapter;
import org.ml4j.nn.components.onetomany.OneToManyDirectedComponent;
import org.ml4j.nn.components.onetomany.SerializableIntSupplier;
import org.ml4j.nn.components.onetomany.base.OneToManyDirectedComponentAdapter;
import org.ml4j.nn.components.onetone.DefaultChainableDirectedComponent;
import org.ml4j.nn.components.onetone.DefaultDirectedComponentBipoleGraph;
import org.ml4j.nn.components.onetone.DefaultDirectedComponentChain;
import org.ml4j.nn.neurons.Neurons;
import org.ml4j.nn.neurons.Neurons3D;

public class DirectedComponentFactoryAdapter implements DirectedComponentFactory {

	/**
	 * Default serialization id.
	 */
	private static final long serialVersionUID = 1L;
	
	protected DirectedComponentFactory delegated;
	
	
	public DirectedComponentFactoryAdapter(DirectedComponentFactory delegated) {
		this.delegated = delegated;
	}
	
	@Override
	public DirectedAxonsComponent<Neurons, Neurons, ?> createFullyConnectedAxonsComponent(String name, FullyConnectedAxonsConfig config, WeightsMatrix connectionWeights, BiasVector biases) {
		return new DirectedAxonsComponentAdapter<>(delegated.createFullyConnectedAxonsComponent(name, config, connectionWeights, biases));
	}

	@Override
	public DirectedAxonsComponent<Neurons3D, Neurons3D, ?> createConvolutionalAxonsComponent(String name, ConvolutionalAxonsConfig config,
			WeightsMatrix connectionWeights, BiasVector biases) {
		return new DirectedAxonsComponentAdapter<>(delegated.createConvolutionalAxonsComponent(name, config,
				connectionWeights, biases));
	}

	@Override
	public DirectedAxonsComponent<Neurons3D, Neurons3D, ?> createMaxPoolingAxonsComponent(String name, PoolingAxonsConfig config,
			boolean scaleOutputs) {
		return new DirectedAxonsComponentAdapter<>(delegated.createMaxPoolingAxonsComponent(name,  
				config, scaleOutputs));
	}

	@Override
	public DirectedAxonsComponent<Neurons3D, Neurons3D, ?> createAveragePoolingAxonsComponent(String name,  PoolingAxonsConfig config) {
		return new DirectedAxonsComponentAdapter<>(delegated.createAveragePoolingAxonsComponent(name,
				config));
	}

	@Override
	public <N extends Neurons> BatchNormDirectedAxonsComponent<N, ?> createBatchNormAxonsComponent(String name, BatchNormAxonsConfig<N> batchNormAxonsConfig) {
		return new BatchNormDirectedAxonsComponentAdapter<>(delegated.createBatchNormAxonsComponent(name, batchNormAxonsConfig));
	}

	@Override
	public <L extends Neurons, R extends Neurons> DirectedAxonsComponent<L, R, ?> createDirectedAxonsComponent(
			String name, Axons<L, R, ?> axons, AxonsContextConfigurer axonsContextConfigurer) {
		return new DirectedAxonsComponentAdapter<>(delegated.createDirectedAxonsComponent(name, axons, axonsContextConfigurer));
	}


	@Override
	public <N extends Neurons> DirectedAxonsComponent<N, N, ?> createPassThroughAxonsComponent(String name, N leftNeurons,
			N rightNeurons) {
		return delegated.createPassThroughAxonsComponent(name, leftNeurons, rightNeurons);
	}

	@Override
	public OneToManyDirectedComponent<?> createOneToManyDirectedComponent(SerializableIntSupplier targetComponentsCount) {
		return new OneToManyDirectedComponentAdapter<>(delegated.createOneToManyDirectedComponent(targetComponentsCount));
	}

	@Override
	public ManyToOneDirectedComponent<?> createManyToOneDirectedComponent(Neurons outputNeurons,
			PathCombinationStrategy pathCombinationStrategy) {
		return new ManyToOneDirectedComponentAdapter<>(delegated.createManyToOneDirectedComponent(outputNeurons, pathCombinationStrategy));
	}

	@Override
	public ManyToOneDirectedComponent<?> createManyToOneDirectedComponent3D(Neurons3D outputNeurons,
			PathCombinationStrategy pathCombinationStrategy) {
		return new ManyToOneDirectedComponentAdapter<>(delegated.createManyToOneDirectedComponent3D(outputNeurons, pathCombinationStrategy));
	}

	@Override
	public DifferentiableActivationFunctionComponent createDifferentiableActivationFunctionComponent(String name, Neurons neurons,
			DifferentiableActivationFunction differentiableActivationFunction) {
		return delegated.createDifferentiableActivationFunctionComponent(name, neurons, differentiableActivationFunction);
	}
	
	@Override
	public DifferentiableActivationFunctionComponent createDifferentiableActivationFunctionComponent(String name, Neurons neurons,
			ActivationFunctionType activationFunctionType, ActivationFunctionProperties activationFunctionProperties) {
		return delegated.createDifferentiableActivationFunctionComponent(name, neurons, activationFunctionType, activationFunctionProperties);
	}

	@Override
	public DefaultDirectedComponentChain createDirectedComponentChain(
			List<DefaultChainableDirectedComponent<?, ?>> sequentialComponents) {
		return delegated.createDirectedComponentChain(sequentialComponents);
	}

	@Override
	public DefaultDirectedComponentBipoleGraph createDirectedComponentBipoleGraph(String name, Neurons arg0, Neurons arg1,
			List<DefaultChainableDirectedComponent<?, ?>> parallelComponents, PathCombinationStrategy arg3) {
		return delegated.createDirectedComponentBipoleGraph(name, arg0, arg1, parallelComponents, arg3);
	}

	@Override
	public DefaultChainableDirectedComponent<?, ?> createComponent(String name, 
			Neurons leftNeurons, Neurons rightNeurons, NeuralComponentType neuralComponentType) {
		return delegated.createComponent(name, leftNeurons, rightNeurons, neuralComponentType);
	}

}
