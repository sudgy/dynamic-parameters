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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.scijava.Context;

/**
 * HoldingParameter is a dynamic parameter that contains other parameters.
 * <p>
 * It should be used whenever several parameters can be thought of as one
 * group of parameters that you should be able to manipulate as one unit.
 * It deals with adding and reading from the dialog and the preferences.
 * <p>
 * To use it, extend from it and then always use {@link addParameter} to
 * add parameters.  You may not add parameters in the constructor, and you
 * should instead add them in {@link initialize}.  If you want to see an example
 * of using this class, see the source for {@link PluginParameter}.
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
     * This calls addToDialog to every parameter in this one, as long as it is
     * {@link visible visible()}.
     */
    @Override public void addToDialog(DPDialog dialog)
    {
        for (DParameter<?> param : M_params) {
            if (param.visible()) {
                param.addToDialog(dialog);
            }
        }
    }
    /**
     * Read this parameter from the dialog.
     * <p>
     * This calls readFromDialog to every parameter in this one, as long as it is
     * {@link visible visible()}.
     */
    @Override public void readFromDialog()
    {
        for (DParameter<?> param : M_params) {
            if (param.visible()) {
                param.readFromDialog();
            }
        }
    }
    /**
     * Save the values of this parameter to the preferences.
     * <p>
     * This calls saveToPrefs to every parameter in this one.
     */
    @Override public void saveToPrefs(Class<?> c, String name)
    {
        for (DParameter<?> param : M_params) {
            param.saveToPrefs(c, name + "." + param.label());
        }
    }
    /**
     * Read the values of this parameter from the preferences.
     * <p>
     * This calls readFromPrefs to every parameter in this one.
     */
    @Override public void readFromPrefs(Class<?> c, String name)
    {
        for (DParameter<?> param : M_params) {
            param.readFromPrefs(c, name + "." + param.label());
        }
    }
    /**
     * {@inheritDoc}
     * <p>
     * This returns <code>true</code> if any of the contained parameter's
     * visibility has changed.
     */
    @Override public boolean visibilityChanged()
    {
        for (DParameter<?> param : M_params) {
            if (param.visibilityChanged()) return true;
        }
        return super.visibilityChanged();
    }
    /** {@inheritDoc}
     * <p>
     * This calls refreshVisibility on all of the contained parameters.
     */
    @Override public void refreshVisibility()
    {
        for (DParameter<?> param : M_params) {
            param.refreshVisibility();
        }
        super.refreshVisibility();
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
    @Override public String getError()
    {
        String result = super.getError();
        if (result == null) {
            for (DParameter<?> param : M_params) {
                if (param.visible()) {
                    result = param.getError();
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
    @Override public String getWarning()
    {
        String result = super.getWarning();
        if (result == null) {
            for (DParameter<?> param : M_params) {
                if (param.visible()) {
                    result = param.getWarning();
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
    @Override public void setHarvester(Harvester h)
    {
        for (DParameter<?> param : M_params) {
            param.setHarvester(h);
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
    protected <T extends DParameter<?>> T addParameter(Class<T> cls, Object... args)
    {
        Class<?>[] argsC = new Class<?>[args.length];
        if (args.length != 0) {
            for (int i = 0; i < args.length; ++i) {
                argsC[i] = args[i].getClass();
            }
        }
        try {
            T result;
            Constructor<T> constr = cls.getConstructor(argsC);
            constr.setAccessible(true);
            result = constr.newInstance(args);
            addPremadeParameter(result);
            return result;
        }
        catch (NoSuchMethodException e) {throw new RuntimeException(e);}
        catch (InstantiationException e) {throw new RuntimeException(e);}
        catch (IllegalAccessException e) {throw new RuntimeException(e);}
        catch (InvocationTargetException e) {throw new RuntimeException(e);}
    }
    /** Add a pre-made parameter to this parameter.
     * <p>
     * If you are unable to use {@link addParameter} for any reason, use this
     * function to add the parameter and still do all of the initialization that
     * addParameter does.  You may not pass in a parameter that already has a
     * context.
     *
     * @param param The parameter to add.
     */
    protected void addPremadeParameter(DParameter param)
    {
        Context context = getContext();
        if (context != null) {
            param.setContext(context);
            param.initialize();
        }
        M_params.add(param);
    }
    /** Remove a parameter by value.
     *
     * @param param The parameter to remove.
     * @return <code>true</code> if the parameter was successfully removed.
     */
    protected boolean removeParameter(DParameter<?> param)
        {return M_params.remove(param);}
    /** Remove a parameter by index.
     *
     * @param index The index of the parameter to remove.
     * @return The parameter that was removed.
     */
    protected DParameter<?> removeParameter(int index)
        {return M_params.remove(index);}
    /** Remove all parameters. */
    protected void clearParameters()
        {M_params.clear();}
    /** Get all of the plugins that are in this plugin.
     *
     * @return An Iterable that iterates through all of the plugins.
     */
    protected Iterable<DParameter<?>> getAllParams()
    {
        return M_params;
    }
    private ArrayList<DParameter<?>> M_params = new ArrayList<DParameter<?>>();
}
