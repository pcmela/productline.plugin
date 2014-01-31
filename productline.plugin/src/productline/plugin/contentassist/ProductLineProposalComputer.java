package productline.plugin.contentassist;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

public class ProductLineProposalComputer implements
		IJavaCompletionProposalComputer {
	
	private final ProductLineProposalProcessor processor = new ProductLineProposalProcessor();

	public ProductLineProposalComputer() {
		System.out.println("Completition initialize");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sessionStarted() {
		// TODO Auto-generated method stub
		System.out.println("Completetion started");
	}

	@Override
	public List<ICompletionProposal> computeCompletionProposals(
			ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Arrays.asList(processor.computeCompletionProposals(context.getViewer(), context.getInvocationOffset()));
	}

	@Override
	public List<IContextInformation> computeContextInformation(
			ContentAssistInvocationContext context, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		System.out.println("Completition error");
		return null;
	}

	@Override
	public void sessionEnded() {
		// TODO Auto-generated method stub
		System.out.println("Completition ended");

	}

}
