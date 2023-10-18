//  SettingsFactory.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.experiment;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

/**
 * This class represents a factory for Setting object
 */
public class SettingsFactory {
  /**
   * Creates a Settings object
   *
   * @param algorithmName Name of the algorithm
   * @param params        Parameters
   * @return The experiment.settings object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Settings getSettingsObject(String algorithmName, Object[] params)
    throws JMetalException {
    String base = "org.uma.jmetal.experiment.settings." + algorithmName + "Settings";
    try {
      Class<?> settingClass = Class.forName(base);
      Constructor<?>[] constructors = settingClass.getConstructors();
      int i = 0;
      //find the constructor
      while ((i < constructors.length) &&
        (constructors[i].getParameterTypes().length != params.length)) {
        i++;
      }
      // constructors[i] is the selected one constructor
      Settings algorithmSettings = (Settings) constructors[i].newInstance(params);
      return algorithmSettings;
    } catch (Exception e) {
      JMetalLogger.logger.log(Level.SEVERE, "SettingsFactory.getSettingsObject: " +
        "Settings '" + base + "' does not exist. " +
        "Please, check the algorithm name in org.uma.jmetal/metaheuristic", e);
      throw new JMetalException("Exception in " + base + ".getSettingsObject()");
    }
  }
}
