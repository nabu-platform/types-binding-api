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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.types.binding.api.BindingProvider;

public class BindingProviderFactory {

	private static BindingProviderFactory instance;
	private boolean spiLoaded = false;
	
	public static BindingProviderFactory getInstance() {
		if (instance == null) {
			synchronized(BindingProviderFactory.class) {
				if (instance == null) {
					instance = new BindingProviderFactory();
				}
			}
		}
		return instance;
	}
	
	private List<BindingProvider> bindingProviders = new ArrayList<BindingProvider>();
	
	public void addConverter(BindingProvider converter) {
		this.bindingProviders.add(converter);
	}
	
	public void removeConverter(BindingProvider converter) {
		this.bindingProviders.remove(converter);
	}
	
	public BindingProvider getProviderFor(String contentType) {
		if (contentType == null) {
			throw new NullPointerException("Must provide a content type");
		}
		for (BindingProvider available : getProviders()) {
			if (contentType.equals(available.getContentType())) {
				return available;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<BindingProvider> getProviders() {
		if (!spiLoaded) {
			synchronized(this) {
				if (!spiLoaded) {
					try {
						// let's try this with custom service loading based on a configuration
						Class<?> clazz = getClass().getClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
						Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
						bindingProviders.addAll((List<BindingProvider>) declaredMethod.invoke(null, BindingProvider.class));
					}
					catch (ClassNotFoundException e) {
						// ignore, the framework is not present
					}
					catch (NoSuchMethodException e) {
						// corrupt framework?
						throw new RuntimeException(e);
					}
					catch (SecurityException e) {
						throw new RuntimeException(e);
					}
					catch (IllegalAccessException e) {
						// ignore
					}
					catch (InvocationTargetException e) {
						// ignore
					}
					// if there are still no instances, fall back to SPI
					// it is actually possible that you _are_ using the custom service loader, but 
					if (bindingProviders.isEmpty()) {
						ServiceLoader<BindingProvider> serviceLoader = ServiceLoader.load(BindingProvider.class);
						for (BindingProvider converter : serviceLoader) {
							bindingProviders.add(converter);
						}
					}
					spiLoaded = true;
				}
			}
		}
		return bindingProviders;
	}
	
	@SuppressWarnings("unused")
	private void activate() {
		instance = this;
	}
	@SuppressWarnings("unused")
	private void deactivate() {
		instance = null;
	}
	
}
