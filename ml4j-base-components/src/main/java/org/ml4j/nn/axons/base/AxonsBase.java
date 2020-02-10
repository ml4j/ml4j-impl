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
package org.ml4j.nn.axons.base;

import org.ml4j.nn.axons.Axons;
import org.ml4j.nn.axons.AxonsType;
import org.ml4j.nn.neurons.Neurons;

public abstract class AxonsBase<L extends Neurons, R extends Neurons, A extends Axons<L, R, A>> implements Axons<L, R, A>{

	/**
	 * Default serialization id.
	 */
	private static final long serialVersionUID = 1L;
	
	protected L leftNeurons;
	protected R rightNeurons;
	protected AxonsType axonsType;
	
	
	public AxonsBase(AxonsType axonsType, L leftNeurons, R rightNeurons) {
		this.leftNeurons = leftNeurons;
		this.rightNeurons = rightNeurons;
		this.axonsType = axonsType;
	}

	@Override
	public L getLeftNeurons() {
		return leftNeurons;
	}

	@Override
	public R getRightNeurons() {
		return rightNeurons;
	}

	public AxonsType getAxonsType() {
		return axonsType;
	}

}
