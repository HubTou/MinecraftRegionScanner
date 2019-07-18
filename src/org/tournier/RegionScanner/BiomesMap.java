package org.tournier.RegionScanner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.ImageIO;

public class BiomesMap
{
	private static HashMap<Integer, String> vanillaBiomesNameFromValue = new HashMap<Integer, String>();
	private static HashMap<String, String> lotrBiomesNameFromRGB = new HashMap<String, String>();
	private static HashMap<String, Integer> lotrBiomesValueFromRGB = new HashMap<String, Integer>();
	private static HashMap<Integer, String> lotrBiomesNameFromValue = new HashMap<Integer, String>();
	private static HashMap<Integer, String> lotrBiomesVariantsNameFromValue = new HashMap<Integer, String>();

	///////////////////////////////////////
	private static void initVanillaBiomes()
	///////////////////////////////////////
	{
		// From https://minecraft.gamepedia.com/index.php?title=Biome/ID
		// I used the latest names (the early January 2015 ones are indicated in comments)
		vanillaBiomesNameFromValue.put(0, "Ocean");
		vanillaBiomesNameFromValue.put(1, "Plains");
		vanillaBiomesNameFromValue.put(2, "Desert");
		vanillaBiomesNameFromValue.put(3, "Mountains"); // Extreme Hills
		vanillaBiomesNameFromValue.put(4, "Forest");
		vanillaBiomesNameFromValue.put(5, "Taiga");
		vanillaBiomesNameFromValue.put(6, "Swamp"); // Swampland
		vanillaBiomesNameFromValue.put(7, "River");
		vanillaBiomesNameFromValue.put(8, "Nether"); // Hell
		vanillaBiomesNameFromValue.put(9, "The End");
		vanillaBiomesNameFromValue.put(10, "Frozen Ocean");
		vanillaBiomesNameFromValue.put(11, "Frozen River");
		vanillaBiomesNameFromValue.put(12, "Snowy Tundra"); // Ice Plains
		vanillaBiomesNameFromValue.put(13, "Snowy Mountains"); // Ice Mountains
		vanillaBiomesNameFromValue.put(14, "Mushroom Fields"); // Mushroom Island
		vanillaBiomesNameFromValue.put(15, "Mushroom Fields Shore"); // Mushroom Island Shore
		vanillaBiomesNameFromValue.put(16, "Beach");
		vanillaBiomesNameFromValue.put(17, "Desert Hills");
		vanillaBiomesNameFromValue.put(18, "Wooded Hills"); // Forest Hills
		vanillaBiomesNameFromValue.put(19, "Taiga Hills");
		vanillaBiomesNameFromValue.put(20, "Mountain Edge"); // Extreme Hills Edge
		vanillaBiomesNameFromValue.put(21, "Jungle");
		vanillaBiomesNameFromValue.put(22, "Jungle Hills");
		vanillaBiomesNameFromValue.put(23, "Jungle Edge");
		vanillaBiomesNameFromValue.put(24, "Deep Ocean");
		vanillaBiomesNameFromValue.put(25, "Stone Shore"); // Stone Beach
		vanillaBiomesNameFromValue.put(26, "Snowy Beach"); // Cold Beach
		vanillaBiomesNameFromValue.put(27, "Birch Forest");
		vanillaBiomesNameFromValue.put(28, "Birch Forest Hills");
		vanillaBiomesNameFromValue.put(29, "Dark Forest"); // Roofed Forest
		vanillaBiomesNameFromValue.put(30, "Snowy Taiga"); // Cold Taiga
		vanillaBiomesNameFromValue.put(31, "Snowy Taiga Hills"); // Cold Taiga Hills
		vanillaBiomesNameFromValue.put(32, "Giant Tree Taiga"); // Mega Taiga
		vanillaBiomesNameFromValue.put(33, "Giant Tree Taiga Hills"); // Mega Taiga Hills
		vanillaBiomesNameFromValue.put(34, "Wooded Mountains"); // Extreme Hills+
		vanillaBiomesNameFromValue.put(35, "Savanna");
		vanillaBiomesNameFromValue.put(36, "Savanna Plateau");
		vanillaBiomesNameFromValue.put(37, "Badlands"); // Mesa
		vanillaBiomesNameFromValue.put(38, "Wooded Badlands Plateau"); // Mesa Plateau F
		vanillaBiomesNameFromValue.put(39, "Badlands Plateau"); // Mesa Plateau
		vanillaBiomesNameFromValue.put(40, "Small End Islands");
		vanillaBiomesNameFromValue.put(41, "End Midlands");
		vanillaBiomesNameFromValue.put(42, "End Highlands");
		vanillaBiomesNameFromValue.put(43, "End Barrens");
		vanillaBiomesNameFromValue.put(44, "Warm Ocean");
		vanillaBiomesNameFromValue.put(45, "Lukewarm Ocean");
		vanillaBiomesNameFromValue.put(46, "Cold Ocean");
		vanillaBiomesNameFromValue.put(47, "Deep Warm Ocean7");
		vanillaBiomesNameFromValue.put(48, "Deep Lukewarm Ocean");
		vanillaBiomesNameFromValue.put(49, "Deep Cold Ocean");
		vanillaBiomesNameFromValue.put(50, "Deep Frozen Ocean");
		vanillaBiomesNameFromValue.put(127, "The Void");
		vanillaBiomesNameFromValue.put(129, "Sunflower Plains");
		vanillaBiomesNameFromValue.put(130, "Desert Lakes"); // Desert M
		vanillaBiomesNameFromValue.put(131, "Gravelly Mountains"); // Extreme Hills M
		vanillaBiomesNameFromValue.put(132, "Flower Forest");
		vanillaBiomesNameFromValue.put(133, "Taiga Mountains"); // Taiga M
		vanillaBiomesNameFromValue.put(134, "Swamp Hills"); // Swampland M
		vanillaBiomesNameFromValue.put(140, "Ice Spikes"); // Ice Plains Spikes
		vanillaBiomesNameFromValue.put(149, "Modified Jungle"); // Jungle M
		vanillaBiomesNameFromValue.put(151, "Modified Jungle Edge"); // Jungle Edge M
		vanillaBiomesNameFromValue.put(155, "Tall Birch Forest"); // Birch Forest M
		vanillaBiomesNameFromValue.put(156, "Tall Birch Hills"); // Birch Forest Hills M
		vanillaBiomesNameFromValue.put(157, "Dark Forest Hills"); // Roofed Forest M
		vanillaBiomesNameFromValue.put(158, "Snowy Taiga Mountains"); // Cold Taiga M
		vanillaBiomesNameFromValue.put(160, "Giant Spruce Taiga"); // Mega Spruce Taiga
		vanillaBiomesNameFromValue.put(161, "Giant Spruce Taiga Hills"); // Redwood Taiga Hills M
		vanillaBiomesNameFromValue.put(162, "Gravelly Mountains+"); // Extreme Hills+ M
		vanillaBiomesNameFromValue.put(163, "Shattered Savanna"); // Savanna M
		vanillaBiomesNameFromValue.put(164, "Shattered Savanna Plateau"); // Savanna Plateau M
		vanillaBiomesNameFromValue.put(165, "Eroded Badlands"); // Mesa (Bryce)
		vanillaBiomesNameFromValue.put(166, "Modified Wooded Badlands Plateau"); // Mesa Plateau F M
		vanillaBiomesNameFromValue.put(167, "Modified Badlands Plateau"); // Mesa Plateau M
		vanillaBiomesNameFromValue.put(168, "Bamboo Jungle");
		vanillaBiomesNameFromValue.put(169, "Bamboo Jungle Hills");
	}

