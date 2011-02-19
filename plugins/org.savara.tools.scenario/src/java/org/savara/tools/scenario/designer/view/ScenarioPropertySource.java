/*
 * Copyright 2005 Pi4 Technologies Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Change History:
 * Jan 10, 2006 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.view;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.savara.scenario.model.*;
import org.savara.tools.scenario.designer.DesignerDefinitions;

public class ScenarioPropertySource extends org.savara.tools.scenario.designer.util.PropertySource {

	/**
	 * This is the constructor for the property source.
	 * 
	 * @param elem The source
	 */
	public ScenarioPropertySource(Object elem) {
		super(elem);
	}

	/* TODO: GPB:
	public IPropertyDescriptor createAttributePropertyDescriptor(EAttribute attr) {
		IPropertyDescriptor ret=null;
		String label=getLabel(attr);
		
		if (label.indexOf("URL") != -1) {
			ret = new URLPropertyDescriptor(Integer.toString(attr.getFeatureID()),
							label, getElement());
		} else {
			ret = super.createAttributePropertyDescriptor(attr);
		}
		
		return(ret);
	}
	*/

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] ret=super.getPropertyDescriptors();
	
		/* TODO: GPB:		
		java.util.Vector list=new java.util.Vector();
		boolean f_changed=false;
		boolean f_businessView=DesignerDefinitions.isPreference(DesignerDefinitions.BUSINESS_VIEW);
		
		for (int i=0; i < ret.length; i++) {
			
			if (ret[i].getDisplayName().endsWith("Id") &&
					(ItemProviderSupport.isShowIds((ScenarioObject)getElement()) == false ||
							f_businessView)) {
				f_changed = true;
			} else if (ret[i].getDisplayName().startsWith("Participant") ||
					ret[i].getDisplayName().startsWith("Direction")) {
				f_changed = true;
			} else if (ret[i].getDisplayName().equals("Service Type")) {
				f_changed = true;
			} else if (ret[i].getDisplayName().equals("Value") && f_businessView) {
				f_changed = true;
			//} else if (ret[i].getDisplayName().equals("Causes Exception") && f_businessView) {
			//	f_changed = true;
			} else {
				list.add(ret[i]);
			}
		}

		if (f_changed) {
			ret = new IPropertyDescriptor[list.size()];
			list.toArray(ret);
		}
		*/
		
		return(ret);
	}
	
	/* TODO: GPB:
	@Override
	protected String getLabel(EStructuralFeature feature) {
		if (feature == ScenarioPackage.eINSTANCE.getLifelineItem_CausesException()) {
			return("Expected To Fail");
		} else {
			return(super.getLabel(feature));
		}
	}
	*/

	/**
	 * This method determines whether the supplied property
	 * represents a derived list of values.
	 * 
	 * @param feature The feature
	 * @return Whether the feature has a derived list
	 */
	/* TODO: GPB:
	public boolean isDerivedList(EStructuralFeature feature) {
		boolean ret=ItemProviderSupport.isChoice(feature,
					getElement());
		
		return(ret);
	}
	*/
	
	/**
	 * This method returns the list of values relevant for the
	 * supplied reference.
	 * 
	 * @param src The source object
	 * @param ref The reference
	 * @return The list of values
	 */
	/* TODO: GPB:
	public java.util.List getValues(EReference ref) {
		java.util.List ret=null;
		
		if (getElement() instanceof ScenarioObject) {
			ret = ItemProviderSupport.getChoiceOfValues((ScenarioObject)getElement(), ref);
		}
		
		if (ret == null) {
			ret = new java.util.Vector();
		}
		
		return(ret);
	}
	*/
	
	/**
	 * This method returns the class name for the enumeration
	 * value.
	 * 
	 * @param enumval The enumeration value
	 * @return The class name
	 */
	/* TODO: GPB:
	protected String getEnumClassName(EEnum enumval) {
		return("org.pi4soa.cdl."+enumval.getName());
	}
	*/
	
	/**
	 * This method determines if the supplied attribute can be
	 * provided as a text region.
	 * 
	 * @param attr The attribute
	 * @return Whether it can be provided as a text region
	 */
	/* TODO: GPB:
	public boolean isTextRegion(EAttribute attr) {
		boolean ret=false;
		
		if (attr == ScenarioPackage.eINSTANCE.getScenarioObject_Description() ||
				attr == ScenarioPackage.eINSTANCE.getMessageEvent_Value() ||
				attr == ScenarioPackage.eINSTANCE.getScenario_Description()) {
			ret = true;
		}
		
		return(ret);
	}
	*/
	
	/**
	 * This method determines if the supplied attribute can be
	 * provided as an editable list.
	 * 
	 * @param attr The attribute
	 * @return Whether it can be provided as an editable list
	 */
	/* TODO: GPB:
	public boolean isEditableList(EAttribute attr) {
		boolean ret=false;
		
		if (getElement() instanceof LifelineItem &&
				((LifelineItem)getElement()).getCausesException() != null &&
				((LifelineItem)getElement()).getCausesException().booleanValue()) {
			ret = ItemProviderSupport.isChoice(attr,
								getElement());
		}
		
		return(ret);
	}
	*/
		
	/**
	 * This method provides the list of values that can be
	 * provided in an editable property field with associated
	 * list.
	 * 
	 * @param attr The attribute
	 * @return The list of values
	 */
	/* TODO: GPB:
	public java.util.List getStringValues(EAttribute attr) {
		java.util.List ret=new java.util.Vector();
		
		if (getElement() instanceof ScenarioObject) {
			java.util.List choices=
				ItemProviderSupport.getChoiceOfValues((ScenarioObject)getElement(),
					attr);
			
			if (choices != null) {
				ret.addAll(choices);
			}
			
			if (ret.size() > 0 && ret.get(0).equals("") &&
					getElement() instanceof LifelineItem &&
					((LifelineItem)getElement()).getCausesException() != null &&
					((LifelineItem)getElement()).getCausesException().booleanValue()) {
				ret.remove(0);
			}
		}
		
		return(ret);
	}
	*/

	/**
	 * This method returns whether the supplied integer
	 * attribute should use a blank string when the
	 * integer value is zero.
	 * 
	 * @param attr The atribute
	 * @return Whether a blank string should be used for zero
	 */
	/* TODO: GPB:
	protected boolean isZeroBlank(EAttribute attr) {
		boolean ret=false;
		
		return(ret);
	}
	*/
}
