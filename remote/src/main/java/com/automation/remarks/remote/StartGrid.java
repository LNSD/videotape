package com.automation.remarks.remote;

import org.openqa.grid.selenium.GridLauncherV3;

/**
 * Created by Serhii_Pirohov on 10.05.2016.
 */
public class StartGrid {

  public static void main(String[] args) throws Exception {
    String[] hub = {"-port", "4444",
        "-host", "localhost",
        "-role", "hub"};
    GridLauncherV3.main(hub);

    String[] node = {"-port", "5555",
        "-host", "localhost",
        "-role", "node",
        "-hub", "http://localhost:4444/grid/register",
        "-servlets", "com.automation.remarks.remote.node.Video"};
    GridLauncherV3.main(node);
  }
}