	///////////////////////////////////////////////////
	public static String getVanillaBiomeName(int value)
	///////////////////////////////////////////////////
	{
		if (vanillaBiomesNameFromValue.isEmpty())
			initVanillaBiomes();

		String name = vanillaBiomesNameFromValue.get(value);
		if (name == null)
			name = "UNKNOWN(" + value + ")";

		return name;
	}

	////////////////////////////////////
	private static void initLotrBiomes()
	////////////////////////////////////
	{
		// From https://lotr-minecraft-mod-exiles.fandom.com/wiki/Biomes_color_on_the_map
		lotrBiomesNameFromRGB.put("54:124:181", "River");
		lotrBiomesNameFromRGB.put("112:173:69", "Rohan");
		lotrBiomesNameFromRGB.put("232:231:225", "Misty Mountains");
		lotrBiomesNameFromRGB.put("103:173:53", "Shire");
		lotrBiomesNameFromRGB.put("68:119:54", "Shire Woodlands");
		lotrBiomesNameFromRGB.put("17:16:14", "Mordor");
		lotrBiomesNameFromRGB.put("81:77:72", "Mordor Mountains");
		lotrBiomesNameFromRGB.put("136:180:69", "Gondor");
		lotrBiomesNameFromRGB.put("229:229:232", "White Mountains");
		lotrBiomesNameFromRGB.put("251:216:63", "Lothlorien");
		lotrBiomesNameFromRGB.put("116:175:70", "Celebrant");
		lotrBiomesNameFromRGB.put("139:127:77", "Iron Hills");
		lotrBiomesNameFromRGB.put("111:115:63", "Dead Marshes");
		lotrBiomesNameFromRGB.put("88:124:47", "Trollshaws");
		lotrBiomesNameFromRGB.put("62:101:38", "Woodland Realm");
		lotrBiomesNameFromRGB.put("46:68:27", "Mirkwood Corrupted");
		lotrBiomesNameFromRGB.put("126:147:90", "Rohan Uruk Highlands");
		lotrBiomesNameFromRGB.put("150:140:114", "Emyn Muil");
		lotrBiomesNameFromRGB.put("117:167:52", "Ithilien");
		lotrBiomesNameFromRGB.put("171:193:81", "Pelargir");
		lotrBiomesNameFromRGB.put("130:168:74", "Lone Lands");
		lotrBiomesNameFromRGB.put("132:142:78", "Lone Lands Hills");
		lotrBiomesNameFromRGB.put("105:153:76", "Dunland");
		lotrBiomesNameFromRGB.put("66:117:25", "Fangorn");
		lotrBiomesNameFromRGB.put("143:175:79", "Angle");
		lotrBiomesNameFromRGB.put("124:137:90", "Ettenmoors");
		lotrBiomesNameFromRGB.put("69:117:59", "Old Forest");
		lotrBiomesNameFromRGB.put("162:181:70", "Harondor");
		lotrBiomesNameFromRGB.put("107:166:68", "Eriador");
		lotrBiomesNameFromRGB.put("116:140:71", "Eriador Downs");
		lotrBiomesNameFromRGB.put("66:127:77", "Eryn Vorn");
		lotrBiomesNameFromRGB.put("202:204:193", "Grey Mountains");
		lotrBiomesNameFromRGB.put("91:147:87", "Midgewater");
		lotrBiomesNameFromRGB.put("130:126:80", "Brown Lands");
		lotrBiomesNameFromRGB.put("2:89:141", "Ocean");
		lotrBiomesNameFromRGB.put("107:178:92", "Anduin Hills");
		lotrBiomesNameFromRGB.put("145:183:90", "Meneltarma");
		lotrBiomesNameFromRGB.put("76:155:89", "Gladden Fields");
		lotrBiomesNameFromRGB.put("212:198:67", "Lothlorien Edge");
		lotrBiomesNameFromRGB.put("216:216:210", "Forodwaith");
		lotrBiomesNameFromRGB.put("122:168:79", "Enedwaith");
		lotrBiomesNameFromRGB.put("84:71:47", "Angmar");
		lotrBiomesNameFromRGB.put("101:144:72", "Eregion");
		lotrBiomesNameFromRGB.put("116:173:69", "Lindon");
		lotrBiomesNameFromRGB.put("30:119:47", "Lindon Woodlands");
		lotrBiomesNameFromRGB.put("138:149:93", "East Bight");
		lotrBiomesNameFromRGB.put("201:218:226", "Blue Mountains");
		lotrBiomesNameFromRGB.put("40:45:29", "Mirkwood Mountains");
		lotrBiomesNameFromRGB.put("146:172:80", "Wilderland");
		lotrBiomesNameFromRGB.put("107:95:69", "Dagorlad");
		lotrBiomesNameFromRGB.put("40:36:27", "Nurn");
		lotrBiomesNameFromRGB.put("14:54:86", "Nurnen");
		lotrBiomesNameFromRGB.put("61:59:43", "Nurn Marshes");
		lotrBiomesNameFromRGB.put("119:155:79", "Adornland");
		lotrBiomesNameFromRGB.put("207:207:203", "Angmar Mountains");
		lotrBiomesNameFromRGB.put("77:168:83", "Anduin Mouth");
		lotrBiomesNameFromRGB.put("85:163:70", "Entwash Mouth");
		lotrBiomesNameFromRGB.put("142:191:69", "Dor En Ernil");
		lotrBiomesNameFromRGB.put("130:160:67", "Dor En Ernil Hills");
		lotrBiomesNameFromRGB.put("103:124:76", "Fangorn Wasteland");
		lotrBiomesNameFromRGB.put("87:135:54", "Rohan Woodlands");
		lotrBiomesNameFromRGB.put("89:135:43", "Gondor Woodlands");
		lotrBiomesNameFromRGB.put("52:100:158", "Lake");
		lotrBiomesNameFromRGB.put("141:149:150", "Lindon Coast");
		lotrBiomesNameFromRGB.put("123:142:82", "Barrow Downs");
		lotrBiomesNameFromRGB.put("109:135:70", "Long Marshes");
		lotrBiomesNameFromRGB.put("89:173:58", "Fangorn Clearing");
		lotrBiomesNameFromRGB.put("106:152:64", "Ithilien Hills");
		lotrBiomesNameFromRGB.put("122:135:79", "Ithilien Wasteland");
		lotrBiomesNameFromRGB.put("108:132:70", "Nindalf");
		lotrBiomesNameFromRGB.put("126:150:82", "Coldfells");
		lotrBiomesNameFromRGB.put("108:124:82", "Nan Curunir");
		lotrBiomesNameFromRGB.put("155:206:121", "White Downs");
		lotrBiomesNameFromRGB.put("95:156:89", "Swanfleet");
		lotrBiomesNameFromRGB.put("171:204:75", "Pelennor");
		lotrBiomesNameFromRGB.put("112:158:70", "Minhiriath");
		lotrBiomesNameFromRGB.put("114:109:85", "Erebor");
		lotrBiomesNameFromRGB.put("58:82:35", "Mirkwood North");
		lotrBiomesNameFromRGB.put("55:80:31", "Woodland Realm Hills");
		lotrBiomesNameFromRGB.put("10:5:1", "Nan Ungol");
		lotrBiomesNameFromRGB.put("151:198:69", "Pinnath Gelin");
		lotrBiomesNameFromRGB.put("154:181:83", "Island");
		lotrBiomesNameFromRGB.put("237:237:238", "Forodwaith Mountains");
		lotrBiomesNameFromRGB.put("190:193:182", "Misty Mountains Foothills");
		lotrBiomesNameFromRGB.put("139:150:96", "Grey Mountains Foothills");
		lotrBiomesNameFromRGB.put("171:181:178", "Blue Mountains Foothills");
		lotrBiomesNameFromRGB.put("188:178:150", "Tundra");
		lotrBiomesNameFromRGB.put("99:150:79", "Taiga");
		lotrBiomesNameFromRGB.put("104:179:57", "Breeland");
		lotrBiomesNameFromRGB.put("67:131:29", "Chetwood");
		lotrBiomesNameFromRGB.put("143:204:224", "Forodwaith Glacier");
		lotrBiomesNameFromRGB.put("192:205:183", "White Mountains Foothills");
		lotrBiomesNameFromRGB.put("219:202:151", "Beach");
		lotrBiomesNameFromRGB.put("150:149:160", "Beach Gravel");
		lotrBiomesNameFromRGB.put("216:195:119", "Near Harad");
		lotrBiomesNameFromRGB.put("148:160:65", "Far Harad");
		lotrBiomesNameFromRGB.put("150:144:117", "Harad Mountains");
		lotrBiomesNameFromRGB.put("145:156:84", "Umbar");
		lotrBiomesNameFromRGB.put("75:116:35", "Far Harad Jungle");
		lotrBiomesNameFromRGB.put("125:134:74", "Umbar Hills");
		lotrBiomesNameFromRGB.put("185:167:98", "Near Harad Hills");
		lotrBiomesNameFromRGB.put("34:170:204", "Far Harad Jungle Lake");
		lotrBiomesNameFromRGB.put("162:163:101", "Lostladen");
		lotrBiomesNameFromRGB.put("56:130:29", "Far Harad Forest");
		lotrBiomesNameFromRGB.put("158:170:78", "Near Harad Fertile");
		lotrBiomesNameFromRGB.put("135:126:90", "Pertorogwaith");
		lotrBiomesNameFromRGB.put("109:135:58", "Umbar Forest");
		lotrBiomesNameFromRGB.put("113:136:46", "Far Harad Jungle Edge");
		lotrBiomesNameFromRGB.put("164:188:69", "Tauredain Clearing");
		lotrBiomesNameFromRGB.put("139:168:80", "Gulf Harad");
		lotrBiomesNameFromRGB.put("203:211:169", "Dorwinion Hills");
		lotrBiomesNameFromRGB.put("155:160:109", "Tolfalas");
		lotrBiomesNameFromRGB.put("119:182:42", "Lebennin");
		lotrBiomesNameFromRGB.put("159:178:88", "Rhun");
		lotrBiomesNameFromRGB.put("114:135:59", "Rhun Forest");
		lotrBiomesNameFromRGB.put("147:113:76", "Red Mountains");
		lotrBiomesNameFromRGB.put("153:148:82", "Red Mountains Foothills");
		lotrBiomesNameFromRGB.put("36:47:15", "Dol Guldur");
		lotrBiomesNameFromRGB.put("189:187:106", "Near Harad Semi Desert");
		lotrBiomesNameFromRGB.put("170:174:85", "Far Harad Arid");
		lotrBiomesNameFromRGB.put("153:141:91", "Far Harad Arid Hills");
		lotrBiomesNameFromRGB.put("85:147:75", "Far Harad Swamp");
		lotrBiomesNameFromRGB.put("46:123:64", "Far Harad Cloud Forest");
		lotrBiomesNameFromRGB.put("153:145:62", "Far Harad Bushland");
		lotrBiomesNameFromRGB.put("127:121:52", "Far Harad Bushland Hills");
		lotrBiomesNameFromRGB.put("135:142:77", "Far Harad Mangrove");
		lotrBiomesNameFromRGB.put("105:132:50", "Near Harad Fertile Forest");
		lotrBiomesNameFromRGB.put("113:165:72", "Anduin Vale");
		lotrBiomesNameFromRGB.put("144:181:79", "Wold");
		lotrBiomesNameFromRGB.put("105:155:76", "Shire Moors");
		lotrBiomesNameFromRGB.put("61:160:95", "Shire Marshes");
		lotrBiomesNameFromRGB.put("201:147:79", "Near Harad Red Desert");
		lotrBiomesNameFromRGB.put("104:87:52", "Far Harad Volcano");
		lotrBiomesNameFromRGB.put("1:0:0", "Udun");
		lotrBiomesNameFromRGB.put("33:29:29", "Gorgoroth");
		lotrBiomesNameFromRGB.put("21:45:25", "Morgul Vale");
		lotrBiomesNameFromRGB.put("92:92:71", "Eastern Desolation");
		lotrBiomesNameFromRGB.put("125:163:79", "Dale");
		lotrBiomesNameFromRGB.put("108:165:69", "Dorwinion");
		lotrBiomesNameFromRGB.put("104:150:65", "Tower Hills");
		lotrBiomesNameFromRGB.put("89:140:46", "Gulf Harad Forest");
		lotrBiomesNameFromRGB.put("147:166:108", "Wilderland North");
		lotrBiomesNameFromRGB.put("140:154:173", "Forodwaith Coast");
		lotrBiomesNameFromRGB.put("127:130:120", "Far Harad Coast");
		lotrBiomesNameFromRGB.put("109:158:80", "Near Harad Riverbank");
		lotrBiomesNameFromRGB.put("128:197:46", "Lossarnach");
		lotrBiomesNameFromRGB.put("221:133:104", "Imloth Melui");
		lotrBiomesNameFromRGB.put("12:181:0", "Near Harad Oasis");
		lotrBiomesNameFromRGB.put("237:237:237", "Beach White");
		lotrBiomesNameFromRGB.put("174:179:85", "Harnedor");
		lotrBiomesNameFromRGB.put("166:189:100", "Lamedon");
		lotrBiomesNameFromRGB.put("206:214:169", "Lamedon Hills");
		lotrBiomesNameFromRGB.put("109:158:49", "Blackroot Vale");
		lotrBiomesNameFromRGB.put("135:150:96", "Andrast");
		lotrBiomesNameFromRGB.put("86:122:66", "Pukel");
		lotrBiomesNameFromRGB.put("173:171:79", "Rhun Land");
		lotrBiomesNameFromRGB.put("178:183:98", "Rhun Land Steppe");
		lotrBiomesNameFromRGB.put("142:141:78", "Rhun Land Hills");
		lotrBiomesNameFromRGB.put("145:108:62", "Rhun Red Forest");
		lotrBiomesNameFromRGB.put("165:177:87", "Rhun Island");
		lotrBiomesNameFromRGB.put("145:121:62", "Rhun Island Forest");
		lotrBiomesNameFromRGB.put("211:195:135", "Last Desert");
		lotrBiomesNameFromRGB.put("211:211:211", "Wind Mountains");
		lotrBiomesNameFromRGB.put("154:159:106", "Wind Mountains Foothills");
		lotrBiomesNameFromRGB.put("134:183:42", "Rivendell");
		lotrBiomesNameFromRGB.put("216:213:177", "Rivendell Hills");
		lotrBiomesNameFromRGB.put("99:90:70", "Far Harad Jungle Mountains");
		lotrBiomesNameFromRGB.put("91:112:52", "Half Troll Forest");
		lotrBiomesNameFromRGB.put("78:120:24", "Far Harad Kanuka");

		lotrBiomesValueFromRGB.put("54:124:181", 0);
		lotrBiomesValueFromRGB.put("112:173:69", 1);
		lotrBiomesValueFromRGB.put("232:231:225", 2);
		lotrBiomesValueFromRGB.put("103:173:53", 3);
		lotrBiomesValueFromRGB.put("68:119:54", 4);
		lotrBiomesValueFromRGB.put("17:16:14", 5);
		lotrBiomesValueFromRGB.put("81:77:72", 6);
		lotrBiomesValueFromRGB.put("136:180:69", 7);
		lotrBiomesValueFromRGB.put("229:229:232", 8);
		lotrBiomesValueFromRGB.put("251:216:63", 9);
		lotrBiomesValueFromRGB.put("116:175:70", 10);
		lotrBiomesValueFromRGB.put("139:127:77", 11);
		lotrBiomesValueFromRGB.put("111:115:63", 12);
		lotrBiomesValueFromRGB.put("88:124:47", 13);
		lotrBiomesValueFromRGB.put("62:101:38", 14);
		lotrBiomesValueFromRGB.put("46:68:27", 15);
		lotrBiomesValueFromRGB.put("126:147:90", 16);
		lotrBiomesValueFromRGB.put("150:140:114", 17);
		lotrBiomesValueFromRGB.put("117:167:52", 18);
		lotrBiomesValueFromRGB.put("171:193:81", 19);
		lotrBiomesValueFromRGB.put("130:168:74", 21);
		lotrBiomesValueFromRGB.put("132:142:78", 22);
		lotrBiomesValueFromRGB.put("105:153:76", 23);
		lotrBiomesValueFromRGB.put("66:117:25", 24);
		lotrBiomesValueFromRGB.put("143:175:79", 25);
		lotrBiomesValueFromRGB.put("124:137:90", 26);
		lotrBiomesValueFromRGB.put("69:117:59", 27);
		lotrBiomesValueFromRGB.put("162:181:70", 28);
		lotrBiomesValueFromRGB.put("107:166:68", 29);
		lotrBiomesValueFromRGB.put("116:140:71", 30);
		lotrBiomesValueFromRGB.put("66:127:77", 31);
		lotrBiomesValueFromRGB.put("202:204:193", 32);
		lotrBiomesValueFromRGB.put("91:147:87", 33);
		lotrBiomesValueFromRGB.put("130:126:80", 34);
		lotrBiomesValueFromRGB.put("2:89:141", 35);
		lotrBiomesValueFromRGB.put("107:178:92", 36);
		lotrBiomesValueFromRGB.put("145:183:90", 37);
		lotrBiomesValueFromRGB.put("76:155:89", 38);
		lotrBiomesValueFromRGB.put("212:198:67", 39);
		lotrBiomesValueFromRGB.put("216:216:210", 40);
		lotrBiomesValueFromRGB.put("122:168:79", 41);
		lotrBiomesValueFromRGB.put("84:71:47", 42);
		lotrBiomesValueFromRGB.put("101:144:72", 43);
		lotrBiomesValueFromRGB.put("116:173:69", 44);
		lotrBiomesValueFromRGB.put("30:119:47", 45);
		lotrBiomesValueFromRGB.put("138:149:93", 46);
		lotrBiomesValueFromRGB.put("201:218:226", 47);
		lotrBiomesValueFromRGB.put("40:45:29", 48);
		lotrBiomesValueFromRGB.put("146:172:80", 49);
		lotrBiomesValueFromRGB.put("107:95:69", 50);
		lotrBiomesValueFromRGB.put("40:36:27", 51);
		lotrBiomesValueFromRGB.put("14:54:86", 52);
		lotrBiomesValueFromRGB.put("61:59:43", 53);
		lotrBiomesValueFromRGB.put("119:155:79", 54);
		lotrBiomesValueFromRGB.put("207:207:203", 55);
		lotrBiomesValueFromRGB.put("77:168:83", 56);
		lotrBiomesValueFromRGB.put("85:163:70", 57);
		lotrBiomesValueFromRGB.put("142:191:69", 58);
		lotrBiomesValueFromRGB.put("130:160:67", 59);
		lotrBiomesValueFromRGB.put("103:124:76", 60);
		lotrBiomesValueFromRGB.put("87:135:54", 61);
		lotrBiomesValueFromRGB.put("89:135:43", 62);
		lotrBiomesValueFromRGB.put("52:100:158", 63);
		lotrBiomesValueFromRGB.put("141:149:150", 64);
		lotrBiomesValueFromRGB.put("123:142:82", 65);
		lotrBiomesValueFromRGB.put("109:135:70", 66);
		lotrBiomesValueFromRGB.put("89:173:58", 67);
		lotrBiomesValueFromRGB.put("106:152:64", 68);
		lotrBiomesValueFromRGB.put("122:135:79", 69);
		lotrBiomesValueFromRGB.put("108:132:70", 70);
		lotrBiomesValueFromRGB.put("126:150:82", 71);
		lotrBiomesValueFromRGB.put("108:124:82", 72);
		lotrBiomesValueFromRGB.put("155:206:121", 74);
		lotrBiomesValueFromRGB.put("95:156:89", 75);
		lotrBiomesValueFromRGB.put("171:204:75", 76);
		lotrBiomesValueFromRGB.put("112:158:70", 77);
		lotrBiomesValueFromRGB.put("114:109:85", 78);
		lotrBiomesValueFromRGB.put("58:82:35", 79);
		lotrBiomesValueFromRGB.put("55:80:31", 80);
		lotrBiomesValueFromRGB.put("10:5:1", 81);
		lotrBiomesValueFromRGB.put("151:198:69", 82);
		lotrBiomesValueFromRGB.put("154:181:83", 83);
		lotrBiomesValueFromRGB.put("237:237:238", 84);
		lotrBiomesValueFromRGB.put("190:193:182", 85);
		lotrBiomesValueFromRGB.put("139:150:96", 86);
		lotrBiomesValueFromRGB.put("171:181:178", 87);
		lotrBiomesValueFromRGB.put("188:178:150", 88);
		lotrBiomesValueFromRGB.put("99:150:79", 89);
		lotrBiomesValueFromRGB.put("104:179:57", 90);
		lotrBiomesValueFromRGB.put("67:131:29", 91);
		lotrBiomesValueFromRGB.put("143:204:224", 92);
		lotrBiomesValueFromRGB.put("192:205:183", 93);
		lotrBiomesValueFromRGB.put("219:202:151", 94);
		lotrBiomesValueFromRGB.put("150:149:160", 95);
		lotrBiomesValueFromRGB.put("216:195:119", 96);
		lotrBiomesValueFromRGB.put("148:160:65", 97);
		lotrBiomesValueFromRGB.put("150:144:117", 98);
		lotrBiomesValueFromRGB.put("145:156:84", 99);
		lotrBiomesValueFromRGB.put("75:116:35", 100);
		lotrBiomesValueFromRGB.put("125:134:74", 101);
		lotrBiomesValueFromRGB.put("185:167:98", 102);
		lotrBiomesValueFromRGB.put("34:170:204", 103);
		lotrBiomesValueFromRGB.put("162:163:101", 104);
		lotrBiomesValueFromRGB.put("56:130:29", 105);
		lotrBiomesValueFromRGB.put("158:170:78", 106);
		lotrBiomesValueFromRGB.put("135:126:90", 107);
		lotrBiomesValueFromRGB.put("109:135:58", 108);
		lotrBiomesValueFromRGB.put("113:136:46", 109);
		lotrBiomesValueFromRGB.put("164:188:69", 110);
		lotrBiomesValueFromRGB.put("139:168:80", 111);
		lotrBiomesValueFromRGB.put("203:211:169", 112);
		lotrBiomesValueFromRGB.put("155:160:109", 113);
		lotrBiomesValueFromRGB.put("119:182:42", 114);
		lotrBiomesValueFromRGB.put("159:178:88", 115);
		lotrBiomesValueFromRGB.put("114:135:59", 116);
		lotrBiomesValueFromRGB.put("147:113:76", 117);
		lotrBiomesValueFromRGB.put("153:148:82", 118);
		lotrBiomesValueFromRGB.put("36:47:15", 119);
		lotrBiomesValueFromRGB.put("189:187:106", 120);
		lotrBiomesValueFromRGB.put("170:174:85", 121);
		lotrBiomesValueFromRGB.put("153:141:91", 122);
		lotrBiomesValueFromRGB.put("85:147:75", 123);
		lotrBiomesValueFromRGB.put("46:123:64", 124);
		lotrBiomesValueFromRGB.put("153:145:62", 125);
		lotrBiomesValueFromRGB.put("127:121:52", 126);
		lotrBiomesValueFromRGB.put("135:142:77", 127);
		lotrBiomesValueFromRGB.put("105:132:50", 128);
		lotrBiomesValueFromRGB.put("113:165:72", 129);
		lotrBiomesValueFromRGB.put("144:181:79", 130);
		lotrBiomesValueFromRGB.put("105:155:76", 131);
		lotrBiomesValueFromRGB.put("61:160:95", 132);
		lotrBiomesValueFromRGB.put("201:147:79", 133);
		lotrBiomesValueFromRGB.put("104:87:52", 134);
		lotrBiomesValueFromRGB.put("1:0:0", 135);
		lotrBiomesValueFromRGB.put("33:29:29", 136);
		lotrBiomesValueFromRGB.put("21:45:25", 137);
		lotrBiomesValueFromRGB.put("92:92:71", 138);
		lotrBiomesValueFromRGB.put("125:163:79", 139);
		lotrBiomesValueFromRGB.put("108:165:69", 140);
		lotrBiomesValueFromRGB.put("104:150:65", 141);
		lotrBiomesValueFromRGB.put("89:140:46", 142);
		lotrBiomesValueFromRGB.put("147:166:108", 143);
		lotrBiomesValueFromRGB.put("140:154:173", 144);
		lotrBiomesValueFromRGB.put("127:130:120", 145);
		lotrBiomesValueFromRGB.put("109:158:80", 146);
		lotrBiomesValueFromRGB.put("128:197:46", 147);
		lotrBiomesValueFromRGB.put("221:133:104", 148);
		lotrBiomesValueFromRGB.put("12:181:0", 149);
		lotrBiomesValueFromRGB.put("237:237:237", 150);
		lotrBiomesValueFromRGB.put("174:179:85", 151);
		lotrBiomesValueFromRGB.put("166:189:100", 152);
		lotrBiomesValueFromRGB.put("206:214:169", 153);
		lotrBiomesValueFromRGB.put("109:158:49", 154);
		lotrBiomesValueFromRGB.put("135:150:96", 155);
		lotrBiomesValueFromRGB.put("86:122:66", 156);
		lotrBiomesValueFromRGB.put("173:171:79", 157);
		lotrBiomesValueFromRGB.put("178:183:98", 158);
		lotrBiomesValueFromRGB.put("142:141:78", 159);
		lotrBiomesValueFromRGB.put("145:108:62", 160);
		lotrBiomesValueFromRGB.put("165:177:87", 161);
		lotrBiomesValueFromRGB.put("145:121:62", 162);
		lotrBiomesValueFromRGB.put("211:195:135", 163);
		lotrBiomesValueFromRGB.put("211:211:211", 164);
		lotrBiomesValueFromRGB.put("154:159:106", 165);
		lotrBiomesValueFromRGB.put("134:183:42", 166);
		lotrBiomesValueFromRGB.put("216:213:177", 167);
		lotrBiomesValueFromRGB.put("99:90:70", 168);
		lotrBiomesValueFromRGB.put("91:112:52", 169);
		lotrBiomesValueFromRGB.put("78:120:24", 170);

		lotrBiomesNameFromValue.put(0, "River");
		lotrBiomesNameFromValue.put(1, "Rohan");
		lotrBiomesNameFromValue.put(2, "Misty Mountains");
		lotrBiomesNameFromValue.put(3, "Shire");
		lotrBiomesNameFromValue.put(4, "Shire Woodlands");
		lotrBiomesNameFromValue.put(5, "Mordor");
		lotrBiomesNameFromValue.put(6, "Mordor Mountains");
		lotrBiomesNameFromValue.put(7, "Gondor");
		lotrBiomesNameFromValue.put(8, "White Mountains");
		lotrBiomesNameFromValue.put(9, "Lothlorien");
		lotrBiomesNameFromValue.put(10, "Celebrant");
		lotrBiomesNameFromValue.put(11, "Iron Hills");
		lotrBiomesNameFromValue.put(12, "Dead Marshes");
		lotrBiomesNameFromValue.put(13, "Trollshaws");
		lotrBiomesNameFromValue.put(14, "Woodland Realm");
		lotrBiomesNameFromValue.put(15, "Mirkwood Corrupted");
		lotrBiomesNameFromValue.put(16, "Rohan Uruk Highlands");
		lotrBiomesNameFromValue.put(17, "Emyn Muil");
		lotrBiomesNameFromValue.put(18, "Ithilien");
		lotrBiomesNameFromValue.put(19, "Pelargir");
		lotrBiomesNameFromValue.put(20, "UNKNOWN-20");
		lotrBiomesNameFromValue.put(21, "Lone Lands");
		lotrBiomesNameFromValue.put(22, "Lone Lands Hills");
		lotrBiomesNameFromValue.put(23, "Dunland");
		lotrBiomesNameFromValue.put(24, "Fangorn");
		lotrBiomesNameFromValue.put(25, "Angle");
		lotrBiomesNameFromValue.put(26, "Ettenmoors");
		lotrBiomesNameFromValue.put(27, "Old Forest");
		lotrBiomesNameFromValue.put(28, "Harondor");
		lotrBiomesNameFromValue.put(29, "Eriador");
		lotrBiomesNameFromValue.put(30, "Eriador Downs");
		lotrBiomesNameFromValue.put(31, "Eryn Vorn");
		lotrBiomesNameFromValue.put(32, "Grey Mountains");
		lotrBiomesNameFromValue.put(33, "Midgewater");
		lotrBiomesNameFromValue.put(34, "Brown Lands");
		lotrBiomesNameFromValue.put(35, "Ocean");
		lotrBiomesNameFromValue.put(36, "Anduin Hills");
		lotrBiomesNameFromValue.put(37, "Meneltarma");
		lotrBiomesNameFromValue.put(38, "Gladden Fields");
		lotrBiomesNameFromValue.put(39, "Lothlorien Edge");
		lotrBiomesNameFromValue.put(40, "Forodwaith");
		lotrBiomesNameFromValue.put(41, "Enedwaith");
		lotrBiomesNameFromValue.put(42, "Angmar");
		lotrBiomesNameFromValue.put(43, "Eregion");
		lotrBiomesNameFromValue.put(44, "Lindon");
		lotrBiomesNameFromValue.put(45, "Lindon Woodlands");
		lotrBiomesNameFromValue.put(46, "East Bight");
		lotrBiomesNameFromValue.put(47, "Blue Mountains");
		lotrBiomesNameFromValue.put(48, "Mirkwood Mountains");
		lotrBiomesNameFromValue.put(49, "Wilderland");
		lotrBiomesNameFromValue.put(50, "Dagorlad");
		lotrBiomesNameFromValue.put(51, "Nurn");
		lotrBiomesNameFromValue.put(52, "Nurnen");
		lotrBiomesNameFromValue.put(53, "Nurn Marshes");
		lotrBiomesNameFromValue.put(54, "Adornland");
		lotrBiomesNameFromValue.put(55, "Angmar Mountains");
		lotrBiomesNameFromValue.put(56, "Anduin Mouth");
		lotrBiomesNameFromValue.put(57, "Entwash Mouth");
		lotrBiomesNameFromValue.put(58, "Dor En Ernil");
		lotrBiomesNameFromValue.put(59, "Dor En Ernil Hills");
		lotrBiomesNameFromValue.put(60, "Fangorn Wasteland");
		lotrBiomesNameFromValue.put(61, "Rohan Woodlands");
		lotrBiomesNameFromValue.put(62, "Gondor Woodlands");
		lotrBiomesNameFromValue.put(63, "Lake");
		lotrBiomesNameFromValue.put(64, "Lindon Coast");
		lotrBiomesNameFromValue.put(65, "Barrow Downs");
		lotrBiomesNameFromValue.put(66, "Long Marshes");
		lotrBiomesNameFromValue.put(67, "Fangorn Clearing");
		lotrBiomesNameFromValue.put(68, "Ithilien Hills");
		lotrBiomesNameFromValue.put(69, "Ithilien Wasteland");
		lotrBiomesNameFromValue.put(70, "Nindalf");
		lotrBiomesNameFromValue.put(71, "Coldfells");
		lotrBiomesNameFromValue.put(72, "Nan Curunir");
		lotrBiomesNameFromValue.put(74, "White Downs");
		lotrBiomesNameFromValue.put(75, "Swanfleet");
		lotrBiomesNameFromValue.put(76, "Pelennor");
		lotrBiomesNameFromValue.put(77, "Minhiriath");
		lotrBiomesNameFromValue.put(78, "Erebor");
		lotrBiomesNameFromValue.put(79, "Mirkwood North");
		lotrBiomesNameFromValue.put(80, "Woodland Realm Hills");
		lotrBiomesNameFromValue.put(81, "Nan Ungol");
		lotrBiomesNameFromValue.put(82, "Pinnath Gelin");
		lotrBiomesNameFromValue.put(83, "Island");
		lotrBiomesNameFromValue.put(84, "Forodwaith Mountains");
		lotrBiomesNameFromValue.put(85, "Misty Mountains Foothills");
		lotrBiomesNameFromValue.put(86, "Grey Mountains Foothills");
		lotrBiomesNameFromValue.put(87, "Blue Mountains Foothills");
		lotrBiomesNameFromValue.put(88, "Tundra");
		lotrBiomesNameFromValue.put(89, "Taiga");
		lotrBiomesNameFromValue.put(90, "Breeland");
		lotrBiomesNameFromValue.put(91, "Chetwood");
		lotrBiomesNameFromValue.put(92, "Forodwaith Glacier");
		lotrBiomesNameFromValue.put(93, "White Mountains Foothills");
		lotrBiomesNameFromValue.put(94, "Beach");
		lotrBiomesNameFromValue.put(95, "Beach Gravel");
		lotrBiomesNameFromValue.put(96, "Near Harad");
		lotrBiomesNameFromValue.put(97, "Far Harad");
		lotrBiomesNameFromValue.put(98, "Harad Mountains");
		lotrBiomesNameFromValue.put(99, "Umbar");
		lotrBiomesNameFromValue.put(100, "Far Harad Jungle");
		lotrBiomesNameFromValue.put(101, "Umbar Hills");
		lotrBiomesNameFromValue.put(102, "Near Harad Hills");
		lotrBiomesNameFromValue.put(103, "Far Harad Jungle Lake");
		lotrBiomesNameFromValue.put(104, "Lostladen");
		lotrBiomesNameFromValue.put(105, "Far Harad Forest");
		lotrBiomesNameFromValue.put(106, "Near Harad Fertile");
		lotrBiomesNameFromValue.put(107, "Pertorogwaith");
		lotrBiomesNameFromValue.put(108, "Umbar Forest");
		lotrBiomesNameFromValue.put(109, "Far Harad Jungle Edge");
		lotrBiomesNameFromValue.put(110, "Tauredain Clearing");
		lotrBiomesNameFromValue.put(111, "Gulf Harad");
		lotrBiomesNameFromValue.put(112, "Dorwinion Hills");
		lotrBiomesNameFromValue.put(113, "Tolfalas");
		lotrBiomesNameFromValue.put(114, "Lebennin");
		lotrBiomesNameFromValue.put(115, "Rhun");
		lotrBiomesNameFromValue.put(116, "Rhun Forest");
		lotrBiomesNameFromValue.put(117, "Red Mountains");
		lotrBiomesNameFromValue.put(118, "Red Mountains Foothills");
		lotrBiomesNameFromValue.put(119, "Dol Guldur");
		lotrBiomesNameFromValue.put(120, "Near Harad Semi Desert");
		lotrBiomesNameFromValue.put(121, "Far Harad Arid");
		lotrBiomesNameFromValue.put(122, "Far Harad Arid Hills");
		lotrBiomesNameFromValue.put(123, "Far Harad Swamp");
		lotrBiomesNameFromValue.put(124, "Far Harad Cloud Forest");
		lotrBiomesNameFromValue.put(125, "Far Harad Bushland");
		lotrBiomesNameFromValue.put(126, "Far Harad Bushland Hills");
		lotrBiomesNameFromValue.put(127, "Far Harad Mangrove");
		lotrBiomesNameFromValue.put(128, "Near Harad Fertile Forest");
		lotrBiomesNameFromValue.put(129, "Anduin Vale");
		lotrBiomesNameFromValue.put(130, "Wold");
		lotrBiomesNameFromValue.put(131, "Shire Moors");
		lotrBiomesNameFromValue.put(132, "Shire Marshes");
		lotrBiomesNameFromValue.put(133, "Near Harad Red Desert");
		lotrBiomesNameFromValue.put(134, "Far Harad Volcano");
		lotrBiomesNameFromValue.put(135, "Udun");
		lotrBiomesNameFromValue.put(136, "Gorgoroth");
		lotrBiomesNameFromValue.put(137, "Morgul Vale");
		lotrBiomesNameFromValue.put(138, "Eastern Desolation");
		lotrBiomesNameFromValue.put(139, "Dale");
		lotrBiomesNameFromValue.put(140, "Dorwinion");
		lotrBiomesNameFromValue.put(141, "Tower Hills");
		lotrBiomesNameFromValue.put(142, "Gulf Harad Forest");
		lotrBiomesNameFromValue.put(143, "Wilderland North");
		lotrBiomesNameFromValue.put(144, "Forodwaith Coast");
		lotrBiomesNameFromValue.put(145, "Far Harad Coast");
		lotrBiomesNameFromValue.put(146, "Near Harad Riverbank");
		lotrBiomesNameFromValue.put(147, "Lossarnach");
		lotrBiomesNameFromValue.put(148, "Imloth Melui");
		lotrBiomesNameFromValue.put(149, "Near Harad Oasis");
		lotrBiomesNameFromValue.put(150, "Beach White");
		lotrBiomesNameFromValue.put(151, "Harnedor");
		lotrBiomesNameFromValue.put(152, "Lamedon");
		lotrBiomesNameFromValue.put(153, "Lamedon Hills");
		lotrBiomesNameFromValue.put(154, "Blackroot Vale");
		lotrBiomesNameFromValue.put(155, "Andrast");
		lotrBiomesNameFromValue.put(156, "Pukel");
		lotrBiomesNameFromValue.put(157, "Rhun Land");
		lotrBiomesNameFromValue.put(158, "Rhun Land Steppe");
		lotrBiomesNameFromValue.put(159, "Rhun Land Hills");
		lotrBiomesNameFromValue.put(160, "Rhun Red Forest");
		lotrBiomesNameFromValue.put(161, "Rhun Island");
		lotrBiomesNameFromValue.put(162, "Rhun Island Forest");
		lotrBiomesNameFromValue.put(163, "Last Desert");
		lotrBiomesNameFromValue.put(164, "Wind Mountains");
		lotrBiomesNameFromValue.put(165, "Wind Mountains Foothills");
		lotrBiomesNameFromValue.put(166, "Rivendell");
		lotrBiomesNameFromValue.put(167, "Rivendell Hills");
		lotrBiomesNameFromValue.put(168, "Far Harad Jungle Mountains");
		lotrBiomesNameFromValue.put(169, "Half Troll Forest");
		lotrBiomesNameFromValue.put(170, "Far Harad Kanuka");	// added in v35
	}

