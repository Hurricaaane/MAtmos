package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import eu.ha3.matmos.editor.InstantTextField;
import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialMachine;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialMachineEvent;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditMachine extends JPanel implements IFlaggable
{
	private boolean init = true;
	private final EditPanel edit;
	private final SerialMachine machine;
	private SerialMachineEvent event;
	private JSpinner delayIn;
	private JSpinner delayOut;
	private JSpinner fadeIn;
	private JSpinner fadeOut;
	
	private JTextField fileField;
	private JSpinner spinner;
	private JSpinner spinner_2;
	private JCheckBox chckbxIsLooping;
	private SetRemoverPanel activeSet;
	private SetRemoverPanel inactiveSet;
	private JList list;
	private JList timedEvents;
	private boolean eventBeingModified = true;
	private JSpinner startDelay;
	private JSpinner delayMax;
	private InstantTextField instantEvent;
	private JSpinner delayMin;
	private JSpinner pitchMod;
	private JSpinner volMod;
	
	public EditMachine(EditPanel parentConstruct, SerialMachine machineConstruct)
	{
		this.edit = parentConstruct;
		this.machine = machineConstruct;
		setLayout(new BorderLayout(0, 0));
		
		JPanel options = new JPanel();
		options.setBorder(new TitledBorder(
			UIManager.getBorder("TitledBorder.border"), "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(options, BorderLayout.SOUTH);
		options.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel internal = new JPanel();
		internal.setBorder(new TitledBorder(null, "Internal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(internal, BorderLayout.CENTER);
		internal.setLayout(new BorderLayout(0, 0));
		
		JPanel internalOptions = new JPanel();
		internal.add(internalOptions, BorderLayout.NORTH);
		GridBagLayout gbl_internalOptions = new GridBagLayout();
		gbl_internalOptions.columnWidths = new int[] { 50, 100, 100, 0 };
		gbl_internalOptions.rowHeights = new int[] { 0, 0, 0 };
		gbl_internalOptions.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_internalOptions.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		internalOptions.setLayout(gbl_internalOptions);
		
		JLabel lblDelay = new JLabel("Delay before fade (in/out)");
		GridBagConstraints gbc_lblDelay = new GridBagConstraints();
		gbc_lblDelay.anchor = GridBagConstraints.EAST;
		gbc_lblDelay.insets = new Insets(0, 0, 5, 5);
		gbc_lblDelay.gridx = 0;
		gbc_lblDelay.gridy = 0;
		internalOptions.add(lblDelay, gbc_lblDelay);
		
		this.delayIn = new JSpinner();
		this.delayIn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditMachine.this.init)
					return;
				
				EditMachine.this.machine.delay_fadein = (Float) EditMachine.this.delayIn.getValue();
				EditMachine.this.edit.flagChange();
			}
		});
		this.delayIn.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_delayIn = new GridBagConstraints();
		gbc_delayIn.fill = GridBagConstraints.HORIZONTAL;
		gbc_delayIn.insets = new Insets(0, 0, 5, 5);
		gbc_delayIn.gridx = 1;
		gbc_delayIn.gridy = 0;
		internalOptions.add(this.delayIn, gbc_delayIn);
		
		this.delayOut = new JSpinner();
		this.delayOut.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditMachine.this.init)
					return;
				
				EditMachine.this.machine.delay_fadeout = (Float) EditMachine.this.delayOut.getValue();
				EditMachine.this.edit.flagChange();
			}
		});
		this.delayOut.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_delayOut = new GridBagConstraints();
		gbc_delayOut.fill = GridBagConstraints.HORIZONTAL;
		gbc_delayOut.insets = new Insets(0, 0, 5, 0);
		gbc_delayOut.gridx = 2;
		gbc_delayOut.gridy = 0;
		internalOptions.add(this.delayOut, gbc_delayOut);
		
		JLabel lblFade = new JLabel("Fade time (in/out)");
		GridBagConstraints gbc_lblFade = new GridBagConstraints();
		gbc_lblFade.anchor = GridBagConstraints.EAST;
		gbc_lblFade.insets = new Insets(0, 0, 0, 5);
		gbc_lblFade.gridx = 0;
		gbc_lblFade.gridy = 1;
		internalOptions.add(lblFade, gbc_lblFade);
		
		this.fadeIn = new JSpinner();
		this.fadeIn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditMachine.this.init)
					return;
				
				EditMachine.this.machine.fadein = (Float) EditMachine.this.fadeIn.getValue();
				EditMachine.this.edit.flagChange();
			}
		});
		this.fadeIn.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_fadeIn = new GridBagConstraints();
		gbc_fadeIn.fill = GridBagConstraints.HORIZONTAL;
		gbc_fadeIn.insets = new Insets(0, 0, 0, 5);
		gbc_fadeIn.gridx = 1;
		gbc_fadeIn.gridy = 1;
		internalOptions.add(this.fadeIn, gbc_fadeIn);
		
		this.fadeOut = new JSpinner();
		this.fadeOut.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditMachine.this.init)
					return;
				
				EditMachine.this.machine.fadeout = (Float) EditMachine.this.fadeOut.getValue();
				EditMachine.this.edit.flagChange();
			}
		});
		this.fadeOut.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_fadeOut = new GridBagConstraints();
		gbc_fadeOut.fill = GridBagConstraints.HORIZONTAL;
		gbc_fadeOut.gridx = 2;
		gbc_fadeOut.gridy = 1;
		internalOptions.add(this.fadeOut, gbc_fadeOut);
		
		JPanel otherGroup = new JPanel();
		internal.add(otherGroup, BorderLayout.CENTER);
		otherGroup.setLayout(new BorderLayout(0, 0));
		
		JPanel timed = new JPanel();
		timed.setBorder(new TitledBorder(null, "Timed Events", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		otherGroup.add(timed);
		timed.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		timed.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3, BorderLayout.NORTH);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_3.add(scrollPane_1, BorderLayout.CENTER);
		
		this.timedEvents = new JList();
		this.timedEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.timedEvents.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0)
			{
				if (EditMachine.this.eventBeingModified)
					return;
				
				if (EditMachine.this.timedEvents.getSelectedIndex() != -1
					&& EditMachine.this.timedEvents.getSelectedIndex() < EditMachine.this.machine.event.size())
				{
					changeToEvent(EditMachine.this.timedEvents.getSelectedIndex());
					
				}
			}
		});
		this.timedEvents.setVisibleRowCount(3);
		scrollPane_1.setViewportView(this.timedEvents);
		
		JButton btnAdd = new JButton("+");
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				addEvent();
			}
		});
		panel_3.add(btnAdd, BorderLayout.EAST);
		
		JPanel panel_4 = new JPanel();
		panel_2.add(panel_4, BorderLayout.CENTER);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[] { 0, 100, 100, 0 };
		gbl_panel_4.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel_4.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_4.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_4.setLayout(gbl_panel_4);
		
		JLabel lblEvent = new JLabel("Event");
		GridBagConstraints gbc_lblEvent = new GridBagConstraints();
		gbc_lblEvent.insets = new Insets(0, 0, 5, 5);
		gbc_lblEvent.anchor = GridBagConstraints.EAST;
		gbc_lblEvent.gridx = 0;
		gbc_lblEvent.gridy = 0;
		panel_4.add(lblEvent, gbc_lblEvent);
		
		this.instantEvent = new InstantTextField() {
			@Override
			protected void editEvent()
			{
				if (EditMachine.this.eventBeingModified)
					return;
				
				EditMachine.this.event.event = EditMachine.this.instantEvent.getText();
				EditMachine.this.edit.flagChange();
			}
		};
		GridBagConstraints gbc_instantEvent = new GridBagConstraints();
		gbc_instantEvent.gridwidth = 2;
		gbc_instantEvent.insets = new Insets(0, 0, 5, 0);
		gbc_instantEvent.fill = GridBagConstraints.HORIZONTAL;
		gbc_instantEvent.gridx = 1;
		gbc_instantEvent.gridy = 0;
		panel_4.add(this.instantEvent, gbc_instantEvent);
		
		JLabel lblDelayMin = new JLabel("Delay (min/max)");
		GridBagConstraints gbc_lblDelayMin = new GridBagConstraints();
		gbc_lblDelayMin.anchor = GridBagConstraints.EAST;
		gbc_lblDelayMin.insets = new Insets(0, 0, 5, 5);
		gbc_lblDelayMin.gridx = 0;
		gbc_lblDelayMin.gridy = 1;
		panel_4.add(lblDelayMin, gbc_lblDelayMin);
		
		this.delayMin = new JSpinner();
		this.delayMin.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditMachine.this.init)
					return;
				if (EditMachine.this.eventBeingModified)
					return;
				
				EditMachine.this.event.delay_min = (Float) EditMachine.this.delayMin.getValue();
				EditMachine.this.edit.flagChange();
			}
		});
		this.delayMin.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_delayMin = new GridBagConstraints();
		gbc_delayMin.fill = GridBagConstraints.HORIZONTAL;
		gbc_delayMin.insets = new Insets(0, 0, 5, 5);
		gbc_delayMin.gridx = 1;
		gbc_delayMin.gridy = 1;
		panel_4.add(this.delayMin, gbc_delayMin);
		
		this.delayMax = new JSpinner();
		this.delayMax.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditMachine.this.init)
					return;
				if (EditMachine.this.eventBeingModified)
					return;
				
				EditMachine.this.event.delay_max = (Float) EditMachine.this.delayMax.getValue();
				EditMachine.this.edit.flagChange();
			}
		});
		this.delayMax.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_delayMax = new GridBagConstraints();
		gbc_delayMax.fill = GridBagConstraints.HORIZONTAL;
		gbc_delayMax.insets = new Insets(0, 0, 5, 0);
		gbc_delayMax.gridx = 2;
		gbc_delayMax.gridy = 1;
		panel_4.add(this.delayMax, gbc_delayMax);
		
		JLabel lblStartDelay = new JLabel("Start delay");
		GridBagConstraints gbc_lblStartDelay = new GridBagConstraints();
		gbc_lblStartDelay.anchor = GridBagConstraints.EAST;
		gbc_lblStartDelay.insets = new Insets(0, 0, 5, 5);
		gbc_lblStartDelay.gridx = 0;
		gbc_lblStartDelay.gridy = 2;
		panel_4.add(lblStartDelay, gbc_lblStartDelay);
		
		this.startDelay = new JSpinner();
		this.startDelay.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditMachine.this.init)
					return;
				if (EditMachine.this.eventBeingModified)
					return;
				
				EditMachine.this.event.delay_start = (Float) EditMachine.this.startDelay.getValue();
				EditMachine.this.edit.flagChange();
			}
		});
		this.startDelay.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_startDelay = new GridBagConstraints();
		gbc_startDelay.fill = GridBagConstraints.HORIZONTAL;
		gbc_startDelay.insets = new Insets(0, 0, 5, 5);
		gbc_startDelay.gridx = 1;
		gbc_startDelay.gridy = 2;
		panel_4.add(this.startDelay, gbc_startDelay);
		
		JLabel lblMultipliervolpitch = new JLabel("Multiplier (vol/pitch)");
		GridBagConstraints gbc_lblMultipliervolpitch = new GridBagConstraints();
		gbc_lblMultipliervolpitch.anchor = GridBagConstraints.EAST;
		gbc_lblMultipliervolpitch.insets = new Insets(0, 0, 5, 5);
		gbc_lblMultipliervolpitch.gridx = 0;
		gbc_lblMultipliervolpitch.gridy = 3;
		panel_4.add(lblMultipliervolpitch, gbc_lblMultipliervolpitch);
		
		this.volMod = new JSpinner();
		this.volMod.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditMachine.this.init)
					return;
				if (EditMachine.this.eventBeingModified)
					return;
				
				EditMachine.this.event.vol_mod = (Float) EditMachine.this.volMod.getValue();
				EditMachine.this.edit.flagChange();
			}
		});
		this.volMod.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_volMod = new GridBagConstraints();
		gbc_volMod.fill = GridBagConstraints.HORIZONTAL;
		gbc_volMod.insets = new Insets(0, 0, 5, 5);
		gbc_volMod.gridx = 1;
		gbc_volMod.gridy = 3;
		panel_4.add(this.volMod, gbc_volMod);
		
		this.pitchMod = new JSpinner();
		this.pitchMod.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditMachine.this.init)
					return;
				if (EditMachine.this.eventBeingModified)
					return;
				
				EditMachine.this.event.pitch_mod = (Float) EditMachine.this.pitchMod.getValue();
				EditMachine.this.edit.flagChange();
			}
		});
		this.pitchMod.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_pitchMod = new GridBagConstraints();
		gbc_pitchMod.insets = new Insets(0, 0, 5, 0);
		gbc_pitchMod.fill = GridBagConstraints.HORIZONTAL;
		gbc_pitchMod.gridx = 2;
		gbc_pitchMod.gridy = 3;
		panel_4.add(this.pitchMod, gbc_pitchMod);
		
		JButton btnRemove = new JButton("REMOVE");
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				removeEvent();
			}
		});
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.insets = new Insets(0, 0, 0, 5);
		gbc_btnRemove.gridx = 0;
		gbc_btnRemove.gridy = 4;
		panel_4.add(btnRemove, gbc_btnRemove);
		
		JPanel stream = new JPanel();
		stream.setBorder(new TitledBorder(null, "Streaming Sound", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		otherGroup.add(stream, BorderLayout.SOUTH);
		stream.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("181px:grow"), }, new RowSpec[] {
			RowSpec.decode("20px"), FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), }));
		
		JPanel filePanel = new JPanel();
		stream.add(filePanel, "1, 1, fill, top");
		filePanel.setLayout(new BorderLayout(0, 0));
		
		this.fileField = new JTextField();
		this.fileField.setText("<editing the stream data is disabled in this version>");
		this.fileField.setEditable(false);
		this.fileField.setEnabled(false);
		filePanel.add(this.fileField);
		this.fileField.setColumns(10);
		
		JButton btnOpen = new JButton("Open...");
		filePanel.add(btnOpen, BorderLayout.EAST);
		
		JPanel otherPanel = new JPanel();
		stream.add(otherPanel, "1, 3, fill, fill");
		GridBagLayout gbl_otherPanel = new GridBagLayout();
		gbl_otherPanel.columnWidths = new int[] { 50, 100, 0 };
		gbl_otherPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_otherPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_otherPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		otherPanel.setLayout(gbl_otherPanel);
		
		JLabel lblVolumeMultiplier = new JLabel("Volume multiplier");
		GridBagConstraints gbc_lblVolumeMultiplier = new GridBagConstraints();
		gbc_lblVolumeMultiplier.anchor = GridBagConstraints.EAST;
		gbc_lblVolumeMultiplier.insets = new Insets(0, 0, 5, 5);
		gbc_lblVolumeMultiplier.gridx = 0;
		gbc_lblVolumeMultiplier.gridy = 0;
		otherPanel.add(lblVolumeMultiplier, gbc_lblVolumeMultiplier);
		
		this.spinner = new JSpinner();
		this.spinner.setEnabled(false);
		this.spinner.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.insets = new Insets(0, 0, 5, 0);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 0;
		otherPanel.add(this.spinner, gbc_spinner);
		
		JLabel lblPitchMultiplier = new JLabel("Pitch multiplier");
		GridBagConstraints gbc_lblPitchMultiplier = new GridBagConstraints();
		gbc_lblPitchMultiplier.anchor = GridBagConstraints.EAST;
		gbc_lblPitchMultiplier.insets = new Insets(0, 0, 5, 5);
		gbc_lblPitchMultiplier.gridx = 0;
		gbc_lblPitchMultiplier.gridy = 1;
		otherPanel.add(lblPitchMultiplier, gbc_lblPitchMultiplier);
		
		this.spinner_2 = new JSpinner();
		this.spinner_2.setEnabled(false);
		this.spinner.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_spinner_2 = new GridBagConstraints();
		gbc_spinner_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_2.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_2.gridx = 1;
		gbc_spinner_2.gridy = 1;
		otherPanel.add(this.spinner_2, gbc_spinner_2);
		
		this.chckbxIsLooping = new JCheckBox("Is looping");
		this.chckbxIsLooping.setEnabled(false);
		GridBagConstraints gbc_chckbxIsLooping = new GridBagConstraints();
		gbc_chckbxIsLooping.anchor = GridBagConstraints.WEST;
		gbc_chckbxIsLooping.gridx = 1;
		gbc_chckbxIsLooping.gridy = 2;
		otherPanel.add(this.chckbxIsLooping, gbc_chckbxIsLooping);
		
		JPanel activationPanel = new JPanel();
		activationPanel.setBorder(new TitledBorder(
			UIManager.getBorder("TitledBorder.border"), "Activation", TitledBorder.LEADING, TitledBorder.TOP, null,
			null));
		add(activationPanel, BorderLayout.NORTH);
		activationPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("333px:grow"), }, new RowSpec[] {
			RowSpec.decode("21px"), RowSpec.decode("60px"), FormFactory.RELATED_GAP_ROWSPEC,
			FormFactory.DEFAULT_ROWSPEC, RowSpec.decode("60px"), FormFactory.RELATED_GAP_ROWSPEC,
			RowSpec.decode("default:grow"), }));
		
		JLabel lblMustBeActive = new JLabel("Any can be active (Power sources):");
		activationPanel.add(lblMustBeActive, "1, 1, left, center");
		
		this.activeSet = new SetRemoverPanel(this, this.machine.allow);
		activationPanel.add(this.activeSet, "1, 2, fill, top");
		
		JLabel lblMustBeInactive = new JLabel("None must be active (Jammers):");
		activationPanel.add(lblMustBeInactive, "1, 4, left, center");
		
		this.inactiveSet = new SetRemoverPanel(this, this.machine.restrict);
		activationPanel.add(this.inactiveSet, "1, 5, fill, top");
		
		JPanel panel = new JPanel();
		activationPanel.add(panel, "1, 7");
		panel.setBorder(new TitledBorder(null, "Add", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);
		
		this.list = new JList();
		this.list.setVisibleRowCount(4);
		scrollPane.setViewportView(this.list);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.EAST);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnActive = new JButton("Active");
		btnActive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				addToAllow();
			}
		});
		panel_1.add(btnActive);
		
		JButton btnInactive = new JButton("Inactive");
		btnInactive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				addToRestrict();
			}
		});
		panel_1.add(btnInactive);
		
		updateValues();
		this.eventBeingModified = false;
		this.init = false;
	}
	
	protected void addEvent()
	{
		this.eventBeingModified = true;
		SerialMachineEvent sme = new SerialMachineEvent();
		this.machine.event.add(sme);
		this.event = sme;
		updateValues();
		changeToEvent(this.machine.event.size() - 1);
	}
	
	protected void removeEvent()
	{
		this.machine.event.remove(this.event);
		this.edit.flagChange();
		updateValues();
	}
	
	protected void changeToEvent(int selectedIndex)
	{
		this.eventBeingModified = true;
		this.timedEvents.setSelectedIndex(selectedIndex);
		this.event = this.machine.event.get(selectedIndex);
		fillEvents();
		this.eventBeingModified = false;
	}
	
	private void fillEvents()
	{
		if (this.machine.event == null)
			return;
		
		this.delayMin.setValue(this.event.delay_min);
		this.delayMax.setValue(this.event.delay_max);
		this.startDelay.setValue(this.event.delay_start);
		this.instantEvent.setText(this.event.event);
		
		this.volMod.setValue(this.event.vol_mod);
		this.pitchMod.setValue(this.event.pitch_mod);
	}
	
	private void updateValues()
	{
		this.delayIn.setValue(this.machine.delay_fadein);
		this.delayOut.setValue(this.machine.delay_fadeout);
		this.fadeIn.setValue(this.machine.fadein);
		this.fadeOut.setValue(this.machine.fadeout);
		
		if (this.machine.event != null)
		{
			List<String> data = new ArrayList<String>();
			int acc = 1;
			for (SerialMachineEvent e : this.machine.event)
			{
				data.add("(" + acc + ") " + e.event);
				acc = acc + 1;
			}
			
			this.timedEvents.setListData(data.toArray());
		}
		
		if (this.machine.stream != null)
		{
		}
		
		fillWithValues();
		this.activeSet.fillWithValues();
		this.inactiveSet.fillWithValues();
		
	}
	
	protected void addToAllow()
	{
		Object values[] = this.list.getSelectedValues();
		if (values.length == 0)
			return;
		
		int addedCount = 0;
		for (Object o : values)
		{
			String value = (String) o;
			if (!this.machine.allow.contains(value))
			{
				this.machine.allow.add(value);
				addedCount = addedCount + 1;
			}
		}
		
		if (addedCount > 0)
		{
			flagChange();
		}
	}
	
	protected void addToRestrict()
	{
		Object values[] = this.list.getSelectedValues();
		if (values.length == 0)
			return;
		
		int addedCount = 0;
		for (Object o : values)
		{
			String value = (String) o;
			if (!this.machine.restrict.contains(value))
			{
				this.machine.restrict.add(value);
				addedCount = addedCount + 1;
			}
		}
		
		if (addedCount > 0)
		{
			flagChange();
		}
	}
	
	private void fillWithValues()
	{
		Set<String> unused = new TreeSet<String>(this.edit.getSerialRoot().set.keySet());
		unused.removeAll(this.machine.allow);
		unused.removeAll(this.machine.restrict);
		
		this.list.removeAll();
		this.list.setListData(unused.toArray(new String[unused.size()]));
	}
	
	@Override
	public void flagChange()
	{
		this.edit.flagChange();
		updateValues();
	}
}