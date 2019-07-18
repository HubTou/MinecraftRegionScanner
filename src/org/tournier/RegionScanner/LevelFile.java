package org.tournier.RegionScanner;

import java.util.HashMap;
import java.util.Iterator;
import net.querz.nbt.*;

public class LevelFile
{
	//////////////////////////////////////////////////////////////////////////////
	public static BlocksAndItemsMap getDefaultBlocksAndItemsMap() throws Exception
	//////////////////////////////////////////////////////////////////////////////
	{
		BlocksAndItemsMap map = new BlocksAndItemsMap();

		map.put("air", 0);
		map.put("stone", 1);
		map.put("grass", 2);
		map.put("dirt", 3);
		map.put("cobblestone", 4);
		map.put("planks", 5);
		map.put("sapling", 6);
		map.put("bedrock", 7);
		map.put("flowing_water", 8);
		map.put("water", 9);
		map.put("flowing_lava", 10);
		map.put("lava", 11);
		map.put("sand", 12);
		map.put("gravel", 13);
		map.put("gold_ore", 14);
		map.put("iron_ore", 15);
		map.put("coal_ore", 16);
		map.put("log", 17);
		map.put("leaves", 18);
		map.put("sponge", 19);
		map.put("glass", 20);
		map.put("lapis_ore", 21);
		map.put("lapis_block", 22);
		map.put("dispenser", 23);
		map.put("sandstone", 24);
		map.put("noteblock", 25);
		map.put("bed", 26);
		map.put("golden_rail", 27);
		map.put("detector_rail", 28);
		map.put("sticky_piston", 29);
		map.put("web", 30);
		map.put("tallgrass", 31);
		map.put("deadbush", 32);
		map.put("piston", 33);
		map.put("piston_head", 34);
		map.put("wool", 35);
		map.put("piston_extension", 36);
		map.put("yellow_flower", 37);
		map.put("red_flower", 38);
		map.put("brown_mushroom", 39);
		map.put("red_mushroom", 40);
		map.put("gold_block", 41);
		map.put("iron_block", 42);
		map.put("double_stone_slab", 43);
		map.put("stone_slab", 44);
		map.put("brick_block", 45);
		map.put("tnt", 46);
		map.put("bookshelf", 47);
		map.put("mossy_cobblestone", 48);
		map.put("obsidian", 49);
		map.put("torch", 50);
		map.put("fire", 51);
		map.put("mob_spawner", 52);
		map.put("oak_stairs", 53);
		map.put("chest", 54);
		map.put("redstone_wire", 55);
		map.put("diamond_ore", 56);
		map.put("diamond_block", 57);
		map.put("crafting_table", 58);
		map.put("wheat", 59);
		map.put("farmland", 60);
		map.put("furnace", 61);
		map.put("lit_furnace", 62);
		map.put("standing_sign", 63);
		map.put("wooden_door", 64);
		map.put("ladder", 65);
		map.put("rail", 66);
		map.put("stone_stairs", 67);
		map.put("wall_sign", 68);
		map.put("lever", 69);
		map.put("stone_pressure_plate", 70);
		map.put("iron_door", 71);
		map.put("wooden_pressure_plate", 72);
		map.put("redstone_ore", 73);
		map.put("lit_redstone_ore", 74);
		map.put("unlit_redstone_torch", 75);
		map.put("redstone_torch", 76);
		map.put("stone_button", 77);
		map.put("snow_layer", 78);
		map.put("ice", 79);
		map.put("snow", 80);
		map.put("cactus", 81);
		map.put("clay", 82);
		map.put("reeds", 83);
		map.put("jukebox", 84);
		map.put("fence", 85);
		map.put("pumpkin", 86);
		map.put("netherrack", 87);
		map.put("soul_sand", 88);
		map.put("glowstone", 89);
		map.put("portal", 90);
		map.put("lit_pumpkin", 91);
		map.put("cake", 92);
		map.put("unpowered_repeater", 93);
		map.put("powered_repeater", 94);
		map.put("stained_glass", 95);
		map.put("trapdoor", 96);
		map.put("monster_egg", 97);
		map.put("stonebrick", 98);
		map.put("brown_mushroom_block", 99);
		map.put("red_mushroom_block", 100);
		map.put("iron_bars", 101);
		map.put("glass_pane", 102);
		map.put("melon_block", 103);
		map.put("pumpkin_stem", 104);
		map.put("melon_stem", 105);
		map.put("vine", 106);
		map.put("fence_gate", 107);
		map.put("brick_stairs", 108);
		map.put("stone_brick_stairs", 109);
		map.put("mycelium", 110);
		map.put("waterlily", 111);
		map.put("nether_brick", 112);
		map.put("nether_brick_fence", 113);
		map.put("nether_brick_stairs", 114);
		map.put("nether_wart", 115);
		map.put("enchanting_table", 116);
		map.put("brewing_stand", 117);
		map.put("cauldron", 118);
		map.put("end_portal", 119);
		map.put("end_portal_frame", 120);
		map.put("end_stone", 121);
		map.put("dragon_egg", 122);
		map.put("redstone_lamp", 123);
		map.put("lit_redstone_lamp", 124);
		map.put("double_wooden_slab", 125);
		map.put("wooden_slab", 126);
		map.put("cocoa", 127);
		map.put("sandstone_stairs", 128);
		map.put("emerald_ore", 129);
		map.put("ender_chest", 130);
		map.put("tripwire_hook", 131);
		map.put("tripwire", 132);
		map.put("emerald_block", 133);
		map.put("spruce_stairs", 134);
		map.put("birch_stairs", 135);
		map.put("jungle_stairs", 136);
		map.put("command_block", 137);
		map.put("beacon", 138);
		map.put("cobblestone_wall", 139);
		map.put("flower_pot", 140);
		map.put("carrots", 141);
		map.put("potatoes", 142);
		map.put("wooden_button", 143);
		map.put("skull", 144);
		map.put("anvil", 145);
		map.put("trapped_chest", 146);
		map.put("light_weighted_pressure_plate", 147);
		map.put("heavy_weighted_pressure_plate", 148);
		map.put("unpowered_comparator", 149);
		map.put("powered_comparator", 150);
		map.put("daylight_detector", 151);
		map.put("redstone_block", 152);
		map.put("quartz_ore", 153);
		map.put("hopper", 154);
		map.put("quartz_block", 155);
		map.put("quartz_stairs", 156);
		map.put("activator_rail", 157);
		map.put("dropper", 158);
		map.put("stained_hardened_clay", 159);
		map.put("stained_glass_pane", 160);
		map.put("leaves2", 161);
		map.put("log2", 162);
		map.put("acacia_stairs", 163);
		map.put("dark_oak_stairs", 164);
		map.put("hay_block", 170);
		map.put("carpet", 171);
		map.put("hardened_clay", 172);
		map.put("coal_block", 173);
		map.put("packed_ice", 174);
		map.put("double_plant", 175);

		map.put("stone", 1);
		map.put("grass", 2);
		map.put("dirt", 3);
		map.put("cobblestone", 4);
		map.put("planks", 5);
		map.put("sapling", 6);
		map.put("bedrock", 7);
		map.put("flowing_water", 8);
		map.put("water", 9);
		map.put("flowing_lava", 10);
		map.put("lava", 11);
		map.put("sand", 12);
		map.put("gravel", 13);
		map.put("gold_ore", 14);
		map.put("iron_ore", 15);
		map.put("coal_ore", 16);
		map.put("log", 17);
		map.put("leaves", 18);
		map.put("sponge", 19);
		map.put("glass", 20);
		map.put("lapis_ore", 21);
		map.put("lapis_block", 22);
		map.put("dispenser", 23);
		map.put("sandstone", 24);
		map.put("noteblock", 25);
		map.put("golden_rail", 27);
		map.put("detector_rail", 28);
		map.put("sticky_piston", 29);
		map.put("web", 30);
		map.put("tallgrass", 31);
		map.put("deadbush", 32);
		map.put("piston", 33);
		map.put("wool", 35);
		map.put("yellow_flower", 37);
		map.put("red_flower", 38);
		map.put("brown_mushroom", 39);
		map.put("red_mushroom", 40);
		map.put("gold_block", 41);
		map.put("iron_block", 42);
		map.put("double_stone_slab", 43);
		map.put("stone_slab", 44);
		map.put("brick_block", 45);
		map.put("tnt", 46);
		map.put("bookshelf", 47);
		map.put("mossy_cobblestone", 48);
		map.put("obsidian", 49);
		map.put("torch", 50);
		map.put("fire", 51);
		map.put("mob_spawner", 52);
		map.put("oak_stairs", 53);
		map.put("chest", 54);
		map.put("diamond_ore", 56);
		map.put("diamond_block", 57);
		map.put("crafting_table", 58);
		map.put("farmland", 60);
		map.put("furnace", 61);
		map.put("lit_furnace", 62);
		map.put("ladder", 65);
		map.put("rail", 66);
		map.put("stone_stairs", 67);
		map.put("lever", 69);
		map.put("stone_pressure_plate", 70);
		map.put("wooden_pressure_plate", 72);
		map.put("redstone_ore", 73);
		map.put("redstone_torch", 76);
		map.put("stone_button", 77);
		map.put("snow_layer", 78);
		map.put("ice", 79);
		map.put("snow", 80);
		map.put("cactus", 81);
		map.put("clay", 82);
		map.put("jukebox", 84);
		map.put("fence", 85);
		map.put("pumpkin", 86);
		map.put("netherrack", 87);
		map.put("soul_sand", 88);
		map.put("glowstone", 89);
		map.put("portal", 90);
		map.put("lit_pumpkin", 91);
		map.put("stained_glass", 95);
		map.put("trapdoor", 96);
		map.put("monster_egg", 97);
		map.put("stonebrick", 98);
		map.put("brown_mushroom_block", 99);
		map.put("red_mushroom_block", 100);
		map.put("iron_bars", 101);
		map.put("glass_pane", 102);
		map.put("melon_block", 103);
		map.put("vine", 106);
		map.put("fence_gate", 107);
		map.put("brick_stairs", 108);
		map.put("stone_brick_stairs", 109);
		map.put("mycelium", 110);
		map.put("waterlily", 111);
		map.put("nether_brick", 112);
		map.put("nether_brick_fence", 113);
		map.put("nether_brick_stairs", 114);
		map.put("enchanting_table", 116);
		map.put("end_portal", 119);
		map.put("end_portal_frame", 120);
		map.put("end_stone", 121);
		map.put("dragon_egg", 122);
		map.put("redstone_lamp", 123);
		map.put("double_wooden_slab", 125);
		map.put("wooden_slab", 126);
		map.put("cocoa", 127);
		map.put("sandstone_stairs", 128);
		map.put("emerald_ore", 129);
		map.put("ender_chest", 130);
		map.put("tripwire_hook", 131);
		map.put("emerald_block", 133);
		map.put("spruce_stairs", 134);
		map.put("birch_stairs", 135);
		map.put("jungle_stairs", 136);
		map.put("command_block", 137);
		map.put("beacon", 138);
		map.put("cobblestone_wall", 139);
		map.put("carrots", 141);
		map.put("potatoes", 142);
		map.put("wooden_button", 143);
		map.put("anvil", 145);
		map.put("trapped_chest", 146);
		map.put("light_weighted_pressure_plate", 147);
		map.put("heavy_weighted_pressure_plate", 148);
		map.put("daylight_detector", 151);
		map.put("redstone_block", 152);
		map.put("quartz_ore", 153);
		map.put("hopper", 154);
		map.put("quartz_block", 155);
		map.put("quartz_stairs", 156);
		map.put("activator_rail", 157);
		map.put("dropper", 158);
		map.put("stained_hardened_clay", 159);
		map.put("stained_glass_pane", 160);
		map.put("leaves2", 161);
		map.put("log2", 162);
		map.put("acacia_stairs", 163);
		map.put("dark_oak_stairs", 164);
		map.put("hay_block", 170);
		map.put("carpet", 171);
		map.put("hardened_clay", 172);
		map.put("coal_block", 173);
		map.put("packed_ice", 174);
		map.put("double_plant", 175);
		map.put("iron_shovel", 256);
		map.put("iron_pickaxe", 257);
		map.put("iron_axe", 258);
		map.put("flint_and_steel", 259);
		map.put("apple", 260);
		map.put("bow", 261);
		map.put("arrow", 262);
		map.put("coal", 263);
		map.put("diamond", 264);
		map.put("iron_ingot", 265);
		map.put("gold_ingot", 266);
		map.put("iron_sword", 267);
		map.put("wooden_sword", 268);
		map.put("wooden_shovel", 269);
		map.put("wooden_pickaxe", 270);
		map.put("wooden_axe", 271);
		map.put("stone_sword", 272);
		map.put("stone_shovel", 273);
		map.put("stone_pickaxe", 274);
		map.put("stone_axe", 275);
		map.put("diamond_sword", 276);
		map.put("diamond_shovel", 277);
		map.put("diamond_pickaxe", 278);
		map.put("diamond_axe", 279);
		map.put("stick", 280);
		map.put("bowl", 281);
		map.put("mushroom_stew", 282);
		map.put("golden_sword", 283);
		map.put("golden_shovel", 284);
		map.put("golden_pickaxe", 285);
		map.put("golden_axe", 286);
		map.put("string", 287);
		map.put("feather", 288);
		map.put("gunpowder", 289);
		map.put("wooden_hoe", 290);
		map.put("stone_hoe", 291);
		map.put("iron_hoe", 292);
		map.put("diamond_hoe", 293);
		map.put("golden_hoe", 294);
		map.put("wheat_seeds", 295);
		map.put("wheat", 296);
		map.put("bread", 297);
		map.put("leather_helmet", 298);
		map.put("leather_chestplate", 299);
		map.put("leather_leggings", 300);
		map.put("leather_boots", 301);
		map.put("chainmail_helmet", 302);
		map.put("chainmail_chestplate", 303);
		map.put("chainmail_leggings", 304);
		map.put("chainmail_boots", 305);
		map.put("iron_helmet", 306);
		map.put("iron_chestplate", 307);
		map.put("iron_leggings", 308);
		map.put("iron_boots", 309);
		map.put("diamond_helmet", 310);
		map.put("diamond_chestplate", 311);
		map.put("diamond_leggings", 312);
		map.put("diamond_boots", 313);
		map.put("golden_helmet", 314);
		map.put("golden_chestplate", 315);
		map.put("golden_leggings", 316);
		map.put("golden_boots", 317);
		map.put("flint", 318);
		map.put("porkchop", 319);
		map.put("cooked_porkchop", 320);
		map.put("painting", 321);
		map.put("golden_apple", 322);
		map.put("sign", 323);
		map.put("wooden_door", 324);
		map.put("bucket", 325);
		map.put("water_bucket", 326);
		map.put("lava_bucket", 327);
		map.put("minecart", 328);
		map.put("saddle", 329);
		map.put("iron_door", 330);
		map.put("redstone", 331);
		map.put("snowball", 332);
		map.put("boat", 333);
		map.put("leather", 334);
		map.put("milk_bucket", 335);
		map.put("brick", 336);
		map.put("clay_ball", 337);
		map.put("reeds", 338);
		map.put("paper", 339);
		map.put("book", 340);
		map.put("slime_ball", 341);
		map.put("chest_minecart", 342);
		map.put("furnace_minecart", 343);
		map.put("egg", 344);
		map.put("compass", 345);
		map.put("fishing_rod", 346);
		map.put("clock", 347);
		map.put("glowstone_dust", 348);
		map.put("fish", 349);
		map.put("cooked_fished", 350);
		map.put("dye", 351);
		map.put("bone", 352);
		map.put("sugar", 353);
		map.put("cake", 354);
		map.put("bed", 355);
		map.put("repeater", 356);
		map.put("cookie", 357);
		map.put("filled_map", 358);
		map.put("shears", 359);
		map.put("melon", 360);
		map.put("pumpkin_seeds", 361);
		map.put("melon_seeds", 362);
		map.put("beef", 363);
		map.put("cooked_beef", 364);
		map.put("chicken", 365);
		map.put("cooked_chicken", 366);
		map.put("rotten_flesh", 367);
		map.put("ender_pearl", 368);
		map.put("blaze_rod", 369);
		map.put("ghast_tear", 370);
		map.put("gold_nugget", 371);
		map.put("nether_wart", 372);
		map.put("potion", 373);
		map.put("glass_bottle", 374);
		map.put("spider_eye", 375);
		map.put("fermented_spider_eye", 376);
		map.put("blaze_powder", 377);
		map.put("magma_cream", 378);
		map.put("brewing_stand", 379);
		map.put("cauldron", 380);
		map.put("ender_eye", 381);
		map.put("speckled_melon", 382);
		map.put("spawn_egg", 383);
		map.put("experience_bottle", 384);
		map.put("fire_charge", 385);
		map.put("writable_book", 386);
		map.put("written_book", 387);
		map.put("emerald", 388);
		map.put("item_frame", 389);
		map.put("flower_pot", 390);
		map.put("carrot", 391);
		map.put("potato", 392);
		map.put("baked_potato", 393);
		map.put("poisonous_potato", 394);
		map.put("map", 395);
		map.put("golden_carrot", 396);
		map.put("skull", 397);
		map.put("carrot_on_a_stick", 398);
		map.put("nether_star", 399);
		map.put("pumpkin_pie", 400);
		map.put("fireworks", 401);
		map.put("firework_charge", 402);
		map.put("enchanted_book", 403);
		map.put("comparator", 404);
		map.put("netherbrick", 405);
		map.put("quartz", 406);
		map.put("tnt_minecart", 407);
		map.put("hopper_minecart", 408);
		map.put("iron_horse_armor", 417);
		map.put("golden_horse_armor", 418);
		map.put("diamond_horse_armor", 419);
		map.put("lead", 420);
		map.put("name_tag", 421);
		map.put("command_block_minecart", 422);
		map.put("record_13", 2256);
		map.put("record_cat", 2257);
		map.put("record_blocks", 2258);
		map.put("record_chirp", 2259);
		map.put("record_far", 2260);
		map.put("record_mall", 2261);
		map.put("record_mellohi", 2262);
		map.put("record_stal", 2263);
		map.put("record_strad", 2264);
		map.put("record_ward", 2265);
		map.put("record_11", 2266);
		map.put("record_wait", 2267);

		return map;
	}

