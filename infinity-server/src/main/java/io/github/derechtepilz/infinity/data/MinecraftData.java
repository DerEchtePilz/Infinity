package io.github.derechtepilz.infinity.data;

import io.github.derechtepilz.infinity.Infinity;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class MinecraftData extends Data {

	public MinecraftData() {
	}

	@Override
	public InfinityData getOtherGamemodeData() {
		return Infinity.getInstance().getInfinityData();
	}

	@Override
	public BufferedWriter getWriter() {
		return super.getWriter("minecraft-data");
	}

	@Override
	public BufferedReader getReader() {
		return super.getReader("minecraft-data");
	}

}
