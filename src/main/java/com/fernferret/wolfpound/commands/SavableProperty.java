package com.fernferret.wolfpound.commands;

public interface SavableProperty {
	public String getPropertyName();
	
	public void setPropertyName(String name);
	
	public void saveProperty(String world);
	
	public void savePropertyGlobal();
	
	public void checkProperty(String world);
	
	public void checkPropertyGlobal();
}
