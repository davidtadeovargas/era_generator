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
public class TableModelGenerator {
    
    final String classesPath = "../era_views/src/main/java/com/era/views/tables/tablemodels";
    private static TableModelGenerator TableModelGenerator;
    
    
    public static TableModelGenerator getSingleton(){
        if(TableModelGenerator == null){
            TableModelGenerator = new TableModelGenerator();
        }
        return TableModelGenerator;
    }
    
    public void createClass(final String name) throws Exception {
        
        final String newName = name.substring(0, 1).toUpperCase() + name.substring(1);
        final String finalClassName = newName + "TableModel.java";
        final String finalPath = classesPath + "/" + finalClassName;
        
        final File File = new File(finalPath);
        
        //First delete if exists
        if(File.exists()){
            LoggerUtility.getSingleton().logInfo(TableModelGenerator.class, "Table class " + finalPath + " exists");
            File.delete();
            LoggerUtility.getSingleton().logInfo(TableModelGenerator.class, "Table class " + finalPath + " deleted");
        }
        
        //Create it
        if (File.createNewFile())
        {
            LoggerUtility.getSingleton().logInfo(TableModelGenerator.class, "Table class " + finalPath + " created succesfully");
        } else {
            LoggerUtility.getSingleton().logInfo(TableModelGenerator.class, "Table class " + finalPath + " not created");
        }

        try ( //Write the class
            FileWriter writer = new FileWriter(File)) {
            final String content = this.getContentOfClas(newName);
            writer.write(content);
        }        
    }
    
    private String getContentOfClas(final String className){
        
        final String tableClass = className + "TableModel";
        
        final String classs =
                "package com.era.views.tables.tablemodels;\n\n" +
                "import com.era.views.tables.headers.ColumnTable;\n" +
                "import java.util.List;\n" +
                "import com.era.models." + className + ";\n" +
                "import com.era.views.tables.headers.TableHeaderFactory;\n" +                
                "import com.era.views.abstracttablesmodel.BaseAbstractTableModel;\n\n" +
                "public class " + tableClass + "  extends BaseAbstractTableModel {\n\n" +
                "   public " + tableClass + "(List<?> items, final List<ColumnTable> header){\n" +
                "       super(items,header);\n\n" +
                "       this.GetValueAt = (int rowIndex, int columnIndex, String valueColumn, final Object model) -> {\n\n" +
                "           final " + className + " " + className + " = (" + className + ") model;\n" +
                "           return null;\n\n" +
                "       };\n" +
                "    }\n\n" +
                "}";
        
        return classs;
    }
}
