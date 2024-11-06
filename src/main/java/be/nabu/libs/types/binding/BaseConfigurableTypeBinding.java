/*
* Copyright (C) 2014 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.libs.types.binding;

import java.io.IOException;
import java.text.ParseException;

import be.nabu.libs.property.api.Value;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.DefinedTypeResolver;
import be.nabu.libs.types.api.Type;
import be.nabu.libs.types.binding.api.Window;

abstract public class BaseConfigurableTypeBinding<T extends BindingConfig> extends BaseTypeBinding {
	
	private T config;
	private DefinedTypeResolver definedTypeResolver;
	
	public BaseConfigurableTypeBinding(DefinedTypeResolver definedTypeResolver, T config) {
		this.config = config;
		this.definedTypeResolver = definedTypeResolver;
	}
	
	protected ComplexType getComplexType() {
		if (config == null || config.getComplexType() == null) {
			throw new NullPointerException("Can not find binding config or the complex type is not defined in the binding config");
		}
		Type type = definedTypeResolver.resolve(config.getComplexType());
		if (type == null) {
			throw new IllegalArgumentException("Can not resolve type " + config.getComplexType());
		}
		else if (!(type instanceof ComplexType)) {
			throw new IllegalArgumentException("The type " + config.getComplexType() + " is not complex");
		}
		return (ComplexType) type;
	}
	
	public T getConfig() {
		return config;
	}
	
	protected ComplexContent unmarshal(ReadableResource resource, Window [] windows, Value<?>...values) throws IOException, ParseException {
		return unmarshal(resource, getComplexType(), windows, values);
	}

	abstract protected ComplexContent unmarshal(ReadableResource resource, ComplexType type, Window [] windows, Value<?>...values) throws IOException, ParseException;
}
