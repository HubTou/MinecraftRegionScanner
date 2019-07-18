package org.tournier.RegionScanner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import net.querz.nbt.*;

public class RegionFile
{
	public static final int INT_SIZE = 4;
	public static final int REGION_WIDTH_IN_CHUNKS = 32;
	public static final int CHUNKS_PER_REGION = REGION_WIDTH_IN_CHUNKS * REGION_WIDTH_IN_CHUNKS;
	public static final int CHUNK_WIDTH_IN_BLOCKS = 16;
	public static final int CHUNK_HEIGTH_IN_BLOCKS = 256;
	public static final int SECTION_HEIGTH_IN_BLOCKS = 16;
	public static final int SECTIONS_PER_CHUNK = CHUNK_HEIGTH_IN_BLOCKS / SECTION_HEIGTH_IN_BLOCKS;
	public static final int BLOCKS_PER_SECTION = CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS * SECTION_HEIGTH_IN_BLOCKS;
	public static final int MAX_BIOMES = 256; // ?
	public static final int MAX_BLOCK_ID = 4096;
	public static final int MAX_ITEM_ID = 8192; // ?
	public static final int MAX_BLOCK_DATA = 16;
	public static final int MAX_ITEM_DATA = 4096; // ?

	public static final int UNDEFINED_COMPRESSION_TYPE = 0;
	public static final int COMPRESSION_TYPE_GZIP = 1;
	public static final int COMPRESSION_TYPE_ZLIB = 2;

	// An in-memory region file
	private int xRegion;
	private int zRegion;
	private int[] offset = new int[CHUNKS_PER_REGION];
	private int[] sectors = new int[CHUNKS_PER_REGION];
	private int[] timestamp = new int[CHUNKS_PER_REGION];
	private int[] length = new int[CHUNKS_PER_REGION];
	private byte[] compressionType = new byte[CHUNKS_PER_REGION];
	private byte[][] compressedData = new byte[CHUNKS_PER_REGION][];
	private CompoundTag[] chunkRoot = new CompoundTag[CHUNKS_PER_REGION];
	private boolean isRegionModified;
	
	// Global stats counters
	int convertedItemsCount = 0;
	int	convertedItemCount[] = new int[MAX_ITEM_ID];
	int unknownItemsCount = 0;
	int deletedItemsCount = 0;
	int	deletedItemCount[] = new int[MAX_ITEM_ID];
	int replacedItemsCount = 0;
	int	replacedItemCount[] = new int[MAX_ITEM_ID];
	int translatedNamesCount = 0;

	///////////////////
	public RegionFile()
   	///////////////////
	{
		xRegion = 0;
		zRegion = 0;
		for (int i = 0; i < CHUNKS_PER_REGION; i++)
		{
			// initializing arrays to 0 is already done by Java but I do it for readability...
			offset[i] = 0;
			sectors[i] = 0;
			timestamp[i] = 0;
			length[i] = 0;
			compressionType[i] = UNDEFINED_COMPRESSION_TYPE;
			compressedData[i] = null;
			chunkRoot[i] = null;
		}
		isRegionModified = false;
	}

	///////////////////////////////////////////////////////
	public void loadRegion(Path fileName) throws Exception
	///////////////////////////////////////////////////////
	{
		File fileObject = new File(fileName.toString());
		if (! fileObject.exists())
			throw new Exception("File not found");
		
		String[] parts = fileObject.getName().split("\\.");
		if (parts.length >= 4 && parts[0].equalsIgnoreCase("r") && parts[3].equalsIgnoreCase("mca"))
		{
			xRegion = Integer.valueOf(parts[1]);
			zRegion = Integer.valueOf(parts[2]);
		}

		RandomAccessFile file = new RandomAccessFile(fileObject, "r");
		if (file.length() < 2 * INT_SIZE * CHUNKS_PER_REGION)
		{
			file.close();		   
			throw new Exception("Invalid file structure (file is not big enough to contain a valid region file header)");
		}
		if ((file.length() & 0xfff) != 0)
		{
			file.close();
			throw new Exception("Invalid file structure (file size is not a multiple of 4K)");
		}
		
		// Load the header
		for (int i = 0; i < CHUNKS_PER_REGION; i++)
		{
			int j = file.readInt();
			offset[i] = (j & 0xFFFFFF00) >> 8;
			sectors[i] = (j & 0x000000FF);
		}
		for (int i = 0; i < CHUNKS_PER_REGION; i++)
			timestamp[i] = file.readInt();

		// Load the chunks
		for (int i = 0; i < CHUNKS_PER_REGION; i++)
		{
			if (offset[i] != 0)
			{
				// Move to the starting position of the chunk
				file.seek(offset[i] * INT_SIZE * CHUNKS_PER_REGION);
				
				// Load the chunk header
				length[i] = file.readInt();
				compressionType[i] = file.readByte();
				
				// Verify the chunk header
				if (length[i] == 0)
				{
					file.close();
					throw new Exception("Empty chunk");
				}
				if (length[i] > sectors[i] * INT_SIZE * CHUNKS_PER_REGION)
				{
					file.close();
					throw new Exception("Invalid chunk length");
				}
				if (! (compressionType[i] == COMPRESSION_TYPE_GZIP || compressionType[i] == COMPRESSION_TYPE_ZLIB))
				{
					file.close();		   
					throw new Exception("Unknown compression type");
				}
			
				// Load the chunk compressed data
				compressedData[i] = new byte[length[i] - 1];
				file.read(compressedData[i]);

				// display what we have now, in case of decompression issues
				if (RegionScanner.logLevel > 2)
				{
					System.out.println("offset[" + i + "]=" + offset[i]);
					System.out.println("sectors[" + i + "]=" + sectors[i]);
					System.out.println("timestamp[" + i + "]=" + timestamp[i]);
					System.out.println("length[" + i + "]=" + length[i]);
					if (compressionType[i] == COMPRESSION_TYPE_GZIP)
						System.out.println("compressionType[" + i + "]=GZIP");
					else if (compressionType[i] == COMPRESSION_TYPE_ZLIB)
						System.out.println("compressionType[" + i + "]=ZLIB");
				}

				// Uncompress the chunk (Zlib compression is not handled by Querz NBT library for NBT objeccts...)
				ByteArrayInputStream bais = new ByteArrayInputStream(compressedData[i]);
				if (compressionType[i] == COMPRESSION_TYPE_ZLIB)
				{
					if (RegionScanner.logLevel > 2)
						System.out.println("DEBUG: { ZLIB decompression of chunk[" + i + "]");
					InflaterInputStream iis = new InflaterInputStream(bais);
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int nRead;
					byte[] data = new byte[1024];
					while ((nRead = iis.read(data, 0, data.length)) != -1)
					{
						buffer.write(data, 0, nRead);
					}
					buffer.flush();

					// now use the uncompressed data
					DataInputStream dais = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
					chunkRoot[i] = (CompoundTag) Tag.deserialize(dais, Tag.DEFAULT_MAX_DEPTH);
					if (RegionScanner.logLevel > 2)
						System.out.println("DEBUG: } ZLIB decompression of chunk[" + i + "]");
				}
				else if (compressionType[i] == COMPRESSION_TYPE_GZIP)
				{
					if (RegionScanner.logLevel > 2)
						System.out.println("DEBUG: { GZIP decompression of chunk[" + i + "]");
					GZIPInputStream iis = new GZIPInputStream(bais);
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int nRead;
					byte[] data = new byte[1024];
					while ((nRead = iis.read(data, 0, data.length)) != -1)
					{
						buffer.write(data, 0, nRead);
					}
					buffer.flush();

					// now use the uncompressed data
					DataInputStream dais = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
					chunkRoot[i] = (CompoundTag) Tag.deserialize(dais, Tag.DEFAULT_MAX_DEPTH);
					if (RegionScanner.logLevel > 2)
						System.out.println("DEBUG: } GZIP decompression of chunk[" + i + "]");
				}

				if (RegionScanner.logLevel > 2)
				{
					System.out.println("chunkRoot[" + i + "]=" + chunkRoot[i].keySet());
					System.out.println("");
				}
			}
		}

		file.close();		   
	}

