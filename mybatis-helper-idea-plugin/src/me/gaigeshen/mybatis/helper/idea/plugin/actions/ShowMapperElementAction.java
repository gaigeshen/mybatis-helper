package me.gaigeshen.mybatis.helper.idea.plugin.actions;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author gaigeshen
 */
public class ShowMapperElementAction extends AnAction {
  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    Project project = event.getProject();
    Module module = event.getData(LangDataKeys.MODULE);
    if (project == null || module == null) {
      return;
    }
    Editor editor = event.getData(PlatformDataKeys.EDITOR);
    FileEditor fileEditor = event.getData(LangDataKeys.FILE_EDITOR);
    if (editor == null || EditorKind.MAIN_EDITOR != editor.getEditorKind()
            || fileEditor == null) {
      return;
    }

    VirtualFile virtualFile = fileEditor.getFile();
    if (virtualFile == null) {
      return;
    }
    if (virtualFile.getFileType() instanceof JavaFileType) {
      handleDaoFile(project, module, editor);
    }
    if (virtualFile.getFileType() instanceof XmlFileType) {
      handleXmlFile(project, module, editor);
    }
  }

  /**
   * Dao file to xml file
   *
   * @param project The project
   * @param module The module
   * @param editor The editor
   */
  private void handleDaoFile(Project project, Module module, Editor editor) {
    String text = editor.getDocument().getText();
    String daoClassName = StringUtils.substringBetween(text, "interface ", " extends Dao<");
    if (daoClassName == null) {
      return;
    }
    String daoPackageName = StringUtils.substringBetween(text, "package ", ";");
    if (daoPackageName == null) {
      daoPackageName = "";
    }

    PsiFile[] mapperFiles = FilenameIndex.getFilesByName(project, daoClassName + ".xml",
            GlobalSearchScope.moduleScope(module));
    for (PsiFile mapperFile : mapperFiles) {
      VirtualFile mapperVirtualFile = mapperFile.getVirtualFile();
      if (StringUtils.endsWith(mapperVirtualFile.getParent().getPath().replaceAll("/", "."), daoPackageName)) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        fileEditorManager.openFile(mapperVirtualFile, true);
        Editor mapperFileEditor = fileEditorManager.getSelectedTextEditor();
        if (mapperFileEditor != null) {
          String mapperFileText = mapperFile.getText();
          String daoSelectedText = editor.getSelectionModel().getSelectedText();
          if (StringUtils.isNotBlank(mapperFileText) && StringUtils.isNotBlank(daoSelectedText)) {
            mapperFileEditor.getCaretModel()
                    .moveToOffset(StringUtils.indexOf(mapperFileText, daoSelectedText));
          }
        }
        break;
      }
    }
  }

  /**
   * Xml file to dao file
   *
   * @param project The project
   * @param module The module
   * @param editor The editor
   */
  private void handleXmlFile(Project project, Module module, Editor editor) {

  }
}
