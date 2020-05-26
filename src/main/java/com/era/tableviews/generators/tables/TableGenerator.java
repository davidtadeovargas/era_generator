/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era.tableviews.generators.tables;

import com.era.logger.LoggerUtility;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author PC
 */
public class TableGenerator {
    
    final String classesPath = "../era_views/src/main/java/com/era/views/tables";
    private static TableGenerator TableGenerator;
    
    
    public static TableGenerator getSingleton(){
        if(TableGenerator == null){
            TableGenerator = new TableGenerator();
        }
        return TableGenerator;
    }
    
    public void createClass(final String name) throws Exception {
        
        final String newName = name.substring(0, 1).toUpperCase() + name.substring(1);
        final String finalClassName = newName + "Table.java";
        final String finalPath = classesPath + "/" + finalClassName;
        
        final File File = new File(finalPath);
        
        //First delete if exists
        if(File.exists()){
            LoggerUtility.getSingleton().logInfo(TableGenerator.class, "Table class " + finalPath + " exists");
            File.delete();
            LoggerUtility.getSingleton().logInfo(TableGenerator.class, "Table class " + finalPath + " deleted");
        }
        
        //Create it
        if (File.createNewFile())
        {
            LoggerUtility.getSingleton().logInfo(TableGenerator.class, "Table class " + finalPath + " created succesfully");
        } else {
            LoggerUtility.getSingleton().logInfo(TableGenerator.class, "Table class " + finalPath + " not created");
        }

        try ( //Write the class
            FileWriter writer = new FileWriter(File)) {
            final String content = this.getContentOfClas(newName);
            writer.write(content);
        }        
    }
    
    private String getContentOfClas(final String className){
        
        final String tableClass = className + "Table";
        final String tableModel = className + "TableModel";
        
        final String classs =
                "package com.era.views.tables;\n\n" +
                "import com.era.models." + className + ";\n" +
                "import com.era.repositories.RepositoryFactory;\n" +
                "import com.era.views.tables.tablemodels." + tableModel + ";\n" +
                "import java.util.List;\n\n" +
                "public class " + tableClass + " extends BaseJTable {\n\n" +
                "   public " + tableClass + "(){\n" +
                "       super();\n" +
                "    }\n\n" +
                "   @Override\n" +
                "   public void initTable(List<?> items) {\n" +
                "       final " + tableModel + " " + tableModel + " = new " + tableModel + "(items,this.ShowColumns);\n" +
                "        this.setModel(" + tableModel + ");\n" + 
                "   }\n\n" +
                "   @Override\n" +
                "   public List<?> getAllItemsInTable() throws Exception {\n" +
                "       final " + tableModel + " " + tableModel + " = (" + tableModel + ")this.getModel();\n" +
                "       final List<" + className + "> items_ = (List<" + className + ">) " + tableModel + ".getItems();\n" +
                "       return items_;\n" + 
                "   }\n\n" +
                "   @Override\n" +
                "   public void loadAllItemsInTable() throws Exception {\n" +
                "       final List<" + className + "> items_ = (List<" + className + ">) RepositoryFactory.getInstance().get" + className + "sRepository().getAll();\n" +
                "       final " + className + "TableModel " + className + "TableModel = new " + className + "TableModel(items_,this.ShowColumns);\n" +
                "       this.setModel(" + className + "TableModel);\n" +
                "   }\n\n" +
                "   @Override\n" +
                "   public void deleteObjectInTable(Object Model) throws Exception{\n" +
                "       if(IDeleteObjectInTable != null){\n" +
                "            IDeleteObjectInTable.onPrevDelete(Model);\n" +
                "        }\n\n" +
                "       RepositoryFactory.getInstance().get" + className + "sRepository().delete(Model);\n\n" + 
                "       if(IDeleteObjectInTable != null){\n" +
                "            IDeleteObjectInTable.onPostDelete(Model);\n" +
                "        }\n" +
                "   }\n\n" +
                "   @Override\n" +
                "   public void insertNewObjectToTable(Object Model) throws Exception {\n" +
                "       if(IInsertNewObjectToTable != null){\n" +
                "            IInsertNewObjectToTable.onPrevInsert(Model);\n" +
                "        }\n\n" + 
                "       final " + className + " " + className + " = (" + className + ")Model;\n\n" +
                "       RepositoryFactory.getInstance().get" + className + "sRepository().save(Model);\n\n" +
                "       if(IInsertNewObjectToTable != null){\n" +
                "            IInsertNewObjectToTable.onPostInsert(Model);\n" +
                "        }\n" + 
                "   }\n\n" +                
                "   @Override\n" +
                "   public void deleteAllObjectsInTable() throws Exception {\n" +
                "       if(this.IDeleteAllItemsInTable != null){\n" +
                "            this.IDeleteAllItemsInTable.onPrevDelete();\n" +
                "        }\n\n" +
                "       RepositoryFactory.getInstance().get" + className + "sRepository().deleteAll();\n\n" + 
                "       if(this.IDeleteAllItemsInTable != null){\n" +
                "            this.IDeleteAllItemsInTable.onPostDelete();\n" +
                "        }\n" +
                "   }\n\n" +
                "}";
        
        return classs;
    }
}
