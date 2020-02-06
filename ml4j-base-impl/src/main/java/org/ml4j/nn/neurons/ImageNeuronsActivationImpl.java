package org.ml4j.nn.neurons;

import org.ml4j.FloatModifier;
import org.ml4j.FloatPredicate;
import org.ml4j.Matrix;
import org.ml4j.MatrixFactory;
import org.ml4j.images.Image;
import org.ml4j.images.Images;
import org.ml4j.images.MultiChannelImages;
import org.ml4j.images.SingleChannelImages;
import org.ml4j.nn.neurons.format.ImageNeuronsActivationFormat;
import org.ml4j.nn.neurons.format.features.DimensionScope;

public class ImageNeuronsActivationImpl implements ImageNeuronsActivation {

	private final Neurons3D neurons;
	private Images images;
	private boolean immutable;
	private Integer exampleCount;
	private ImageNeuronsActivationFormat format;

	public ImageNeuronsActivationImpl(Neurons3D neurons, Images images,
			ImageNeuronsActivationFormat format, boolean immutable) {
		this.neurons = neurons;
		this.images = images;
		setImmutable(immutable);
		if (!format.equals(ImageNeuronsActivationFormat.ML4J_DEFAULT_IMAGE_FORMAT)) {
			throw new UnsupportedOperationException("Format not yet supported:" + format);
		}
		this.exampleCount = images.getExamples();
		this.format = format;
	}
	
	public ImageNeuronsActivationImpl(Neurons3D neurons, Image image,
			ImageNeuronsActivationFormat format, boolean immutable) {
		this.neurons = neurons;
		this.images = image.asImages();
		setImmutable(immutable);
		if (!format.equals(ImageNeuronsActivationFormat.ML4J_DEFAULT_IMAGE_FORMAT)) {
			throw new UnsupportedOperationException("Format not yet supported:" + format);
		}
		this.exampleCount = images.getExamples();
		this.format = format;
	}

	public ImageNeuronsActivationImpl(Matrix activations, Neurons3D neurons,
			ImageNeuronsActivationFormat format, boolean immutable) {
		if (!format.equals(ImageNeuronsActivationFormat.ML4J_DEFAULT_IMAGE_FORMAT)) {
			throw new UnsupportedOperationException("Format not yet supported:" + format);
		}
		this.neurons = neurons;
		if (neurons.getDepth() == 1) {
			images = new SingleChannelImages(activations.getRowByRowArray(), 0, neurons.getHeight(), neurons.getWidth(),
					0, 0, activations.getColumns());
		} else {
			images = new MultiChannelImages(activations.getRowByRowArray(), neurons.getDepth(), neurons.getHeight(),
					neurons.getWidth(), 0, 0, activations.getColumns());
		}
		setImmutable(immutable);
		this.exampleCount = images.getExamples();
		this.format = format;
	}

	@Override
	public void addInline(MatrixFactory matrixFactory, NeuronsActivation other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void combineFeaturesInline(NeuronsActivation other, MatrixFactory matrixFactory) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void applyValueModifier(FloatPredicate condition, FloatModifier modifier) {
		getImages().applyValueModifier(condition, modifier);
	}

	@Override
	public void applyValueModifier(FloatModifier modifier) {
		getImages().applyValueModifier(modifier);
	}

	@Override
	public ImageNeuronsActivation asImageNeuronsActivation(Neurons3D neurons, DimensionScope scope) {
		if (neurons.equals(this.neurons)) {
			// TODO - scope
			return this;
		} else {
			throw new IllegalArgumentException("Neurons do not match");
		}
	}

	@Override
	public void close() {
		if (images != null && !images.isClosed()) {
			exampleCount = images.getExamples();
			images.close();
			images = null;
		}
	}

	@Override
	public NeuronsActivation dup() {
		return new ImageNeuronsActivationImpl(neurons, images.dup(), getFormat(), false);
	}

	@Override
	public Matrix getActivations(MatrixFactory matrixFactory) {
		Matrix activations = matrixFactory.createMatrixFromRowsByRowsArray(getRows(), getColumns(), images.getData());
		activations.setImmutable(immutable);
		return activations;
	}

	@Override
	public int getColumns() {
		return getExampleCount();
	}

	@Override
	public int getExampleCount() {
		return images != null && !images.isClosed() ? images.getExamples() : exampleCount;
	}

	@Override
	public int getFeatureCount() {
		return neurons.getNeuronCountExcludingBias();
	}

	@Override
	public NeuronsActivationFeatureOrientation getFeatureOrientation() {
		return getFormat().getFeatureOrientation();
	}

	@Override
	public int getRows() {
		return getFeatureCount();
	}

	@Override
	public boolean isImmutable() {
		return immutable;
	}

	@Override
	public void reshape(int arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setImmutable(boolean immutable) {
		this.immutable = immutable;
	}

	@Override
	public Images getImages() {
		if (images == null || images.isClosed()) {
			throw new IllegalStateException();
		}
		return images;
	}

	@Override
	public Neurons3D getNeurons() {
		return neurons;
	}

	@Override
	public Matrix im2ColConv(MatrixFactory matrixFactory, int filterHeight, int filterWidth, int strideHeight,
			int strideWidth, int paddingHeight, int paddingWidth) {
		Images imageWithPadding = getImages().softDup();
		imageWithPadding.setPaddingHeight(paddingHeight);
		imageWithPadding.setPaddingWidth(paddingWidth);
		return imageWithPadding.im2colConvExport(matrixFactory, filterHeight, filterWidth, strideHeight, strideWidth);
	}

	@Override
	public Matrix im2ColPool(MatrixFactory matrixFactory, int filterHeight, int filterWidth, int strideHeight,
			int strideWidth, int paddingHeight, int paddingWidth) {
		Images imageWithPadding = getImages().softDup();
		imageWithPadding.setPaddingHeight(paddingHeight);
		imageWithPadding.setPaddingWidth(paddingWidth);
		return imageWithPadding.im2colPoolExport(matrixFactory, filterHeight, filterWidth, strideHeight, strideWidth);
	}

	@Override
	public ImageNeuronsActivationFormat getFormat() {
		return format;
	}

}
