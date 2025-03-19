/** 
 * @file Reader.java
 * @brief Read object file to wireframe
 * @author Alex Johnson
 */

import java.util.Scanner;
import java.util.*;
import java.io.*;

public class Reader {
  private Wireframe wireframe;
  private File file;
  private Scanner sc;

  public Reader(String filename, Wireframe wireframe) {
    this.wireframe = wireframe;
    this.wireframe.setPrintFile(filename);
    this.file = new File(filename);
    try {
      this.sc = new Scanner(this.file);
      this.readFile();
    } catch (FileNotFoundException e) {
    }
  }

  private void readFile() {
    while (this.sc.hasNextLine()) {
      String[] tokens = this.sc.nextLine().split(" ");
      if (tokens.length > 0) {
        double x = 2*Math.random()-1;
        double y = 2*Math.random()-1;
        double z = 2*Math.random()-1;
        if (tokens[0].charAt(tokens[0].length() - 1) == ')') {
          String[] coords = tokens[0].split(",");
          x = Double.parseDouble(coords[0].split("\\(")[1]);
          y = Double.parseDouble(coords[1]);
          z = Double.parseDouble(coords[2].substring(0, coords[2].length() - 1));
          tokens[0] = coords[0].split("\\(")[0];
        }
        boolean lock = false;
        if (tokens[0].charAt(tokens[0].length() - 1) == '*') {
          lock = true;
          tokens[0] = tokens[0].substring(0, tokens[0].length() - 1);
        }
        int p = Integer.parseInt(tokens[0]);
        Point3 point = new Point3(x, y, z);
        point.setLock(lock);
        this.wireframe.addPoint(point);
        for (int i = 1; i < tokens.length; i++) {
          String[] subtok = tokens[i].split(":");
          int q = Integer.parseInt(subtok[0]);
          if (subtok.length > 1) {
            this.wireframe.addConnection(p, q, Double.parseDouble(subtok[1]));
          } else {
            this.wireframe.addConnection(p, q);
          }
        }
      }
    }
  }
}
