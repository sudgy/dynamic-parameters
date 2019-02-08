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

import ij.gui.GenericDialog;

import org.scijava.plugin.Plugin;

@Plugin(type = DParameter.class)
public class BoolParameter extends AbstractDParameter<Boolean> {
    public BoolParameter(String label, Boolean default_value)
    {
        M_label = label;
        M_value = default_value;
    }
    @Override
    public Boolean get_value() {return M_value;}

    @Override
    public void add_to_dialog(GenericDialog gd)
    {
        gd.addCheckbox(M_label, M_value);
    }
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        M_value = gd.getNextBoolean();
    }
    public void save_to_prefs(Class<?> c, String name)
        {prefs().put(c, name, M_value);}
    public void read_from_prefs(Class<?> c, String name)
        {M_value = prefs().getBoolean(c, name, M_value);}

    private String M_label;
    private boolean M_value;
}