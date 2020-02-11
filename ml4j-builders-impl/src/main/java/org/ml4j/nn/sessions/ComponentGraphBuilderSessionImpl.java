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
package org.ml4j.nn.sessions;

import org.ml4j.nn.components.DirectedComponentsContext;
import org.ml4j.nn.components.NeuralComponent;
import org.ml4j.nn.components.builders.componentsgraph.InitialComponents3DGraphBuilder;
import org.ml4j.nn.components.builders.componentsgraph.InitialComponentsGraphBuilder;
import org.ml4j.nn.components.builders.initial.InitialComponents3DGraphBuilderImpl;
import org.ml4j.nn.components.builders.initial.InitialComponentsGraphBuilderImpl;
import org.ml4j.nn.components.factories.NeuralComponentFactory;
import org.ml4j.nn.neurons.Neurons;
import org.ml4j.nn.neurons.Neurons3D;

/**
 * Default ComponentGraphBuilderSession implementation for the creation of
 * Neural Component graphs.
 * 
 * @author Michael Lavelle
 *
 * @param <T> The type of NeuralComponent within the session.
 */
public class ComponentGraphBuilderSessionImpl<T extends NeuralComponent<?>> implements ComponentGraphBuilderSession<T> {

	private NeuralComponentFactory<T> neuralComponentFactory;
	private DirectedComponentsContext directedComponentsContext;

	public ComponentGraphBuilderSessionImpl(NeuralComponentFactory<T> neuralComponentFactory,
			DirectedComponentsContext directedComponentsContext) {
		this.neuralComponentFactory = neuralComponentFactory;
		this.directedComponentsContext = directedComponentsContext;
	}

	@Override
	public InitialComponents3DGraphBuilder<T> startWith3DNeurons(Neurons3D initialNeurons) {
		return new InitialComponents3DGraphBuilderImpl<>(neuralComponentFactory, directedComponentsContext,
				initialNeurons);
	}

	@Override
	public InitialComponentsGraphBuilder<T> startWithNeurons(Neurons initialNeurons) {
		return new InitialComponentsGraphBuilderImpl<>(neuralComponentFactory, directedComponentsContext,
				initialNeurons);
	}

	@Override
	public NeuralComponentFactory<T> getNeuralComponentFactory() {
		return neuralComponentFactory;
	}

}
