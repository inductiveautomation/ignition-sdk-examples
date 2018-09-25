package com.inductiveautomation.ignition.examples.consolenotification.profile;

import java.util.Collection;

import org.apache.log4j.Level;

import com.inductiveautomation.ignition.common.util.LoggerEx;

public enum LogLevel {
	TRACE(Level.TRACE),
	DEBUG(Level.DEBUG),
	INFO(Level.INFO),
	WARN(Level.WARN),
	ERROR(Level.ERROR),
	FATAL(Level.FATAL);
	
	Level level;
	
	LogLevel(Level level ){
		this.level = level;
	}
	
	public void log(LoggerEx log, String message){
		if(log != null){
			log.getLogger().log(level, message);
		}	
	}
	
	public void log(LoggerEx log, Collection<?> objects, String formatString, String message){
		if(log != null){
			for(Object obj : objects){
				String str = String.format(formatString, obj.toString(), message);
				this.log(log, str);
			}
		}	
	}
	
	

}