	///////////////////////////////////////////////////////////////////////////////////////
	public static BlocksAndItemsMap getBlocksAndItemsMap(String fromLevel) throws Exception
	///////////////////////////////////////////////////////////////////////////////////////
	{
		BlocksAndItemsMap map = new BlocksAndItemsMap();

		Tag<?> levelDat = null;
		try
		{
			levelDat = NBTUtil.readTag(fromLevel);
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage());
		}
		CompoundTag levelRoot = (CompoundTag) levelDat;

		// Analyse Data tag
		if (RegionScanner.logLevel > 0)
		{
			System.out.println("DEBUG: level.dat file=" + fromLevel);
			if (RegionScanner.logLevel > 2)
				System.out.println("  DEBUG: keySet=" + levelRoot.keySet() + "\n");

			if (levelRoot.containsKey("Data"))
			{
				CompoundTag levelData = levelRoot.getCompoundTag("Data");
				if (RegionScanner.logLevel > 2)
					System.out.println("  DEBUG: Data keySet=" + levelData.keySet() + "\n");
				System.out.println("  DEBUG: Data/version=" + (levelData.containsKey("version")? levelData.getInt("version") : "[not found]"));
				System.out.println("  DEBUG: Data/LevelName=" + (levelData.containsKey("LevelName")? levelData.getString("LevelName") : "[not found]"));
				System.out.println("  DEBUG: Data/generatorName=" + (levelData.containsKey("generatorName")? levelData.getString("generatorName") : "[not found]"));
				System.out.println("  DEBUG: Data/generatorVersion=" + (levelData.containsKey("generatorVersion")? levelData.getInt("generatorVersion") : "[not found]"));
				System.out.println("  DEBUG: Data/initialized=" + (levelData.containsKey("initialized")? levelData.getByte("initialized") : "[not found]"));
				System.out.println("  DEBUG: Data/RandomSeed=" + (levelData.containsKey("RandomSeed")? levelData.getLong("RandomSeed") : "[not found]"));
			}
		}

