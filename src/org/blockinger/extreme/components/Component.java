package org.blockinger.extreme.components;

import org.blockinger.extreme.activities.GameActivity;

public abstract class Component {

	protected GameActivity host;
	
	public Component(GameActivity ga) {
		host = ga;
	}

	public void reconnect(GameActivity ga) {
		host = ga;
	}

	public void disconnect() {
		host = null;
	}
	
}
