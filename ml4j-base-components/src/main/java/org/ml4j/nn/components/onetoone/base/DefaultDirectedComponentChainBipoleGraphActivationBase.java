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
package org.ml4j.nn.components.onetoone.base;

import org.ml4j.nn.components.base.DefaultChainableDirectedComponentActivationBase;
import org.ml4j.nn.components.onetone.DefaultDirectedComponentChainBipoleGraph;
import org.ml4j.nn.components.onetone.DefaultDirectedComponentChainBipoleGraphActivation;
import org.ml4j.nn.neurons.NeuronsActivation;

/**
 * Default base class for an activation from a DefaultDirectedComponentChainBipoleGraph.
 * 
 * Encapsulates the activations from a forward propagation through a DefaultDirectedComponentChainBipoleGraph including the
 * output NeuronsActivation from the RHS of the DefaultDirectedComponentBipoleGraph.
 * 
 * @author Michael Lavelle
 *
 */
public abstract class DefaultDirectedComponentChainBipoleGraphActivationBase extends DefaultChainableDirectedComponentActivationBase<DefaultDirectedComponentChainBipoleGraph>
		implements DefaultDirectedComponentChainBipoleGraphActivation {
	
	public DefaultDirectedComponentChainBipoleGraphActivationBase(DefaultDirectedComponentChainBipoleGraph bipoleGraph, NeuronsActivation output) {
		super(bipoleGraph, output);
	}

}
