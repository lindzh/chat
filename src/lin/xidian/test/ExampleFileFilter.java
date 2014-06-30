package lin.xidian.test;

import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.filechooser.*;

// FileFilter是一个抽象类，因此构造ExampleFileFilter类来实现它
// 该类和JFileChooser类结合使用可以在打开文件时只显示那些符合条件的文件
public class ExampleFileFilter extends FileFilter {
  private Hashtable filters = null;
  private String description = null;
  private String fullDescription = null;

  //构造函数
  public ExampleFileFilter() {
    this.filters = new Hashtable();
  }

  // 为文件过滤器填加文件扩展名（在扩展名前不需要加“.”）
  public void addExtension(String extension) {
    if(filters == null) {
      filters = new Hashtable();                       // 构造一个哈希表
    }
    filters.put(extension.toLowerCase(), this);        // 向哈希表中增加一个键值对
    fullDescription = null;
  }

  // 设置文件过滤器的一般性描述
  public void setDescription(String description) {
    this.description = description;
    fullDescription = null;
  }

  // 得到文件的扩展名
  public String getExtension(File f) {
    if(f != null) {
      String filename = f.getName();
      int i = filename.lastIndexOf('.');                   // 得到文件名中“.”的位置
      if(i > 0 && i < filename.length() - 1) {
        return filename.substring(i+1).toLowerCase();      // 取“.”之后的子字符串，即为扩展名
      }
    }
    return null;
  }


  /**********************************************************
   *  以下两个函数实现了 FileFilter 类的两个抽象函数
   **********************************************************/

  // 判断一个文件是否应被接受（true : 接受 , false ： 不接受）
  public boolean accept(File f) {
    if(f != null) {
      if(f.isDirectory()) {          // 目录应接受
        return true;
      }
      String extension = getExtension(f);
      if(extension != null && filters.get(getExtension(f)) != null) {    // 扩展名符合设定范围的文件应接受
        return true;
      };
    }
    return false;
  }

  // 得到文件过滤器的完整描述（包括一般性描述和扩展名）
  public String getDescription() {
    if(fullDescription == null) {
      fullDescription = (description == null) ? "(" : (description + " (");

      Enumeration extensions = filters.keys();                          // 得到哈希表的全部键（扩展名）
      if(extensions != null) {
        fullDescription += "*." + (String) extensions.nextElement();    // 加上第一个扩展名
        while (extensions.hasMoreElements()) {                          // 加上后面的扩展名
          fullDescription += ", *." + (String) extensions.nextElement();
        }
      }
      fullDescription += ")";
    }

    return fullDescription;
  }

}
