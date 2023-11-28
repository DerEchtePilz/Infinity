package io.github.derechtepilz;

import io.github.derechtepilz.separation.GamemodeSeparator;

public class InfinityAPI {

	private static InfinityAPIServer server;

	public static InfinityAPIServer getServer() {
		return server;
	}

	public static void setServer(InfinityAPIServer server) {
		if (InfinityAPI.server != null) {
			throw new IllegalStateException("InfinityAPIServer is already defined!");
		}
		InfinityAPI.server = server;
	}

	public static GamemodeSeparator getGamemodeSeparator() {
		return server.getGamemodeSeparator();
	}

}
