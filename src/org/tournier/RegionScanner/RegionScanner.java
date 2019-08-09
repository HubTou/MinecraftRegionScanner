package org.tournier.RegionScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.cli.*;

public class RegionScanner
{
	// Command line options
	public static List<String> chunksToDelete = null;
	public static String fromLevel = "";
	public static List<String> entitiesToKill = null;
	public static int logLevel = 0;
	public static String mapOfMiddleEarth = "";
	public static boolean fixNames = false;
	public static String output = "";
	public static List<String> chunksToPreserve = null;
	public static HashMap<String, String> blocksToReplace = null;
	public static HashMap<String, String> itemsToReplace = null;
	public static boolean scan = false;
	public static boolean fixStacks = false;
	public static String toLevel = "";
	public static boolean fixUnknown = false;
	public static List<String> regionFilesToProcess = null;

	// From / To level.dat
	public static BlocksAndItemsMap blocksAndItems = null;
	public static BlocksAndItemsMap newBlocksAndItems = null;
	public static HashMap<Integer, Integer> modifiedBlocks = null;
	public static HashMap<Integer, Integer> modifiedItems = null;

	//////////////////////////////////////////////
	private static Options configHelpParameter()
	//////////////////////////////////////////////
	{
		Option helpOption = Option.builder("h") 
			.longOpt("help")
			.desc("Display usage") 
			.hasArg(false) 
			.required(false) 
			.build();
	
		Options options = new Options();
	
		options.addOption(helpOption);

		return options;
	}

	/////////////////////////////////////////////////////////////
	private static Options configParameters(Options firstOptions)
	/////////////////////////////////////////////////////////////
	{
		// QUESTION: possibility to declare mutually exclusive options?
		// QUESTION: possibility to declare at least one option mandatory in a set?

		Option deleteOption = Option.builder("d") 
			.longOpt("delete")
			.desc("List of chunks to delete, inline (x1,z1/x2,z2/...) or @infile") 
			.hasArg(true) 
			.argName("chunksToDelete")
			.required(false) 
			.build();
	
		Option fromOption = Option.builder("f") 
			.longOpt("from")
			.desc("level.dat file providing the blocks & itema ID/value matching") 
			.hasArg(true) 
			.argName("fromLevel")
			.required(false) 
			.build();

		Option killOption = Option.builder("k") 
			.longOpt("kill") 
			.desc("List of entities to kill, inline (ALL keyworkd or e1/e2/...) or @infile") 
			.hasArg(true) 
			.argName("entitiesToKill")
			.required(false) 
			.build();

		Option logLevelOption = Option.builder("l") 
			.longOpt("logLevel") 
			.desc("Log level (0=none, 1=debug, 2+=verbose)") 
			.hasArg(true) 
			.argName("logLevelValue")
			.required(false) 
			.build();
	
		Option mapOption = Option.builder("m") 
			.longOpt("map")
			.desc("Path to the Middle-Earth dimension map") 
			.hasArg(true) 
			.argName("lotrModMap")
			.required(false) 
			.build();
	
		Option nameOption = Option.builder("n") 
			.longOpt("rename")
			.desc("Transliterate cyrillic names to latin names") 
			.hasArg(false) 
			.required(false) 
			.build();
	
		Option outputOption = Option.builder("o") 
			.longOpt("output")
			.desc("Modified regions naming (new (default)=dest.new, old=src.old, overwrite=dest>src)") 
			.hasArg(true) 
			.argName("outputChoice")
			.required(false) 
			.build();
	
		Option preserveOption = Option.builder("p") 
			.longOpt("preserve")
			.desc("List of chunks to preserve, inline (x1,z1/x2,z2/...) or @infile") 
			.hasArg(true) 
			.argName("chunksToPreserve")
			.required(false) 
			.build();
	
		Option replaceOption = Option.builder("r") 
			.longOpt("replace")
			.desc("List of blocks or items to replace, inline (b,blksrc[:dat],blkdst[:dat]/ i,itmsrc[:dat]/itmdst[:dat]/... or DELETE or AMBIANT keywords for destination) or @infile") 
			.hasArg(true) 
			.argName("thingsToReplace")
			.required(false) 
			.build();
	
		Option scanOption = Option.builder("s") 
			.longOpt("scan") 
			.desc("Display region file(s) statistics") 
			.hasArg(false) 
			.required(false) 
			.build();
	
		Option stacksOption = Option.builder("S") 
			.longOpt("stacks")
			.desc("Fix item stacks of more than 64 items") 
			.hasArg(false) 
			.required(false) 
			.build();
	
		Option toOption = Option.builder("t") 
			.longOpt("to") 
			.desc("level.dat file providing the blocks & items new values") 
			.hasArg(true) 
			.argName("toLevel") 
			.required(false) 
			.build();
	
		Option unknownOption = Option.builder("u") 
			.longOpt("unknown")
			.desc("Fix unknown blocks") 
			.hasArg(false) 
			.required(false) 
			.build();
	
		Options options = new Options();
	
		for (Option firstOption : firstOptions.getOptions())
			options.addOption(firstOption);

		options.addOption(deleteOption);			// d
		options.addOption(fromOption);				// f
													// h (help)
		options.addOption(killOption);				// k
		options.addOption(logLevelOption);			// l
		options.addOption(mapOption);				// m
		options.addOption(nameOption);				// n
		options.addOption(outputOption);			// o
		options.addOption(preserveOption);			// p
		options.addOption(replaceOption);			// r
		options.addOption(scanOption);				// s
		options.addOption(stacksOption);			// S
		options.addOption(toOption);				// t
		options.addOption(unknownOption);			// u
	
		return options;
	}

