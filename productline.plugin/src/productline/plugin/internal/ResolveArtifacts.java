package productline.plugin.internal;

import java.util.ArrayList;
import java.util.Set;

public class ResolveArtifacts {
	private final String START_MODULE = "@module";
	private final String START_VARIABILITY = "@variability";
	private final String END_MODULE = "@endModule";
	private final String END_VARIABILITY = "@endVariability";
	
	private final String variabilityPattern = "(.*)@variability";
	private final String modulePattern = "(.*)@module";

	private boolean processingElement = false;
	private boolean beginOfComment = false;
	private boolean processingVariability = false;
	private boolean processingModule = false;
	private boolean startOfComment = false;
	private boolean reachElement = false;
	
	private ArrayList<String> buffer = new ArrayList<>();
	
	private String module;
	private Set<String> variabilities;
	
	public ResolveArtifacts(String module, Set<String> variabilities){
		this.module = module;
		this.variabilities = variabilities;
	}
	
	public String processLine(String line){
		String newLine = "";
		
		if(processingElement){
			if(beginOfComment){
				newLine = line;
				
				buffer.add(line + "\n");
				
				if(line.contains(START_MODULE) && !processingModule){
					String name = line.replaceFirst(modulePattern, "").trim();
					reachElement = true;
					if(module.equals(name)){
						processingModule = false;
						processingElement = false;
					}else{
						processingModule = true;
						processingElement = true;
					}
				}else if(line.contains(START_VARIABILITY) && !processingVariability){
					String name = line.replaceFirst(variabilityPattern, "").trim();
					reachElement = true;
					boolean exist = false;
					for(String v : variabilities){
						if(v.equals(name)){
							exist = true;
							break;
						}
					}
					if(!exist){
						processingVariability = true;
						processingElement = true;
					}else{
						processingVariability = false;
						processingElement = false;
					}
				}else if(line.contains(END_MODULE) || line.contains(END_VARIABILITY)){
					setAllToFalse();				
				}else if(line.contains("*/") && (processingModule || processingVariability)){
					startOfComment = true;
					beginOfComment = false;
				}/*else{
					startOfComment = false;
					beginOfComment = false;
				}*/
			}else{
				
				if(line.contains("/**")){
					processingElement = true;
					beginOfComment = true;
				}
				
				if(startOfComment && !beginOfComment && reachElement){
					newLine = "//" + line;
				}else{
					newLine = line;
				}
			}
		}else{
			if(line.contains("/**")){
				processingElement = true;
				beginOfComment = true;
			}
			newLine = line;
		}
		
		return newLine;
	}
	
	private void setAllToFalse(){
		processingElement = false;
		beginOfComment = false;
		processingVariability = false;
		processingModule = false;
		startOfComment = false;
		reachElement = false;
	}
}
