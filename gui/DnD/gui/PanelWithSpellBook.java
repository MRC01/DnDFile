package DnD.gui;

import DnD.model.SpellBook;

/** This is an interface for Class Info Panels that have a Spellbook
 *  It is implemented by PanelClassMUBase and PanelFighter (for Rangers)
 */
public interface PanelWithSpellBook
{
	// PanelSpellList calls this when a spell is selected
	public void pickSpell(int idx);

	// PanelSpellList calls this to add a new spell
	public void addSpell(int idx);

	// PanelSpellList calls this to delete a spell
	public void delSpell(int idx);

	// PanelSpellDetail calls this when a spell is applied
	public void applySpell(SpellBook.Spell sp);
}