	////////////////////////////////////////////////////////////////////////////////
	private static void verifyFileParameterOrExit(String fileName, String paramName)
	////////////////////////////////////////////////////////////////////////////////
	{
		File file = new File(fileName);
		if (! file.exists())
		{
			System.err.println("ERROR: " + paramName + " does not exist");
			System.exit(1);
		}
		if (! file.isFile())
		{
			System.err.println("ERROR: " + paramName + " is not a file");
			System.exit(1);
		}
	}

	/////////////////////////////////////////////////////////////////////////////
	private static String[] getFileContent(String fileName, String paramName)
	/////////////////////////////////////////////////////////////////////////////
	{
		List<String> unCommentedLines = new ArrayList<String>();

		BufferedReader reader;
		try
		{
			reader = new BufferedReader(new FileReader(fileName));
			String line = reader.readLine();
			while (line != null)
			{
				if (! line.matches("^[ 	]*#.*$"))
					unCommentedLines.add(line);
				line = reader.readLine();
			}
			reader.close();
		}
		catch (Exception e)
		{
			System.err.println("ERROR: reading " + paramName + ": " + e.getMessage());
			System.exit(1);
		}

		return unCommentedLines.toArray(new String[0]);
	}

	//////////////////////////////////////////////////////////////////////////////////
	private static List<String> getChunksListOrExit(String parameter, String paramName)
	//////////////////////////////////////////////////////////////////////////////////
	{
		List<String> chunksList = new ArrayList<String>();
		String[] elements;

		if (parameter.charAt(0) == '@')
		{
			verifyFileParameterOrExit(parameter.substring(1), paramName);
			elements = getFileContent(parameter.substring(1), paramName);
		}
		else
			elements = parameter.split("/");


		for (int i = 0; i < elements.length; i++)
		{
			String[] coordinate = elements[i].split(",");
			if (coordinate.length != 2)
			{
				System.err.println("ERROR: " + paramName + " contains an invalid chunk ccordinates: " + elements[i]);
				System.exit(1);
			}

			try
			{
				String strCoord = Integer.valueOf(coordinate[0]) + "," + Integer.valueOf(coordinate[1]);
				chunksList.add(strCoord);
			}
			catch (NumberFormatException  e)
			{
				System.err.println("ERROR: " + paramName + " contains an invalid chunk ccordinates: " + elements[i]);
				System.exit(1);
			}
		}

		return chunksList;
	}

