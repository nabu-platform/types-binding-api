package be.nabu.libs.types.binding.api;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import be.nabu.libs.types.api.ComplexContent;

/**
 * This allows the code to unmarshal a part of the data
 * The inputstream is positioned at the beginning while the offset is whatever offset you gave the windowed list when parsing
 * The batch size allows you to unmarshal multiple parts in one go
 * If this can not be done by the marshaller, just return one instance, the code will keep asking until it has enough parts
 * 
 * It might seem odd that the offset is given to the partial unmarshaller instead of just skipping to the correct position in the inputstream but this is because the offset is not always in bytes
 * For example xml parsing & flat file parsing gives you offsets in characters.
 */
public interface PartialUnmarshaller {
	public List<ComplexContent> unmarshal(InputStream input, long offset, int batchSize) throws IOException, ParseException;
}