	///////////////////////////////////////////////
	private byte getNibble(byte[] array, int index)
	///////////////////////////////////////////////
	{
		byte fullByte = array[index / 2];

		if ((index % 2) == 0)
		{
			byte lowNibble = (byte) (fullByte & 0x0f);
			return lowNibble;
		}
		else
		{
			byte highNibble = (byte) ((fullByte & 0xf0) >> 4);
			return highNibble;
		}
	}

	///////////////////////////////////////////////////////////////////////////
	private void setNibble(byte[] array, int index, int value) throws Exception
	///////////////////////////////////////////////////////////////////////////
	{
		if (value < 16)
		{
			byte oldByte = array[index / 2];
			byte newByte;

			if ((index % 2) == 0) // low nibble
			{
				byte highNibble = (byte) (oldByte & 0xf0);
				newByte = (byte) (highNibble + value);
			}
			else // high nibble
			{
				byte lowNibble = (byte) (oldByte & 0x0f);
				newByte = (byte) ((value << 4) + lowNibble);
			}

			array[index /2] = newByte;
		}
		else
			throw new Exception("Nibble value is greater than 15");
	}

	///////////////////////////////////////////////////////////
	private byte[] compressChunk(int chunkPos) throws Exception
	///////////////////////////////////////////////////////////
	{
		byte[] compressedChunk = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		if (compressionType[chunkPos] == COMPRESSION_TYPE_ZLIB)
		{
			// first transform the root tag to a byte[]
			DataOutputStream dos = new DataOutputStream(baos);
			chunkRoot[chunkPos].serialize(dos, Tag.DEFAULT_MAX_DEPTH);
			byte[] serialisedChunk = baos.toByteArray();

			// then compress it with ZLIB
			Deflater deflater = new Deflater();
			deflater.setInput(serialisedChunk);
			baos = new ByteArrayOutputStream(serialisedChunk.length);
			deflater.finish();
			byte[] buffer = new byte[1024];
			while (! deflater.finished())
			{
				int count = deflater.deflate(buffer);
				baos.write(buffer, 0, count);
			}
			baos.close();
		}
		else if (compressionType[chunkPos] == COMPRESSION_TYPE_GZIP)
		{
// UNTESTED {
			GZIPOutputStream gos = new GZIPOutputStream(baos);
			DataOutputStream dos = new DataOutputStream(gos);
			chunkRoot[chunkPos].serialize(dos, Tag.DEFAULT_MAX_DEPTH);
// UNTESTED }
		}
		compressedChunk = baos.toByteArray();

		return compressedChunk;
	}

	///////////////////////////////////////////////////////
	public void saveRegion(Path fileName) throws Exception
	///////////////////////////////////////////////////////
	{
		File fileObject = new File(fileName.toString());
		if (fileObject.exists())
			throw new Exception("New region file already exists");
			
		RandomAccessFile file = new RandomAccessFile(fileObject, "rw");

		int newOffset = 2; // 2 * INT_SIZE * CHUNKS_PER_REGION
			
		// Write all chunks
		for (int c = 0; c < CHUNKS_PER_REGION; c++)
		{
			// Write an entry in the region file header
			file.seek(c * INT_SIZE);
			int location = 0;
			if (sectors[c] > 0)
				location = (newOffset << 8) + sectors[c];
			file.writeInt(location);
				
			// Write an entry in the region file timestamps table
			file.seek((INT_SIZE * CHUNKS_PER_REGION) + (c * INT_SIZE));
			file.writeInt(timestamp[c]);

			if (sectors[c] > 0)
			{
				// Write the chunk
				file.seek(newOffset * INT_SIZE * CHUNKS_PER_REGION);
				file.writeInt(length[c]);
				file.writeByte(compressionType[c]);
				file.write(compressedData[c]);

				// Pad with 0 if the chunk header+data is not a 4K multiple
				int m = (INT_SIZE + length[c]) % (INT_SIZE * CHUNKS_PER_REGION);
				if (m != 0)
					for (int j = 0; j < ((INT_SIZE * CHUNKS_PER_REGION) - m); j++)
						file.writeByte(0);
					
				newOffset += sectors[c];
			}
		}

		file.close();		   
	}

	///////////////////////////////////////
	private byte[] getByteArray(Tag<?> tag)
	///////////////////////////////////////
	{
		byte[] ba = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(baos))
		{
			tag.serialize(dos, Tag.DEFAULT_MAX_DEPTH);
		} catch (Exception e)
		{
			return null;
		}
		ba = baos.toByteArray();

