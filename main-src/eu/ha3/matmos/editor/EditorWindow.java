package eu.ha3.matmos.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import eu.ha3.matmos.editor.interfaces.EditorModel;
import eu.ha3.matmos.editor.interfaces.IEditorWindow;
import eu.ha3.matmos.editor.interfaces.ISerialUpdate;
import eu.ha3.matmos.jsonformat.serializable.SerialRoot;

/* 
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditorWindow extends JFrame implements IEditorWindow
{
	private final EditorModel model;
	
	private static final String WINDOW_TITLE = "MAtmos Editor";
	private JMenuItem mntmFDiscardChanges;
	private JMenuItem mntmFSave;
	private JMenuItem mntmFSaveAs;
	private JMenuItem mntmOpenFile;
	private JMenu mnMinecraft;
	private JMenuItem mntmStartLiveCapture;
	private JMenuItem mntmStopLiveCapture;
	private JMenuItem mntmReplaceCurrentFile;
	private CSMPanelSimpler csm;
	
	private EditorModel getModel()
	{
		return this.model;
	}
	
	public EditorWindow(EditorModel modelConstruct)
	{
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
				System.out.println("save");
			}
		});
		this.mntmFSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(this.mntmFSave);
		
		this.mntmFSaveAs = new JMenuItem("Save as...");
		mnFile.add(this.mntmFSaveAs);
		
		JMenuItem mntmFSaveACopy = new JMenuItem("Save a copy...");
		mntmFSaveACopy
			.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		mnFile.add(mntmFSaveACopy);
		
		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		
		this.mntmOpenFile = new JMenuItem("Open file...");
		mnFile.add(this.mntmOpenFile);
		
		this.mntmReplaceCurrentFile = new JMenuItem("Replace current file with backup...");
		mnFile.add(this.mntmReplaceCurrentFile);
		
		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);
		
		this.mntmFDiscardChanges = new JMenuItem("Discard changes and reload");
		mnFile.add(this.mntmFDiscardChanges);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);
		
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
				System.out.println("boo");
			}
		});
		mntmMCPushEditorState.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		this.mnMinecraft.add(mntmMCPushEditorState);
		
		JMenuItem mntmMCSaveAndPush = new JMenuItem("Save file and push to Minecraft");
		mntmMCSaveAndPush.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.CTRL_MASK));
		mntmMCSaveAndPush.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				getModel().minecraftReloadFromDisk();
			}
		});
		this.mnMinecraft.add(mntmMCSaveAndPush);
		
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
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("CSM", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		this.csm = new CSMPanelSimpler();
		panel_1.add(this.csm, BorderLayout.CENTER);
		
		init();
	}
	
	private void init()
	{
		SimpleFrame.makeMeGeneric(this, WINDOW_TITLE, 600, 400);
		if (this.model.isMinecraftControlled())
		{
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.mntmOpenFile.setEnabled(false);
			this.mntmFSaveAs.setEnabled(false);
		}
		else
		{
			this.mnMinecraft.setEnabled(false);
			this.mntmReplaceCurrentFile.setEnabled(false);
		}
		
		refreshFileState();
	}
	
	@Override
	public void refreshFileState()
	{
		boolean hasValidFile = this.model.hasValidFile();
		
		this.mntmFSave.setEnabled(hasValidFile);
		this.mntmFDiscardChanges.setEnabled(hasValidFile);
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
	public ISerialUpdate getCondition()
	{
		return this.csm.getCondition();
	}
	
	@Override
	public ISerialUpdate getSet()
	{
		return this.csm.getSet();
	}
	
	@Override
	public ISerialUpdate getMachine()
	{
		return this.csm.getMachine();
	}
	
	@Override
	public void updateSerial(SerialRoot root)
	{
		this.csm.getCondition().updateSerial(root);
		this.csm.getSet().updateSerial(root);
		this.csm.getMachine().updateSerial(root);
	}
}
