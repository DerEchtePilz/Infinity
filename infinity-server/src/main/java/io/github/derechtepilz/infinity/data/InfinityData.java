package io.github.derechtepilz.infinity.data;

import io.github.derechtepilz.infinity.Infinity;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class InfinityData extends Data {

	public InfinityData() {
	}

	@Override
	public MinecraftData getOtherGamemodeData() {
		return Infinity.getInstance().getMinecraftData();
	}

	@Override
	public BufferedWriter getWriter() {
		return super.getWriter("infinity-data");
	}

	@Override
	public BufferedReader getReader() {
		return super.getReader("infinity-data");
	}

}
