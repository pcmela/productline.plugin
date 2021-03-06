package productline.plugin.contentassist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diploma.productline.DaoUtil;
import diploma.productline.dao.ModuleDAO;
import diploma.productline.dao.PackageDAO;
import diploma.productline.dao.VariabilityDAO;
import diploma.productline.entity.Module;
import diploma.productline.entity.Variability;

public class ProductLineProposalProcessor implements IContentAssistProcessor {

	private static Logger LOG = LoggerFactory
			.getLogger(ProductLineProposalProcessor.class);

	private final String PREFIX_VARIABILITY = "@variability";
	private final String PREFIX_MODULE = "@module";
	private static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];
	private static final IContextInformation[] NO_CONTEXTS = new IContextInformation[0];
	private Set<Variability> variabilities;
	private Set<Module> modules;
	private Properties properties;

	public ProductLineProposalProcessor() {
		properties = getProperties();
		variabilities = getVariabilities();
		modules = getModules();
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		properties = getProperties();

		try {
			// get prefix i.e. @variability
			String prefix = getPrefix(viewer, offset);

			if (prefix == null || prefix.length() == 0)
				return NO_PROPOSALS;

			if (prefix.startsWith(PREFIX_VARIABILITY)) {
				variabilities = getVariabilities();
				if (variabilities != null && variabilities.size() > 0) {
					// String which be replaced with new name
					String stringToBeReplaced = getStringToBeReplaces(viewer,
							offset);

					return getArrayOfProposalVariabilities(stringToBeReplaced,
							offset);
				} else {
					return NO_PROPOSALS;
				}
			} else if (prefix.startsWith(PREFIX_MODULE)) {
				modules = getModules();
				if (modules != null && modules.size() > 0) {
					// String which be replaced with new name
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
			LOG.error(e.getMessage());
		}

		return NO_PROPOSALS;
	}

	/**
	 * Get arrays of variabilities based on actual modul
	 * @param stringToBeReplaced
	 * @param offset
	 * @return
	 */
	private CompletionProposal[] getArrayOfProposalVariabilities(
			String stringToBeReplaced, int offset) {
		ArrayList<CompletionProposal> compProposal = new ArrayList<>();
		int i = 0;
		for (Variability v : variabilities) {
			if (!stringToBeReplaced.equals("")) {
				if (v.getName().toLowerCase()
						.startsWith(stringToBeReplaced.toLowerCase())) {
					compProposal.add(new CompletionProposal(v.getName(), offset
							- stringToBeReplaced.length(), stringToBeReplaced
							.length(), v.getName().length()));
				}
			}
		}

		return compProposal
				.toArray(new CompletionProposal[compProposal.size()]);
	}

	/**
	 * Get arrays of all modules or modules which starting with stringToBeReplaced
	 * @param stringToBeReplaced
	 * @param offset
	 * @return
	 */
	private CompletionProposal[] getArrayOfProposalModules(
			String stringToBeReplaced, int offset) {
		ArrayList<CompletionProposal> compProposal = new ArrayList<>();
		int i = 0;
		for (Module v : modules) {
			if (!stringToBeReplaced.equals("")) {
				if (v.getName().toLowerCase()
						.startsWith(stringToBeReplaced.toLowerCase())) {
					compProposal.add(new CompletionProposal(v.getName(), offset
							- stringToBeReplaced.length(), stringToBeReplaced
							.length(), v.getName().length()));

				}
			}
		}

		return compProposal
				.toArray(new CompletionProposal[compProposal.size()]);
	}

	private IProject getEclipseProject() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart editorPart = activePage.getActiveEditor();
		FileEditorInput fei = (FileEditorInput) editorPart.getEditorInput();
		IFile f = fei.getFile();
		return f.getProject();
	}

	/**
	 * Get package of the current Java class
	 * @return
	 */
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
			LOG.error(e.getMessage());
			return null;
		}
	}

	/**
	 * Return String which will be replaced with new name
	 * @param viewer
	 * @param offset
	 * @return
	 * @throws BadLocationException
	 */
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
		// if offset is out of range of document then exit method
		if (doc == null || offset > doc.getLength())
			return null;

		int length = 0;
		boolean skipedSpaces = false;

		boolean firstIsSpace = doc.getChar(offset - 1) == ' ';

		if (firstIsSpace) {
			/**
			 * Iterate over doc then you get the first word before your actual
			 * cursor position
			 */
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
			/**
			 * Iterate over doc then you get the first word before your actual
			 * cursor position
			 */
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

		return doc.get(offset, length);
	}

	/**
	 * Load properties from the product line configuration file
	 * 
	 * @return
	 */
	private Properties getProperties() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(getEclipseProject()
					.getLocation() + "/configuration.productline"));
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage());
		} catch (IOException e) {
			LOG.error(e.getMessage());
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
			LOG.error(e.getMessage());
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}

		return new HashSet<Variability>();
	}

	private Set<Module> getModules() {
		try (Connection con = DaoUtil.connect(properties)) {
			IPackageDeclaration pkg = getPackage();
			ModuleDAO mDao = new ModuleDAO();
			try {
				return mDao.getModuleByProductLine(con, Integer
						.parseInt(properties.getProperty("productline_id")));
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage());
				return new HashSet<>();
			}
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage());
		} catch (SQLException e) {
			LOG.error(e.getMessage());
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