		if (levelRoot.containsKey("FML"))
		{
			CompoundTag levelFml = levelRoot.getCompoundTag("FML");
			if (RegionScanner.logLevel > 2)
				System.out.println("  DEBUG: FML keySet=" + levelFml.keySet() + "\n");

			// Analyse FML/ModList tag
			if (RegionScanner.logLevel > 0)
			{
				ListTag levelModList = levelFml.getListTag("ModList");
				int n = 1;
				for (Iterator i = levelModList.iterator(); i.hasNext(); n++)
				{
					CompoundTag keyValue = (CompoundTag) i.next();
					if (keyValue.containsKey("ModId") && keyValue.containsKey("ModVersion"))
						System.out.println("  DEBUG: FML/ModList/#" + n + "=" + keyValue.getString("ModId") + " " + keyValue.getString("ModVersion"));
					else
						throw new Exception("Missing Key or Value in ModList ListCompound");
				}
			}

			// Get FML/ItemData keys/values
			if (levelFml.containsKey("ItemData"))
			{
				if (RegionScanner.logLevel > 1)
					System.out.println("  DEBUG: Blocks & Items name=value:");

				ListTag levelItemData = levelFml.getListTag("ItemData");
				for (Iterator i = levelItemData.iterator(); i.hasNext(); )
				{
					CompoundTag keyValue = (CompoundTag) i.next();
					if (keyValue.containsKey("K") && keyValue.containsKey("K"))
					{
						try
						{
							map.put(keyValue.getString("K"), keyValue.getInt("V"));
						}
						catch (Exception e)
						{
							throw new Exception(e.getMessage());
						}
					}
					else
						throw new Exception("Missing Key or Value in ItemData ListCompound");
				}
			}
		}
		else // a vanilla Minecraft level.dat file implying standard blocks & items values
		{
			return getDefaultBlocksAndItemsMap();
		}

		return map;
	}
}
