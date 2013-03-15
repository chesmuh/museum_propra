package de.museum.berleburg.userInterface.listeners;

import java.util.Collection;

import de.museum.berleburg.datastorage.model.Label;

/**
 * Exchange Labels between dialogs.
 * <p>
 * Example:
 * <p>
 * <pre>{@code}
 *  class LabelService {
 *  	List&ltLabelListener&gt listeners = new LinkedList&ltLabelListener&gt();
 *  	public void addListener (LabelListener listener) {
 *  		listeners.add(listener);
 *  	}
 *  	public void doStuff() {
 *  		tamtamtam();
 *  		blupp();
 *  		Label label=new Label("sowieso, "+getBla());
 *  		result(new LinkedList&ltLabel&gt(label));
 *  	}
 *  	private void result (Collection&ltLabel&gt labels) {
 *  		for (LabelListener listener : listeners)
 *  			listener.event(labels); 
 *  	}
 *  	public LabelService() {}
 *  }
 *  //this class requests a label list from a LabelService
 *  class LabelClient {
 *  	private Collection&ltLabel&gt fetchedLabels=null;
 *  	public void doStuff() {
 *  		sowieso();
 *  		LabelService ls = new LabelService();
 *  		ls.addListener(new LabelListener() {
 *  			public void event (Collection&ltLabel&gt labels) {
 *  				fetchedLabels=labels;
 *  				updateLabels_usw();
 *  			}
 *  		});
 *  		ls.doStuff();
 *  	}
 *  }
 * </pre>
 * 			
 * @author Christian Landel
 *
 */
public interface LabelListener {
	public void event (Collection<Label> labels);
}
