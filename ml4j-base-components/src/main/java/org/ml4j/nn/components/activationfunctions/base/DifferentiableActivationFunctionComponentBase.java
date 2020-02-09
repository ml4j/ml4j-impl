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
package org.ml4j.nn.components.activationfunctions.base;

import java.util.Arrays;
import java.util.List;

import org.ml4j.nn.activationfunctions.ActivationFunctionType;
import org.ml4j.nn.components.DirectedComponentsContext;
import org.ml4j.nn.components.NeuralComponentBaseType;
import org.ml4j.nn.components.NeuralComponentType;
import org.ml4j.nn.components.activationfunctions.DifferentiableActivationFunctionComponent;
import org.ml4j.nn.components.onetone.DefaultChainableDirectedComponent;
import org.ml4j.nn.neurons.Neurons;
import org.ml4j.nn.neurons.NeuronsActivationContext;
import org.ml4j.nn.neurons.NeuronsActivationContextImpl;

/**
 * 
 * 
 * @author Michael Lavelle
 */
public abstract class DifferentiableActivationFunctionComponentBase implements DifferentiableActivationFunctionComponent {

	/**
	 * Generated serialization id.
	 */
	private static final long serialVersionUID = -6033017517698579773L;
	
	protected ActivationFunctionType activationFunctionType;
	protected Neurons neurons;
	protected String name;
	
	public DifferentiableActivationFunctionComponentBase(String name, Neurons neurons, ActivationFunctionType activationFunctionType){
		this.activationFunctionType = activationFunctionType;
		this.neurons = neurons;
		this.name = name;
	}

	@Override
	public List<DefaultChainableDirectedComponent<?, ?>> decompose() {
		return Arrays.asList(this);
	}

	@Override
	public NeuronsActivationContext getContext(DirectedComponentsContext context) {
		return context.getContext(this, () -> new NeuronsActivationContextImpl(context.getMatrixFactory(), context.isTrainingContext()));
	}

	@Override
	public ActivationFunctionType getActivationFunctionType() {
		return activationFunctionType;
	}

	@Override
	public NeuralComponentType getComponentType() {
		return NeuralComponentType.createSubType(NeuralComponentBaseType.ACTIVATION_FUNCTION, 
				activationFunctionType.getQualifiedId());
	}

	@Override
	public Neurons getInputNeurons() {
		return neurons;
	}

	@Override
	public Neurons getOutputNeurons() {
		return neurons;
	}

	@Override
	public String getName() {
		return name;
	}
	
	
	
}
