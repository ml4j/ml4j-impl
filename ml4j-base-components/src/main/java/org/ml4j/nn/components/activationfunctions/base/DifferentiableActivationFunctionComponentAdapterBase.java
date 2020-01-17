package org.ml4j.nn.components.activationfunctions.base;

import org.ml4j.nn.activationfunctions.DifferentiableActivationFunction;
import org.ml4j.nn.components.activationfunctions.DifferentiableActivationFunctionComponentAdapter;
import org.ml4j.nn.neurons.Neurons;

public abstract class DifferentiableActivationFunctionComponentAdapterBase
		extends DifferentiableActivationFunctionComponentBase implements DifferentiableActivationFunctionComponentAdapter {
	
	/**
	 * Default serialization id.
	 */
	private static final long serialVersionUID = 1L;
	
	protected DifferentiableActivationFunction activationFunction;

	public DifferentiableActivationFunctionComponentAdapterBase(Neurons neurons,
			DifferentiableActivationFunction activationFunction) {
		super(neurons, activationFunction.getActivationFunctionType());
		this.activationFunction = activationFunction;
	}

	@Override
	public DifferentiableActivationFunction getActivationFunction() {
		return activationFunction;
	}
}
