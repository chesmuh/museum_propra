package de.museum.berleburg.userInterface.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.museum.berleburg.datastorage.model.Label;

/**
 * an implementation of AbstractListModel featuring a handy "getSelection()" method
 * @author Christian Landel
 */
@SuppressWarnings("serial")
public class LabelListModel extends AbstractListModel<String>
{
	List<Label> labels = new LinkedList<Label>();
	JList<String> parent;
	public LabelListModel (List<Label> labels, JList<String> parent) {
		set(labels);
		this.parent=parent;
	}
	@Override
	public String getElementAt(int arg0) throws IndexOutOfBoundsException {
		int i=0; for (Label label : labels)
			if (i++ == arg0)
				return label.getName();
		throw new IndexOutOfBoundsException();
	}
	@Override
	public int getSize() {
		return labels.size();
	}
	private int indexOf (Label label) {
		int i=0; for (Label test : labels) {
			if (test.getId().equals(label.getId()))
				return i;
			i++;
		}
		return -1;
	}
	public void remove (Label label) {
		int index = indexOf(label);
		if (index<0)
			return;
		labels.remove(label);
		for (ListDataListener l : getListDataListeners())
			l.intervalRemoved(new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,index,index));
	}
	public void remove (Collection<Label> labels) {
		for (Label l : labels)
			remove(l);
	}
	public void add (Label label) {
		//check if the label already exists (no duplicate entries allowed!)
		if (indexOf(label)>=0)
			return;
		labels.add(label);
		int index = indexOf(label);
		for (ListDataListener l : getListDataListeners())
			l.intervalRemoved(new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,index,index));
	}
	public void add (Collection<Label> labels) {
		for (Label l : labels)
			add(l);
	}
	public void set (List<Label> labels) {
		if (getSize()>0) {
			int oldSize = getSize();
			this.labels = new LinkedList<Label>();
			for (ListDataListener l : getListDataListeners())
				l.intervalRemoved(new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,0,oldSize-1));
		}
		this.labels=labels;
		for (ListDataListener l : getListDataListeners())
			l.intervalRemoved(new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,0,labels.size()));
	}
	public List<Label> get() {
		return labels;
	}
	public List<Label> getSelection() {
		int[] indices = parent.getSelectedIndices();
		List<Label> result = new ArrayList<Label>(indices.length);
		int i=0; for (Label label : labels) {
			boolean found=false;
			for (int index : indices)
				if (i == index)
					found=true;
			if (found)
				result.add(label);
			i++;
		}
		return result;
	}
}
