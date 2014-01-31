package productline.plugin.contentassist;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

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
