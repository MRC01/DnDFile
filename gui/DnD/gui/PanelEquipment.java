package DnD.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;

import DnD.model.Item;
import DnD.util.*;

/** This is the GUI panel for character equipment
    It contains a Tree control:
    1. the Tree model (and nodes) is the equipment item tree itself
    2. the Tree supports drag & drop, both copy & move
 */
public class PanelEquipment extends PanelBase implements ActionListener
{
	// This stops the Java compiler from complaining
	private static final long serialVersionUID = 1;

	static final int	kTreeHeight = 10;

	// GUI stuff
	JTree			itsTree;
	ItemTreeModel		itsTModel;
	JButton			itsButAdd, itsButEdit, itsButDel, itsButExp;

	// Other stuff

	public PanelEquipment() throws NoSuchFieldException
	{
		super();

		JScrollPane		sp;
		GridBagLayout		gb = new GridBagLayout();
		GridBagConstraints	gc = new GridBagConstraints();
		MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);

		setBorder(BorderFactory.createTitledBorder("Equipment Items"));
		setLayout(gb);

		// Set up the tree
		itsTree = new JTree();
		itsTree.setEditable(false);
		itsTree.setRootVisible(false);
		itsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// enable drag & drop
		itsTree.setDragEnabled(true);
		itsTree.setDropMode(DropMode.INSERT);
		itsTree.setTransferHandler(new EquipTransferHandler());
		// Add a double-click listener (which edits)
		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
		             	if(e.getClickCount() == 2)
		             	{
					TreePath selPath = itsTree.getPathForLocation(e.getX(), e.getY());
		             		openDlg(kModeEdit, selPath);
		             	}
		        }
		};
		itsTree.addMouseListener(ml);

		// put it into a scrollpane
		sp = new JScrollPane(itsTree,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1.0;
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, sp);

		// Control buttons
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.weighty = 0.0;
		gc.weightx = 0.0;
		gc.gridwidth = 1;
		itsButAdd = MainGui.newButton("Add", this);
		itsButEdit= MainGui.newButton("Edit", this);
		itsButDel = MainGui.newButton("Delete", this);
		itsButExp = MainGui.newButton("Expand", this);
		MainGui.addGui(guiCfg, itsButAdd);
		MainGui.addGui(guiCfg, itsButEdit);
		MainGui.addGui(guiCfg, itsButDel);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		MainGui.addGui(guiCfg, itsButExp);

		// initialize the tree model
		revertAll();
	}

	public void expandTree()
	{
		// Expand all nodes of the tree
		for(int i = 0; i < itsTree.getRowCount(); i++)
			itsTree.expandRow(i);
	}

	// Returns the grid bag height of this panel
	public int gbHeight()
	{
		return kTreeHeight;
	}

	// Called when buttons are pressed etc.
	public void actionPerformed(ActionEvent ev)
	{
		String		bc;
		TreePath	path;
		Item		itm, pitm;

		bc = ev.getActionCommand();
		path = itsTree.getSelectionModel().getSelectionPath();
		if(bc.equals(itsButAdd.getText()))
		{
			openDlg(kModeAdd, path);
		}
		else if(bc.equals(itsButEdit.getText()))
		{
			if(path == null)
			{
				// nothing selected to edit
				return;
			}
			openDlg(kModeEdit, path);
		}
		else if(bc.equals(itsButDel.getText()))
		{
			if(path == null)
			{
				// nothing selected to delete
				return;
			}
			int	row = itsTree.getRowForPath(path);
			itm = (Item)path.getLastPathComponent();
			pitm = (Item)path.getParentPath().getLastPathComponent();
			if(pitm.delChild(itm))
			{
				// Signal the tree that it has changed
				itsTModel.treeChanged();
			}
			// select the next item (if any), which is now in the prior row
			path = itsTree.getPathForRow(row);
			itsTree.setSelectionPath(path);
		}
		else if(bc.equals(itsButExp.getText()))
		{
			expandTree();
		}
		else
			super.actionPerformed(ev);
	}

	public void revertAll()
	{
		itsTModel = new PanelEquipment.ItemTreeModel(MainGui.get().itsChar.itsEquip);
		itsTree.setModel(itsTModel);
		expandTree();
	}

	protected void openDlg(int mode, TreePath path)
	{
		ItemDialog	dlg;
		String		title;
		int		row;

		// The row may be -1 (nothing selected), but that is OK if we are in add mode
		row = itsTree.getRowForPath(path);
		if(row < 0 && mode != kModeAdd)
			return;
		switch(mode)
		{
		case kModeAdd: title = "Add Item";
			break;
		default: title = "Edit Item";
			break;
		}
		// Pop up the dialog box (modal)
		dlg = new PanelEquipment.ItemDialog(MainGui.get().itsFrame, this, title, mode, path);
		dlg.pack();
		dlg.setVisible(true);
		// when we get here, the dialog has returned
		if(dlg.itsExitState)
		{
			// OK was pressed (not cancel)
			if(mode == kModeAdd)
			{
				// select the newly added item
				row++;
				path = itsTree.getPathForRow(row);
			}
			itsTree.setSelectionPath(path);
		}
	}

	// The tree model for the tree - based directly on the underlying equipment items
	// Tree nodes *are* equip items
	public class ItemTreeModel implements TreeModel
	{
		protected java.util.List<TreeModelListener> itsTMListeners;

		protected Item		itsRoot;

		public ItemTreeModel(Item root)
		{
			itsTMListeners = new ArrayList<TreeModelListener>();
			itsRoot = root;
		}

		public Object getRoot()
		{
			return itsRoot;
		}

		public Object getChild(Object parent, int idx)
		{
			Item	itm;

			itm = (Item)parent;
			if(itm.itsItems == null)
				return null;
			if(idx < 0 || idx >= itm.itsItems.size())
				return null;
			return itm.itsItems.get(idx);
		}

		public int getChildCount(Object parent)
		{
			Item	itm;

			itm = (Item)parent;
			if(itm.itsItems == null)
				return 0;
			return itm.itsItems.size();
		}

		public int getIndexOfChild(Object parent, Object child)
		{
			Item	pItm, cItm;

			pItm = (Item)parent;
			cItm = (Item)child;
			if(pItm == null || cItm == null)
				return -1;
			if(pItm.itsItems == null)
				return -1;
			return pItm.itsItems.indexOf(cItm);
		}

		public boolean isLeaf(Object node)
		{
			// NOTE: Always return FALSE, even for leaf items.
			// This enables drag & drop to move anything to any item.
			return false;
		}

		public void valueForPathChanged(TreePath path, Object newValue)
		{
			// NOTE:MRC: not used
			// The strings inside the value can change, but the values themselves should never change
		}

		public void addTreeModelListener(TreeModelListener l)
		{
			itsTMListeners.add(l);
		}

		public void removeTreeModelListener(TreeModelListener l)
		{
			itsTMListeners.remove(l);
		}

		public void treeChanged()
		{
			TreeModelEvent e = new TreeModelEvent(this, new Object[] {itsRoot});
			for(TreeModelListener tml : itsTMListeners)
				tml.treeStructureChanged(e);
			expandTree();
		}
	}

	// dialog box for adding & editing items
	public class ItemDialog extends JDialog implements ActionListener
	{
		// This stops the Java compiler from complaining
		private static final long serialVersionUID = 1;

		JButton			itsButOK, itsButCancel;
		JTextField		itsTFName, itsTFDesc;
		JCheckBox		itsCBAdd, itsCBChild;
		int			itsMode;
		TreePath		itsItemPath, itsItemPPath;
		Item			itsItem, itsPItem;
		boolean			itsExitState;

		public ItemDialog(Frame owner, PanelEquipment parent, String title, int mode, TreePath path)
		{
			super(owner, title, true);
			itsExitState = false;
			itsMode = mode;
			itsItemPath = path;
			if(itsItemPath != null)
			{
				itsItem = (Item)itsItemPath.getLastPathComponent();
				itsItemPPath = itsItemPath.getParentPath();
				itsPItem = (Item)itsItemPPath.getLastPathComponent();
			}
			GridBagLayout		gb = new GridBagLayout();
			GridBagConstraints	gc = new GridBagConstraints();
			MainGui.GuiCfg		guiCfg = new MainGui.GuiCfg(this, gb, gc);
			JLabel			jl;

			setLayout(gb);
			gc.fill = GridBagConstraints.NONE;
			gc.weighty = 0.0;
			setLocationRelativeTo(parent);

			// fields
			jl = new JLabel("Name");
			itsTFName = MainGui.newTextField();
			gc.weightx = 0.0;
			gc.gridwidth = 1;
			MainGui.addGui(guiCfg, jl);
			gc.weightx = 1.0;
			gc.gridwidth = GridBagConstraints.REMAINDER;
			MainGui.addGui(guiCfg, itsTFName);

			jl = new JLabel("Desc");
			itsTFDesc = MainGui.newTextField();
			gc.weightx = 0.0;
			gc.gridwidth = 1;
			MainGui.addGui(guiCfg, jl);
			gc.weightx = 1.0;
			gc.gridwidth = GridBagConstraints.REMAINDER;
			MainGui.addGui(guiCfg, itsTFDesc);

			// buttons
			itsButOK = MainGui.newButton("OK", this);
			itsButCancel = MainGui.newButton("Cancel", this);
			gc.anchor = GridBagConstraints.WEST;
			gc.weighty = 0.0;
			gc.weightx = 0.0;
			gc.gridwidth = 1;
			MainGui.addGui(guiCfg, itsButOK);
			if(itsMode != kModeAdd)
				gc.gridwidth = GridBagConstraints.REMAINDER;
			MainGui.addGui(guiCfg, itsButCancel);
			getRootPane().setDefaultButton(itsButOK);

			// checkboxes (for add)
			if(itsMode == kModeAdd)
			{
				itsCBAdd = new JCheckBox("After", true);
				MainGui.addGui(guiCfg, itsCBAdd);
				itsCBChild = new JCheckBox("Child", false);
				gc.gridwidth = GridBagConstraints.REMAINDER;
				MainGui.addGui(guiCfg, itsCBChild);
			}

			// Initialize text fields
			if(itsMode == kModeEdit)
			{
				itsTFName.setText(itsItem.itsName);
				itsTFDesc.setText(itsItem.itsDesc);
			}
		}

		// Called when buttons are pressed etc.
		public void actionPerformed(ActionEvent ev)
		{
			String		bc,
					nam, des;
			Item		itm;
			int		idx;

			bc = ev.getActionCommand();
			if(bc.equals(itsButOK.getText()))
			{
				nam = itsTFName.getText();
				if(Util.isBlank(nam))
					nam = null;
				des = itsTFDesc.getText();
				if(Util.isBlank(des))
					des = null;
				switch(itsMode)
				{
				case kModeEdit:
					itsItem.itsName = nam;
					itsItem.itsDesc = des;
					break;
				case kModeAdd:
					itm = new Item(nam, des);
					if(itsItem == null)
					{
						// nothing selected, so add to root
						itsPItem = (Item)itsTModel.getRoot();
						itsPItem.addChild(itm);
					}
					else if(itsCBChild.isSelected())
					{
						// Add a child to the current selected item
						itsItem.addChild(itm);
					}
					else
					{
						// Add a sibling to the current selected item
						idx = itsTModel.getIndexOfChild(itsPItem, itsItem);
						if(itsCBAdd.isSelected())
							idx++;
						itsPItem.addChild(itm, idx);
					}
					break;
				}
				// Signal the tree that it has changed
				itsTModel.treeChanged();
				itsExitState = true;
				// close this dialog
				dispose();
			}
			else if(bc.equals(itsButCancel.getText()))
			{
				dispose();
			}
		}
	}

	// This class controls drag & drop on the tree
	class EquipTransferHandler extends TransferHandler
	{
		// This stops the Java compiler from complaining
		private static final long serialVersionUID = 1;

		DataFlavor	itsNodeFlavor;
		DataFlavor[]	itsFlavors;
		Item		itsRemParent, itsRemItem;

		public EquipTransferHandler()
		{
			itsFlavors = new DataFlavor[1];
			try
			{
				StringBuffer mimeType = new StringBuffer();
				mimeType.append(DataFlavor.javaJVMLocalObjectMimeType)
					.append(";class=\"")
					.append(Item.class.getName())
					.append("\"");
				itsNodeFlavor = new DataFlavor(mimeType.toString());
				itsFlavors[0] = itsNodeFlavor;
			}
			catch(ClassNotFoundException e)
			{
				 System.out.println("ClassNotFound: " + e.getMessage());
			}
		}

		/* Called when a drag begins
		   1. remember the item and its parent (for use on the drop, to delete in case it's a move)
		   2. wrap the dragged object with a NodeTransferable and return it
		 */
		protected Transferable createTransferable(JComponent c)
		{
			if(!itsTree.equals(c))
				return null;

			TreePath path = itsTree.getSelectionPath();
			if(path != null)
			{
				Item		item;

				item = (Item)path.getLastPathComponent();
				itsRemItem = item;
				// item parent is the next-to-last path element
				itsRemParent = (Item)path.getParentPath().getLastPathComponent();

				// wrap the object to be dragged and return the wrapper
				return new NodeTransferable(item);
			}
			return null;
		}

		// Tells DnD what actions we support
		public int getSourceActions(JComponent c)
		{
			 return COPY_OR_MOVE;
		}

		// called repeatedly during the drag to see if the item can be dropped
		public boolean canImport(TransferHandler.TransferSupport tranSup)
		{
			if(!tranSup.isDrop())
				 return false;

			tranSup.setShowDropLocation(true);

			// Do not allow unsupported flavors to be dropped
			if(!tranSup.isDataFlavorSupported(itsNodeFlavor))
				 return false;

			// Do not allow a drop from any other tree
			if(!itsTree.equals(tranSup.getComponent()))
				return false;

			// Do not allow a drop on top of itself
			JTree.DropLocation dl = (JTree.DropLocation)tranSup.getDropLocation();
			if(dl.getPath().equals(itsTree.getSelectionPath()))
				return false;

			return true;
		}

		// called when the drop occurs
		public boolean importData(TransferHandler.TransferSupport tranSup)
		{
			if(!canImport(tranSup))
				 return false;

			// Extract transfer data.
			Item	node = null;
			try
			{
				Transferable t = tranSup.getTransferable();
				node = (Item)t.getTransferData(itsNodeFlavor);
			}
			catch(Exception e)
			{
				 MainGui.get().errBox("Drag and Drop Exception", e);
			}
			if(node == null)
				return false;

			// Get drop location info.
			JTree.DropLocation dl = (JTree.DropLocation)tranSup.getDropLocation();
			int childIndex = dl.getChildIndex();
			TreePath dest = dl.getPath();
			Item newParent = (Item)dest.getLastPathComponent();

			// Add the item to its new parent
			newParent.addChild(node, childIndex);
			itsTModel.treeChanged();
			return true;
		}

		// called after the drop
		protected void exportDone(JComponent source, Transferable data, int action)
		{
			if((action & MOVE) == MOVE)
			{
				// Remove the node that was moved
				itsRemParent.delChild(itsRemItem);
				itsTModel.treeChanged();
			}
		}

		// wraps the items being dragged & dropped
		public class NodeTransferable implements Transferable
		{
			Item	node;

			public NodeTransferable(Item node)
			{
				/* Always hold a deep copy of the thing being dragged.
				   This way it can be dropped even at a different location in the same parent
				   without any problems related to it being "equal" to something else.
				*/
				this.node = node.deepCopy();
			}

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
			{
				if(!isDataFlavorSupported(flavor))
					throw new UnsupportedFlavorException(flavor);
				return node;
			}

			public DataFlavor[] getTransferDataFlavors()
			{
				 return itsFlavors;
			}

			public boolean isDataFlavorSupported(DataFlavor flavor)
			{
				return itsNodeFlavor.equals(flavor);
			}
		}
	}
}
