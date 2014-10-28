package be.nabu.libs.types.binding;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * Implements a linked hashmap that serves as a limit-sized cache
 * 
 * @author alex
 */
public class LinkedHashMapCache<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = -8020577453324745628L;

	private int maxSize;
	
	public LinkedHashMapCache(int maxSize) {
		this.maxSize = maxSize;
	}
	
	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		return size() > maxSize;
	}
}
