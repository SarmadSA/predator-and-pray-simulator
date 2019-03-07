import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class DataLogger {
    private StringBuilder builder;
    private FieldStats fieldStats;
    private File file;

    private final Class[] actorClasses = {Mouse.class, Cat.class, Dog.class};

    public DataLogger(String fileName) {
        this.builder = new StringBuilder();
        this.fieldStats = new FieldStats();
        this.createFile(fileName + ".csv");
        this.initFile();
    }

    public void logData(int step, Field field){
        this.fieldStats.reset();
        this.builder.append("" + step);
        for(Class c  : actorClasses){
            int populationCount = fieldStats.getPopulationCount(field, c);
            this.builder.append(",").append(populationCount);
        }
        this.builder.append("\n");
        this.writeToFile(builder);
    }

    private void initFile(){
        builder.append("Step");

        for(Class c : actorClasses){
            String[] longName = c.getName().split("\\.");
            String name = longName[longName.length - 1];
            builder.append(",").append(name);
        }
        builder.append("\n");
        writeToFile(builder);
    }

    private void createFile(String filePath){
        this.file = new File(filePath);
    }

    private void writeToFile(StringBuilder builder){
        try {
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.write(builder.toString());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}