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

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;

import org.scijava.plugin.Plugin;

/** ImageParameter is a {@link DParameter} that gets an ImagePlus from the
 * currently open images.  {@link invalid} will return <code>true</code> if
 * there are no images open.  However, all of the methods in this class can
 * still be called, and they will all be no-ops (and return <code>null</code> if
 * it has to).
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
        int[] id_list = WindowManager.getIDList();
        if (id_list == null) {
            M_invalid = true;
            set_error("At least one image must be open.");
            return;
        }
        M_options = new String[id_list.length];
        M_images = new ImagePlus[id_list.length];
        for (int i = 0; i < id_list.length; ++i) {
            M_images[i]  = WindowManager.getImage(id_list[i]);
            M_options[i] = String.valueOf(i+1) + ": " + M_images[i].getTitle();
        }
    }
    /** Constructor with a custom image list.
     * <p>
     * This constructor takes in all of the possible images rather than using
     * ImageJ1's WindowManager.  It can be useful if you have certain images
     * that you want to pick from, or if you want to do something when
     * WindowManager won't work, like in a testing environment.
     *
     * @param label The label for this parameter to be used on the dialog.
     * @param images A Collection of images that can be selected.
     */
    public ImageParameter(String label, Collection<ImagePlus> images)
    {
        super(label);
        M_label = label;
        if (images.isEmpty()) {
            M_invalid = true;
            set_error("At least one image must be passed to the parameter "
                + DParameter.display_label(label) + ".");
        }
        M_options = new String[images.size()];
        M_images  = new ImagePlus[M_options.length];
        int i = 0;
        for (ImagePlus imp : images) {
            M_images[i]  = imp;
            M_options[i] = imp.getTitle();
            ++i;
        }
    }
    /** Constructor with a custom image list.
     * <p>
     * This is an overload that directly calls
     * {@link ImageParameter(String, Collection)}, provided for convenience.
     *
     * @param label The label for this parameter to be used on the dialog.
     * @param images An array of images that can be selected.
     */
    public ImageParameter(String label, ImagePlus[] images)
    {
        this(label, Arrays.asList(images));
    }
    /** Gets the ImagePlus from this parameter.
     *
     * @return The ImagePlus from this parameter
     */
    @Override
    public ImagePlus get_value()
    {
        if (M_images == null) return null;
        else return M_images[M_current_index];
    }

    /** Adds this parameter to the dialog.
     */
    @Override
    public void add_to_dialog(DPDialog dialog)
    {
        if (M_images == null) return;
        M_supplier = dialog.add_choice_index(M_label,
                                             M_options[M_current_index],
                                             M_options);
    }
    /** Reads this parameter from the dialog.
     */
    @Override
    public void read_from_dialog()
    {
        if (M_images == null) return;
        M_current_index = M_supplier.get();
    }
    /** Saves the name of this image to prefs.
     * <p>
     * This class uses the name of the image to try to remember it.
     *
     * @param c unused
     * @param name unused
     */
    @Override public void save_to_prefs(Class<?> c, String name)
    {
        if (M_images == null) return;
        prefs().put(c, name, M_options[M_current_index]);
    }
    /** Reads the last saved image from prefs and tries to select it again.
     * <p>
     * If the image name is not found, nothing happens.
     *
     * @param c unused
     * @param name unused
     */
    @Override public void read_from_prefs(Class<?> c, String name)
    {
        if (M_images == null) return;
        String image = prefs().get(c, name);
        for (int i = 0; i < M_options.length; ++i) {
            if (M_options[i].equals(image)) {
                M_current_index = i;
            }
        }
    }
    /** Determines if there were no images open during initialization.
     *
     * @return Whether or not there are no images open.
     */
    @Override public boolean invalid() {return M_invalid;}

    private String            M_label;
    private ImagePlus[]       M_images;
    private String[]          M_options;
    private int               M_current_index = 0;
    private boolean           M_invalid = false;
    private Supplier<Integer> M_supplier;
}
