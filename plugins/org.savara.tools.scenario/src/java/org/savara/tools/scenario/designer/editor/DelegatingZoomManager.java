/*
 * Copyright 2005 Pi4 Technologies Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Change History:
 * Jul 5, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.editor;

import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.util.ListenerList;

/**
 * The zoom manager.
 */
public class DelegatingZoomManager extends ZoomManager implements ZoomListener {

    /**
     * Creates a new DelegatingZoomManager instance.
     */
    public DelegatingZoomManager()
    {
        super((ScalableFigure) null, (Viewport) null);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomListener#zoomChanged(double)
     */
    public void zoomChanged(double zoom)
    {
        Object[] listeners = m_zoomListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i)
        {
            ((ZoomListener) listeners[i]).zoomChanged(zoom);
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#addZoomListener(org.eclipse.gef.editparts.ZoomListener)
     */
    public void addZoomListener(ZoomListener listener)
    {
        m_zoomListeners.add(listener);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#removeZoomListener(org.eclipse.gef.editparts.ZoomListener)
     */
    public void removeZoomListener(ZoomListener listener)
    {
        m_zoomListeners.remove(listener);
    }

    /**
     * Sets the ZoomManager all work should be delegated to.
     * @param zoomManager
     */
    public void setCurrentZoomManager(ZoomManager zoomManager)
    {
        if (null != m_currentZoomManager)
            m_currentZoomManager.removeZoomListener(this);

        m_currentZoomManager = zoomManager;
        if(null != m_currentZoomManager)
        {
            m_currentZoomManager.addZoomListener(this);
            zoomChanged(m_currentZoomManager.getZoom());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#canZoomIn()
     */
    public boolean canZoomIn()
    {
        if(null == m_currentZoomManager)
            return false;
            
        return m_currentZoomManager.canZoomIn();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#canZoomOut()
     */
    public boolean canZoomOut()
    {
        if(null == m_currentZoomManager)
            return false;
            
        return m_currentZoomManager.canZoomOut();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getMaxZoom()
     */
    public double getMaxZoom()
    {
        if(null == m_currentZoomManager)
            return 1;
            
        return m_currentZoomManager.getMaxZoom();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getMinZoom()
     */
    public double getMinZoom()
    {
        if(null == m_currentZoomManager)
            return 1;
            
        return m_currentZoomManager.getMinZoom();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getNextZoomLevel()
     */
    public double getNextZoomLevel()
    {
        if(null == m_currentZoomManager)
            return 1;
            
        return m_currentZoomManager.getNextZoomLevel();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getPreviousZoomLevel()
     */
    public double getPreviousZoomLevel()
    {
        if(null == m_currentZoomManager)
            return 1;
            
        return m_currentZoomManager.getPreviousZoomLevel();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getScalableFigure()
     */
    public ScalableFigure getScalableFigure()
    {
        if(null == m_currentZoomManager)
           return null;
            
       return m_currentZoomManager.getScalableFigure();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getUIMultiplier()
     */
    public double getUIMultiplier()
    {
        if(null == m_currentZoomManager)
            return 1;
            
        return m_currentZoomManager.getUIMultiplier();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getViewport()
     */
    public Viewport getViewport()
    {
        if(null == m_currentZoomManager)
            return null;
            
        return m_currentZoomManager.getViewport();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getZoom()
     */
    public double getZoom()
    {
        if(null == m_currentZoomManager)
            return 1;
            
        return m_currentZoomManager.getZoom();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getZoomAsText()
     */
    public String getZoomAsText()
    {
        if(null == m_currentZoomManager)
            return " 100%";
            
        return m_currentZoomManager.getZoomAsText();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getZoomLevels()
     */
    public double[] getZoomLevels()
    {
        if(null == m_currentZoomManager)
            return new double[] {1};
            
        return m_currentZoomManager.getZoomLevels();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#getZoomLevelsAsText()
     */
    public String[] getZoomLevelsAsText()
    {
        if(null == m_currentZoomManager)
            return new String[] {" 100%"};
            
        return m_currentZoomManager.getZoomLevelsAsText();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#setUIMultiplier(double)
     */
    public void setUIMultiplier(double multiplier)
    {
        if(null == m_currentZoomManager)
            return ;
            
        m_currentZoomManager.setUIMultiplier(multiplier);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#setViewLocation(org.eclipse.draw2d.geometry.Point)
     */
    public void setViewLocation(Point p)
    {
        if(null == m_currentZoomManager)
            return ;
            
        m_currentZoomManager.setViewLocation(p);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#setZoom(double)
     */
    public void setZoom(double zoom)
    {
        if(null == m_currentZoomManager)
            return ;
            
        m_currentZoomManager.setZoom(zoom);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#setZoomAnimationStyle(int)
     */
    public void setZoomAnimationStyle(int style)
    {
        if(null == m_currentZoomManager)
            return ;
            
        m_currentZoomManager.setZoomAnimationStyle(style);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#setZoomAsText(java.lang.String)
     */
    public void setZoomAsText(String zoomString)
    {
        if(null == m_currentZoomManager)
            return ;
            
        m_currentZoomManager.setZoomAsText(zoomString);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#setZoomLevels(double[])
     */
    public void setZoomLevels(double[] zoomLevels)
    {
        if(null == m_currentZoomManager)
            return ;
            
        m_currentZoomManager.setZoomLevels(zoomLevels);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#zoomIn()
     */
    public void zoomIn()
    {
        if(null == m_currentZoomManager)
            return ;
            
        m_currentZoomManager.zoomIn();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#zoomOut()
     */
    public void zoomOut()
    {
        if(null == m_currentZoomManager)
            return ;
            
        m_currentZoomManager.zoomOut();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.ZoomManager#zoomTo(org.eclipse.draw2d.geometry.Rectangle)
     */
    public void zoomTo(Rectangle rect)
    {
        if(null == m_currentZoomManager)
            return ;
            
        m_currentZoomManager.zoomTo(rect);
    }
    
    private ZoomManager m_currentZoomManager;
    private ListenerList m_zoomListeners = new ListenerList(3);
}
