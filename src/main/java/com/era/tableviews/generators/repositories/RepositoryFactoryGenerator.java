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
            final String content = this.getContentOfClas(className,new File(classesPath).listFiles());
            writer.write(content);
        }        
    }
    
    private String getContentOfClas(final String className, File[] files){
                        
        StringBuilder classs = new StringBuilder("package com.era.repositories;\n\n" +
                "import com.era.logger.LoggerUtility;\n\n" +                
                "public class " + className + " {\n\n" +
                "   private static " + className + " " + className + ";\n\n");
        for (File file : files) {                        
            
            final String fileName = file.getName().replace(".java", "");
            
            if(fileName.compareTo(className)==0 || fileName.compareTo("Repository")==0 || file.isDirectory()){
                continue;
            }
            
            final String simpleName = fileName;
            classs.append(  "   private " + simpleName + " " + simpleName + ";\n");                    
        }
        classs.append(      "\n");
        classs.append(      "   private " + className + "(){\n" +
                            "   }\n\n");
        classs.append(      "   final public static " + className + " getInstance(){\n" +
                            "       LoggerUtility.getSingleton().logInfo(RepositoryFactory.class, \"Hibernate: Getting instance repository manager\");\n" +
                            "       if(RepositoryFactory==null){\n" + 
                            "           RepositoryFactory = new RepositoryFactory();\n" +
                            "       }\n" + 
                            "       return RepositoryFactory;\n" + 
                            "   }\n\n");
        for (File file : files) {
            
            final String fileName = file.getName().replace(".java", "");
            
            if(fileName.compareTo(className)==0 || fileName.compareTo("Repository")==0 || file.isDirectory()){
                continue;
            }
            
            final String simpleName = fileName;
            classs.append(  "   public " + simpleName + " get" + simpleName + "() {\n" +
                            "       if(" + simpleName + "==null){" + simpleName + " = new " + simpleName + "();}return " + simpleName + ";\n" +
                            "   }\n");
        }
        classs.append("}");
        
        return classs.toString();
    }
}
