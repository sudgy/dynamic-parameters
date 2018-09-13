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

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;

import org.scijava.plugin.Plugin;

@Plugin(type = DParameter.class)
public class ImageParameter extends AbstractDParameter<ImagePlus> {
    public ImageParameter(String label)
    {
        M_label = label;
        M_id_list = WindowManager.getIDList();
        if (M_id_list == null) {
            M_invalid = true;
            set_error("At least one image must be open.");
            return;
        }
        M_options = new String[M_id_list.length];
        for (int i = 0; i < M_id_list.length; ++i) {
            M_options[i] = String.valueOf(i + 1) + ": " + WindowManager.getImage(M_id_list[i]).getTitle();
        }
    }
    @Override
    public ImagePlus get_value() {return WindowManager.getImage(M_current_id);}

    @Override
    public void add_to_dialog(GenericDialog gd)
    {
        gd.addChoice(M_label, M_options, M_options[M_current_index]);
    }
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        M_current_index = gd.getNextChoiceIndex();
        M_current_id = M_id_list[M_current_index];
    }
    @Override public void save_to_prefs(Class<?> c, String name) {}
    @Override public void read_from_prefs(Class<?> c, String name) {}
    @Override public boolean invalid() {return M_invalid;}

    private String M_label;
    private int[] M_id_list;
    private String[] M_options;
    private int M_current_index = 0;
    private int M_current_id = 1;
    private boolean M_invalid = false;
}
