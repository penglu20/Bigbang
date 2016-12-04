import java.util.regex.Matcher
import java.util.regex.Pattern

def generateNewCount(String javaPath, String newFilePath){
    COUNT_PREFIX="public static final String "
    COUNT_REG=" *?(.*?) *?= *?\"(.*)\" *?;"
    CLICK_PREFIX = "CLICK"
    STATUS_PREFIX = "STATUS"

    def countString=[]
    javaFile= new File(javaPath)
    javaFile.eachLine{
        if (it.trim().startsWith(COUNT_PREFIX)){
            countString.add(it)
//            println it
        }
    }

    def counts=[]

    countString.each{
        String t=it;
        t = t.replace(COUNT_PREFIX,"")
        Pattern pattern= Pattern.compile(COUNT_REG)
        Matcher matcher=pattern.matcher(t)
        if (matcher.find()) {
            String name = matcher.group(1).trim()
            String content = matcher.group(2).trim()
            int type;
            if (name.startsWith(CLICK_PREFIX)) {
                type = 0;
            } else {
                type = 1;
            }
            counts.add(content + "," + name + "," + type)
        }

    }

    newFile=new File(newFilePath)
    def printWriter = newFile.newPrintWriter()
    counts.each{
        printWriter.println(it)
    }


    printWriter.flush()
    printWriter.close()

}

def compare(String oldFilePath,String newFilePath,String resultDir) {
    old = new File(oldFilePath);

    def oldLines = [];
    if (old.exists()) {
        old.eachLine {
            oldLines.add(it)
        }
    }

    newFile=new File(newFilePath)
    def newLines=[]
    newFile.eachLine{
        newLines.add(it)
    }

    def added=[]
    newLines.each{
        if (!oldLines.contains(it)){
            added.add(it)
        }
    }

    addedFile=new File(resultDir,"added.txt")
    def printWriter = addedFile.newPrintWriter()
    added.each{
        printWriter.println(it)
    }
    printWriter.flush()
    printWriter.close()


    def removed=[]
    if (old.exists()) {
        old.eachLine {
            if (!newLines.contains(it)) {
                removed.add(it)
            }
        }
    }

    removedFile=new File(resultDir,"removed.txt")
    printWriter = removedFile.newPrintWriter()
    removed.each{
        printWriter.println(it)
    }
    printWriter.flush()
    printWriter.close()



//    added.each{
//        println it
//    }
//
//    println "....................................................................................."
//    removed.each{
//        println it
//    }
}

generateNewCount("../app/src/main/java/com/forfan/bigbang/util/UrlCountUtil.java","../count_id_1.7.1.txt");
compare("../count_id_1.7.0.txt","../count_id_1.7.1.txt","../")