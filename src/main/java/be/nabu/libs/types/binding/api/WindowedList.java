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
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.types.TypeUtils;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.binding.LinkedHashMapCache;
import be.nabu.utils.io.IOUtils;

/**
 * There is a horrible hack in this class
 * In the original version the windowed list provided a list of ComplexContent
 * But since then java bean support has become a big thing and this doesn't work so well anymore
 * In the java bean will be a List<MyObject> and this will actually be a WindowedList<ComplexContent> which causes class cast exceptions at some point
 * This is actually a rather big problem because the types api has no direct correlation to java beans for complex types, so you can't ask it what its bean representation is
 * However, suppose you have a BeanInstance in the background with a List<MyObject> and you do this:
 * 
 * ComplexContent compatibleContent = ...
 * mybean/myobjects[0] = compatibleContent;
 * 
 * The bean layer will actually wrap it correctly to be a MyObject instance instead of a ComplexContent.
 * Combine this with the fact that (unless the window has size 0 which is not allowed), you will always set at least _some_ objects in the list at load time
 * So currently we use the type of whatever objects you set to cast the complex content to
 */
public class WindowedList<T> implements List<T> {
	
	private Window window;
	
	private ReadableResource resource;
	
	/**
	 * The elements that are currently parsed as per their respective indexes
	 */
	private Map<Integer, T> cache;
	
	/**
	 * This keeps track off the offsets for each item by their index
	 * The reader is moved to the appropriate offset to perform the reading
	 * The offsets are learned through analysis by the parser
	 */
	private Map<Integer, Long> offsets = new HashMap<Integer, Long>();
	
	private Class<T> beanType;
	
	/**
	 * The binding used to do further parsing
	 */
	private PartialUnmarshaller unmarshaller;
	
	private int size = 0;
	
	/**
	 * 
	 * @param window
	 * @param unmarshaller
	 * @param reader
	 * @param windows nested windows
	 */
	public WindowedList(ReadableResource resource, Window window, PartialUnmarshaller unmarshaller) {
		this.window = window;
		this.unmarshaller = unmarshaller;
		this.cache = new LinkedHashMapCache<Integer, T>(window.getSize());
		this.resource = resource;
	}
	
	public void setOffset(int index, long offset) {
		offsets.put(index, offset);
		if (index >= this.size) {
			this.size = index + 1;
		}
	}

	@Override
	public boolean add(T arg0) {
		// you can add "null" to artificially grow the list size
		if (arg0 == null) {
			size++;
			return true;
		}
		else
			throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public void add(int arg0, T arg1) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public boolean contains(Object arg0) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}
	
	@SuppressWarnings("unchecked")
	private Class<T> getBeanType() {
		if (beanType == null) {
			for (Object object : cache.values()) {
				if (object != null) {
					if (object instanceof ComplexContent) {
						beanType = (Class<T>) ComplexContent.class;
						break;
					}
					else {
						beanType = (Class<T>) object.getClass();
						break;
					}
				}
			}
			if (beanType == null) {
				throw new IllegalStateException("The windowed list has no initialized objects, can not determine type");
			}
		}
		return beanType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException("The index " + index + " is beyond the size of the windowed list: " + size);
		}
		if (!cache.containsKey(index)) {
			// we need to reparse the entity and add it
			try {
				// can't read more records than there are
				int batchSize = Math.min(window.getBatchSize(), size() - index);
				int read = 0;

				while (read < batchSize) {
					List<ComplexContent> content = unmarshaller.unmarshal(IOUtils.toInputStream(resource.getReadable()), offsets.get(index), batchSize);
					// no data found, this means the backend resource has gone out of sync with this list
					if (content.size() == 0) {
						throw new IllegalStateException("The backend resource no longer contains all the necessary data");
					}
					for (int i = 0; i < content.size(); i++) {
						if (ComplexContent.class.isAssignableFrom(getBeanType())) {
							cache.put(index + i + read, (T) content.get(i));
						}
						else {
							cache.put(index + i + read, TypeUtils.getAsBean(content.get(i), getBeanType()));
						}
					}
					read += content.size();
				}
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		return cache.get(index);
	}

	@Override
	public int indexOf(Object arg0) {
		throw new RuntimeException("Not supported for windowed lists");
	}

	@Override
	public boolean isEmpty() {
		return cache.size() == 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int internalIndex = 0;
			
			@Override
			public boolean hasNext() {
				return internalIndex < size;
			}

			@Override
			public T next() {
				return get(internalIndex++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Not supported for windowed lists");
			}
		};
	}

	@Override
	public int lastIndexOf(Object arg0) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public ListIterator<T> listIterator(int arg0) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public T remove(int arg0) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public T set(int arg0, T arg1) {
		if (arg0 >= size) {
			size = arg0 + 1;
		}
		T returnValue = cache.get(arg0);
		cache.put(arg0, arg1);
		return returnValue;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public List<T> subList(int arg0, int arg1) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}

	@Override
	public <S> S[] toArray(S[] arg0) {
		throw new UnsupportedOperationException("Not supported for windowed lists");
	}	
}
