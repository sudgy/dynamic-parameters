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

import org.scijava.Context;
import org.scijava.NullContextException;
import org.scijava.plugin.AbstractRichPlugin;
import org.scijava.plugin.Plugin;
import org.scijava.prefs.PrefService;

/**
 * AbstractDParameter is a {@link DParameter} that implements (almost) all of
 * the functions to sensible defaults.
 * <p>
 * It is suggested to extend this class rather than implement the
 * {@link DParameter} <code>interface</code>, for two main reasons.  First, this
 * class extends
 * <a href="https://javadoc.scijava.org/SciJava/org/scijava/plugin/AbstractRichPlugin.html">AbstractRichPlugin</a>,
 * so all of the benefits that you get from it you get from this class.  Second,
 * this class implements most of the functions from {@link DParameter} with
 * sensible defaults, leaving only the <code>add</code> and <code>read</code>
 * functions and {@link getValue} to implement.  Of course, if you want, you
 * can override any of these defaults.
 */
public abstract class AbstractDParameter<T> extends AbstractRichPlugin implements DParameter<T> {
    /** Constructs a AbstractDParameter with a given label.
     * <p>
     * This label is the ones returned by {@link label}, not necessarilly the
     * one displayed on screen (even though this is suggested).
     * <p>
     * Note that this is the only constructor, so every AbstractDParameter must
     * have a label.
     *
     * @param label The label to be returned by {@link label}.
     */
    public AbstractDParameter(String label)
    {
        M_label = label;
    }
    /** {@inheritDoc}
     * <p>
     * This function defaults to doing nothing.
     */
    @Override public void initialize() {}
    /** {@inheritDoc}
     * <p>
     * This function defaults to returning what was passed into the constructor.
     */
    @Override public String label() {return M_label;}
    /** {@inheritDoc} */
    @Override public boolean visible() {return M_visible;}
    /** {@inheritDoc} */
    @Override public boolean visibilityChanged() {return M_visible != M_newVisible;}
    /** {@inheritDoc} */
    @Override public void setNewVisibility(boolean value) {M_newVisible = value;}
    /** {@inheritDoc} */
    @Override public void refreshVisibility() {M_visible = M_newVisible;}
    /** {@inheritDoc}
     * <p>
     * This function defaults to returning <code>false</code>.
     */
    @Override public boolean reconstructionNeeded() {return false;}
    /** {@inheritDoc}
     * <p>
     * This function defaults to returning zero.
     */
    @Override public int width() {return 0;}
    /** {@inheritDoc}
     * <p>
     * This function defaults to returning the string passed into the last call
     * to {@link setError}.
     */
    @Override public String getError() {return M_error;}
    /** {@inheritDoc}
     * <p>
     * This function defaults to returning the string passed into the last call
     * to {@link setWarning}.
     */
    @Override public String getWarning() {return M_warning;}
    /** {@inheritDoc}
     * <p>
     * This function defaults to returning false.
     */
    @Override public boolean invalid() {return false;}
    /** {@inheritDoc}
     * <p>
     * This function defaults to setting <code>M_harvester</code> to
     * <code>h</code>.
     */
    @Override public void setHarvester(Harvester h) {M_harvester = h;}

    /** Sets the error.
     * <p>
     * The string passed into this function will be used as the error for
     * {@link getError}.
     *
     * @param error The error string
     */
    protected final void setError(String error) {M_error = error;}
    /** Sets the warning.
     * <p>
     * The string passed into this function will be used as the warning for
     * {@link getWarning}.
     *
     * @param warning The warning string
     */
    protected final void setWarning(String warning) {M_warning = warning;}
    /** Gets the <a href="https://javadoc.scijava.org/SciJava/org/scijava/prefs/PrefService.html">PrefService</a>
     * associated with the context.
     * <p>
     * Use it to implement {@link readFromPrefs readFromPrefs}.
     *
     * @return The PrefService associated with the context
     */
    protected final PrefService prefs() {return context().getService(PrefService.class);}
    /** The harvester that this parameter is in.
     * <p>
     * It is set by {@link setHarvester} after initialization, but before the
     * dialog appears.
     */
    protected Harvester M_harvester;

    private String M_label;
    private String M_error;
    private String M_warning;
    private boolean M_visible = true;
    private boolean M_newVisible = true;
}
