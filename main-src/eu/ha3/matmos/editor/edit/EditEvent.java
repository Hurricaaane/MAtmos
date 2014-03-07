package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.ha3.matmos.jsonformat.serializable.SerialEvent;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditEvent extends JPanel
{
	private final EditPanel edit;
	private final SerialEvent event;
	private JPanel panel;
	private JPanel panel_1;
	private JSpinner volMin;
	private JLabel lblVolumeminmax;
	private JSpinner volMax;
	private JSpinner pitchMin;
	private JSpinner pitchMax;
	private JLabel lblPitchminmax;
	private JButton btnTest;
	private JSpinner distance;
	private JCheckBox chckbxIsStereoFile;
	private JLabel lblDistance;
	private JPanel panel_2;
	private JLabel lblSoundsPlayedEqually;
	
	private boolean init = true;
	
	public EditEvent(EditPanel parentConstruct, SerialEvent eventConstruct)
	{
		this.edit = parentConstruct;
		this.event = eventConstruct;
		setLayout(new BorderLayout(0, 0));
		
		this.panel = new JPanel();
		this.panel.setBorder(new TitledBorder(null, "Internal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(this.panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 50, 100, 100, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		this.panel.setLayout(gbl_panel);
		
		this.lblVolumeminmax = new JLabel("Volume (min/max)");
		GridBagConstraints gbc_lblVolumeminmax = new GridBagConstraints();
		gbc_lblVolumeminmax.anchor = GridBagConstraints.EAST;
		gbc_lblVolumeminmax.insets = new Insets(0, 0, 5, 5);
		gbc_lblVolumeminmax.gridx = 0;
		gbc_lblVolumeminmax.gridy = 0;
		this.panel.add(this.lblVolumeminmax, gbc_lblVolumeminmax);
		
		this.volMin = new JSpinner();
		this.volMin.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditEvent.this.init)
					return;
				
				EditEvent.this.event.vol_min = (Float) EditEvent.this.volMin.getValue();
				EditEvent.this.edit.flagChange();
			}
		});
		this.volMin.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_volMin = new GridBagConstraints();
		gbc_volMin.fill = GridBagConstraints.HORIZONTAL;
		gbc_volMin.insets = new Insets(0, 0, 5, 5);
		gbc_volMin.gridx = 1;
		gbc_volMin.gridy = 0;
		this.panel.add(this.volMin, gbc_volMin);
		
		this.volMax = new JSpinner();
		this.volMax.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditEvent.this.init)
					return;
				
				EditEvent.this.event.vol_max = (Float) EditEvent.this.volMax.getValue();
				EditEvent.this.edit.flagChange();
			}
		});
		this.volMax.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_volMax = new GridBagConstraints();
		gbc_volMax.fill = GridBagConstraints.HORIZONTAL;
		gbc_volMax.insets = new Insets(0, 0, 5, 0);
		gbc_volMax.gridx = 2;
		gbc_volMax.gridy = 0;
		this.panel.add(this.volMax, gbc_volMax);
		
		this.lblPitchminmax = new JLabel("Pitch (min/max)");
		GridBagConstraints gbc_lblPitchminmax = new GridBagConstraints();
		gbc_lblPitchminmax.anchor = GridBagConstraints.EAST;
		gbc_lblPitchminmax.insets = new Insets(0, 0, 5, 5);
		gbc_lblPitchminmax.gridx = 0;
		gbc_lblPitchminmax.gridy = 1;
		this.panel.add(this.lblPitchminmax, gbc_lblPitchminmax);
		
		this.pitchMin = new JSpinner();
		this.pitchMin.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditEvent.this.init)
					return;
				
				EditEvent.this.event.pitch_min = (Float) EditEvent.this.pitchMin.getValue();
				EditEvent.this.edit.flagChange();
			}
		});
		this.pitchMin.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_pitchMin = new GridBagConstraints();
		gbc_pitchMin.fill = GridBagConstraints.HORIZONTAL;
		gbc_pitchMin.insets = new Insets(0, 0, 5, 5);
		gbc_pitchMin.gridx = 1;
		gbc_pitchMin.gridy = 1;
		this.panel.add(this.pitchMin, gbc_pitchMin);
		
		this.pitchMax = new JSpinner();
		this.pitchMax.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditEvent.this.init)
					return;
				
				EditEvent.this.event.pitch_max = (Float) EditEvent.this.pitchMax.getValue();
				EditEvent.this.edit.flagChange();
			}
		});
		this.pitchMax.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(0.1)));
		GridBagConstraints gbc_pitchMax = new GridBagConstraints();
		gbc_pitchMax.fill = GridBagConstraints.HORIZONTAL;
		gbc_pitchMax.insets = new Insets(0, 0, 5, 0);
		gbc_pitchMax.gridx = 2;
		gbc_pitchMax.gridy = 1;
		this.panel.add(this.pitchMax, gbc_pitchMax);
		
		this.lblDistance = new JLabel("Distance");
		this.pitchMax.setModel(new SpinnerNumberModel(new Integer(0), null, null, new Integer(1)));
		GridBagConstraints gbc_lblDistance = new GridBagConstraints();
		gbc_lblDistance.anchor = GridBagConstraints.EAST;
		gbc_lblDistance.insets = new Insets(0, 0, 0, 5);
		gbc_lblDistance.gridx = 0;
		gbc_lblDistance.gridy = 2;
		this.panel.add(this.lblDistance, gbc_lblDistance);
		
		this.distance = new JSpinner();
		this.distance.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (EditEvent.this.init)
					return;
				
				EditEvent.this.event.distance = (Integer) EditEvent.this.distance.getValue();
				EditEvent.this.chckbxIsStereoFile.setSelected(EditEvent.this.event.distance <= 0);
				EditEvent.this.edit.flagChange();
			}
		});
		GridBagConstraints gbc_distance = new GridBagConstraints();
		gbc_distance.fill = GridBagConstraints.HORIZONTAL;
		gbc_distance.insets = new Insets(0, 0, 0, 5);
		gbc_distance.gridx = 1;
		gbc_distance.gridy = 2;
		this.panel.add(this.distance, gbc_distance);
		
		this.chckbxIsStereoFile = new JCheckBox("0 = Is stereo file");
		this.chckbxIsStereoFile.setEnabled(false);
		GridBagConstraints gbc_chckbxIsStereoFile = new GridBagConstraints();
		gbc_chckbxIsStereoFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxIsStereoFile.gridx = 2;
		gbc_chckbxIsStereoFile.gridy = 2;
		this.panel.add(this.chckbxIsStereoFile, gbc_chckbxIsStereoFile);
		
		this.panel_1 = new JPanel();
		this.panel_1.setBorder(new TitledBorder(
			UIManager.getBorder("TitledBorder.border"), "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(this.panel_1, BorderLayout.SOUTH);
		this.panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		this.btnTest = new JButton("Test sound in-game");
		this.panel_1.add(this.btnTest);
		
		this.panel_2 = new JPanel();
		this.panel_2.setBorder(new TitledBorder(
			null, "Important information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(this.panel_2, BorderLayout.NORTH);
		
		this.lblSoundsPlayedEqually =
			new JLabel(
				"<html>Sounds played equally on both ears must be stereo;<br>Fixed location sounds must be mono.</html>");
		this.panel_2.add(this.lblSoundsPlayedEqually);
		
		updateValues();
		this.init = false;
	}
	
	private void updateValues()
	{
		this.volMin.setValue(this.event.vol_min);
		this.volMax.setValue(this.event.vol_max);
		this.pitchMin.setValue(this.event.pitch_min);
		this.pitchMax.setValue(this.event.pitch_max);
		this.distance.setValue(this.event.distance);
		this.chckbxIsStereoFile.setSelected(this.event.distance <= 0);
	}
}
