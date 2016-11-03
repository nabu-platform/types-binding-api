package be.nabu.libs.types.binding.api;

public class Window {
	
	/**
	 * The path to the field that has to be windowed (must be a list of ComplexType)
	 */
	private String path;
	
	/**
	 * The total size of the window, this is the max amount of items that should be in memory at all times
	 * It should also be the last items loaded, not necessarily in index-order
	 */
	private int size = 10;
	
	/**
	 * The amount of items that is read each time you need to move the window
	 */
	private int batchSize;

	public Window() {
		// auto construct
	}
	
	public Window(String path, int size, int batchSize) {
		if (size <= 0)
			throw new IllegalArgumentException("The size of a window must be at least 1");
		if (batchSize > size)
			throw new IllegalArgumentException("The batch size of a window can not exceed the size");
		this.path = path;
		this.size = size;
		this.batchSize = batchSize;
	}

	public String getPath() {
		return path;
	}

	public int getSize() {
		return size;
	}

	public int getBatchSize() {
		return batchSize;
	}
	
	@Override
	public String toString() {
		return "WINDOW[" + getPath() + ", " + getBatchSize() + ", " + getSize() + "]";
	}
}
