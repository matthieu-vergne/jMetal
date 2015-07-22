package org.uma.jmetal.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.ErrorRatio;
import org.uma.jmetal.qualityindicator.impl.SetCoverage;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.naming.impl.DescribedEntitySet;
import org.uma.jmetal.util.point.impl.PointSolution;

public class JMetalLoader {

	private static final String CLASS_FILE_SUFFIX = ".class";
	public static final Logger logger = Logger.getLogger(JMetalLoader.class
			.getName());

	private final Set<File> sourcesToLoad = new HashSet<>();
	private final Set<File> sourcesLoaded = new HashSet<>();
	private final SpecializedClassLoader externalClassLoader;
	private final List<File> libraries = new ArrayList<File>();

	public JMetalLoader() {
		externalClassLoader = new SpecializedClassLoader(
				ClassLoader.getSystemClassLoader());
	}

	public void addClassSource(File source) {
		if (!source.isDirectory()) {
			throw new IllegalArgumentException(source + " is not a directory.");
		} else if (sourcesLoaded.contains(source)) {
			// already loaded, ignore it
		} else {
			sourcesToLoad.add(source);
		}
	}

	private void loadUnloadedSources() {
		Iterator<File> sourceIterator = sourcesToLoad.iterator();
		while (sourceIterator.hasNext()) {
			File source = sourceIterator.next();
			for (File library : source.listFiles()) {
				if (library.isDirectory()) {
					loadDirectorySource(library);
				} else if (library.getName().endsWith(".jar")) {
					try {
						loadJarSource(new JarFile(library));
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				} else {
					throw new IllegalStateException(library
							+ " is not a managed source.");
				}
				libraries.add(library);
			}
			sourcesLoaded.add(source);
			sourceIterator.remove();
		}
	}

	private void loadJarSource(JarFile library) {
		ArrayList<JarEntry> todo = Collections.list(library.entries());
		Collection<Object> classEntries = new ArrayList<Object>();
		while (!todo.isEmpty()) {
			JarEntry entry = todo.remove(0);
			String path = entry.getName();
			if (path.endsWith(CLASS_FILE_SUFFIX)) {
				classEntries.add(entry);
			} else {
				// do nothing
			}
		}

		loadLibrary(library, classEntries);
	}

	private void loadDirectorySource(File library) {
		Collection<Object> classEntries = new ArrayList<Object>();
		ArrayList<File> todo = new ArrayList<File>();
		todo.add(library);
		while (!todo.isEmpty()) {
			File file = todo.remove(0);
			if (file.isDirectory()) {
				todo.addAll(Arrays.asList(file.listFiles()));
			} else if (file.getName().endsWith(CLASS_FILE_SUFFIX)) {
				classEntries.add(file);
			} else {
				// do nothing
			}
		}

		loadLibrary(library, classEntries);
	}

	// TODO refactor to extract File- and JAR-specific casts
	private void loadLibrary(Object library, Collection<Object> classEntries) {
		File libraryFile = null;
		if (library instanceof JarFile) {
			libraryFile = new File(((JarFile) library).getName());
		} else if (library instanceof File) {
			libraryFile = (File) library;
		} else {
			throw new RuntimeException("Unmanaged library class: "
					+ library.getClass());
		}
		String libraryName = libraryFile.getName();
		logger.info("Loading library " + libraryName + "...");

		String separator;
		int start;
		if (library instanceof JarFile) {
			separator = "/";
			start = 0;
		} else if (library instanceof File) {
			separator = File.separator;
			start = ((File) library).getPath().length() + 1; // +1 for '/'
		} else {
			throw new IllegalArgumentException(libraryName
					+ " is not a managed library");
		}

		for (Object entry : classEntries) {
			String path;
			if (entry instanceof JarEntry) {
				path = ((JarEntry) entry).getName();
			} else if (entry instanceof File) {
				path = ((File) entry).getPath();
			} else {
				throw new IllegalArgumentException(entry
						+ " is not a managed entry");
			}
			int end = path.length() - CLASS_FILE_SUFFIX.length();
			path = path.substring(start, end);
			path = path.replaceAll(separator, ".");
			logger.fine("Loading class " + path + "...");
			externalClassLoader.loadClass(library, path);
		}
	}

	public DescribedEntitySet<QualityIndicator<?, ?>> getAvailableQualityIndicators(
			Front referenceFront) {
		Collection<Class<QualityIndicator<?, ?>>> classes = getInstantiableClasses(QualityIndicator.class);
		DescribedEntitySet<QualityIndicator<?, ?>> instances = new DescribedEntitySet<>();
		for (Class<QualityIndicator<?, ?>> clazz : classes) {
			try {
				instances.add(clazz.getConstructor(Front.class).newInstance(
						referenceFront));
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				try {
					instances.add(clazz.newInstance());
				} catch (InstantiationException | IllegalAccessException e1) {
					e.printStackTrace();
				}
			}
		}
		return instances;
	}

	private <T> Collection<Class<T>> getDefaultInstantiableClasses(
			Class<?> clazz) {
		logger.info("Searching for default classes of " + clazz + "...");

		URL root = clazz.getProtectionDomain().getCodeSource().getLocation();
		logger.info("Identified root: " + root);

		Collection<Class<?>> candidates = new LinkedList<Class<?>>();
		if (root.getFile().endsWith("/")) {
			/*
			 * Case where the classes are in a folder on the file system,
			 * typically when jMetal is run through an IDE.
			 */

			LinkedList<File> remaining = new LinkedList<>();
			try {
				remaining.add(new File(URLDecoder.decode(root.getFile(),
						"UTF-8")));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			while (!remaining.isEmpty()) {
				File file = remaining.removeFirst();
				if (file.isDirectory()) {
					remaining.addAll(Arrays.asList(file
							.listFiles(new FilenameFilter() {
								public boolean accept(File dir, String name) {
									return new File(dir, name).isDirectory()
											|| name.endsWith(".class");
								}
							})));
				} else {
					String className = file.getAbsolutePath()
							.substring(root.getFile().length())
							.replace('/', '.').replaceAll(".class$", "");
					try {
						candidates.add(Class.forName(className));
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} else if (root.getFile().endsWith(".jar")) {
			/*
			 * Case where the classes are in a JAR, typically when jMetal is run
			 * or used as a lib.
			 */

			JarFile jar;
			try {
				jar = new JarFile(root.getFile());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			logger.info("JAR opened.");

			Enumeration<JarEntry> entries = jar.entries();
			try {
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String path = entry.getName();
					logger.fine("Checking entry: " + path);

					if (path.endsWith(CLASS_FILE_SUFFIX)) {
						String className = path.replace('/', '.').replaceAll(
								".class$", "");
						try {
							candidates.add(Class.forName(className));
						} catch (NoClassDefFoundError e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							throw new RuntimeException(e);
						}
					} else {
						// irrelevant entry
					}
				}
			} finally {
				try {
					jar.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		} else {
			throw new RuntimeException("Unmanaged root: " + root);
		}

		Collection<Class<T>> classes = new LinkedList<Class<T>>();
		for (Class<?> candidate : candidates) {
			logger.fine("Checking " + candidate + "...");
			if (clazz.isAssignableFrom(candidate)
					&& !Modifier.isInterface(candidate.getModifiers())
					&& !Modifier.isAbstract(candidate.getModifiers())
					&& Modifier.isPublic(candidate.getModifiers())) {
				logger.info("Valid class: " + candidate);
				@SuppressWarnings("unchecked")
				Class<T> correctClass = (Class<T>) candidate;
				classes.add(correctClass);
			} else {
				logger.fine("Invalid class: " + candidate);
			}
		}

		return classes;
	}

	// TODO Make Algorithm extends DescribedEntity
	// TODO Returns DescribedEntitySet
	public Collection<Algorithm<?>> getAvailableAlgorithms() {
		Collection<Class<Algorithm<?>>> classes = getInstantiableClasses(Algorithm.class);
		Collection<Algorithm<?>> instances = new LinkedList<>();
		for (Class<Algorithm<?>> clazz : classes) {
			try {
				instances.add(clazz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return instances;
	}

	// TODO Make Problem extends DescribedEntity
	// TODO Returns DescribedEntitySet
	public Collection<Problem<?>> getAvailableProblems() {
		Collection<Class<Problem<?>>> classes = getInstantiableClasses(Problem.class);
		Collection<Problem<?>> instances = new LinkedList<>();
		for (Class<Problem<?>> clazz : classes) {
			try {
				instances.add(clazz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return instances;
	}

	@SuppressWarnings("unchecked")
	private <T> Collection<Class<T>> getInstantiableClasses(
			Class<?> referenceClass) {
		Collection<Class<T>> classes = new LinkedList<>(
				this.<T> getDefaultInstantiableClasses(referenceClass));

		loadUnloadedSources();
		for (Class<?> clazz : externalClassLoader
				.getInstantiableClasses(referenceClass)) {
			classes.add((Class<T>) clazz);
		}
		return classes;
	}

	private static class SpecializedClassLoader extends ClassLoader {
		private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
		private final Map<String, Object> libraries = new HashMap<String, Object>();

		public SpecializedClassLoader(ClassLoader parent) {
			super(parent);
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			return getParent().loadClass(name);
		}

		public Class<?> loadClass(Object library, String name) {
			if (classes.containsKey(name)) {
				if (libraries.get(name).equals(library)) {
					return classes.get(name);
				} else {
					throw new RuntimeException("Impossible to load the class "
							+ name + " from " + library
							+ ", it has been already loaded from "
							+ libraries.get(name));
				}
			} else {
				try {
					URL sourceUrl;
					if (library instanceof File) {
						File directory = (File) library;
						File file = new File(directory, name.replaceAll("\\.",
								File.separator) + CLASS_FILE_SUFFIX);
						sourceUrl = new URL("file:" + file.getAbsolutePath());
					} else if (library instanceof JarFile) {
						JarFile jar = (JarFile) library;
						String path = jar.getName() + "!/"
								+ name.replaceAll("\\.", "/")
								+ CLASS_FILE_SUFFIX;
						sourceUrl = new URL("jar:file:" + path);
					} else {
						throw new IllegalStateException(library
								+ " is not a manageable library.");
					}

					InputStream input = sourceUrl.openConnection()
							.getInputStream();
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int data;
					while ((data = input.read()) != -1) {
						buffer.write(data);
					}
					input.close();

					byte[] classData = buffer.toByteArray();
					Class<?> clazz = defineClass(name, classData, 0,
							classData.length);
					classes.put(name, clazz);
					libraries.put(name, library);

					return clazz;
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		public Set<Class<?>> getInstantiableClasses(Class<?> clazz) {
			Set<Class<?>> compatibleClasses = new HashSet<>();
			for (Class<?> candidate : classes.values()) {
				if (clazz.isAssignableFrom(candidate)
						&& !Modifier.isInterface(candidate.getModifiers())
						&& !Modifier.isAbstract(candidate.getModifiers())) {
					compatibleClasses.add((Class<?>) candidate);
				}
			}
			return compatibleClasses;
		}
	}

	public static void main(String[] args) {
		JMetalLoader loader = new JMetalLoader();
		JMetalLoader.logger.setLevel(Level.OFF);

		System.out.println("Problems" + loader.getAvailableProblems());

		System.out.println("Algorithms" + loader.getAvailableAlgorithms());

		List<Solution<?>> list = new LinkedList<>();
		list.add(new PointSolution(3));
		list.add(new PointSolution(3));
		list.add(new PointSolution(3));
		System.out.println("List: " + list);
		Front referenceFront = new ArrayFront(list);
		System.out.println("Ref: " + referenceFront);
		DescribedEntitySet<QualityIndicator<?, ?>> indicators = loader
				.getAvailableQualityIndicators(referenceFront);
		System.out.println("Indicators:" + indicators);

		SetCoverage setCoverage = indicators.get("SC");
		System.out.println("Set coverage:" + setCoverage.evaluate(list, list));

		Epsilon<List<Solution<?>>> epsilon = indicators.get("EP");
		System.out.println("Epsilon: " + epsilon.evaluate(list));

		ErrorRatio<List<Solution<?>>> errorRatio = indicators.get("ER");
		System.out.println("Error ratio: " + errorRatio.evaluate(list));
	}
}
