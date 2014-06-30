package lin.xidian.test;

import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.filechooser.*;

// FileFilter��һ�������࣬��˹���ExampleFileFilter����ʵ����
// �����JFileChooser����ʹ�ÿ����ڴ��ļ�ʱֻ��ʾ��Щ�����������ļ�
public class ExampleFileFilter extends FileFilter {
  private Hashtable filters = null;
  private String description = null;
  private String fullDescription = null;

  //���캯��
  public ExampleFileFilter() {
    this.filters = new Hashtable();
  }

  // Ϊ�ļ�����������ļ���չ��������չ��ǰ����Ҫ�ӡ�.����
  public void addExtension(String extension) {
    if(filters == null) {
      filters = new Hashtable();                       // ����һ����ϣ��
    }
    filters.put(extension.toLowerCase(), this);        // ���ϣ��������һ����ֵ��
    fullDescription = null;
  }

  // �����ļ���������һ��������
  public void setDescription(String description) {
    this.description = description;
    fullDescription = null;
  }

  // �õ��ļ�����չ��
  public String getExtension(File f) {
    if(f != null) {
      String filename = f.getName();
      int i = filename.lastIndexOf('.');                   // �õ��ļ����С�.����λ��
      if(i > 0 && i < filename.length() - 1) {
        return filename.substring(i+1).toLowerCase();      // ȡ��.��֮������ַ�������Ϊ��չ��
      }
    }
    return null;
  }


  /**********************************************************
   *  ������������ʵ���� FileFilter �������������
   **********************************************************/

  // �ж�һ���ļ��Ƿ�Ӧ�����ܣ�true : ���� , false �� �����ܣ�
  public boolean accept(File f) {
    if(f != null) {
      if(f.isDirectory()) {          // Ŀ¼Ӧ����
        return true;
      }
      String extension = getExtension(f);
      if(extension != null && filters.get(getExtension(f)) != null) {    // ��չ�������趨��Χ���ļ�Ӧ����
        return true;
      };
    }
    return false;
  }

  // �õ��ļ�����������������������һ������������չ����
  public String getDescription() {
    if(fullDescription == null) {
      fullDescription = (description == null) ? "(" : (description + " (");

      Enumeration extensions = filters.keys();                          // �õ���ϣ���ȫ��������չ����
      if(extensions != null) {
        fullDescription += "*." + (String) extensions.nextElement();    // ���ϵ�һ����չ��
        while (extensions.hasMoreElements()) {                          // ���Ϻ������չ��
          fullDescription += ", *." + (String) extensions.nextElement();
        }
      }
      fullDescription += ")";
    }

    return fullDescription;
  }

}
