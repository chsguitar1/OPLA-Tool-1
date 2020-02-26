package br.ufpr.dinf.gres.domain.view.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author elf
 */
public class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class);

    public static String extractSolutionIdFromSolutionFileName(String fileName) {
        return fileName.substring(fileName.indexOf("-") + 1, fileName.length());
    }

    public static String capitalize(String word) {
        return WordUtils.capitalize(word);
    }

    public static void copy(String source, String target) throws Exception {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(source);
            try (FileOutputStream out = new FileOutputStream(target)) {
                byte[] buffer = new byte[1024];
                int len = in.read(buffer);
                while (len != -1) {
                    out.write(buffer, 0, len);
                    len = in.read(buffer);
                }
            }
            LOGGER.info(String.format("File copy from %s to %s", source, target));
        } catch (Exception e) {
            LOGGER.error(e);
            throw e;
        }
    }

    public static void unTargz(File file, String dest) {
        final TarGZipUnArchiver ua = new TarGZipUnArchiver();
        ConsoleLoggerManager manager = new ConsoleLoggerManager();
        manager.initialize();
        ua.enableLogging(manager.createLogger(1, "a"));
        ua.setSourceFile(file);
        ua.setDestDirectory(new File(dest));
        ua.extract();
    }

    /**
     * This method is used to get the tar file name from the gz file
     * by removing the .gz part from the input file
     *
     * @param inputFile
     * @param outputFolder
     * @return
     */
    private static String getFileName(File inputFile, String outputFolder) {
        return outputFolder + File.separator +
                inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
    }

    public static void createPath(String uriPath) {
        br.ufpr.dinf.gres.architecture.io.FileUtils.createDirectory(Paths.get(uriPath));
    }

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }

        return ext;
    }

    public static boolean selectedSolutionIsNonDominated(String fileName) {
        if (fileName.startsWith("VAR_All")) {
            return true;
        }

        return false;
    }

    public static List<Map.Entry<String, Double>> shortMap(SortedMap<String, Double> resultsEds) {
        List<Map.Entry<String, Double>> edsValues = Lists.newArrayList(resultsEds.entrySet());

        Ordering<Map.Entry<String, Double>> byMapValues = new Ordering<Map.Entry<String, Double>>() {

            @Override
            public int compare(Map.Entry<String, Double> left, Map.Entry<String, Double> right) {
                return left.getValue().compareTo(right.getValue());
            }
        };

        Collections.sort(edsValues, byMapValues);

        return edsValues;
    }

    public static boolean isDigit(String text) {
        try {
            Integer.parseInt(text);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param selectedExperiment
     * @param directoryToExportModels
     */
    public static String getProfilesUsedForSelectedExperiment(String selectedExperiment,
                                                              String directoryToExportModels) {
        try {
            String exts[] = {"uml"};
            StringBuilder names = new StringBuilder();

            StringBuilder path = new StringBuilder();
            path.append(directoryToExportModels);
            path.append(selectedExperiment);
            path.append("/resources/");
            System.out.println(path.toString());
            List<File> files = (List<File>) FileUtils.listFiles(new File(path.toString()), exts, false);

            for (File file : files) {
                names.append(file.getName().toLowerCase());
                names.append(", ");
            }

            return names.deleteCharAt(names.lastIndexOf(",")).toString().trim();
        } catch (Exception e) {
            // I dont care.
            LOGGER.info(e);
        }
        return "-";
    }

    public static void createPathsOplaTool() {
        UserHome.createDefaultOplaPathIfDontExists();

        Path pathApplicationYaml = Paths.get(UserHome.getOplaUserHome()).resolve(Constants.APPLICATION_YAML_NAME);

        if (!Files.exists(pathApplicationYaml)) {
            br.ufpr.dinf.gres.architecture.io.FileUtils.copy(Constants.LOCAL_YAML_PATH, pathApplicationYaml);
        }

        Utils.copyFileGuiSettings();

        UserHome.createProfilesPath();
        UserHome.createTemplatePath();
        UserHome.createOutputPath();
        UserHome.createBinsHVPath();
        UserHome.createTempPath(); // Manipulation dir. apenas para uso intenro

    }

    public static void copyFileGuiSettings() {
        Path target = Paths.get(UserHome.getOplaUserHome()).resolve(Constants.GUI_SETTINGS);
        if (!Files.exists(target)) {
            try {
                URI uri = ClassLoader.getSystemResource(br.ufpr.dinf.gres.architecture.util.Constants.BASE_RESOURCES + Constants.LOCAL_GUI_PATH).toURI();
                br.ufpr.dinf.gres.architecture.io.FileUtils.copy(Paths.get(uri.getSchemeSpecificPart()), target);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createDataBaseIfNotExists() {
        Path pathDb = Paths.get(UserHome.getPathToDb());
        LOGGER.info("Verificando diretorio da base de dados");

        if (!Files.exists(pathDb)) {
            try {
                String pathEmptyDbFile = Constants.PATH_EMPTY_DB + Constants.FILE_SEPARATOR + Constants.EMPTY_DB_NAME;
                URI uri = ClassLoader.getSystemResource(br.ufpr.dinf.gres.architecture.util.Constants.BASE_RESOURCES + pathEmptyDbFile).toURI();
                br.ufpr.dinf.gres.architecture.io.FileUtils.createDirectory(Paths.get(UserHome.getOplaUserHome() + Constants.DB_DIR));
                br.ufpr.dinf.gres.architecture.io.FileUtils.copy(Paths.get(uri.getSchemeSpecificPart()), pathDb);
            } catch (URISyntaxException e) {
                LOGGER.info("Erro ao copiar arquivo de banco de dados", e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.info("Banco de dados já configurado");
        }
        try {
            br.ufpr.dinf.gres.domain.db.Database.setContent(br.ufpr.dinf.gres.core.jmetal4.results.Experiment.all());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Process executeCommand(String command) throws IOException {
         return Runtime.getRuntime().exec(command);
    }

    public static String execCmd(String cmd) throws java.io.IOException {
        Process proc = Runtime.getRuntime().exec(cmd);
        java.io.InputStream is = proc.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String val = "";
        if (s.hasNext()) {
            val = s.next();
        }
        else {
            val = "";
        }
        return val;
    }

    public static Process executePapyrus(String location, String plas) {
        try {
            return Utils.executeCommand(location + " -nosplash -Xverify:none -XX:+AggressiveOpts -XX:PermSize=512m-XX:MaxPermSize=512m -Xms2048m-Xmx2048m -Xmn512m -Xss2m -XX:+UseParallelOldGC -XX:MaxGCPauseMillis=10-XX:+UseG1GC-XX:CompileThreshold=5-XX:MaxGCPauseMillis=10-XX:MaxHeapFreeRatio=70-XX:+CMSIncrementalPacing-XX:+UseFastAccessorMethods-server " +
                    "-vm /home/wmfsystem/App/jdk/bin -clean -clearPersistedState -refresh --launcher.openFile " + plas);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO Ajustar quando refatorar br.ufpr.dinf.gres.core.jmetal4.database
    // public static String generateFileName(String id) {
    // String algorithmName = br.ufpr.dinf.gres.opla.db.Database.getAlgoritmUsedToExperimentId(id);
    // String plaName = br.ufpr.dinf.gres.opla.db.Database.getPlaUsedToExperimentId(id);
    //
    // StringBuilder fileName = new StringBuilder();
    // fileName.append(id);
    // fileName.append("_");
    // fileName.append(plaName);
    // fileName.append("_");
    // fileName.append(algorithmName);
    // fileName.append(".txt");
    //
    // return fileName.toString();
    // }
}