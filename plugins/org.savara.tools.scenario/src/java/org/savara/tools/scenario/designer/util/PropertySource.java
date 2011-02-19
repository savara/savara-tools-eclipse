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
 * Jul 8, 2005 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;

/**
 * This class implements the property source for a CDL type.
 */
public abstract class PropertySource implements IPropertySource {

	protected PropertySource(Object element) {
		m_element = element;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return(m_element);
	}
	
	/**
	 * This method returns the raw property value.
	 * 
	 * @param pd The property descriptor
	 * @return The raw value
	 */
	public Object getRawPropertyValue(IPropertyDescriptor pd) {
		Object ret=null;
		
		/* GPB:
		EStructuralFeature feature=m_element.eClass().getEStructuralFeature(
				Integer.parseInt((String)pd.getId()));
		
		if (feature instanceof EReference ||
				feature instanceof EAttribute) {
			ret = m_element.eGet(feature);
		}
		*/
		
		return(ret);
	}
	
	/* GPB
	public IPropertyDescriptor createAttributePropertyDescriptor(EAttribute attr) {
		IPropertyDescriptor ret=null;
		
		EDataType type = attr.getEAttributeType();			
		
		if (isEditableList(attr)) {
			ret = new EditableListPropertyDescriptor(
					Integer.toString(attr.getFeatureID()),
					getLabel(attr),
					getStringValues(attr));
		} else if (isDerivedList(attr)) {
			java.util.List list=getStringValues(attr);
			String[] values=new String[list.size()];
			list.toArray(values);
			
			ret = new ComboBoxPropertyDescriptor(
					Integer.toString(attr.getFeatureID()),
					getLabel(attr),
					values);
		} else if (isTextRegion(attr)) {
			ret = new TextRegionPropertyDescriptor(
					Integer.toString(attr.getFeatureID()),
					getLabel(attr));
		} else if (type.getInstanceClass() == String.class) {
			ret = new TextPropertyDescriptor(
					Integer.toString(attr.getFeatureID()),
					getLabel(attr));
		} else if (type.getInstanceClass() == Boolean.class) {
			ret = new ComboBoxPropertyDescriptor(
					Integer.toString(attr.getFeatureID()),
					getLabel(attr),
					new String[]{
						Boolean.FALSE.toString(),
						Boolean.TRUE.toString()});
		} else if (type.getInstanceClass() == int.class) {
			ret = new TextPropertyDescriptor(
					Integer.toString(attr.getFeatureID()),
					getLabel(attr));
		} else if (type instanceof EEnum) {
			EEnum enumval=(EEnum)type;
			java.util.List list=enumval.getELiterals();
			String[] vals=new String[list.size()];
			for (int i=0; i < list.size(); i++) {
				EEnumLiteral literal=(EEnumLiteral)list.get(i);
				vals[i] = literal.getName();
			}
			ret = new ComboBoxPropertyDescriptor(
					Integer.toString(attr.getFeatureID()),
					getLabel(attr), vals);
		}
		
		return(ret);
	}
	
	public IPropertyDescriptor createReferencePropertyDescriptor(EReference ref) {
		IPropertyDescriptor ret=null;
		
		if (ref.isContainment() == false) {
			if (ref.getUpperBound() == 1) {
				java.util.List list=getValues(ref);
				
				if (list.size() < COMBO_SIZE_LIMIT) {
					String[] vals=getStringValues(ref);
					
					ret = new ComboBoxPropertyDescriptor(
							Integer.toString(ref.getFeatureID()),
							getLabel(ref), vals);
				} else {
					Object cur=m_element.eGet(ref);
					
					ret = new FilteredListPropertyDescriptor(
							Integer.toString(ref.getFeatureID()),
							getLabel(ref), getLabel(ref),
							cur, list.toArray());
				}
			} else {
				java.util.List current=(java.util.List)
							m_element.eGet(ref);

				ret = new MultiRefPropertyDescriptor(
						Integer.toString(ref.getFeatureID()),
						getLabel(ref), getLabel(ref),
						m_element, current,
						getValues(ref));
			}
		}

		return(ret);
	}
		*/
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		Iterator	iter=null;
		//EClass		cls = m_element.eClass();
		Collection	descriptors = new Vector();
		 
		/* TODO: GPB
		iter = cls.getEAllAttributes().iterator();
		while(iter.hasNext()) {
			EAttribute attr=(EAttribute)iter.next();
			
			if (attr.isTransient() == false) {
				IPropertyDescriptor pd=createAttributePropertyDescriptor(attr);
				
				if (pd != null) {
					descriptors.add(pd);
				}
			}
		}
		
		iter = cls.getEAllReferences().iterator();
		while (iter.hasNext()) {
			EReference ref=(EReference)iter.next();
			
			if (ref.isTransient() == false) {
				IPropertyDescriptor pd=createReferencePropertyDescriptor(ref);
				
				if (pd != null) {
					descriptors.add(pd);
				}
			}
		}
		*/
		
