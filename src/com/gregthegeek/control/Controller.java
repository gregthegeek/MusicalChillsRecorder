package com.gregthegeek.control;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;

public class Controller {
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");
  private final HashMap<String, List<Record>> users = new HashMap<String, List<Record>>();
  private int lastPress;
  public enum SaveFormat {
      CSV,
      TXT;
  }
  
  public void onButtonPress(String user, int songDur) {
      if(lastPress < 0) {
          lastPress = songDur;
      }
  }
  
  public void onButtonRelease(String user, String song, int songDur) {
      addChill(user, song, lastPress, songDur);
      lastPress = -1;
  }
  
  private void addChill(String user, String song, long start, long stop) {
      getRecord(user, song).addChill(new Chill(start, stop));
  }
  
  private Record getRecord(String user, String song) {
	  List<Record> records = getRecords(user);
	  for(Record r : records) {
		  if(r.getSong().equalsIgnoreCase(song)) {
			  return r;			  
		  }
	  }
	  
	  Record r = new Record(song);
	  records.add(r);
	  return r;
  }
  
  private List<Record> getRecords(String user) {
    if(users.containsKey(user)) {
      return users.get(user);
    }
    List<Record> newList = new ArrayList<Record>();
    users.put(user, newList);
    return newList;
  }
  
  public void saveData(SaveFormat format) throws IOException {
      switch(format) {
      case CSV:
          saveCSV();
          break;
      case TXT:
          saveTXT();
          break;
      }
  }
  
  private void saveCSV() throws IOException {
      BufferedWriter output = new BufferedWriter(new FileWriter("data.csv"));
      output.write("User:Song,Start:Stop..." + LINE_SEPARATOR);
      for(Entry<String, List<Record>> e : users.entrySet()) {
          String user = e.getKey();
          for(Record r : e.getValue()) {
              output.write(user + ":" + r.getSong() + ",");
              for(Chill c : r.getChills()) {
                  output.write(c.getStart() + ":" + c.getStop() + ",");
              }
              output.write(LINE_SEPARATOR);
          }
      }
      output.close();
  }
  
  private void saveTXT() throws IOException {
      BufferedWriter output = new BufferedWriter(new FileWriter("data.txt"));
      for(Entry<String, List<Record>> e : users.entrySet()) {
          output.write(e.getKey() + LINE_SEPARATOR); // write username on first line
          for(Record r : e.getValue()) {
              output.write("\t" + r.getSong() + LINE_SEPARATOR); // write song name
              for(Chill c : r.getChills()) {
                  output.write("\t\t" + c.getStart() + "-" + c.getStop() + LINE_SEPARATOR); // write chills
              }
          }
      }
      output.close();
  }
}
