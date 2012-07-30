/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.savara.tools.bpmn2.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
//import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.savara.tools.bpmn2.osgi.Activator;

/**
 * This class provides the property page for the Savara BPMN2 tools.
 *
 */
public class BPMN2PropertyPage extends PropertyPage {

    private static final String PATH_TITLE = "Path:";
	private static final String MBI_TITLE = "&Generate Message Based Service Invocations:";
	private static final int DEFAULT_INDEX=0;

	private Combo _generatembi=null;

	/**
	 * Default constructor.
	 */
	public BPMN2PropertyPage() {
		super();
	}

	/**
	 * This method provides information about the resource.
	 * 
	 * @param parent
	 */
	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		//Label for path field
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText(PATH_TITLE);

		// Path text field
		Text pathValueText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
		pathValueText.setEnabled(false);
		pathValueText.setText(((IResource) getElement()).getFullPath().toString());
	}

	/**
	 * This method adds the separator.
	 * 
	 * @param parent
	 */
	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	/**
	 * This method adds the section related to the properties.
	 * 
	 * @param parent The composite parent
	 */
	private void addSecondSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label
		Label enabledLabel = new Label(composite, SWT.NONE);
		enabledLabel.setText(MBI_TITLE);

		// Combo
		_generatembi = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		_generatembi.add(Boolean.FALSE.toString());
		_generatembi.add(Boolean.TRUE.toString());
		
		try {
			String enabled =
				((IResource) getElement()).getPersistentProperty(
					new QualifiedName(Activator.PLUGIN_ID,
							BPMN2Properties.GENERATE_MESSAGE_BASED_INVOCATION));
			
			if (enabled == null) {
				_generatembi.select(DEFAULT_INDEX);
				
			} else if (enabled.equalsIgnoreCase(Boolean.TRUE.toString())) {
				_generatembi.select(_generatembi.getItemCount()-1);
			} else {
				_generatembi.select(_generatembi.getItemCount()-2);
			}

		} catch (CoreException e) {
			_generatembi.select(DEFAULT_INDEX);
		}		
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addFirstSection(composite);
		addSeparator(composite);
		addSecondSection(composite);

		return composite;
	}

	/**
	 * This method creates the default composite.
	 * 
	 * @param parent The parent composite
	 * @return The new default composite
	 */
	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	/**
	 * This method resets the properties to their default values.
	 */
	protected void performDefaults() {
	}
	
	/**
	 * This method applies the properties to the persistent
	 * storage associated with the resource.
	 */
	public boolean performOk() {
		// store the value in the appropriate persistent property
		try {
			String val=_generatembi.getItem(
					_generatembi.getSelectionIndex());
			
			((IResource) getElement()).setPersistentProperty(
				new QualifiedName(Activator.PLUGIN_ID,
						BPMN2Properties.GENERATE_MESSAGE_BASED_INVOCATION), val);
			
			//updateResource();
			// Need to force resource to re-validate itself
			((IResource)getElement()).touch(null);

		} catch (CoreException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method updates the effected resources, so they can
	 * re-validated if necessary.
	 * 
	 * @throws CoreException
	 */
	/*
	protected void updateResource() throws CoreException {
		
		if (getElement() instanceof IProject) {
			IProject proj=(IProject)getElement();
			
			proj.accept(new IResourceVisitor() {
				public boolean visit(IResource res) {
					
					if (res instanceof IFile &&
							((IFile)res).getFileExtension() != null &&
							((IFile)res).getFileExtension().equals(
									CDLDefinitions.CDL_FILE_EXTENSION)) {
						try {
							res.touch(null);
						} catch(CoreException ex) {
							logger.severe("Failed to 'touch' CDL " +
									"file to force re-validation: "+ex);
						}
					}
					
					return(true);
				}
			});
			
		} else {
			// Need to force resource to re-validate itself
			((IResource)getElement()).touch(null);
		}
	}
	*/
}