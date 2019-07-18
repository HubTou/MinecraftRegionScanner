package org.tournier.RegionScanner;

import java.util.HashMap;

public class BlocksAndItemsMap
{
	public HashMap<String, Integer> blocksValueFromId = null;
	public HashMap<String, Integer> itemsValueFromId = null;
	public HashMap<Integer, String> blocksIdFromValue = null;
	public HashMap<Integer, String> itemsIdFromValue = null;

	///////////////////////////
	public BlocksAndItemsMap()
	///////////////////////////
	{
		blocksValueFromId = new HashMap<String, Integer>();
		itemsValueFromId = new HashMap<String, Integer>();
		blocksIdFromValue = new HashMap<Integer, String>();
		itemsIdFromValue = new HashMap<Integer, String>();
	}

	//////////////////////////////////////////////////////
	public void put(String id, int value) throws Exception
	//////////////////////////////////////////////////////
	{
		char type = id.charAt(0);
		String name= id.substring(1);

		if (type == 1)
		{
			blocksValueFromId.put(name, value);
			blocksIdFromValue.put(value, name);

			if (RegionScanner.logLevel > 1)
				System.out.println("    DEBUG: block " + name + "=" + value);
		}
		else if (type == 2)
		{
			itemsValueFromId.put(name, value);
			itemsIdFromValue.put(value, name);

			if (RegionScanner.logLevel > 1)
				System.out.println("    DEBUG: item " + name + "=" + value);
		}
		else
			throw new Exception("ID is not a block or item");
	}

	//////////////////////////////////////////////////////
	public String getBlockName(int value) throws Exception
	//////////////////////////////////////////////////////
	{
		if (! blocksIdFromValue.containsKey(value))
			throw new Exception("value " + value + " is unknown in blocks IDs");

		return blocksIdFromValue.get(value);
	}

	/////////////////////////////////////////////////////
	public String getItemName(int value) throws Exception
	/////////////////////////////////////////////////////
	{
		if (! itemsIdFromValue.containsKey(value))
			throw new Exception("value " + value + " is unknown in items IDs");

		return itemsIdFromValue.get(value);
	}

	//////////////////////////////////////////////////////
	public int getBlockValue(String name) throws Exception
	//////////////////////////////////////////////////////
	{
		if (! blocksValueFromId.containsKey(name))
			throw new Exception("name " + name + " is unknown in blocks names");

		return blocksValueFromId.get(name);
	}

	/////////////////////////////////////////////////////
	public int getItemValue(String name) throws Exception
	/////////////////////////////////////////////////////
	{
		if (! itemsValueFromId.containsKey(name))
			throw new Exception("name " + name + " is unknown in items names");

		return itemsValueFromId.get(name);
	}

	///////////////////////////////////////////////////////////////////////////////
	public HashMap<Integer, Integer> getBlocksDiff(BlocksAndItemsMap newMap)
	///////////////////////////////////////////////////////////////////////////////
	{
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		// Add blocks whose value has changed to the map
		// (We don't care about new blocks)
		for (String key : blocksValueFromId.keySet())
		{
			int value = blocksValueFromId.get(key);
			int newValue = newMap.blocksValueFromId.get(key);
			if (value != newValue)
			{
				map.put(value, newValue);
				if (RegionScanner.logLevel > 0)
					System.out.println("DEBUG: modified block " + key + ": " + value + " -> " + newValue);
			}
		}
		return map;
	}

	///////////////////////////////////////////////////////////////////////////////
	public HashMap<Integer, Integer> getItemsDiff(BlocksAndItemsMap newMap)
	///////////////////////////////////////////////////////////////////////////////
	{
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		// Add items whose value has changed to the map
		// (We don't care about new items)
		for (String key : itemsValueFromId.keySet())
		{
			int value = itemsValueFromId.get(key);
			int newValue = newMap.itemsValueFromId.get(key);
			if (value != newValue)
			{
				map.put(value, newValue);
				if (RegionScanner.logLevel > 0)
					System.out.println("DEBUG: modified item " + key + ": " + value + " -> " + newValue);
			}
		}
		return map;
	}
}
