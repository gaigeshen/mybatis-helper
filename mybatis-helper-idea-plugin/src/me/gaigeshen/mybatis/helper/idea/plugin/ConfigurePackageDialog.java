package me.gaigeshen.mybatis.helper.idea.plugin;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiPackage;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Configure packages for entity class, dao class and mapper xml file directory
 *
 * @author gaigeshen
 */
public class ConfigurePackageDialog extends DialogWrapper {

  private List<Column> columns;

  private ComboBox<Column> identityColumnComboBox;
  private ComboBox<Project> projectComboBox;
  private ComboBox<Module> moduleComboBox;
  private JTextField entityPackageField;
  private JTextField daoPackageField;
  private JTextField mapperDirectoryField;

  private JButton entityPackageButton;
  private JButton daoPackageButton;
  private JButton mapperDirectoryButton;

  private Column identityColumn;
  private Project project;
  private Module module;
  private PsiPackage entityPackage;
  private PsiPackage daoPackage;
  private VirtualFile mapperDirectory;

  public ConfigurePackageDialog(List<Column> columns) {
    super(true);
    Validate.notNull(columns, "columns");
    this.columns = columns;
    init();
    setTitle("Configure Packages");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;

    initializeIdColumnsField(panel, gbc);
    initializeProjectsField(panel, gbc);
    initializeModulesField(panel, gbc);
    initializeEntityPackageField(panel, gbc);
    initializeDaoPackageField(panel, gbc);
    initializeMapperDirectoryField(panel, gbc);

    return panel;
  }

  private void initializeIdColumnsField(JComponent container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Identity Column:");
    Column[] columns = new Column[this.columns.size()];
    for (int i = 0; i < this.columns.size(); i++) {
      columns[i] = this.columns.get(i);
    }
    identityColumnComboBox = new ComboBox<>(columns);
    Column column = (Column) identityColumnComboBox.getSelectedItem();
    if (column != null) {
      this.identityColumn = column;
    }
    gbc.gridx = 0;
    gbc.gridy = 0;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 0;
    container.add(identityColumnComboBox, gbc);
    identityColumnComboBox.addItemListener(e -> {
      this.identityColumn = (Column) e.getItem();
    });
  }

  private void initializeProjectsField(JComponent container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Project:");
    projectComboBox = new ComboBox<>();
    Project[] projects = ProjectManager.getInstance().getOpenProjects();
    projectComboBox.setModel(new DefaultComboBoxModel<>(projects));
    Project project = (Project) projectComboBox.getSelectedItem();
    if (project != null) {
      this.project = project;
    }
    gbc.gridx = 0;
    gbc.gridy = 1;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 1;
    container.add(projectComboBox, gbc);
    projectComboBox.addItemListener(e -> {
      this.project = (Project) e.getItem();
      moduleComboBox.removeAllItems();
      moduleComboBox.setModel(new DefaultComboBoxModel<>(ModuleManager.getInstance(this.project).getModules()));
    });
  }

  private void initializeModulesField(JPanel container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Module:");
    moduleComboBox = new ComboBox<>();
    gbc.gridx = 0;
    gbc.gridy = 2;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 2;
    container.add(moduleComboBox, gbc);
    moduleComboBox.addItemListener(e -> {
      module = (Module) e.getItem();
      entityPackageField.setText("");
      daoPackageField.setText("");
      mapperDirectoryField.setText("");
    });
    Project selected = (Project) projectComboBox.getModel().getSelectedItem();
    if (selected != null) {
      moduleComboBox.setModel(new DefaultComboBoxModel<>(ModuleManager.getInstance(selected).getModules()));
      Module module = (Module) moduleComboBox.getSelectedItem();
      if (module != null) {
        this.module = module;
      }
    }
  }

  private void initializeEntityPackageField(JPanel container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Entity Package:");
    entityPackageField = new JTextField();
    entityPackageField.setEditable(false);
    entityPackageButton = new JButton("Browse...");
    entityPackageButton.addActionListener(e -> {
      if (module == null) {
        Messages.showWarningDialog("Please selecte module", "Warning");
        return;
      }
      PackageChooserDialog dialog = new PackageChooserDialog("Choose Entity Package", module);
      if (dialog.showAndGet()) {
        entityPackage = dialog.getSelectedPackage();
        if (entityPackage != null) {
          String packageName = entityPackage.getQualifiedName();
          entityPackageField.setText(packageName.isEmpty() ? "(default)": packageName);
        }
      }
    });
    gbc.gridx = 0;
    gbc.gridy = 3;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 3;
    container.add(entityPackageField, gbc);
    gbc.gridx = 2;
    gbc.gridy = 3;
    container.add(entityPackageButton, gbc);
  }

  private void initializeDaoPackageField(JPanel container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Dao Package:");
    daoPackageField = new JTextField();
    daoPackageField.setEditable(false);
    daoPackageButton = new JButton("Browse...");
    daoPackageButton.addActionListener(e -> {
      if (module == null) {
        Messages.showWarningDialog("Please selecte module", "Warning");
        return;
      }
      PackageChooserDialog dialog = new PackageChooserDialog("Choose Dao Package", module);
      if (dialog.showAndGet()) {
        daoPackage = dialog.getSelectedPackage();
        if (daoPackage != null) {
          String packageName = daoPackage.getQualifiedName();
          daoPackageField.setText(packageName.isEmpty() ? "(default)": packageName);
        }
      }
    });
    gbc.gridx = 0;
    gbc.gridy = 4;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 4;
    container.add(daoPackageField, gbc);
    gbc.gridx = 2;
    gbc.gridy = 4;
    container.add(daoPackageButton, gbc);
  }

  private void initializeMapperDirectoryField(JPanel container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Mapper Directory:");
    mapperDirectoryField = new JTextField();
    mapperDirectoryField.setEditable(false);
    mapperDirectoryButton = new JButton("Browse...");
    mapperDirectoryButton.addActionListener(e -> {
      FileChooser.chooseFile(new FileChooserDescriptor(false, true,
              false, false, false, false),
              project, null, vf -> {
        if (vf.isValid()) {
          mapperDirectory = vf;
          mapperDirectoryField.setText(vf.getPath());
        }
      });
    });
    gbc.gridx = 0;
    gbc.gridy = 5;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 5;
    container.add(mapperDirectoryField, gbc);
    gbc.gridx = 2;
    gbc.gridy = 5;
    container.add(mapperDirectoryButton, gbc);
  }

  public Column getIdentityColumn() {
    return identityColumn;
  }

  public Project getProject() {
    return project;
  }

  public Module getModule() {
    return module;
  }

  public PsiPackage getEntityPackage() {
    return entityPackage;
  }

  public PsiPackage getDaoPackage() {
    return daoPackage;
  }

  public VirtualFile getMapperDirectory() {
    return mapperDirectory;
  }
}
