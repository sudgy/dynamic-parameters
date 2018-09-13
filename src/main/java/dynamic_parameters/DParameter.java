/* Copyright (C) 2018 Portland State University
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

import ij.gui.GenericDialog;

import org.scijava.Context;
import org.scijava.Contextual;
import net.imagej.ImageJPlugin;

public interface DParameter<T> extends Contextual, ImageJPlugin {
    void initialize();
    void add_to_dialog(GenericDialog gd);
    void read_from_dialog(GenericDialog gd);
    void save_to_prefs(Class<?> c, String name);
    void read_from_prefs(Class<?> c, String name);
    boolean reconstruction_needed();
    void recreate();
    int width();
    String get_error();
    boolean invalid();
    T get_value();
    void set_harvester(Harvester h);
}
