package be.nabu.libs.types.binding.api;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.api.ComplexContent;

public interface UnmarshallableBinding {
	public ComplexContent unmarshal(InputStream input, Window [] windows, Value<?>...values) throws IOException, ParseException;
}
