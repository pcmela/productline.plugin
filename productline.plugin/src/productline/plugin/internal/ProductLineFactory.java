package productline.plugin.internal;

import org.eclipse.core.resources.IProject;

import diploma.productline.entity.ProductLine;

public class ProductLineFactory {

	private ProductLine productLine;
	private String destinationPath;
	private IProject project;
	
	public ProductLineFactory(ProductLine productLine, String destinationPath, IProject project){
		this.productLine = productLine;
		this.destinationPath = destinationPath;
		this.project = project;
	}
	
	
}
