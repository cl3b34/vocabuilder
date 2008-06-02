package br.boirque.vocabuilder.model;

/**
 * VO to hold application preferences
 * @author cleber.goncalves
 *
 */
public class Preference {
	private String name;
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
