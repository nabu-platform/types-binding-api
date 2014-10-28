package be.nabu.libs.types.binding.api;

import java.util.Set;

import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;

/**
 * Currently unused interface
 * May require this for interface generation
 * 
 * @author alex
 *
 */
public interface TypeBinding {
	public Value<?> [] getProperties();
	public void setProperty(Value<?>...values);
	public Set<Property<?>> getSupportedProperties(Value<?>...properties);
}
