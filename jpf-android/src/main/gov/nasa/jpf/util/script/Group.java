// Copyright  (C) 2006 United States Government as represented by the

package gov.nasa.jpf.util.script;

public class Group extends ScriptElementContainer {

	public Group(ScriptElement parent, int line) {
		super(parent, line);
	}

	public String toString() {
		return toString("GROUP ");
	}

	public void process(ElementProcessor p) {
		p.process(this);
	}

}
