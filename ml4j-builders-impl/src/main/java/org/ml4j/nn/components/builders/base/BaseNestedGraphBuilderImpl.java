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
package org.ml4j.nn.components.builders.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.ml4j.nn.axons.FullyConnectedAxonsConfig;
import org.ml4j.nn.components.NeuralComponent;
import org.ml4j.nn.components.builders.BaseGraphBuilderState;
import org.ml4j.nn.components.builders.axons.AxonsBuilder;
import org.ml4j.nn.components.builders.common.ComponentsContainer;
import org.ml4j.nn.components.builders.common.PathEnder;
import org.ml4j.nn.components.factories.NeuralComponentFactory;
import org.ml4j.nn.components.manytoone.PathCombinationStrategy;
import org.ml4j.nn.neurons.Neurons;

public abstract class BaseNestedGraphBuilderImpl<P extends ComponentsContainer<Neurons, T>, C extends AxonsBuilder<T>, T extends NeuralComponent<?>>
		extends BaseGraphBuilderImpl<C, T> implements PathEnder<P, C> {

	protected Supplier<P> parentGraph;
	private boolean pathEnded;
	private boolean pathsEnded;

	public BaseNestedGraphBuilderImpl(Supplier<P> parentGraph, NeuralComponentFactory<T> directedComponentFactory,
			BaseGraphBuilderState builderState, 
			List<T> components) {
		super(directedComponentFactory, builderState, components);
		this.parentGraph = parentGraph;
	}

	protected abstract C createNewNestedGraphBuilder();

	protected void completeNestedGraph(boolean addSkipConnection) {

		if (!pathEnded) {
			Neurons initialNeurons = getComponentsGraphNeurons().getCurrentNeurons();
			addAxonsIfApplicable();
			Neurons endNeurons = getComponentsGraphNeurons().getCurrentNeurons();
			T chain = directedComponentFactory.createDirectedComponentChain(getComponents());
			parentGraph.get().getChains().add(chain);
			parentGraph.get().getComponentsGraphNeurons()
					.setCurrentNeurons(getComponentsGraphNeurons().getCurrentNeurons());
			parentGraph.get().getComponentsGraphNeurons()
					.setRightNeurons(getComponentsGraphNeurons().getRightNeurons());
			if (addSkipConnection) {
				if (initialNeurons.getNeuronCountIncludingBias() == endNeurons.getNeuronCountIncludingBias()) {

					T skipConnectionAxons = directedComponentFactory.createPassThroughAxonsComponent("SkipConnection-" + UUID.randomUUID().toString(),initialNeurons,
							endNeurons);
					T skipConnection = directedComponentFactory
							.createDirectedComponentChain(Arrays.asList(skipConnectionAxons));
					this.parentGraph.get().getChains().add(skipConnection);
				} else {
					T skipConnectionAxons = directedComponentFactory.createFullyConnectedAxonsComponent("SkipConnection-" + UUID.randomUUID().toString(),
							FullyConnectedAxonsConfig.create(new Neurons(initialNeurons.getNeuronCountExcludingBias(), true), endNeurons), null, null);
					T skipConnection = directedComponentFactory
							.createDirectedComponentChain(Arrays.asList(skipConnectionAxons));
					this.parentGraph.get().getChains().add(skipConnection);
				}
			}
			pathEnded = true;
		}
	}

	protected void completeNestedGraphs(String name, PathCombinationStrategy pathCombinationStrategy) {
		if (!pathsEnded) {
			parentGraph.get().getComponentsGraphNeurons()
					.setCurrentNeurons(getComponentsGraphNeurons().getCurrentNeurons());
			parentGraph.get().getComponentsGraphNeurons()
					.setRightNeurons(getComponentsGraphNeurons().getRightNeurons());
			parentGraph.get().getComponentsGraphNeurons().setHasBiasUnit(getComponentsGraphNeurons().hasBiasUnit());
			List<T> chainsList = new ArrayList<>();
			chainsList.addAll(this.parentGraph.get().getChains());
			// ComponentChainBatchDefinition batch =
			// directedComponentFactory.createDirectedComponentChainBatch(chainsList);
			Neurons graphInputNeurons = chainsList.get(0).getInputNeurons();
			Neurons graphOutputNeurons = parentGraph.get().getComponentsGraphNeurons().getCurrentNeurons();
			parentGraph.get().addComponent(directedComponentFactory.createDirectedComponentBipoleGraph(name,
					graphInputNeurons,
					new Neurons(graphOutputNeurons.getNeuronCountExcludingBias(), graphOutputNeurons.hasBiasUnit()),
					chainsList, pathCombinationStrategy));
			pathsEnded = true;
			parentGraph.get().getEndNeurons().clear();
			parentGraph.get().getChains().clear();
		}
	}

	@Override
	public P endParallelPaths(String name,PathCombinationStrategy pathCombinationStrategy) {
		completeNestedGraph(false);
		completeNestedGraphs(name, pathCombinationStrategy);
		return parentGraph.get();
	}

	@Override
	public C withPath() {
		completeNestedGraph(false);
		return createNewNestedGraphBuilder();
	}
}
