package productline.plugin.contentassist;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class ProductLineProposalProcessor implements IContentAssistProcessor {

	private final String PREFIX_VARIABILITY = "@variability";
	private final String PREFIX_MODULE = "@module";
	
	private static final ICompletionProposal[] NO_PROPOSALS= new ICompletionProposal[0];
	private static final IContextInformation[] NO_CONTEXTS= new IContextInformation[0];
	
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		
		try {
			String prefix = getPrefix(viewer, offset);
			
			if (prefix == null || prefix.length() == 0)
				return NO_PROPOSALS;
			
			if(prefix.startsWith(PREFIX_VARIABILITY) || prefix.startsWith(PREFIX_MODULE)){
				IPackageDeclaration pkg = getPackage();
				
				CompletionProposal[] array = new CompletionProposal[3]; 
				array[0] = new CompletionProposal("Variability1", offset -3, 3, "Variability1".length());
				array[1] = new CompletionProposal("Variability2", offset -3, 3, 0);
				array[2] = new CompletionProposal("Variability3", offset -3, 3, 0);
				
				return array;
			}else{
				return NO_PROPOSALS;
			}
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return NO_PROPOSALS;
	}
	
	private IPackageDeclaration getPackage(){
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editorPart = activePage.getActiveEditor();
		FileEditorInput fei = (FileEditorInput)editorPart.getEditorInput();
		IFile f = fei.getFile();
		ICompilationUnit cu = JavaCore.createCompilationUnitFrom(f);
		IPackageDeclaration[] pd = null;
		try {
			pd = cu.getPackageDeclarations();
			if(pd.length > 0){
				return pd[0];
			}else{
				return null;
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getPrefix(ITextViewer viewer, int offset) throws BadLocationException {
		IDocument doc= viewer.getDocument();
		if (doc == null || offset > doc.getLength())
			return null;

		int length= 0;
		boolean skipedSpaces = false;
		
		boolean firstIsSpace = doc.getChar(offset - 1) == ' ';
		
		if(firstIsSpace){
			while (--offset >= 0 && Character.isJavaIdentifierPart(doc.getChar(offset)) ||
					(doc.getChar(offset) == ' ' && skipedSpaces == false) ||
					(doc.getChar(offset) == '@')){
				length++;
				char c = doc.getChar(offset);
				if(c == '@'){
					break;
				}
				if(!skipedSpaces && c != ' '){
					skipedSpaces = true;
				}
			}
		}else{
			boolean reachedSpace = false;
			while (--offset >= 0 && Character.isJavaIdentifierPart(doc.getChar(offset)) ||
					(doc.getChar(offset) == ' ' && skipedSpaces == false) ||
					(doc.getChar(offset) == '@')){
				length++;
				char c = doc.getChar(offset);
				if(c == '@'){
					break;
				}
				
				if(c == ' ' && !reachedSpace){
					reachedSpace = true;
				}
				
				if(reachedSpace && (!skipedSpaces && c != ' ')){
					skipedSpaces = true;
				}
			}
		}
		String r = doc.get(offset, length);
		String r1 = doc.get(offset, length + 1);
		String r2 = doc.get(offset + 1, length);
		return doc.get(offset, length);
	}
	
	// ------------------------------------------------------
	
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return NO_CONTEXTS;
	}
	
	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}
	
	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}
	
	@Override
	public String getErrorMessage() {
		return null;
	}

}
