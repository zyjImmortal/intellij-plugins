package com.jetbrains.lang.dart;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesProcessor;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author: Fedor.Korotkov
 */
public class DartMoveTest extends LightPlatformCodeInsightFixtureTestCase {
  @Override
  protected String getBasePath() {
    return FileUtil.toSystemDependentName("/plugins/Dart/testData/move/");
  }


  //Both names are relative to root directory
  private void doTest(String toMove, final String targetDirName) throws Exception {
    myFixture.copyDirectoryToProject(getTestName(true) + "/before", "");
    doTest(new String[]{toMove}, targetDirName);

  }

  //Both names are relative to root directory
  private void doTest(final String[] toMove, final String targetDirName) throws Exception {

    Collection<PsiElement> files = new ArrayList<PsiElement>();
    for (String s : toMove) {
      final VirtualFile child = myFixture.findFileInTempDir(s);
      assertNotNull("Neither class nor file " + s + " not found", child);
      files.add(myFixture.getPsiManager().findFile(child));
    }
    final VirtualFile child1 = myFixture.findFileInTempDir(targetDirName);
    assertNotNull("Target dir " + targetDirName + " not found", child1);
    final PsiDirectory targetDirectory = myFixture.getPsiManager().findDirectory(child1);
    assertNotNull(targetDirectory);

    new MoveFilesOrDirectoriesProcessor(myFixture.getProject(), PsiUtilCore.toPsiElementArray(files), targetDirectory,
                                        false, true, null, null).run();
    FileDocumentManager.getInstance().saveAllDocuments();

    VirtualFile after = LocalFileSystem.getInstance().findFileByPath(getTestDataPath() + getTestName(true) + "/after");
    PlatformTestUtil.assertDirectoriesEqual(after, myFixture.findFileInTempDir(""));
  }

  public void testMoveFile1() throws Exception {
    doTest("Foo.dart", "bar");
  }

  public void testMoveFile2() throws Exception {
    doTest("bar/Foo.dart", "");
  }
}
