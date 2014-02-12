package productline.plugin.contentassist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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

import diploma.productline.DaoUtil;
import diploma.productline.dao.ModuleDAO;
import diploma.productline.dao.PackageDAO;
import diploma.productline.dao.ProductLineDAO;
import diploma.productline.dao.VariabilityDAO;
import diploma.productline.entity.Module;
import diploma.productline.entity.Variability;

public class ProductLineProposalProcessor implements IContentAssistProcessor {

	private final String PREFIX_VARIABILITY = "@variability";
	private final String PREFIX_MODULE = "@module";
	private static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];
	private static final IContextInformation[] NO_CONTEXTS = new IContextInformation[0];
	private Set<Variability> variabilities;
	private Set<Module> modules;
	private Properties properties;
	private int count = 0;

	public ProductLineProposalProcessor() {
		properties = getProperties();
		variabilities = getVariabilities();
		modules = getModules();
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		properties = getProperties();
		System.out.println(count);
		count++;
		try {
			String prefix = getPrefix(viewer, offset);

			if (prefix == null || prefix.length() == 0)
				return NO_PROPOSALS;

			if (prefix.startsWith(PREFIX_VARIABILITY)) {
				variabilities = getVariabilities();
				if (variabilities != null && variabilities.size() > 0) {
					String stringToBeReplaced = getStringToBeReplaces(viewer,
							offset);

					return getArrayOfProposalVariabilities(stringToBeReplaced, offset);
				} else {
					return NO_PROPOSALS;
				}
			} else if(prefix.startsWith(PREFIX_MODULE)){
				modules = getModules();
				if (modules != null && modules.size() > 0) {
					String stringToBeReplaced = getStringToBeReplaces(viewer,
							offset);

					return getArrayOfProposalModules(stringToBeReplaced, offset);
				} else {
					return NO_PROPOSALS;
				}
			} else {
				return NO_PROPOSALS;
			}

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return NO_PROPOSALS;
	}

	private CompletionProposal[] getArrayOfProposalVariabilities(String stringToBeReplaced, int offset) {
		ArrayList<CompletionProposal> compProposal = new ArrayList<>();
		int i = 0;
		for (Variability v : variabilities) {
			if (!stringToBeReplaced.equals("")) {
				if (v.getName().toLowerCase()
						.startsWith(stringToBeReplaced.toLowerCase())) {
					compProposal.add(new CompletionProposal(v.getName(), offset - stringToBeReplaced.length(),
							stringToBeReplaced.length(), v.getName().length()));
				}
			}
		}
		
		return compProposal.toArray(new CompletionProposal[compProposal.size()]);
	}
	
	private CompletionProposal[] getArrayOfProposalModules(String stringToBeReplaced, int offset) {
		ArrayList<CompletionProposal> compProposal = new ArrayList<>();
		int i = 0;
		for (Module v : modules) {
			if (!stringToBeReplaced.equals("")) {
				if (v.getName().toLowerCase()
						.startsWith(stringToBeReplaced.toLowerCase())) {
					compProposal.add(new CompletionProposal(v.getName(), offset - stringToBeReplaced.length(),
							stringToBeReplaced.length(), v.getName().length()));
			
				}
			}
		}
		
		return compProposal.toArray(new CompletionProposal[compProposal.size()]);
	}

	private IProject getEclipseProject() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart editorPart = activePage.getActiveEditor();
		FileEditorInput fei = (FileEditorInput) editorPart.getEditorInput();
		IFile f = fei.getFile();
		return f.getProject();
	}

	private IPackageDeclaration getPackage() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart editorPart = activePage.getActiveEditor();
		FileEditorInput fei = (FileEditorInput) editorPart.getEditorInput();
		IFile f = fei.getFile();
		ICompilationUnit cu = JavaCore.createCompilationUnitFrom(f);
		IPackageDeclaration[] pd = null;
		try {
			pd = cu.getPackageDeclarations();
			if (pd.length > 0) {
				return pd[0];
			} else {
				return null;
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getStringToBeReplaces(ITextViewer viewer, int offset)
			throws BadLocationException {
		IDocument doc = viewer.getDocument();
		if (doc == null || offset > doc.getLength())
			return null;

		if (doc.getChar(offset - 1) == ' ') {
			return "";
		}

		int length = 0;

		while (--offset >= 0 && doc.getChar(offset) != ' ') {
			length++;
		}

		return doc.get(offset + 1, length);
	}

	private String getPrefix(ITextViewer viewer, int offset)
			throws BadLocationException {
		IDocument doc = viewer.getDocument();
		if (doc == null || offset > doc.getLength())
			return null;

		int length = 0;
		boolean skipedSpaces = false;

		boolean firstIsSpace = doc.getChar(offset - 1) == ' ';

		if (firstIsSpace) {
			while (--offset >= 0
					&& Character.isJavaIdentifierPart(doc.getChar(offset))
					|| (doc.getChar(offset) == ' ' && skipedSpaces == false)
					|| (doc.getChar(offset) == '@')) {
				length++;
				char c = doc.getChar(offset);
				if (c == '@') {
					break;
				}
				if (!skipedSpaces && c != ' ') {
					skipedSpaces = true;
				}
			}
		} else {
			boolean reachedSpace = false;
			while (--offset >= 0
					&& Character.isJavaIdentifierPart(doc.getChar(offset))
					|| (doc.getChar(offset) == ' ' && skipedSpaces == false)
					|| (doc.getChar(offset) == '@')) {
				length++;
				char c = doc.getChar(offset);
				if (c == '@') {
					break;
				}

				if (c == ' ' && !reachedSpace) {
					reachedSpace = true;
				}

				if (reachedSpace && (!skipedSpaces && c != ' ')) {
					skipedSpaces = true;
				}
			}
		}
		String r = doc.get(offset, length);
		String r1 = doc.get(offset, length + 1);
		String r2 = doc.get(offset + 1, length);
		return doc.get(offset, length);
	}

	private Properties getProperties() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(getEclipseProject()
					.getLocation() + "/configuration.productline"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
	}

	private Set<Variability> getVariabilities() {
		try (Connection con = DaoUtil.connect(properties)) {
			IPackageDeclaration pkg = getPackage();
			PackageDAO pDao = new PackageDAO();
			VariabilityDAO vDao = new VariabilityDAO();
			String moduleId = pDao.getModuleIdByPackageName(
					properties.getProperty("productline_id"),
					pkg.getElementName(), con);
			return variabilities = vDao.getVariabilitiesByModuleId(moduleId,
					con);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new HashSet<Variability>();
	}
	
	private Set<Module> getModules(){
		try (Connection con = DaoUtil.connect(properties)) {
			IPackageDeclaration pkg = getPackage();
			ModuleDAO mDao = new ModuleDAO();
			try{
				return mDao.getModuleByProductLine(con, Integer.parseInt(properties.getProperty("productline_id")));
			}catch (NumberFormatException e){
				e.printStackTrace();
				return new HashSet<>();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new HashSet<Module>();
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
