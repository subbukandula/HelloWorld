package com.subbu.todoApp.datamodel;

/**
 * 
 * @author subbu
 *
 */
public class ApplicationItem {

	private String applicationName;
	
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public ApplicationItem(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String toString() {
        return applicationName;
    }
	
}
