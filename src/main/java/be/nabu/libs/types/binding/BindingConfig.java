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

import javax.xml.bind.annotation.XmlAttribute;

public class BindingConfig {
	
	private String complexType;

	public BindingConfig() {
		// do nothing
	}

	public BindingConfig(String complexType) {
		this.complexType = complexType;
	}

	@XmlAttribute
	public String getComplexType() {
		return complexType;
	}
	public void setComplexType(String complexType) {
		this.complexType = complexType;
	}
}
