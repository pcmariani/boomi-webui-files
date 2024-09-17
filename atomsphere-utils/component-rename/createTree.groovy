import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput;
import com.boomi.execution.ExecutionUtil;

String foldersRawXml = ExecutionUtil.getDynamicProcessProperty("DPP_FoldersXML") ?: "<Folders></Folders>"
def foldersAll = new XmlParser().parseText(foldersRawXml)

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
  InputStream is = dataContext.getStream(i);
  Properties props = dataContext.getProperties(i);

  def components = new XmlParser().parse(is)
  def foldersNeeded = foldersAll.findAll{it.@id in components.collect{it.@folderId}}

  def outData = new StringBuilder("")
  def outputType
  outputType = "text"
  // outputType = "html"

  // if (outputType == "html") {
  //   outData.append("<ul>" + System.lineSeparator())
  // }

  def foldersArrPrev = ['']
  def numComponentsInFolderPrev = 0
  def lev = 0
  def levPrev = 0
  def uls = []
  def lp = 0

  foldersNeeded.sort{it.@fullPath}.each { folder ->

    def foldersArr = folder.@fullPath.split("/") as ArrayList
    def level = foldersArr.size() - 1
    def intersect = foldersArr.intersect(foldersArrPrev)
    def resultFolderPath = intersect.collect{""} + foldersArr - intersect
    lev = intersect.size() -1

    // println "---------------------------------------------------------------------------------"
    // println "foldersArrPrev:   " + foldersArrPrev
    // println "foldersArr:       " + foldersArr
    // println "level:            " + level
    // println "intersect:        " + intersect
    // println "resultFolderPath: " + resultFolderPath

    def l = foldersArr.size()
    def lPrev = foldersArrPrev.size()
    def levelPrev = foldersArrPrev.size() - 1
    def componentsInThisFolder = components.findAll { it.@folderId == folder.@id }

    // outData.append("\nL" + levelPrev + "->L" + level + ": " + componentsInThisFolder.size() + "\n" + System.lineSeparator())
    // outData.append("Lev" + lev + System.lineSeparator())
    // print levPrev + " | " 
    // println lev +1
    // if (lev+1 < levPrev){
    //   // println "LESS"
    //   (0..uls.size()).each { ulIndex ->
    //     if (ulIndex > lev+1) {
    //       uls.pop()
    //     }
    //   }
    // }

    println lp + " : " + lev
    if (lp > lev) { 
      println "   "*(lev+1) + "UL"
    }
    resultFolderPath.eachWithIndex { item, index ->
      if (item) {
        lev++
        uls[lev] = lev
        lp = lev
        // if (lev > levPrev) {
          // println "    "*lev + "UL"
        // }
        // println "    "*lev + lev + ":" + item + uls + ": " + lp
        println "    "*lev + lev + ":" + item + ": " + lp


        // uls.each { ulIndex ->
        //   if (ulIndex > lev) {
        //     uls.pop()
        //   }
        // }

        // if (lev < levPrev) {
        //
        //   outData.append("    "*lev + "LESS" + System.lineSeparator())
        //
        //   (0..uls.size()).each { ulIndex ->
        //     if (ulIndex > lev) {
        //       outData.append("    "*(lev) + "END UL" + System.lineSeparator())
        //       uls.pop()
        //       outData.append("    "*lev + uls + System.lineSeparator())
        //
        //     }
        //   }
        // }
        

        // if (lev != levPrev) {
        //   if (lev > levPrev) {
        //     outData.append("    "*lev + "START UL" + System.lineSeparator())
        //   }
        //   outData.append("    "*lev + "L" + lev + ": " + uls + ": " + levPrev + System.lineSeparator())
        // }
        //
        // if (outputType == "text") {
        //   outData.append("    "*index + item + "/" + System.lineSeparator())
        // }

        // else if (outputType == "html") {
          // if (levelPrev < level && numComponentsInFolderPrev == 0) {
          //   outData.append("<ul>" + System.lineSeparator())
          // }
          // outData.append("    "*(index+1) + "<li>" + item + "/" + System.lineSeparator())
          // if (levelPrev > level) {
          //   outData.append("    "*index + "</li>\n</ul>" + System.lineSeparator())
          // }
        // }
      }
    }


    // if (outputType == "html") {
    //   outData.append("<ul>" + System.lineSeparator())
    // }
    //
    if (componentsInThisFolder.size() > 0) {
      lev++
      // uls[lev] = lev
      lp = lev
      // println "    "*lev + lev + ":COMPONENTS" + uls + ": " + lp
      println "    "*lev + lev + ":COMPONENTS" + ": " + lp
      // outData.append("    "*lev + "L" + lev + ": " + uls + System.lineSeparator())

              outData.append("    "*lev + "START UL" + System.lineSeparator())
    }
    componentsInThisFolder.each { component ->
      def pre = "- "
      // def pre = "----------"
      // switch(component.@type) {
      //   case "process": pre             = " process      "; break
      //   case "script.processing": pre   = " script       "; break
      //   case "profile.xml": pre         = " XML profile  "; break
      //   case "profile.json": pre        = " JSON profile "; break
      //   case "connector-action": pre    = " operation    "; break
      //   case "documentcache": pre       = " doc cache    "; break
      //   case "transform.map": pre       = " map          "; break
      // }
      if (outputType == "text") {
        outData.append("    "*(level+1) + "$pre" + component.@name + System.lineSeparator())
      }
      if (outputType == "html") {
        outData.append("    "*(level+2) + "<li>" + component.@name + "</li>" + System.lineSeparator())
      }
    }
    if (componentsInThisFolder.size() > 0) {
      // uls.pop()
      lp--
    }

    foldersArrPrev = foldersArr
    numComponentsInFolderPrev = componentsInThisFolder.size()
    levPrev = lev-1
  }
  // if (outputType == "html") {
  //   outData.append("</ul>")
  // }
  // println outData.toString()

  dataContext.storeStream(is, props);
}