	/////////////////////////////////////////////////
	public static String getLotrBiomeName(String rgb)
	/////////////////////////////////////////////////
	{
		if (lotrBiomesNameFromRGB.isEmpty())
			initLotrBiomes();

		String name = lotrBiomesNameFromRGB.get(rgb);
		if (name == null)
			name = "UNKNOWN(" + rgb + ")";

		return name;
	}

	///////////////////////////////////////////////
	public static int getLotrBiomeValue(String rgb)
	///////////////////////////////////////////////
	{
		if (lotrBiomesValueFromRGB.isEmpty())
			initLotrBiomes();

		return lotrBiomesValueFromRGB.get(rgb);
	}

	////////////////////////////////////////////////
	public static String getLotrBiomeName(int value)
	////////////////////////////////////////////////
	{
		if (lotrBiomesNameFromValue.isEmpty())
			initLotrBiomes();

		String name = lotrBiomesNameFromValue.get(value);
		if (name == null)
			name = "UNKNOWN(" + value + ")";

		return name;
	}

	/////////////////////////////////////////////////////////////////////
	public static String getLotrBiomeRGB(int mx, int mz) throws Exception
	/////////////////////////////////////////////////////////////////////
	{
		Path file = Paths.get(RegionScanner.mapOfMiddleEarth);

		BufferedImage image = ImageIO.read(new File(file.toString()));

		if (image == null)
			throw new Exception("--map parameter is not a picture");

		if (mx < 0 || mz < 0)
			throw new Exception("Invalid LOR mod map coordinates");

		if (mx > image.getWidth() || mz > image.getHeight())
			return "2:89:141"; // Ocean

		Color colour = new Color(image.getRGB(mx, mz));
		int r = colour.getRed();
		int g = colour.getGreen();
		int b = colour.getBlue();
		String rgb =  r + ":" + g + ":" + b;

		return rgb;
	}

