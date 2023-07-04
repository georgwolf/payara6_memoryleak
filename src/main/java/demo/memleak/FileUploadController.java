package demo.memleak;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.inject.Model;
import jakarta.servlet.http.Part;
import net.bull.javamelody.internal.model.HeapHistogram;
import net.bull.javamelody.internal.model.VirtualMachine;

@Model
public class FileUploadController{

    public static final Set<String> LEAKING_CLASSES = new HashSet<>();

    static{
        FileUploadController.LEAKING_CLASSES.add("org.apache.catalina.fileupload.DeferredFileOutputStream");
        FileUploadController.LEAKING_CLASSES.add("org.apache.catalina.fileupload.PartHeaders");
        FileUploadController.LEAKING_CLASSES.add("org.apache.catalina.fileupload.PartItem");
        FileUploadController.LEAKING_CLASSES.add("jdk.internal.ref.CleanerImpl$PhantomCleanableRef");
        FileUploadController.LEAKING_CLASSES.add("java.io.File");
    }

    private Part                          uploadedFile;

    private List<HeapHistogram.ClassInfo> leakingClassStats;

    private long                          heapSize;

    public Part getUploadedFile(){
        return this.uploadedFile;
    }

    public void setUploadedFile(final Part uploadedFile){
        this.uploadedFile = uploadedFile;
    }

    public List<HeapHistogram.ClassInfo> getLeakingClassStats(){
        return this.leakingClassStats;
    }

    public void setLeakingClassStats(final List<HeapHistogram.ClassInfo> leakingClassStats){
        this.leakingClassStats = leakingClassStats;
    }

    public void upload(){
        this.updateClassCounts();
    }

    public void init(){
        this.updateClassCounts();
    }

    public void gc(){
        System.gc();
        this.updateClassCounts();
    }

    private void updateClassCounts(){
        try{
            final HeapHistogram heapHistogram = VirtualMachine.createHeapHistogram();
            this.heapSize = heapHistogram.getTotalHeapBytes();
            this.leakingClassStats = heapHistogram.getHeapHistogram()
                    .stream()
                    .filter(classInfo -> FileUploadController.LEAKING_CLASSES.contains(classInfo.getName()))
                    .collect(Collectors.toList());
        }
        catch(final Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * @return heapSize
     */
    public long getHeapSize(){
        return this.heapSize;
    }

    /**
     * Set heapSize
     *
     * @param heapSize new heapSize
     */
    public void setHeapSize(final long heapSize){
        this.heapSize = heapSize;
    }
}
