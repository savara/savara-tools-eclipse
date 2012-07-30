/*
 * Copyright 2005-7 Pi4 Technologies Ltd
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
 * Feb 12, 2007 : Initial version created by gary
 */
package org.savara.tools.scenario.designer.view;

import java.text.MessageFormat;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;

/**
 * This class implements the editable file selection cell editor.
 */
public class FileURLCellEditor extends DialogCellEditor {

	/**
	 * This is the constructor for the editable file
	 * selection cell editor.
	 * 
	 * @param parent The parent
	 */
	public FileURLCellEditor(Composite parent, String value,
						Object selectedObject) {
		super(parent);
		
		m_value = value;
		m_selectedObject = selectedObject;
	}
	
    /**
     * Checks to see if the "deleteable" state (can delete/
     * nothing to delete) has changed and if so fire an
     * enablement changed notification.
     */
    private void checkDeleteable() {
        boolean oldIsDeleteable = isDeleteable;
        isDeleteable = isDeleteEnabled();
        if (oldIsDeleteable != isDeleteable) {
            fireEnablementChanged(DELETE);
        }
    }

    /**
     * Checks to see if the "selectable" state (can select)
     * has changed and if so fire an enablement changed notification.
     */
    private void checkSelectable() {
        boolean oldIsSelectable = isSelectable;
        isSelectable = isSelectAllEnabled();
        if (oldIsSelectable != isSelectable) {
            fireEnablementChanged(SELECT_ALL);
        }
    }

    /**
     * Checks to see if the selection state (selection /
     * no selection) has changed and if so fire an
     * enablement changed notification.
     */
    private void checkSelection() {
        boolean oldIsSelection = isSelection;
        isSelection = m_textField.getSelectionCount() > 0;
        if (oldIsSelection != isSelection) {
            fireEnablementChanged(COPY);
            fireEnablementChanged(CUT);
        }
    }

    /**
	 * Opens a dialog box under the given parent control and returns the
	 * dialog's value when it closes, or <code>null</code> if the dialog
	 * was cancelled or no selection was made in the dialog.
	 * <p>
	 * This framework method must be implemented by concrete subclasses.
	 * It is called when the user has pressed the button and the dialog
	 * box must pop up.
	 * </p>
	 *
	 * @param cellEditorWindow the parent control cell editor's window
	 *   so that a subclass can adjust the dialog box accordingly
	 * @return the selected value, or <code>null</code> if the dialog was 
	 *   cancelled or no selection was made in the dialog
	 */
	protected Object openDialogBox(Control cellEditorWindow) {
		String ret=null;
		
		m_dirty = false;
		
		org.eclipse.swt.widgets.FileDialog dialog=
			new org.eclipse.swt.widgets.FileDialog(getControl().getShell());
		
		dialog.setFileName(m_value);
		
		ret = dialog.open();

		if (ret != null) {
			try {
				org.eclipse.core.runtime.Path path=
					new org.eclipse.core.runtime.Path(ret);
	
				/* TODO: GPB locate resource
				 * 
				 *
				org.eclipse.emf.ecore.EObject m_eobject=null;
				
				if (m_selectedObject instanceof org.eclipse.emf.ecore.EObject) {
					m_eobject = (org.eclipse.emf.ecore.EObject)m_selectedObject;
				}

				if (m_eobject != null && m_eobject.eResource() != null) {
						
					ret = getRelativePath(m_eobject.eResource().getURI(),
								org.eclipse.emf.common.util.URI.createFileURI(path.toPortableString()));
				}
				*/
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		return(ret);
	}
	
	/**
	 * This method identifies the relative path difference between
	 * two files specified by a 'from' and 'to' URI.
	 * 
	 * @param from The 'from' file
	 * @param to The 'to' file
	 * @return The relative path between the two files
	 */
	/*
	protected String getRelativePath(org.eclipse.emf.common.util.URI from,
				org.eclipse.emf.common.util.URI to) {
		StringBuffer ret=new StringBuffer();
		
		// Find common root segment
		int common=0;
		for (; common < from.segmentCount() &&
				common < to.segmentCount() &&
				from.segments()[common].equals(to.segments()[common]);
				common++) {
		}

		for (int i=common; i < from.segmentCount()-1; i++) {
			ret.append("..");
			ret.append('/');
		}
		
		for (int i=common; i < to.segmentCount(); i++) {
			ret.append(to.segments()[i]);
			
			if (i < to.segmentCount()-1) {
				ret.append('/');
			}
		}
		
		return(ret.toString());
	}
	*/
	
	/**
	 * This method creates the contents for the cell editor.
	 * 
	 * @param cell The cell
	 */
    protected Control createContents(Composite cell) {
        m_textField = new Text(cell, getStyle());
        m_textField.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {
                handleDefaultSelection(e);
            }
        });
        m_textField.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201  
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);