		// the ba byte array starts with a 7 byte long prefix which is irrelevant
		return java.util.Arrays.copyOfRange(ba, 7, ba.length);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	private void showItemStack(CompoundTag tag, String additionalIndent) throws Exception
	/////////////////////////////////////////////////////////////////////////////////////
	{
		int value = 0;
		String name = "";
		byte count = 0;
		int damage = 0;

		if (tag.containsKey("id"))
		{
			value = tag.getShort("id");
			if (RegionScanner.toLevel.equals(""))
				name = RegionScanner.blocksAndItems.getItemName(value);
			else
				name = RegionScanner.newBlocksAndItems.getItemName(value);
			if (tag.containsKey("Count"))
				count = tag.getByte("Count");
			if (tag.containsKey("Damage"))
				damage = tag.getShort("Damage");

			if (damage > 0)
				System.out.println(additionalIndent + "       i# " + count + "x " + name + ":" + damage); 
			else
				System.out.println(additionalIndent + "       i# " + count + "x " + name); 
			if (tag.containsKey("tag"))
			{ 
				CompoundTag remainingTags = tag.getCompoundTag("tag");
				System.out.println(additionalIndent + "          # " + remainingTags);  
				if (remainingTags.containsKey("LOTRPouchData"))
				{
					System.out.println("          # pouch content =");  
					CompoundTag pouchContent = remainingTags.getCompoundTag("LOTRPouchData");
					if (pouchContent.containsKey("Items"))
					{
						ListTag items = pouchContent.getListTag("Items");
						for (Iterator i = items.iterator(); i.hasNext();)
							showItemStack((CompoundTag) i.next(), "    ");
					}
				}
			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	private int processItemStack(CompoundTag item, CompoundTag entity, String indicator) throws Exception
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	{
		int change = 0;

		if (item.containsKey("id"))
		{
			int value = item.getShort("id");
			String name = null;

			if (RegionScanner.fixUnknown == true)
			{
				try
				{
					name = RegionScanner.blocksAndItems.getItemName(value);
				}
				catch (Exception e)
				{
					unknownItemsCount++;

					if (indicator != null)
					{
						if (indicator.startsWith("Has"))
							entity.putBoolean(indicator, false);
						else if (indicator.endsWith("Empty"))
							entity.putBoolean(indicator, true);
					}

					int newValue = 0;
					item.putShort("id", (short) newValue);
					item.putByte("Count", (byte) 0);
					item.putShort("Damage", (short) 0);
					item.remove("tag");

					return -1;
				}
			}

			// the following code might generate an exception is option -u is not used:
			name = RegionScanner.blocksAndItems.getItemName(value);

			if (! RegionScanner.toLevel.equals("") && RegionScanner.modifiedItems.containsKey(value))
			{
				convertedItemsCount++;
				convertedItemCount[value]++;

				int newValue = RegionScanner.modifiedItems.get(value);
				item.putShort("id", (short) newValue);
				change = 1;
			}

			if (RegionScanner.itemsToReplace != null)
			{
				String itemName;
				short itemData = 0;

				if (change == 0)
					itemName = RegionScanner.blocksAndItems.getItemName(value);
				else
					itemName = RegionScanner.newBlocksAndItems.getItemName(value);

				if (item.containsKey("Damage"))
					itemData = item.getShort("Damage");

				String replacementItem = null;
				if ((replacementItem = RegionScanner.itemsToReplace.get(itemName)) != null || (replacementItem = RegionScanner.itemsToReplace.get(itemName + ":" + itemData)) != null)
				{
					if (replacementItem.equalsIgnoreCase("DELETE"))
					{
						deletedItemsCount++;
						deletedItemCount[value]++;
	
						if (indicator != null)
						{
							if (indicator.startsWith("Has"))
								entity.putBoolean(indicator, false);
							else if (indicator.endsWith("Empty"))
								entity.putBoolean(indicator, true);
						}
		
						int newValue = 0;
						item.putShort("id", (short) newValue);
						item.putByte("Count", (byte) 0);
						item.putShort("Damage", (short) 0);
						item.remove("tag");
	
						return -1;
					}
					else
					{
						replacedItemsCount++;
						replacedItemCount[value]++;
	
						String replacementItemName;
						short replacementItemData;
						int newValue;
	
						String[] parts = replacementItem.split(":");
						if (parts.length > 1 && isInteger(parts[parts.length - 1]))
						{
							replacementItemName = replacementItem.replaceAll(":[0-9]*$", "");
							replacementItemData = Short.valueOf(parts[parts.length - 1]);
						}
						else
						{
							replacementItemName = replacementItem;
							replacementItemData = 0;
						}
	
						if (change == 0)
							newValue = RegionScanner.blocksAndItems.getItemValue(replacementItemName);
						else
							newValue = RegionScanner.newBlocksAndItems.getItemValue(replacementItemName);
	
						// Count is resetted to 1 and tags are removed
						item.putShort("id", (short) newValue);
						item.putByte("Count", (byte) 1);
						item.putShort("Damage", replacementItemData);
						item.remove("tag");
	
						change = 1;

						// if a pouch is replaced by something else, it won't be a pouch anymore
						// if something is replaced by a pouch, it won't have any content yet
						name = ""; 
					}
				}
			}

			// a LOTR mod pouch is the only item that I know about that can contains items while throwed on the ground or placed in other containters
			if (name.equals("lotr:item.pouch") && item.containsKey("tag"))
			{
				CompoundTag remainingTags = item.getCompoundTag("tag");
				if (remainingTags.containsKey("LOTRPouchData"))
				{
					CompoundTag pouchContent = remainingTags.getCompoundTag("LOTRPouchData");
					if (pouchContent.containsKey("Items"))
					{
						int changes = change;

						ListTag items = pouchContent.getListTag("Items");
						for (Iterator i = items.iterator(); i.hasNext();)
						{
							change = processItemStack((CompoundTag) i.next(), null, null);
							if (change == 1)
								changes++;
							else if (change == -1)
							{
								changes++;
								i.remove();
							}
						}
						if (changes > 0)
							change = 1;
					}
				}
			}

			if (RegionScanner.fixNames == true && item.containsKey("tag"))
			{
				CompoundTag remainingTags = item.getCompoundTag("tag");
				if (remainingTags.containsKey("LOTROwner"))
				{
					String originalName = remainingTags.getString("LOTROwner");
					String newName = transliterateCyrillicToLatin(originalName);
					if (! newName.equals(originalName))
					{
						translatedNamesCount++;
						change = 1;
						remainingTags.putString("LOTROwner", newName);
					}
				}
				else if (remainingTags.containsKey("author"))
				{
					String originalName = remainingTags.getString("author");
					String newName = transliterateCyrillicToLatin(originalName);
					if (! newName.equals(originalName))
					{
						translatedNamesCount++;
						change = 1;
						remainingTags.putString("author", newName);
					}
				}
			}
		}

		return change;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	private int processItemValue(String tagName, String tagData, CompoundTag entity) throws Exception
	/////////////////////////////////////////////////////////////////////////////////////////////////
	{
		int change = 0;
		int value = entity.getInt(tagName);

		if (value > 0)
		{
			if (RegionScanner.fixUnknown == true)
			{
				try
				{
					String name = RegionScanner.blocksAndItems.getItemName(value);
				}
				catch (Exception e)
				{
					unknownItemsCount++;

					entity.putInt(tagName, 0);
					entity.putInt(tagData, 0);
					return 1;
				}
			}

			if (! RegionScanner.toLevel.equals("") && RegionScanner.modifiedItems.containsKey(value))
			{
				convertedItemsCount++;
				convertedItemCount[value]++;

				entity.putInt(tagName, RegionScanner.modifiedItems.get(value));
				change = 1;
			}

			if (RegionScanner.itemsToReplace != null)
			{
				// the following code might generate an exception is option -u is not used:
				String itemName = RegionScanner.blocksAndItems.getItemName(value);

				String replacementItem = null;
				if ((replacementItem = RegionScanner.itemsToReplace.get(itemName)) == null)
					return 0;

				if (replacementItem.equalsIgnoreCase("DELETE"))
				{
					deletedItemsCount++;
					deletedItemCount[value]++;
	
					entity.putInt(tagName, 0);
					entity.putInt(tagData, 0);
					return 1;
				}
				else
				{
					replacedItemsCount++;
					replacedItemCount[value]++;

					String replacementItemName;
					int replacementItemData;
					int newValue;

					String[] parts = replacementItem.split(":");
					if (parts.length > 1 && isInteger(parts[parts.length - 1]))
					{
						replacementItemName = replacementItem.replaceAll(":[0-9]*$", "");
						replacementItemData = Integer.valueOf(parts[parts.length - 1]);
					}
					else
					{
						replacementItemName = replacementItem;
						replacementItemData = 0;
					}

					if (change == 0)
						newValue = RegionScanner.blocksAndItems.getItemValue(replacementItemName);
					else
						newValue = RegionScanner.newBlocksAndItems.getItemValue(replacementItemName);

					entity.putInt(tagName, newValue);
					entity.putInt(tagData, replacementItemData);

					return 1;
				}
			}
		}

		return change;
	}

	/////////////////////////////////////////////////////////////
	private int processItems(CompoundTag entity) throws Exception
	/////////////////////////////////////////////////////////////
	// return values:
	//	0 no change to the entity
	//	1 the entity has been modified
	//	-1 the entity needs to be fully removed
	/////////////////////////////////////////////////////////////
	{
		int change = 0;

		String id = entity.getString("id");
		if (id.equals("FlowerPot") && entity.containsKey("Item"))
			return processItemValue("Item", "Data", entity);
		else if (id.equals("LOTRFlowerPot") && entity.containsKey("PlantID"))
			return processItemValue("PlantID", "PlantMeta", entity);
		else if (entity.containsKey("Item"))
		{
			change = processItemStack(entity.getCompoundTag("Item"), null, null);
			if (change == -1)
			{
				if (id.equals("Item"))
					return -1;
				else if (id.equals("ItemFrame"))
				{
					entity.remove("Item");
					entity.remove("ItemRotation");
					entity.remove("ItemDropChance");
					return 1;
				}
				else
				{
					System.out.println("DEBUG: I don't know how to delete " + id + " items. Removing the whole entity for safety. Please report this!");
					return -1;
				}
			}
		}
		else if (entity.containsKey("Items"))
		{
			int changes = 0;
			ListTag items = entity.getListTag("Items");

			for (Iterator i = items.iterator(); i.hasNext();)
			{
				change = processItemStack((CompoundTag) i.next(), null, null);
				if (change == 1)
					changes++;
				else if (change == -1)
				{
					changes++;
					i.remove();
				}
			}
			if (changes > 0)
				change = 1;
		}
		else if (id.equals("LOTRWeaponRack") && entity.containsKey("WeaponItem"))
		{
			change = processItemStack(entity.getCompoundTag("WeaponItem"), entity, "HasWeapon");
			if (change == -1)
			{
				entity.remove("WeaponItem");
				change = 1;
			}
		}
		else if (id.equals("LOTRPlate") && entity.containsKey("FoodItem"))
		{
			change = processItemStack(entity.getCompoundTag("FoodItem"), entity, "PlateEmpty");
			if (change == -1)
			{
				entity.remove("FoodItem");
				change = 1;
			}
		}
		else if (id.equals("LOTRMug") && entity.containsKey("MugItem"))
		{
			change = processItemStack(entity.getCompoundTag("MugItem"), entity, "HasMugItem");
			if (change == -1)
			{
				entity.remove("MugItem");
				change = 1;
			}
		}

		return change;
	}

	//////////////////////////////////////////
	public boolean isInteger(String parameter)
	//////////////////////////////////////////
	{
		try
		{
			int i = Integer.parseInt(parameter);
		}
		catch (Exception e)
		{
 			return false;
		}
		return true;
	}

	//////////////////////////////////////////////////////////////
	public static String transliterateCyrillicToLatin(String text)
	//////////////////////////////////////////////////////////////
	// This function borrowed from lxknvlk (https://stackoverflow.com/users/3060279/lxknvlk)
	// at https://stackoverflow.com/questions/16273318/transliteration-from-cyrillic-to-latin-icu4j-java
	//////////////////////////////////////////////////////////////
	{
		char[] abcCyr =   {' ','а','б','в','г','д','е','ё', 'ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х', 'ц','ч', 'ш','щ','ъ','ы','ь','э', 'ю','я','А','Б','В','Г','Д','Е','Ё', 'Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У','Ф','Х', 'Ц', 'Ч','Ш', 'Щ','Ъ','Ы','Ь','Э','Ю','Я','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		String[] abcLat = {" ","a","b","v","g","d","e","e","zh","z","i","y","k","l","m","n","o","p","r","s","t","u","f","h","ts","ch","sh","sch", "","i", "","e","ju","ja","A","B","V","G","D","E","E","Zh","Z","I","Y","K","L","M","N","O","P","R","S","T","U","F","H","Ts","Ch","Sh","Sch", "","I", "","E","Ju","Ja","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < text.length(); i++)
		{
			boolean found = false;

			for (int x = 0; x < abcCyr.length; x++ )
				if (text.charAt(i) == abcCyr[x])
				{
					builder.append(abcLat[x]);
					found = true;
					break;
				}

			if (found == false)
				builder.append(text.charAt(i));
		}

		return builder.toString();
	}

	////////////////////////////////////////////////////////////
	public void processRegion(Path newFileName) throws Exception
	////////////////////////////////////////////////////////////
	{
		boolean[][] hasAddSubSection = new boolean[CHUNKS_PER_REGION][SECTIONS_PER_CHUNK];

		// Stats counters
		int chunksCount = 0;
		List<int[]> chunksList = new ArrayList<int[]> ();
		int chunksDeletedCount = 0;

		int sectionsCount = 0;

		int biomeCount[] = new int[CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS];
		int lotrBiomeCount[] = new int[MAX_BIOMES];
		int lotrBiomeVariantsCount = 0;
		int lotrBiomeVariantCount[] = new int[CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS];

		int blocksCount = 0;
		int	blockCount[][] = new int[MAX_BLOCK_ID][MAX_BLOCK_DATA];
		int unknownBlocksCount = 0;
		int convertedBlocksCount = 0;
		int	convertedBlockCount[] = new int[MAX_BLOCK_ID];
		int deletedBlocksCount = 0;
		int	deletedBlockCount[] = new int[MAX_BLOCK_ID];
		int replacedBlocksCount = 0;
		int	replacedBlockCount[] = new int[MAX_BLOCK_ID];

		convertedItemsCount = 0;
		deletedItemsCount = 0;
		replacedItemsCount = 0;
		for (int i = 0; i < MAX_ITEM_ID; i++)
		{
			convertedItemCount[i] = 0;
			deletedItemCount[i] = 0;
			replacedItemCount[i] = 0;
		}

		int entitiesCount = 0;
		HashMap<String, Integer> entityCount = new HashMap<String, Integer>();
		int entitiesKilledCount = 0;
		HashMap<String, Integer> entityKilledCount = new HashMap<String, Integer>();

		int tileEntitiesCount = 0;
		HashMap<String, Integer> tileEntityCount = new HashMap<String, Integer>();

		// Details
		int[][] heightMap = new int[CHUNKS_PER_REGION][CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS];
		ListTag[] tileEntities = new ListTag[CHUNKS_PER_REGION];
		ListTag[] entities = new ListTag[CHUNKS_PER_REGION];

		for (int c = 0; c < CHUNKS_PER_REGION; c++)
		{
			if (offset[c] != 0)
			{
				HashMap<String, String> deletedBlocks = new HashMap<String, String>();
				HashMap<String, String> modifiedBlocks = new HashMap<String, String>();
				boolean modifiedHeightMap = false;

				// chunkRoot[c].keySet():
				// [Level]
				// [LOTRBiomeVariants, Level]

				if (! chunkRoot[c].containsKey("Level"))
					throw new Exception("Invalid chunk. No [Level] key");
				CompoundTag level = chunkRoot[c].getCompoundTag("Level");

				// level.keySet():
				// [LightPopulated, zPos, HeightMap, Sections, LastUpdate, V, Biomes, InhabitedTime, xPos, TerrainPopulated, TileEntities, Entities]

				// getting the x / z coordinates of the processed chunk
				if (! level.containsKey("xPos"))
					throw new Exception("Invalid chunk. No [level/xPos] key");
				if (! level.containsKey("zPos"))
					throw new Exception("Invalid chunk. No [level/zPos] key");
				int[] chunkCoordinates = new int[2];
				chunkCoordinates[0] = level.getInt("xPos");
				chunkCoordinates[1] = level.getInt("zPos");
				chunksList.add(chunkCoordinates);

				if ((RegionScanner.chunksToDelete != null && RegionScanner.chunksToDelete.contains(chunkCoordinates[0] + "," + chunkCoordinates[1])) || (RegionScanner.chunksToPreserve != null && ! RegionScanner.chunksToPreserve.contains(chunkCoordinates[0] + "," + chunkCoordinates[1])))
				{
					if (RegionScanner.logLevel > 1)
						System.out.println("Deleting chunk #" + c + " (" + chunkCoordinates[0] + "," + chunkCoordinates[1] + ")");

					chunksDeletedCount++;

					isRegionModified = true;
					offset[c] = 0;
					sectors[c] = 0;
					timestamp[c] = 0;
					length[c] = 0;
					compressionType[c] = UNDEFINED_COMPRESSION_TYPE;
					compressedData[c] = null;
					chunkRoot[c] = null;

					heightMap[c] = null;
					tileEntities[c] = null;
					entities[c] = null;
				}
				else
				{
					chunksCount++;

					// Loading the tile entities
					if (level.containsKey("TileEntities"))
						tileEntities[c] = level.getListTag("TileEntities");
	
					// Loading the height map
					if (level.containsKey("HeightMap"))
						heightMap[c] = level.getIntArray("HeightMap");
	
					// Processing the chunk's blocks
					if (level.containsKey("Sections"))
					{
						ListTag sections = level.getListTag("Sections");
						for (Iterator j = sections.iterator(); j.hasNext(); sectionsCount++)
						{
							boolean isSectionModified = false;
							int yPos;
							byte[] blocks = null;
							byte[] add = null;
							byte[] data = null;
	
							CompoundTag section = (CompoundTag) j.next();
	
							if (section.containsKey("Y"))
								yPos = (int) section.getByte("Y");
							else
								throw new Exception("Invalid chunk. No [level/Section/Y] key");
	
							// section.keySet():
							// [Add, Y, BlockLight, Data, Blocks, SkyLight]
							// [Y, BlockLight, Data, Blocks, SkyLight]
	
							if (section.containsKey("Blocks"))
							{
								ByteArrayTag bat = section.getByteArrayTag("Blocks");
								blocks = getByteArray(bat);
								if (blocks.length != BLOCKS_PER_SECTION)
									throw new Exception("Invalid chunk. [Level/Section/Blocks] length is different from " + BLOCKS_PER_SECTION + ": " + blocks.length);
							}
							else
							{
								blocks = new byte[BLOCKS_PER_SECTION];
								for (int k = 0; k < BLOCKS_PER_SECTION; k++)
									blocks[k] = 0;
							}
							if (section.containsKey("Add"))
							{
								hasAddSubSection[c][yPos] = true;
	
								ByteArrayTag bat = section.getByteArrayTag("Add");
								add = getByteArray(bat);
								if (add.length != (BLOCKS_PER_SECTION / 2))
									throw new Exception("Invalid chunk. [Level/Section/Add] length is different from " + (BLOCKS_PER_SECTION / 2) + ": " + add.length);
							}
							else
							{
								hasAddSubSection[c][yPos] = false;
	
								add = new byte[BLOCKS_PER_SECTION / 2];
								for (int k = 0; k < (BLOCKS_PER_SECTION / 2); k++)
									add[k] = 0;
							}
							if (section.containsKey("Data"))
							{
								ByteArrayTag bat = section.getByteArrayTag("Data");
								data = getByteArray(bat);
								if (data.length != (BLOCKS_PER_SECTION / 2))
									throw new Exception("Invalid chunk. [Level/Section/Data] length is different from " + (BLOCKS_PER_SECTION / 2) + ": " + data.length);
							}
							else
							{
								data = new byte[BLOCKS_PER_SECTION / 2];
								for (int k = 0; k < (BLOCKS_PER_SECTION / 2); k++)
									data[k] = 0;
							}
		
							blocksCount += BLOCKS_PER_SECTION;
							for (int pos = 0; pos < BLOCKS_PER_SECTION; pos++)
							{
								int blockId = (blocks[pos] & 0xff) + (getNibble(add, pos) << 8);
								int originalBlockId = blockId;
								int blockData = getNibble(data, pos);
	
								if (blockId < 0 || blockId >= MAX_BLOCK_ID || blockData < 0 || blockData >= MAX_BLOCK_DATA)
									throw new Exception("Invalid block. [Level/Section] out of range " + blockId + ":" + blockData);
	
								blockCount[blockId][blockData]++;
	
								String blockName = "";
								if (RegionScanner.fixUnknown == true)
								{
									try
									{
										blockName = RegionScanner.blocksAndItems.getBlockName(blockId);
									}
									catch (Exception e)
									{
										unknownBlocksCount++;
		
										isRegionModified = true;
										compressedData[c] = null;
										isSectionModified = true;
		
										blocks[pos] = (byte) 0; // Air
										setNibble(add, pos, 0);
										setNibble(data, pos, 0);
	
										int rX = (pos % CHUNK_WIDTH_IN_BLOCKS);
										int rY = (pos / (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS));
										int rZ = ((pos % (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS)) / CHUNK_WIDTH_IN_BLOCKS);
	
										int bX = xRegion + rX;
										int bY = (yPos * SECTION_HEIGTH_IN_BLOCKS)  + rY;
										int bZ = zRegion + rZ;
	
										// do we need to modify the HeightMap?
										if (heightMap[c] != null && heightMap[c][rX + (16 * rZ)] == bY + 1)
										{
											// we check if the blocks below are air up to the section floor
											int pos2 = pos - (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS);
											int minusY = 1;
											while (pos2 >= 0 && blocks[pos2] == 0)
											{
												pos2 -= (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS);
												minusY++;
											}
											heightMap[c][rX + (CHUNK_WIDTH_IN_BLOCKS * rZ)] -= minusY;
											modifiedHeightMap = true;
										}
		
										// take note of this block location for deleting potential tile entities
										deletedBlocks.put(bX + ":" + bY + ":" + bZ, "");
	
										continue;
									}
								}
								else
									blockName = RegionScanner.blocksAndItems.getBlockName(blockId);

								if (! RegionScanner.toLevel.equals("") && RegionScanner.modifiedBlocks.containsKey(blockId))
								{
									convertedBlocksCount++;
									convertedBlockCount[blockId]++;
	
									isRegionModified = true;
									compressedData[c] = null;
									isSectionModified = true;
	
									blockId = RegionScanner.modifiedBlocks.get(blockId);
	
									// if needed, an Add sub section will be saved
									if (blockId >= 256)
										hasAddSubSection[c][yPos] = true;
	
									blocks[pos] = (byte) (blockId % 256);
									setNibble(add, pos, blockId / 256);
								}

								if (RegionScanner.blocksToReplace != null)
								{
									String replacementBlock = null;

									if ((replacementBlock = RegionScanner.blocksToReplace.get(blockName)) == null && (replacementBlock = RegionScanner.blocksToReplace.get(blockName + ":" + blockData)) == null)
										continue;

									isRegionModified = true;
									compressedData[c] = null;
									isSectionModified = true;

									int rX = (pos % CHUNK_WIDTH_IN_BLOCKS);
									int rY = (pos / (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS));
									int rZ = ((pos % (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS)) / CHUNK_WIDTH_IN_BLOCKS);

									int bX = xRegion + rX;
									int bY = (yPos * SECTION_HEIGTH_IN_BLOCKS)  + rY;
									int bZ = zRegion + rZ;
	
									if (replacementBlock.equalsIgnoreCase("DELETE"))
									{
										deletedBlocksCount++;
										deletedBlockCount[originalBlockId]++;
	
										blocks[pos] = (byte) 0; // Air
										setNibble(add, pos, 0);
										setNibble(data, pos, 0);
	
										// do we need to modify the HeightMap?
										if (heightMap[c] != null && heightMap[c][rX + (16 * rZ)] == bY + 1)
										{
											// we check if the blocks below are air up to the section floor
											int pos2 = pos - (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS);
											int minusY = 1;
											while (pos2 >= 0 && blocks[pos2] == 0)
											{
												pos2 -= (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS);
												minusY++;
											}
											heightMap[c][rX + (CHUNK_WIDTH_IN_BLOCKS * rZ)] -= minusY;
											modifiedHeightMap = true;
										}
	
										// take note of this block location for deleting potential tile entities
										deletedBlocks.put(bX + ":" + bY + ":" + bZ, "");
									}
									else if (replacementBlock.equalsIgnoreCase("AMBIANT"))
									{
										replacedBlocksCount++;
										replacedBlockCount[originalBlockId]++;

										// examine the 26(max) surrounding blocks but only in the current section
										HashMap<String, Integer> ambiantBlocks = new HashMap<String, Integer>();
										for (int aY = -1 * (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS); aY <= (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS); aY += (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS))
											for (int aZ = -1 * CHUNK_WIDTH_IN_BLOCKS; aZ <= CHUNK_WIDTH_IN_BLOCKS; aZ += CHUNK_WIDTH_IN_BLOCKS)
												for (int aX = -1; aX <= 1; aX += 1)
												{
													int pos2 = pos + aY + aZ + aX;
													if (pos2 >= 0 && pos2 != pos && pos2 < MAX_BLOCK_ID)
													{
														int neighbourBlockId = (blocks[pos2] & 0xff) + (getNibble(add, pos2) << 8);
														int neighbourBlockData = getNibble(data, pos2);

														// we don't take into account same blocks with different data values
//														if (neighbourBlockId == blockId && neighbourBlockData == blockData)
														if (neighbourBlockId == blockId)
															continue;

														if (ambiantBlocks.containsKey(neighbourBlockId + ":" + neighbourBlockData))
															ambiantBlocks.put(neighbourBlockId + ":" + neighbourBlockData, ambiantBlocks.get(neighbourBlockId + ":" + neighbourBlockData) + 1);
														else
															ambiantBlocks.put(neighbourBlockId + ":" + neighbourBlockData, 1);
													}
												}

										// find the most represented block
										int highestValue = 0;
										String mostRepresentedBlock = "0:0";
										for (String ambiantBlock : ambiantBlocks.keySet())
										{
											int value = ambiantBlocks.get(ambiantBlock);
											if (value > highestValue)
											{
												highestValue = value;
												mostRepresentedBlock = ambiantBlock;
											}
										}

										String[] parts = mostRepresentedBlock.split(":");
										blocks[pos] = (byte) (Integer.valueOf(parts[0]) % 256);
										setNibble(add, pos, Integer.valueOf(parts[0]) / 256);
										setNibble(data, pos, Integer.valueOf(parts[1]));
	
										// if needed, an Add sub section will be saved
										if (Integer.valueOf(parts[0]) >= 256)
											hasAddSubSection[c][yPos] = true;
	
										// take note of this block location for replacing potential tile entities
										if (RegionScanner.toLevel.equals(""))
											modifiedBlocks.put(bX + ":" + bY + ":" + bZ, RegionScanner.blocksAndItems.getBlockName(Integer.valueOf(parts[0])) + ":" + parts[1]);
										else
											modifiedBlocks.put(bX + ":" + bY + ":" + bZ, RegionScanner.newBlocksAndItems.getBlockName(Integer.valueOf(parts[0])) + ":" + parts[1]);
									}
									else
									{
										replacedBlocksCount++;
										replacedBlockCount[originalBlockId]++;

										String replacementBlockName;
										int replacementBlockData;
										String[] parts = replacementBlock.split(":");
										if (parts.length > 1 && isInteger(parts[parts.length - 1]))
										{
											replacementBlockName = replacementBlock.replaceAll(":[0-9]*$", "");
											replacementBlockData = Integer.valueOf(parts[parts.length - 1]);
										}
										else
										{
											replacementBlockName = replacementBlock;
											// we reset the data to 0
											replacementBlockData = 0;
										}

										int newValue;
										if (RegionScanner.toLevel.equals(""))
											newValue = RegionScanner.blocksAndItems.getBlockValue(replacementBlockName);
										else
											newValue = RegionScanner.newBlocksAndItems.getBlockValue(replacementBlockName);

										blocks[pos] = (byte) (newValue % 256);
										setNibble(add, pos, newValue / 256);
										setNibble(data, pos, replacementBlockData);
	
										// if needed, an Add sub section will be saved
										if (newValue >= 256)
											hasAddSubSection[c][yPos] = true;
	
										// take note of this block location for replacing potential tile entities
										modifiedBlocks.put(bX + ":" + bY + ":" + bZ, replacementBlock);
									}
								}
							}
	
							if (isSectionModified)
							{
								section.putByteArray("Blocks", blocks);
								if (section.containsKey("Add"))
									section.remove("Add");
								if (hasAddSubSection[c][yPos])
									section.putByteArray("Add", add);
								section.putByteArray("Data", data);
							}
						}
					}
	
					// Processing the entities list
					if (level.containsKey("Entities"))
					{
						if (RegionScanner.entitiesToKill != null && RegionScanner.entitiesToKill.get(0).equals("ALL"))
						{
							entities[c] = level.getListTag("Entities");
	
							entitiesKilledCount += entities[c].size();
	
							isRegionModified = true;
							compressedData[c] = null;
		
							entities[c].clear();
							level.put("Entities", entities[c]);
						}
						else
						{
							entities[c] = level.getListTag("Entities");
							int changes = 0;
		
							for (Iterator k = entities[c].iterator(); k.hasNext();)
							{
								CompoundTag entity = (CompoundTag) k.next();
		
								if (entity.containsKey("id"))
								{
									String id = entity.getString("id");
	
									if (RegionScanner.entitiesToKill != null && RegionScanner.entitiesToKill.contains(id))
									{
										entitiesKilledCount++;
										if (entityKilledCount.containsKey(id))
										{
											int count = entityKilledCount.get(id) + 1;
											entityKilledCount.put(id, count);
										}
										else
											entityKilledCount.put(id, 1);
	
										k.remove();
										changes++;
									}
									else
									{
										entitiesCount++;
										if (entityCount.containsKey(id))
										{
											int count = entityCount.get(id) + 1;
											entityCount.put(id, count);
										}
										else
											entityCount.put(id, 1);
									}
								}
								else
									throw new Exception("Invalid entity in chunk. No [level/Entities/id] key");
		
								int change = processItems(entity);
								if (change == 1)
									changes++;
								else if (change == -1)
								{
									changes++;
									k.remove();
								}

								if (RegionScanner.fixNames == true && entity.containsKey("NPCName"))
								{
									String originalName = entity.getString("NPCName");
									String newName = transliterateCyrillicToLatin(originalName);
									if (! newName.equals(originalName))
									{
										translatedNamesCount++;
										changes++;
										entity.putString("NPCName", newName);
									}
								}
							}
		
							if (changes > 0)
							{
								isRegionModified = true;
								compressedData[c] = null;
		
								level.put("Entities", entities[c]);
							}
						}
					}
	
					// Processing the tile entities list
					if (level.containsKey("TileEntities"))
					{
						// tileEntities is already loaded...
						int changes = 0;
	
						for (Iterator k = tileEntities[c].iterator(); k.hasNext();)
						{
							CompoundTag tileEntity = (CompoundTag) k.next();
	
							if (! tileEntity.containsKey("x") || ! tileEntity.containsKey("y") || ! tileEntity.containsKey("z"))
								throw new Exception("Invalid entity in chunk. No [level/TileEntities/x-y-z] key");

							int x = tileEntity.getInt("x");
							int y = tileEntity.getInt("y");
							int z = tileEntity.getInt("z");

							if (deletedBlocks.containsKey(x + ":" + y + ":" + z))
							{
								k.remove();
								changes++;
							}
							else if (modifiedBlocks.containsKey(x + ":" + y + ":" + z))
							{
								k.remove();
								// WONTDO create NBT data if needed
								changes++;
							}
							else
							{
								if (tileEntity.containsKey("id"))
								{
									tileEntitiesCount++;
									String id = tileEntity.getString("id");
									if (tileEntityCount.containsKey(id))
									{
										int count = tileEntityCount.get(id) + 1;
										tileEntityCount.put(id, count);
									}
									else
										tileEntityCount.put(id, 1);
								}
								else
									throw new Exception("Invalid entity in chunk. No [level/TileEntities/id] key");
		
								int change = processItems(tileEntity);
								if (change == 1)
									changes++;
								else if (change == -1)
								{
									changes++;
									k.remove();
								}
							}
						}
	
						if (changes > 0)
						{
							isRegionModified = true;
							compressedData[c] = null;
	
							level.put("TileEntities", tileEntities[c]);
						}
					}
	
					// Processing the height map
					if (modifiedHeightMap)
						level.putIntArray("HeightMap", heightMap[c]);

					if (chunkRoot[c].containsKey("LOTRBiomeVariants"))
					{
						ByteArrayTag bat = chunkRoot[c].getByteArrayTag("LOTRBiomeVariants");
						byte[] lotrBiomes = getByteArray(bat);
						if (lotrBiomes.length != (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS))
							throw new Exception("Invalid chunk. [LOTRBiomeVariants] length is different from " + (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS) + ": " + lotrBiomes.length);
	
						for (int k = 0; k < (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS); k++)
						{
							int biome = (lotrBiomes[k] & 0xff);
							if (lotrBiomeVariantCount[biome] == 0)
								lotrBiomeVariantsCount++;
							lotrBiomeVariantCount[biome]++;
						}
					}
					else if (level.containsKey("Biomes"))
					{
						ByteArrayTag bat = level.getByteArrayTag("Biomes");
						byte[] biomes = getByteArray(bat);
						if (biomes.length != (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS))
							throw new Exception("Invalid chunk. [Level/Section/Biomes] length is different from " + (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS) + ": " + biomes.length);
	
						for (int k = 0; k < (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS); k++)
						{
							int biome = (biomes[k] & 0xff);
							biomeCount[biome]++;
						}
					}
	
					// if chunk modified
					if (compressedData[c] == null)
					{
						compressedData[c] = compressChunk(c);
						// adding 1 to length for the compression type byte
						length[c] = compressedData[c].length + 1;
						// adding 4 to length for sectors need for the length integer
						sectors[c] = ((length[c] + 4) / 4096) + ((((length[c] + 4) % 4096) > 0) ? 1 : 0);
 						Date date = new Date();
						long time = date.getTime();
						timestamp[c] = (int) (time / 1000);
					}
				}
			}
			else
			{
				heightMap[c] = null;
				tileEntities[c] = null;
				entities[c] = null;
			}
		}
		// end of chunks processing

		// CHUNKS STATISTICS
		if (RegionScanner.scan == true)
		{
			System.out.println("  # chunks = " + chunksCount);
			System.out.print("    # present = ");
			if (chunksCount == 1024)
				System.out.println("ALL");
			else
			{
				for (int i = 0; i < chunksList.size(); i++)
				{
					int[] coord = chunksList.get(i);
					System.out.print("{" + coord[0] + "," + coord[1] + "} ");
				}
				System.out.println("");
			}
			System.out.println("    # sections = " + sectionsCount);
			System.out.println("    # borders height differences map =");
			System.out.println("      (from chunk {" + (xRegion * REGION_WIDTH_IN_CHUNKS) + "," + (zRegion * REGION_WIDTH_IN_CHUNKS) + "} to {" + ((xRegion * REGION_WIDTH_IN_CHUNKS) + (REGION_WIDTH_IN_CHUNKS - 1)) + "," + ((zRegion * REGION_WIDTH_IN_CHUNKS) + (REGION_WIDTH_IN_CHUNKS - 1)) + "})");
			System.out.println("      (. = chunk, ° = ungenerated chunk, n = height diff in blocks, blank = rounded diff < 2, + = rounded diff > 9, ? = not computable)");
			for (int j = 0; j < REGION_WIDTH_IN_CHUNKS; j++)
			{
				if (j > 0)
				{
					System.out.print("      m# ");
					for (int i = 0; i < REGION_WIDTH_IN_CHUNKS; i++)
					{
						if (offset[i + (REGION_WIDTH_IN_CHUNKS * j)] == 0 || offset[i + (REGION_WIDTH_IN_CHUNKS * (j - 1))] == 0)
							System.out.printf("?   ");
						else
						{
							int sumHeightTopChunk = 0;
							int sumHeightBottomChunk = 0;
							for (int k = 0; k < CHUNK_WIDTH_IN_BLOCKS; k++)
							{
								sumHeightTopChunk += heightMap[i + (REGION_WIDTH_IN_CHUNKS * (j - 1))][k + 240];
								sumHeightBottomChunk += heightMap[i + (REGION_WIDTH_IN_CHUNKS * j)][k];
							}
							float averageHeightTopChunk = (float) sumHeightTopChunk / CHUNK_WIDTH_IN_BLOCKS;
							float averageHeightBottomChunk = (float) sumHeightBottomChunk / CHUNK_WIDTH_IN_BLOCKS;
							float diff = Math.abs(averageHeightTopChunk - averageHeightBottomChunk);
							if (Math.round(diff) < 2)
								System.out.printf("    ");
							else if (Math.round(diff) > 9)
								System.out.printf("+   ");
							else
								System.out.printf("%1.0f   ", diff);
						}
					}
					System.out.println("");
				}

				if (offset[0 + (REGION_WIDTH_IN_CHUNKS * j)] == 0)
					System.out.print("      m# ° ");
				else
					System.out.print("      m# . ");
				for (int i = 0; i < REGION_WIDTH_IN_CHUNKS - 1; i++)
				{
					if (offset[(i + 1) + (REGION_WIDTH_IN_CHUNKS * j)] == 0)
						System.out.printf("? ° ");
					else if (offset[i + (REGION_WIDTH_IN_CHUNKS * j)] == 0)
						System.out.printf("? . ");
					else
					{
						int sumHeightLeftChunk = 0;
						int sumHeightRightChunk = 0;
						for (int k = 15; k < (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS); k += CHUNK_WIDTH_IN_BLOCKS)
						{
							sumHeightLeftChunk += heightMap[i + (REGION_WIDTH_IN_CHUNKS * j)][k];
							sumHeightRightChunk += heightMap[(i + 1) + (REGION_WIDTH_IN_CHUNKS * j)][k - 15];
						}
						float averageHeightLeftChunk = (float) sumHeightLeftChunk / CHUNK_WIDTH_IN_BLOCKS;
						float averageHeightRightChunk = (float) sumHeightRightChunk / CHUNK_WIDTH_IN_BLOCKS;
						float diff = Math.abs(averageHeightLeftChunk - averageHeightRightChunk);
						if (Math.round(diff) < 2)
							System.out.printf("  . ");
						else if (Math.round(diff) > 9)
							System.out.printf("+ . ");
						else
							System.out.printf("%1.0f . ", diff);
					}
				}
				System.out.println("");
			}
		}
		if (RegionScanner.chunksToDelete != null || RegionScanner.chunksToPreserve != null)
			System.out.println("  # chunks deleted = " + chunksDeletedCount);

		// BIOMES STATISTICS
		if (RegionScanner.scan == true)
		{
			if (lotrBiomeVariantsCount == 0)
			{
				System.out.println("  # biomes = ");
				for (int i = 0; i < (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS); i++)
					if (biomeCount[i] > 0)
						System.out.println("    # " + BiomesMap.getVanillaBiomeName(i) + " = " + biomeCount[i]);
			}
			else
			{
				System.out.println("  # LOTR biomes map =");
				int xMin = xRegion * 512;
				int xMax = (xRegion * 512) + 511;
				int zMin = zRegion * 512;
				int zMax = (zRegion * 512) + 511;
				int mxMin = Math.round(((float) xMin / 128) + (float) 809.5);
				int mxMax = Math.round(((float) xMax / 128) + (float) 809.5);
				int mzMin = Math.round(((float) zMin / 128) + (float) 729.5);
				int mzMax = Math.round(((float) zMax / 128) + (float) 729.5);
				for (int i = mxMin; i <= mxMax; i++)
				{
					System.out.print("    ");
					for (int j = mzMin; j <= mzMax; j++)
					{
						String rgb = BiomesMap.getLotrBiomeRGB(i, j);
						int value = BiomesMap.getLotrBiomeValue(rgb);

						lotrBiomeCount[value]++;

						System.out.printf("|%03d", value);
					}
					System.out.println("|");
				}
				System.out.println("    # Biomes values =");
				for (int i = 0; i < MAX_BIOMES; i++)
					if (lotrBiomeCount[i] > 0)
					{
						String name = BiomesMap.getLotrBiomeName(i);
						System.out.println("      # " + name + "(" + i + ") = " + lotrBiomeCount[i]);
					}

				System.out.println("  # LOTR biomes variants = " + lotrBiomeVariantsCount);
				for (int i = 0; i < (CHUNK_WIDTH_IN_BLOCKS * CHUNK_WIDTH_IN_BLOCKS); i++)
					if (lotrBiomeVariantCount[i] > 0)
						System.out.println("    # " + BiomesMap.getLotrBiomeVariantName(i) + " = " + lotrBiomeVariantCount[i]);
			}
		}

		// BLOCKS STATISTICS
		if (RegionScanner.scan == true)
		{
			System.out.println("  # blocks = " + blocksCount);
			for (int id = 0; id < MAX_BLOCK_ID; id++)
				for (int data = 0; data < MAX_BLOCK_DATA; data++)
					if (blockCount[id][data] > 0)
						System.out.println("   b# " + RegionScanner.blocksAndItems.getBlockName(id) + "(" + id + "):" + data + " = " + blockCount[id][data]);
		}
		if (RegionScanner.fixUnknown == true)
			System.out.println("  # unknown blocks = " + unknownBlocksCount);
		if (! RegionScanner.toLevel.equals(""))
		{
			System.out.println("  # converted blocks = " + convertedBlocksCount);
			for (int id = 0; id < MAX_BLOCK_ID; id++)
				if (convertedBlockCount[id] > 0)
					System.out.println("    # " + RegionScanner.blocksAndItems.getBlockName(id) + "(" + id + "=>" + RegionScanner.modifiedBlocks.get(id) + ") = " + convertedBlockCount[id]);
		}
		if (RegionScanner.blocksToReplace != null)
		{
			System.out.println("  # deleted blocks = " + deletedBlocksCount);
			for (int id = 0; id < MAX_BLOCK_ID; id++)
				if (deletedBlockCount[id] > 0)
					System.out.println("    # " + RegionScanner.blocksAndItems.getBlockName(id) + "(" + id + ") = " + deletedBlockCount[id]);
			System.out.println("  # replaced blocks = " + replacedBlocksCount);
			for (int id = 0; id < MAX_BLOCK_ID; id++)
				if (replacedBlockCount[id] > 0)
					System.out.println("    # " + RegionScanner.blocksAndItems.getBlockName(id) + "(" + id + ") = " + replacedBlockCount[id]);
		}

		// ENTITIES & ITEMS STATISTICS
		if (RegionScanner.fixUnknown == true)
			System.out.println("  # unknown items = " + unknownItemsCount + " stacks");
		if (! RegionScanner.toLevel.equals(""))
		{
			System.out.println("  # converted items = " + convertedItemsCount + " stacks");
			for (int id = 0; id < MAX_ITEM_ID; id++)
				if (convertedItemCount[id] > 0)
					System.out.println("    # " + RegionScanner.blocksAndItems.getItemName(id) + "(" + id + "=>" + RegionScanner.modifiedItems.get(id) + ") = " + convertedItemCount[id]);
		}
		if (RegionScanner.itemsToReplace != null)
		{
			System.out.println("  # deleted items = " + deletedItemsCount + " stacks");
			for (int id = 0; id < MAX_ITEM_ID; id++)
				if (deletedItemCount[id] > 0)
					System.out.println("    # " + RegionScanner.blocksAndItems.getItemName(id) + "(" + id + ") = " + deletedItemCount[id]);
			System.out.println("  # replaced items = " + replacedItemsCount + " stacks");
			for (int id = 0; id < MAX_ITEM_ID; id++)
				if (replacedItemCount[id] > 0)
				{
					String itemName = RegionScanner.blocksAndItems.getItemName(id);
					String replacementItem;

					if ((replacementItem = RegionScanner.itemsToReplace.get(itemName)) == null)
					{
						// then we try to find a match with some item data value, but there may be an error if several are used...
						for (int itemData = 0 ; itemData < MAX_ITEM_DATA; itemData++)
							if ((replacementItem = RegionScanner.itemsToReplace.get(itemName + ":" + itemData)) != null)
								break;
						if (replacementItem == null)
							replacementItem = "?";
					}
					System.out.println("    # " + RegionScanner.blocksAndItems.getItemName(id) + "(" + id + "=>" + replacementItem + ") = " + replacedItemCount[id]);
				}
		}
		if (RegionScanner.scan == true)
		{
			System.out.println("  # entities = " + entitiesCount);
			for (String id : entityCount.keySet())
				System.out.println("   e# " + id + " = " + entityCount.get(id));
			if (entitiesCount > 0)
			{
				System.out.println("    # List:");
				for (int c = 0; c < CHUNKS_PER_REGION; c++)
					if (entities[c] != null)
						for (Iterator i = entities[c].iterator(); i.hasNext();)
						{
							CompoundTag entity = (CompoundTag) i.next();
	
							String id = entity.getString("id");
							String name = "";
							if (entity.containsKey("NPCName"))
								name = entity.getString("NPCName");
							ListTag pos = entity.getListTag("Pos");
							DoubleTag[] coordinates = new DoubleTag[3];
							int j = 0;
							for (Iterator k = pos.iterator(); k.hasNext();)
							{
								coordinates[j] = (DoubleTag) k.next();
								j++;
								if (j == 3)
									break;
							}
							if (name.equals(""))
								System.out.println("      # " + id + " at {" + coordinates[0].asDouble() + ", " + coordinates[1].asDouble() + ", " + coordinates[2].asDouble() + "}");
							else
								System.out.println("      # " + id + " named \"" + name + "\" at {" + coordinates[0].asDouble() + ", " + coordinates[1].asDouble() + ", " + coordinates[2].asDouble() + "}");
	
							if (id.equals("Item"))
							{
								if (entity.containsKey("Thrower"))
								{
									String thrower = entity.getString("Thrower");
									System.out.println("        # Throwed by " + thrower);  
								}
								showItemStack(entity.getCompoundTag("Item"), "");
							}
							else if (id.equals("ItemFrame"))
							{
								if (entity.containsKey("Item"))
									showItemStack(entity.getCompoundTag("Item"), "");
							}
							else if (id.equals("lotr.Banner"))
							{
								if (entity.containsKey("ProtectData"))
								{
									CompoundTag protectData = entity.getCompoundTag("ProtectData");
									if (protectData.getByte("StructureProtection") > 0)
										System.out.println("        # Structure protection");  
									if (protectData.getInt("AlignementProtection") > 0)
										System.out.println("        # Alignement protection = " + protectData.getInt("AlignementProtection"));  
									if (protectData.getByte("PlayerProtection") > 0)
									{
										System.out.println("        # Player protection:");  
										ListTag allowedPlayers = protectData.getListTag("AllowedPlayers");
										for (Iterator l = allowedPlayers.iterator(); l.hasNext();)
										{
											CompoundTag listElement = (CompoundTag) l.next();
											CompoundTag profile = listElement.getCompoundTag("Profile");
											System.out.println("          # " + profile.getString("Name") + " (" + profile.getString("Id") + ")");  
										}
									}
									if (protectData.getByte("SelfProtection") > 0)
										System.out.println("        # Self protection");  
									if (protectData.getShort("CustomRange") > 0)
										System.out.println("        # Custom range = " + protectData.getShort("CustomRange"));  
								}
							}
							else if (id.equals("lotr.NPCRespawner"))
							{
								if (entity.containsKey("SpawnClass1"))
								{
									String npc = entity.getString("SpawnClass1");
									if (! npc.equals(""))
										System.out.println("        # Spawns " + npc);  
								}
								if (entity.containsKey("SpawnClass2"))
								{
									String npc = entity.getString("SpawnClass2");
									if (! npc.equals(""))
										System.out.println("        # Spawns " + npc);  
								}
							}
	
							if (RegionScanner.logLevel > 1)
								System.out.println("      # Entity= " + entity);  
						}
			}
		}
		if (RegionScanner.entitiesToKill != null)
		{
			System.out.println("  # entities killed = " + entitiesKilledCount);
			for (String id : entityKilledCount.keySet())
				System.out.println("    # " + id + " = " + entityKilledCount.get(id));
		}
		if (RegionScanner.scan == true)
		{
			System.out.println("  # tile entities = " + tileEntitiesCount);
			for (String id : tileEntityCount.keySet())
				System.out.println("   t# " + id + " = " + tileEntityCount.get(id));  
			System.out.println("    # List:");  
			for (int c = 0; c < CHUNKS_PER_REGION; c++)
				if (tileEntities[c] != null)
					for (Iterator i = tileEntities[c].iterator(); i.hasNext();)
					{
						CompoundTag tileEntity = (CompoundTag) i.next();

						String id = tileEntity.getString("id");
						int x = tileEntity.getInt("x");
						int y = tileEntity.getInt("y");
						int z = tileEntity.getInt("z");
						System.out.println("      # " + id + " at {" + x + ", " + y + ", " + z + "}");

						if (tileEntity.containsKey("Items"))
						{
							ListTag items = tileEntity.getListTag("Items");
							for (Iterator j = items.iterator(); j.hasNext();)
								showItemStack((CompoundTag) j.next(), "");
						}
						else if (id.equals("Control") && tileEntity.containsKey("Command"))
							System.out.println("        # Command= " + tileEntity.getString("Command"));  
						else if (id.equals("FlowerPot") && tileEntity.containsKey("Item"))
						{
							int value = tileEntity.getInt("Item");
							if (value > 0)
							{
								String name = "";
								int data = 0;
								if (tileEntity.containsKey("Data"))
									data = tileEntity.getInt("Data");
								if (RegionScanner.toLevel.equals(""))
									name = RegionScanner.blocksAndItems.getItemName(value);
								else
									name = RegionScanner.newBlocksAndItems.getItemName(value);
								System.out.println("        # 1x " + name + ":" + data);  
							}
						}
						else if (id.equals("LOTRFlowerPot") && tileEntity.containsKey("PlantID"))
						{
							int value = tileEntity.getInt("PlantID");
							if (value > 0)
							{
								String name = "";
								int data = 0;
								if (tileEntity.containsKey("PlantMeta"))
									data = tileEntity.getInt("PlantMeta");
								if (RegionScanner.toLevel.equals(""))
									name = RegionScanner.blocksAndItems.getItemName(value);
								else
									name = RegionScanner.newBlocksAndItems.getItemName(value);
								System.out.println("        # 1x " + name + ":" + data);  
							}
						}
						else if (id.equals("LOTRWeaponRack") && tileEntity.containsKey("WeaponItem"))
							showItemStack(tileEntity.getCompoundTag("WeaponItem"), "");
						else if (id.equals("LOTRPlate") && tileEntity.containsKey("FoodItem"))
							showItemStack(tileEntity.getCompoundTag("FoodItem"), "");
						else if (id.equals("LOTRMug") && tileEntity.containsKey("MugItem"))
							showItemStack(tileEntity.getCompoundTag("MugItem"), "");
						else if (id.equals("Sign"))
						{
							for (int j = 1; j < 5; j++)
								if (tileEntity.containsKey("Text" + j))
								{
									String line = tileEntity.getString("Text" + j);
									if (! line.equals(""))
										System.out.println("        " + j + "> " + line);  
								}
						}
						else if (id.equals("LOTRSignCarved") || id.equals("LOTRSignCarvedIthildin"))
						{
							for (int j = 1; j < 9; j++)
								if (tileEntity.containsKey("Text" + j))
								{
									String line = tileEntity.getString("Text" + j);
									if (! line.equals(""))
										System.out.println("        " + j + "> " + line);  
								}
						}
						else if (id.equals("LOTRButterflyJar") && tileEntity.containsKey("JarEntityData"))
						{
							CompoundTag entityData = (CompoundTag) tileEntity.getCompoundTag("JarEntityData");
							if (entityData.containsKey("id"))
								System.out.println("        # " + entityData.getString("id"));  
						}

						if (RegionScanner.logLevel > 1)
							System.out.println("      # TileEntity= " + tileEntity);  
					}
		}
		if (RegionScanner.fixNames == true)
		{
			System.out.println("  # NPC names translated = " + translatedNamesCount);
		}

		// finally save a modified region
		if (isRegionModified == true)
			saveRegion(newFileName);
	}
}