		return (IPropertyDescriptor[])descriptors.toArray( new IPropertyDescriptor[] {} );
	}
	
	/**
	 * This method determines whether the supplied property
	 * represents a derived list of values.
	 * 
	 * @param feature The feature
	 * @return Whether the feature has a derived list
	 */
	/*
	public boolean isDerivedList(EStructuralFeature feature) {
		boolean ret=false;
		
		return(ret);
	}
	*/
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		/* TODO: GPB
		EStructuralFeature feature=m_element.eClass().getEStructuralFeature( Integer.parseInt( (String)id ) );
		
		Object result = m_element.eGet(feature);
		
		if (feature instanceof EReference) {
		
			if ((feature.getUpperBound() > 1 ||
					feature.getUpperBound() == -1) &&
					result instanceof java.util.List) {
				java.util.List list=(java.util.List)result;
				
				String str="";
				for (int i=0; i < list.size(); i++) {
					if (i > 0) {
						str += ", ";
					}
					str += ViewSupport.getName(list.get(i));
				}
				
				result = str;
				
			} else {
				java.util.List values=getValues((EReference)feature);
				
				if (values.size() < COMBO_SIZE_LIMIT) {
					if (result == null) {
						result = new Integer(0);
					} else {
						int index=values.indexOf(result);
						index++;
					
						result = new Integer(index);
					}
				} else {
					result = ViewSupport.getName(result);
				}
			}
		} else if (feature instanceof EAttribute) {
			EDataType type = ((EAttribute)feature).getEAttributeType();			

			if (isDerivedList(feature) && isEditableList((EAttribute)feature) == false) {
				java.util.List list=getStringValues((EAttribute)feature);
				
				int index=list.indexOf(result);
				
				if (index != -1) {
					result = new Integer(index);
				} else {
					result = new Integer(0);
				}
				
			} else if (type.getInstanceClass() == Boolean.class) {
				
				if (result == null) {
					result = new Integer(0);
				} else if (result instanceof Boolean) {
					if (((Boolean)result).booleanValue()) {
						result = new Integer(1);
					} else {
						result = new Integer(0);
					}
				}
			} else if (type instanceof EEnum &&
						result instanceof Enumerator) {				
				EEnum enumval=(EEnum)type;
				Enumerator res=(Enumerator)result;
				
				java.util.List list=enumval.getELiterals();
				
				int pos=-1;
				
				for (int i=0; pos == -1 && i < list.size(); i++) {
					EEnumLiteral literal=(EEnumLiteral)list.get(i);
					
					if (literal.getName().equals(res.getName())) {
						pos = i;
					}
				}
				
				result = null;
				if (pos != -1) {
					result = new Integer(pos);
				}
			} else if (type.getInstanceClass() == int.class) {
				
				if (result instanceof Integer &&
						((Integer)result).intValue() == 0 &&
						isZeroBlank((EAttribute)feature)) {
					result = "";
				} else {
					result = ""+result;
				}
				
			} else if (result == null) {
				result = "";
			}
		}

		return(result);
		*/
		return(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySouce#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		/* TODO: GPB
		EStructuralFeature feature=m_element.eClass().getEStructuralFeature(Integer.parseInt((String)id ));
		
		if (feature instanceof EReference) {
			
			if (value instanceof Integer) {
				int index=((Integer)value).intValue();
				value = getSelectedValue((EReference)feature, index);
			} else if (value instanceof java.util.List) {
				
				// Get the list and then update each element
				java.util.List list=(java.util.List)
						m_element.eGet(feature);
				
				list.clear();
				
				Iterator iter=((java.util.List)value).iterator();
				while (iter.hasNext()) {
					list.add(iter.next());
				}
			} else {
				org.pi4soa.designer.inference.InferenceManager.propertyChange(m_element,
						((EReference)feature).getName(), value);
			}
			
		} else if (feature instanceof EAttribute) {
			EDataType type=((EAttribute)feature).getEAttributeType();			

			if (value instanceof Integer) {
				
				if (value instanceof Integer && isDerivedList(feature)) {
					java.util.List list=getStringValues((EAttribute)feature);
					int index=((Integer)value).intValue();
					
					if (index != -1) {
						value = list.get(((Integer)value).intValue());
					} else {
						value = "";
					}
					
				} else if (type.getInstanceClass() == Boolean.class) {
					if (((Integer)value).intValue() == 0) {
						value = Boolean.FALSE;
					} else {
						value = Boolean.TRUE;
					}
				} else if (type instanceof EEnum) {
					int val=((Integer)value).intValue();
					
					value = getEnumValue((EEnum)type, val);
				}
			} else {
				if (type.getInstanceClass() == int.class) {
					
					if (value instanceof String &&
							((String)value).trim().length() > 0) {
						value = getIntegerValue((EAttribute)feature,
									(String)value);
					}
					
					if (value == null) {
						// Default value for empty field
						value = new Integer(0);
					}
				} else if (type.getInstanceClass() == String.class) {
		
					org.pi4soa.designer.inference.InferenceManager.propertyChange(m_element,
							((EAttribute)feature).getName(), value);
		
				} else if (type instanceof EEnum) {
		
					if (value instanceof String) {
						org.pi4soa.designer.inference.InferenceManager.propertyChange(m_element,
								((EAttribute)feature).getName(), value);
			
						value = getEnumValue((EEnum)type, (String)value);
					} else if (value instanceof Enumerator) {
						org.pi4soa.designer.inference.InferenceManager.propertyChange(m_element,
								((EAttribute)feature).getName(),
								((Enumerator)value).getLiteral());
					}
				}
			}
		}

		m_element.eSet(feature, value);
		*/
	}
	
	/**
	 * This method returns the integer associated with the supplied
	 * string value and attribute.
	 * 
	 * @param attr The attribute
	 * @param value The string value
	 * @return The integer, or null if the conversion failed
	 */
	/*
	protected Integer getIntegerValue(EAttribute attr, String value) {
		Integer ret=null;
		
		try {
			ret = new Integer(value);
		} catch(Exception e) {
			logger.severe("Failed to convert string '"+
					value+"' to integer: "+e);
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
	/*
	protected boolean isZeroBlank(EAttribute attr) {
		boolean ret=false;
		
		return(ret);
	}
	*/
	
	/**
	 * This method determines if the supplied attribute can be
	 * provided as a text region.
	 * 
	 * @param attr The attribute
	 * @return Whether it can be provided as a text region
	 */
	/*
	public boolean isTextRegion(EAttribute attr) {
		boolean ret=false;
		
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
	/*
	public boolean isEditableList(EAttribute attr) {
		boolean ret=false;
		
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
	/*
	public java.util.List getStringValues(EAttribute attr) {
		java.util.List ret=new java.util.Vector();
		return(ret);
	}
	*/
	
	/**
	 * This method returns the list of values appropriate for
	 * the reference.
	 * 
	 * @param ref The reference
	 * @return The list of values
	 */
	/*
	public String[] getStringValues(EReference ref) {
		String[] ret=new String[0];
		
		java.util.List list=getValues(ref);
		if (list != null) {
			ret = new String[list.size()+1];
			ret[0] = "";
			
			for (int i=0; i < list.size(); i++) {
				ret[i+1] = ViewSupport.getName(list.get(i));
			}
		}
	
		return(ret);
	}
	*/
		
	/**
	 * This method returns the selected value associated with
	 * the reference and the index.
	 * 
	 * @param ref The reference
	 * @param index The index
	 * @return The selected value
	 */
	/*
	public Object getSelectedValue(EReference ref, int index) {
		Object ret=null;
		
		// Subtract 1 from the index to account for the initial
		// empty slot
		index--;
		
		// Check for known referenced class types
		java.util.List list=getValues(ref);
		
		if (list != null && index >= 0 &&
					index < list.size()) {
			ret = list.get(index);
		}
		
		return(ret);
	}
	*/
	
	/**
	 * This method returns the list of values relevant for the
	 * supplied reference.
	 * 
	 * @param ref The reference
	 * @return The list of values
	 */
	public java.util.List getValues(IPropertyDescriptor pd) {
		java.util.List ret=null;
		
		/* TODO: GPB
		EStructuralFeature feature=m_element.eClass().getEStructuralFeature(
				Integer.parseInt((String)pd.getId()));
		
		if (feature instanceof EReference) {
			ret = getValues((EReference)feature);
		} else if (feature instanceof EAttribute) {
			ret = getValues((EAttribute)feature);
		}
		*/

		return(ret);
	}
	
	/**
	 * This method returns the list of values relevant for the
	 * supplied attribute.
	 * 
	 * @param attr The attribute
	 * @return The list of values
	 */
	/*
	public java.util.List getValues(EAttribute attr) {
		java.util.List ret=new java.util.Vector();
		
		if (attr.getEAttributeType() instanceof EEnum) {
			EEnum enumval=(EEnum)attr.getEAttributeType();
			java.util.List list=enumval.getELiterals();
			for (int i=0; i < list.size(); i++) {
				EEnumLiteral literal=(EEnumLiteral)list.get(i);
				ret.add(literal.getName());
			}
		} else {
			ret = getStringValues(attr);
		}
		
		return(ret);
	}
	*/
	
	/**
	 * This method returns the list of values relevant for the
	 * supplied reference.
	 * 
	 * @param ref The reference
	 * @return The list of values
	 */
	//public abstract java.util.List getValues(EReference ref);
	
	/**
	 * This method returns the enumeration value.
	 * 
	 * @param enumval The enumeration
	 * @param index The index position
	 * @return The value
	 */
	/*
	public Object getEnumValue(EEnum enumval, int index) {
		Object ret=null;
		
		try {
			String clsName=getEnumClassName(enumval);
				
			Class cls=Class.forName(clsName);
			
			java.lang.reflect.Method met=
				cls.getMethod("get", new Class[]{int.class});
			
			ret = met.invoke(null, new Object[]{new Integer(index+1)});
			
		} catch(Exception e) {
			// Log error
			logger.severe("Failed to get enumeration value: "+e);
		}
		
		return(ret);
	}
	*/
	
	/**
	 * This method returns the enumeration value.
	 * 
	 * @param enumval The enumeration
	 * @param val The selection text
	 * @return The value
	 */
	/*
	public Object getEnumValue(EEnum enumval, String val) {
		Object ret=null;
		
		try {
			String clsName=getEnumClassName(enumval);
				
			Class cls=Class.forName(clsName);
			
			java.lang.reflect.Method met=
				cls.getMethod("get", new Class[]{String.class});
			
			ret = met.invoke(null, new Object[]{val});
			
		} catch(Exception e) {
			// Log error
			logger.severe("Failed to get enumeration value: "+e);
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
	//protected abstract String getEnumClassName(EEnum enumval);
	
	/**
	 * This method returns the element.
	 * 
	 * @return The element
	 */
	public Object getElement() {
		return(m_element);
	}
	
	public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
		if (logger.isLoggable(java.util.logging.Level.FINEST)) {
			logger.finest(">> "+this+" add "+l);
		}
		
		synchronized(m_propertyChangeListeners) {
			if (m_propertyChangeListeners.size() == 0) {
				// Register to receive notifications
				
				// TODO: GPB How to register for events
				//m_element.eAdapters().add(m_adapter);
			}
			
			if (m_propertyChangeListeners.contains(l) == false) {
				m_propertyChangeListeners.add(l);
			}
		}
	}
	
	public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
		if (logger.isLoggable(java.util.logging.Level.FINEST)) {
			logger.finest(">> "+this+" remove "+l);
		}
		
		synchronized(m_propertyChangeListeners) {
			m_propertyChangeListeners.remove(l);

			if (m_propertyChangeListeners.size() == 0) {
				// Un-Register to receive notifications

				// TODO: GPB How to register for events
				//m_element.eAdapters().remove(m_adapter);
			}
		}
	}
	
    private static Logger logger = Logger.getLogger("org.pi4soa.designer.util");	

	private static final int COMBO_SIZE_LIMIT = 15;

    private Object m_element=null;
    //private org.eclipse.emf.common.notify.Adapter m_adapter=new PropertySourceAdapter();
    private java.util.Vector m_propertyChangeListeners=new java.util.Vector();
    
    /* GPB: Need to find another solution
    public class PropertySourceAdapter implements org.eclipse.emf.common.notify.Adapter {

		public Notifier getTarget() {
			return getElement();
		}

		public boolean isAdapterForType(Object type) {
			return false;
		}

		public void notifyChanged(Notification notification) {
			String propName=null;
			if (notification.getFeature() instanceof EAttribute) {
				propName = ((EAttribute)notification.getFeature()).getName();
			} else if (notification.getFeature() instanceof EReference) {
				propName = ((EReference)notification.getFeature()).getName();
			}
			
			if (propName != null) {
				java.beans.PropertyChangeEvent evt=
						new java.beans.PropertyChangeEvent(PropertySource.this,
								propName, notification.getOldValue(),
									notification.getNewValue());
				
				for (int i=0; i < m_propertyChangeListeners.size(); i++) {
					java.beans.PropertyChangeListener l=
						(java.beans.PropertyChangeListener)
							m_propertyChangeListeners.get(i);
					l.propertyChange(evt);
				}
			}
		}

		public void setTarget(Notifier newTarget) {
		}
    }
    */
}
