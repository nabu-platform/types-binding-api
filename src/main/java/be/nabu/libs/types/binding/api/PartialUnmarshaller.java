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
