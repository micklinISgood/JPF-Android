package gov.nasa.jpf.android;

class ComponentEntry {
	int id;
	String name;
	int componentRef;
	int toplevelRef;

	public ComponentEntry() {
	}

	ComponentEntry(String name, int toplevelRef, int componentRef) {
		this.name = name;
		this.componentRef = componentRef;
		this.toplevelRef = toplevelRef;
	}

	ComponentEntry(String name, int id, int toplevelRef, int componentRef) {
		this.id = id;
		this.name = name;
		this.componentRef = componentRef;
		this.toplevelRef = toplevelRef;
	}

}