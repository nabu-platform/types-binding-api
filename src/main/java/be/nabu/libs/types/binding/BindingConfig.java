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
