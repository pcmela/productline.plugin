package productline.plugin.internal;

import java.util.ArrayList;
import java.util.Set;

public class ResolveArtifactsXml {
	private final String START_VARIABILITY = "(.*)<!--( *)@variability( *[a-z]*[0-9]*)*-->";
	String END_VARIABILITY = "(.*)<!--( *)@endvariability( *)-->";
	
	private final String variabilityPatternStart = "(.*)@variability";
	private final String variabilityPatternEnd = "( *)-->";

	private boolean processingElement = false;
	private boolean beginOfComment = false;
	private boolean processingVariability = false;
	private boolean processingModule = false;
	private boolean startOfComment = false;
	private boolean reachElement = false;
	private String lastLine = "";
	
	private ArrayList<String> buffer = new ArrayList<>();
	
	private String module;
	private Set<String> variabilities;
	
	public ResolveArtifactsXml(String module, Set<String> variabilities){
		this.module = module;
		this.variabilities = variabilities;
	}
	
	public String processLine(String line){
		
		if(processingElement){
			if(line.toLowerCase().matches(END_VARIABILITY)){
				if(lastLine.equals("")){
					return line;
				}else{
					String l = new String(lastLine);
					setAllToFalse();
					return l + "-->\n" + line;
				}
			}else{
				if(lastLine.equals("")){
					lastLine = "<!--" + line;
					return null;
				}else{
					String lineToReturn = lastLine;
					lastLine = line;
					return lineToReturn;
				}
			}
		}else{
			if(line.toLowerCase().matches(START_VARIABILITY)){
				
				String name = line.replaceAll(variabilityPatternStart, "").replaceAll(variabilityPatternEnd, "").trim();
				
				boolean exist = false;
				for(String v : variabilities){
					if(v.equals(name)){
						exist = true;
						break;
					}
				}
				
				if(exist){
					processingVariability = true;
					processingElement = true;
					beginOfComment = true;
				}else{
					processingVariability = false;
					processingElement = false;
					beginOfComment = false;
				}
			}
			return line;
		}
	}
	
	private void setAllToFalse(){
		lastLine = "";
		processingElement = false;
		beginOfComment = false;
		processingVariability = false;
		processingModule = false;
		startOfComment = false;
		reachElement = false;
	}
}