                // as a result of processing the above call, clients may have
                // disposed this cell editor
                if ((getControl() == null) || getControl().isDisposed())
                    return;
                checkSelection(); // see explaination below
                checkDeleteable();
                checkSelectable();
            }
        });
        m_textField.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });
        // We really want a selection listener but it is not supported so we
        // use a key listener and a mouse listener to know when selection changes
        // may have occured
        m_textField.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent e) {
                checkSelection();
                checkDeleteable();
                checkSelectable();
            }
        });
        m_textField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                FileURLCellEditor.this.focusLost();
            }
        });
        m_textField.setFont(cell.getFont());
        m_textField.setBackground(cell.getBackground());
        m_textField.setText("");//$NON-NLS-1$
        m_textField.addModifyListener(getModifyListener());
         
        return(m_textField);
    }

    /**
     * This method updates the contents field.
     */
    protected void updateContents(Object value) {
        if (m_textField == null) {
             return;
        }

        String text = "";//$NON-NLS-1$
        if (value != null)
            text = value.toString();
        
        //if (text.equals(m_textField.getText()) == false) {
        	m_textField.setText(text);
        //}
    }

    protected Object doGetValue() {
    	/*
    	if (m_textField == null) {
    		return(m_initialValue);
    	}
        */
        return(m_textField.getText());
    	//return(m_value);
    }

    protected void doSetFocus() {
        if (m_textField != null) {
        	m_textField.selectAll();
        	m_textField.setFocus();
            checkSelection();
            checkDeleteable();
            checkSelectable();
        }
    }

    protected void doSetValue(Object value) {
        Assert.isTrue(m_textField != null && (value instanceof String));
        m_textField.removeModifyListener(getModifyListener());
        m_textField.setText((String) value);
        m_textField.addModifyListener(getModifyListener());
    }

    protected void editOccured(ModifyEvent e) {
        String value = m_textField.getText();
        if (value == null) {
            value = "";
        }
        
        Object typedValue = value;
        boolean oldValidState = isValueValid();
        boolean newValidState = isCorrect(typedValue);
        
        if (typedValue == null && newValidState) {
            Assert.isTrue(false,
                    "Validator isn't limiting the cell editor's type range");//$NON-NLS-1$
        }
        
        if (!newValidState) {
            // try to insert the current value into the error message.
            setErrorMessage(MessageFormat.format(getErrorMessage(),
                    new Object[] { value }));
        }
        
        valueChanged(oldValidState, newValidState);
    }

    /**
     * Since a text editor field is scrollable we don't
     * set a minimumSize.
     */
    public LayoutData getLayoutData() {
        return new LayoutData();
    }

    /**
     * Return the modify listener.
     */
    private ModifyListener getModifyListener() {
        if (modifyListener == null) {
            modifyListener = new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    editOccured(e);
                }
            };
        }
        return modifyListener;
    }

    /**
     * Handles a default selection event from the text control by applying the editor
     * value and deactivating this cell editor.
     * 
     * @param event the selection event
     * 
     * @since 3.0
     */
    protected void handleDefaultSelection(SelectionEvent event) {
        // same with enter-key handling code in keyReleaseOccured(e);
        fireApplyEditorValue();
        deactivate();
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method returns <code>true</code> if 
     * the current selection is not empty.
     */
    public boolean isCopyEnabled() {
        if (m_textField == null || m_textField.isDisposed())
            return false;
        return m_textField.getSelectionCount() > 0;
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method returns <code>true</code> if 
     * the current selection is not empty.
     */
    public boolean isCutEnabled() {
        if (m_textField == null || m_textField.isDisposed())
            return false;
        return m_textField.getSelectionCount() > 0;
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method returns <code>true</code>
     * if there is a selection or if the caret is not positioned 
     * at the end of the text.
     */
    public boolean isDeleteEnabled() {
        if (m_textField == null || m_textField.isDisposed())
            return false;
        return m_textField.getSelectionCount() > 0
                || m_textField.getCaretPosition() < m_textField.getCharCount();
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method always returns <code>true</code>.
     */
    public boolean isPasteEnabled() {
        if (m_textField == null || m_textField.isDisposed())
            return false;
        return true;
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method always returns <code>true</code>.
     */
    public boolean isSaveAllEnabled() {
        if (m_textField == null || m_textField.isDisposed())
            return false;
        return true;
    }

    /**
     * Returns <code>true</code> if this cell editor is
     * able to perform the select all action.
     * <p>
     * This default implementation always returns 
     * <code>false</code>.
     * </p>
     * <p>
     * Subclasses may override
     * </p>
     * @return <code>true</code> if select all is possible,
     *  <code>false</code> otherwise
     */
    public boolean isSelectAllEnabled() {
        if (m_textField == null || m_textField.isDisposed())
            return false;
        return m_textField.getCharCount() > 0;
    }

    /**
     * Processes a key release event that occurred in this cell editor.
     * <p>
     * The <code>TextCellEditor</code> implementation of this framework method 
     * ignores when the RETURN key is pressed since this is handled in 
     * <code>handleDefaultSelection</code>.
     * An exception is made for Ctrl+Enter for multi-line texts, since
     * a default selection event is not sent in this case. 
     * </p>
     *
     * @param keyEvent the key event
     */
    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == '\r') { // Return key
            // Enter is handled in handleDefaultSelection.
            // Do not apply the editor value in response to an Enter key event
            // since this can be received from the IME when the intent is -not-
            // to apply the value.  
            // See bug 39074 [CellEditors] [DBCS] canna input mode fires bogus event from Text Control
            //
            // An exception is made for Ctrl+Enter for multi-line texts, since
            // a default selection event is not sent in this case. 
            if (m_textField != null && !m_textField.isDisposed()
                    && (m_textField.getStyle() & SWT.MULTI) != 0) {
                if ((keyEvent.stateMask & SWT.CTRL) != 0) {
                    super.keyReleaseOccured(keyEvent);
                }
            }
            return;
        }
        super.keyReleaseOccured(keyEvent);
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method copies the
     * current selection to the clipboard. 
     */
    public void performCopy() {
    	m_textField.copy();
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method cuts the
     * current selection to the clipboard. 
     */
    public void performCut() {
    	m_textField.cut();
        checkSelection();
        checkDeleteable();
        checkSelectable();
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method deletes the
     * current selection or, if there is no selection,
     * the character next character from the current position. 
     */
    public void performDelete() {
        if (m_textField.getSelectionCount() > 0)
            // remove the contents of the current selection
        	m_textField.insert(""); //$NON-NLS-1$
        else {
            // remove the next character
            int pos = m_textField.getCaretPosition();
            if (pos < m_textField.getCharCount()) {
            	m_textField.setSelection(pos, pos + 1);
            	m_textField.insert(""); //$NON-NLS-1$
            }
        }
        checkSelection();
        checkDeleteable();
        checkSelectable();
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method pastes the
     * the clipboard contents over the current selection. 
     */
    public void performPaste() {
    	m_textField.paste();
        checkSelection();
        checkDeleteable();
        checkSelectable();
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method selects all of the
     * current text. 
     */
    public void performSelectAll() {
    	m_textField.selectAll();
        checkSelection();
        checkDeleteable();
    }

    /**
	 * This method returns whether a change has occurred.
	 * 
	 * @return Whether a change has occurred
	 */
	public boolean isDirty() {
		return(m_dirty);
	}
	
	//private java.util.List m_values=null;
	//private String m_title=null;
	private String m_value=null;
	private Object m_selectedObject=null;
	private boolean m_dirty=false;
	private Text m_textField;
    private ModifyListener modifyListener;
    private boolean isSelection = false;
    private boolean isDeleteable = false;
    private boolean isSelectable = false;	
}
