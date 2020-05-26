/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era.tableviews.generators.repositories;

import com.era.logger.LoggerUtility;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 *
 * @author PC
 */
public class RepositoryFactoryGenerator {
    
    final String classesPath = "../era_repositories/src/main/java/com/era/repositories";
    private static RepositoryFactoryGenerator TableGenerator;
    
    
    public static RepositoryFactoryGenerator getSingleton(){
        if(TableGenerator == null){
            TableGenerator = new RepositoryFactoryGenerator();
        }
        return TableGenerator;
    }
    
    public void createClass(List<Class> tables) throws Exception {
        
        final String className = "RepositoryFactory";
        final String fileName = className + ".java";
        final String finalPath = classesPath + "/" + fileName;
        
        final File File = new File(finalPath);
        if(File.exists()){
            LoggerUtility.getSingleton().logInfo(RepositoryFactoryGenerator.class, "The file " + finalPath + " exists and will be deleted");
            File.delete();
            LoggerUtility.getSingleton().logInfo(RepositoryFactoryGenerator.class, "The file " + finalPath + " was deleted");
        }
        
        LoggerUtility.getSingleton().logInfo(RepositoryFactoryGenerator.class, "The file " + finalPath + " is being created");
        File.createNewFile();
        LoggerUtility.getSingleton().logInfo(RepositoryFactoryGenerator.class, "The file " + finalPath + " was created");
            
        try ( //Write the class
            FileWriter writer = new FileWriter(File)) {
            final String content = this.getContentOfClas(className,tables);
            writer.write(content);
        }        
    }
    
    private String getContentOfClas(final String className, List<Class> tables){
        
        StringBuilder classs = new StringBuilder("package com.era.repositories;\n\n" +
                "import com.era.logger.LoggerUtility;\n\n" +                
                "public class " + className + " {\n\n" +
                "   private static " + className + " " + className + ";\n\n");
        for(Class Class_: tables){
            
            final String simpleName = Class_.getSimpleName();
            classs.append(  "   private " + simpleName + "sRepository " + simpleName + "sRepository;\n");                    
        }
        classs.append(      "\n");
        classs.append(      "   private " + className + "(){\n" +
                            "   }\n\n");
        classs.append(      "   final public static RepositoryFactory getInstance(){\n" +
                            "       LoggerUtility.getSingleton().logInfo(RepositoryFactory.class, \"Hibernate: Getting instance repository manager\");\n" +
                            "       if(RepositoryFactory==null){\n" + 
                            "           RepositoryFactory = new RepositoryFactory();\n" +
                            "       }\n" + 
                            "       return RepositoryFactory;\n" + 
                            "   }\n\n");
        for(Class Class_: tables){
            
            final String simpleName = Class_.getSimpleName();
            classs.append(  "   public " + simpleName + "sRepository get" + simpleName + "sRepository() {\n" +
                            "       if(" + simpleName + "sRepository==null){" + simpleName + "sRepository = new " + simpleName + "sRepository();}return " + simpleName + "sRepository;\n" +
                            "   }\n");
        }        
        classs.append("}");
        
        return classs.toString();
    }
}
