package be.nabu.libs.types.binding.api;


public interface LargeUnmarshallableBinding extends UnmarshallableBinding {
	/**
	 * Create a partial unmarshaller for the given window
	 */
	public PartialUnmarshaller createPartialUnmarshaller(Window bindingWindow, Window...remainingWindows);
}
