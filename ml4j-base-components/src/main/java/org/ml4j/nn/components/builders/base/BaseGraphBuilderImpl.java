package org.ml4j.nn.components.builders.base;

import java.util.ArrayList;
import java.util.List;

import org.ml4j.Matrix;
import org.ml4j.nn.activationfunctions.DifferentiableActivationFunction;
import org.ml4j.nn.axons.AxonsContext;
import org.ml4j.nn.components.DefaultChainableDirectedComponent;
import org.ml4j.nn.components.DefaultDirectedComponentChain;
import org.ml4j.nn.components.DirectedComponentChain;
import org.ml4j.nn.components.axons.DirectedAxonsComponent;
import org.ml4j.nn.components.builders.BaseGraphBuilderState;
import org.ml4j.nn.components.builders.axons.AxonsBuilder;
import org.ml4j.nn.components.builders.axons.AxonsPermitted;
import org.ml4j.nn.components.builders.axons.UncompletedFullyConnectedAxonsBuilder;
import org.ml4j.nn.components.builders.axons.UncompletedFullyConnectedAxonsBuilderImpl;
import org.ml4j.nn.components.builders.common.ComponentsContainer;
import org.ml4j.nn.components.builders.componentsgraph.ComponentsGraphNeurons;
import org.ml4j.nn.components.builders.synapses.SynapsesAxonsGraphBuilder;
import org.ml4j.nn.components.builders.synapses.SynapsesAxonsGraphBuilderImpl;
import org.ml4j.nn.components.builders.synapses.SynapsesPermitted;
import org.ml4j.nn.components.defaults.DefaultDirectedComponentChainImpl;
import org.ml4j.nn.components.factories.DirectedComponentFactory;
import org.ml4j.nn.neurons.Neurons;
import org.ml4j.nn.neurons.NeuronsActivation;

public abstract class BaseGraphBuilderImpl<C extends AxonsBuilder> implements AxonsPermitted<C>, SynapsesPermitted<C>, AxonsBuilder {

	protected DirectedComponentFactory directedComponentFactory;
	
	protected BaseGraphBuilderState builderState;
	
	protected BaseGraphBuilderState initialBuilderState;
	
	private List<DefaultDirectedComponentChain> chains;
	private List<Neurons> endNeurons;
	
	protected List<DefaultChainableDirectedComponent<?, ?>>	components;
	
	
	public BaseGraphBuilderImpl(DirectedComponentFactory directedComponentFactory, BaseGraphBuilderState builderState, List<DefaultChainableDirectedComponent<?, ?>>	components) {
		this.builderState = builderState;
		this.components = components;
		this.directedComponentFactory = directedComponentFactory;
		this.initialBuilderState = new 	BaseGraphBuilderStateImpl(builderState.getComponentsGraphNeurons().getCurrentNeurons());
		this.chains = new ArrayList<>();
		this.endNeurons = new ArrayList<>();
	}
	
	@Override
	public List<DefaultChainableDirectedComponent<?, ?>> getComponents() {
		return components;
	}
	
	@Override
	public ComponentsContainer<Neurons> getAxonsBuilder() {
		return this;
	}
	
	public BaseGraphBuilderState getBuilderState() {
		return builderState;
	}

	@Override
	public ComponentsGraphNeurons<Neurons> getComponentsGraphNeurons() {
		return builderState.getComponentsGraphNeurons();
	}

	@Override
	public Matrix getConnectionWeights() {
		return builderState.getConnectionWeights();
	}

	public AxonsBuilder withConnectionWeights(Matrix connectionWeights) {
		builderState.setConnectionWeights(connectionWeights);
		return this;
	}
	
	public abstract C getBuilder();

	public void addAxonsIfApplicable() {
		if ((builderState.getFullyConnectedAxonsBuilder() != null) && builderState.getComponentsGraphNeurons().getRightNeurons() != null) {
			Neurons leftNeurons = builderState.getComponentsGraphNeurons().getCurrentNeurons();
			if (builderState.getComponentsGraphNeurons().hasBiasUnit() && !leftNeurons.hasBiasUnit()) {
				leftNeurons = new Neurons(builderState.getComponentsGraphNeurons().getCurrentNeurons().getNeuronCountExcludingBias(), true);
			}

			DirectedAxonsComponent<Neurons, Neurons> axonsComponent = directedComponentFactory.createFullyConnectedAxonsComponent(leftNeurons, 
					builderState.getComponentsGraphNeurons().getRightNeurons(), builderState.getConnectionWeights(), builderState.getBiases());
					
			if (builderState.getFullyConnectedAxonsBuilder().getDirectedComponentsContext() != null && 
					builderState.getFullyConnectedAxonsBuilder().getAxonsContextConfigurer() != null) {
				AxonsContext axonsContext = axonsComponent.getContext(builderState.getFullyConnectedAxonsBuilder().getDirectedComponentsContext(), 0);
				builderState.getFullyConnectedAxonsBuilder().getAxonsContextConfigurer().accept(axonsContext);
			}
			
			components.add(axonsComponent);
		
			builderState.setFullyConnectedAxonsBuilder(null);
			builderState.getComponentsGraphNeurons().setCurrentNeurons(builderState.getComponentsGraphNeurons().getRightNeurons());
			builderState.getComponentsGraphNeurons().setRightNeurons(null);
		}	
	}
	
	@Override
	public UncompletedFullyConnectedAxonsBuilder<C> withFullyConnectedAxons() {
		addAxonsIfApplicable();
		builderState.setConnectionWeights(null);
		builderState.getComponentsGraphNeurons().setHasBiasUnit(false);
		UncompletedFullyConnectedAxonsBuilder<C> axonsBuilder = 
				new UncompletedFullyConnectedAxonsBuilderImpl<>(this::getBuilder, builderState.getComponentsGraphNeurons().getCurrentNeurons());
		builderState.setFullyConnectedAxonsBuilder(axonsBuilder);
		builderState.getComponentsGraphNeurons().setHasBiasUnit(false);
		return axonsBuilder;
	}

	@Override
	public SynapsesAxonsGraphBuilder<C> withSynapses() {
		addAxonsIfApplicable();
		SynapsesAxonsGraphBuilder<C> synapsesBuilder = new SynapsesAxonsGraphBuilderImpl<>(this::getBuilder, directedComponentFactory, builderState, new ArrayList<>());
		builderState.setSynapsesBuilder(synapsesBuilder);
		return synapsesBuilder;
	}
	
	protected void addActivationFunction(DifferentiableActivationFunction activationFunction) {
		addAxonsIfApplicable();
		components.add(directedComponentFactory.createDifferentiableActivationFunctionComponent(activationFunction));
	}
	
	public DirectedComponentChain<NeuronsActivation, ?, ?, ?> getComponentChain() {
		addAxonsIfApplicable();
		return new DefaultDirectedComponentChainImpl(components);
	}

	public AxonsBuilder withBiasUnit() {
		builderState.getComponentsGraphNeurons().setHasBiasUnit(true);
		return this;
	}
	
	@Override
	public void addComponents(
			List<DefaultChainableDirectedComponent<?, ?>> components) {
		this.components.addAll(components);
	}

	@Override
	public void addComponent(
			DefaultChainableDirectedComponent<?, ?> component) {
		this.components.add(component);		
	}

	public List<DefaultDirectedComponentChain> getChains() {
		return chains;
	}

	public List<Neurons> getEndNeurons() {
		return endNeurons;
	}
	
	
}