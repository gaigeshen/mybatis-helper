package me.gaigeshen.mybatis.helper.idea.plugin;

import com.google.common.collect.Sets;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.impl.scopes.ModulesScope;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.ui.treeStructure.treetable.TreeColumnInfo;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import me.gaigeshen.mybatis.helper.idea.plugin.util.DatabaseUtils;
import me.gaigeshen.mybatis.helper.idea.plugin.util.NameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

/**
 * @author gaigeshen
 */
public class Window implements ToolWindowFactory {

  private static final String ENTITY_CONTENT = "package _package_;\n" +
          "\n" +
          "import lombok.Getter;\n" +
          "import lombok.Setter; \n" +
          "import _baseEntityPackage_.BaseEntity; \n" +
          "\n" +
          "/**\n" +
          " *\n" +
          " * @author mybatis helper\n" +
          " */\n" +
          "@Getter\n" +
          "@Setter\n" +
          "public class _typeName_ extends BaseEntity {\n" +
          "\n" +
          "  _modelFields_\n" +
          "\n" +
          "}\n";
  private static final String DAO_CONTENT = "package _package_;\n" +
          "\n" +
          "import _daoPackage_.Dao;\n" +
          "import _typePackage_._typeName_;\n" +
          "\n" +
          "/**\n" +
          " * \n" +
          " * @author mybatis helper\n" +
          " */\n" +
          "public interface _typeName_Dao extends Dao<_typeName_, _ID_> {\n" +
          "  \n" +
          "  // You can type your methods here\n" +
          "  \n" +
          "}";

  private static final String MAPPER_CONTENT = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
          "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" +
          "\t\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
          "\n" +
          "<mapper>\n" +
          "\n" +
          "\n" +
          "</mapper>";

  private TreeTable treeTable;

