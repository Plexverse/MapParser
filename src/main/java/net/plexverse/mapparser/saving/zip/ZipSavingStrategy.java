package net.plexverse.mapparser.saving.zip;

import lombok.SneakyThrows;
import net.plexverse.mapparser.saving.SavingStrategy;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipSavingStrategy implements SavingStrategy {

    @SneakyThrows
    @Override
    public void save(final File file, final String folder) {
        final String zipName = file.getName() + ".zip";
        final File templatesTarget = new File(folder, zipName);
        templatesTarget.delete();

        final File zippedFile = new File(Bukkit.getWorldContainer(), zipName);
        this.zip(file, zippedFile, "session.lock", "uid.dat");
        FileUtils.moveFile(zippedFile, templatesTarget);
        FileUtils.deleteDirectory(file);
    }

    private void zip(File source, File output, String... ignore) {
        final List<String> fileList = new ArrayList<>();
        this.generateFileList(fileList, source, ignore);
        final byte[] buffer = new byte[1024];
        try (final FileOutputStream fileOutputStream = new FileOutputStream(output);
             final ZipOutputStream zos = new ZipOutputStream(fileOutputStream)
        ) {
            for (final String file : fileList) {
                final ZipEntry entry = new ZipEntry(this.replace(file.replace(source.getCanonicalPath(), "")));
                zos.putNextEntry(entry);
                try (final FileInputStream inputStream = new FileInputStream(file)) {
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void generateFileList(List<String> fileList, File node, String... ignore) {
        for (final String str : ignore) {
            if (str.equalsIgnoreCase(node.getName())) {
                return;
            }
        }

        if (node.isDirectory()) {
            final String[] subNode = node.list();
            assert subNode != null;

            for (final String name : subNode) {
                this.generateFileList(fileList, new File(node, name), ignore);
            }

            return;
        }

        try {
            fileList.add(node.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String replace(String input) {
        return input.replaceFirst(Pattern.quote("\\"), "").replaceFirst(Pattern.quote("/"), "");
    }
}
