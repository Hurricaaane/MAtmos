package eu.ha3.matmos.editor;

import eu.ha3.matmos.editor.interfaces.Editor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class JsonPanel extends JPanel
{
	private Editor model;
	
	private JTextArea textArea;
	
	public JsonPanel(Editor modelConstruct)
	{
		this.model = modelConstruct;
		
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);
		
		this.textArea = new JTextArea();
		this.textArea.setEditable(false);
		this.textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.textArea.setLineWrap(true);
		scrollPane.setViewportView(this.textArea);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		
		JButton btnGeneratePretty = new JButton("Generate");
		btnGeneratePretty.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JsonPanel.this.textArea.setText(JsonPanel.this.model.generateJson(true));
			}
		});
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.add(btnGeneratePretty);
		
		JButton btnGenerateMinified = new JButton("Minify");
		btnGenerateMinified.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JsonPanel.this.textArea.setText(JsonPanel.this.model.generateJson(false));
			}
		});
		panel.add(btnGenerateMinified);
		
		JButton btnCopyToClipboard = new JButton("Copy to Clipboard");
		btnCopyToClipboard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					StringSelection selection = new StringSelection(JsonPanel.this.textArea.getText());
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		panel.add(btnCopyToClipboard);
	}
}
