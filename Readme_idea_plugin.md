##  IntelliJ IDEA Plugin

### Introduction

This plugin helps you to generate mybatis files, include entity classes, dao classes and mapper xml file.

> Please add mybatis-helper.jar package to your module before using this plugin

### Install plugin

Search "*mybatis helper*" and install it, after idea restarted, the plugin tool window shows.

#### Command buttons

1. Config: Configure database options
2. Load: Load all database tables
3. Generate: Generate mybatis files dialog

![toolwindow](docs/pictures/toolwindow.png)

### Configure database options

![configure-database-options](docs/pictures/configure-database-options.png)

### Generate mybatis files

![configure-packages](docs/pictures/configure-packages.png)

1. Identity Column: Which column is identity of this database table
2. Project: Current project
3. Module: Select module
4. Entity Package: Which package to generate entity class
5. Dao Package: Which package to generate dao class
6. Mapper Directory: Which directory to generate mapper xml file