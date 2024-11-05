package be.nabu.libs.types.binding.api;

import java.nio.charset.Charset;

import be.nabu.libs.property.api.Value;

public interface DynamicBindingProvider extends BindingProvider {
	public UnmarshallableBinding getDynamicUnmarshallableBinding(Charset charset, Value<?>...values);
	public MarshallableBinding getDynamicMarshallableBinding(Charset charset, Value<?>...values);
}
