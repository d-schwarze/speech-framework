package de.speech.core.annotation.reflection;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MockPackageClassSearcher extends PackageClassSearcher {

    public MockPackageClassSearcher() throws IOException {
        super();

    }

    public List<SpeechClassVisitor> visitClassFilesFromPackages(ClassLoader classLoader, String[] packageNames) throws IOException {
        return super.visitClassFilesFromPackages(classLoader, packageNames);
    }

    protected List<SpeechClassVisitor> visitClassFilesFromLocation(ClassLoader classLoader, String location) throws IOException {
        return super.visitClassFilesFromLocation(classLoader, location);
    }

    protected void visitFile(File file, List<SpeechClassVisitor> visitors) throws IOException {
        super.visitFile(file, visitors);
    }
}
