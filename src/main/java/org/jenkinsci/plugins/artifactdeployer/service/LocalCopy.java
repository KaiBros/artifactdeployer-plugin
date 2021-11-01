/**
 * The MIT License
 * Copyright (c) 2014 Gregory Boissinot and all contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.artifactdeployer.service;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.jenkinsci.plugins.artifactdeployer.ArtifactDeployerException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kai Broszat
 */
public class LocalCopy {

    public List<File> copyAndGetNumbers(FileSet fileSet, boolean flatten, File targetDir) throws ArtifactDeployerException {
        List<File> deployedFiles = new ArrayList<>(fileSet.size());

        File baseDir = fileSet.getDir();
        Iterator<Resource> iter = fileSet.iterator();
        while (iter.hasNext()) {
            FileResource resource = (FileResource) iter.next();
            File source = resource.getFile();
            File dest = computeDestFile(baseDir, targetDir, source, flatten);

            try {
                // original code included empty directories - this code doesn't
                FileUtils.copyFile(source, dest, true, StandardCopyOption.COPY_ATTRIBUTES);
                deployedFiles.add(dest);
            } catch (IOException ioExceptio) {
                throw new ArtifactDeployerException("Error on copying file.", ioExceptio);
            }
        }
        return deployedFiles;
    }

    private File computeDestFile(File baseDir, File targetDir, File sourceFile, boolean flatten) {
        if(flatten) {
            return new File(targetDir, sourceFile.getName());
        } else {
            Path relativePathOfSourceFile = sourceFile.toPath().relativize(baseDir.toPath());
            return new File(targetDir, relativePathOfSourceFile.toString());
        }
    }
}
