package be.nabu.libs.types.binding.api;

import java.io.IOException;
import java.io.OutputStream;

import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.api.ComplexContent;

public interface MarshallableBinding {
	public void marshal(OutputStream output, ComplexContent content, Value<?>...values) throws IOException;
}
