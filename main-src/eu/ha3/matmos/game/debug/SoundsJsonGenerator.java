package eu.ha3.matmos.game.debug;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
--filenotes-placeholder
*/

public class SoundsJsonGenerator implements Runnable
{
	private List<String> filenames = new ArrayList<String>();
	
	private boolean OVERWRITE_FILE = true;
	
	private final File soundsFolder;
	private final File jsonFile;
	
	public SoundsJsonGenerator(File soundsFolder, File jsonFile)
	{
		this.soundsFolder = soundsFolder;
		this.jsonFile = jsonFile;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void run()
	{
		listAllFiles();
		
		List<String> workingNames = new ArrayList<String>();
		for (String name : this.filenames)
		{
            if (name.endsWith(".ogg"))
            {
                workingNames.add(name.substring(0, name.indexOf(".")));
            }
		}
		
		Map<String, List> catNames = new LinkedHashMap<String, List>();
		for (String name : workingNames)
		{
			String newName = name.replace('/', '.').replaceAll("[0-9]", "");
			if (!catNames.containsKey(newName))
			{
				catNames.put(newName, new ArrayList<String>());
			}
			
			if (!name.contains("stream"))
			{
				catNames.get(newName).add(name);
			}
			else
			{
				Map<String, Object> streaming = new LinkedHashMap<String, Object>();
				streaming.put("name", name);
				streaming.put("stream", true);
				catNames.get(newName).add(streaming);
			}
		}
		
		Map<String, Object> toJsonify = new LinkedHashMap<String, Object>();
		for (String catName : catNames.keySet())
		{
			Map<String, Object> blob = new LinkedHashMap<String, Object>();
			blob.put("category", catName.contains("underwater") ? "weather" : "ambient");
			blob.put("sounds", catNames.get(catName));
			
			toJsonify.put(catName, blob);
		}
		
		//
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jason = gson.toJson(toJsonify);
		
		if (!this.OVERWRITE_FILE)
		{
			System.out.println(jason);
			return;
		}
		
		FileWriter writer;
		try
		{
			writer = new FileWriter(this.jsonFile);
			writer.append(jason);
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void listAllFiles()
	{
		listFiles(this.soundsFolder, this.soundsFolder);
	}
	
	private void listFiles(File root, File subdir)
	{
		for (File file : subdir.listFiles())
		{
			if (file.isDirectory())
			{
				listFiles(root, file);
			}
			else
			{
				this.filenames.add(root.toURI().relativize(file.toURI()).getPath().toLowerCase().toString());
			}
		}
	}
}