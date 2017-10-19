package be.nabu.libs.types.binding.api;

import java.nio.charset.Charset;
import java.util.Collection;

import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.api.ComplexType;

public interface BindingProvider {
	public String getContentType();
	public Collection<Property<?>> getSupportedProperties();
	public UnmarshallableBinding getUnmarshallableBinding(ComplexType type, Charset charset, Value<?>...values);
	public MarshallableBinding getMarshallableBinding(ComplexType type, Charset charset, Value<?>...values);
}
