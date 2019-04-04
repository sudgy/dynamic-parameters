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

import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;

import ij.gui.GenericDialog;

/**
 * HoldingParameter is a dynamic parameter that contains other parameters.
 * <p>
 * It should be used whenever several parameters can be thought of as one
 * group of parameters that you should be able to manipulate as one unit.
 * It deals with adding and reading from the dialog and the preferences.
 * <p>
 * To use it, extend from it and then always use {@link add_parameter} to
 * add parameters.  You may not add parameters in the constructor, and you
 * should instead add them in {@link initialize}.
 */
public abstract class HoldingParameter<T> extends AbstractDParameter<T> {
    /** Constructs a HoldingParameter with a given label.
     *
     * This is identical to {@link AbstractDParameter}'s constructor.
     *
     * @param label The label to be returned by {@link label label()}
     */
    public HoldingParameter(String label)
    {
        super(label);
    }
    /**
     * Add this parameter to the dialog.
     * <p>
     * This calls add_to_dialog to every parameter in this one, as long as it is
     * {@link visible visible()}.
     */
    @Override public void add_to_dialog(DPDialog dialog)
    {
        for (DParameter<?> param : M_params) {
            if (param.visible()) {
                param.add_to_dialog(dialog);
            }
        }
    }
    /**
     * Read this parameter from the dialog.
     * <p>
     * This calls read_from_dialog to every parameter in this one, as long as it is
     * {@link visible visible()}.
     */
    @Override public void read_from_dialog()
    {
        for (DParameter<?> param : M_params) {
            if (param.visible()) {
                param.read_from_dialog();
            }
        }
    }
    /**
     * Save the values of this parameter to the preferences.
     * <p>
     * This calls save_to_prefs to every parameter in this one.
     */
    @Override public void save_to_prefs(Class<?> c, String name)
    {
        for (DParameter<?> param : M_params) {
            param.save_to_prefs(c, name + "." + param.label());
        }
    }
    /**
     * Read the values of this parameter from the preferences.
     * <p>
     * This calls read_from_prefs to every parameter in this one.
     */
    @Override public void read_from_prefs(Class<?> c, String name)
    {
        for (DParameter<?> param : M_params) {
            param.read_from_prefs(c, name + "." + param.label());
        }
    }
    /**
     * {@inheritDoc}
     * <p>
     * This returns <code>true</code> if any of the contained parameter's
     * visibility has changed.
     */
    @Override public boolean visibility_changed()
    {
        for (DParameter<?> param : M_params) {
            if (param.visibility_changed()) return true;
        }
        return super.visibility_changed();
    }
    /** {@inheritDoc}
     * <p>
     * This calls refresh_visibility on all of the contained parameters.
     */
    @Override public void refresh_visibility()
    {
        for (DParameter<?> param : M_params) {
            param.refresh_visibility();
        }
        super.refresh_visibility();
    }
    /** The width that this parameter needs on the dialog, if needed.
     * <p>
     * This returns the maximum width needed by all of the contained parameters.
     */
    @Override public int width()
    {
        int result = 0;
        for (DParameter<?> param : M_params) {
            result = Math.max(result, param.width());
        }
        return result;
    }
    /** An error in any of the parameters.
     * <p>
     * If any of the contained parameters have an error and are visibible, this
     * will return one of those.
     */
    @Override public String get_error()
    {
        String result = super.get_error();
        if (result == null) {
            for (DParameter<?> param : M_params) {
                if (param.visible()) {
                    result = param.get_error();
                    if (result != null) break;
                }
            }
        }
        return result;
    }
    /** A warning in any of the parameters.
     * <p>
     * If any of the contained parameters have a warning and are visibible, this
     * will return one of those.
     */
    @Override public String get_warning()
    {
        String result = super.get_warning();
        if (result == null) {
            for (DParameter<?> param : M_params) {
                if (param.visible()) {
                    result = param.get_warning();
                    if (result != null) break;
                }
            }
        }
        return result;
    }
    /** Check if something went wrong during initialization.
     * <p>
     * This returns true if any contained parameter is invalid.
     */
    @Override public boolean invalid()
    {
        for (DParameter<?> param : M_params) {
            if (param.invalid()) return true;
        }
        return false;
    }
    /** Set the Harvester for all contained parameters. */
    @Override public void set_harvester(Harvester h)
    {
        for (DParameter<?> param : M_params) {
            param.set_harvester(h);
        }
    }

    /** Add a parameter to this parameter.
     * <p>
     * There are many things that need to happen to a new parameter, so this
     * function is required to create any parameters that need to be visible.
     * This function will set the context and initialize the new parameter.
     * If any kind of error happens in initialization (wrong arguments, illegal
     * access, etc.) the exception is converted to a {@link RuntimeException}
     * and rethrown.
     *
     *
     * @param <T> The type of the new parameter to create.  It can be determined
     *            through the <code>cls</code> parameter.
     * @param cls The class of the new parameter to create.
     * @param args The arguments to the class constructor.  If creating a
     *             non-static inner class, the first argument must be an
     *             instance of the outer class.
     * @return The new parameter.
     */
    protected final <T extends DParameter<?>> T add_parameter(Class<T> cls, Object... args)
    {
        Class<?>[] args_c = new Class<?>[args.length];
        if (args.length != 0) {
            for (int i = 0; i < args.length; ++i) {
                args_c[i] = args[i].getClass();
            }
        }
        try {
            T result;
            if (args.length == 0) result = cls.getDeclaredConstructor().newInstance();
            else result = cls.getConstructor(args_c).newInstance(args);
            result.setContext(context());
            result.initialize();
            M_params.add(result);
            return result;
        }
        catch (NoSuchMethodException e) {throw new RuntimeException(e);}
        catch (InstantiationException e) {throw new RuntimeException(e);}
        catch (IllegalAccessException e) {throw new RuntimeException(e);}
        catch (InvocationTargetException e) {throw new RuntimeException(e);}
    }
    /** Remove a parameter by value.
     *
     * @param param The parameter to remove.
     * @return <code>true</code> if the parameter was successfully removed.
     */
    protected final boolean remove_parameter(DParameter<?> param)
        {return M_params.remove(param);}
    /** Remove a parameter by index.
     *
     * @param index The index of the parameter to remove.
     * @return The parameter that was removed.
     */
    protected final DParameter<?> remove_parameter(int index)
        {return M_params.remove(index);}
    /** Remove all parameters. */
    protected final void clear_parameters()
        {M_params.clear();}
    private ArrayList<DParameter<?>> M_params = new ArrayList<DParameter<?>>();
}
