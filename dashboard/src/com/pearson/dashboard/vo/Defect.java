/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pearson.dashboard.vo;

import java.io.Serializable;

/**
 *
 * @author Dell
 */
public class Defect implements Serializable{
    private String defectId;
    private String defectDesc;
    private String state;
    private String priority;
    private String project;
    private String lastUpdateDate;
    private String lastUpdateDateOriginal;
    
	public String getDefectId() {
		return defectId;
	}
	public void setDefectId(String defectId) {
		this.defectId = defectId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getDefectDesc() {
		return defectDesc;
	}
	public void setDefectDesc(String defectDesc) {
		this.defectDesc = defectDesc;
	}
	public String getLastUpdateDateOriginal() {
		return lastUpdateDateOriginal;
	}
	public void setLastUpdateDateOriginal(String lastUpdateDateOriginal) {
		this.lastUpdateDateOriginal = lastUpdateDateOriginal;
	}
}
