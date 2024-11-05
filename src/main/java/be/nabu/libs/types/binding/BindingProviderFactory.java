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
