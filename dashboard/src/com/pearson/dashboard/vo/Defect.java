/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pearson.dashboard.vo;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Mohammed Saquib (mohammed.saquib)
 */
public class Defect implements Serializable, Comparable<Defect> {
    private String defectId;
    private String defectDesc;
    private String state;
    private String priority;
    private String project;
    private String lastUpdateDate;
    private String lastUpdateDateOriginal;
    private String defectUrl;
    
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
	public String getDefectUrl() {
		return defectUrl;
	}
	public void setDefectUrl(String defectUrl) {
		this.defectUrl = defectUrl;
	}
	public int compareTo(Defect defect){
		Integer defectIdFromParam = Integer.parseInt(defect.getDefectId().replace("DE", ""));
		Integer defectId = Integer.parseInt(this.defectId.replace("DE", "")); 
		return defectIdFromParam.compareTo(defectId);
	}
}
