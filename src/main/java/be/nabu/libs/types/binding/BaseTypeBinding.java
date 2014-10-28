package be.nabu.libs.types.binding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.UUID;

import be.nabu.libs.property.api.Value;
import be.nabu.libs.resources.DynamicResource;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.binding.api.MarshallableBinding;
import be.nabu.libs.types.binding.api.UnmarshallableBinding;
import be.nabu.libs.types.binding.api.Window;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.containers.bytes.FileWrapper;

abstract public class BaseTypeBinding implements MarshallableBinding, UnmarshallableBinding {

	public static final String LARGE_DATA_FOLDER = "be.nabu.libs.types.binding.largeDataFolder";
	
	private File largeDataFolder;

	abstract protected ComplexContent unmarshal(ReadableResource resource, Window [] windows, Value<?>...values) throws IOException, ParseException;

	@Override
	public ComplexContent unmarshal(InputStream input, Window [] windows, Value<?>...values) throws IOException, ParseException {
		DynamicResource resource = null;
		// no windows, just use a memory backend (the default)
		if (windows.length == 0) {
			resource = new DynamicResource(IOUtils.wrap(input), "unmarshallable", "text/plain", false);
		}
		// if there are windows, use a file backend because we are expecting a large doc
		else {
			File targetFile = getLargeDataFolder() == null 
				? File.createTempFile("unmarshallable", ".txt")
				: new File(getLargeDataFolder(), "unmarshallable." + UUID.randomUUID().toString() + ".txt");
			resource = new DynamicResource(IOUtils.wrap(input), new FileWrapper(targetFile), "unmarshallable", "text/plain", false);
		}
		return unmarshal(resource, windows, values);
	}

	public File getLargeDataFolder() {
		if (largeDataFolder == null) {
			String value = System.getProperty(LARGE_DATA_FOLDER);
			if (value != null) {
				largeDataFolder = new File(value);
			}
		}
		return largeDataFolder;
	}

	public void setLargeDataFolder(File largeDataFolder) {
		this.largeDataFolder = largeDataFolder;
	}
}
