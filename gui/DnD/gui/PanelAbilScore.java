package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;

import DnD.model.AbilScore;

/** This is the GUI panel for ability scores.
 */
public class PanelAbilScore extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	// GUI stuff
	JTextField[]	itsABVal, itsABAdj;
	JButton		itsButABAdj;
	JButton[]	itsButAdj;

	// Other stuff
	int		itsABCount;

	// Draw the panel
	public PanelAbilScore()
	{
		super();

		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		JTextField		jtfVal, jtfAdj;
		JButton			jbAdj;
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		setLayout(gb);

		itsABCount = AbilScore.Type.values().length;
		itsABVal = new JTextField[itsABCount];
		itsABAdj = new JTextField[itsABCount];
		itsButAdj = new JButton[itsABCount];
		itsButABAdj = MainGui.newButton("Set Adjustments", this);
		itsButABAdj.setForeground(Color.RED);

		setBorder(BorderFactory.createTitledBorder("Ability Scores"));

		gc.fill = GridBagConstraints.NONE;
		gc.weighty = 1.0;
		for(AbilScore.Type ab : AbilScore.Type.values())
		{
			jtfVal = MainGui.newTextField(8);
			jtfAdj = MainGui.newTextField(32);
			jbAdj = MainGui.newButton(ab.itsName, this);
			jbAdj.setForeground(Color.RED);

			gc.weightx = 1.0;
			gc.gridwidth = 1;
			gc.anchor = GridBagConstraints.EAST;
			MainGui.addGui(guiCfg, jbAdj);
			gc.anchor = GridBagConstraints.WEST;
			gc.gridwidth = 2;
			MainGui.addGui(guiCfg, jtfVal);
			gc.gridwidth = GridBagConstraints.REMAINDER;
			MainGui.addGui(guiCfg, jtfAdj);

			int idx = ab.ordinal();
			itsABVal[idx] = jtfVal;
			itsABAdj[idx] = jtfAdj;
			itsButAdj[idx] = jbAdj;
		}

		// Control buttons
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx = 0.0;
		gc.gridwidth = 1;
		MainGui.addGui(guiCfg, itsButApply);
		MainGui.addGui(guiCfg, itsButRevert);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsButABAdj);

		// set focus traversal order
		java.util.List<Component> lst = new LinkedList<Component>();
		for(JTextField tf : itsABVal)
			lst.add(tf);
		lst.add(itsButABAdj);
		setFocusOrder(lst);
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return itsABCount + 2;
	}

	// Called when buttons are pressed etc.
	public void actionPerformed(ActionEvent ev)
	{
		String		bc;

		super.actionPerformed(ev);

		bc = ev.getActionCommand();

		// Individual ability score adjust buttons
		for(AbilScore.Type abt : AbilScore.Type.values())
		{
			int	idx = abt.ordinal();
			if(bc.equals(itsButAdj[idx].getText()))
			{
				setABScoreAdj(abt);
				break;
			}
		}
		// Set all ability score adjustments
		if(bc.equals(itsButABAdj.getText()))
		{
			for(AbilScore.Type abt : AbilScore.Type.values())
				setABScoreAdj(abt);
		}
	}

	public void applyAll()
	{
		for(AbilScore.Type abt : AbilScore.Type.values())
		{
			int	idx;
			String	val, adj;

			idx = abt.ordinal();
			val = itsABVal[idx].getText();
			adj = itsABAdj[idx].getText();
			MainGui.get().itsChar.itsAbilScores.set(abt, val, adj);
		}
	}

	public void revertAll()
	{
		for(AbilScore.Type abt : AbilScore.Type.values())
		{
			int		idx;
			AbilScore	ab;

			idx = abt.ordinal();
			ab = MainGui.get().itsChar.itsAbilScores.get(abt);
			itsABVal[idx].setText(ab.itsVal);
			itsABAdj[idx].setText(ab.itsAdjust);
		}
	}

	protected void setABScoreAdj(AbilScore.Type abt)
	{
		String	txtVal;
		int	i;

		i = abt.ordinal();
		txtVal = itsABVal[i].getText();
		itsABAdj[i].setText(MainGui.get().itsChar.itsAbilScores.get(abt).getAdjust(txtVal));
	}
}
