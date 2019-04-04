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

import java.util.function.Supplier;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;

import org.scijava.plugin.Plugin;

/** ImageParameter is a {@link DParameter} that gets an ImagePlus from the
 * currently open images.
 */
@Plugin(type = DParameter.class)
public class ImageParameter extends AbstractDParameter<ImagePlus> {
    /** Constructor using a label.
     * <p>
     * If there are no images open, {@link invalid} will return
     * <code>true</code>.
     *
     * @param label The label for this parameter to be used on the dialog.
     */
    public ImageParameter(String label)
    {
        super(label);
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
    /** Gets the ImagePlus from this parameter.
     *
     * @return The ImagePlus from this parameter
     */
    @Override
    public ImagePlus get_value() {return WindowManager.getImage(M_current_id);}

    /** Adds this parameter to the dialog.
     */
    @Override
    public void add_to_dialog(DPDialog dialog)
    {
        M_supplier = dialog.add_choice_index(M_label, M_options[M_current_index], M_options);
    }
    /** Reads this parameter from the dialog.
     */
    @Override
    public void read_from_dialog()
    {
        M_current_index = M_supplier.get();
        M_current_id = M_id_list[M_current_index];
    }
    /** Does nothing.
     * <p>
     * Because images are constantly changing, ImageParameter does not try to
     * save its values between runs.
     *
     * @param c unused
     * @param name unused
     */
    @Override public void save_to_prefs(Class<?> c, String name) {}
    /** Does nothing.
     * <p>
     * Because images are constantly changing, ImageParameter does not try to
     * save its values between runs.
     *
     * @param c unused
     * @param name unused
     */
    @Override public void read_from_prefs(Class<?> c, String name) {}
    /** Determines if there were no images open during initialization.
     *
     * @return Whether or not there are no images open.
     */
    @Override public boolean invalid() {return M_invalid;}

    private String M_label;
    private int[] M_id_list;
    private String[] M_options;
    private int M_current_index = 0;
    private int M_current_id = 1;
    private boolean M_invalid = false;
    private Supplier<Integer> M_supplier;
}