  private ColumnInfo[] columnInfos = new ColumnInfo[]{new TreeColumnInfo("Table & Column"),
          new ColumnInfo<TableOrColumnNode, String>("Description") {
            @Nullable @Override
            public String valueOf(TableOrColumnNode node) {
              Object data = node.getData();
              if (data instanceof Table) {
                return ((Table) data).getFieldsDescription();
              }
              if (data instanceof Column) {
                return ((Column) data).getDescription();
              }
              return "";
            }
          }};

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow wnd) {
    ContentManager contentManager = wnd.getContentManager();
    contentManager.addContent(createContent(contentManager));
  }

  private Content createContent(ContentManager manager) {
    TreeTableModel model = new ListTreeTableModelOnColumns(null, columnInfos);

    treeTable = new TreeTable(model);
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    renderer.setPreferredSize(new Dimension(0, 0));
    treeTable.getTableHeader().setVisible(false);
    treeTable.getTableHeader().setDefaultRenderer(renderer);
    JBScrollPane pane = new JBScrollPane(treeTable);

    DefaultActionGroup actionGroup = new DefaultActionGroup(new ConfigureDatabaseOptionAction(), new LoadAction(), new GenerateAction());
    ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("WindowToolbar", actionGroup, true);
    toolbar.setTargetComponent(treeTable);

    SimpleToolWindowPanel windowPanel = new SimpleToolWindowPanel(true, true);
    windowPanel.setContent(pane);
    windowPanel.setToolbar(toolbar.getComponent());

    return manager.getFactory().createContent(windowPanel, null, false);
  }

  private TableOrColumnNode createTableOrColumnNodes(DatabaseOption option) {
    List<String> tableNames;
    try {
      tableNames = DatabaseUtils.tableNames(option.getUrl(), option.getUser(), option.getPassword());
    } catch (SQLException e) {
      throw new IllegalStateException("Could not get table names", e);
    }
    List<TableOrColumnNode> tables = new ArrayList<>(tableNames.size());
    TableOrColumnNode root = new TableOrColumnNode(null, null, tables);

    for (String tableName : tableNames) {
      Map<String, String[]> columnTypes;
      try {
        columnTypes = DatabaseUtils.columnTypes(option.getUrl(), option.getUser(), option.getPassword(), tableName);
      } catch (SQLException e) {
        throw new IllegalStateException("Could not get column types", e);
      }

      List<TableOrColumnNode> columns = new ArrayList<>(columnTypes.size());

      TableOrColumnNode table = new TableOrColumnNode(new Table(tableName, null), root, columns);
      tables.add(table);

      for (Map.Entry<String, String[]> entry : columnTypes.entrySet()) {
        String[] commentAndType = entry.getValue();
        columns.add(new TableOrColumnNode(new Column(tableName, entry.getKey(), commentAndType[0], commentAndType[1]), table, Collections.emptyList()));
      }
    }
    return root;
  }

  private class ConfigureDatabaseOptionAction extends AnAction {
    ConfigureDatabaseOptionAction() {
      super("Configure", "Configure database option", AllIcons.Actions.Edit);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      DatabaseOption option = DatabaseOptionStore.get();
      ConfigureDatabaseOptionDialog dialog = ConfigureDatabaseOptionDialog.createWithValue(option);
      if (dialog.showAndGet()) {
        if (!dialog.isOptionsValid()) {
          Messages.showWarningDialog("Url or user canot be blank", "Warning");
          return;
        }
        DatabaseOptionStore.set(new DatabaseOption(dialog.getUrl(), dialog.getUser(), dialog.getPassword()));
      }
    }
  }

  private class LoadAction extends AnAction {
    LoadAction() {
      super("Load", "Load or reload tables", AllIcons.Actions.Refresh);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
      treeTable.clearSelection();
      DatabaseOption option = DatabaseOptionStore.get();
      if (!option.isValid()) {
        Messages.showWarningDialog("Please configure database option", "Warning");
        return;
      }
      treeTable.setModel(new ListTreeTableModelOnColumns(createTableOrColumnNodes(option), columnInfos));
      treeTable.setRootVisible(false);
    }
  }

  private class GenerateAction extends AnAction {
    GenerateAction() {
      super("Generate", "Show dialog for generate files", AllIcons.Actions.GroupByFile);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
      int selectedRow = treeTable.getSelectedRow();
      if (selectedRow > -1) {
        TableOrColumnNode node = (TableOrColumnNode) treeTable.getModel().getValueAt(selectedRow, 0);
        Object data = node.getData();
        if (data instanceof Table) {
          List<Column> columns = new ArrayList<>();
          Enumeration children = node.children();
          while (children.hasMoreElements()) {
            Column child = (Column) ((TableOrColumnNode) children.nextElement()).getData();
            columns.add(child);
          }
          ConfigurePackageDialog dialog = new ConfigurePackageDialog();
          if (dialog.showAndGet()) {
            Module module = dialog.getModule();
            PsiPackage entityPackage = dialog.getEntityPackage();
            PsiPackage daoPackage = dialog.getDaoPackage();
            VirtualFile mapperDirectory = dialog.getMapperDirectory();
            String tableNameCamel = NameUtils.underlineToCamel(((Table) data).getName());
            String typeName = StringUtils.upperCase(StringUtils.left(tableNameCamel, 1)) + tableNameCamel.substring(1);
            generateFiles(module, entityPackage, daoPackage, mapperDirectory, typeName, columns);
          }
          return;
        }
      }
      Messages.showWarningDialog("No selected database table", "Warning");
    }
  }

  /**
   * Generate files
   *
   * @param module The module
   * @param entityPackage The entity package
   * @param daoPackage The dao package
   * @param mapperDirectory The mapper xml file directory
   * @param typeName The entity type name, simple name
   * @param columns The columns data
   */
  private void generateFiles(Module module, PsiPackage entityPackage, PsiPackage daoPackage, VirtualFile mapperDirectory, String typeName, List<Column> columns) {
    generateEntityClass(module, entityPackage, typeName, columns);
    generateDaoClass(module, daoPackage, typeName);
    generateMapper(module, daoPackage, mapperDirectory, typeName);
  }

  /**
   * Generate entity class
   *
   * @param module The module
   * @param entityPackage The eneity package
   * @param typeName The entity type name, simple name
   * @param columns Columns data
   */
  private void generateEntityClass(Module module, PsiPackage entityPackage, String typeName, List<Column> columns) {
    if (entityPackage == null || !entityPackage.isValid()) {
      Messages.showWarningDialog("Entity package invalid", "Warning");
      return;
    }
    if (isFileExists(module, typeName + ".java")) {
      return;
    }
    // Search baseEntity class
    PsiFile[] psiFiles = FilenameIndex.getFilesByName(module.getProject(), "BaseEntity.java",
            new ModulesScope(Sets.newHashSet(module), module.getProject()));
    if (psiFiles.length == 0) {
      Messages.showWarningDialog("Could not found BaseEntity.java", "Warning");
      return;
    }
    String baseEntityPackageName = ((PsiJavaFile) psiFiles[0]).getPackageName();
    String content = ENTITY_CONTENT
            .replaceAll("_package_", entityPackage.getQualifiedName())
            .replaceAll("_baseEntityPackage_", baseEntityPackageName)
            .replaceAll("_typeName_", typeName);
    // Search entity package name
    PsiDirectory entityPackageDirectory = PackageUtil.findPossiblePackageDirectoryInModule(module, entityPackage.getQualifiedName());
    if (entityPackageDirectory == null) {
      Messages.showWarningDialog("The entity package is missing or invalid", "Warning");
      return;
    }
    // Replace fields
    StringBuilder fields = new StringBuilder();
    columns.forEach(col -> {
      if (!col.getColumnName().equals("id")) {
        if (fields.length() != 0) { fields.append("\n  "); }
        fields.append("private ")
              .append(col.getJavaType()).append(" ")
              .append(col.getPropertyName())
              .append("; //")
              .append(col.getDescription());
      }
    });
    content = content.replaceAll("_modelFields_", fields.toString());
    // Create entity class file
    PsiFile entity = PsiFileFactory.getInstance(module.getProject()).createFileFromText(
            typeName + ".java", StdFileTypes.JAVA, content);
    WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {
      entityPackageDirectory.add(entity);
    });
  }

  /**
   * Generate dao class
   *
   * @param module The module
   * @param daoPackage The dao package
   * @param typeName The entity type name. simple name
   */
  private void generateDaoClass(Module module, PsiPackage daoPackage, String typeName) {
    if (daoPackage == null || !daoPackage.isValid()) {
      Messages.showWarningDialog("Dao package invalid", "Warning");
      return;
    }
    if (isFileExists(module, typeName + "Dao.java")) {
      return;
    }
    // Search Dao.java interface
    PsiFile[] psiFiles = FilenameIndex.getFilesByName(module.getProject(), "Dao.java",
            new ModulesScope(Sets.newHashSet(module), module.getProject()));
    if (psiFiles.length == 0) {
      Messages.showWarningDialog("Could not found Dao.java", "Warning");
      return;
    }
    // Search entity class
    PsiJavaFile baseDao = (PsiJavaFile) psiFiles[0];
    psiFiles = FilenameIndex.getFilesByName(module.getProject(), typeName + ".java",
            new ModulesScope(Sets.newHashSet(module), module.getProject()));
    if (psiFiles.length == 0) {
      Messages.showWarningDialog("Could not found " + typeName + ".java", "Warning");
      return;
    }
    // Search dao package
    PsiDirectory daoPackageDirectory = PackageUtil.findPossiblePackageDirectoryInModule(module, daoPackage.getQualifiedName());
    if (daoPackageDirectory == null) {
      Messages.showWarningDialog("The dao package is missing or invalid", "Warning");
      return;
    }
    PsiJavaFile type = (PsiJavaFile) psiFiles[0];
    String content = DAO_CONTENT
            .replaceAll("_package_", daoPackage.getQualifiedName())
            .replaceAll("_typeName_", typeName)
            .replaceAll("_typePackage_", type.getPackageName())
            .replaceAll("_daoPackage_", baseDao.getPackageName());
    // Create dao class
    PsiFile dao = PsiFileFactory.getInstance(module.getProject()).createFileFromText(typeName + "Dao.java", StdFileTypes.JAVA, content);
    WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {
      daoPackageDirectory.add(dao);
    });
  }

  /**
   * Generate mapper xml file
   *
   * @param module The module
   * @param daoPackage Dao package
   * @param mapperDirectory Mapper xml file directory
   * @param typeName Entity name, simple name
   */
  private void generateMapper(Module module, PsiPackage daoPackage, VirtualFile mapperDirectory, String typeName) {
    if (!mapperDirectory.exists()) {
      Messages.showWarningDialog("The mapper directory is missing or invalid", "Warning");
      return;
    }
    if (isFileExists(module, typeName + "Dao.xml")) {
      return;
    }

    // Create mapper xml file
    PsiFile daoXmlFile = PsiFileFactory.getInstance(daoPackage.getProject()).createFileFromText(
            typeName + "Dao.xml", StdFileTypes.XML, MAPPER_CONTENT);

    WriteAction.compute(() -> {
      boolean success = true;
      try {
        VirtualFile file = mapperDirectory.createChildData(daoXmlFile, typeName + "Dao.xml");
        file.setBinaryContent(MAPPER_CONTENT.getBytes(Charset.forName("utf-8")));
      } catch (IOException e) {
        success = false;
      }
      return success;
    });
  }

  /**
   * 检查文件是否存在
   *
   * @param module 模块
   * @param fileName 文件名称
   * @return 返回该文件是否存在
   */
  private boolean isFileExists(Module module, String fileName) {
    PsiFile[] psiFiles = FilenameIndex.getFilesByName(module.getProject(), fileName,
            new ModulesScope(Sets.newHashSet(module), module.getProject()));
    return psiFiles.length > 0;
  }

}
