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

public class Window {
	
	/**
	 * The path to the field that has to be windowed (must be a list of ComplexType)
	 */
	private String path;
	
	/**
	 * The total size of the window, this is the max amount of items that should be in memory at all times
	 * It should also be the last items loaded, not necessarily in index-order
	 */
	private int size = 10;
	
	/**
	 * The amount of items that is read each time you need to move the window
	 */
	private int batchSize;

	public Window() {
		// auto construct
	}
	
	public Window(String path, int size, int batchSize) {
		if (size <= 0)
			throw new IllegalArgumentException("The size of a window must be at least 1");
		if (batchSize > size)
			throw new IllegalArgumentException("The batch size of a window can not exceed the size");
		this.path = path;
		this.size = size;
		this.batchSize = batchSize;
	}

	public String getPath() {
		return path;
	}

	public int getSize() {
		return size;
	}

	public int getBatchSize() {
		return batchSize;
	}
	
	@Override
	public String toString() {
		return "WINDOW[" + getPath() + ", " + getBatchSize() + ", " + getSize() + "]";
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	
}
