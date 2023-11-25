package io.github.derechtepilz;

import io.github.derechtepilz.separation.GamemodeSeparator;

public class InfinityAPI {

	private static InfinityAPIServer server;

	public static InfinityAPIServer getServer() {
		return server;
	}

	public static GamemodeSeparator getGamemodeSeparator() {
		return server.getGamemodeSeparator();
	}

}