	/////////////////////////////////////////////////////////////////////////////////
	private static List<String> getKillListOrExit(String parameter, String paramName)
	/////////////////////////////////////////////////////////////////////////////////
	{
		List<String> entitiesList = new ArrayList<String>();
		String[] elements;

		if (parameter.charAt(0) == '@')
		{
			verifyFileParameterOrExit(parameter.substring(1), paramName);
			elements = getFileContent(parameter.substring(1), paramName);
		}
		else
			elements = parameter.split("/");

		for (int i = 0; i < elements.length; i++)
			if (elements[i] != null && ! elements[i].isEmpty())
				entitiesList.add(elements[i]);

		return entitiesList;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////°///////////
	private static HashMap<String, String> getReplacementListOrExit(char type, String parameter, String paramName)
	//////////////////////////////////////////////////////////////////////////////////°///////////////////////////
	{
		HashMap<String, String> thingsMap = new HashMap<String, String>();
		String[] elements;

		if (parameter.charAt(0) == '@')
		{
			verifyFileParameterOrExit(parameter.substring(1), paramName);
			elements = getFileContent(parameter.substring(1), paramName);
		}
		else
			elements = parameter.split("/");

		for (int i = 0; i < elements.length; i++)
			if (elements[i] != null && ! elements[i].isEmpty())
			{
				String[] parts = elements[i].split(",");
				if (parts.length != 3)
				{
					System.err.println("ERROR: " + paramName + " contains an invalid block or item replacement specification: " + elements[i]);
					System.exit(1);
				}

				// it's not possible at this time to check if the following replacement block or item exists or not as level.dat file is not yet processed...
				if (parts[0].charAt(0) == type)
					thingsMap.put(parts[1], parts[2]);
			}

		return thingsMap;
	}

	//////////////////////////////////////////////////////////////
	private static void verifyBlockReplacements() throws Exception
	//////////////////////////////////////////////////////////////
	{
		for (String blockToReplace : blocksToReplace.keySet())
		{
			String blockReplacement = blocksToReplace.get(blockToReplace);
			String blockReplacementWithoutData = blockReplacement.replaceAll(":[0-9]*$", "");
			if (! blockReplacementWithoutData.equalsIgnoreCase("DELETE") && ! blockReplacementWithoutData.equalsIgnoreCase("AMBIANT"))
			{
				// this will throw an exception if the block replacement is unknown
				int value = blocksAndItems.getBlockValue(blockReplacementWithoutData);
			}
		}
	}

	/////////////////////////////////////////////////////////////
	private static void verifyItemReplacements() throws Exception
	/////////////////////////////////////////////////////////////
	{
		for (String itemToReplace : itemsToReplace.keySet())
		{
			String itemReplacement = itemsToReplace.get(itemToReplace);
			String itemReplacementWithoutData = itemReplacement.replaceAll(":[0-9]*$", "");
			if (! itemReplacementWithoutData.equalsIgnoreCase("DELETE"))
			{
				// this will throw an exception if the item replacement is unknown
				int value = blocksAndItems.getItemValue(itemReplacementWithoutData);
			}
		}
	}

	////////////////////////////////////////////////////////////
	public static void main(String[] args) throws ParseException
	////////////////////////////////////////////////////////////
	{
		Options firstOptions = configHelpParameter();
		Options options = configParameters(firstOptions);
		CommandLineParser parser = new DefaultParser();
 		String helpHeader = "       regionFile1.mca [regionFile2.mca ... regionFileN.mca]\n\n";
		String helpFooter = "\nFor more information, please visit our Web site at:\nhttps://lotr-minecraft-mod-exiles.fandom.com/wiki/Minecraft_Region_Scanner\n\n";

		// Display usage?
		CommandLine firstLine = parser.parse(firstOptions, args, true);
		if (firstLine.hasOption("help"))
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("RegionScanner", helpHeader, options, helpFooter, true);
			System.exit(0);
		}

		// Process command line
		try
		{
			CommandLine command = parser.parse(options, args);
			regionFilesToProcess = command.getArgList();

			if (command.hasOption("delete"))
				chunksToDelete = getChunksListOrExit(command.getOptionValue("delete"), "delete parameter");
			if (command.hasOption("from"))
			{
				verifyFileParameterOrExit(command.getOptionValue("from", ""), "from parameter");
				fromLevel = command.getOptionValue("from", "");
			}
			else
			{
				// Use the level.dat file in the current directory if it exists
				File file = new File("level.dat");
				if (file.exists() && file.isFile())
					fromLevel = "level.dat";
			}
			if (command.hasOption("kill"))
				entitiesToKill = getKillListOrExit(command.getOptionValue("kill"), "kill parameter");
			if (command.hasOption("logLevel"))
			{
				String logLevelParam = command.getOptionValue("logLevel", "0");
				try
				{
					logLevel = Integer.valueOf(logLevelParam);
				}
				catch (Exception e)
				{
					System.err.println("ERROR: logLevel parameter must be a number");
					System.exit(1);
				}
				if (logLevel < 0)
				{
					System.err.println("ERROR: logLevel parameter must be >= 0");
					System.exit(1);
				}
			}
			if (command.hasOption("map"))
			{
				verifyFileParameterOrExit(command.getOptionValue("map", ""), "map parameter");
				mapOfMiddleEarth = command.getOptionValue("map", "");
			}
			else
			{
				// Use the map.png file in the current directory if it exists
				File file = new File("map.png");
				if (file.exists() && file.isFile())
					mapOfMiddleEarth = "map.png";
			}
			if (command.hasOption("rename"))
				fixNames = true;
			if (command.hasOption("output"))
			{
				output = command.getOptionValue("output", "");
				if (!(output.equals("new") || output.equals("old") || output.equals("overwrite")))
				{
					System.err.println("ERROR: output parameter must be \"new\", \"old\" or \"overwrite\"");
					System.exit(1);
				}
			}
			else
				output = "new";
			if (command.hasOption("preserve"))
				chunksToPreserve = getChunksListOrExit(command.getOptionValue("preserve"), "preserve parameter");
			if (command.hasOption("replace"))
			{
				blocksToReplace = getReplacementListOrExit('b', command.getOptionValue("replace"), "replace parameter");
				itemsToReplace = getReplacementListOrExit('i', command.getOptionValue("replace"), "replace parameter");
			}
			if (command.hasOption("scan"))
				scan = true;
			if (command.hasOption("stacks"))
				fixStacks = true;
			if (command.hasOption("to"))
			{
				verifyFileParameterOrExit(command.getOptionValue("to", ""), "to parameter");
				toLevel = command.getOptionValue("to", "");
			}
			if (command.hasOption("unknown"))
				fixUnknown = true;

			// Verify options requirements
			if (regionFilesToProcess == null || regionFilesToProcess.size() == 0)
			{
        		System.err.println( "WARNING: no region file to process");
				System.exit(0);
			}
			else if (fixUnknown == false && chunksToDelete == null && entitiesToKill == null && fixNames == false && chunksToPreserve == null && blocksToReplace == null && itemsToReplace == null && scan == false && fixStacks == false && toLevel.equals(""))
			{
        		System.err.println( "WARNING: no actions specified");
				System.exit(0);
			}
			else if (command.hasOption("delete") && command.hasOption("preserve"))
			{
        		System.err.println( "ERROR: options Delete and Preserve cannot be used at the same time");
				System.exit(1);
			}
		}
		catch (ParseException e)
		{
        	System.err.println( "ERROR: " + e.getMessage() + "\n");

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("RegionScanner", helpHeader, options, helpFooter, true);
			System.exit(1);
		}

		// Load level data
		try
		{
			if (fromLevel.equals(""))
				blocksAndItems = LevelFile.getDefaultBlocksAndItemsMap();
			else
				blocksAndItems = LevelFile.getBlocksAndItemsMap(fromLevel);

			if (! toLevel.equals(""))
			{
				newBlocksAndItems = LevelFile.getBlocksAndItemsMap(toLevel);

				modifiedBlocks = blocksAndItems.getBlocksDiff(newBlocksAndItems);
				modifiedItems = blocksAndItems.getItemsDiff(newBlocksAndItems);

				if ((modifiedBlocks == null || modifiedBlocks.isEmpty() == true) && (modifiedItems == null || modifiedItems.isEmpty()== true))
				{
        			System.err.println( "WARNING: Blocks & Items values are the same in the two level.dat files");
					toLevel = "";
				}
			}

			if (blocksToReplace != null)
				verifyBlockReplacements();
			if (itemsToReplace != null)
				verifyItemReplacements();
		}
		catch (Exception e)
		{
        	System.err.println( "ERROR: " + e.getMessage());
			if (logLevel > 0)
				e.printStackTrace();
			System.exit(1);
		}

		// Execute specified actions for each region file to process
		for (String regionFile : regionFilesToProcess)
		{
			Path pathToRegionFile = Paths.get(regionFile);
			RegionFile region = new RegionFile();
			try
			{
				region.loadRegion(pathToRegionFile);	
			}
			catch (Exception e)
			{
				System.err.println("ERROR: invalid region file " + regionFile + ": " + e.getMessage());
				if (logLevel > 0)
					e.printStackTrace();
				continue;
			}

			Path pathToNewRegionFile = null;
			if (output.equals("new"))
				pathToNewRegionFile = Paths.get(regionFile + ".new");
			else if (output.equals("old"))
			{
				File oldName = new File(regionFile);
				File newName = new File(regionFile + ".old");
				if (newName.exists())
				{
        			System.err.println( "ERROR: cannot backup region file \"" + regionFile + "\" because \"" + regionFile + ".old\" already exists");
					System.exit(1);
				}
				if (oldName.renameTo(newName) == false)
				{
        			System.err.println( "ERROR: while attempting to backup region file \"" + regionFile + "\" to \"" + regionFile + ".old\"");
					System.exit(1);
				}
				pathToNewRegionFile = Paths.get(regionFile);
			}
			else // overwrite
			{
				File oldName  = new File(regionFile);
        		if (oldName.delete() == false)
				{
        			System.err.println( "ERROR: while attempting to delete original region file \"" + regionFile + "\"");
					System.exit(1);
				}
				pathToNewRegionFile = Paths.get(regionFile);
			}

			try
			{
				System.out.println("Processing region file: " + regionFile);
				region.processRegion(pathToNewRegionFile);
			}
			catch (Exception e)
			{
				System.err.println("ERROR: failed to process region file " + regionFile + ": " + e.getMessage());
				if (logLevel > 0)
					e.printStackTrace();
				continue;
			}
		}
		
		System.exit(0);
	}
}
