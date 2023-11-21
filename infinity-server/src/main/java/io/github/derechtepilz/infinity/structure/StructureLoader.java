package io.github.derechtepilz.infinity.structure;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derechtepilz.infinity.Infinity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StructureLoader {

	private final String world;
	private final InputStream structureFile;

	public StructureLoader(String world, InputStream structureFile) {
		this.world = world;
		this.structureFile = structureFile;

		generateStructure();
	}

	private void generateStructure() {
		try {
			World world = Bukkit.getWorld(new NamespacedKey(Infinity.NAME, this.world));
			BufferedReader structureReader = new BufferedReader(new InputStreamReader(structureFile));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = structureReader.readLine()) != null) {
				builder.append(line);
			}
			JsonArray jsonArray = JsonParser.parseString(builder.toString()).getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject blockInformationJson = jsonArray.get(i).getAsJsonObject();
				int locX = blockInformationJson.get("locX").getAsInt();
				int locY = blockInformationJson.get("locY").getAsInt();
				int locZ = blockInformationJson.get("locZ").getAsInt();
				Material material = Material.matchMaterial(blockInformationJson.get("materialType").getAsString());
				world.setType(locX, locY, locZ, material);
				// Change BlockData
				Block placedBlock = world.getBlockAt(locX, locY, locZ);
				BlockState placedBlockState = placedBlock.getState();
				BlockData placedBlockData = placedBlockState.getBlockData();

				if (blockInformationJson.has("direction")) {
					Directional directional = (Directional) placedBlockData;
					directional.setFacing(BlockFace.valueOf(blockInformationJson.get("direction").getAsString()));
					placedBlockData = directional;
				}

				if (blockInformationJson.has("shapeType")) {
					Stairs shaped = (Stairs) placedBlockData;
					shaped.setShape(Stairs.Shape.valueOf(blockInformationJson.get("shapeType").getAsString()));
					placedBlockData = shaped;
				}

				if (blockInformationJson.has("bisectedHalf")) {
					Bisected bisected = (Bisected) placedBlockData;
					bisected.setHalf(Bisected.Half.valueOf(blockInformationJson.get("bisectedHalf").getAsString()));
					placedBlockData = bisected;
				}

				if (blockInformationJson.has("slabHalf")) {
					Slab slab = (Slab) placedBlockData;
					slab.setType(Slab.Type.valueOf(blockInformationJson.get("slabHalf").getAsString()));
					placedBlockData = slab;
				}

				if (blockInformationJson.has("persistent")) {
					Leaves leaves = (Leaves) placedBlockData;
					leaves.setPersistent(blockInformationJson.get("persistent").getAsBoolean());
					placedBlockData = leaves;
				}

				placedBlockState.setBlockData(placedBlockData);
				placedBlockState.update();
			}
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There has been a problem while reading resource files. Generated Infinity worlds may be empty. Please report this, alongside any helpful information like a full server log, etc.");
		}
	}

}
