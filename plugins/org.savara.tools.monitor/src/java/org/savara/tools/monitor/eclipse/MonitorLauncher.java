/*
 * Copyright 2005-8 Pi4 Technologies Ltd
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
 * 17 Jan, 2008 : Initial version created by gary
 */
package org.savara.tools.monitor.eclipse;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jst.server.core.internal.GenericRuntime;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.pi4soa.common.eclipse.BundleUtil;
import org.savara.tools.monitor.preferences.MonitorPreferences;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;

/**
 * This class is responsible for launching a monitor against
 * a choreography description.
 */
public class MonitorLauncher
			extends AbstractJavaLaunchConfigurationDelegate {

	/**
	 * This is the default constructor.
	 *
	 */
	public MonitorLauncher() {
	}
	
	/**
	 * This method launches the monitor.
	 * 
	 * @param configuration The launch configuration
	 * @param mode The mode (run or debug)
	 * @param launch The launch object
	 * @param monitor The optional progress monitor
	 */
	public void launch(ILaunchConfiguration configuration,
            String mode, ILaunch launch, IProgressMonitor monitor)
						throws CoreException {
		
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		monitor.beginTask(MessageFormat.format("{0}...", new String[]{configuration.getName()}), 3); //$NON-NLS-1$
		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}
		
		monitor.subTask("Verifying launch configuration....");
						
		String mainTypeName = org.savara.tools.monitor.ui.Monitor.class.getName(); 

		IVMInstall vm = verifyVMInstall(configuration);

		IVMRunner runner = vm.getVMRunner(mode);
		if (runner == null) {
			abort("VM runner does not exist",
					null, IJavaLaunchConfigurationConstants.ERR_VM_RUNNER_DOES_NOT_EXIST); //$NON-NLS-1$
		}

		File workingDir = verifyWorkingDirectory(configuration);
		String workingDirName = null;
		if (workingDir != null) {
			workingDirName = workingDir.getAbsolutePath();
		}
		
		// Environment variables
		String[] envp= DebugPlugin.getDefault().getLaunchManager().getEnvironment(configuration);
		
		// Program & VM args
		String filename=configuration.getAttribute(
				MonitorLaunchConfigurationConstants.ATTR_PROJECT_NAME, "")+
				"/"+configuration.getAttribute(
				MonitorLaunchConfigurationConstants.ATTR_CHOREOGRAPHY_DESCRIPTION, "");
		
		String pgmArgs="\""+getPathForResource(filename);
		
		logger.fine("Launching monitor with args: "+pgmArgs);
			
		String vmArgs = getVMArguments(configuration);
		
		vmArgs += " -Djava.naming.factory.initial="+MonitorPreferences.getJNDIFactoryInitial();
		vmArgs += " -Djava.naming.provider.url="+MonitorPreferences.getJNDIProviderURL();
		
		String pkgs=MonitorPreferences.getJNDIFactoryInitial();
		
		if (pkgs != null && pkgs.trim().length() > 0) {
			vmArgs += " -Djava.naming.factory.url.pkgs="+MonitorPreferences.getJNDIFactoryURLPackages();
		}
		
		vmArgs += " -Djava.messaging.factory="+MonitorPreferences.getJMSFactory();
		vmArgs += " -Djava.messaging.destination="+MonitorPreferences.getJMSDestination();
		
		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);
		
		// VM-specific attributes
		Map vmAttributesMap = getVMSpecificAttributesMap(configuration);
		
		// Classpath
		String[] classpath = getClasspath(configuration);
			
		// Create VM config
		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName, classpath);
		runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
		runConfig.setEnvironment(envp);
		runConfig.setVMArguments(execArgs.getVMArgumentsArray());
		runConfig.setWorkingDirectory(workingDirName);
		runConfig.setVMSpecificAttributesMap(vmAttributesMap);

		// Bootpath
		runConfig.setBootClassPath(getBootpath(configuration));
				
		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}		
		
		// stop in main
		prepareStopInMain(configuration);
		
		// done the verification phase
		monitor.worked(1);
		
		// Launch the configuration - 1 unit of work
		runner.run(runConfig, launch, monitor);
		
		IProcess[] processes=launch.getProcesses();
		if (processes.length > 0) {
			processes[0].getStreamsProxy().getOutputStreamMonitor().
						addListener(new IStreamListener() {
				public void streamAppended(String str, IStreamMonitor mon) {
					handleResults(str, false);
				}
			});
			processes[0].getStreamsProxy().getErrorStreamMonitor().
						addListener(new IStreamListener() {
				public void streamAppended(String str, IStreamMonitor mon) {
					handleResults(str, true);
				}
			});
		}
		
		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}	
		
		monitor.done();
	}
	
	/**
	 * This method handles the results produced by the launched
	 * monitor.
	 * 
	 * @param results The results
	 * @param errorStream Whether the results are from the error
	 * 						stream
	 */
	protected void handleResults(String results, boolean errorStream) {
		//System.out.println(results);
	}
	
	/**
	 * This method returns the full path to the resource.
	 * 
	 * @param relativePath The is the resource path beginning at
	 * 					the project
	 * @return The full path
	 */
	protected String getPathForResource(String relativePath) {
		String ret=null;
		
		IFile file=ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(relativePath));
		if (file != null && file.exists()) {
			ret = file.getLocation().toString();
		}
		
		return(ret);
	}
	
	/**
	 * This method derives the classpath required to run the 
	 * Monitor.
	 * 
	 * @param configuration The launch configuation
	 * @return The list of classpath entries
	 */
	/*
	public String[] getClasspath(ILaunchConfiguration configuration) {
		String[] ret=null;
		Vector classpathEntries=new Vector();
					
		// Add classpath entry for current Java project
		try {
			String projname=configuration.getAttribute(
				MonitorLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
		
			IProject project=
				ResourcesPlugin.getWorkspace().getRoot().getProject(projname);

			IJavaProject jproject=JavaCore.create(project); 
			
			// Add output location
			IPath outputLocation=jproject.getOutputLocation();
			
			IFolder folder=
				ResourcesPlugin.getWorkspace().getRoot().getFolder(outputLocation);
			
			String path=folder.getLocation().toString();

			classpathEntries.add(path);
			
			// Add other libraries to the classpath
			IClasspathEntry[] curclspath=jproject.getRawClasspath();
			for (int i=0; curclspath != null &&
						i < curclspath.length; i++) {
				
				if (curclspath[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					IFile file=
						ResourcesPlugin.getWorkspace().
							getRoot().getFile(curclspath[i].getPath());

					if (file.exists()) {
						// Library is within the workspace
						classpathEntries.add(file.getLocation().toString());
					} else {
						// Assume library is external to workspace
						classpathEntries.add(curclspath[i].getPath().toString());
					}
					
				} else if (curclspath[i].getEntryKind() ==
								IClasspathEntry.CPE_CONTAINER) {
					// Container's not currently handled - but
					// problem need to retrieve from project and
					// iterate over container entries
				}
			}
			
		} catch(Exception e) {
			// TODO: report error
		}
		
		String[] cpes=BundleUtil.getClasspathEntries();
		
		for (int i=0; i < cpes.length; i++) {
			classpathEntries.add(cpes[i]);
		}
		
		ret = new String[classpathEntries.size()];
		classpathEntries.copyInto(ret);
		
		return(ret);
	}
	*/
	
	public String[] getClasspath(ILaunchConfiguration configuration) {
		String[] ret=null;
		java.util.Vector<String> classpathEntries=new java.util.Vector<String>();
					
		// Add classpath entry for current Java project
		try {
			String projname=configuration.getAttribute(
					MonitorLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
		
			IProject project=
				ResourcesPlugin.getWorkspace().getRoot().getProject(projname);

			IJavaProject jproject=JavaCore.create(project); 
			
			// Add output location
			IPath outputLocation=jproject.getOutputLocation();
			
			IFolder folder=
				ResourcesPlugin.getWorkspace().getRoot().getFolder(outputLocation);
			
			String path=folder.getLocation().toString();

			classpathEntries.add(path);
			
			// Add other libraries to the classpath
			IClasspathEntry[] curclspath=jproject.getRawClasspath();
			for (int i=0; curclspath != null &&
						i < curclspath.length; i++) {
				
				if (curclspath[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					IFile file=
						ResourcesPlugin.getWorkspace().
							getRoot().getFile(curclspath[i].getPath());

					if (file.exists()) {
						// Library is within the workspace
						classpathEntries.add(file.getLocation().toString());
					} else {
						// Assume library is external to workspace
						classpathEntries.add(curclspath[i].getPath().toString());
					}
					
				} else if (curclspath[i].getEntryKind() ==
								IClasspathEntry.CPE_CONTAINER) {
					// Container's not currently handled - but
					// problem need to retrieve from project and
					// iterate over container entries
				}
			}
			
		} catch(Exception e) {
			// TODO: report error
			//e.printStackTrace();
		}
		
		buildClassPath("org.savara.tools.monitor", classpathEntries);
		buildClassPath("org.savara.activity", classpathEntries);
		buildClassPath("org.pi4soa.service", classpathEntries);
		buildClassPath("org.pi4soa.common", classpathEntries);
		buildClassPath("org.pi4soa.cdl", classpathEntries);
		buildClassPath("org.eclipse.emf.ecore", classpathEntries);
		buildClassPath("org.eclipse.emf.ecore.xmi", classpathEntries);
		buildClassPath("org.eclipse.emf.common", classpathEntries);
		buildClassPath("org.apache.xalan", classpathEntries);
		buildClassPath("org.apache.xml.serializer", classpathEntries);
		
		// Access library paths to include all found jars
		String paths=MonitorPreferences.getLibraryPaths();
		
		java.util.StringTokenizer st=new java.util.StringTokenizer(paths, ":");
		
		while (st.hasMoreTokens()) {
			String libpath=st.nextToken();
			
			java.io.File f=new java.io.File(libpath);
			java.io.File[] childFiles=f.listFiles();
			
			for (int i=0; i < childFiles.length; i++) {
				if (childFiles[i].isFile() && childFiles[i].getName().endsWith(".jar")) {
					classpathEntries.add(childFiles[i].getAbsolutePath());
				}
			}
		}

		// Return entries as an array
		ret = new String[classpathEntries.size()];
		classpathEntries.copyInto(ret);
		
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Scenario Simulation Classpath:");
			for (int i=0; i < ret.length; i++) {
				logger.finest("    ["+i+"] "+ret[i]);
			}
		}
		
		return(ret);
	}
	
	protected void buildClassPath(String bundleId, java.util.List<String> entries) {
		Bundle bundle= Platform.getBundle(bundleId);
		if (bundle != null) {
			java.net.URL installLocation= bundle.getEntry("/");
			java.net.URL local= null;
			try {
				local= Platform.asLocalURL(installLocation);
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
			
			String baseLocation = local.getFile();

			try {
				String requires = (String)bundle.getHeaders().get(Constants.BUNDLE_CLASSPATH);
				ManifestElement[] elements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, requires);
				
				for (int i=0; elements != null && i < elements.length; i++) {
					
					String path=baseLocation+elements[i].getValue();
					
					// Check if path is for a Jar and that the
					// file exists - if not see if a classes
					// directory exists
					if (path.endsWith(".jar")) {
						
						if ((new File(path)).exists() == false) {
							if ((new File(baseLocation+"classes")).exists()) {
								path = baseLocation+"classes";
							}
						}
					}
					
					if (entries.contains(path) == false) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Adding classpath entry '"+
									path+"'");
						}
						entries.add(path);
						
						if (elements[i].getValue().equals(".")) {
							if ((new File(baseLocation+"classes")).exists()) {
								path = baseLocation+"classes";
								
								entries.add(path);
							} else if ((new File(baseLocation+"bin")).exists()) {
								path = baseLocation+"bin";
								
								entries.add(path);
							} else if ((new File(baseLocation+"target/classes")).exists()) {
								path = baseLocation+"target/classes";
								
								entries.add(path);
							}
						}
					}
				}
				
				if (elements == null) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Adding classpath entry '"+
								baseLocation+"'");
					}
					entries.add(baseLocation);
				}
				
				/*
				requires = (String)bundle.getHeaders().get(Constants.REQUIRE_BUNDLE);
			    elements = ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, requires);

				for (int i=0; recursive &&
						elements != null && i < elements.length; i++) {
					buildClasspathEntries(elements[i].getValue(),
							entries, false);
				}
				*/
				
			} catch(Exception e) {
				logger.severe("Failed to construct classpath: "+e);
				e.printStackTrace();
			}
		}
	}

	private static Logger logger = Logger.getLogger("org.savara.tools.monitor.eclipse");
}