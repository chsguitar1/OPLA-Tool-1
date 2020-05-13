package br.ufpr.dinf.gres.core.jmetal4.experiments;

import br.ufpr.dinf.gres.architecture.io.OPLAConfigThreadScopeReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Util {

    private static String fileSeparator = System.getProperty("file.separator");

    public static void moveResourceToExperimentFolder(String experimentId) {
        StringBuilder sourcePath = new StringBuilder();
        String dirExportTarget = OPLAConfigThreadScopeReader.getDirectoryToExportModels();
        sourcePath.append(dirExportTarget);
        sourcePath.append(fileSeparator);
        sourcePath.append("resources");

        StringBuilder destinationPath = new StringBuilder();
        destinationPath.append(dirExportTarget);
        destinationPath.append(fileSeparator);
        destinationPath.append(experimentId);
        destinationPath.append(fileSeparator);
        destinationPath.append("resources");

        File source = new File(sourcePath.toString());
        File destination = new File(destinationPath.toString());
        source.renameTo(destination);
    }

    public static void copyFolder(String experimentId, String executionId) throws IOException {
        StringBuilder sourcePath = new StringBuilder();
        String dirExportTarget = OPLAConfigThreadScopeReader.getDirectoryToExportModels();
        sourcePath.append(dirExportTarget);
        sourcePath.append(fileSeparator);
        sourcePath.append("resources");

        StringBuilder destinationPath = new StringBuilder();
        destinationPath.append(dirExportTarget);
        destinationPath.append(fileSeparator);
        destinationPath.append(experimentId);
        destinationPath.append(fileSeparator);
        destinationPath.append(executionId);
        destinationPath.append(fileSeparator);
        destinationPath.append("resources");

        File src = new File(sourcePath.toString());
        File dest = new File(destinationPath.toString());
        FileUtils.copyDirectory(src, dest);
    }

    public static void moveAllFilesToExecutionDirectory(String experiementID, String executionID) {
        String[] exts = {"uml", "di", "notation"};
        @SuppressWarnings("unchecked")
        List<File> listFiles = (List<File>) FileUtils.listFiles(new File(OPLAConfigThreadScopeReader.getDirectoryToExportModels()), exts, false);
        for (File file : listFiles) {
            if (executionID == null) {
                StringBuilder path = new StringBuilder();
                path.append(OPLAConfigThreadScopeReader.getDirectoryToExportModels());
                path.append(experiementID);
                path.append(fileSeparator);
                path.append(file.getName());

                file.renameTo(new File(path.toString()));
            } else {
                StringBuilder path = new StringBuilder();
                path.append(OPLAConfigThreadScopeReader.getDirectoryToExportModels());
                path.append(experiementID);
                path.append(fileSeparator);
                path.append(executionID);
                path.append(fileSeparator);
                path.append(file.getName());
                file.renameTo(new File(path.toString()));
            }
        }
    }

}