	////////////////////////////////////////////
	private static void initLotrBiomesVariants()
	////////////////////////////////////////////
	{
		// From https://lotr-minecraft-mod-exiles.fandom.com/wiki/Biomes_color_on_the_map
		lotrBiomesVariantsNameFromValue.put(0, "Standard");
		lotrBiomesVariantsNameFromValue.put(1, "Flowers");
		lotrBiomesVariantsNameFromValue.put(2, "Forest");
		lotrBiomesVariantsNameFromValue.put(3, "Forest light");
		lotrBiomesVariantsNameFromValue.put(4, "Steppe");
		lotrBiomesVariantsNameFromValue.put(5, "Steppe barren");
		lotrBiomesVariantsNameFromValue.put(6, "Hills");
		lotrBiomesVariantsNameFromValue.put(7, "Hills forest");
		lotrBiomesVariantsNameFromValue.put(8, "Mountain");
		lotrBiomesVariantsNameFromValue.put(9, "Clearing");
		lotrBiomesVariantsNameFromValue.put(10, "Dense Forest oak");
		lotrBiomesVariantsNameFromValue.put(11, "Dense Forest spruce");
		lotrBiomesVariantsNameFromValue.put(12, "Dense Forest oak spruce");
		lotrBiomesVariantsNameFromValue.put(13, "Dead Forest oak");
		lotrBiomesVariantsNameFromValue.put(14, "Dead Forest spruce");
		lotrBiomesVariantsNameFromValue.put(15, "Dead Forest oak spruce");
		lotrBiomesVariantsNameFromValue.put(16, "Shrubland oak");
		lotrBiomesVariantsNameFromValue.put(17, "Dense Forest birch");
		lotrBiomesVariantsNameFromValue.put(18, "Swamp Lowland");
		lotrBiomesVariantsNameFromValue.put(19, "Swamp Upland");
		lotrBiomesVariantsNameFromValue.put(20, "Savannah Baobab");
		lotrBiomesVariantsNameFromValue.put(21, "Lake");
		lotrBiomesVariantsNameFromValue.put(22, "Dense Forest lebethron");
		lotrBiomesVariantsNameFromValue.put(23, "Boulders red");
		lotrBiomesVariantsNameFromValue.put(24, "Boulders rohan");
		lotrBiomesVariantsNameFromValue.put(25, "Jungle dense");
		lotrBiomesVariantsNameFromValue.put(26, "Vineyard");
		lotrBiomesVariantsNameFromValue.put(27, "Forest aspen");
		lotrBiomesVariantsNameFromValue.put(28, "Forest birch");
		lotrBiomesVariantsNameFromValue.put(29, "Forest beech");
		lotrBiomesVariantsNameFromValue.put(30, "Forest maple");
		lotrBiomesVariantsNameFromValue.put(31, "Forest larch");
		lotrBiomesVariantsNameFromValue.put(32, "Forest pine");
		lotrBiomesVariantsNameFromValue.put(33, "Orchard shire");
		lotrBiomesVariantsNameFromValue.put(34, "Orchard apple pear");
		lotrBiomesVariantsNameFromValue.put(35, "Orchard orange");
		lotrBiomesVariantsNameFromValue.put(36, "Orchard lemon");
		lotrBiomesVariantsNameFromValue.put(37, "Orchard lime");
		lotrBiomesVariantsNameFromValue.put(38, "Orchard almond");
		lotrBiomesVariantsNameFromValue.put(39, "Orchard olive");
		lotrBiomesVariantsNameFromValue.put(40, "Orchard plum");
		lotrBiomesVariantsNameFromValue.put(41, "River");
		lotrBiomesVariantsNameFromValue.put(42, "Scrubland");
		lotrBiomesVariantsNameFromValue.put(43, "Hills scrubland");
		lotrBiomesVariantsNameFromValue.put(44, "Wasteland");
		lotrBiomesVariantsNameFromValue.put(45, "Orchard date");
		lotrBiomesVariantsNameFromValue.put(46, "Dense Forest dark Oak");
		lotrBiomesVariantsNameFromValue.put(47, "Orchard pomegranate");
		lotrBiomesVariantsNameFromValue.put(48, "Dunes");
		lotrBiomesVariantsNameFromValue.put(49, "Scrubland sand");
		lotrBiomesVariantsNameFromValue.put(50, "Hills scrubland sand");
		lotrBiomesVariantsNameFromValue.put(51, "Wasteland sand");
	}

	///////////////////////////////////////////////////////
	public static String getLotrBiomeVariantName(int value)
	///////////////////////////////////////////////////////
	{
		if (lotrBiomesVariantsNameFromValue.isEmpty())
			initLotrBiomesVariants();

		String name = lotrBiomesVariantsNameFromValue.get(value);
		if (name == null)
			name = "UNKNOWN(" + value + ")";

		return name;
	}
}
