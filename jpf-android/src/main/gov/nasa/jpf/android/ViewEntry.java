package gov.nasa.jpf.android;

import android.view.View;

/**
 * Keeps a reference to a {@link View} object.
 * 
 * @author "Heila van der Merwe"
 * 
 */
class ViewEntry {
	/**
	 * The unique id of a {@link View} as defined in the R.java file else
	 * generated in native Window class.
	 */
	private int id;

	/**
	 * The unique name of the {@link View} as defined in the R.java file else
	 * generated in native Window class.
	 */
	private String name;

	/** Reference to the actual {@link View} object */
	private int componentRef;

	/** Referenec to the {@link View} object's parent */
	private int toplevelRef;

	public ViewEntry() {
	}

	public ViewEntry(String name, int toplevelRef, int componentRef) {
		this.name = name;
		this.componentRef = componentRef;
		this.toplevelRef = toplevelRef;
	}

	public ViewEntry(String name, int id, int toplevelRef, int componentRef) {
		this.id = id;
		this.name = name;
		this.componentRef = componentRef;
		this.toplevelRef = toplevelRef;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getComponentRef() {
		return componentRef;
	}

	public void setComponentRef(int componentRef) {
		this.componentRef = componentRef;
	}

	public int getToplevelRef() {
		return toplevelRef;
	}

	public void setToplevelRef(int toplevelRef) {
		this.toplevelRef = toplevelRef;
	}

}