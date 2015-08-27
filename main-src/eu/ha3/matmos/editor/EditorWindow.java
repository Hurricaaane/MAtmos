package eu.ha3.matmos.editor;

import eu.ha3.matmos.editor.edit.EditPanel;
import eu.ha3.matmos.editor.filechooser.JasonFileChooser;
import eu.ha3.matmos.editor.filechooser.OverwriteWarningJasonFileChooser;
import eu.ha3.matmos.editor.interfaces.Editor;
import eu.ha3.matmos.editor.interfaces.Window;
import eu.ha3.matmos.editor.tree.ItemTreeBranch;
import eu.ha3.matmos.editor.tree.ItemTreeNode;
import eu.ha3.matmos.editor.tree.ItemTreeViewPanel;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/* 
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditorWindow extends JFrame implements Window
{
	private final Editor model;
	
	private static final String WINDOW_TITLE = "MAtmos Editor";
	
	private String windowTitle = WINDOW_TITLE;
	private JMenuItem mntmFDiscardChanges;
	private JMenuItem mntmFSave;
	private JMenuItem mntmFSaveAs;
	private JMenuItem mntmOpenFile;
	private JMenu mnMinecraft;
	private JMenuItem mntmStartLiveCapture;
	private JMenuItem mntmStopLiveCapture;
	private JMenuItem mntmReplaceCurrentFile;
	private JPanel omniPanel;
	private JMenuItem mntmMCSaveAndPush;
	
	private JLabel specialWarningLabel;
	private ItemTreeViewPanel panelTree;
	private EditPanel editPanel;
	
	public EditorWindow(Editor modelConstruct)
	{
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0)
			{
				if (EditorWindow.this.model.hasUnsavedChanges())
				{
					if (!continueUnsavedChangesWarningIfNecessary())
						return;
				}
				
				if (EditorWindow.this.model.isMinecraftControlled())
				{
					setVisible(false);
				}
				else
				{
					dispose();
				}
			}
		});
		this.model = modelConstruct;
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('f');
		menuBar.add(mnFile);
		
		this.mntmFSave = new JMenuItem("Save");
		this.mntmFSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				EditorWindow.this.model.quickSave();
			}
		});
		
		this.mntmOpenFile = new JMenuItem("Open file...");
		this.mntmOpenFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (!continueUnsavedChangesWarningIfNecessary())
					return;
				
				JFileChooser fc = new JasonFileChooser(EditorWindow.this.model.getExpansionDirectory());
				int returnValue = fc.showOpenDialog(EditorWindow.this);
				if (returnValue != JFileChooser.APPROVE_OPTION)
					return;
				
				File file = fc.getSelectedFile();
				if (file == null || !file.exists() || file.isDirectory())
				{
					if (file.isDirectory())
					{
						showErrorPopup("Unexpected error: The file is a directory.");
					}
					else
					{
						showErrorPopup("Unexpected error: The file does not exist.");
					}
					return;
				}
				
				EditorWindow.this.model.trySetAndLoadFile(file);
			}
		});
		mnFile.add(this.mntmOpenFile);
		
		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		this.mntmFSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(this.mntmFSave);
		
		this.mntmFSaveAs = new JMenuItem("Save as...");
		this.mntmFSaveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new OverwriteWarningJasonFileChooser(EditorWindow.this.model.getExpansionDirectory());
				int returnValue = fc.showSaveDialog(EditorWindow.this);
				if (returnValue != JFileChooser.APPROVE_OPTION)
					return;
				
				File file = fc.getSelectedFile();
				if (file == null || file.isDirectory())
				{
					if (file.isDirectory())
					{
						showErrorPopup("Unexpected error: The file is a directory.");
					}
					else
					{
						showErrorPopup("Unexpected error: No file pointer.");
					}
					return;
				}
				
				boolean success = EditorWindow.this.model.longSave(file, true);
				if (!success)
				{
					actionPerformed(e);
				}
			}
		});
		mnFile.add(this.mntmFSaveAs);
		
		JMenuItem mntmFSaveACopy = new JMenuItem("Save a backup copy...");
		mntmFSaveACopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new OverwriteWarningJasonFileChooser(EditorWindow.this.model.getExpansionDirectory());
				int returnValue = fc.showSaveDialog(EditorWindow.this);
				if (returnValue != JFileChooser.APPROVE_OPTION)
					return;
				
				File file = fc.getSelectedFile();
				if (file == null || file.isDirectory())
				{
					if (file.isDirectory())
					{
						showErrorPopup("Unexpected error: The file is a directory.");
					}
					else
					{
						showErrorPopup("Unexpected error: No file pointer.");
					}
					return;
				}
				
				boolean success = EditorWindow.this.model.longSave(file, false);
				if (!success)
				{
					actionPerformed(e);
				}
			}
		});
		mntmFSaveACopy
			.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		mnFile.add(mntmFSaveACopy);
		
		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);
		
		this.mntmReplaceCurrentFile = new JMenuItem("Replace current file with backup... (NOT IMPLEMENTED)");
		mnFile.add(this.mntmReplaceCurrentFile);
		
		this.mntmFDiscardChanges = new JMenuItem("Discard changes and reload (NOT IMPLEMENTED)");
		mnFile.add(this.mntmFDiscardChanges);
		
		JSeparator separator_7 = new JSeparator();
		mnFile.add(separator_7);
		
		JMenuItem mntmMergeAnotherFile = new JMenuItem("Merge another file in...");
		mntmMergeAnotherFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (!continueUnsavedChangesWarningIfNecessary())
					return;
				
				JFileChooser fc = new JasonFileChooser(EditorWindow.this.model.getExpansionDirectory());
				int returnValue = fc.showOpenDialog(EditorWindow.this);
				if (returnValue != JFileChooser.APPROVE_OPTION)
					return;
				
				File file = fc.getSelectedFile();
				if (file == null || !file.exists() || file.isDirectory())
				{
					if (file.isDirectory())
					{
						showErrorPopup("Unexpected error: The file is a directory.");
					}
					else
					{
						showErrorPopup("Unexpected error: The file does not exist.");
					}
					return;
				}
				
				EditorWindow.this.model.mergeFrom(file);
			}
		});
		mnFile.add(mntmMergeAnotherFile);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);
		
		JMenu mnCreate = new JMenu("Create");
		menuBar.add(mnCreate);
		
		JMenuItem mntmAdd = new JMenuItem("Create new item...");
		mntmAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				new PopupHelper();
				KnowledgeItemType choice = PopupHelper.askForType(EditorWindow.this, "Create new item...");
				
				String name = "New item";
				while (true)
				{
					name = PopupHelper.askForName(EditorWindow.this, "Create new item...", name);
					
					if (name == null || EditorWindow.this.model.createItem(choice, name))
						return;
				}
			}
		});
		mnCreate.add(mntmAdd);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Duplicate item...");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				ItemTreeNode node = getSelectedITN();
				if (node == null)
					return;
				
				EditorWindow.this.model.duplicateItem(
					((ItemTreeBranch) node.getParent()).getSelector(), node.getItemName());
			}
		});
		mnCreate.add(mntmNewMenuItem);
		
		JSeparator separator_6 = new JSeparator();
		mnCreate.add(separator_6);
		
		JMenuItem mntmPurgeUnusedLogic = new JMenuItem("Purge unused logic");
		mntmPurgeUnusedLogic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int saveOption =
					JOptionPane.showConfirmDialog(
						EditorWindow.this,
						"This is going to remove all set and conditions that are recursively unused by any machine.\n"
							+ "Make sure you have backups before appenting this. Confirm?", "Purging logic",
						JOptionPane.CANCEL_OPTION);
				
				if (saveOption == JOptionPane.YES_OPTION)
				{
					EditorWindow.this.model.purgeLogic();
				}
			}
		});
		mnCreate.add(mntmPurgeUnusedLogic);
		
		JMenuItem mntmPurgeUnusedSupports = new JMenuItem("Purge unused supports");
		mntmPurgeUnusedSupports.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int saveOption =
					JOptionPane.showConfirmDialog(
						EditorWindow.this,
						"This is going to remove all supports items that are unused by any machine.\n"
							+ "Make sure you have backups before appenting this. Confirm?", "Purging supports",
						JOptionPane.CANCEL_OPTION);
				
				if (saveOption == JOptionPane.YES_OPTION)
				{
					EditorWindow.this.model.purgeSupports();
				}
			}
		});
		mnCreate.add(mntmPurgeUnusedSupports);
		
		JMenu mnOptions = new JMenu("Options");
		mnOptions.setMnemonic('o');
		menuBar.add(mnOptions);
		
		JMenuItem mntmOpenDefinitionsFile = new JMenuItem("Open definitions file...");
		mnOptions.add(mntmOpenDefinitionsFile);
		
		JMenuItem mntnLoadDefaultDefinitions = new JMenuItem("Load default definitions");
		mnOptions.add(mntnLoadDefaultDefinitions);
		
		JSeparator separator_4 = new JSeparator();
		mnOptions.add(separator_4);
		
		JMenuItem mntmOpenDatavaluesFile = new JMenuItem("Open blocks and items file...");
		mnOptions.add(mntmOpenDatavaluesFile);
		
		JMenuItem mntmLoadDefaultDatavalues = new JMenuItem("Load last generated data values");
		mnOptions.add(mntmLoadDefaultDatavalues);
		
		this.mnMinecraft = new JMenu("Minecraft");
		this.mnMinecraft.setMnemonic('m');
		menuBar.add(this.mnMinecraft);
		
		JMenuItem mntmMCPushEditorState = new JMenuItem("Push editor state to Minecraft");
		mntmMCPushEditorState.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				EditorWindow.this.model.minecraftPushCurrentState();
			}
		});
		mntmMCPushEditorState.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		this.mnMinecraft.add(mntmMCPushEditorState);
		
		this.mntmMCSaveAndPush = new JMenuItem("Save file and push to Minecraft");
		this.mntmMCSaveAndPush.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.CTRL_MASK));
		this.mntmMCSaveAndPush.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				// Should never happen hopefully...
				if (!EditorWindow.this.model.isMinecraftControlled())
					return;
				
				if (attemptToQuickSave())
				{
					EditorWindow.this.model.minecraftReloadFromDisk();
				}
				else
				{
					showErrorPopup("Saving was unsuccessful.");
				}
			}
		});
		this.mnMinecraft.add(this.mntmMCSaveAndPush);
		
		JSeparator separator_3 = new JSeparator();
		this.mnMinecraft.add(separator_3);
		
		JMenuItem mntmCaptureCurrentState = new JMenuItem("Capture current state");
		mntmCaptureCurrentState.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		this.mnMinecraft.add(mntmCaptureCurrentState);
		
		this.mntmStartLiveCapture = new JMenuItem("Start live capture");
		this.mnMinecraft.add(this.mntmStartLiveCapture);
		
		this.mntmStopLiveCapture = new JMenuItem("Stop live capture");
		this.mntmStopLiveCapture.setEnabled(false);
		this.mnMinecraft.add(this.mntmStopLiveCapture);
		
		JSeparator separator_5 = new JSeparator();
		this.mnMinecraft.add(separator_5);
		
		JMenuItem mntmGenerateDataValues = new JMenuItem("Generate data values file");
		this.mnMinecraft.add(mntmGenerateDataValues);
		
		this.omniPanel = new JPanel();
		this.omniPanel.setBorder(new EmptyBorder(0, 4, 4, 4));
		getContentPane().add(this.omniPanel, BorderLayout.CENTER);
		this.omniPanel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.omniPanel.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel treeTab = new JPanel();
		tabbedPane.addTab("Knowledge", null, treeTab, null);
		treeTab.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		treeTab.add(splitPane, BorderLayout.CENTER);
		splitPane.setResizeWeight(0.5);
		
		this.panelTree = new ItemTreeViewPanel(this.model);
		splitPane.setLeftComponent(this.panelTree);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
		this.editPanel = new EditPanel(this.model);
		scrollPane.setViewportView(this.editPanel);
		this.editPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
		
		JPanel sheetsTab = new JPanel();
		tabbedPane.addTab("Sheets", null, sheetsTab, null);
		
		JPanel jsonTab = new JPanel();
		tabbedPane.addTab("Json", null, jsonTab, null);
		jsonTab.setLayout(new BorderLayout(0, 0));
		
		JsonPanel jsonPanel = new JsonPanel(this.model);
		jsonTab.add(jsonPanel, BorderLayout.CENTER);
		
		init();
	}
	
	private ItemTreeNode getSelectedITN()
	{
		return this.panelTree.getSelectedITN();
	}
	
	private void init()
	{
		this.setSize(600, 400);
		setLocationRelativeTo(this);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		if (this.model.isMinecraftControlled())
		{
			this.windowTitle = WINDOW_TITLE + " - Minecraft integration (WILL CLOSE IF MINECRAFT CLOSES)";
			
			//setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			
			this.specialWarningLabel =
				new JLabel(
					"Integrated with Minecraft. If Minecraft closes/crashes, this window will close. SAVE FREQUENTLY (CTRL-F5 / CTRL-S)");
			this.omniPanel.add(this.specialWarningLabel, BorderLayout.NORTH);
			this.specialWarningLabel.setForeground(Color.RED);
			
			this.mntmOpenFile.setEnabled(false);
			this.mntmFSaveAs.setEnabled(false);
		}
		else
		{
			this.windowTitle = WINDOW_TITLE;
			
			//setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			
			this.mnMinecraft.setEnabled(false);
			this.mntmReplaceCurrentFile.setEnabled(false);
		}
		setTitle(this.windowTitle);
		
		refreshFileState();
	}
	
	protected boolean continueUnsavedChangesWarningIfNecessary()
	{
		if (!this.model.hasUnsavedChanges())
			return true;
		
		int saveOption =
			JOptionPane.showConfirmDialog(
				this, "You have unsaved changes. Are you sure you want to continue?", "Unsaved changes",
				JOptionPane.CANCEL_OPTION);
		
		return saveOption == JOptionPane.YES_OPTION;
	}
	
	@Override
	public void refreshFileState()
	{
		boolean hasValidFile = this.model.hasValidFile();
		boolean hasUnsavedChanges = this.model.hasUnsavedChanges();
		
		this.mntmFSave.setEnabled(hasValidFile && hasUnsavedChanges);
		this.mntmFDiscardChanges.setEnabled(hasValidFile && !hasUnsavedChanges);
		this.mntmMCSaveAndPush.setEnabled(hasValidFile && hasUnsavedChanges);
		
		if (hasValidFile)
		{
			setTitle((this.model.hasUnsavedChanges() ? "*" : "")
				+ this.model.getFile().getName() + " - " + this.windowTitle);
		}
		else
		{
			if (this.model.hasUnsavedChanges())
			{
				setTitle("*(no file) - " + this.windowTitle);
			}
			else
			{
				setTitle(this.windowTitle);
			}
		}
	}
	
	@Override
	public void display()
	{
		setVisible(true);
		if (this.model.isMinecraftControlled())
		{
			toFront();
			repaint();
		}
	}
	
	@Override
	public Component asComponent()
	{
		return this;
	}
	
	@Override
	public void updateSerial(SerialRoot root)
	{
		this.panelTree.updateSerial(root);
	}
	
	@Override
	public void showErrorPopup(String error)
	{
		JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Returns true even if the file has no changes.
	 * 
	 * @return
	 */
	private boolean attemptToQuickSave()
	{
		if (!this.model.hasValidFile())
		{
			showErrorPopup("Cannot Quick Save: No file is open.");
			return false;
		}
		
		if (this.model.hasUnsavedChanges())
			return this.model.quickSave();
		
		return true;
	}
	
	@Override
	public void disableMinecraftCapabilitites()
	{
		if (this.specialWarningLabel != null)
		{
			this.specialWarningLabel
				.setText("MINECRAFT CONNECTION LOST. If Minecraft closes/crashes, this window will close. YOU SHOULD SAVE (CTRL-S)");
		}
		
		this.mnMinecraft.setEnabled(false);
		
		showErrorPopup("Minecraft connection lost!\n"
			+ "This may be due to Resource Packs being reloaded.\n" + "You should save!");
	}
	
	@Override
	public void setEditFocus(String name, Object item, boolean forceSelect)
	{
		this.panelTree.setEditFocus(name, item, forceSelect);
		this.editPanel.setEditFocus(name, item, forceSelect);
	}
}
