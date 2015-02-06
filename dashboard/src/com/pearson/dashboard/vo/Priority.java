/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pearson.dashboard.vo;

import java.io.Serializable;

/**
 *
 * @author Mohammed Saquib (mohammed.saquib)
 */
public class Priority implements Serializable {
	
    private String priorityName;
    private int priorityCount;
    private String pxSize;
    
    

    public String getPxSize() {
		return pxSize;
	}

	public void setPxSize(String pxSize) {
		this.pxSize = pxSize;
	}

	/**
     * @return the priorityName
     */
    public String getPriorityName() {
        return priorityName;
    }

    /**
     * @param priorityName the priorityName to set
     */
    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    /**
     * @return the priorityCount
     */
    public int getPriorityCount() {
        return priorityCount;
    }

    /**
     * @param priorityCount the priorityCount to set
     */
    public void setPriorityCount(int priorityCount) {
        this.priorityCount = priorityCount;
    }
    
    
}
