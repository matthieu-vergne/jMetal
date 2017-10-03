package org.uma.jmetal.experiment.util;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;

public class RToolBox {
	public static final String DEFAULT_R_DIRECTORY = "R";

	public void writeBoxplotsScripts(List<String> problemNames, List<String> algorithmNames, String indicatorName,
			String fileName, int numberOfRows, int numberOfColumns, boolean displayNotch) throws IOException {
		PrintStream s = new PrintStream(fileName);
		s.println("postscript(\"" + indicatorName
				+ ".Boxplot.eps\", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)");

		s.println("resultDirectory<-\"../data" + "\"");
		s.println("qIndicator <- function(indicator, problem)");
		s.println("{");

		for (int i = 0; i < algorithmNames.size(); i++) {
			String algorithmName = algorithmNames.get(i);
			s.println("file" + algorithmName + "<-paste(resultDirectory, \"" + algorithmName + "\", sep=\"/\")");
			s.println("file" + algorithmName + "<-paste(file" + algorithmName + ", " + "problem, sep=\"/\")");
			s.println("file" + algorithmName + "<-paste(file" + algorithmName + ", " + "indicator, sep=\"/\")");
			s.println(algorithmName + "<-scan(" + "file" + algorithmName + ")");
			s.println();
		}

		s.print("algs<-c(");
		for (int i = 0; i < algorithmNames.size() - 1; i++) {
			s.print("\"" + algorithmNames.get(i) + "\",");
		} // for
		s.println("\"" + algorithmNames.get(algorithmNames.size() - 1) + "\")");

		s.print("boxplot(");
		for (int i = 0; i < algorithmNames.size(); i++) {
			s.print(algorithmNames.get(i) + ",");
		} // for
		if (displayNotch) {
			s.println("names=algs, notch = TRUE)");
		} else {
			s.println("names=algs, notch = FALSE)");
		}
		s.println("titulo <-paste(indicator, problem, sep=\":\")");
		s.println("title(main=titulo)");

		s.println("}");

		s.println("par(mfrow=c(" + numberOfRows + "," + numberOfColumns + "))");

		s.println("indicator<-\"" + indicatorName + "\"");

		for (String problemName : problemNames) {
			s.println("qIndicator(indicator, \"" + problemName + "\")");
		}

		s.close();
	}

	public void writeWilcoxonTestTables(List<String> problemNames, List<String> algorithmNames,
			GenericIndicator<?> indicator, String dataDirectory, String rFileName, String latexFileName)
			throws IOException {
		PrintStream s = new PrintStream(rFileName);

		printHeaderLatexCommands(s, latexFileName, dataDirectory);
		printTableHeader(indicator.getName(), s, latexFileName);
		printLines(indicator.isTheLowerTheIndicatorValueTheBetter(), s, latexFileName);
		printTableTail(s, latexFileName);
		printEndLatexCommands(s, latexFileName);

		printGenerateMainScript(indicator.getName(), s, latexFileName, problemNames, algorithmNames);

		s.close();
	}

	private void printHeaderLatexCommands(PrintStream s, String latexFileName, String dataDirectory)
			throws IOException {
		s.println("write(\"\", \"" + latexFileName + "\",append=FALSE)");

		s.println("resultDirectory<-\"" + dataDirectory + "\"");
		s.println("latexHeader <- function() {");
		s.println();
		s.println("  write(\"\\\\documentclass{article}\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\title{StandardStudy}\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\usepackage{amssymb}\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\author{A.J.Nebro}\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\begin{document}\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\maketitle\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\section{Tables}\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\\", \"" + latexFileName + "\", append=TRUE)");
		s.println("}");
	}

	private void printTableHeader(String indicatorName, PrintStream s, String latexFileName) throws IOException {
		// Generate function latexTableHeader()
		s.println("latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {");
		s.println("  write(\"\\\\begin{table}\", \"" + latexFileName + "\", append=TRUE)");

		// Latex table caption
		s.println("  write(\"\\\\caption{\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(problem, \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"." + indicatorName + ".}\", \"" + latexFileName + "\", append=TRUE)");

