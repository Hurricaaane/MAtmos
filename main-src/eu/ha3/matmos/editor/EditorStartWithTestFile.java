package eu.ha3.matmos.editor;

import java.io.File;

/*
--filenotes-placeholder
*/

public class EditorStartWithTestFile
{
	public static void main(String[] args)
	{
		EditorMaster master =
			new EditorMaster(
				null,
				new File(
					"L:\\MCMod-C\\jars\\resourcepacks\\mat_matmos\\assets\\matmos\\expansions\\interactions_environments.json"));
		master.run();
	}
}