private String prettyJson(def thing) { return JsonOutput.prettyPrint(JsonOutput.toJson(thing)) }


/* Example:

---------------------------------------------------------------------------------
foldersArrPrev:   []
foldersArr:       [Boomi_PeterMariani, AAPI]
level:            1
intersect:        []
resultFolderPath: [Boomi_PeterMariani, AAPI]
---------------------------------------------------------------------------------
foldersArrPrev:   [Boomi_PeterMariani, AAPI]
foldersArr:       [Boomi_PeterMariani, AAPI, Component]
level:            2
intersect:        [Boomi_PeterMariani, AAPI]
resultFolderPath: [, , Component]
---------------------------------------------------------------------------------
foldersArrPrev:   [Boomi_PeterMariani, AAPI, Component]
foldersArr:       [Boomi_PeterMariani, FWK]
level:            1
intersect:        [Boomi_PeterMariani]
resultFolderPath: [, FWK]
---------------------------------------------------------------------------------
foldersArrPrev:   [Boomi_PeterMariani, FWK]
foldersArr:       [Boomi_PeterMariani, WebUI, Atomsphere Utils, Component Rename]
level:            3
intersect:        [Boomi_PeterMariani]
resultFolderPath: [, WebUI, Atomsphere Utils, Component Rename]
---------------------------------------------------------------------------------
foldersArrPrev:   [Boomi_PeterMariani, WebUI, Atomsphere Utils, Component Rename]
foldersArr:       [Boomi_PeterMariani, WebUI, Atomsphere Utils, QuickDeploy]
level:            3
intersect:        [Boomi_PeterMariani, WebUI, Atomsphere Utils]
resultFolderPath: [, , , QuickDeploy]
---------------------------------------------------------------------------------
foldersArrPrev:   [Boomi_PeterMariani, WebUI, Atomsphere Utils, QuickDeploy]
foldersArr:       [Boomi_PeterMariani, www, gpt]
level:            2
intersect:        [Boomi_PeterMariani]
resultFolderPath: [, www, gpt]
---------------------------------------------------------------------------------
foldersArrPrev:   [Boomi_PeterMariani, www, gpt]
foldersArr:       [Boomi_PeterMariani, www, utils, QuickDeploy]
level:            3
intersect:        [Boomi_PeterMariani, www]
resultFolderPath: [, , utils, QuickDeploy]
---------------------------------------------------------------------------------
foldersArrPrev:   [Boomi_PeterMariani, www, utils, QuickDeploy]
foldersArr:       [Boomi_PeterMariani, www, z_scratch]
level:            2
intersect:        [Boomi_PeterMariani, www]
resultFolderPath: [, , z_scratch]

*/

