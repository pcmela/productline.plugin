package productline.plugin.contentassist;

import org.eclipse.jdt.core.CompletionProposal;

public class ProductLineCompletionProposal extends CompletionProposal {

	private final String fString;
	private final String fPrefix;
	private final int fOffset;

	public ProductLineCompletionProposal(String string, String prefix, int offset) {
		fString= string;
		fPrefix= prefix;
		fOffset= offset;
	}
	
	

}
