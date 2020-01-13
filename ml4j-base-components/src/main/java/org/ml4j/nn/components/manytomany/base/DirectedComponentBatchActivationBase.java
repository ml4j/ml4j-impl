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
package org.ml4j.nn.components.manytomany.base;

import java.util.List;
import java.util.stream.Collectors;

import org.ml4j.nn.components.manytomany.DefaultDirectedComponentBatchActivation;
import org.ml4j.nn.components.onetone.DefaultChainableDirectedComponentActivation;
import org.ml4j.nn.neurons.NeuronsActivation;

/**
 * Default base class for an activation from a DefaultDirectedComponentChainBatch instance.
 * 
 * @author Michael Lavelle
 *
 */
public abstract class DirectedComponentBatchActivationBase implements DefaultDirectedComponentBatchActivation {

	protected List<DefaultChainableDirectedComponentActivation> activations;
	
	/**
	 * @param activations The list of DefaultDirectedComponentChainActivation instances generated by the DefaultDirectedComponentChainBatch
	 */
	public DirectedComponentBatchActivationBase(List<DefaultChainableDirectedComponentActivation> activations) {
		this.activations = activations;
	}

	@Override
	public List<NeuronsActivation> getOutput() {
		return activations.stream().map(a -> a.getOutput()).collect(Collectors.toList());
	}

	@Override
	public List<DefaultChainableDirectedComponentActivation> getActivations() {
		return activations;
	}

}