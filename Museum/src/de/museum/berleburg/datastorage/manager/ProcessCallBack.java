package de.museum.berleburg.datastorage.manager;


/**
 *
 * @author Anselm
 */
public interface ProcessCallBack
{
    /**
     * 
     * @param percent the current process-status in percent
     */
    public void updateProcess(int percent,int manager);
}
