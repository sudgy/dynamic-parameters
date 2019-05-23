/* Copyright (C) 2019 Portland State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * For any questions regarding the license, please contact the Free Software
 * Foundation.  For any other questions regarding this program, please contact
 * David Cohoe at dcohoe@pdx.edu.
 */

package edu.pdx.imagej.dynamic_parameters;

import org.scijava.Prioritized;
import org.scijava.plugin.SciJavaPlugin;

/** ParameterPlugin is an interface for Plugins that have a {@link DParameter}.
 * The main reason to use this interface is to allow use with
 * TODO.  See the documentation for that class for more
 * information on how to use it.
 */
public interface ParameterPlugin extends SciJavaPlugin, Prioritized {
    /** Get the DParameter used for this plugin.  The default return value is
     * <code>null</code>, which is taken to mean that there is no parameter.
     *
     * @return A DParameter that should get the required options for this
     *         plugin.
     */
    default DParameter param() {return null;}
}
