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

import org.scijava.Context;
import org.scijava.Contextual;
import net.imagej.ImageJPlugin;

/**
 * DParameter is the base interface for all Dynamic Parameters.
 * <p>
 * Upon running a command, the {@link DynamicPreprocessor} finds all
 * <a href="https://javadoc.scijava.org/SciJava/org/scijava/plugin/Parameter.html">Parameters</a>
 * that implement this interface creates a {@link Harvester}, which in turn
 * creates a dialog to populate them.  The way that this is done is through a
 * dialog that should start with the values that were input previously.  At any
 * time, the {@link getValue} method can be used to query the value that the
 * parameter is (currently) holding.
 * <p>
 * Many of these functions are used to signal to the {@link Harvester} holding
 * them, such as {@link visible}, {@link getError}, and {@link getWarning}.
 * <p>
 * It is not suggested to directly implement this interface.
 * {@link AbstractDParameter} should be used instead, as it automates a lot of
 * the easier tasks that Dynamic Parameters do.  Only implement this interface
 * if you want to extend another class, or if you want to do things wildly
 * differently than usual.
 *
 * @author David Cohoe
 */
public interface DParameter<T> extends Contextual, ImageJPlugin {
    /**
     * Performs any extra initialization needed after the Context has been
     * injected.
     * <p>
     * Only use this if you need the context during initialization.  The
     * constructor should be good enough for most purposes.  When the
     * {@link DynamicPreprocessor} locates this parameter, it will inject the
     * context and then call this function.
     */
    void initialize();
    /**
     * Get the label used to differentiate this parameter from others.
     * <p>
     * This function is used internally for things like saving to preferences.
     * While it is suggested that the label is the same label as on the dialog,
     * this is not required.
     * @return The label used for differentiating this parameter from others
     */
    String label();
    /**
     * Add this parameter to the dialog.
     *
     * @param gd The {@link DPDialog} to add to.
     */
    void addToDialog(DPDialog gd);
    /**
     * Read this parameter from the dialog.
     */
    void readFromDialog();
    /**
     * Save the value of this parameter to the preferences.
     * <p>
     * Subclasses usually use a SciJava
     * <a href="https://javadoc.scijava.org/SciJava/org/scijava/prefs/PrefService.html">PrefService</a>
     * to implement this function.  The function gets called by the
     * {@link Harvester} automatically, so the user should only ever call it if
     * it is a subclass of {@link HoldingParameter}.  Also, the string should
     * probably be extended so that two parameters don't have the same string.
     *
     * @param c The class to associate with when saving.  This is always the
     *          class for the command that is being run.
     * @param name The name used to identify this parameter.
     */
    void saveToPrefs(Class<?> c, String name);
    /**
     * Read the value of this parameter from the preferences.
     * <p>
     * Subclasses usually use a SciJava
     * <a href="https://javadoc.scijava.org/SciJava/org/scijava/prefs/PrefService.html">PrefService</a>
     * to implement this function.  This function is the direct analogue of
     * {@link saveToPrefs saveToPrefs}, and after this function is called,
     * {@link getValue} should have the same value as it did before the last
     * call to {@link saveToPrefs saveToPrefs}.  The parameters must be
     * identical between calls to this function and
     * {@link saveToPrefs saveToPrefs} for it to work correctly.
     *
     * @param c The class to associate with when saving.  This is always the
     *          class for the command that is being run.
     * @param name The name used to identify this parameter.
     */
    void readFromPrefs(Class<?> c, String name);
    /** Checks if this parameter should be put on the dialog.
     * <p>
     * If the parameter is visible, it will be put on the dialog, and if it is
     * not visible, it will not be put on the dialog.
     * <p>
     * Note: This function should only be called by the {@link Harvester}.
     *
     * @return Whether or not this parameter should be put on the dialog.
     */
    boolean visible();
    /** Checks if the parameter's visibility changed.
     * <p>
     * If the parameter is currently visible and shouldn't be, or vice versa,
     * the Harvester needs to know, and uses this function to determine it.
     * <p>
     * Note: This function should only be called by the {@link Harvester}.
     *
     * @return If the visibility of this parameter has changed.
     */
    boolean visibilityChanged();
    /** Sets the new visibility.
     * <p>
     * This function should not change the result of {@link visible}
     * <em>yet</em>.  This just marks that the visibility needs to change at
     * some point soon.  The {@link Harvester}, when it sees that
     * {@link visibilityChanged} is true, will then refresh all parameter's
     * visibility.
     *
     * @param value The new visibility to eventually change to
     */
    void setNewVisibility(boolean value);
    /** Makes {@link visible} return the value last set by
     * {@link setNewVisibility}.
     * <p>
     * This function should only be called by the {@link Harvester}.
     */
    void refreshVisibility();
    /** Checks if the parameter has changed enough that the dialog must be
     * recreated.
     * <p>
     * Reconstruction can most of the time be handled by the visibility
     * functions, but if your parameter needs to stay visible while still being
     * recreated, use this.
     * <p>
     * Note: This function should only be called bye the {@link Harvester}.
     *
     * @return If the parameter needs to be reconstructed.
     */
    boolean reconstructionNeeded();
    /**
     * The width that this parameter needs on the dialog, if needed.
     * <p>
     * Normally, this can be detected automatically, but if this parameter needs
     * any extra space that the dialog cannot detect, use this function.  If you
     * don't need it, return 0.
     *
     * @return The width that this parameter needs on the dialog
     */
    int width();
    /** An error in this parameter.
     * <p>
     * If, for some reason, this parameter is invalid, use this function to say
     * why.  The dialog will be disabled while any parameters have an error.  If
     * there is no error, return <code>null</code>.
     *
     * @return A string representing the error, or <code>null</code> if there is
     *         no error.
     */
    String getError();
    /** A warning in this parameter.
     * <p>
     * This function should be used to return a warning to the user about this
     * parameter.  It is similar to {@link getError}, except that if there is a
     * warning, it will not disable the dialog.  If there is no warning, return
     * <code>null</code>.
     *
     * @return A string representing the warning, or <code>null</code> if there
     *         is no error.
     */
    String getWarning();
    /** Check if something went wrong during initialization.
     * <p>
     * This function should return true if something went wrong during
     * initialization.  The {@link DynamicPreprocessor} will abort the command
     * if this function returns <code>true</code>, using the string returned by
     * {@link getError} as the error message.
     *
     * @return Whether or not an error happened during initialization.
     */
    boolean invalid();
    /** Get the value stored in this parameter.
     * <p>
     * This function should return the <em>current</em> value that is in this
     * parameter.  It is important that it should always be current so that its
     * validity can be checked before the dialog closes.  Once the dialog has
     * closed, the command should use this function to get the values it
     * requires.
     *
     * @return The value stored in this parameter.
     */
    T getValue();
    /** Set the Harvester that this parameter is in.
     * <p>
     * Some parameters may need a reference to the {@link Harvester} that they
     * are in.  This function gets called on all parameters before the dialog is created.
     *
     * @param h The {@link Harvester} that this parameter is inside.
     */
    void setHarvester(Harvester h);

    static String displayLabel(String label)
    {
        return label.replace("_", " ");
    }
}