		// Latex table label
		s.println("  write(\"\\\\label{Table:\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(problem, \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"." + indicatorName + ".}\", \"" + latexFileName + "\", append=TRUE)");

		s.println("  write(\"\\\\centering\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\begin{scriptsize}\", \"" + latexFileName + "\", append=TRUE)");
		// s.println(" write(\"\\\\begin{tabular}{" + latexTabularAlignment + "}\", \""
		// + texFile + "\", append=TRUE)");
		s.println("  write(\"\\\\begin{tabular}{\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(tabularString, \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"}\", \"" + latexFileName + "\", append=TRUE)");
		// s.print(latexTableFirstLine);
		s.println("  write(latexTableFirstLine, \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\hline \", \"" + latexFileName + "\", append=TRUE)");
		s.println("}");
		s.println();
	}

	private void printLines(boolean isTheLowerTheIndicatorValueTheBetter, PrintStream s, String latexFileName)
			throws IOException {
		if (isTheLowerTheIndicatorValueTheBetter) {
			s.println("printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { ");
			s.println("  file1<-paste(resultDirectory, algorithm1, sep=\"/\")");
			s.println("  file1<-paste(file1, problem, sep=\"/\")");
			s.println("  file1<-paste(file1, indicator, sep=\"/\")");
			s.println("  data1<-scan(file1)");
			s.println("  file2<-paste(resultDirectory, algorithm2, sep=\"/\")");
			s.println("  file2<-paste(file2, problem, sep=\"/\")");
			s.println("  file2<-paste(file2, indicator, sep=\"/\")");
			s.println("  data2<-scan(file2)");
			s.println("  if (i == j) {");
			s.println("    write(\"-- \", \"" + latexFileName + "\", append=TRUE)");
			s.println("  }");
			s.println("  else if (i < j) {");
			s.println(
					"    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {");
			s.println("      if (median(data1) <= median(data2)) {");
			s.println("        write(\"$\\\\blacktriangle$\", \"" + latexFileName + "\", append=TRUE)");
			s.println("      }");
			s.println("      else {");
			s.println("        write(\"$\\\\triangledown$\", \"" + latexFileName + "\", append=TRUE) ");
			s.println("      }");
			s.println("    }");
			s.println("    else {");
			s.println("      write(\"--\", \"" + latexFileName + "\", append=TRUE) ");
			s.println("    }");
			s.println("  }");
			s.println("  else {");
			s.println("    write(\" \", \"" + latexFileName + "\", append=TRUE)");
			s.println("  }");
			s.println("}");

		} else {
			s.println("printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { ");
			s.println("  file1<-paste(resultDirectory, algorithm1, sep=\"/\")");
			s.println("  file1<-paste(file1, problem, sep=\"/\")");
			s.println("  file1<-paste(file1, indicator, sep=\"/\")");
			s.println("  data1<-scan(file1)");
			s.println("  file2<-paste(resultDirectory, algorithm2, sep=\"/\")");
			s.println("  file2<-paste(file2, problem, sep=\"/\")");
			s.println("  file2<-paste(file2, indicator, sep=\"/\")");
			s.println("  data2<-scan(file2)");
			s.println("  if (i == j) {");
			s.println("    write(\"--\", \"" + latexFileName + "\", append=TRUE)");
			s.println("  }");
			s.println("  else if (i < j) {");
			s.println(
					"    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {");
			s.println("      if (median(data1) >= median(data2)) {");
			s.println("        write(\"$\\\\blacktriangle$\", \"" + latexFileName + "\", append=TRUE)");
			s.println("      }");
			s.println("      else {");
			s.println("        write(\"$\\\\triangledown$\", \"" + latexFileName + "\", append=TRUE) ");
			s.println("      }");
			s.println("    }");
			s.println("    else {");
			s.println("      write(\"$-$\", \"" + latexFileName + "\", append=TRUE) ");
			s.println("    }");
			s.println("  }");
			s.println("  else {");
			s.println("    write(\" \", \"" + latexFileName + "\", append=TRUE)");
			s.println("  }");
			s.println("}");
		}
	}

	private void printTableTail(PrintStream s, String latexFileName) throws IOException {
		// Generate function latexTableTail()
		s.println("latexTableTail <- function() { ");
		s.println("  write(\"\\\\hline\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\end{tabular}\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\end{scriptsize}\", \"" + latexFileName + "\", append=TRUE)");
		s.println("  write(\"\\\\end{table}\", \"" + latexFileName + "\", append=TRUE)");
		s.println("}");
	}

	private void printEndLatexCommands(PrintStream s, String latexFileName) throws IOException {
		s.println("latexTail <- function() { ");
		s.println("  write(\"\\\\end{document}\", \"" + latexFileName + "\", append=TRUE)");
		s.println("}");
		s.println();
	}

	private void printGenerateMainScript(String indicatorName, PrintStream s, String latexFileName,
			List<String> problemNames, List<String> algorithmNames) throws IOException {
		// Start of the R script
		s.println("### START OF SCRIPT ");

		String problemList = "problemList <-c(";
		String algorithmList = "algorithmList <-c(";

		for (int i = 0; i < (problemNames.size() - 1); i++) {
			problemList += "\"" + problemNames.get(i) + "\", ";
		}
		problemList += "\"" + problemNames.get(problemNames.size() - 1) + "\") ";

		for (int i = 0; i < (algorithmNames.size() - 1); i++) {
			algorithmList += "\"" + algorithmNames.get(i) + "\", ";
		}
		algorithmList += "\"" + algorithmNames.get(algorithmNames.size() - 1) + "\") ";

		String latexTabularAlignment = "l";
		for (int i = 1; i < algorithmNames.size(); i++) {
			latexTabularAlignment += "c";
		}

		latexTabularAlignment = "l";
		String latexTableFirstLine = "\\\\hline ";

		for (int i = 1; i < algorithmNames.size(); i++) {
			latexTabularAlignment += "c";
			latexTableFirstLine += " & " + algorithmNames.get(i);
		}
		latexTableFirstLine += "\\\\\\\\ \"";

		String tabularString = "tabularString <-c(" + "\"" + latexTabularAlignment + "\"" + ") ";
		String tableFirstLine = "latexTableFirstLine <-c(" + "\"" + latexTableFirstLine + ") ";

		s.println("# Constants");
		s.println(problemList);
		s.println(algorithmList);
		s.println(tabularString);
		s.println(tableFirstLine);
		s.println("indicator<-\"" + indicatorName + "\"");

		s.println("# Step 1.  Writes the latex header");
		s.println("latexHeader()");

		// Generate full table
		problemList = "";
		for (String problemName : problemNames) {
			problemList += problemName + " ";
		}
		// The tabular environment and the latexTableFirstLine encodings.variable must
		// be redefined
		latexTabularAlignment = "| l | ";
		latexTableFirstLine = "\\\\hline \\\\multicolumn{1}{|c|}{}";
		for (int i = 1; i < algorithmNames.size(); i++) {
			latexTabularAlignment += StringUtils.repeat("p{0.15cm }", problemNames.size());
			latexTableFirstLine += " & \\\\multicolumn{" + problemNames.size() + "}{c|}{" + algorithmNames.get(i) + "}";
			latexTabularAlignment += " | ";
		}
		latexTableFirstLine += " \\\\\\\\";

		tabularString = "tabularString <-c(" + "\"" + latexTabularAlignment + "\"" + ") ";
		latexTableFirstLine = "latexTableFirstLine <-c(" + "\"" + latexTableFirstLine + "\"" + ") ";

		s.println(tabularString);
		s.println();

		s.println(latexTableFirstLine);
		s.println();

		s.println("# Step 3. Problem loop ");
		s.println("latexTableHeader(\"" + problemList + "\", tabularString, latexTableFirstLine)");
		s.println();
		s.println("indx = 0");
		s.println("for (i in algorithmList) {");
		s.println("  if (i != \"" + algorithmNames.get(algorithmNames.size() - 1) + "\") {");
		s.println("    write(i , \"" + latexFileName + "\", append=TRUE)");
		s.println("    write(\" & \", \"" + latexFileName + "\", append=TRUE)" + "\n");
		s.println("    jndx = 0");
		s.println("    for (j in algorithmList) {");
		s.println("      for (problem in problemList) {");
		s.println("        if (jndx != 0) {");
		s.println("          if (i != j) {");
		s.println("            printTableLine(indicator, i, j, indx, jndx, problem)");
		s.println("          }");
		s.println("          else {");
		s.println("            write(\"  \", \"" + latexFileName + "\", append=TRUE)");
		s.println("          } ");
		s.println("          if (problem == \"" + problemNames.get(problemNames.size() - 1) + "\") {");
		s.println("            if (j == \"" + algorithmNames.get(algorithmNames.size() - 1) + "\") {");
		s.println("              write(\" \\\\\\\\ \", \"" + latexFileName + "\", append=TRUE)");
		s.println("            } ");
		s.println("            else {");
		s.println("              write(\" & \", \"" + latexFileName + "\", append=TRUE)");
		s.println("            }");
		s.println("          }");
		s.println("     else {");
		s.println("    write(\"&\", \"" + latexFileName + "\", append=TRUE)");
		s.println("     }");
		s.println("        }");
		s.println("      }");
		s.println("      jndx = jndx + 1");
		s.println("    }");
		s.println("    indx = indx + 1");
		s.println("  }");
		s.println("} # for algorithm");
		s.println();
		s.println("  latexTableTail()");
		s.println();

		// Generate end of file
		s.println("#Step 3. Writes the end of latex file ");
		s.println("latexTail()");
	}
}
